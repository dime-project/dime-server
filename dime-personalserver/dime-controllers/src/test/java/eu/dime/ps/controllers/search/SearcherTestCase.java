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

package eu.dime.ps.controllers.search;

import ie.deri.smile.rdf.util.ModelUtils;
import ie.deri.smile.vocabulary.PIMO;

import java.io.IOException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Syntax;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eu.dime.ps.semantic.rdf.ResourceStore;

/**
 * Abstract test case which must be extended by the test cases in
 * the search controller.
 * 
 * @author Ismael Rivera
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring-config/search-tests-context.xml")
public abstract class SearcherTestCase extends Assert {

	@Autowired
	protected ResourceStore resourceStore;

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	protected void loadPIMO() throws ModelRuntimeException, IOException {
		Model sinkModel = RDF2Go.getModelFactory().createModel(PIMO.NS_PIMO).open();
		ModelUtils.loadFromInputStream(
				this.getClass().getClassLoader().getResourceAsStream("vocabularies/pimo/pimo.trig"),
				Syntax.Trig, sinkModel);
		resourceStore.getTripleStore().addModel(sinkModel);
		sinkModel.close();
	}

}
