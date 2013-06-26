package eu.dime.ps.controllers.infosphere;

import ie.deri.smile.rdf.TripleStore;
import ie.deri.smile.vocabulary.PIMO;

import java.util.Collection;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import eu.dime.ps.controllers.infosphere.manager.ShareableProfileManager;
import eu.dime.ps.semantic.model.ModelFactory;
import eu.dime.ps.semantic.model.dao.Account;
import eu.dime.ps.semantic.model.nco.BirthDate;
import eu.dime.ps.semantic.model.nco.PersonContact;
import eu.dime.ps.semantic.model.nco.PersonName;
import eu.dime.ps.semantic.model.nco.PhoneNumber;
import eu.dime.ps.semantic.model.nso.AccessSpace;
import eu.dime.ps.semantic.model.pimo.Person;
import eu.dime.ps.semantic.model.ppo.PrivacyPreference;
import eu.dime.ps.semantic.privacy.PrivacyPreferenceType;
import eu.dime.ps.semantic.rdf.ResourceStore;
import eu.dime.ps.semantic.service.impl.PimoService;
import eu.dime.ps.semantic.util.DateUtils;

/**
 * Tests {@link ShareableProfileManager}.
 * 
 * @author Ismael Rivera
 */
public class ShareableProfileManagerTest extends InfoSphereManagerTest {

	@Autowired
	private TripleStore tripleStore;
	
	@Autowired
	private ResourceStore resourceStore;
	
	@Autowired
	private PimoService pimoService;
	
	@Autowired
	private ShareableProfileManager manager;
	
	private ModelFactory modelFactory = new ModelFactory();
	
	@Test
	public void testGetProfileFromAccount() throws Exception {
		Account sender = modelFactory.getDAOFactory().createAccount();
		sender.setCreator(pimoService.getUserUri());
		sender.setAccountType("di.me");
		sender.setPrefLabel("my account");
		pimoService.createOrUpdate(sender);
		
		PersonContact profile1 = modelFactory.getNCOFactory().createPersonContact();
		BirthDate dob = modelFactory.getNCOFactory().createBirthDate();
		dob.setBirthDate(DateUtils.now());
		profile1.setBirthDate(dob);
		profile1.getModel().addAll(dob.getModel().iterator());
		PersonName name = modelFactory.getNCOFactory().createPersonName();
		name.setNameGiven("Ismael");
		name.setNameFamily("Rivera");
		name.setFullname("Ismael Rivera");
		name.setNickname("ismael");
		profile1.setPersonName(name);
		profile1.getModel().addAll(name.getModel().iterator());
		pimoService.createOrUpdate(profile1);
		
		PersonContact profile2 = modelFactory.getNCOFactory().createPersonContact();
		PhoneNumber number = modelFactory.getNCOFactory().createPhoneNumber();
		number.setPhoneNumber("555-123872");
		profile2.setPhoneNumber(number);
		profile2.getModel().addAll(number.getModel().iterator());
		pimoService.createOrUpdate(profile2);

		Person jaimito = modelFactory.getPIMOFactory().createPerson();
		jaimito.setPrefLabel("Jaimito");
		pimoService.createOrUpdate(jaimito);
		
		Account jaimitoAccount = modelFactory.getDAOFactory().createAccount();
		jaimitoAccount.setCreator(jaimito);
		jaimitoAccount.setAccountType("di.me");
		jaimitoAccount.setPrefLabel("jaimito's account");
		pimoService.createOrUpdate(jaimitoAccount);

		AccessSpace accessSpace = modelFactory.getNSOFactory().createAccessSpace();
		accessSpace.setSharedThrough(sender);
		accessSpace.addIncludes(jaimito);
		PrivacyPreference card = modelFactory.getPPOFactory().createPrivacyPreference();
		card.getModel().addStatement(card, PIMO.isDefinedBy, pimoService.getPimoUri());
		card.setLabel(PrivacyPreferenceType.PROFILECARD.toString());
		card.setPrefLabel("public card");
		card.setAccessSpace(accessSpace);
		card.addAppliesToResource(name);
		card.addAppliesToResource(number);
		card.getModel().addAll(accessSpace.getModel().iterator());
		pimoService.createOrUpdate(card);
		
		Collection<PersonContact> profiles = manager.getAll(sender.asURI().toString(), jaimitoAccount.asURI().toString());
		assertEquals(1, profiles.size());
		
		PersonContact profile = profiles.iterator().next();
		assertEquals("Ismael Rivera", profile.getPrefLabel());
		
		assertTrue(profile.hasPersonName());
		assertEquals("Ismael Rivera", profile.getAllPersonName().next().getFullname());
		assertTrue(profile.hasPhoneNumber());
		assertEquals("555-123872", profile.getAllPhoneNumber().next().getPhoneNumber());
	}

}
