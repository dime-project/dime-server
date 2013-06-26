package eu.dime.ps.controllers.search;

import ie.deri.smile.vocabulary.PIMO;

import java.util.Collection;

import org.apache.commons.lang.ArrayUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.ontoware.rdf2go.model.node.URI;

import eu.dime.ps.controllers.search.impl.PropertySearcher;

public class PropertySearcherTestCase extends SearcherTestCase {

	protected Searcher searcher;
	
	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();
		searcher = new PropertySearcher(resourceStore);
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
		results = searcher.search("tag");

		// nao:hasTag & nao:isTagFor properties won't appear if not using all RDFS entailment rules
		URI[] expected = new URI[]{ PIMO.hasTag, PIMO.isTagFor, PIMO.tagLabel, PIMO.hasInterest, PIMO.isInterestOf, PIMO.isDefinedBy };//, NAO.hasTag, NAO.isTagFor };
		assertEquals(expected.length, results.size());
		for (SearchResult result : results) {
			assertTrue(ArrayUtils.contains(expected, result.getElement(URI.class)));
		}
	}

}
