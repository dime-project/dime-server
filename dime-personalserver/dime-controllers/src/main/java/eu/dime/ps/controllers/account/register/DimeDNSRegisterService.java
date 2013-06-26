package eu.dime.ps.controllers.account.register;

import java.io.FileNotFoundException;

import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.List;
import java.util.Properties;

import javax.naming.NamingException;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;
import org.ontoware.rdf2go.model.node.URI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import eu.dime.commons.dto.DNSRegister;
import eu.dime.commons.util.HttpUtils;
import eu.dime.commons.util.JaxbJsonSerializer;
import eu.dime.ps.controllers.accesscontrol.utils.KeyStoreManager;
import eu.dime.ps.gateway.policy.PolicyManager;
import eu.dime.ps.gateway.service.internal.DimeServiceAdapter;
import eu.dime.ps.gateway.util.DnsResolver;
import eu.dime.ps.semantic.BroadcastManager;
import eu.dime.ps.semantic.BroadcastReceiver;
import eu.dime.ps.semantic.Event;
import eu.dime.ps.semantic.model.dao.Account;
import eu.dime.ps.storage.entities.ServiceAccount;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import org.apache.http.client.ClientProtocolException;

/**
 * this class listens for the account created-event, checks is said is already
 * registered, and if not registers at the registry.
 *
 * @author marcel
 *
 */
public class DimeDNSRegisterService implements BroadcastReceiver {

    static Logger logger = Logger.getLogger(DimeDNSRegisterService.class);
    @Autowired
    protected PolicyManager policyManager;
    String dns;

    public DimeDNSRegisterService() {
        BroadcastManager.getInstance().registerReceiver(this);
    }

    @Override
    public void onReceive(Event event) {

        List<URI> typeList = event.getTypes();
        boolean accountCreated = false;
        for (URI uri : typeList) {
            if (uri.equals(Account.RDFS_CLASS)) {
                accountCreated = true;
                break;
            }
        }
        if (accountCreated && event.getAction().equals(Event.ACTION_RESOURCE_ADD)) {
            String said = null;
            try {
                Account account = (Account) event.getData();

                if (account.getAccountType().equals(DimeServiceAdapter.NAME)) {
                    ServiceAccount sa = ServiceAccount.findAllByAccountUri(account.asURI().toString());
                    if (sa != null) { //if sa == null it is not an own account so no dns register neccessary
                        said = sa.getName();
                        dns = policyManager.getPolicyString("DIME_DNS", null);
                        String resolvedSaid = DnsResolver.resolve(dns, said + ".dns.dime-project.eu");
                        if (resolvedSaid.equals("")) {
                            try {
                                //not resolved = not registered
                                registerSaid(said);
                            } catch (DNSRegisterFailedException ex) {
                                logger.error(ex.getMessage(), ex);
                            }
                        }
                    }
                }
            } catch (ClassCastException e) {
                logger.warn("Resource of type " + Account.RDFS_CLASS + " cannot be cast to Account", e);
            } catch (NamingException e) {
                //name not found
                if (!said.equals("")) {
                    try{
                        registerSaid(said);
                    } catch (DNSRegisterFailedException ex) {
                        logger.error(ex.getMessage(), ex);
                    }
                }
            }

        }

    }

    public void registerSaid(String said) throws DNSRegisterFailedException {
        String today = "", ip = "", pubKey = "", dns;

        ip = policyManager.getPolicyString("IPADDRESS", null);
        dns = policyManager.getPolicyString("DIME_DNS", null);

        today = String.valueOf(System.nanoTime()).substring(0, 6);

        // FIXME: This causes premature termination!
		/*
         * try { pubKey = new KeyStoreManager().getDefaultPublicKey(); } catch
         * (UnrecoverableKeyException e1) { logger.warn("Could not load public
         * key", e1); } catch (KeyStoreException e1) { logger.warn("Could not
         * load public key", e1); } catch (NoSuchAlgorithmException e1) {
         * logger.warn("Could not load public key", e1); } catch
         * (CertificateException e1) { logger.warn("Could not load public key",
         * e1); } catch (FileNotFoundException e1) { logger.warn("Could not load
         * public key", e1); } catch (IOException e1) { logger.warn("Could not
         * load public key", e1); } catch (SecurityException e1) {
         * logger.warn("Could not load public key", e1); }
         */

        DNSRegister payload = new DNSRegister(today, ip, said + ".dns.dime-project.eu", pubKey);
        //"{"changeDate":'+${today}',"content":"'${ip}'","name":"ps'${h}'.dns.dime-project.eu","publickey":"'${pubkey}'"}'


        String payloadString = JaxbJsonSerializer.jsonValue(payload);
        sendRegisterPost(payloadString, dns);

    }

    public static void sendRegisterPost(String payloadString, String dns) throws DNSRegisterFailedException {

        HttpClient httpClient = HttpUtils.createHttpClient();
        String dnsRegisterUrl = "http://" + dns + ":8080/dime_dns_registry/recordses";

        try {

            HttpPost request = new HttpPost(dnsRegisterUrl);
            StringEntity params = new StringEntity(payloadString);
            request.addHeader("Content-type", "application/json");
            request.setEntity(params);
            // FIXME: IllegalstateException is thrown but it was still successful.
            HttpResponse response = httpClient.execute(request);
            //System.out.println(JaxbJsonSerializer.jsonValue(response.getEntity()));

        } catch (ClientProtocolException ex) {
            logger.error(ex.getMessage(), ex);
            throw new DNSRegisterFailedException("Unable to register at" + dnsRegisterUrl + "\n" + ex.getMessage(), ex);
        } catch (UnsupportedEncodingException ex) {
            logger.error(ex.getMessage(), ex);
            throw new DNSRegisterFailedException("Unable to register at" + dnsRegisterUrl + "\n" + ex.getMessage(), ex);
        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
            throw new DNSRegisterFailedException("Unable to register at" + dnsRegisterUrl + "\n" + ex.getMessage(), ex);
        } finally {
            httpClient.getConnectionManager().shutdown();
        }

    }
}
