/**
 * For copyright information see the LICENSE document.
 */

package realityshard.container;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import realityshard.container.gameapp.GameAppManager;
import realityshard.container.gameapp.GameAppContext;
import realityshard.container.gameapp.GameAppFactory;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import realityshard.container.network.GameAppContextKey;
import realityshard.container.util.Handle;
import realityshard.container.util.HandleRegistry;


/**
 * Container facade is what the host application works with.
 * Creates all game apps and manages them.
 * Also manages the network.
 *
 * @author _rusty
 */
public final class ContainerFacade implements GameAppManager
{

    private static final class GameAppInfo
    {
        public GameAppFactory Factory;
        public MetaGameAppContext MetaContext;
        public NioEventLoopGroup Boss;
        public NioEventLoopGroup Worker;
        public NioServerSocketChannel NetworkChannel;
    }

    
    private final static Logger LOGGER = LoggerFactory.getLogger(ContainerFacade.class);

    private final Map<String, GameAppInfo> gameApps = new ConcurrentHashMap<>();
    private final HandleRegistry<GameAppContext> gameAppHandleRegistry = new HandleRegistry<>();

    private InetAddress localAddress = null;

    /**
     * Constructor.
     * 
     * @param       factories               The factories for each kind of game app. 
     */
    public ContainerFacade(List<GameAppFactory> factories) throws Exception
    {
        for (GameAppFactory factory : factories)
        {
            gameApps.put(factory.getName(), produceInfoFromFactory(factory));
        }

        // now start up all start-up apps
        startUpApps();
        
        // try to get the local ip
        // TODO: use a better method ;)
        Enumeration<NetworkInterface> netIfaces = NetworkInterface.getNetworkInterfaces();
        while (netIfaces.hasMoreElements()) 
        {
            NetworkInterface cur = netIfaces.nextElement();
            if (!cur.isUp() || cur.isLoopback() || cur.isVirtual()) { continue; }
            
            Enumeration<InetAddress> addr = cur.getInetAddresses();
            while (addr.hasMoreElements())
            {
                InetAddress curAddr = addr.nextElement();
                if (curAddr instanceof Inet4Address)
                {
                    localAddress = curAddr;
                    return;
                }
            }
        }
    }

    
    /**
     * Check if we can create a game app with a certain name
     * 
     * @param       name
     * @return      True if we got a factory for it, false otherwise
     */
    @Override
    public boolean canCreateGameApp(String name) 
    {
        return gameApps.containsKey(name);
    }
    
    
    /**
     * Create a game app by name.
     * 
     * @param       name
     * @param       parent
     * @param       additionalParams
     * @return      The game app or null, if creation failed.
     */
    @Override
    public Handle<GameAppContext> createGameApp(String name, Handle<GameAppContext> parent, Map<String, String> additionalParams)
    {
        return internalCreateGameApp(name, parent, additionalParams);
    }
    
    
    /**
     * Try get a game app handle by its unique identifier.
     * 
     * @param       gameAppUid
     * @return      The global handle of the game app, or null.
     */
    @Override
    public Handle<GameAppContext> tryGetGameApp(UUID gameAppUid)
    {
        return gameAppHandleRegistry.getHandle(gameAppUid);
    }

    
    /**
     * Shutdown a specific game app.
     * 
     * @param       that                    Game app
     */
    @Override
    public void removeGameApp(Handle<GameAppContext> that) 
    {
        GameAppInfo gameAppInfo = gameApps.get(that.get().getName());
        
        if (gameAppInfo == null) { LOGGER.error("Game app doesnt exist! [name {} ]", that.get().getName()); return; }
        
        gameAppInfo.MetaContext.shutdown(that.get());
    }   
    
    
    /**
     * Getter.
     * 
     * @param       that
     * @return      The local address of that game app.
     */
    @Override
    public InetSocketAddress localAddressFor(Handle<GameAppContext> that) 
    {
        GameAppInfo gameAppInfo = gameApps.get(that.get().getName());
        
        if (gameAppInfo == null) { LOGGER.error("Game app doesnt exist! [name {} ]", that.get().getName()); return null; }
        
        int port = gameAppInfo.NetworkChannel.localAddress().getPort();
        
        // always return the address of this server...
        // the parameter is important when we do remoting
        if (localAddress == null) 
        { 
            return new InetSocketAddress(Inet4Address.getLoopbackAddress(), port);
        }
        
        return new InetSocketAddress(localAddress, port);
    }
    

    /**
     * Load all apps that have the "start-up" marker
     */
    private void startUpApps()
    {
        for (GameAppInfo gameAppInfo : gameApps.values())
        {
            if (!gameAppInfo.Factory.isStartup()) { continue; }

            internalCreateGameApp(gameAppInfo.Factory.getName(), null, new HashMap<String, String>());
        }
    }


    /**
     * Shutdown this container.
     */
    public void shutdown()
    {
        for (Map.Entry<String, GameAppInfo> entry : gameApps.entrySet()) 
        {
            GameAppInfo gameAppInfo = entry.getValue();
            
            gameAppInfo.MetaContext.shutdown();
            gameAppInfo.NetworkChannel.close().syncUninterruptibly();
        }
    }
    
    
    /**
     * Init our factories and load the info about them into our map.
     */
    private GameAppInfo produceInfoFromFactory(GameAppFactory factory) throws Exception
    {
        GameAppInfo result = new GameAppInfo();
        result.Factory = factory;
        result.MetaContext = new MetaGameAppContext(factory.getName(), this);
        
        // register the metacontext with its own aggregator
        result.MetaContext.getEventAggregator().register(result.MetaContext);
        
        result.Boss = new NioEventLoopGroup();
        result.Worker = new NioEventLoopGroup();
        
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(result.Boss, result.Worker)
                 .channel(NioServerSocketChannel.class)
                 .childAttr(GameAppContextKey.KEY, result.MetaContext)
                 .childAttr(GameAppContextKey.IS_SET, false)
                 .option(ChannelOption.SO_BACKLOG, 1000);
        
        result.NetworkChannel = (NioServerSocketChannel) factory.getServerChannel(bootstrap);
        
        return result;
    }
    
    
    /**
     * Create a new game app, using the factory.
     */
    private Handle<GameAppContext> internalCreateGameApp(String name, Handle<GameAppContext> parent, Map<String, String> additionalParams)
    {
        GameAppInfo gameAppInfo = gameApps.get(name);
        
        if (gameAppInfo == null) { LOGGER.error("Game app doesnt exist! [name {} ]", name); return null; }
        
        // create the context for the app
        GameAppContext context = new GameAppContext.Default(name, this, parent);
        
        // register it
        Handle<GameAppContext> contextHandle = gameAppHandleRegistry.register(context);
        
        // create the app
        if (!gameAppInfo.Factory.initGameApp(contextHandle, parent, additionalParams))
        {
            LOGGER.error("Failed to create game app! [name {} ]", gameAppInfo.Factory.getName());
            contextHandle.invalidate();
            return null;
        }

        // dont forget to add it to the metacontext
        gameAppInfo.MetaContext.addContext(context);

        return contextHandle;
    }
}
