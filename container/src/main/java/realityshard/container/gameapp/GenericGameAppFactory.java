/**
 * For copyright information see the LICENSE document.
 */

package realityshard.container.gameapp;

import realityshard.shardlet.Shardlet;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import realityshard.shardlet.EventAggregator;
import realityshard.shardlet.Session;
import realityshard.shardlet.ShardletContext;


/**
 * This class holds all the data necessary to create a new game app.
 * In our case, this is done by using the classes that were added
 * to this factory as prototypes. This means they are not loaded dynamically.
 *
 * @author _rusty
 */
public abstract class GenericGameAppFactory implements GameAppFactory
{

    private final static Logger LOGGER = LoggerFactory.getLogger(GenericGameAppFactory.class);

    private final String name;
    private final String ip;
    private final int port;
    private final boolean isStartup;
    private final Map<String, String> initParams;
    private final Map<Shardlet, Map<String, String>> shardletPrototypes; // second type param is the init params


    /**
     * Constructor.
     *
     * @param       name                    Name of this game app.
     * @param       ip                      The IP or hostname of this local server
     * @param       isStartup               Bool to indicate whether this should
     *                                      be producing one game app when the container
     *                                      starts.
     * @param       initParams              The initParams for the game app
     */
    public GenericGameAppFactory(
            String name,
            String ip,
            int port,
            boolean isStartup,
            Map<String, String> initParams)
    {
        this.name = name;
        this.ip = ip;
        this.port = port;
        this.isStartup = isStartup;
        this.initParams = initParams;
        this.shardletPrototypes = new HashMap<>();
    }


    /**
     * Add a new shardlet prototype and the data used for initialization
     *
     * @param       shardlet                The prototype shardlet
     * @param       initParams              The initialization parameters used when
     *                                      the shardlet is initialized.
     * @return      This factory, so you can chain these kind of methods.
     */
    public GenericGameAppFactory addShardlet(Shardlet shardlet, Map<String, String> initParams)
    {
        shardletPrototypes.put(shardlet, initParams);

        return this;
    }


    /**
     * Factory method.
     *
     * @return      A newly created instance of the GameApp this factory produces.
     */
    @Override
    public GameAppContext produceGameApp(GameAppManager manager, ShardletContext.Remote parent, Map<String, String> additionalParams)
    {
        // now, lets create the context
        // although it still is quite verbose
        return GameAppContextFluentBuilder
                .start()
                .useManager(manager)
                .useParent(parent)
                .useAggregator(new EventAggregator())
                .useClassloader(ClassLoader.getSystemClassLoader())
                .useName(name)
                .useInitParams(initParams, additionalParams)
                .useIpAddress(ip)
                .useShardlets(cloneShardlets())
                .build();
    }


    /**
     * Clones the shardlet prototypes that this factory uses,
     * and initializes them during that process. They are ready to be used
     * with a new game app.
     *
     * @return      The cloned shardlets.
     */
    private Map<Shardlet, Map<String, String>> cloneShardlets()
    {
        Map<Shardlet, Map<String, String>> result = new HashMap<>();

        for (Map.Entry<Shardlet, Map<String, String>> entry : shardletPrototypes.entrySet())
        {
            // just for convenience
            Shardlet shardlet = entry.getKey();
            Map<String, String> params = entry.getValue();

            // create the clone
            Shardlet clone = shardlet.clone();

            // and add it to the result set
            result.put(clone, params);
        }

        return result;
    }


    /**
     * Getter.
     *
     * @return      The Game-App's name (may be used to identifiy this game app!)
     */
    @Override
    public String getName()
    {
        return name;
    }


    /**
     * Should one of the game apps of this factory be loaded at the start of the
     * container?
     *
     * @return
     */
    @Override
    public boolean isStartup()
    {
        return isStartup;
    }


    /**
     * Getter.
     *
     * @return
     */
    @Override
    public int getProtocolPort()
    {
        return port;
    }


    @Override
    public abstract void initializeSession(Session session);
}
