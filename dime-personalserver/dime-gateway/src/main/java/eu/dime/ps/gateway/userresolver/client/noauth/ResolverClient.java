package eu.dime.ps.gateway.userresolver.client.noauth;
import com.google.gson.Gson;
import eu.dime.ps.gateway.userresolver.client.DimeResolver;
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



/**
 *
 * @author simon
 */
public class ResolverClient implements DimeResolver {
 
	public static final Logger logger = LoggerFactory.getLogger(ResolverClient.class);
	private final HttpClient httpClient;
	private final String serviceEnpoint;

	public ResolverClient(String serviceEndpoint) {
		this.serviceEnpoint = serviceEndpoint;
		httpClient = new DefaultHttpClient();
        logger.info("Created new resolverClient for endpoint: "+serviceEnpoint);
	}


    
    
     
   
    @Override
	public String register(String token, String firstname, String surname, String nickname, String said) throws IOException{
		HttpPost httpPost = new HttpPost(serviceEnpoint + "/register");

        if (token!=null){
            httpPost.setHeader("Authorization", "Bearer " + token);
        }

		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("name", firstname));
		nameValuePairs.add(new BasicNameValuePair("surname", surname));
		nameValuePairs.add(new BasicNameValuePair("nickname", nickname));
		nameValuePairs.add(new BasicNameValuePair("said", said));

        logger.info("register call at: "+serviceEnpoint+" for "+nickname+", "+firstname+", "+surname);


		try {
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		} catch(UnsupportedEncodingException e) {
			logger.debug("Unable to set post prameters", e);
			throw new RuntimeException("Unable to set post prameters");
		}

		HttpResponse response = httpClient.execute(httpPost);
		HttpEntity entity = response.getEntity();
		if(entity != null) {
			String jsonResponse = IOUtils.toString(entity.getContent());
			logger.debug("Register response: {}", jsonResponse);
			return jsonResponse;
		}
		throw new IOException("Unable to register");

	}

}