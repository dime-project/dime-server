package eu.dime.ps.datamining.crawler;


import java.io.Serializable;

/**
 * This is an abstract class which represents various types of data which is 
 * passed to the data mining module for processing. 
 * 
 * The data could be entire documents, mp3 meta data, contacts etc.. 
 * 
 * @author Will Fleury
 */
public abstract class Data implements Serializable {
    
    /**
     * This is the hash of whatever data is represented.
     */
    protected String hash;
    
    public Data() { }
    
    public Data(String hash) {
        this.hash = hash;
    }
    

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

}
