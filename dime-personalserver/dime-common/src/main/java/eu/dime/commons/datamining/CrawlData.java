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

package eu.dime.commons.datamining;

import java.io.InputStream;

/**
 * Contains all the known data for a resource, output of the
 * crawling process.
 *  
 * @author Ismael Rivera (ismael.rivera@deri.org)
 */
public class CrawlData {
	
	private String uri;
	
	private CrawlMetadata metadata;
	
	private String hash;

	private transient InputStream binaryData;
    
    public CrawlData(String uri, CrawlMetadata metadata, String hash, InputStream binaryData) {
    	this.uri = uri;
    	this.metadata = metadata;
    	this.hash = hash;
    	this.binaryData = binaryData;
    }

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public CrawlMetadata getMetadata() {
		return metadata;
	}

	public void setMetadata(CrawlMetadata metadata) {
		this.metadata = metadata;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public InputStream getBinaryData() {
		return binaryData;
	}

	public void setBinaryData(InputStream binaryData) {
		this.binaryData = binaryData;
	}
    
}
