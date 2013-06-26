package eu.dime.ps.controllers.infosphere;

import java.util.Collection;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import eu.dime.ps.controllers.exception.InfosphereException;
import eu.dime.ps.controllers.infosphere.manager.DeviceManagerImpl;
import eu.dime.ps.semantic.model.ddo.Device;
import eu.dime.ps.semantic.model.pimo.Person;
import eu.dime.ps.semantic.rdf.ResourceStore;

/**
 * Tests {@link DeviceManagerImpl}.
 * 
 * @author Ismael Rivera
 */
public class DeviceManagerTest extends InfoSphereManagerTest {

	@Autowired
	private ResourceStore resourceStore;

	@Autowired
	private DeviceManagerImpl deviceManager;
	
	@Test
	public void testExist() throws Exception {
		Device device = buildDevice("Samsung Nexus S");
		deviceManager.add(device);
		assertTrue(deviceManager.exist(device.toString()));
	}
	
	@Test
	public void testGet() throws Exception {
		Device device = buildDevice("Samsung Nexus S");
		deviceManager.add(device);
		Device another = deviceManager.get(device.asResource().toString());
		assertEquals(device, another);
	}

	@Test(expected=InfosphereException.class)
	public void testGetUnknown() throws Exception {
		deviceManager.get("urn:12345");
	}

	@Test
	public void testGetAll() throws Exception {
		deviceManager.add(buildDevice("Samsung Galaxy III"));
		deviceManager.add(buildDevice("HTC Evo"));
		assertEquals(2, deviceManager.getAll().size());
	}
	
	@Test
	public void testGetAllOwnedBy() throws Exception {
		Person p1 = buildPerson("Anna");
		Person p2 = buildPerson("Norbert");
		Device d1 = buildDevice("Samsung Galaxy III");
		Device d2 = buildDevice("HTC Evo");
		p1.setOwns(d1);
		p2.setOwns(d2);

		pimoService.createOrUpdate(p1);
		pimoService.createOrUpdate(p2);
		pimoService.createOrUpdate(d1);
		pimoService.createOrUpdate(d2);

		assertEquals(d1, deviceManager.getAllOwnedBy(p1.toString()).iterator().next());
	}
	
	@Test
	public void testAdd() throws Exception {
		Device device = buildDevice("Samsung Nexus S");
		deviceManager.add(device);
		Collection<Device> devices = deviceManager.getAll();
		assertEquals(1, devices.size());
		assertTrue(devices.contains(device));
	}

	@Test
	public void testUpdate() throws Exception {
		Device device = buildDevice("Samsung");
		deviceManager.add(device);
		
		device.setPrefLabel("Samsung Galaxy");
		deviceManager.update(device);
		
		Device galaxy = deviceManager.get(device.asResource().toString());
		assertEquals("Samsung Galaxy", galaxy.getPrefLabel());
	}

	@Test
	public void testRemove() throws Exception {
		Device device1 = buildDevice("Samsung Nexus S");
		Device device2 = buildDevice("Samsung Galaxy III");
		deviceManager.add(device1);
		deviceManager.add(device2);
		deviceManager.remove(device1.asResource().toString());
		assertEquals(1, deviceManager.getAll().size());
	}

}