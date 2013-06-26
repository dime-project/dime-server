package eu.dime.ps.controllers.context.browing;

/**
 * This represents the events that can occur to webpages while browsing
 * on the Internet such as a page being loaded, a page gaining focus, etc. 
 * 
 * @author Ismael Rivera
 */
public class BrowsingEvent {

	public static final String ACTION_PAGE_LOADED = "PAGE LOADED";
	public static final String ACTION_PAGE_UNLOADED = "PAGE UNLOADED";
	public static final String ACTION_PAGE_FOCUSED = "PAGE FOCUSED";
	public static final String ACTION_PAGE_BLURRED = "PAGE BLURRED";
	
	private String action;
	private String fullUrl;
	
	public BrowsingEvent(String action) {
		this.action = action;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getFullUrl() {
		return fullUrl;
	}

	public void setFullUrl(String fullUrl) {
		this.fullUrl = fullUrl;
	}
	
}
