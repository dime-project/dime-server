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
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.springframework.beans.factory.annotation.Autowired;

import eu.dime.ps.controllers.infosphere.manager.TaggingManagerImpl;
import eu.dime.ps.semantic.model.nao.Tag;

/**
 * Tests {@link TaggingManagerImpl}.
 * 
 * @author Ismael Rivera
 */
public class TaggingManagerTest extends InfoSphereManagerTest {

	@Autowired
	private TaggingManagerImpl taggingManager;
	
	private Tag buildTag(String name) {
		Tag tag = modelFactory.getNAOFactory().createTag();
		tag.setPrefLabel(name);
		return tag;
	}
	
	@Test
	public void testGetTag() throws Exception {
		Tag tag = buildTag("facebook");
		taggingManager.add(tag);
		Tag another = taggingManager.get(tag.asResource().toString());
		assertEquals(tag, another);
	}

	@Test
	public void testGetAllTags() throws Exception {
		Tag twitter = buildTag("twitter");
		Tag ym = buildTag("yellowmap");
		taggingManager.add(twitter);
		taggingManager.add(ym);
		assertEquals(2, taggingManager.getAll().size());
	}
	
	public void testGetAllTagsByResource() throws Exception {
		URI resource = new URIImpl("some:resource");
		Tag tag = buildTag("di.me");
		tag.setIsTagFor(resource);
		taggingManager.add(tag);
		Collection<Tag> tags = taggingManager.getAllByResource(resource);
		assertEquals(1, tags.size());
		assertTrue(tags.contains(tag));
	}

	@Test
	public void testAddTag() throws Exception {
		Tag tag = buildTag("facebook");
		taggingManager.add(tag);
		Collection<Tag> tags = taggingManager.getAll();
		assertEquals(1, tags.size());
		assertTrue(tags.contains(tag));
	}

	@Test
	public void testUpdateTag() throws Exception {
		Tag tag = buildTag("linkedin");
		taggingManager.add(tag);
		
		tag.setPrefLabel("LinkedIn");
		taggingManager.update(tag);
		
		Tag galaxy = taggingManager.get(tag.asResource().toString());
		assertEquals("LinkedIn", galaxy.getPrefLabel());
	}

	@Test
	public void testRemoveTag() throws Exception {
		Tag tag1 = buildTag("facebook");
		Tag tag2 = buildTag("twitter");
		taggingManager.add(tag1);
		taggingManager.add(tag2);
		taggingManager.remove(tag1.asResource().toString());
		assertEquals(1, taggingManager.getAll().size());
	}
	
	@Test
	public void testTaggingResource() throws Exception {
		URI resource = new URIImpl("some:resource");
		
		Collection<Tag> tags = taggingManager.add(resource, "tag1", "tag2", "tag3");
		assertEquals(3, tags.size());
		
		Tag[] tagArray = tags.toArray(new Tag[tags.size()]);
		assertEquals("tag1", tagArray[0].getPrefLabel());
		assertEquals("tag2", tagArray[1].getPrefLabel());
		assertEquals("tag3", tagArray[2].getPrefLabel());
	}

	@Test
	public void testGetTagsByResource() throws Exception {
		URI resource1 = new URIImpl("some:resource1");
		URI resource2 = new URIImpl("some:resource2");
		
		taggingManager.add(resource1, "tag1", "tag2");
		taggingManager.add(resource2, "tag3");
		
		Collection<Tag> r1Tags = taggingManager.getAllByResource(resource1);
		Collection<Tag> r2Tags = taggingManager.getAllByResource(resource2);
		assertEquals(2, r1Tags.size());
		assertEquals(1, r2Tags.size());
	}
	
	@Test
	public void testGetTagsByLabelLike() throws Exception {
		Tag tag1 = buildTag("tag1");
		Tag tag2 = buildTag("tag2");
		Tag other = buildTag("other");
		taggingManager.add(tag1);
		taggingManager.add(tag2);
		taggingManager.add(other);
		
		Collection<Tag> tags = taggingManager.getAllByLabelLike("tag");
		assertEquals(2, tags.size());

		tags = taggingManager.getAllByLabelLike("oth");
		assertEquals(1, tags.size());
	}

}
