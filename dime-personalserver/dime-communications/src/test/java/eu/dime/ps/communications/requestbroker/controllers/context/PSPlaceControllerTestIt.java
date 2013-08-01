/*
* Copyright 2013 by the digital.me project (http:\\www.dime-project.eu).
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

package eu.dime.ps.communications.requestbroker.controllers.context;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

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
import eu.dime.commons.dto.Place;
import eu.dime.commons.dto.Request;
import eu.dime.commons.dto.Response;
import eu.dime.ps.controllers.TenantContextHolder;
import eu.dime.ps.controllers.exception.InfosphereException;
import eu.dime.ps.controllers.placeprocessor.PlaceProcessor;
import eu.dime.ps.dto.SituationDTO;
import eu.dime.ps.gateway.exception.ServiceNotAvailableException;
import eu.dime.ps.semantic.connection.Connection;
import eu.dime.ps.semantic.connection.ConnectionProvider;
import eu.dime.ps.semantic.exception.ResourceExistsException;
import eu.dime.ps.semantic.model.ModelFactory;
import eu.dime.ps.semantic.model.dcon.Situation;
import eu.dime.ps.semantic.model.pimo.Person;
import eu.dime.ps.semantic.service.impl.PimoService;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring-config/controllers-tests-context.xml")
public class PSPlaceControllerTestIt extends Assert{

	private static final String SAID = "juan";

	protected ModelFactory modelFactory = new ModelFactory();
	
	@Autowired
	protected Connection connection;
	
	@Autowired
	protected PimoService pimoService;
	
	@Autowired
	protected ConnectionProvider connectionProvider;

	protected PlaceProcessor mockedPlaceProcessor;
	
	private PSPlaceController controller;
	
	
	@Before
	public void setUp() throws Exception {
		
		// set up tenant data in the thread local holders
    	TenantContextHolder.setTenant(Long.parseLong(connection.getName()));
    	
		// mocking connection provider
		when(connectionProvider.getConnection(connection.getName())).thenReturn(connection);
		
		//mocking PlaceProcessor
		
		mockedPlaceProcessor = buildMockPlaceProcessor();
		
		// set up PSPlaceController
		controller = new PSPlaceController();		
		controller.setPlaceProcessor(mockedPlaceProcessor); 
	}
			
	private PlaceProcessor buildMockPlaceProcessor() {
		Place place = new Place();
		place.setGuid("testguid");
		List<Place> places = new ArrayList<Place>();
		places.add(place);
		PlaceProcessor mockedProcessor = mock(PlaceProcessor.class);
				
			try {
				when(mockedProcessor.getPlaces("said", 0, 0, 0, null)).thenReturn(places);
			} catch (ServiceNotAvailableException e) {			
				e.printStackTrace();
			}			
		 
		return mockedProcessor;
		
	}

	@After
	public void tearDown() throws Exception {
									
		TenantContextHolder.clear();
		
	}
			
	
	@Test
	public void testGetAllPlaces() throws Exception {
				
		Response<Place> response = controller.getAllPlacesAround("said", 0, 0, 0, null);
		
		assertNotNull(response);
		assertEquals(1, response.getMessage().getData().getEntries().size());
		
		Place resource =  response.getMessage().getData().getEntries().iterator().next();
		
		assertEquals(resource.getGuid(),"testguid");			
		assertEquals(resource.getUserId(),"@me");		
	}		
	
}
