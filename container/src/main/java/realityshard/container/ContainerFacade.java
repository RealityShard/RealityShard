/**
 * For copyright information see the LICENSE document.
 */

package realityshard.container;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.nio.NioEventLoopGroup;
import realityshard.container.gameapp.MetaGameAppContext;
import java.net.SocketAddress;
import realityshard.container.gameapp.GameAppManager;
import realityshard.container.gameapp.GameAppContext;
import realityshard.container.gameapp.GameAppFactory;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import realityshard.container.network.GameAppContextKey;


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
        public ChannelFuture NetworkChannel;
    }

    
    private final static Logger LOGGER = LoggerFactory.getLogger(ContainerFacade.class);

    private final Map<String, GameAppInfo> gameApps = new ConcurrentHashMap<>();


    /**
     * Constructor.
     * 
     * @param       factories               The factories for each kind of game app. 
     */
    public ContainerFacade(List<GameAppFactory> factories)
    {
        for (GameAppFactory factory : factories)
        {
            gameApps.put(factory.getName(), produceInfoFromFactory(factory));
        }

        // now start up all start-up apps
        startUpApps();
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
    public GameAppContext.Remote createGameApp(String name, GameAppContext.Remote parent, Map<String, String> additionalParams)
    {
        return internalCreateGameApp(name, parent, additionalParams);
    }

    
    /**
     * Shutdown a specific game app.
     * 
     * @param       that                    Game app
     */
    @Override
    public void removeGameApp(GameAppContext that) 
    {
        GameAppInfo gameAppInfo = gameApps.get(that.getName());
        
        if (gameAppInfo == null) { LOGGER.error("Game app doesnt exist! [name {} ]", that.getName()); return; }
        
        gameAppInfo.MetaContext.shutdown(that);
    }   
    
    
    /**
     * Getter.
     * 
     * @param       that
     * @return      The local address of that game app.
     */
    @Override
    public SocketAddress localAddressFor(GameAppContext that) 
    {
        GameAppInfo gameAppInfo = gameApps.get(that.getName());
        
        if (gameAppInfo == null) { LOGGER.error("Game app doesnt exist! [name {} ]", that.getName()); return null; }
        
        return gameAppInfo.NetworkChannel.channel().localAddress();
    }
    

    /**
     * Load all apps that have the "start-up" marker
     */
    private void startUpApps()
    {
        for (GameAppInfo gameAppInfo : gameApps.values())
        {
            if (!gameAppInfo.Factory.isStartup()) { continue; }

            internalCreateGameApp(gameAppInfo.Factory.getName(), gameAppInfo.MetaContext, new HashMap<String, String>());
        }
    }


    /**
     * Shutdown this container.
     */
    public void shutdown()
    {
        for (Map.Entry<String, GameAppInfo> entry : gameApps.entrySet()) 
        {
            String string = entry.getKey();
            GameAppInfo gameAppInfo = entry.getValue();
            
            gameAppInfo.MetaContext.shutdown();
            
            //TODO: shutdown network
        }
    }
    
    
    /**
     * Init our factories and load the info about them into our map.
     */
    private GameAppInfo produceInfoFromFactory(GameAppFactory factory)
    {
        GameAppInfo result = new GameAppInfo();
        result.Factory = factory;
        result.MetaContext = new MetaGameAppContext(factory.getName(), this);
        
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(new NioEventLoopGroup())
                 .attr(GameAppContextKey.GAME_APP_CONTEXT_KEY, result.MetaContext);
        
        result.NetworkChannel = factory.getServerChannel(bootstrap);
        
        return result;
    }
    
    
    /**
     * Create a new game app, using the factory.
     */
    private GameAppContext.Remote internalCreateGameApp(String name, GameAppContext.Remote parent, Map<String, String> additionalParams)
    {
        GameAppInfo gameAppInfo = gameApps.get(name);
        
        if (gameAppInfo == null) { LOGGER.error("Game app doesnt exist! [name {} ]", name); return null; }
        
        // create the app
        GameAppContext.Remote newApp = gameAppInfo.Factory.produceGameApp(this, gameAppInfo.MetaContext, additionalParams);

        // failcheck
        if (newApp == null) { LOGGER.error("Failed to create game app! [name {} ]", gameAppInfo.Factory.getName()); return null; }

        // dont forget to add it to the metacontext
        gameAppInfo.MetaContext.addContext(newApp);

        return newApp;
    }
}
