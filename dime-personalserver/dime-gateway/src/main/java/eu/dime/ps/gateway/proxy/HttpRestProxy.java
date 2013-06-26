package eu.dime.ps.gateway.proxy;

import java.io.BufferedInputStream;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpDelete;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import eu.dime.commons.util.HttpUtils;
import eu.dime.ps.gateway.exception.ServiceNotAvailableException;

/**
 * Connects to an external service using HTTP connection
 * 
 * @author Sophie.Wrobel
 * @author <a href="mailto:bourimi@wiwi.uni-siegen.de"> Mohamed Bourimi (mbourimi)</a>
 * @author Ismael Rivera
 */
public class HttpRestProxy implements ServiceProxy {

	private static final Logger logger = LoggerFactory.getLogger(HttpRestProxy.class);
	
	public static final String DEFAULT_HTTP_CLIENT_SCHEME = "https";
	public static final int DEFAULT_HTTP_CLIENT_PORT = 443;
	
	private String scheme;
	private int port;
	
	private URL url;
	private String username = null;
	private String password = null;
	private DefaultHttpClient client;
	private int serverPort;
	private String serverRealm;

	/**
	 * Opens connection to specified external API
	 * 
	 * @param url
	 *            to external API gateway
	 * @throws ServiceNotAvailableException
	 */
	public HttpRestProxy(URL url) throws ServiceNotAvailableException {
		this.url = url;
	
		// reading gateway.properties file
		ResourceBundle bundle = ResourceBundle.getBundle("gateway");
		if (bundle.containsKey("HTTP_CLIENT_SCHEME")) {
			this.scheme = bundle.getString("HTTP_CLIENT_SCHEME");
		} else {
			this.scheme = DEFAULT_HTTP_CLIENT_SCHEME;
		}
		if (bundle.containsKey("HTTP_CLIENT_PORT")) {
			this.port = Integer.parseInt(bundle.getString("HTTP_CLIENT_PORT"));
		} else {
			this.port = DEFAULT_HTTP_CLIENT_PORT;
		}
	}
	
	/**
	 * Opens authenticated connection to specified external API
	 * 
	 * @param url
	 *            to external API gateway
	 * @param username
	 *            username for authenticating with BasicHTTPAuthentication
	 * @param password
	 *            password for authenticating with BasicHTTPAuthentication
	 * @throws ServiceNotAvailableException
	 */
	public HttpRestProxy(URL url, int serverPort, String realm,
			String username, String password)
			throws ServiceNotAvailableException {
		this(url);
		this.serverPort = serverPort;
		this.serverRealm = realm;
		this.username = username;
		this.password = password;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * eu.dime.ps.communications.services.ServiceProxy#query(java.lang.String)
	 */
	public String get(String query) throws ServiceNotAvailableException {
		return get(query, new HashMap<String, String>(0));
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * eu.dime.ps.communications.services.ServiceProxy#query(java.lang.String, java.util.Map)
	 */
	public String get(String query, Map<String, String> headers) throws ServiceNotAvailableException {

		StringBuilder result = new StringBuilder();
		this.client = this.getConnection(query);

		// execute the GET
		try {
			HttpGet httpget = new HttpGet(this.url + query);
			// adding headers to request
			for (String name : headers.keySet()) {
				httpget.addHeader(name, headers.get(name));
			}
			//add basic auth header
                        
                        //fix by YellowMap, illegal request because of line break
                        String encoded = Base64.encodeBase64String((this.username+":"+this.password).getBytes()).replace("\r\n", "");
                        
			httpget.addHeader("Authorization","Basic "+ encoded);
			logger.info("Executing GET request: " + httpget.getRequestLine());
			HttpResponse response = this.client.execute(httpget);
			HttpEntity entity = response.getEntity();

			try {
				char[] buffer = new char[4*1024];
				int length;
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(response.getEntity().getContent()));
				while ((length = reader.read(buffer)) >= 0) {
				    result.append(buffer, 0, length);
				}
			} catch (RuntimeException e) {
				httpget.abort();
			}

			EntityUtils.consume(entity);
		} catch (ClientProtocolException e) {
			throw new ServiceNotAvailableException (e);
		} catch (IOException e) {
			throw new ServiceNotAvailableException (e);
		} finally {
			// We don't want to shut down!
			// this.client.getConnectionManager().shutdown();
		}

		return result.toString();
	}
	
	
	public BinaryFile getBinary(String query, Map<String, String> headers) throws ServiceNotAvailableException {

		
		this.client = this.getConnection(query);
		InputStream byteStream = null;
		BinaryFile result = new BinaryFile();
		// execute the GET
		try {
			HttpGet httpget = new HttpGet(this.url + query);
			// adding headers to request
			for (String name : headers.keySet()) {
				httpget.addHeader(name, headers.get(name));
			}
			//add basic auth header
                        
                        //fix by YellowMap, illegal request because of line break
                        String encoded = Base64.encodeBase64String((this.username+":"+this.password).getBytes()).replace("\r\n", "");
                        
			httpget.addHeader("Authorization","Basic "+ encoded);
			logger.info("Executing GET request: " + httpget.getRequestLine());
			HttpResponse response = this.client.execute(httpget);
			HttpEntity entity = response.getEntity();
			byteStream = entity.getContent();	
			byte[] bytes = readAndClose(new BufferedInputStream(byteStream));
			
			result.setByteStream(new ByteArrayInputStream(bytes));
			result.setType(	entity.getContentType().toString());
		} catch (ClientProtocolException e) {
			throw new ServiceNotAvailableException (e);
		} catch (IOException e) {
			throw new ServiceNotAvailableException (e);
		} finally {
			// We don't want to shut down!
			// this.client.getConnectionManager().shutdown();
		}

		return result;
	}
	
	byte[] readAndClose(InputStream is){    
	    byte[] bucket = new byte[32*1024]; 
	    ByteArrayOutputStream result = null; 
	    try  {
	      try {
	        result = new ByteArrayOutputStream();
	        int bytesRead = 0;
	        while(bytesRead != -1){
	          bytesRead = is.read(bucket);
	          if(bytesRead > 0){
	            result.write(bucket, 0, bytesRead);
	          }
	        }
	      }
	      finally {
	    	is.close();
	    	result.close();
	      }
	    }
	    catch (IOException e){
	      logger.error("Could not download file.", e);
	    }
	    return result.toByteArray();
	  }

	
	

	/*
	 * (non-Javadoc)
	 * 
	 * @see eu.dime.ps.communications.services.ServiceProxy#authenticate(org.
	 * springframework.security.authentication.AbstractAuthenticationToken)
	 */
	@Override
	public boolean authenticate(AbstractAuthenticationToken authToken) {
		UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) authToken;
		this.username = (String) token.getPrincipal();
		this.password = (String) token.getCredentials();
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * eu.dime.ps.communications.services.proxy.ServiceProxy#post(java.lang.
	 * String, java.lang.String)
	 */
	@Override
	public int post(String query, String content)
			throws ServiceNotAvailableException {

		return post (query, content, "application/json");
	}
	

	public int post(String query, String content, String contentType)
			throws ServiceNotAvailableException {

		int status = HttpStatus.SC_OK;
		this.client = this.getConnection(query);

		// execute the POST
		try {
			HttpPost httppost = new HttpPost(this.url + query);
			StringEntity contentEntity = new StringEntity(content);
			contentEntity.setContentEncoding(HTTP.UTF_8);
			contentEntity.setContentType(contentType);
			httppost.setEntity(contentEntity);
			logger.info("Executing POST request:" + httppost.getRequestLine());
			logger.debug("Payload:" + content);
			HttpResponse response = this.client.execute(httppost);
			HttpEntity entity = response.getEntity();

			if (entity != null) {
				status = response.getStatusLine().getStatusCode();
			}

			EntityUtils.consume(entity);
		} catch (ClientProtocolException e) {
			throw new ServiceNotAvailableException (e);
		} catch (IOException e) {
			throw new ServiceNotAvailableException (e);
		} finally {
			// We don't want to shut down!
			// this.client.getConnectionManager().shutdown();
		}
		return status;
	}
	
	public String postAndGetResponse(String query, String content, String contentType, String auth)
			throws ServiceNotAvailableException, IOException {

		int status = HttpStatus.SC_OK;
		this.client = this.getConnection(query);
		HttpResponse response = null;
		
		// execute the POST
		try {
			HttpPost httppost = new HttpPost(this.url + query);
			StringEntity contentEntity = new StringEntity(content);
			contentEntity.setContentEncoding(HTTP.UTF_8);
			contentEntity.setContentType(contentType);
			if (!auth.equalsIgnoreCase("")) httppost.addHeader("Authorization",auth);
			httppost.setEntity(contentEntity);
			logger.info("Executing POST request:" + httppost.getRequestLine());
			logger.debug("Payload:" + content);
			response = this.client.execute(httppost);
			
			BasicResponseHandler handler = new BasicResponseHandler();
			return handler.handleResponse(response);
			
		} catch (ClientProtocolException e) {
			if (response != null) {
				HttpEntity entity = response.getEntity();
				EntityUtils.consume(entity);
				return null;
			}
			//throw new ServiceNotAvailableException (e);
		} catch (IOException e) {
			if (response != null) {
				HttpEntity entity = response.getEntity();
				EntityUtils.consume(entity);
			}
			throw new ServiceNotAvailableException (e);
		} finally {
			// We don't want to shut down!
			// this.client.getConnectionManager().shutdown();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * eu.dime.ps.communications.services.proxy.ServiceProxy#delete(java.lang
	 * .String)
	 */
	@Override
	public int delete(String query) throws ServiceNotAvailableException {
		int status = HttpStatus.SC_OK;
		this.client = this.getConnection(query);

		// execute the DELETE
		try {
			HttpDelete httpDelete = new HttpDelete(this.url + query);
			System.out.println("Executing DELETE request:" + httpDelete.getRequestLine());
			HttpResponse response = this.client.execute(httpDelete);
			HttpEntity entity = response.getEntity();

			if (entity != null) {
				status = response.getStatusLine().getStatusCode();
			}

			EntityUtils.consume(entity);
		} catch (ClientProtocolException e) {
			throw new ServiceNotAvailableException (e);
		} catch (IOException e) {
			throw new ServiceNotAvailableException (e);
		} finally {
			// We don't want to shut down!
			// this.client.getConnectionManager().shutdown();
		}
		return status;
	}

	/**
	 * Opens a connection to the server
	 * 
	 * @param query
	 *            Part of URL after hostname/port
	 * @return Connection to the server
	 * @throws ServiceNotAvailableException
	 */
	private DefaultHttpClient getConnection(String query)
			throws ServiceNotAvailableException {

		if (this.client == null) {
			this.client = this.createClient();

			if (this.username != null && this.password != null) {
				this.client.getCredentialsProvider().setCredentials(
						new AuthScope(this.url.getHost(), this.serverPort),
						new UsernamePasswordCredentials(this.username,
								this.password));
			}
			return this.client;
		} else {
			return this.client;
		}
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
			sr.register(new Scheme(this.scheme, this.port, ssf));

			return new DefaultHttpClient(ccm, base.getParams());

		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}

	}

	/* (non-Javadoc)
	 * @see eu.dime.ps.communications.services.proxy.ServiceProxy#close()
	 */
	public void close() {
		if (this.client != null && this.client.getConnectionManager() != null) {
			this.client.getConnectionManager().shutdown();
		}
		
	}
}
