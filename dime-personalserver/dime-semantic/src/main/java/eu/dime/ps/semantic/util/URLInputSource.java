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
