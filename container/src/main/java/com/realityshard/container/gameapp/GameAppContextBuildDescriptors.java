/**
 * For copyright information see the LICENSE document.
 */

package com.realityshard.container.gameapp;

import com.realityshard.shardlet.environment.GameAppManager;
import com.realityshard.container.Pacemaker;
import com.realityshard.shardlet.EventAggregator;
import com.realityshard.shardlet.RemoteShardletContext;
import com.realityshard.shardlet.Shardlet;
import java.util.Map;


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
        public BuildEventAggregator useParent(RemoteShardletContext parent);
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
        public BuildPacemaker useName(String name);
    }

    
    /**
     * Build step: build the pacemaker.
     */
    public interface BuildPacemaker 
    {
        public BuildInitParams useHeartBeat(int milliseconds);
        public BuildInitParams usePacemaker(Pacemaker pacemaker);
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
