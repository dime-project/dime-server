/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.dime.ps.datamining.crawler.impl;

import eu.dime.ps.datamining.crawler.Data;

/**
 *
 * @author Will Fleury
 */
public class FileData extends Data {
    private static final long serialVersionUID = 4359049532273159865L;
    
    protected MetaData metaData;

    /**
     * Default Constructor - needed for some forms of marshalling/unmarshalling.
     * Since I don't know what types of marshalling will be used (json,XML, etc)
     * i'll leave this in.
     */
    public FileData() { 
        
    }
    
    /**
     * Construct an instance with the specified hash.
     * @param hash hash the hash for the data
     */
    public FileData(String hash) {
        super (hash);
    }
    
    /**
     * Construct an instance with the specified hash and associated meta data
     * @param hash the hash for the data
     * @param metaData the metadata associated with whatever this instance is
     * describing.
     */
    public FileData(String hash, MetaData metaData) {
        super(hash);
        
        this.metaData = metaData;
    }

    
    public MetaData getMetaData() {
        return metaData;
    }

    public void setMetaData(MetaData metaData) {
        this.metaData = metaData;
    }
}
