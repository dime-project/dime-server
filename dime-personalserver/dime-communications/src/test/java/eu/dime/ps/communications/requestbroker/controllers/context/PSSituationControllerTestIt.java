package eu.dime.ps.communications.requestbroker.controllers.context;

import static org.mockito.Mockito.when;

import java.util.Collection;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ontoware.rdf2go.model.node.impl.DatatypeLiteralImpl;
import org.ontoware.rdf2go.vocabulary.XSD;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eu.dime.commons.dto.Data;
import eu.dime.commons.dto.Message;
import eu.dime.commons.dto.Request;
import eu.dime.commons.dto.Response;
import eu.dime.ps.controllers.TenantContextHolder;
import eu.dime.ps.controllers.context.LiveContextManager;
import eu.dime.ps.controllers.infosphere.manager.SituationManager;
import eu.dime.ps.dto.SituationDTO;
import eu.dime.ps.semantic.connection.Connection;
import eu.dime.ps.semantic.connection.ConnectionProvider;
import eu.dime.ps.semantic.exception.ResourceExistsException;
import eu.dime.ps.semantic.model.ModelFactory;
import eu.dime.ps.semantic.model.dcon.Situation;
import eu.dime.ps.semantic.model.pimo.Person;
import eu.dime.ps.semantic.service.impl.PimoService;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring-config/controllers-tests-context.xml")
public class PSSituationControllerTestIt extends Assert{

	private static final String SAID = "juan";

	protected ModelFactory modelFactory = new ModelFactory();
	
	@Autowired
	protected Connection connection;
	
	@Autowired
	protected PimoService pimoService;
	
	@Autowired
	protected ConnectionProvider connectionProvider;
	
	@Autowired
	private SituationManager situationManager;

	@Autowired
	private LiveContextManager liveContextManager;	
	
	private PSSituationController controller;
	
	
	
	@Before
	public void setUp() throws Exception {
		
		// set up tenant data in the thread local holders
    	TenantContextHolder.setTenant(Long.parseLong(connection.getName()));
    	
		// mocking connection provider
		when(connectionProvider.getConnection(connection.getName())).thenReturn(connection);
		
		// set up PSResourcesController
		controller = new PSSituationController();		
		controller.setSituationManager(situationManager);		
		controller.setLiveContextManager(liveContextManager);
	}
			
	@After
	public void tearDown() throws Exception {
					
		Collection<Situation> Situations = situationManager.getAll();
		for (Situation Situation: Situations){
			situationManager.remove(Situation.asURI().toString());				
			}
		
		TenantContextHolder.clear();
		
	}
		
	
	
	@Test
	public void testGetSituation() throws Exception {
		Person creator = createPerson("John Doe");
		Situation situation = createSituation(creator);		
		situationManager.add(situation);
		situationManager.activate(situation.asURI().toString());
		Response<SituationDTO> response = controller.getMeAll(SAID);
		assertNotNull(response);
		assertEquals(1, response.getMessage().getData().getEntries().size());
		
		SituationDTO resource =  response.getMessage().getData().getEntries().iterator().next();
		
		assertEquals(resource.get("guid"),situation.asURI().toString());
		assertEquals(resource.get("name"),"test situation");		
		assertEquals(resource.get("userId"),creator.asURI().toString());
		assertEquals(resource.get("type"),"situation" );
		//assertEquals(resource.get("created"),1338824999L);
		//assertEquals(resource.get("lastModified"),1338824999L);		
		assertEquals(resource.get("active"),true);
	}
	
	
	@Test
	public void testDeActivateSituationWellFormedJSON() throws Exception {
		Person creator = createPerson("John Doe");
		Situation situation = createSituation(creator);
		situationManager.add(situation);
		situationManager.activate(situation.asURI().toString());
		
		Request<SituationDTO> request = buildSituationRequest(situation.asURI().toString(), false, "test situation name changed");
		
		Response<SituationDTO> response = controller.updateSituation(SAID, request, situation.asURI().toString());
		
		assertNotNull(response);
		assertEquals(1, response.getMessage().getData().getEntries().size());
		
		SituationDTO resource =  response.getMessage().getData().getEntries().iterator().next();
		
		assertEquals(resource.get("guid"),situation.asURI().toString());
		assertEquals(resource.get("name"),"test situation name changed");		
		assertEquals(resource.get("userId"),"@me");
		assertEquals(resource.get("type"),"situation" );
		assertEquals(resource.get("active"),false);	
		
	}
	
	@Test
	public void testActivateSituationWellFormedJSON() throws Exception {
		Person creator = createPerson("John Doe");
		Situation situation = createSituation(creator);
		situationManager.add(situation);		
		
		Request<SituationDTO> request = buildSituationRequest(situation.asURI().toString(), true, "test situation name changed");
		
		Response<SituationDTO> response = controller.updateSituation(SAID, request, situation.asURI().toString());
		
		assertNotNull(response);
		assertEquals(1, response.getMessage().getData().getEntries().size());
		
		SituationDTO resource =  response.getMessage().getData().getEntries().iterator().next();
		
		assertEquals(resource.get("guid"),situation.asURI().toString());
		assertEquals(resource.get("name"),"test situation name changed");		
		assertEquals(resource.get("userId"),"@me");
		assertEquals(resource.get("type"),"situation" );
		assertEquals(resource.get("active"),true);	
		
	}
	
	@Test
	public void testDeActivateActiveSituationWellFormedJSON() throws Exception {
		Person creator = createPerson("John Doe");
		Situation situation = createSituation(creator);
		situationManager.add(situation);
		situationManager.activate(situation.asURI().toString());
		
		Request<SituationDTO> request = buildSituationRequest(situation.asURI().toString(), true, "test situation name changed");
		
		Response<SituationDTO> response = controller.updateSituation(SAID, request, situation.asURI().toString());
		
		assertNotNull(response);
		assertEquals(1, response.getMessage().getData().getEntries().size());
		
		SituationDTO resource =  response.getMessage().getData().getEntries().iterator().next();
		
		assertEquals(resource.get("guid"),situation.asURI().toString());
		assertEquals(resource.get("name"),"test situation name changed");		
		assertEquals(resource.get("userId"),"@me");
		assertEquals(resource.get("type"),"situation" );
		assertEquals(resource.get("active"),true);	
		
	}
	
	@Test
	public void testDeActivateDeActivedSituationWellFormedJSON() throws Exception {
		Person creator = createPerson("John Doe");
		Situation situation = createSituation(creator);
		situationManager.add(situation);
		situationManager.deactivate(situation.asURI().toString());
		
		Request<SituationDTO> request = buildSituationRequest(situation.asURI().toString(), false, "test situation name changed");
		
		Response<SituationDTO> response = controller.updateSituation(SAID, request, situation.asURI().toString());
		
		assertNotNull(response);
		assertEquals(1, response.getMessage().getData().getEntries().size());
		
		SituationDTO resource =  response.getMessage().getData().getEntries().iterator().next();
		
		assertEquals(resource.get("guid"),situation.asURI().toString());
		assertEquals(resource.get("name"),"test situation name changed");		
		assertEquals(resource.get("userId"),"@me");
		assertEquals(resource.get("type"),"situation" );
		assertEquals(resource.get("active"),false);	
		
	}
	
	
	@Test
	public void testCreateSituationnWellFormedRDF() throws Exception {
				
		Request<SituationDTO> request = buildSituationRequest("dumbGUID", true, "test situation name changed");
		
		Response<SituationDTO> response = controller.createSituation(SAID, request);
		
		assertNotNull(response);
		assertEquals(1, response.getMessage().getData().getEntries().size());		
		
		SituationDTO situationdto = response.getMessage().getData().getEntries().iterator().next();
		Situation situation = situationManager.get(situationdto.get("guid").toString());
	
		assertEquals("test situation name changed",situation.getPrefLabel());
	}
	
	@Test
	public void testCreateAndUpdate() throws Exception {
		String situationId = null;
		Request<SituationDTO> request = null;
		Response<SituationDTO> response = null;
		SituationDTO dto = null;
		
		// call 'create' and assign an id to the situation
		request = buildSituationRequest(null, false, "name 1");
		response = controller.createSituation(SAID, request);
		dto = response.getMessage().getData().getEntries().iterator().next();
		situationId = dto.get("guid").toString();

		// call 'update' twice changing the name and active attributes
		request = buildSituationRequest(situationId, true, "name 2");
		controller.updateSituation(SAID, request, situationId);
		request = buildSituationRequest(situationId, false, "name 3");
		response = controller.updateSituation(SAID, request, situationId);
		
		assertNotNull(response);
		assertEquals(1, response.getMessage().getData().getEntries().size());
		
		dto =  response.getMessage().getData().getEntries().iterator().next();
		assertEquals(situationId, dto.get("guid"));
		assertEquals("name 3", dto.get("name"));		
		assertEquals("@me", dto.get("userId"));
		assertEquals("situation", dto.get("type"));
		assertEquals(false, dto.get("active"));	
	}

	private Request<SituationDTO> buildSituationRequest(String situationId, boolean active, String label) {
		Request<SituationDTO> request = new Request<SituationDTO>();
		Message<SituationDTO> message = new Message<SituationDTO>();
		
		SituationDTO situation = new SituationDTO();
		situation.put("guid", situationId);
		situation.put("lastModified", 1338824999L);
		situation.put("created", 1338824999L);
		situation.put("name", label);
		situation.put("type", "situation");
		situation.put("active", active);
		
		Data<SituationDTO> data = new Data<SituationDTO>();
		data.getEntries().add(situation);
		message.setData(data);
		request.setMessage(message);
		
		return request;
	}
		
	private Situation createSituation(Person creator) throws ResourceExistsException {
		//create a situation
		Situation situation = modelFactory.getDCONFactory().createSituation();
		situation.setPrefLabel("test situation");
		situation.setCreator(creator);
		situation.setCreated(new DatatypeLiteralImpl("1970-01-16T11:53:44.999Z", XSD._dateTime));
		situation.setLastModified(new DatatypeLiteralImpl("1970-01-16T11:53:44.999Z", XSD._dateTime));	
						
		return situation;
	}
	
	protected Person createPerson(String name) throws ResourceExistsException  {
		Person person = modelFactory.getPIMOFactory().createPerson();
		person.setPrefLabel(name);
		pimoService.create(person);
		return person;
	}
	
}
