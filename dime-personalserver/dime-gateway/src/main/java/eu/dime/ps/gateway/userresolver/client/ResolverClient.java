package eu.dime.ps.gateway.userresolver.client;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

public class ResolverClient {
	
	private class RoundZeroResponse {
		public String id;
		public String nonce;
		public String proofSpec;
		
		@Override
		public String toString() {
			return "RoundZeroResponse [id=" + id + ", nonce=" + nonce
					+ ", proofSpec=" + proofSpec + "]";
		}
		
	}
	
	////////////////////////////////////////////////////////////////////////////
	
	public static final Logger LOG = 
			LoggerFactory.getLogger(ResolverClient.class);
	
	private HttpClient httpClient;
	
	private IdemixClient idemixClient;
	
	private String authEndpoint;
	private String serviceEnpoint;
		
	public ResolverClient(String serviceEndpoint, String authEndpoint, 
			IdemixClient idemixClient) {
		this.serviceEnpoint = serviceEndpoint;
		this.authEndpoint = authEndpoint;
		
		this.idemixClient = idemixClient;
		
		httpClient = new DefaultHttpClient();
	}
	
	public String getToken(String scope, String masterSecret, 
			String credential) throws IOException{
		HttpPost httpPost = new HttpPost(authEndpoint + "/authorize");
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("scope", scope));
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		} catch(UnsupportedEncodingException e) {
			throw new RuntimeException("Unable to set post prameters", e);
		}
		
		RoundZeroResponse roundZeroResponse;
		
		HttpResponse response = httpClient.execute(httpPost);
		HttpEntity entity = response.getEntity();
		if(entity != null) {
			String jsonResponse = IOUtils.toString(entity.getContent());
			roundZeroResponse =
					new Gson().fromJson(jsonResponse, RoundZeroResponse.class);
		} else {
			throw new IOException("Unable to execute auth round0");
		}
		
		LOG.debug("Auth round0 response: {}", roundZeroResponse);
		
		Map<String, String> credentials = new HashMap<String, String>();
		credentials.put("rname", credential);
		String proof = idemixClient.compileProof(roundZeroResponse.nonce, 
				masterSecret, credentials, roundZeroResponse.proofSpec);
		
		LOG.debug("Proof: {}", proof);
		
		httpPost = new HttpPost(
				authEndpoint + "/authorize/" + roundZeroResponse.id);
		nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("proof", proof));
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		} catch(UnsupportedEncodingException e) {
			throw new RuntimeException("Unable to set post prameters", e);
		}
		
		response = httpClient.execute(httpPost);
		entity = response.getEntity();
		
		Map<String, String> map;
		if(entity != null) {
			String jsonResponse = IOUtils.toString(entity.getContent());
			LOG.debug("Auth1 response: {}", jsonResponse);
			map = 
					new Gson().fromJson(jsonResponse, Map.class);
		} else {
			throw new IOException("Unable to execute auth round1");
		}
		
		return map.get("tokenKey");
	}
	
	////////////////////////////////////////////////////////////////////////////
	
	public void search(String token, String name, String surname, 
			String nickname) {
		
		HttpGet httpGet;
		try {
			URIBuilder builder = new URIBuilder(serviceEnpoint + "/search");
			if(name != null)
				builder.setParameter("name", name);
			if(surname != null)
				builder.setParameter("surname", surname);
			if(nickname != null)
				builder.setParameter("nickname", nickname);
			
			httpGet = new HttpGet(builder.build());			
		} catch(URISyntaxException e) {
			throw new RuntimeException(e);
		}
		
		httpGet.setHeader("Authorization", "Bearer " + token);
		try {
			HttpResponse response = httpClient.execute(httpGet);
			HttpEntity entity = response.getEntity();
			if(entity != null) {
				String jsonResponse = IOUtils.toString(entity.getContent());
				LOG.debug("Search response: {}", jsonResponse);
			}
		} catch(IOException e) {
			LOG.debug("Unable to search", e);	
		}
		
	}

	public String register(String token, String name, String surname,
			String nickname, String said) throws IOException{
		HttpPost httpPost = new HttpPost(serviceEnpoint + "/register");
		httpPost.setHeader("Authorization", "Bearer " + token);
		
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("name", name));
		nameValuePairs.add(new BasicNameValuePair("surname", surname));
		nameValuePairs.add(new BasicNameValuePair("nickname", nickname));
		nameValuePairs.add(new BasicNameValuePair("said", said));
		
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		} catch(UnsupportedEncodingException e) {
			LOG.debug("Unable to set post prameters", e);
			throw new RuntimeException("Unable to set post prameters");
		}
		
		HttpResponse response = httpClient.execute(httpPost);
		HttpEntity entity = response.getEntity();
		if(entity != null) {
			String jsonResponse = IOUtils.toString(entity.getContent());
			LOG.debug("Register response: {}", jsonResponse);
			return jsonResponse;
		}
		throw new IOException("Unable to register");
		
	}
	
}
