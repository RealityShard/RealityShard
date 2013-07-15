/**
 * For copyright information see the LICENSE document.
 */

package com.realityshard.shardlet;

import java.util.Enumeration;
import java.util.Map;


/**
 * Defines a set of methods that a shardlet may use to communicate with its
 * shardlet context.
 * 
 * A context is the environment for the shardlets of a game app, and thus
 * the first place for communicaton with network clients or other shards 
 * or the server itself.
 *
 * There is one context per game app  
 *
 * @author _rusty
 */
public interface ShardletContext 
{

    /**
     * Returns the name and version of the Shardlet container which created
     * this context.
     *
     * @return      A <code>String</code> containing at least the 
     *              Shardlet container name and version number
     */
    public String getServerInfo();


    /**
     * Getter.
     *
     * Note that the port of a server is protocol specific, so we cannot determine
     * that here. You need to ask for the protocol of a context to get the ports.
     *
     * @return      The IP address of this server.
     */
    public String getHostAddress();
    
    
    /**
     * Getter.
     * 
     * Returns the name of this game app as specified by the deployment descriptor
     * or the dev environment.
     *
     * @return         The name of the game app or null if no name has been
     *                 declared in the deployment descriptor.
     */
    public String getShardletContextName();
    
    
    /**
     * Getter.
     * 
     * The event-aggregator is the place where every
     * shardlet event is distributed.
     * 
     * @return      The event aggregator bound to this context. 
     */
    public EventAggregator getAggregator();
    
    
    /**
     * Adds a new verifier to the list. If we have a new client,
     * the context will run through the verifiers checking if one of them
     * accepts the client.
     * 
     * If that decider accepted the client, the context will check whether it is
     * persistent and delete it if not.
     * 
     * A hint on the isPersistent boolean:
     * It determines if the verifier will be deleted after it has
     * accepted the first client or not. 
     * If this is persistent,there will be no way of deleting it, 
     * except by calling the <code>clearClientVerifiers</code> 
     * method, that deletes every verifier of the list
     * 
     * @see         ClientVerifier
     * 
     * @param       verifier                Checks whether we want to accept a new
     *                                      client based on its first message
     * @param       isPersistent            See above description. Should only be true
     *                                      if you want to auto-accept new clients.
     */
    public void addClientVerifier(ClientVerifier verifier, boolean isPersistent);
    
    
    /**
     * Clears the client verifiers list.
     * 
     * If the parameter is true, this will ignore any verifier that is not
     * persistent and instead remove all persistent ones.
     * (This is especially helpful when you want to auto-accept clients
     *  only temporary, but dont want to loose any special non-persistent clients
     *  when you decide to end the auto accept period)
     * 
     * @param       persistentVerifiersOnly Determines whether the context should
     *                                      delete only persistent verifiers.
     */
    public void clearClientVerifiers(boolean persistentVerifiersOnly);
    
    
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
    public RemoteShardletContext tryCreateGameApp(String gameApp, Map<String, String> parameters)
            throws Exception;
    
    
    /**
     * This method can be used to get a reference to the parent of this context.
     * Note that a parent context is only available if this game app was not
     * automatically created by the container at startup time.
     * 
     * @return      The reference to the parent context of this context
     */
    public RemoteShardletContext getParentContext();
    
    
    /**
     * This method can be used to access the server tick time interval,
     * which is set by the deployment-descriptor 'HeartBeat' option.
     * 
     * This time interval is the time that the shardlet container will wait
     * until it triggers the next HeartBeatEvent.
     * 
     * @return      The game-app specific server tick time interval (milliseconds)
     */
    public int getHeartBeatInterval();
    
    
    /**
     * Getter.
     * Init parameters are a string-string association of startup parameters that
     * can be defined in the game.xml of a deployed game app,
     * or the development environment. (see DevelopmentEvironment of the container)
     * 
     * @param       name                    The name of the (generic) parameter.
     *                                      The object will try to find it based on that string.
     * @return      The parameter's value if found
     */
    public String getInitParameter(String name);


    /**
     * Getter.
     * 
     * @return      All parameter names (the keys without values)
     */
    public Enumeration<String> getInitParameterNames();


    /**
     * Getter.
     * Returns your context-specific data that you attached to this shardlet context,
     * or null if there was nothing attached.
     *
     * Note that this is, by no means, threadsafe! Be aware that context specific data may be accessed by
     * multiple shardlets or other objects at the same time!
     * Hint: The attachment should only be assigned at context startup, and then not altered anymore.
     * 
     * @return      The context-specific data. Do not forget to cast this appropriately.
     */
    public Object getAttachment();


    /**
     * Setter.
     * This adds any context specific data to this context.
     * 
     * Beware of casting exceptions! You may want to have a single class
     * that manages all session specific data and only use that one as
     * an attachment
     * 
     * @param       attachment              The context-specific data. 
     */
    public void setAttachment(Object attachment);
    
    
    /**
     * This may be called by a shardlet if it wants the GameApp to be
     * unloaded from this R:S instance.
     * 
     * CAUTION: Before calling this, make sure that you saved all data,
     * as every object connected with the game app will get garbage-collected.
     */
    public void tryUnload();
}


