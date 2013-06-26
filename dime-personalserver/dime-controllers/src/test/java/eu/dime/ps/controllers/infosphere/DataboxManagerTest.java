package eu.dime.ps.controllers.infosphere;

import ie.deri.smile.vocabulary.NSO;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.junit.Test;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.Variable;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.springframework.beans.factory.annotation.Autowired;

import eu.dime.ps.controllers.exception.InfosphereException;
import eu.dime.ps.controllers.infosphere.manager.DataboxManagerImpl;
import eu.dime.ps.semantic.model.nfo.DataContainer;
import eu.dime.ps.semantic.model.nie.DataObject;
import eu.dime.ps.semantic.service.impl.PimoService;

/**
 * Tests {@link DataboxManagerImpl}.
 * 
 * @author Ismael Rivera
 */
public class DataboxManagerTest extends InfoSphereManagerTest {

	@Autowired
	private PimoService pimoService;

	@Autowired
	private DataboxManagerImpl databoxManager;

	@Test
	public void testExist() throws Exception {
		DataContainer databox = buildDatabox();
		databoxManager.add(databox);
		assertTrue(databoxManager.exist(databox.toString()));
	}

	@Test
	public void testGet() throws Exception {
		DataContainer databox = buildDatabox();
		pimoService.createOrUpdate(databox);
		databox = databoxManager.get(databox.asResource().toString());
		assertTrue(databox.getModel().contains(Variable.ANY, NSO.includes, Variable.ANY));
		assertTrue(databox.getModel().contains(Variable.ANY, NSO.excludes, Variable.ANY));
	}
	
	@Test
	public void testCreateAndListEmpty() throws Exception {
		DataContainer businessDb = modelFactory.getNFOFactory().createDataContainer();
		businessDb.setPrefLabel("business");
		databoxManager.add(businessDb);
		DataContainer privateDb = modelFactory.getNFOFactory().createDataContainer();
		privateDb.setPrefLabel("private");
		databoxManager.add(privateDb);

		List<DataContainer> databoxes = new ArrayList<DataContainer>();
		databoxes.addAll(databoxManager.getAll());
		assertEquals(2, databoxes.size());
		for (DataContainer databox : databoxes) {
			assertTrue(ArrayUtils.contains(new String[]{"business", "private"}, databox.getPrefLabel()));
		}
	}
	
	@Test
	public void testAdd() throws Exception {
		loadData("files-data.nt"); // load some files for adding to the databoxes
		URI f1 = new URIImpl("file:/home/ismriv/example/dir1/motivation-long-tail.png");
		URI f2 = new URIImpl("file:/home/ismriv/example/dir1/D2.3.2_FAST_requirements_specification.v2.2.pdf");
		URI f3 = new URIImpl("file:/home/ismriv/example/dir1/D2.1.2_StateOfTheArt_v1.pdf");
		DataContainer aDatabox = modelFactory.getNFOFactory().createDataContainer();
		aDatabox.setPrefLabel("business");
		aDatabox.addPart(f1);
		aDatabox.addPart(f2);
		aDatabox.addPart(f3);
		databoxManager.add(aDatabox);
		
		DataContainer otherDatabox = databoxManager.get(aDatabox.asURI().toString());
		assertEquals("business", otherDatabox.getPrefLabel());
		Collection<URI> itemIds = new ArrayList<URI>();
		for (DataObject item : databoxManager.getDataboxItems(otherDatabox.asURI().toString())) {
			itemIds.add(item.asURI());
		}
		assertEquals(3, itemIds.size());
		assertTrue(itemIds.contains(f1));
		assertTrue(itemIds.contains(f2));
		assertTrue(itemIds.contains(f3));
	}
	
	@Test(expected=InfosphereException.class)
	public void testCreateDataboxInvalidItem() throws Exception {
		DataContainer testDb = modelFactory.getNFOFactory().createDataContainer();
		testDb.setPrefLabel("test");
		testDb.addPart(new URIImpl("file:/non-existing-file.txt"));
		databoxManager.add(testDb);
	}
	
	@Test
	public void testUpdate() throws Exception {
		loadData("files-data.nt"); // load some files for adding to the databoxes
		URI f1 = new URIImpl("file:/home/ismriv/example/dir1/motivation-long-tail.png");
		URI f2 = new URIImpl("file:/home/ismriv/example/dir1/D2.3.2_FAST_requirements_specification.v2.2.pdf");
		URI f3 = new URIImpl("file:/home/ismriv/example/dir1/D2.1.2_StateOfTheArt_v1.pdf");
		
		DataContainer aDatabox = modelFactory.getNFOFactory().createDataContainer();
		aDatabox.setPrefLabel("test");
		aDatabox.setPart(f1);
		databoxManager.add(aDatabox);

		aDatabox.setPrefLabel("updated");
		aDatabox.removeAllPart();
		aDatabox.addPart(f2);
		aDatabox.addPart(f3);
		databoxManager.update(aDatabox);

		DataContainer updatedDatabox = databoxManager.get(aDatabox.asURI().toString());
		assertEquals("updated", updatedDatabox.getPrefLabel());
		Collection<String> itemIds = new ArrayList<String>();
		for (DataObject item : databoxManager.getDataboxItems(updatedDatabox.asURI().toString())) {
			itemIds.add(item.asURI().toString());
		}
		
		assertEquals(2, itemIds.size());
		assertTrue(itemIds.contains(f2.toString()));
		assertTrue(itemIds.contains(f3.toString()));
	}
	
	@Test
	public void testRemove() throws Exception {
		DataContainer testDb = modelFactory.getNFOFactory().createDataContainer();
		testDb.setPrefLabel("test");
		databoxManager.add(testDb);
		
		Collection<DataContainer> all = databoxManager.getAll();
		assertEquals(1, all.size());
		
		DataContainer databox = all.iterator().next();
		databoxManager.remove(databox.asURI().toString());
		assertEquals(0, databoxManager.getAll().size());
	}
	
}
