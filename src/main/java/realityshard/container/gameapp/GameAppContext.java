/**
 * For copyright information see the LICENSE document.
 */

package realityshard.container.gameapp;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.UUID;
import realityshard.container.events.Event;
import realityshard.container.events.EventAggregator;
import realityshard.container.network.Message;
import realityshard.container.util.Handle;


/**
 * A game app context encapsulates a complete instance of a singe game app (unless
 * specified otherwise) and its objects.
 *
 * @author _rusty
 */
public interface GameAppContext extends GameAppManager
{    

    /**
     * Handles a new network message.
     * 
     * @param       message 
     */
    public void handleMessage(Message message);
    
    
    /**
     * Trigger an event in the internal aggregator.
     * 
     * @param event 
     */
    public void trigger(Event event);
    
    
    /**
     * Getter.
     * 
     * @return      The internal instance of EventAggregator.
     */
    public EventAggregator getEventAggregator();
    
        
    /**
     * Getter.
     * 
     * @return      The unique name of the type of this gameapp
     */
    public String getName();
    
    
    /**
     * Getter.
     * 
     * @return      The manager of this context. Use the manager
     *              to create new contexts or to try to shutdown this one.
     */
    public GameAppManager getManager();
    
    
    /**
     * This method can be used to get a reference to the parent of this context.
     * Note that a parent context is only available if this game app was not
     * automatically created by the container at startup time.
     * 
     * @return      The reference to the parent context of this context
     */
    public Handle<GameAppContext> getParentContext();

    
    /**
     * Default implementation of a ShardletContext.
     * Build you own contexts by extending this class.
     * 
     * @author _rusty
     */
    public static class Default implements GameAppContext
    {

        private EventAggregator aggregator;
        private String name = "";
        private GameAppManager manager;
        private Handle<GameAppContext> parent;

        
        /**
         * Constructor.
         * 
         * @param       name                The name of the game app
         * @param       manager             The container-specific game app manager.
         * @param       parent              The parent that created this context.
         */
        public Default(String name, GameAppManager manager, Handle<GameAppContext> parent)
        {
            this.name = name;
            this.manager = manager;
            this.parent = parent;
            
            aggregator = new EventAggregator();
        }
        

        @Override
        public void handleMessage(Message message) 
        {
            trigger(message);
        }

        
        @Override
        public void trigger(Event event) 
        {
            aggregator.triggerEvent(event);
        }
        
        
        @Override
        public EventAggregator getEventAggregator()
        {
            return aggregator;
        }

        
        @Override
        public String getName() 
        {
            return name;
        }
        
        
        @Override
        public GameAppManager getManager() 
        {
           return manager;
        }     
        
        
        @Override
        public Handle<GameAppContext> getParentContext()
        {
            return parent;
        }

        
        /**
         * Ask the game app manager to do this... 
         */
        @Override
        public boolean canCreateGameApp(String name) 
        {
            return manager.canCreateGameApp(name);
        }

        
        /**
         * Ask the game app manager to do this... 
         */
        @Override
        public Handle<GameAppContext> createGameApp(String name, Handle<GameAppContext> parent, Map<String, String> additionalParams) 
        {
            return manager.createGameApp(name, parent, additionalParams);
        }

        
        /**
         * Ask the game app manager to do this... 
         */
        @Override
        public Handle<GameAppContext> tryGetGameApp(UUID gameAppUid) 
        {
            return manager.tryGetGameApp(gameAppUid);
        }

        
        /**
         * Ask the game app manager to do this... 
         */
        @Override
        public void removeGameApp(Handle<GameAppContext> that) 
        {
            manager.removeGameApp(that);
        }

        
        /**
         * Ask the game app manager to do this... 
         */
        @Override
        public InetSocketAddress localAddressFor(Handle<GameAppContext> that) 
        {
            return manager.localAddressFor(that);
        }
    }
}
