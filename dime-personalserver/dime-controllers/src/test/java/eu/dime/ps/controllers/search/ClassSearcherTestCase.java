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

package eu.dime.ps.controllers.search;

import ie.deri.smile.vocabulary.PIMO;

import java.util.Collection;

import org.apache.commons.lang.ArrayUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.ontoware.rdf2go.model.node.URI;

import eu.dime.ps.controllers.search.impl.ClassSearcher;

public class ClassSearcherTestCase extends SearcherTestCase {

	protected Searcher searcher;

	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();
		searcher = new ClassSearcher(resourceStore);
		loadPIMO();
	}

	@Override
	@After
	public void tearDown() throws Exception {
		resourceStore.clear();
		super.tearDown();
	}
	
	@Test
	public void testSearch() throws Exception {
		Collection<SearchResult> results = null;
		results = searcher.search("Person");

		URI[] expected = new URI[]{
				PIMO.Person, PIMO.PersonRole, PIMO.PersonGroup, PIMO.PersonalInformationModel, PIMO.Collection,
				PIMO.Attendee, PIMO.Contract, PIMO.Agent, PIMO.OrganizationMember, PIMO.Association
		};
		assertEquals(10, results.size());
		for (SearchResult result : results) {
			assertTrue(ArrayUtils.contains(expected, result.getElement(URI.class)));
		}
	}

}
