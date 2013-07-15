/**
 * For copyright information see the LICENSE document.
 */

package com.realityshard.container;

import com.realityshard.shardlet.environment.GameAppManager;
import com.realityshard.container.gameapp.DefaultContext;
import com.realityshard.container.gameapp.GameAppContext;
import com.realityshard.shardlet.environment.Environment;
import com.realityshard.network.ApplicationLayer;
import com.realityshard.network.NetworkLayer;
import com.realityshard.network.NetworkSession;
import com.realityshard.shardlet.GlobalExecutor;
import com.realityshard.shardlet.RemoteShardletContext;
import com.realityshard.shardlet.ShardletContext;
import com.realityshard.shardlet.environment.GameAppFactory;
import com.realityshard.shardlet.environment.ProtocolFactory;
import com.realityshard.shardlet.utils.GenericTriggerableAction;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
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
public final class ContainerFacade
    implements ApplicationLayer, GameAppManager
{
    
    private final static Logger LOGGER = LoggerFactory.getLogger(ContainerFacade.class);
    
    private final NetworkLayer network;
    private final ScheduledExecutorService executor;
    private final Map<NetworkSession, GameSession> sessions;
    
    private final Map<String, ProtocolChain> protocols;
    private final Map<String, GameAppFactory> gameApps;
    
    private final Environment env;
    private final DefaultContext defaultContext;
    
    
    /**
     * Constructor.
     * This is executed by all constructors.
     * 
     * @param       network                 The network manager of this application
     * @param       env                     The environment for this container, meaning
     *                                      all game apps and protocols as defined by
     *                                      the <code>Environment</code> interface.
     */
    public ContainerFacade(NetworkLayer network, Environment env)
    {
        // the network manager will handle the networking stuff,
        // like sending and recieving data.
        this.network = network;
        this.network.setApplicationLayer(this);
        
        // the executor is responsible for multithreading of this server
        // every internal event listener below will automatically be running parallel
        // depending on the executors decision, because when an event is triggered,
        // the event-aggregator will direct the event-handler invocations to the executor
        this.executor = GlobalExecutor.get();
        
        this.sessions = new ConcurrentHashMap<>();
        this.env = env;
        
        protocols = new ConcurrentHashMap<>();
        gameApps = new ConcurrentHashMap<>();
        
        loadGameApps(env);
        loadProtocols(env);
        
        defaultContext = new DefaultContext();
        
        // now start up all start-up apps
        startUpApps();
    }
    
    
    /**
     * Just the implementation of the interface.
     * This will be called by the network manager!
     * 
     * @param       netSession 
     * @param       rawData
     */
    @Override
    public void handlePacket(NetworkSession netSession, ByteBuffer rawData)
    {
        // we could do anything we want with this packet here,
        // but we don't
        
        // basically, what we will be doing though
        // is create an action, attach a session to it (if we find the session, that is)
        // and then delegate it to the ContextManager
        GameSession session = sessions.get(netSession);
        
        if (session == null)
        {
            LOGGER.error("Got a message from an unkown client! (Its network-session is not registered with the Container)");
            return;
        }

        GenericTriggerableAction action = new GenericTriggerableAction();
        action.init(session);
        action.setBuffer(rawData);

        // delegate it!
        session.receive(action);
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
    public void newClient(NetworkSession netSession, String protocolName, String IP, int port) 
    {
        // add the client here,
        sessions.put(netSession, new GameSession(
                netSession, 
                defaultContext, 
                IP, 
                port, 
                protocolName,
                protocols.get(protocolName)));
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
    public GameAppContext createGameApp(String name, RemoteShardletContext parent, Map<String, String> additionalParams) 
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
    public void unload(ShardletContext thatGameApp)
    {
        // first of all, invalidate the game-apps sessions so we dont get spammed
        // by exceptions later on.
        for (GameSession gameSession : sessions.values()) 
        {
            if (gameSession.getShardletContext() == thatGameApp)
            {
                gameSession.invalidate();
            }
        }
        
        // now remove the game app
        defaultContext.removeGameApp(thatGameApp);
    }
    

    /**
     * Called by the network manager when a client disconnects
     * 
     * @param       netSession 
     */
    @Override
    public void lostClient(NetworkSession netSession) 
    {
        // temporarily get the session
        GameSession session = sessions.get(netSession);
        
        if (session == null)
        {
            LOGGER.error("An unkown client disconnected! (Its network-session is not registered with the Container)");
            return;
        }

        // only to remove it afterwards
        sessions.remove(netSession);

        // and finally unregister that session with its context
        ((GameAppContext) session.getShardletContext()).handleLostClient(session);
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
            // register it with the network layer
            try 
            {
                network.addNetworkListener(prot.getName(), prot.getPort());
            } 
            catch (IOException ex) 
            {
                LOGGER.error("Could not register a protocol with the network layer.", ex);
                continue;
            }

            // finally register it with this container
            protocols.put(prot.getName(), new ProtocolChain(prot.getInFilters(), prot.getOutfilters()));
        }
    }
    
    
    /**
     * Shutdown this shardlet container. 
     * (Close all Sessions and trigger a ContainerShutdownEvent in each GAContext)
     */
    public void shutdown()
    {
        for (GameSession gameSession : sessions.values()) 
        {
            // we already have code handling this for us...
            gameSession.invalidate();
        }
        
        defaultContext.shutdown();
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
}
