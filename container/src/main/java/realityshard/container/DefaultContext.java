/**
 * For copyright information see the LICENSE document.
 */

package realityshard.container;

import realityshard.container.gameapp.GameAppContext;
import realityshard.shardlet.Session;
import realityshard.shardlet.ShardletContext;
import realityshard.shardlet.events.ContainerShutdownEvent;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import realityshard.shardlet.Action;


/**
 * All new sessions will be added to this context.
 *
 * If there is new packets from them, this context will distribute it to the other contexts,
 * and ask for the real owner of the session.
 *
 * @author _rusty
 */
public class DefaultContext extends GameAppContext
{

    private final static Logger LOGGER = LoggerFactory.getLogger(DefaultContext.class);

    private List<GameAppContext> contexts;


    /**
     * Constructor.
     */
    public DefaultContext()
    {
        super();
        contexts = new ArrayList<>();
    }


    /**
     * Add a new context to the collection of contexts,
     * so the next time a new session sends a packet, we may also ask that
     * context if it wants to accept the session.
     *
     * @param context
     */
    public void addContext(GameAppContext context)
    {
        contexts.add(context);
    }


    /**
     * Distributes the given action, and tries to find the owner of the
     * session that send the action.
     *
     * @param action
     */
    @Override
    public void handleIncoming(Action action)
    {
        GameSession session = (GameSession) action.getSession();

        LOGGER.debug("We've got a new client!");

        // check all game apps, maybe one of them wants to accept the client
        for (GameAppContext context : contexts)
        {
            if (context.acceptClient(action))
            {
                // set the new context of the session
                session.setShardletContext(context);

                // and we can end the search here
                return;
            }
        }

        // if this gets executed, we have not found a game app that wants to
        // accept this session, so we may as well kick it.
        LOGGER.warn("A client will be kicked, because no game app accepted it!");

        session.kick();
    }


    /**
     * We do not delegate anything here.
     * This method should not be invoked, so just log this.
     *
     * @param session
     */
    @Override
    public void handleLostClient(Session session)
    {
        LOGGER.warn("A client without game-app association just disconnected!");
    }


    /**
     * Trigger a container shutdown event in all game-app-contexts.
     */
    public void shutdown()
    {
        for (GameAppContext gameAppContext : contexts)
        {
            gameAppContext.getAggregator().triggerEvent(new ContainerShutdownEvent());
            gameAppContext.unload();
        }
    }


    /**
     * Removes a context from the collection of running contexts completely.
     *
     * @param context
     */
    public void removeGameApp(ShardletContext context)
    {
        LOGGER.debug("Removing a game-app instance.");

        contexts.remove((GameAppContext) context);

        // the rest is work for the garbage collector...
    }
}
