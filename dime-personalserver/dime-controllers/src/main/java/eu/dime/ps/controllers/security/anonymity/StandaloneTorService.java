package eu.dime.ps.controllers.security.anonymity;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PreDestroy;
import javax.persistence.PersistenceException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.NameFileFilter;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

/**
 * Creates Tor hidden services
 * !!! IMPORTANT: needs Tor installed on hostsystem !!!
 * @author philipp 
 *
 */
@Service
public class StandaloneTorService {
	private static final Logger logger = Logger.getLogger(StandaloneTorService.class);
	
	//"HiddenServiceDir /var/lib/tor/hidden_service/ \n" +
	//"HiddenServicePort 80 127.0.0.1:80 \n";
	private static final String hiddenServiceTemplate = 
			"HiddenServiceDir $0 \n" +
			"HiddenServicePort $1 $2 \n";
	
	private static final String torrcTemplate = 
			"SocksPort 9050 \n" +
			"SocksListenAddress 127.0.0.1 \n" +
			"RunAsDaemon 0 \n";
	
	private static final String torPath = "/usr/sbin/tor -f ";
	
	private String torFolder;
	private ArrayList<String> hiddenServices = new ArrayList<String>();
	private ArrayList<String> onionAddresses = new ArrayList<String>();
	
	private Process torProcess;
		
	public StandaloneTorService() throws Exception{
		//TODO: Refactor! file system access not from this layer
    	String dimeDir = System.getProperty("dime.appdata.basedir");
    	String homeDir = System.getProperty("user.home");
    	if (dimeDir == null){
        	if (homeDir == null){
        		throw new PersistenceException("There is no folder specified to store the Tor service configuration file. " +
        				"di.me needs either one of the system properties <dime.appdata.basedir> or <user.home> to be set .");
        	}
    		dimeDir = homeDir + File.separator + ".dime";
    	} 
    	
		torFolder = dimeDir + File.separator + "torhs" + File.separator;
		
		createFolder(torFolder);
		
		scannForHiddenServices();
		rebuildTorrc();
	}
	
	@PreDestroy
	public void destroy() {
		logger.info("Beans destroy");
		stopTor();
	}
	
	public void refresh() throws Exception{
		scannForHiddenServices();
	}
	
	public void startTor() {
		try {
			String processString = torPath + torFolder + "torrc";
			logger.info("Tor process start path: " + processString);
			
			torProcess = new ProcessBuilder("tor", "-f", torFolder + "torrc").start();
			
		} catch (IOException e) {
			logger.error("Could not start tor: "  + e.getMessage());
		}
	}
	
	public void stopTor() {
		logger.info("Stopping Tor");
		
		if(torProcess != null) {
			torProcess.destroy();
			logger.info("Tor stopped");
		}
	}
	
	public void addHiddenService(String name) throws Exception{
		logger.info("Adding hidden service " + name);
		
		hiddenServices.add(name);
		
		rebuildTorrc();
		stopTor();
		startTor();
	}
	
	public void removeHiddenService(String name) {
		hiddenServices.remove(name);
		rebuildTorrc();
	}
	
	public List<String> getOnionAdresses() {
		return onionAddresses;
	}
	
	//////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////
	
	private void rebuildTorrc() {
		String torrc = torrcTemplate;
		
		for(String hiddenService : hiddenServices) {
			torrc = torrc 
					+ hiddenServiceTemplate.replace("$0", torFolder + hiddenService)
					.replace("$1", "80")
					.replace("$2", "127.0.0.1:8080");
		}
				
		try {
			FileUtils.write(new File(torFolder + "torrc"), torrc);
			logger.info("Rebuild torrc file: " + torrc);
		} catch (IOException e) {
			logger.error("Could not write torrc");
		}
	}
		
	private void createFolder(String folderName) {
		File directory = new File(folderName);
		if(!directory.exists()) {
			logger.info(folderName + " not exists. Creating");
			try {
				directory.mkdirs();	
			} catch (SecurityException e){
				throw new RuntimeException("Could not create " + folderName);
			}
		}
	}
	
	private void scannForHiddenServices() throws Exception{
		logger.info("Scanning for hidden services in " + torFolder);
		
		onionAddresses.clear();
		
		for(File dir : 
			new File(torFolder).listFiles((FileFilter) DirectoryFileFilter.DIRECTORY)) {
			
			logger.info("Found HiddenService directory: " + dir.getAbsoluteFile());
			hiddenServices.add(dir.getName());
			
			for(File hostname : 
				dir.listFiles((FilenameFilter) new NameFileFilter("hostname"))) {
				
				String onionAddress = FileUtils.readFileToString(hostname);
				logger.info("Found Onion-Adress: " + onionAddress);
				onionAddresses.add(onionAddress);
			}
		}
	}
}
