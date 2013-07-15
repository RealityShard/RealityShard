/**
 * For copyright information see the LICENSE document.
 */

package com.realityshard.shardlet.utils;

import com.realityshard.shardlet.*;
import java.util.*;


/**
 * Used for convenience: hides the ShardletContext interface
 * implementation
 * 
 * @author _rusty
 */
public abstract class GenericContext implements 
        ShardletContext, 
        RemoteShardletContext
{
    
    protected EventAggregator aggregator;
    protected String name = "";
    protected String ip = "";
    protected RemoteShardletContext parent;
    protected int heartBeatInterval;
    protected Map<String, String> initParams;
    
    protected List<ClientVerifier> normalClientVerifiers;
    protected List<ClientVerifier> persistentClientVerifiers;
    
    private volatile Object attachment;
    
    
    /**
     * Constructor.
     */
    protected GenericContext()
    {
        normalClientVerifiers = new ArrayList<>();
        persistentClientVerifiers = new ArrayList<>();
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
     * @param       isPersistant            See above description. Should only be true
     *                                      if you want to auto-accept new clients.
     */
    @Override
    public void addClientVerifier(ClientVerifier verifier, boolean isPersistant)
    {
        if (isPersistant)
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
     * Tries to create a new game app, CURRENTLY ONLY ON THE SAME SERVER!
     * TODO: add ability to specify specific remote R:S server
     * 
     * @param       gameApp                 The 'display name' of the game app that we'll try to load
     * @param       parameters              Init-Params of the new game app (e.g. variable parameters that
     *                                      you don't want to set directly within the game.xml deployment-descriptor)
     * @return      The game app as a remote context reference
     * @throws      Exception               If something went wrong with creating the game app
     *                                      (Does not indicate that the game app doesnt exist,
     *                                      because in that case the returned reference would simply be null)
     */
    @Override
    public abstract RemoteShardletContext tryCreateGameApp(String gameApp, Map<String, String> parameters)
            throws Exception;
    
    
    /**
     * Getter.
     * 
     * @return      The reference to the parent context of this context
     */
    @Override
    public RemoteShardletContext getParentContext()
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
     * Getter.
     * 
     * @return
     */
    @Override
    public Object getAttachment() 
    {
        return attachment;
    }


    /**
     * Setter.
     * 
     * @param       attachment
     */
    @Override
    public void setAttachment(Object attachment) 
    {
        this.attachment = attachment;
    }
    
    
    /**
     * This may be called by a shardlet if it wants the GameApp to be
     * unloaded from this R:S instance.
     * 
     * CAUTION: Before calling this, make sure that you saved all data,
     * as every object connected with the game app will get garbage-collected.
     */
    @Override
    public abstract void unload();
    
    
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
    public abstract void sendTriggerableAction(TriggerableAction action);
}
