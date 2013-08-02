/*
* Copyright 2013 by the digital.me project (http://www.dime-project.eu).
*
* Licensed under the EUPL, Version 1.1 only (the "Licence");
* You may not use this work except in compliance with the Licence.
* You may obtain a copy of the Licence at:
*
* http://joinup.ec.europa.eu/software/page/eupl/licence-eupl
*
* Unless required by applicable law or agreed to in writing, software distributed under the Licence is distributed on an "AS IS" basis,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the Licence for the specific language governing permissions and limitations under the Licence.
*/

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
