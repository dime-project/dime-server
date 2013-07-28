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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.dime.ps.datamining.crawler.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Will Fleury
 */
public class MetaData {
    
    //define all standard metadata keys here i.e.
    //public static final String MP3_TRACK_NAME_TAG = "MP3_TRACK_NAME";
    
    protected Map<String, String> dataMap;
    
    public MetaData() {
        this(new HashMap<String, String>());
    }
    
    public MetaData(Map<String, String> data) {
        this.dataMap = data;
    }
    
    public void addMetaTag(String key, String value) {
        dataMap.put(key, value);
    }
    
    public String getTagValue(String key) {
        return dataMap.get(key);
    }
    
    public Set<String> getMetaTags() {
        return dataMap.keySet();
    }

    public Map<String, String> getDataMap() {
        return dataMap;
    }

    public void setDataMap(Map<String, String> dataMap) {
        this.dataMap = dataMap;
    }
}
