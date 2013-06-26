package eu.dime.ps.datamining.util;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for extracting hyperlinks from any arbitrary text.
 * @author Ismael Rivera
 */
public class LinkExtractor {
	
	private static final Logger logger = LoggerFactory.getLogger(LinkExtractor.class);
	
	// regex from: http://daringfireball.net/2010/07/improved_regex_for_matching_urls
	// in the sentence "let's try www.deri.ie. to catch the url http://example.com and another (deri.ie/about/team) ..." extracts:
	// www.deri.ie, http://example.com and deri.ie/about/team
	private static final Pattern URL_PATTERN = Pattern.compile("(?i)\\b((?:https?://|www\\d{0,3}[.]|[a-z0-9.\\-]+[.][a-z]{2,4}/)(?:[^\\s()<>]+|\\(([^\\s()<>]+|(\\([^\\s()<>]+\\)))*\\))+(?:\\(([^\\s()<>]+|(\\([^\\s()<>]+\\)))*\\)|[^\\s`!()\\[\\]{};:'\".,<>?«»“”‘’]))");
	
	/**
	 * Finds hyperlinks in text.
	 * @param text the text where to look up for hyperlinks 
	 * @return a list of all hyperlinks contained in the text
	 */
	public static List<String> extract(String text) {
		return extract(text, false);
	}

	/**
	 * Same as {@link #extract(String)}, but instead of returning
	 * the hyperlink as found in the text, it follows the 303 redirections
	 * and returns the resolved URLs.
	 * @param text
	 * @param followRedirects
	 * @return
	 */
	public static List<String> extract(String text, boolean followRedirects) {
		Matcher matcher = URL_PATTERN.matcher(text);
		List<String> links = new ArrayList<String>();
		while (matcher.find()) {
			String url = matcher.group();
			if (followRedirects) {
				String redirect = followRedirects(url);
				if (redirect != null) links.add(redirect);
			} else {
				links.add(url);
			}
		}
		logger.debug(links.size()+" hyperlinks found in '"+text+"'");
		return links;
	}

	private static String followRedirects(String url) {
		String redirect = null;
        try {
            URL u = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) u.openConnection();
			conn.setRequestMethod("HEAD");
			conn.connect();
			if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
				redirect = conn.getURL().toString();
			}
	        return redirect;
        } catch (MalformedURLException e) {
        	logger.error("Cannot follow link "+url, e);
        	return null;
        } catch (Exception e) {
        	logger.warn("Cannot follow link "+url, e);
        	return url;
        }
	}
	
}
