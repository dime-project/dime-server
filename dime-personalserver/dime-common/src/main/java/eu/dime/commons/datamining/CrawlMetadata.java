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

/**
 * Metadata of a resource produced by the crawler. 
 * 
 * @author Ismael Rivera (ismael.rivera@deri.og)
 */
public class CrawlMetadata {
	
	/**
	 * serialized RDF data
	 */
	private String content;
	
	/**
	 *  mimeType of the serialized RDF data
	 */
	private String mimeType;
    
    public CrawlMetadata(String content, String mimeType) {
    	this.content = content;
    	this.mimeType = mimeType;
    }

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

}
