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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Data implements Serializable {

	private static final long serialVersionUID = -3780530123410393312L;

	/**
	 * Identifier of the data or resource. This is an URI specifying the machine
	 * the data was obtained from, protocol (e.g. file, mbox), and the local path
	 * to the resource, file, etc.
	 */
	protected String id;
    
	/**
	 * Metadata or relevant information about the resource.
	 * It is preferred to use properties of the ontologies for the keys, such as
	 * nfo:fileName, nfo:fileSize, etc.
	 */
	protected Map<String, String> metadata;
	
	/**
	 * The entire contents of a file/resource serialised as a byte array.
	 */
	protected transient byte[] content;
    
    public Data(String id, Map<String, String> metadata, byte[] content) {
    	this.id = id;
    	this.metadata = metadata;
    	this.content = content;
    }
    
    public Data(String id) {
        this(id, new HashMap<String, String>(), null);
    }
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    
    public Map<String, String> getMetadata() {
    	return metadata;
    }
    
    public String getMetadata(String key) {
    	return metadata.get(key);
    }
    
    public void setMetadata(String key, String value) {
    	metadata.put(key, value);
    }
	
    public byte[] getContent() {
    	return this.content;
    }
    
    public void setContent(byte[] content) {
    	this.content = content;
    }

    @Override
    public String toString() {
    	return "eu.dime.commons.datamining.Data[id="+this.id+", metadata="+this.metadata+"]";
    }

}
