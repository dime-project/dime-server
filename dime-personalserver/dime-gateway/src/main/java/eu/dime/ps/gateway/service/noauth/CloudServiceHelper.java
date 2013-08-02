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

package eu.dime.ps.gateway.service.noauth;

import java.io.IOException;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import eu.dime.commons.util.HttpUtils;

public class CloudServiceHelper {
	
	Logger logger = Logger.getLogger(CloudServiceHelper.class);
	
	private DefaultHttpClient client;
	
	public CloudServiceHelper() {
		this.client = createClient();
	}
	
	private DefaultHttpClient createClient() {
		try {
			DefaultHttpClient base = HttpUtils.createHttpClient();
			SSLContext ctx = SSLContext.getInstance("SSL");
			X509TrustManager tm = new X509TrustManager() {

				public void checkClientTrusted(X509Certificate[] xcs,
						String string) throws CertificateException {
				}

				public void checkServerTrusted(X509Certificate[] xcs,
						String string) throws CertificateException {
				}

				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}
			};

			ctx.init(null, new TrustManager[] { tm }, null);
			SSLSocketFactory ssf = new SSLSocketFactory(ctx,
					SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			ClientConnectionManager ccm = base.getConnectionManager();
			SchemeRegistry sr = ccm.getSchemeRegistry();
			sr.register(new Scheme("https",443,ssf));

			return new DefaultHttpClient(ccm, base.getParams());

		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}

	}
	
	public String post(String serviceUrl, String auth, String content) {

		HttpResponse response = null;
		
		// execute the POST
		try {
			HttpPost httppost = new HttpPost(serviceUrl);
			if (!auth.equalsIgnoreCase("")) httppost.setHeader("Authorization",auth);
			StringEntity contentEntity = new StringEntity(content);
			contentEntity.setContentEncoding(HTTP.UTF_8);
			contentEntity.setContentType("application/json");
			httppost.setEntity(contentEntity);
			logger.debug("Executing POST request: " + httppost.getRequestLine());
			logger.debug("Payload: " + content);
			response = this.client.execute(httppost);
			BasicResponseHandler responsehandler = new BasicResponseHandler();
			String resp = responsehandler.handleResponse(response);
			logger.debug("Cloud Service response: " + resp);
			return resp;
		} catch (ClientProtocolException e) {
			if (e instanceof HttpResponseException) {
				HttpResponseException ex = (HttpResponseException)e;
				if (ex.getStatusCode() == HttpStatus.SC_NOT_FOUND)
					logger.debug("Cloud Service returned 404: Data Not Found");
				else 
					logger.error("Cloud Service returned " + ex.getStatusCode() + ": " + ex.getMessage(), ex);
				HttpEntity h = response.getEntity();
				if (h != null)
					try {
						EntityUtils.consume(h);
					} catch (IOException e1) {
						logger.error(e1.toString());
					}
			} else logger.error(e.toString());
		} catch (IOException e) {
			logger.error(e.toString());
		} finally {
			// We don't want to shut down!
			//this.client.getConnectionManager().shutdown();
		}
		return null;
	}

	public String get(String serviceUrl, String auth) {

		HttpResponse response = null;

		// execute the GET
		try {
			HttpGet httpget = new HttpGet(serviceUrl);
			if (!auth.equalsIgnoreCase("")) httpget.setHeader("Authorization",auth);
			logger.debug("Executing GET request:" + httpget.getRequestLine());
			response = this.client.execute(httpget);
			BasicResponseHandler responsehandler = new BasicResponseHandler();
			String resp = responsehandler.handleResponse(response);
			logger.debug("Cloud Service response: " + resp);
			return resp;
		} catch (ClientProtocolException e) {
			if (e instanceof HttpResponseException) {
				HttpResponseException ex = (HttpResponseException)e;
				if (ex.getStatusCode() == HttpStatus.SC_NOT_FOUND)
					logger.debug("Cloud Service returned 404: Data Not Found");
				else logger.error("Cloud Service returned " + ex.getStatusCode() + ": " + ex.getMessage());
				HttpEntity h = response.getEntity();
				if (h != null)
					try {
						EntityUtils.consume(h);
					} catch (IOException e1) {
						logger.error(e1.toString());
					}
			} else logger.error(e.toString());
		} catch (IOException e) {
			logger.error(e.toString());
		} finally {
			// We don't want to shut down!
			//this.client.getConnectionManager().shutdown();
		}
		return null;
	}

}
