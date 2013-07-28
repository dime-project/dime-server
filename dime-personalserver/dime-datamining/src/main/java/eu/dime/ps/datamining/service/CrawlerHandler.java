/*
* Copyright 2013 by the digital.me project (http:\\www.dime-project.eu).
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

package eu.dime.ps.datamining.service;

import java.util.Collection;

import java.util.Map;
import org.ontoware.rdfreactor.schema.rdfs.Resource;

/**
 * A class should implement this interface if it wants to register and deal with
 * service crawling results.
 * 
 * @author Will Fleury
 */
public interface CrawlerHandler {
    
    /**
     * This method is called when the crawler successfully returns a result. 
     * Note that this is called after every crawl for the service it is registered
     * with. 
     * 
     * @param resources the resources which were found when crawling
     */
    public void onResult(Map<PathDescriptor, Collection<? extends Resource>> resources);
    
    /**
     * This method is called whenever this was an error during the crawling 
     * service..
     * 
     * @param error the cause
     */
    public void onError(Throwable error);
}
