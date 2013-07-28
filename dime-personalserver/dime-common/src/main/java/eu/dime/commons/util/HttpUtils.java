/*
* Copyright 2013 by the digital.me project (http:\\www.dime-project.eu).
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
