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

package eu.dime.ps.controllers.account.register;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.log4j.Logger;
import org.ontoware.rdf2go.model.node.URI;
import org.springframework.beans.factory.annotation.Autowired;

import eu.dime.commons.dto.DNSRegister;
import eu.dime.commons.util.HttpUtils;
import eu.dime.commons.util.JaxbJsonSerializer;
import eu.dime.ps.controllers.util.TenantHelper;
import eu.dime.ps.gateway.policy.PolicyManager;
import eu.dime.ps.gateway.service.internal.DimeDNSCannotResolveException;
import eu.dime.ps.gateway.service.internal.DimeDNSException;
import eu.dime.ps.gateway.service.internal.DimeDNSRegisterFailedException;
import eu.dime.ps.gateway.service.internal.DimeIPResolver;
import eu.dime.ps.gateway.service.internal.DimeServiceAdapter;
import eu.dime.ps.semantic.BroadcastManager;
import eu.dime.ps.semantic.BroadcastReceiver;
import eu.dime.ps.semantic.Event;
import eu.dime.ps.semantic.model.dao.Account;
import eu.dime.ps.storage.entities.ServiceAccount;
import eu.dime.ps.storage.entities.Tenant;

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
    private String dns;

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
                	Tenant tenant = TenantHelper.getTenant(event.getTenantId());
                    ServiceAccount sa = ServiceAccount.findAllByTenantAndAccountUri(tenant, account.asURI().toString());
                    if (sa != null) { //if sa == null it is not an own account so no dns register neccessary
                        said = sa.getName();
                        
                        try {
                            String resolvedSaid = new DimeIPResolver().resolveSaid(said);
                        } catch (DimeDNSCannotResolveException ex){
                            try {
                                registerSaid(said);
                            } catch (DimeDNSRegisterFailedException ex1) {
                                logger.error(ex.getMessage(), ex);
                            }
                        } catch (DimeDNSException ex) {
                            logger.error(ex.getMessage(), ex);
                        }
                    }
                }
            } catch (ClassCastException e) {
                logger.warn("Resource of type " + Account.RDFS_CLASS + " cannot be cast to Account", e);
            } 
        }

    }

    public void registerSaid(String said) throws DimeDNSRegisterFailedException {
        String today = "", ip = "", pubKey = "";

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

        //FIXME: double check fails even though registering was successful
        //TODO: have the register call on the di.me dns blocking until the dns entry was set



    }

    public static void sendRegisterPost(String payloadString, String dns) throws DimeDNSRegisterFailedException {

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
            logger.error("Unable to register at" + dnsRegisterUrl + "\n" + ex.getMessage(), ex);
            throw new DimeDNSRegisterFailedException("Unable to register at" + dnsRegisterUrl + "\n" + ex.getMessage(), ex);
        } catch (UnsupportedEncodingException ex) {
            logger.error("Unable to register at" + dnsRegisterUrl + "\n" + ex.getMessage(), ex);
            throw new DimeDNSRegisterFailedException("Unable to register at" + dnsRegisterUrl + "\n" + ex.getMessage(), ex);
        } catch (IOException ex) {
            logger.error("Unable to register at" + dnsRegisterUrl + "\n" + ex.getMessage(), ex);
            throw new DimeDNSRegisterFailedException("Unable to register at" + dnsRegisterUrl + "\n" + ex.getMessage(), ex);
        } finally {
            httpClient.getConnectionManager().shutdown();
        }

    }
}
