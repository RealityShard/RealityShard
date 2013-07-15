/**
 * For copyright information see the LICENSE document.
 */

package com.realityshard.container.gameapp;

import com.realityshard.shardlet.environment.GameAppManager;
import com.realityshard.shardlet.utils.GenericContext;
import com.realityshard.container.Pacemaker;
import com.realityshard.shardlet.*;
import com.realityshard.shardlet.events.GameAppUnloadedEvent;
import com.realityshard.shardlet.events.NetworkClientDisconnectedEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Implementation of a ShardletContext.
 * This is a basic implementation of an event-driven shardlet context,
 * that encapsulates a GameApp.
 * 
 * @see GameAppContextFluentBuilder
 * 
 * @author _rusty
 */
public class GameAppContext extends GenericContext
{
    
    private final static Logger LOGGER = LoggerFactory.getLogger(GameAppContext.class);
    
    
    protected GameAppManager manager;
    protected ClassLoader loader;

    protected Pacemaker pacemaker;
    
    protected List<Shardlet> shardlets;
    protected List<Session> sessions;

    
    /**
     * Constructor.
     * 
     * @see GameAppFluentBuilder
     */
    protected GameAppContext()
    {
        super();
        
        // dont forget to create the necessary objects - else we will get
        // strange null pointer errors later
        shardlets = new ArrayList<>();
        sessions = new ArrayList<>();
    }
        
    
    /**
     * Decide whether a client is accepted or not.
     * 
     * @param       action                  The first message send by the new client
     * @return  
     */
    public boolean acceptClient(TriggerableAction action)
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
                action.triggerEvent(aggregator);
                
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
                action.triggerEvent(aggregator);
                
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
     * Used by the ContextManager: provide this Context with a
     * new action, coming from a network client.
     * 
     * The action will be published as a
     * <code>ContextIncomingActionEvent</code>
     * within the event aggregator of this game-app
     * 
     * @param action 
     */
    public void handleIncomingAction(TriggerableAction action)
    {
        // send the event to the aggregator,
        // indirectly invoking the shardlets that handle new
        // incoming events
        action.triggerEvent(aggregator);
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
    public RemoteShardletContext tryCreateGameApp(String gameApp, Map<String, String> parameters)
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
     * Sends a new shardlet-event-action (meaning an action that
     * can be triggered as an event ;D)
     * 
     * Use this method to trigger events directly within the remote context,
     * by providing an action that creates these events.
     * 
     * Think of the 'event-action' as an event-wrapper.
     * 
     * @param       action                  The event-action that will be used in the remote
     *                                      context to trigger the desired concrete event
     */
    @Override
    public void sendTriggerableAction(TriggerableAction action) 
    {
        // :D
        handleIncomingAction(action);
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
        
        aggregator.triggerEvent(new NetworkClientDisconnectedEvent(session));
    }
    
    
    /**
     * Tries to unload/close/shutdown this game-app-context
     */
    @Override
    public void unload()
    {
        for (Session session : sessions) 
        {
            session.invalidate();
        }
        
        handleIncomingAction(new GameAppUnloadedEvent());
        
        manager.notifyUnload(this);
    }
}
