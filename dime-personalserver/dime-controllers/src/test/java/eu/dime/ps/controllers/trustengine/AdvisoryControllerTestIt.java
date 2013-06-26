package eu.dime.ps.controllers.trustengine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import eu.dime.commons.dto.GroupDistanceWarning;
import eu.dime.commons.dto.Warning;
import eu.dime.ps.controllers.TenantContextHolder;
import eu.dime.ps.controllers.trustengine.impl.AdvisoryController;
import eu.dime.ps.semantic.model.nco.PersonContact;
import eu.dime.ps.semantic.model.nfo.FileDataObject;
import eu.dime.ps.semantic.model.pimo.Person;
import eu.dime.ps.semantic.model.pimo.PersonGroup;

public class AdvisoryControllerTestIt extends AbstractTrustEngineTest{
	
	private static final Logger logger = Logger.getLogger(AdvisoryControllerTestIt.class);

	@Autowired
	private AdvisoryController advisoryController;
		
	private Person anna;
	private Person bob;
	private Person claire;
	private Person dave;
	
	private PersonGroup groupA;
	private PersonGroup groupB;
	
	private FileDataObject file1;
	private FileDataObject file2;
	
	@BeforeClass
	public static void setUpClass() throws Exception {
		// disabling org.openrdf.rdf2go.RepositoryModel warnings
		org.apache.log4j.Logger.getLogger("org.openrdf.rdf2go").setLevel(org.apache.log4j.Level.OFF);        
		java.util.logging.Logger.getLogger("org.openrdf.rdf2go").setLevel(java.util.logging.Level.OFF);
	}

	@Before
	public void setup() throws Exception{
		// create test Persons in infosphere
		TenantContextHolder.setTenant(Long.parseLong(connection.getName()));

		anna = buildPerson("Anna", 0.8);
		bob = buildPerson("Bob", 0.1);
		claire = buildPerson("Claire", 0.2);
		dave = buildPerson("Dave", 0.4);
		
		personManager.add(anna);
		personManager.add(bob);
		personManager.add(claire);
		personManager.add(dave);
		
		groupA = buildGroup("A");
		groupB = buildGroup("B");
		
		groupA.addMember(anna);
		groupA.addMember(bob);
		groupB.addMember(anna);
		groupB.addMember(claire);
		groupB.addMember(dave);
		
		file1 = buildFile("test-file1", 0.3);
		file2 = buildFile("test-file2", 0.9);
		
		file1.addSharedWith(anna);
		
		groupB.addIsRelated(file2);
		file2.addIsRelated(groupB);
		
		groupA.addIsRelated(file1);
		file1.addIsRelated(groupA);
		
		personGroupManager.add(groupA);
		personGroupManager.add(groupB);
		
		fileManager.add(file1);
		fileManager.add(file2);
		
		//debugging infos
//		logger.info("URIS:");
//		logger.info("Anna: "+anna.asURI());
//		logger.info("Bob: "+bob.asURI());
//		logger.info("Claire: "+claire.asURI());
//		logger.info("Dave: "+dave.asURI());
//		
//		logger.info("Group A: "+groupA.asURI());
//		logger.info("Group B: "+groupB.asURI());
//		logger.info("File 1: "+file1.asURI());
//		logger.info("File 2: "+file2.asURI());
	}
	
	@After
	public void teardown(){
		TenantContextHolder.clear();
	}
	
	@Ignore // FIXME: not everything is of type pimo:Thing
	@Test
	public void testGetGroupAdvisory() throws Exception {
		List<String> agentIDs = new ArrayList<String>();
		List<String> sharedThingIDs = new ArrayList<String>();
		agentIDs.add(bob.toString());
		agentIDs.add(dave.toString());
		sharedThingIDs.add(file1.asURI().toString());
		Collection<GroupDistanceWarning> warnings = 
				advisoryController.getGroupWarnings(agentIDs, sharedThingIDs);
		Assert.assertFalse(warnings.isEmpty());
//		for (GroupDistanceWarning warning : warnings){
//			logger.info(warning.toString());
//		}
	}
	
	@Ignore // FIXME: not everything is of type pimo:Thing
	@Test
	public void testGetAdvisory() throws Exception {
		List<String> agentIDs = new ArrayList<String>();
		List<String> sharedThingIDs = new ArrayList<String>();
		Collection<Warning> warnings = null;
		PersonContact profile = buildProfile("Anna A.", "annie");
		this.connection.getPimoService().createOrUpdate(profile);
//		agentIDs.add(anna.asURI().toString());
//		agentIDs.add(bob.asURI().toString());
		agentIDs.add(groupB.toString());
		//agentIDs.add(dave.asURI().toString());
		sharedThingIDs.add(file1.asURI().toString());
		//sharedThingIDs.add(file2.asURI().toString());
		
		warnings = advisoryController.getAdvisory(agentIDs, sharedThingIDs, profile.asURI().toString());

		Assert.assertNotNull(warnings);	
//		for (Warning warning : warnings){
//			logger.info(warning+"\n");
//		}
	}

}
