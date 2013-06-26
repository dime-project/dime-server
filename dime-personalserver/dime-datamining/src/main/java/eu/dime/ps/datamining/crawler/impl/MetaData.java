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
