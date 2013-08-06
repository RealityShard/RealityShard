/**
 * For copyright information see the LICENSE document.
 */

package realityshard.container;

import java.util.HashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import realityshard.container.events.Event;
import realityshard.container.events.GameAppCreatedEvent;
import realityshard.container.events.GameAppUnloadedEvent;
import realityshard.container.events.NetworkClientConnectedEvent;
import realityshard.container.gameapp.GameAppContext;
import realityshard.container.gameapp.GameAppManager;


/**
 * Encapsulates all gameapp contexts of a certain kind.
 * 
 * If we got messages from a new client, this context will distribute them
 * to the other contexts.
 * 
 * Note that we only want different game app context instances.
 *
 * @author _rusty
 */
class MetaGameAppContext extends GameAppContext.Default
{

    private final static Logger LOGGER = LoggerFactory.getLogger(MetaGameAppContext.class);

    private Set<GameAppContext> contexts;


    /**
     * Constructor.
     */
    public MetaGameAppContext(String name, GameAppManager manager)
    {
        super(name, manager, null);
        contexts = new HashSet<>();
    }
    
    
    /**
     * Handles the new client event and distributes it to all sub-contexts
     * 
     * @param event 
     */
    @Event.Handler
    public void onNewClient(NetworkClientConnectedEvent event)
    {
        for (GameAppContext context : contexts) 
        {
            context.trigger(event);
        }
    }


    /**
     * Add a new context to the collection of contexts,
     * so the next time a new channel sends a packet, we may also ask that
     * context if it wants to accept the channel.
     * 
     * This will also trigger a GameAppCreatedEvent
     *
     * @param context
     */
    public void addContext(GameAppContext context)
    {
        context.trigger(new GameAppCreatedEvent());
        contexts.add(context);
    }


    /**
     * Shutdown/Unload all game apps managed by this.
     */
    public void shutdown()
    {
        for (GameAppContext context : contexts)
        {
            context.trigger(new GameAppUnloadedEvent());
        }
        
        contexts.clear();
    }
    
    
    /**
     * Shutdown/Unload a certain game app managed by this.
     * 
     * @param       context                 The game app.
     *                                      May fail if the game app is not managed by this.
     */
    public void shutdown(GameAppContext context)
    {
        if (contexts.contains(context))
        {
            context.trigger(new GameAppUnloadedEvent());
            contexts.remove(context);
        }
    }
}
