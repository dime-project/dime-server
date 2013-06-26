package eu.dime.ps.communications.requestbroker.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import sun.misc.BASE64Encoder;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.multipart.FormDataMultiPart;

@Ignore
public class PSResourcesControllerTestIt {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testUploadMultipartFile() throws FileNotFoundException {

	String fileName = "src/test/resources/files/fp7.png";
	String protocol = "http://";
	String host = "localhost:8080/";
	String testedURL = protocol + host + "dime-communications/api/ps/infosphere/crawler"; //resources/upload/multipart";
	String username = "owner";
	String password = "dimepass4owner";

	File file = new File(fileName);
	InputStream stream = new FileInputStream(file);
	FormDataMultiPart part = new FormDataMultiPart().field("file", stream,
		MediaType.TEXT_PLAIN_TYPE);

	com.sun.jersey.api.client.config.ClientConfig config = new com.sun.jersey.api.client.config.DefaultClientConfig(); // SSL
															   // configuration
	// SSL configuration
	config.getProperties().put(
		com.sun.jersey.client.urlconnection.HTTPSProperties.PROPERTY_HTTPS_PROPERTIES,
		new com.sun.jersey.client.urlconnection.HTTPSProperties(getHostnameVerifier(),
			getSSLContext()));
	Client client = Client.create(config);
	client.addFilter(new com.sun.jersey.api.client.filter.HTTPBasicAuthFilter(username,
		password));
	
	WebResource webResource = client.resource(testedURL);
	
	String response = webResource.type(MediaType.MULTIPART_FORM_DATA).post(String.class, part);

	Assert.assertEquals("", response);

    }

    private HostnameVerifier getHostnameVerifier() {
	return new HostnameVerifier() {

	    @Override
	    public boolean verify(String hostname, javax.net.ssl.SSLSession sslSession) {
		return true;
	    }
	};
    }

    private SSLContext getSSLContext() {
	javax.net.ssl.TrustManager x509 = new javax.net.ssl.X509TrustManager() {

	    @Override
	    public void checkClientTrusted(java.security.cert.X509Certificate[] arg0, String arg1)
		    throws java.security.cert.CertificateException {
		return;
	    }

	    @Override
	    public void checkServerTrusted(java.security.cert.X509Certificate[] arg0, String arg1)
		    throws java.security.cert.CertificateException {
		return;
	    }

	    @Override
	    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
		return null;
	    }
	};
	SSLContext ctx = null;
	try {
	    ctx = SSLContext.getInstance("SSL");
	    ctx.init(null, new javax.net.ssl.TrustManager[] { x509 }, null);
	} catch (java.security.GeneralSecurityException ex) {
	}
	return ctx;
    }

    @SuppressWarnings("restriction")
    public static String encode(String source) {
	BASE64Encoder enc = new sun.misc.BASE64Encoder();
	return (enc.encode(source.getBytes()));
    }

    public class MyHostnameVerifier implements HostnameVerifier {

	@Override
	public boolean verify(String arg0, SSLSession arg1) {
	    return true;
	}

    }

    private static class TestCustomTrustManager implements X509TrustManager {

	@Override
	public void checkClientTrusted(X509Certificate[] arg0, String arg1)
		throws CertificateException {
	}

	@Override
	public void checkServerTrusted(X509Certificate[] arg0, String arg1)
		throws CertificateException {
	}

	@Override
	public X509Certificate[] getAcceptedIssuers() {
	    return null;
	}
    }

    public class TestHttpsClient extends DefaultHttpClient {
	final Context context;

	public TestHttpsClient(Context context) {
	    this.context = context;
	}

	@Override
	protected ClientConnectionManager createClientConnectionManager() {
	    SchemeRegistry registry = new SchemeRegistry();
	    registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
	    registry.register(new Scheme("https", newSslSocketFactory(), 443));
	    return new SingleClientConnManager(getParams(), registry);
	}

	private SSLSocketFactory newSslSocketFactory() {
	    try {
		TrustManager tm = new TestCustomTrustManager();
		SSLContext ctx = SSLContext.getInstance("TLS");
		ctx.init(null, new TrustManager[] { tm }, null);
		SSLSocketFactory sf = new SSLSocketFactory(ctx, new X509HostnameVerifier() {

		    @Override
		    public boolean verify(String hostname, SSLSession session) {
			// TODO Auto-generated method stub
			return true;
		    }

		    @Override
		    public void verify(String host, String[] cns, String[] subjectAlts)
			    throws SSLException {
			// TODO Auto-generated method stub

		    }

		    @Override
		    public void verify(String host, X509Certificate cert) throws SSLException {
			// TODO Auto-generated method stub

		    }

		    @Override
		    public void verify(String host, SSLSocket ssl) throws IOException {
			// TODO Auto-generated method stub

		    }
		});
		return sf;
	    } catch (Exception e) {
		throw new Error(e);
	    }
	}
    }
}
