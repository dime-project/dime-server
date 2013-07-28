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
import eu.dime.ps.controllers.infosphere.manager.PlacemarkManagerImpl;
import eu.dime.ps.semantic.model.nfo.Placemark;

/**
 * Tests {@link PlacemarkManagerImpl}.
 * 
 * @author Ismael Rivera
 */
public class PlacemarkManagerTest extends InfoSphereManagerTest {

	@Autowired
	private PlacemarkManagerImpl placemarkManager;
	
	@Test
	public void testExist() throws Exception {
		Placemark placemark = buildPlacemark("placemark 1");
		placemarkManager.add(placemark);
		assertTrue(placemarkManager.exist(placemark.toString()));
	}

	@Test
	public void testGet() throws Exception {
		Placemark placemark = buildPlacemark("placemark 1");
		placemarkManager.add(placemark);
		Placemark another = placemarkManager.get(placemark.asResource().toString());
		assertEquals(placemark, another);
	}

	@Test(expected=InfosphereException.class)
	public void testGetUnknown() throws Exception {
		placemarkManager.get("urn:12345");
	}

	@Test
	public void testGetAllPlacemarks() throws Exception {
		placemarkManager.add(buildPlacemark("placemark 2"));
		placemarkManager.add(buildPlacemark("placemark 5"));
		assertEquals(2, placemarkManager.getAll().size());
	}
	
	@Test
	public void testAdd() throws Exception {
		Placemark placemark = buildPlacemark("placemark 1");
		placemarkManager.add(placemark);
		Collection<Placemark> placemarks = placemarkManager.getAll();
		assertEquals(1, placemarks.size());
		assertTrue(placemarks.contains(placemark));
	}

	@Test
	public void testUpdate() throws Exception {
		Placemark placemark = buildPlacemark("placemark 3");
		placemarkManager.add(placemark);
		
		placemark.setPrefLabel("placemark 4");
		placemarkManager.update(placemark);
		
		Placemark galaxy = placemarkManager.get(placemark.asResource().toString());
		assertEquals("placemark 4", galaxy.getPrefLabel());
	}

	@Test
	public void testRemove() throws Exception {
		Placemark placemark1 = buildPlacemark("placemark 1");
		Placemark placemark2 = buildPlacemark("placemark 2");
		placemarkManager.add(placemark1);
		placemarkManager.add(placemark2);
		placemarkManager.remove(placemark1.asResource().toString());
		assertEquals(1, placemarkManager.getAll().size());
	}

}