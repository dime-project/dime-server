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
