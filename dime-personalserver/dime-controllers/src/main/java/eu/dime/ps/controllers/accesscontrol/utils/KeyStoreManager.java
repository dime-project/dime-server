package eu.dime.ps.controllers.accesscontrol.utils;

import eu.dime.ps.gateway.policy.PolicyManager;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Properties;

import javax.net.ssl.KeyManagerFactory;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.support.PropertiesLoaderUtils;

/**
 * Class for key generation and management. Used eg for DNS registration
 * 
 * @author marcel
 *
 */
public class KeyStoreManager {

	Logger logger = Logger.getLogger(getClass());
	
	KeyPair kp;
	PrivateKey kPriv ;
	PublicKey kPub;
	
	Signature dsaSig;
	
	KeyStore keystore;
	KeyManagerFactory kmf;
	@Autowired
	PolicyManager policyManager;
	
	final String DEFAULT_KEYSTORE_PATH = System.getProperty("user.home") + "/dime-server-ssl.keystore";
	
	public KeyStoreManager(){
		String alternateKeystore = "";
		try {
			alternateKeystore = policyManager.getPolicyString("DIME_KEYSTORE", null);
			keystore = KeyStore.getInstance("jks");
			kmf = KeyManagerFactory.getInstance("SunX509");
			keystore.load(new FileInputStream(DEFAULT_KEYSTORE_PATH), 
					"dimepass".toCharArray());
		} catch (NoSuchAlgorithmException e) {
			logger.warn(e);
		} catch (CertificateException e) {
			logger.warn(e);
		} catch (FileNotFoundException e) {
			
			try {
				keystore.load(new FileInputStream(alternateKeystore), 
						"dimepass".toCharArray());
			} catch (NoSuchAlgorithmException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (CertificateException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * shortcut (aka segovia hack) to get pk from default ssl keystore
	 * @return
	 * @throws UnrecoverableKeyException
	 * @throws KeyStoreException
	 * @throws NoSuchAlgorithmException
	 * @throws CertificateException
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public String getDefaultPublicKey() throws UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException, CertificateException, FileNotFoundException, IOException{
		return this.getPublicKey("jbcpserver", "dimepass");
	}
	
	public String getPublicKey(String alias, String pass) throws UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException, CertificateException, FileNotFoundException, IOException{
		try {
			kmf.init(keystore, pass.toCharArray());
			
		    Certificate cert = keystore.getCertificate(alias);
		    byte[] key = cert.getPublicKey().getEncoded();
		    return Base64.encodeBase64String(key);
		    //return cert.getPublicKey().toString();
		} catch (Exception e) {
			logger.warn(e);
			return "dummy-key";
		}
		
	}
	public void generateKeyPair(String alias){
		try {	
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DSA", "SUN");
			
			SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
			//random.setSeed(seed);
			keyGen.initialize(1024, random);
			
			kp = keyGen.generateKeyPair();
			kPriv = kp.getPrivate();
			kPub = kp.getPublic();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchProviderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	// --------- methods for signing stuff.. UNDER CONSTRUCTION
	
	public void signData(String filename) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, SignatureException, IOException{
		dsaSig = Signature.getInstance("SHA1withDSA", "SUN");
		dsaSig.initSign(kPriv);
		
		FileInputStream fis = new FileInputStream(filename);
		BufferedInputStream bufin = new BufferedInputStream(fis);
		
		byte[] buffer = new byte[1024];
		int len;
		while ((len = bufin.read(buffer))>= 0){
			dsaSig.update(buffer, 0, len);
		}
		bufin.close();
			
	}
	
	public void saveSig(String filename) throws IOException, SignatureException{
		byte[] sig = dsaSig.sign();
		FileOutputStream fos = new FileOutputStream(filename);
		fos.write(sig);
		fos.close();
	}
	
	public void savePubKey(String filename) throws IOException{
		byte [] key = kPub.getEncoded();
		FileOutputStream kfos = new FileOutputStream(filename);
		kfos.write(key);
		kfos.close();
	}
	
	public void savePrivKey(String filename) throws IOException{
		byte [] key = kPriv.getEncoded();
		FileOutputStream kfos = new FileOutputStream(filename);
		kfos.write(key);
		kfos.close();
	}
	
	public void verifySig(String kpub, String signame, String filename) throws IOException, InvalidKeySpecException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, SignatureException{
		FileInputStream kfis = new FileInputStream(kpub);
		byte[] encKey = new byte[kfis.available()];
		kfis.read(encKey);
		kfis.close();
		
		X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(encKey);
		
		KeyFactory factory = KeyFactory.getInstance("DSA", "SUN");
		PublicKey pubkey = factory.generatePublic(pubKeySpec);
		
		FileInputStream fis = new FileInputStream(signame);
		byte[] sigbuf = new byte[fis.available()];
		fis.read(sigbuf);
		fis.close();
		
		Signature sig = Signature.getInstance("SHAwithDSA", "SUN");
		sig.initVerify(pubkey);
		
		FileInputStream datafis = new FileInputStream(filename);
		BufferedInputStream bufin = new BufferedInputStream(datafis);
		
		byte[] buffer = new byte[1024];
		int len;
		while ((len = bufin.read(buffer))>= 0){
			sig.update(buffer, 0, len);
		}
		bufin.close();
		
		boolean verifies = sig.verify(sigbuf);
		
		System.out.println("signature verifies: " + verifies);
			
	}


}
