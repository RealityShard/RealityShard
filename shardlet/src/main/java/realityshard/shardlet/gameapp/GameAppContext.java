/**
 * For copyright information see the LICENSE document.
 */

package realityshard.shardlet.gameapp;

import realityshard.shardlet.Shardlet;
import realityshard.shardlet.ClientVerifier;
import realityshard.shardlet.Session;
import realityshard.shardlet.events.GameAppUnloadedEvent;
import realityshard.shardlet.events.NetworkClientDisconnectedEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import realityshard.shardlet.Action;
import realityshard.shardlet.Event;
import realityshard.shardlet.EventAggregator;
import realityshard.shardlet.ShardletContext;


/**
 * Implementation of a ShardletContext.
 * This is a basic implementation of an event-driven shardlet context,
 * that encapsulates a GameApp.
 * 
 * @see GameAppContextFluentBuilder
 * 
 * @author _rusty
 */
public class GameAppContext implements ShardletContext, ShardletContext.Remote
{
    
    private final static Logger LOGGER = LoggerFactory.getLogger(GameAppContext.class);
    
    protected EventAggregator aggregator;
    protected String name = "";
    protected String ip = "";
    protected ShardletContext.Remote parent;
    protected int heartBeatInterval;
    protected Map<String, String> initParams;
    
    protected List<ClientVerifier> normalClientVerifiers;
    protected List<ClientVerifier> persistentClientVerifiers;
    
    private volatile Object attachment;
    
    protected GameAppManager manager;
    protected ClassLoader loader;
    
    protected List<Shardlet> shardlets;
    protected List<Session> sessions;

    
    /**
     * Constructor.
     * 
     * @see GameAppFluentBuilder
     */
    protected GameAppContext()
    {
        normalClientVerifiers = new ArrayList<>();
        persistentClientVerifiers = new ArrayList<>();
        shardlets = new ArrayList<>();
        sessions = new ArrayList<>();
    }
        
    
    /**
     * Decide whether a client is accepted or not.
     * 
     * @param       action                  The first message send by the new client
     * @return  
     */
    public boolean acceptClient(Action action)
    {
        ClientVerifier acceptedVerifier = null;
        
        
        // ask our verifiers...
        // the non persistent first
        for (ClientVerifier shardletActionVerifier : normalClientVerifiers) 
        {
            if (shardletActionVerifier.check(action))
            {
                // a verifier accepted a new client!
                // lets trigger the appropriate event
                trigger(action);
                
                // because the verfier is non persistent, we need to delete it from
                // the list after we left the loop ;D
                acceptedVerifier = shardletActionVerifier;
                
                // log it
                LOGGER.debug("Accepted client");
                
                break;
            }
        }
        
        
        // check if we found one:
        if (acceptedVerifier != null)
        {
            // we need to delete it from the list
            normalClientVerifiers.remove(acceptedVerifier);
            
            sessions.add(action.getSession());
            
            // everything else is already done, so lets end this method
            return true;
        }
        
        
        // now ask the persistent verifiers
        for (ClientVerifier shardletActionVerifier : persistentClientVerifiers) 
        {
            if (shardletActionVerifier.check(action))
            {
                // a verifier accepted a new client!
                // lets trigger the appropriate event
                trigger(action);
                
                // log it
                LOGGER.debug("Accepted client");
                
                sessions.add(action.getSession());
                
                // we dont need to delete the verifier, so we can simply
                // end this method directly here
                return true;
            }
        }
        
        
        // log it
        LOGGER.debug("Didn't accept client");
        
        // if this is executed, all verifiers of our lists denied the client
        // so we can return false 
        return false;
    }
    
    
    /**
     * Trigger an event within this context
     * 
     * @param event 
     */
    public void trigger(Event event)
    {
        aggregator.triggerEvent(event);
    }
    
    
    /**
     * Tries to create a new game app, CURRENTLY ONLY ON THE SAME SERVER!
     * TODO: add ability to specify specific remote R:S server
     * 
     * @param       gameApp                 The 'display name' of the game app that we'll try to load
     * @param       parameters              Init-Params of the new game app (e.g. variable parameters that
     *                                      you don't want to set directly within the game.xml deployment-descriptor)
     * @return      The game app as a remote context reference
     */
    @Override
    public ShardletContext.Remote tryCreateGameApp(String gameApp, Map<String, String> parameters)
    {
        // Call the context manager to check if it has a game app with that name
        // and create it (returns null when no game app with that name was found)
        // also reference this context as the parent context ;D
        GameAppContext newContext = (GameAppContext) manager.createGameApp(gameApp, this, parameters);

        // Return the reference of that context
        // Note that because or GenericContext implements 'RemoteShardletContext'
        // we can simply return the reference to that newly created context.
        return newContext;
    }
    
    
    /**
     * Use this method to transmit an event to another ShardletContext and
     * have it triggered there.
     * 
     * @param       event                   This is triggered in the remote Context
     *                                      ideally, it should be implemented there.
     */
    @Override
    public void sendRemoteEvent(Event event)
    {
        trigger(event);
    }
    
    
    /**
     * Inform the GameApp that we lost a client connection
     * 
     * Note that by the time this session reference arrives at the Shardlets of
     * the context, the facade will have deleted it from its list already, so you cannot
     * send any actions/packets to it anymore.
     * 
     * @param       session                 The session used to identify the client
     */
    public void handleLostClient(Session session)
    {
        sessions.remove(session);
        
        trigger(new NetworkClientDisconnectedEvent(session));
    }
    
    
    /**
     * Getter.
     * 
     * @return      The server's host IP address.
     */
    @Override
    public String getHostAddress()
    {
        return ip;
    }
    
    
    /**
     * Getter.
     * 
     * @return      The name of this game app, as declared in the 
     *              deployment descriptor
     */
    @Override
    public String getShardletContextName() 
    {
        return name;
    }

    
    /**
     * Getter.
     * 
     * @return      The local aggregator (its scope is this context)
     */
    @Override
    public EventAggregator getAggregator() 
    {
        return aggregator;
    }
    
    
    /**
     * Adds a new decider to the list. If we have a new client,
     * the context will run through the deciders checking if one of them
     * accepts the client.
     * 
     * @param       verifier                Checks whether we want to accept a new
     *                                      client based on its first message
     * @param       isPersistent            See above description. Should only be true
     *                                      if you want to auto-accept new clients.
     */
    @Override
    public void addClientVerifier(ClientVerifier verifier, boolean isPersistent)
    {
        if (isPersistent)
        {
            persistentClientVerifiers.add(verifier);
        }
        else
        {
            normalClientVerifiers.add(verifier);
        }
    }
    
    
    /**
     * Clears the client verifiers list.
     * 
     * @param persistentVerifiersOnly       Determines whether the context should
     *                                      delete only persistent verifiers.
     */
    @Override
    public void clearClientVerifiers(boolean persistentVerifiersOnly)
    {
        // they will always be cleared:
        persistentClientVerifiers.clear();
        
        if (!persistentVerifiersOnly)
        {
            // clear them conditionally:
            normalClientVerifiers.clear();
        }
    }

    
    /**
     * Getter.
     * 
     * @return      The reference to the parent context of this context
     */
    @Override
    public ShardletContext.Remote getParentContext()
    {
        return parent;
    }
    
    
    /**
     * Getter.
     * 
     * @return      The game-app specific server tick time interval (milliseconds)
     */
    @Override
    public int getHeartBeatInterval()
    {
        return heartBeatInterval;
    }
        
    
    /**
     * Getter.
     * 
     * @param       name
     * @return      The value of the init parameter if found
     */
    @Override
    public String getInitParameter(String name) 
    {
        return initParams.get(name);
    }

    
    /**
     * Getter.
     * 
     * @return      A list of all possible parameter names 
     */
    @Override
    public Enumeration<String> getInitParameterNames() 
    {
        return Collections.enumeration(initParams.keySet());
    }
    
    
    /**
     * Tries to unload/close/shutdown this game-app-context
     */
    @Override
    public void unload()
    {
        for (Session session : sessions) 
        {
            session.kick();
        }
        
        trigger(new GameAppUnloadedEvent());
        
        manager.notifyUnload(this);
    }
}
