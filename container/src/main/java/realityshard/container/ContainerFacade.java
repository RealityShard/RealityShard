/**
 * For copyright information see the LICENSE document.
 */

package realityshard.container;

import realityshard.network.NetEventHandlers;
import realityshard.network.NetworkSession;
import realityshard.network.NetworkLayer;
import realityshard.shardlet.gameapp.GameAppManager;
import realityshard.shardlet.gameapp.GameAppContext;
import realityshard.shardlet.environment.Environment;
import realityshard.shardlet.RemoteShardletContext;
import realityshard.shardlet.ShardletContext;
import realityshard.shardlet.gameapp.GameAppFactory;
import realityshard.shardlet.environment.ProtocolFactory;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This class is used as the general Interface for working with the container.
 * Use this within your server-application to gain Shardlet-functionality.
 * 
 * Internal usage:
 * For all internal objects, this is a NetworkAdapter
 * 
 * External usage:
 * For the network manager object, this is the ApplicationLayer
 * 
 * @author _rusty
 */
public final class ContainerFacade implements
        GameAppManager,
        NetEventHandlers.NewClient
{
    
    private final static Logger LOGGER = LoggerFactory.getLogger(ContainerFacade.class);
    
    private final NetworkLayer network;
    
    private final Map<String, ProtocolChain> protocols = new ConcurrentHashMap<>();
    private final Map<String, GameAppFactory> gameApps = new ConcurrentHashMap<>();
   
    private final DefaultContext defaultContext = new DefaultContext();;
    
    
    /**
     * Constructor.
     * This is executed by all constructors.
     * 
     * @param       network                 The network manager of this application
     * @param       env                     The environment for this container, meaning
     *                                      all game apps and protocols as defined by
     *                                      the <code>Environment</code> interface.
     */
    public ContainerFacade(NetworkLayer network, List<GameAppFactory> apps)
    {
        // the network manager will handle the networking stuff,
        // like sending and recieving data.
        this.network = network;
        this.network.registerOnNewClient(this);
        
        loadProtocols(env);
        loadGameApps(env);
        
        // now start up all start-up apps
        startUpApps();
    }
    
    
    /**
     * Called by the network manager when a client connects
     * 
     * @param       netSession 
     * @param       protocolName
     * @param       IP
     * @param       port
     */
    @Override
    public void onNewClient(NetworkSession netSession, String protocolName, String IP, int port) 
    {
        // create the game session associated with that network client
        GameSession session = new GameSession(
                netSession, 
                defaultContext, 
                IP, 
                port, 
                protocolName,
                protocols.get(protocolName));
        
        // register for the net client events
        netSession.registerOnNewData(session);
        netSession.registerOnLostClient(session);
    }
    
    
    /**
     * Implementation of the GameAppManager interface.
     * 
     * @param name
     * @param parent
     * @param additionalParams 
     * @return 
     */
    @Override
    public GameAppContext createGameApp(String name, ShardletContext.Remote parent, Map<String, String> additionalParams) 
    {
        GameAppFactory factory = gameApps.get(name);
        
        // failcheck
        if (factory == null) { LOGGER.error("Tried to create a nonexisting game app! [name {} ]", name); return null; }
        
        // if we got that factory, then we just need to create the appropriate game app:
        GameAppContext newApp = (GameAppContext) factory.produceGameApp(this, parent, additionalParams);
        
        // failcheck
        if (newApp == null) { LOGGER.error("Failed to create game app! [name {} ]", name); return null; }
        
        // dont forget to tell the default context that we have a new app
        defaultContext.addContext(newApp);
        
        return newApp;
    }
    
    
    /**
     * Called by a game app to unload/close it.
     * 
     * @param       thatGameApp             The game app that will be removed.
     */
    @Override
    public void notifyUnload(ShardletContext thatGameApp)
    {       
        // now remove the game app
        defaultContext.removeGameApp(thatGameApp);
    }
    
    
    /**
     * Add all game apps defined by the environment.
     */
    private void loadGameApps(Environment env)
    {
        for (GameAppFactory factory : env.getGameAppFactories()) 
        {
            gameApps.put(factory.getName(), factory);
        }
    }
    
    
    /**
     * Loads all protocols defined by the environment.
     */
    private void loadProtocols(Environment env)
    {
        for (ProtocolFactory prot : env.getProtocolFactories()) 
        {

            network.addNetworkListener(prot.getName(), prot.getPort());
            

            // finally register it with this container
            protocols.put(prot.getName(), new ProtocolChain(prot.getInFilters(), prot.getOutfilters()));
        }
    }
    
    
    /**
     * Load all apps that have the "start-up" marker
     */
    private void startUpApps()
    {
        for (GameAppFactory factory : gameApps.values()) 
        {
            if (!factory.isStartup()) { continue; }
            
            // create the app
            GameAppContext newApp = (GameAppContext) factory.produceGameApp(this, null, new HashMap<String, String>());
            
            // failcheck
            if (newApp == null) { LOGGER.error("Failed to auto-create game app! [name {} ]", factory.getName()); return; }

            // dont forget to tell the default context that we have a new app
            defaultContext.addContext(newApp);
        }
    }
    
    
    /**
     * Shutdown this shardlet container. 
     * (Trigger a ContainerShutdownEvent in each GAContext)
     */
    public void shutdown()
    {
        defaultContext.shutdown();
    }
}
