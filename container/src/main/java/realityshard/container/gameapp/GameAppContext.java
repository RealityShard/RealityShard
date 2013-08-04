/**
 * For copyright information see the LICENSE document.
 */

package realityshard.container.gameapp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import realityshard.container.events.Event;
import realityshard.container.events.EventAggregator;
import realityshard.container.network.Message;


/**
 * A game app context encapsulates a complete instance of a singe game app (unless
 * specified otherwise) and its objects.
 *
 * @author _rusty
 */
public interface GameAppContext 
{
    
    /**
     * This interface represents a remote game app context.
     * Use it pass events to other game apps.
     */
    public static interface Remote extends GameAppContext
    {

        /**
         * Use this method to transmit an event to another game app and
         * have it triggered there.
         * 
         * @param       event                   This is triggered in the remote Context
         *                                      ideally, it should be implemented there.
         */
        public void sendRemoteEvent(Event event);
    }
    

    /**
     * Handles a new network message.
     * 
     * @param       message 
     */
    public void handle(Message message);
    
    
    /**
     * Trigger an event in the internal aggregator.
     * 
     * @param event 
     */
    public void trigger(Event event);
    
        
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
    public GameAppContext.Remote getParentContext();

    
    /**
     * Default implementation of a ShardletContext.
     * Build you own contexts by extending this class.
     * 
     * @author _rusty
     */
    public static class Default implements GameAppContext, GameAppContext.Remote
    {

        private final static Logger LOGGER = LoggerFactory.getLogger(Default.class);

        protected EventAggregator aggregator;
        protected String name = "";
        protected GameAppManager manager;
        protected GameAppContext.Remote parent;

        
        /**
         * Constructor.
         * 
         * @param       manager             The container-specific game app manager.
         * @param       parent              The parent that created this context.
         */
        public Default(String name, GameAppManager manager, GameAppContext.Remote parent)
        {
            this.name = name;
            this.manager = manager;
            this.parent = parent;
            
            aggregator = new EventAggregator();
        }


        @Override
        public void sendRemoteEvent(Event event)
        {
            trigger(event);
        }
        

        @Override
        public void handle(Message message) 
        {
            trigger(message);
        }

        
        @Override
        public void trigger(Event event) 
        {
            aggregator.triggerEvent(event);
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
        public GameAppContext.Remote getParentContext()
        {
            return parent;
        }
    }
}
