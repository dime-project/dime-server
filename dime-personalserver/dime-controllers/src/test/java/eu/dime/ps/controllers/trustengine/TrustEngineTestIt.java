package eu.dime.ps.controllers.trustengine;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eu.dime.commons.dto.TrustWarning;
import eu.dime.ps.controllers.infosphere.manager.FileManager;
import eu.dime.ps.controllers.infosphere.manager.PersonGroupManagerImpl;
import eu.dime.ps.controllers.infosphere.manager.PersonManager;
import eu.dime.ps.semantic.connection.Connection;
import eu.dime.ps.semantic.model.ModelFactory;
import eu.dime.ps.semantic.model.nfo.FileDataObject;
import eu.dime.ps.semantic.model.pimo.Person;
import eu.dime.ps.semantic.model.pimo.PersonGroup;

public class TrustEngineTestIt extends AbstractTrustEngineTest {

	//FIXME: write more tests
	
	@Autowired
	TrustEngine trustEngine;
	
	@Autowired
	private PersonManager personManager;
	
	@Autowired
	private PersonGroupManagerImpl personGroupManager;
	
	@Autowired
	private Connection connection;
	
	@Autowired
	private FileManager fileManager;
	
	private ModelFactory modelFactory = new ModelFactory();
	
	@Test
	public void testGetRecommendation(){
		List <String> agents = new ArrayList<String>();
		List<String> things = new ArrayList<String>();
		
		List<TrustWarning> warnings = trustEngine.getRecommendation(agents, things);
		
		assertNotNull(warnings);
		}
	
}
