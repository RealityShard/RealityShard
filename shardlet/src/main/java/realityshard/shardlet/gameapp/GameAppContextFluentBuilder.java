/**
 * For copyright information see the LICENSE document.
 */

package realityshard.shardlet.gameapp;

import realityshard.shardlet.gameapp.GameAppContextBuildDescriptors.Build;
import realityshard.shardlet.gameapp.GameAppContextBuildDescriptors.BuildClassloader;
import realityshard.shardlet.gameapp.GameAppContextBuildDescriptors.BuildEventAggregator;
import realityshard.shardlet.gameapp.GameAppContextBuildDescriptors.BuildIpAddress;
import realityshard.shardlet.gameapp.GameAppContextBuildDescriptors.BuildInitParams;
import realityshard.shardlet.gameapp.GameAppContextBuildDescriptors.BuildManager;
import realityshard.shardlet.gameapp.GameAppContextBuildDescriptors.BuildName;
import realityshard.shardlet.gameapp.GameAppContextBuildDescriptors.BuildParent;
import realityshard.shardlet.gameapp.GameAppContextBuildDescriptors.BuildShardlets;
import realityshard.shardlet.EventAggregator;
import realityshard.shardlet.Shardlet;
import realityshard.shardlet.events.GameAppCreatedEvent;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import realityshard.shardlet.ShardletContext;


/**
 * This class is used to build a new Game App Context
 * 
 * @author _rusty
 */
public final class GameAppContextFluentBuilder extends GameAppContext implements
        BuildManager,
        BuildParent,
        BuildEventAggregator,
        BuildClassloader,
        BuildName,
        BuildIpAddress,
        BuildInitParams,
        BuildShardlets,
        Build
{

    private final static Logger LOGGER = LoggerFactory.getLogger(GameAppContext.class);
    
    
    /**
     * Constructor.
     */
    private GameAppContextFluentBuilder()
    {
        // private means
        // this will keep people from messing with this class.
        super();
    }
    
    
    /**
     * Initiate the building process
     * 
     * @return 
     */
    public static BuildManager start()
    {
        return new GameAppContextFluentBuilder();
    }
    
    
    /**
     * Step.
     * 
     * @param       manager
     * @return 
     */
    @Override
    public BuildParent useManager(GameAppManager manager) 
    {
        this.manager = manager;
        return this;
    }
    
    
    /**
     * Step.
     * 
     * @param       parent 
     * @return 
     */
    @Override
    public BuildEventAggregator useParent(ShardletContext.Remote parent) 
    {
        this.parent = parent;
        return this;
    }
    
    /**
     * Step.
     * 
     * @param       aggregator
     * @return 
     */
    @Override
    public BuildClassloader useAggregator(EventAggregator aggregator) 
    {
        this.aggregator = aggregator;
        return this;
    }

    
    /**
     * Step.
     * 
     * @param       loader
     * @return 
     */
    @Override
    public BuildName useClassloader(ClassLoader loader) 
    {
        this.loader = loader;
        return this;
    }
    
    
    /**
     * Step.
     * 
     * @param       name
     * @return 
     */
    @Override
    public BuildInitParams useName(String name) 
    {
        this.name = name;
        return this;
    }
    
    
    /**
     * Step. (Choice)
     * 
     * @param       mandatoryParams 
     * @return 
     */
    @Override
    public BuildIpAddress useInitParams(Map<String, String> mandatoryParams) 
    {
        // create a new map so we can mess with it
        initParams = new HashMap<>();
        
        // add the mandatory 
        initParams.putAll(mandatoryParams);
        
        // we'r done, return the updated object
        return this;
    }
    
    
    /**
     * Step. (Choice)
     * 
     * @param       mandatoryParams 
     * @param       additionalParams
     * @return 
     */
    @Override
    public BuildIpAddress useInitParams(Map<String, String> mandatoryParams, Map<String, String> additionalParams) 
    {
        // create a new map so we can mess with it
        initParams = new HashMap<>();
        
        // add the additional stuff first
        initParams.putAll(additionalParams);
        
        // then add the mandatory 
        initParams.putAll(mandatoryParams);
        
        // we'r done, return the updated object
        return this;
    }
    
    
    /**
     * Step.
     * 
     * @param       ip
     * @return 
     */
    @Override
    public BuildShardlets useIpAddress(String ip)
    {
        this.ip = ip;
        return this;
    }

    
    /**
     * Step. (Choice)
     * 
     * @param shardlets
     * @return 
     */
    @Override
    public Build useShardlets(Map<Shardlet, Map<String, String>> shardlets) 
    {
        for (Map.Entry<Shardlet, Map<String, String>> entry : shardlets.entrySet()) 
        {
            // just for convenience
            Shardlet shardlet = entry.getKey();
            Map<String, String> params = entry.getValue();
            
            // initialize the shardlet
            shardlet.init(params, this);
            
            // add it to our list
            this.shardlets.add(shardlet);

            // add it to the aggregator
            aggregator.register(shardlet);
        }
        
        return this;
    }
    
    
    /**
     * Step. finish this build-process
     * 
     * @return 
     */
    @Override
    public GameAppContext build() 
    {
        // TODO: check if we got some cleanup to do here

        // and dont forget to invoke the startup event
        this.trigger(new GameAppCreatedEvent());

        return this;
    }
}
