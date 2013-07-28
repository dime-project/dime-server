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

package eu.dime.ps.communications.requestbroker.controllers.infosphere;

import static org.mockito.Mockito.when;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.ontoware.rdf2go.model.node.URI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eu.dime.commons.dto.Data;
import eu.dime.commons.dto.Message;
import eu.dime.commons.dto.Request;
import eu.dime.ps.controllers.TenantContextHolder;
import eu.dime.ps.dto.Databox;
import eu.dime.ps.dto.Resource;
import eu.dime.ps.gateway.service.internal.DimeServiceAdapter;
import eu.dime.ps.semantic.connection.Connection;
import eu.dime.ps.semantic.connection.ConnectionProvider;
import eu.dime.ps.semantic.exception.ResourceExistsException;
import eu.dime.ps.semantic.model.ModelFactory;
import eu.dime.ps.semantic.model.dao.Account;
import eu.dime.ps.semantic.model.nco.PersonContact;
import eu.dime.ps.semantic.model.nie.DataObject;
import eu.dime.ps.semantic.model.pimo.Person;
import eu.dime.ps.semantic.model.ppo.PrivacyPreference;
import eu.dime.ps.semantic.privacy.PrivacyPreferenceType;
import eu.dime.ps.semantic.service.impl.PimoService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring-config/controllers-tests-context.xml")
public abstract class PSInfosphereControllerTestIt extends Assert {

	protected ModelFactory modelFactory = new ModelFactory();

	@Autowired
	protected Connection connection;

	@Autowired
	protected PimoService pimoService;

	@Autowired
	protected ConnectionProvider connectionProvider;

	@BeforeClass
	public static void setUpClass() throws Exception {
		// disabling org.openrdf.rdf2go.RepositoryModel warnings
		org.apache.log4j.Logger.getLogger("org.openrdf.rdf2go").setLevel(org.apache.log4j.Level.OFF);        
		java.util.logging.Logger.getLogger("org.openrdf.rdf2go").setLevel(java.util.logging.Level.OFF);
		org.apache.log4j.Logger.getLogger("org.semanticdesktop.aperture").setLevel(org.apache.log4j.Level.OFF);        
		java.util.logging.Logger.getLogger("org.semanticdesktop.aperture").setLevel(java.util.logging.Level.OFF);
	}

	@Before
	public void setUp() throws Exception {
		// set up tenant data in the thread local holders
		TenantContextHolder.setTenant(Long.parseLong(connection.getName()));

		// mocking connection provider
		when(connectionProvider.getConnection(connection.getName())).thenReturn(connection);
	}

	@After
	public void tearDown() throws Exception {

		TenantContextHolder.clear();
	}

	protected Request<Resource> buildRequest(Resource resource) {
		Request<Resource> request = new Request<Resource>();
		Message<Resource> message = new Message<Resource>();

		Data<Resource> data = new Data<Resource>();
		data.getEntries().add(resource);
		message.setData(data);
		request.setMessage(message);

		return request;
	}

	protected Request<Databox> buildDataboxRequest(Databox databox) {
		Request<Databox> request = new Request<Databox>();
		Message<Databox> message = new Message<Databox>();

		Data<Databox> data = new Data<Databox>();
		data.getEntries().add(databox);
		message.setData(data);
		request.setMessage(message);

		return request;
	}


	protected Person createPerson(String name) throws ResourceExistsException  {
		Person person = modelFactory.getPIMOFactory().createPerson();
		person.setPrefLabel(name);
		pimoService.create(person);
		return person;
	}

	protected PersonContact createProfile(String name) throws ResourceExistsException  {
		PersonContact profile = modelFactory.getNCOFactory().createPersonContact();
		profile.setPrefLabel(name);
		pimoService.create(profile);
		return profile;
	}
	
	protected PrivacyPreference createProfileCard(String name) throws ResourceExistsException  {
		PrivacyPreference profilecard = buildProfileCard(name);
		pimoService.create(profilecard);
		return profilecard;
	}

	protected PrivacyPreference buildProfileCard(String name) throws ResourceExistsException  {
		PrivacyPreference profilecard = modelFactory.getPPOFactory().createPrivacyPreference();
		profilecard.setPrefLabel(name);
		profilecard.setLabel(PrivacyPreferenceType.PROFILECARD.toString());
		return profilecard;
	}


	protected Account createAccount(URI creator) throws ResourceExistsException {		
		return createAccount(creator,DimeServiceAdapter.NAME);
	}

	protected Account createAccount(URI creator,String type) throws ResourceExistsException {
		Account account = modelFactory.getDAOFactory().createAccount();
		account.setAccountType(type);
		account.setCreator(creator);
		pimoService.create(account);
		return account;
	}	

	protected DataObject createDataObject() throws ResourceExistsException{
		DataObject dataOb = modelFactory.getNIEFactory().createDataObject();		
		pimoService.create(dataOb);
		return dataOb;
	}

}
