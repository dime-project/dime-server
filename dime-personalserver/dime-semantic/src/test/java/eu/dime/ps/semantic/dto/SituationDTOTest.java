package eu.dime.ps.semantic.dto;

import ie.deri.smile.vocabulary.DCON;

import org.junit.Assert;
import org.junit.Test;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdf2go.vocabulary.RDF;

import eu.dime.ps.dto.SituationDTO;
import eu.dime.ps.semantic.model.ModelFactory;
import eu.dime.ps.semantic.model.dcon.Situation;
import eu.dime.ps.semantic.model.pimo.Person;

public class SituationDTOTest extends Assert {

	final ModelFactory factory = new ModelFactory();
	final Person me = factory.getPIMOFactory().createPerson();

	@Test
	public void testSerialize() {
		Situation situation = factory.getDCONFactory().createSituation("urn:situation:123");
		situation.setPrefLabel("Example");
		situation.setScore(0.82641F);
		
		SituationDTO dto = new SituationDTO(situation, me);
		assertEquals("urn:situation:123", dto.get("guid"));
		assertEquals("situation", dto.get("type"));
		assertEquals("Example", dto.get("name"));
		assertEquals(0.82641D, dto.get("nao:score"));
		assertTrue(dto.containsKey("active"));
	}
	
	@Test
	public void testSerializeActive() {
		Situation situation = factory.getDCONFactory().createSituation("urn:situation:123");
		situation.getModel().addStatement(me, DCON.hasSituation, situation);
		
		SituationDTO dto = new SituationDTO(situation, me);
		assertEquals(true, dto.get("active"));
	}
	
	@Test
	public void testSerializeNotActive() {
		Situation situation = factory.getDCONFactory().createSituation("urn:situation:123");
		
		SituationDTO dto = new SituationDTO(situation, me);
		assertEquals(false, dto.get("active"));
	}
	
	@Test
	public void testDeserialize() {
		SituationDTO dto = new SituationDTO();
		dto.put("guid", "urn:situation:123");
		dto.put("type", "situation");
		dto.put("name", "Example");
		dto.put("nao:score", "0.82641");
		dto.put("imageUrl", null);
		dto.put("active", true);

		Situation situation = dto.asResource(new URIImpl("urn:situation:123"), Situation.class, me.asURI());
		assertEquals("urn:situation:123", situation.asURI().toString());
		assertTrue(situation.getModel().contains(situation, RDF.type, DCON.Situation));
		assertEquals("Example", situation.getPrefLabel());
		assertEquals(new Float(0.82641F), situation.getScore());
		assertFalse(situation.hasPrefSymbol());
	}

	@Test
	public void testDeserializeActive() {
		SituationDTO dto = new SituationDTO();
		dto.put("guid", "urn:situation:123");
		dto.put("type", "situation");
		dto.put("active", true);

		Situation situation = dto.asResource(new URIImpl("urn:situation:123"), Situation.class, me.asURI());
		assertTrue(situation.getModel().contains(me, DCON.hasSituation, situation));
	}
	
	@Test
	public void testDeserializeNotActive() {
		SituationDTO dto = new SituationDTO();
		dto.put("guid", "urn:situation:123");
		dto.put("type", "situation");
		dto.put("active", false);

		Situation situation = dto.asResource(new URIImpl("urn:situation:123"), Situation.class, me.asURI());
		assertFalse(situation.getModel().contains(me, DCON.hasSituation, situation));
	}
	
}
