/**
 * For copyright information see the LICENSE document.
 */

package realityshard.shardlet;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * An Event Aggregator is a module that decouples the component that actually 
 * triggers an Event from the component that receives that Event. 
 * Instead of observing a specific object for a specific event, you simply 
 * observe the Event Aggregator object for that event. As an object that actually 
 * triggers the event, you simply trigger it as a message to the Event Aggregator 
 * instead of directly messaging your subscribers. The Event Aggregator 
 * associates handler functions with Event-Types so that they are being called 
 * whenever their specific event gets triggered. Generally speaking, the Event 
 * Aggregator is a special kind of mediator.
 * 
 * @author _rusty
 */
public class EventAggregator
{
    
    // Storage class
    private static final class EventHandlerReference
    {
        public Object HandlerObject;
        public Method HandlerMethod;
    }

    // Util class
    private static final class Invokable implements Runnable
    {
        private final EventHandlerReference invokableHandler;
        private final Event parameter;
        
        
        public Invokable(EventHandlerReference invokableHandler, Event parameter)
        {
            this.invokableHandler = invokableHandler;
            this.parameter = parameter;
        }
        
        
        @Override
        public void run() 
        {
            try { 
                invokableHandler.HandlerMethod.invoke(invokableHandler.HandlerObject, parameter); 
            } 
            catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) { 
                LOGGER.warn("Could not execute an event handler", ex); 
            }
        }
    }
    
    
    private static final Logger LOGGER = LoggerFactory.getLogger(EventAggregator.class);
    
    private final Map<Class<? extends Event>, List<EventHandlerReference>> eventMapping;
    private final Executor executor;
    
    
    
    /**
     * Constructor.
     */
    public EventAggregator()
    {
        // get the executor defined by the application
        this.executor = GlobalExecutor.get();
        eventMapping = new ConcurrentHashMap<>();
    }
    
    
    /**
     * Register a new object that holds handler methods with this
     * event-aggregator.
     * 
     * This is done globally. Any event of the requested kind will be transmitted to
     * the handler. 
     * 
     * @param       handlerImpl             The object that holds different kind of handlers for various
     *                                      events. 
     *                                      These handler-methods should have <code>@EventListener</code>
     *                                      as an annotation, and follow the signature:
     *                                      * they have only one parameter which type implements Shardlet.Event and
     *                                      * return void
     */
    public void register(Object handlerImpl)
    {
        // get all the declared methods of the object with the handler implementations
        // so we can look for annotations
        Method[] methods = handlerImpl.getClass().getMethods();
        
        // try extracting the methods with our handler annotation, specified in
        // Shardlet.EventHandler

        for (Method method : methods) 
        {
            // failchecks first
            
            // check if the method has an annotation of type EventHandler
            if (method.getAnnotation(EventHandler.class) == null) { continue; }

            Class<?>[] params = method.getParameterTypes();
            
            // check if the method follows the general handler method conventions
            // meaning it takes only one argument which has a class that implements
            // Shardlet.Event
            if (params.length != 1 || !Event.class.isAssignableFrom(params[0]))
            {
                LOGGER.warn("An object has a method that is annotated as EventHandler but doesnt follow the signature.", handlerImpl);
                continue;
            }
            
            // we've got a valid handler. lets add it :D
            
            // lets build the EventHandlerReference now, used to store the handler its containing object
            EventHandlerReference handler = new EventHandlerReference();
            handler.HandlerMethod = method;
            handler.HandlerObject = handlerImpl;
            
            // check wich event class we are looking for...
            Class<? extends Event> clazz = (Class<? extends Event>)params[0];

            // we want to add the handler methods to a list of methods that have the same
            // signature, and thus handle the same event
            // so try to get the list
            List<EventHandlerReference> list = eventMapping.get(clazz);

            if (list == null)
            {
                // if there is no entry yet, create a new handler list for this event
                list = new CopyOnWriteArrayList<>();
                eventMapping.put(clazz, list);
            }

            // finally add the handler reference
            list.add(handler);
        }
    }
    
    
    /**
     * Unregister all handlers implemented within the given object
     * 
     * @param       handlerImpl             The object that holds different kind of handlers.
     */
    public void unregister(Object handlerImpl)
    {
        // its kinda tricky to remove stuff from our mapping, because we cant simply
        // look up an event. the values of our hashmap consist of lists of the associations
        // that we want to remove, so me must iterate those lists and find the values, then we must remove them from the lists
        
        List<EventHandlerReference> found = new ArrayList<>();
        
        // iterate trough the dictionary values, getting a list of associations
        for (List<EventHandlerReference> list: eventMapping.values())
        {
            // now iterate through the associations, and search for the object that implements
            // the handlers and that equals the one we have been given
            for (EventHandlerReference handler: list)
            {
                if (handler.HandlerObject.equals(handlerImpl))
                {
                    // we've found one of the associations that we want to remove.
                    // temporarily save it...
                    found.add(handler);
                }
            }
            
             // remove all those associations that we found
            for (EventHandlerReference handler: found)
            {
                list.remove(handler);
            }
            
            // clear up the list that we are using to temporarily save associations
            // because we'll simply re-use it for the next hashmap value
            found.clear();
        }
    }
    
    
    /**
     * Trigger an event globally; the EventAggregator will try to distribute it to all 
     * registered handler methods
     * 
     * @param       event                   The event that will be published on this event-aggregator
     */
    public void triggerEvent(Event event)
    {
        // get the listeners of the event
        List<EventHandlerReference> handlers = eventMapping.get(event.getClass());
        
        // failcheck
        if (handlers == null) { return; }
        
        for (EventHandlerReference handler: handlers)
        {
            // for each handler in the handler collection,
            // try to invoke the handler with
            // the object that holds it and the event
            executor.execute(new Invokable(handler, event));
        }
    }
}
