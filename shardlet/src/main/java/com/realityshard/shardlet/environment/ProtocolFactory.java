/**
 * For copyright information see the LICENSE document.
 */

package com.realityshard.shardlet.environment;

import com.realityshard.shardlet.ProtocolFilter;
import java.util.List;


/**
 * This class acts as a dataholder for ONLY ONE PROTOCOL!
 */
public class ProtocolFactory 
{
    private final String name;
    private final int port;
    private final List<ProtocolFilter> inFilters;
    private final List<ProtocolFilter> outfilters;

    /**
     * Constructor.
     *
     * @param   name                    Name of the protocol.
     * @param   port                    The port that this protocol runs on.
     * @param   inFilters               All in-filter references (Must be
     *                                  already initialized)
     * @param   outfilters              All out-filter references (Must be
     *                                  already initialized)
     */
    public ProtocolFactory(String name, int port, List<ProtocolFilter> inFilters, List<ProtocolFilter> outfilters) {
        this.name = name;
        this.port = port;
        this.inFilters = inFilters;
        this.outfilters = outfilters;
    }

    public String getName() {
        return name;
    }

    public int getPort() {
        return port;
    }

    public List<ProtocolFilter> getInFilters() {
        return inFilters;
    }

    public List<ProtocolFilter> getOutfilters() {
        return outfilters;
    }

}
