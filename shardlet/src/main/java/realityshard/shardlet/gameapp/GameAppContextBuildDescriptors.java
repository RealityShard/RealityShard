/**
 * For copyright information see the LICENSE document.
 */

package realityshard.shardlet.gameapp;

import realityshard.shardlet.EventAggregator;
import realityshard.shardlet.Shardlet;
import java.util.Map;
import realityshard.shardlet.ShardletContext;


/**
 * Provides the grammar for building steps used in the creation
 * of a GameAppContext
 * 
 * @author _rusty
 */
public interface GameAppContextBuildDescriptors 
{
    
    /**
     * Build step: add the context manager
     */
    public interface BuildManager
    {
        public BuildParent useManager(GameAppManager manager);
    }
    
    
    /**
     * Build step: add the parent context (if any)
     */
    public interface BuildParent
    {
        public BuildEventAggregator useParent(ShardletContext.Remote parent);
    }

    
    /**
     * Build step: add the event aggregator
     */
    public interface BuildEventAggregator 
    {
        public BuildClassloader useAggregator(EventAggregator aggregator);
    }
    
    
    /**
     * Build step: add the setClassloader
     */
    public interface BuildClassloader 
    {
        public BuildName useClassloader(ClassLoader loader);
    }
    
    
    /**
     * Build step: add the name
     */
    public interface BuildName 
    {
        public BuildInitParams useName(String name);
    }
    
    
    /**
     * Build step: save the init parameters.
     */
    public interface BuildInitParams 
    {
        public BuildIpAddress useInitParams(Map<String, String> mandatoryParams);
        public BuildIpAddress useInitParams(Map<String, String> mandatoryParams, Map<String, String> additionalParams);
    }
    
    
    /**
     * Build step: add the ip of this server
     */
    public interface BuildIpAddress
    {
        public BuildShardlets useIpAddress(String ip);
    }
    
    
    /**
     * Build step: create or set the shardlets
     */
    public interface BuildShardlets 
    {
        public Build useShardlets(Map<Shardlet, Map<String, String>> shardlets);
    }
    
    
    /**
     * Build step: finish building the game app
     */
    public interface Build 
    {
        public GameAppContext build();
    }
}
