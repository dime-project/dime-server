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
