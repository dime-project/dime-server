package eu.dime.ps.controllers.trustengine;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eu.dime.ps.controllers.infosphere.manager.FileManager;
import eu.dime.ps.controllers.infosphere.manager.PersonGroupManagerImpl;
import eu.dime.ps.controllers.infosphere.manager.PersonManager;
import eu.dime.ps.semantic.connection.Connection;
import eu.dime.ps.semantic.model.ModelFactory;
import eu.dime.ps.semantic.model.nco.EmailAddress;
import eu.dime.ps.semantic.model.nco.PersonContact;
import eu.dime.ps.semantic.model.nco.PersonName;
import eu.dime.ps.semantic.model.nco.PhoneNumber;
import eu.dime.ps.semantic.model.nfo.FileDataObject;
import eu.dime.ps.semantic.model.pimo.Person;
import eu.dime.ps.semantic.model.pimo.PersonGroup;

@ContextConfiguration(locations = { "classpath*:**/applicationContext-trustengine-test.xml" })
@RunWith(SpringJUnit4ClassRunner.class)
public abstract class AbstractTrustEngineTest {
	
	@Autowired
	protected Connection connection;
	
	@Autowired
	protected PersonManager personManager;
	
	@Autowired
	protected PersonGroupManagerImpl personGroupManager;
	
	@Autowired
	protected FileManager fileManager;
	
	protected ModelFactory modelFactory = new ModelFactory();


	protected Person buildPerson(String name, double trust){
		Person person = modelFactory.getPIMOFactory().createPerson();
		person.setPrefLabel(name);
		person.setTrustLevel(trust);
		return person;
	}
	
	protected PersonGroup buildGroup(String name){
		PersonGroup group = modelFactory.getPIMOFactory().createPersonGroup();
		group.setPrefLabel(name);
		return group;
	}
	
	protected FileDataObject buildFile(String path, double d){
		FileDataObject file = modelFactory.getNFOFactory().createFileDataObject();
		file.setFileName(path);
		file.setFileSize(1234L);
		file.setPrivacyLevel(d);
		return file;
	}
	
	protected PersonContact buildProfile(String name, String nickname) {
		PersonContact profile = modelFactory.getNCOFactory().createPersonContact();
		PersonName personName = modelFactory.getNCOFactory().createPersonName();
		personName.setFullname(name);
		personName.setNickname(nickname);
		profile.setPersonName(personName);
		profile.getModel().addAll(personName.getModel().iterator());
		return profile;
	}
}
