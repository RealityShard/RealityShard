/**
 * For copyright information see the LICENSE document.
 */

package realityshard.shardlet.utils;

import realityshard.shardlet.Event;
import realityshard.shardlet.EventAggregator;
import realityshard.shardlet.Shardlet;
import realityshard.shardlet.ShardletContext;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This class is a generic shardlet template, that will auto-load the
 * config and household the config/context/aggregator so the sub class
 * doesn't need to implement that code over and over again.
 * Inherit each shardlet from this class for simplicity of usage.
 * 
 * @author _rusty
 */
public abstract  class GenericShardlet implements Shardlet
{    
    
    private final static Logger LOGGER = LoggerFactory.getLogger(GenericShardlet.class);
    
    private Map<String, String> initParams;
    private ShardletContext context;
    private EventAggregator aggregator;
    
    
    /**
     * Called when this is used within the development environment.
     */
    @Override
    public Shardlet clone()
    {
        try 
        {
            // yep, this is actually needed, because we specified it within the
            // interface (which was done because interfaces in general dont 
            // specify "clone()" ... w/e)
            LOGGER.debug("Shardlet Class: {}", this.getClass().getCanonicalName());
            return this.getClass().newInstance();
        } 
        catch (InstantiationException | IllegalAccessException ex) 
        {
            LOGGER.error("Could not clone this shardlet.", ex);
        }
        
        return null;
    }
    
    
    /**
     * Extracts the event-aggregator from the context.
     * 
     * When using this class, make sure to override the init() method,
     * as it will be called by this method.
     * 
     */
    @Override
    public final void init(Map<String, String> initParams, ShardletContext context) 
    {
        // init the attributes
        this.initParams = initParams;
        this.context = context;
        this.aggregator = context.getAggregator();
        
        // call the init() method - it will be overridden by the
        // sub class.
        init();
    }
    
    
    /**
     * Will be called by the init(config) method,
     * so just override this to ensure your code
     * gets loaded at shardlet-startup.
     * This method can also be left empty.
     */
    protected abstract void init();
    
    
    /**
     * Publish an Event.
     * This hides the aggregator from sub types.
     * 
     * Note that we need to make this generic, because we need to keep the type.
     * 
     * @param       <E>                     The event type.
     * @param       event                   The event object.
     */
    protected final <E extends Event> void publishEvent(E event)
    {
        aggregator.triggerEvent(event);
    }
    
    
    /**
     * Getter.
     * 
     * @return      The current context.
     */
    public ShardletContext getShardletContext() 
    {
        return context;
    }


    /**
     * Getter.
     * 
     * @param       name                    The name of the parameter that we'll try to
     *                                      access
     * @return      The value if the parameter exists
     */
    public String getInitParameter(String name) 
    {
        if (initParams == null) 
        {
            throw new IllegalStateException("Error: Shardlet has not been initialized!");
        }

        return initParams.get(name);
    }
}
