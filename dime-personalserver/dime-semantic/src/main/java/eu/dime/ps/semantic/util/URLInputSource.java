package eu.dime.ps.semantic.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;

/**
 * Tries to reimplement the "Fetch as Googlebot" feature, a part of Google's "Webmaster Tools".
 * 
 * @author Ismael Rivera
 */
public class URLInputSource implements InputStreamSource {
    
	private URL url;
	private String accept;
    
    public URLInputSource(URL url)  {
        this.url = url;
        this.accept = "*/*";
    }

    public URLInputSource(URL url, String accept)  {
        this.url = url;
        this.accept = accept;
    }

    public HttpResponse getHttpResponse() throws IOException {
        final HttpClient httpclient = new DefaultHttpClient();
		try {
			final HttpGet httpget = new HttpGet(url.toString());
			httpget.getParams().setParameter(CoreProtocolPNames.USER_AGENT, "Mozilla/5.0 (compatible; dimeBot; http://dime-project.eu/)");
			if (accept != null) {
				httpget.setHeader("Accept", accept);
			}
			return httpclient.execute(httpget);
  		} catch (final Exception e) {
  			return null;
 		}
    	
    }
    
 	/**
	 * Fetches the given URL and prints the HTTP header fields and the content.
	 */
    public InputStream getInputStream() throws IOException {
		try {
	    	final HttpResponse response = getHttpResponse();
			final HttpEntity entity = response.getEntity();
			if (entity == null) {
				return null;
			}
			return entity.getContent();
		} catch (Exception e) {
			return null;
		}
    }

    public URL getURL() {
        return url;
    }
    
}
