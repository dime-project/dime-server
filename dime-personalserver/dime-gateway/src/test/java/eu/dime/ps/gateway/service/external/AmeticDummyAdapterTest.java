package eu.dime.ps.gateway.service.external;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collection;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eu.dime.ps.gateway.exception.AttributeNotSupportedException;
import eu.dime.ps.gateway.exception.InvalidLoginException;
import eu.dime.ps.gateway.exception.ServiceNotAvailableException;
import eu.dime.ps.gateway.service.ServiceResponse;
import eu.dime.ps.gateway.service.external.AMETICDummyAdapter;
import eu.dime.ps.gateway.transformer.Transformer;
import eu.dime.ps.semantic.model.ncal.Event;
import eu.dime.ps.semantic.model.nco.PersonContact;

/**
 * @author Sophie.Wrobel
 *
 */
@Ignore // FIXME fix and enable this test
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:spring-config/adapter-tests-context.xml"})
public class AmeticDummyAdapterTest {
    
    @Autowired
    private Transformer transformer;

	/**
	 * Test method for {@link eu.dime.ps.controllers.service.ametic.AMETICDummyAdapter#getAdapterName()}.
	 */
	@Test
	public void testGetAdapterName() {
			AMETICDummyAdapter adapter;
			try {
				adapter = createAdapter();
				assert (adapter.getAdapterName().equals("AMETICDummyAdapter"));
			} catch (ServiceNotAvailableException e) {
				e.printStackTrace();
				fail ("Service not available: "+e.getMessage());
			}
	}

	/**
	 * Test method for {@link eu.dime.ps.controllers.service.ametic.AMETICDummyAdapter#get(java.lang.String)}.
	 */
	@Test
	public void testGet() {
		try {
			AMETICDummyAdapter adapter = createAdapter();
                        Collection<PersonContact> attendees = adapter.get("/event/@me/173/@all", PersonContact.class);
			assertTrue (attendees.size() > 0);	                        
                        
                        Collection<Event> eventDetails = adapter.get("/event/@me/173", Event.class);
			assertTrue (eventDetails.size() > 0);                       		
                        
                        Collection<Event> allEvents = adapter.get("/event/@all", Event.class);
			assertTrue (allEvents.size() > 0);
		} catch (ServiceNotAvailableException e) {
			e.printStackTrace();
			fail ("Service not available: "+e.getMessage());
		} catch (AttributeNotSupportedException e) {
			e.printStackTrace();
			fail ("Attribute not supported: "+e.getMessage());
		} catch (InvalidLoginException e) {
			e.printStackTrace();
			fail("Invalid credentials: " + e.getMessage());
		}
	}
	
	/**
	 * Test method for {@link eu.dime.ps.controllers.service.ametic.AMETICDummyAdapter#getRaw(java.lang.String)}.
	 */
	@Test
	public void testGetRaw() throws Exception {
		AMETICDummyAdapter adapter = createAdapter();
		ServiceResponse[] attendees = adapter.getRaw("/event/@me/173/@all");
		assertTrue (attendees[0].getResponse().length() > 0);

		ServiceResponse[] eventDetails = adapter.getRaw("/event/@me/173");
		assertTrue (eventDetails[0].getResponse().length() > 0);
                    
		ServiceResponse[] allEvents = adapter.getRaw("/event/@all");
		assertTrue (allEvents[0].getResponse().length() > 0);
	}

	/**
	 * Test method for {@link eu.dime.ps.controllers.service.ametic.AMETICDummyAdapter#register(java.lang.String,java.lang.String)}.
	 */
	@Test
	public void testRegister() throws Exception {
		AMETICDummyAdapter adapter = createAdapter();
		
		// Set credentials
		adapter.register("ameticadmin", "ameticpass");
		
		// Make sure we can retrieve info still
		ServiceResponse[] attendees = adapter.getRaw("/event/@me/173/@all");
		assertTrue (attendees[0].getResponse().length() > 0);
	}
        
        
    private AMETICDummyAdapter createAdapter() throws ServiceNotAvailableException {
        AMETICDummyAdapter adapter = new AMETICDummyAdapter();
        adapter.setTransformer(transformer);

        return adapter;
    }

}
