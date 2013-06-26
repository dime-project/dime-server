package eu.dime.commons.util;

import org.apache.http.HttpHost;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;

public class HttpUtils {
	
	public static DefaultHttpClient createHttpClient() {
		
		DefaultHttpClient httpClient = new DefaultHttpClient();
		
		String proxyHost = System.getProperty("http.proxyHost");
		String proxyPort = System.getProperty("http.proxyPort");
		if ((proxyHost != null) && (proxyPort != null)) {
			HttpHost proxy = new HttpHost(proxyHost,Integer.parseInt(proxyPort));
			httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY,proxy);
		} 
		
		return httpClient;
		
	}

}
