package eu.dime.ps.semantic.poc;

import ie.deri.smile.rdf.util.ModelUtils;
import junit.framework.TestCase;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.model.ModelSet;
import org.ontoware.rdf2go.model.Syntax;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring-config/semantic-poc-tests-context.xml")
public class PocNorbertTest extends TestCase {

	@BeforeClass
	public static void setUpClass() throws Exception {
		// disabling org.openrdf.rdf2go.RepositoryModel warnings
		org.apache.log4j.Logger.getLogger("org.openrdf.rdf2go").setLevel(org.apache.log4j.Level.OFF);        
		java.util.logging.Logger.getLogger("org.openrdf.rdf2go").setLevel(java.util.logging.Level.OFF);
	}

	@Test
	public void testLoadNorbert() throws Exception {
		ModelSet store = RDF2Go.getModelFactory().createModelSet();
		store.open();
		ModelUtils.loadFromInputStream(
				this.getClass().getClassLoader().getResourceAsStream("poc-data/norbert/norbert-pimo.trig"),
				Syntax.Trig, store);
		ModelUtils.loadFromInputStream(
				this.getClass().getClassLoader().getResourceAsStream("poc-data/norbert/norbert-profile.trig"),
				Syntax.Trig, store);
		ModelUtils.loadFromInputStream(
				this.getClass().getClassLoader().getResourceAsStream("poc-data/norbert/norbert-resources.ttl"),
				Syntax.Turtle, store);
		ModelUtils.loadFromInputStream(
				this.getClass().getClassLoader().getResourceAsStream("poc-data/norbert/norbert-databoxes.ttl"),
				Syntax.Turtle, store);
		ModelUtils.loadFromInputStream(
				this.getClass().getClassLoader().getResourceAsStream("poc-data/norbert/norbert-liveposts.ttl"),
				Syntax.Turtle, store);
		ModelUtils.loadFromInputStream(
				this.getClass().getClassLoader().getResourceAsStream("poc-data/norbert/norbert-situations.trig"),
				Syntax.Trig, store);
		store.close();
	}
	
}
