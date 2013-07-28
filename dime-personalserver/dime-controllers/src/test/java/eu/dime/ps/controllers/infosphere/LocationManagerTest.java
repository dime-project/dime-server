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

package eu.dime.ps.controllers.infosphere;

import java.util.Collection;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import eu.dime.ps.controllers.exception.InfosphereException;
import eu.dime.ps.controllers.infosphere.manager.LocationManagerImpl;
import eu.dime.ps.semantic.model.pimo.Location;

/**
 * Tests {@link LocationManagerImpl}.
 * 
 * @author Ismael Rivera
 */
public class LocationManagerTest extends InfoSphereManagerTest {

	@Autowired
	private LocationManagerImpl locationManager;
	
	@Test
	public void testExist() throws Exception {
		Location location = buildLocation("location 1");
		locationManager.add(location);
		assertTrue(locationManager.exist(location.toString()));
	}

	@Test
	public void testGet() throws Exception {
		Location location = buildLocation("location 1");
		locationManager.add(location);
		Location another = locationManager.get(location.asResource().toString());
		assertEquals(location, another);
	}

	@Test(expected=InfosphereException.class)
	public void testGetUnknown() throws Exception {
		locationManager.get("urn:12345");
	}

	@Test
	public void testGetAllLocations() throws Exception {
		locationManager.add(buildLocation("location 2"));
		locationManager.add(buildLocation("location 5"));
		assertEquals(2, locationManager.getAll().size());
	}
	
	@Test
	public void testAdd() throws Exception {
		Location location = buildLocation("location 1");
		locationManager.add(location);
		Collection<Location> locations = locationManager.getAll();
		assertEquals(1, locations.size());
		assertTrue(locations.contains(location));
	}

	@Test
	public void testUpdate() throws Exception {
		Location location = buildLocation("location 4");
		locationManager.add(location);
		
		location.setPrefLabel("location 3");
		locationManager.update(location);
		
		Location galaxy = locationManager.get(location.asResource().toString());
		assertEquals("location 3", galaxy.getPrefLabel());
	}

	@Test
	public void testRemove() throws Exception {
		Location location1 = buildLocation("location 1");
		Location location2 = buildLocation("location 2");
		locationManager.add(location1);
		locationManager.add(location2);
		locationManager.remove(location1.asResource().toString());
		assertEquals(1, locationManager.getAll().size());
	}

}