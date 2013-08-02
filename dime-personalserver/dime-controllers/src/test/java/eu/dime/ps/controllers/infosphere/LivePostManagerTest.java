/*
* Copyright 2013 by the digital.me project (http://www.dime-project.eu).
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
import eu.dime.ps.controllers.infosphere.manager.LivePostManagerImpl;
import eu.dime.ps.semantic.model.dlpo.LivePost;

/**
 * Tests {@link LivePostManagerImpl}.
 * 
 * @author Ismael Rivera
 */
public class LivePostManagerTest extends InfoSphereManagerTest {

	@Autowired
	private LivePostManagerImpl livePostManager;
	
	@Test
	public void testExist() throws Exception {
		LivePost livepost = buildLivePost("test 1");
		livePostManager.add(livepost);
		assertTrue(livePostManager.exist(livepost.toString()));
	}

	@Test
	public void testGet() throws Exception {
		LivePost livepost = buildLivePost("text 1");
		livePostManager.add(livepost);
		LivePost another = livePostManager.get(livepost.asResource().toString());
		assertEquals(livepost, another);
		assertEquals("text 1", livepost.getAllTextualContent().next());
	}

	@Test(expected=InfosphereException.class)
	public void testGetUnknown() throws Exception {
		livePostManager.get("urn:12345");
	}

	@Test
	public void testGetAll() throws Exception {
		livePostManager.add(buildLivePost("text 2"));
		livePostManager.add(buildLivePost("text 5"));
		assertEquals(2, livePostManager.getAll().size());
	}
	
	@Test
	public void testAdd() throws Exception {
		LivePost livepost = buildLivePost("text 1");
		livePostManager.add(livepost);
		Collection<LivePost> liveposts = livePostManager.getAll();
		assertEquals(1, liveposts.size());
		assertTrue(liveposts.contains(livepost));
	}

	@Test
	public void testUpdate() throws Exception {
		LivePost livepost = buildLivePost("text 4");
		livePostManager.add(livepost);
		
		livepost.setPrefLabel("text 3");
		livePostManager.update(livepost);
		
		LivePost another = livePostManager.get(livepost.asResource().toString());
		assertEquals("text 3", another.getPrefLabel());
	}

	@Test
	public void testRemove() throws Exception {
		LivePost livepost1 = buildLivePost("text 1");
		LivePost livepost2 = buildLivePost("text 2");
		livePostManager.add(livepost1);
		livePostManager.add(livepost2);
		livePostManager.remove(livepost1.asResource().toString());
		assertEquals(1, livePostManager.getAll().size());
	}
	
}
