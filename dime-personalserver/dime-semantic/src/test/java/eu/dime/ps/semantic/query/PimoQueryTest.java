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

package eu.dime.ps.semantic.query;

import ie.deri.smile.vocabulary.NAO;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import eu.dime.ps.semantic.SemanticTest;
import eu.dime.ps.semantic.model.pimo.Person;
import eu.dime.ps.semantic.query.impl.PimoQuery;
import eu.dime.ps.semantic.service.impl.PimoService;

/**
 * Tests {@link PimoQuery}.
 */
public final class PimoQueryTest extends SemanticTest {

	private PimoService pimoService;
	
	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();
		pimoService = new PimoService("test", "test", tripleStore);
	}

	@Override
	@After
	public void tearDown() throws Exception {
		super.tearDown();
	}
	
	@Test
	public void simpleTest() throws Exception {
		Person person = modelFactory.getPIMOFactory().createPerson();
		person.setPrefLabel("John Doe");
		pimoService.create(person);
		
		PimoQuery<Person> query = new PimoQuery<Person>(pimoService, Person.class);
		Person found = query.where(NAO.prefLabel).is("John Doe").first();
		assertEquals(person.asURI(), found.asURI());
	}
	
}