package eu.dime.ps.semantic.configuration;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;

/**
 * Contants URIs to define default named graphs, and configuration values.
 * 
 * @author Ismael Rivera
 */
public class Constants {
	
    /**
     * URI identifying the server in the config store.
     */
    public static final URI LOCAL_SERVER_CONFIG = new URIImpl("urn:dime:local:server");
    
    /**
     * URI identifying the user config in the config store.
     */
    public static final URI LOCAL_USER_CONFIG = new URIImpl("urn:dime:local:userconfig");
    
    /**
     * URI of the graph that is used to store the user config in the config-store
     */
    public static final URI LOCAL_USER_CONFIG_GRAPH = new URIImpl("urn:dime:local:userconfig:graph");
    
}
