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

package eu.dime.ps.controllers.infosphere.manager;

import ie.deri.smile.vocabulary.NCO;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;

import eu.dime.ps.controllers.exception.InfosphereException;
import eu.dime.ps.semantic.exception.NotFoundException;
import eu.dime.ps.semantic.exception.PrivacyPreferenceException;
import eu.dime.ps.semantic.model.dao.Account;
import eu.dime.ps.semantic.model.nco.PersonContact;
import eu.dime.ps.semantic.model.ppo.PrivacyPreference;
import eu.dime.ps.semantic.privacy.PrivacyPreferenceService;
import eu.dime.ps.semantic.service.impl.PimoService;

/**
 * Implementation of ShareableManager for profiles card, compliant with the
 * privacy preferences of the user.
 * 
 * @author Ismael Rivera
 */
public class ShareableProfileManager extends ShareableManagerBase<PersonContact> implements ShareableManager<PersonContact> {

	public static final List<URI> SHAREABLE_PROFILE_PROPERTIES;
	static {
		SHAREABLE_PROFILE_PROPERTIES = new ArrayList<URI>(33);
		
		// PersonName
		SHAREABLE_PROFILE_PROPERTIES.add(NCO.nameGiven);
		SHAREABLE_PROFILE_PROPERTIES.add(NCO.nameFamily);
		SHAREABLE_PROFILE_PROPERTIES.add(NCO.nameAdditional);
		SHAREABLE_PROFILE_PROPERTIES.add(NCO.nameHonorificPrefix);
		SHAREABLE_PROFILE_PROPERTIES.add(NCO.nameHonorificSuffix);
		SHAREABLE_PROFILE_PROPERTIES.add(NCO.fullname);
		SHAREABLE_PROFILE_PROPERTIES.add(NCO.nickname);

		SHAREABLE_PROFILE_PROPERTIES.add(NCO.gender);
		SHAREABLE_PROFILE_PROPERTIES.add(NCO.age);

		// BirthDate
		SHAREABLE_PROFILE_PROPERTIES.add(NCO.birthDate);

		// EmailAddress
		SHAREABLE_PROFILE_PROPERTIES.add(NCO.emailAddress);
		
		// PhoneNumber
		SHAREABLE_PROFILE_PROPERTIES.add(NCO.phoneNumber);
		
		SHAREABLE_PROFILE_PROPERTIES.add(NCO.hobby);
		
		SHAREABLE_PROFILE_PROPERTIES.add(NCO.blogUrl);
		SHAREABLE_PROFILE_PROPERTIES.add(NCO.foafUrl);
		SHAREABLE_PROFILE_PROPERTIES.add(NCO.websiteUrl);
		
		// Affiliation
		SHAREABLE_PROFILE_PROPERTIES.add(NCO.department);
		SHAREABLE_PROFILE_PROPERTIES.add(NCO.org);
		SHAREABLE_PROFILE_PROPERTIES.add(NCO.role);
		SHAREABLE_PROFILE_PROPERTIES.add(NCO.title);
		
		// IMAccount
		SHAREABLE_PROFILE_PROPERTIES.add(NCO.imAccountType);
		SHAREABLE_PROFILE_PROPERTIES.add(NCO.imID);
		SHAREABLE_PROFILE_PROPERTIES.add(NCO.imNickname);
		SHAREABLE_PROFILE_PROPERTIES.add(NCO.imStatus);
		SHAREABLE_PROFILE_PROPERTIES.add(NCO.imStatusMessage);
		
		// PostalAddress
		SHAREABLE_PROFILE_PROPERTIES.add(NCO.addressLocation);
		SHAREABLE_PROFILE_PROPERTIES.add(NCO.country);
		SHAREABLE_PROFILE_PROPERTIES.add(NCO.extendedAddress);
		SHAREABLE_PROFILE_PROPERTIES.add(NCO.locality);
		SHAREABLE_PROFILE_PROPERTIES.add(NCO.pobox);
		SHAREABLE_PROFILE_PROPERTIES.add(NCO.postalcode);
		SHAREABLE_PROFILE_PROPERTIES.add(NCO.region);
		SHAREABLE_PROFILE_PROPERTIES.add(NCO.streetAddress);
	};

	private ProfileCardManager profileCardManager;
	
	public void setProfileCardManager(ProfileCardManager profileCardManager) {
		this.profileCardManager = profileCardManager;
	}
	
	@Override
	public boolean exist(String resourceId) throws InfosphereException {
		return profileCardManager.exist(resourceId);
	}

	@Override
	public PersonContact get(String profileCardId, String requesterId) throws InfosphereException {
		PersonContact profile = profileCardManager.getProfile(profileCardId, SHAREABLE_PROFILE_PROPERTIES);
		checkAuthorized(profile, requesterId);

		// sets the profile as sharedWith the requester user
		setSharedWith(profile, requesterId);
		
		return profile;
	}

	@Override
	public Collection<PersonContact> getAll(String accountId, String requesterId) throws InfosphereException {
		Collection<PersonContact> profiles = profileCardManager.getAllProfile(accountId, SHAREABLE_PROFILE_PROPERTIES);
		
		// filtering out those the user is not authorized to access
		Collection<PersonContact> authorized = filterAuthorized(profiles, requesterId);
	
		// sets the profiles as sharedWith the requester user
		setSharedWith(authorized, requesterId);

		return authorized;
	}

	/**
	 * Same as {@link ShareableManagerBase.checkAuthorized} but the authorization will be checked on the privacy
	 * preference instance (with the same identifier as the profile parameter), not the profile itself which
	 * was generated on-the-fly with the profile attributes speficied in the privacy preference.
	 */
	protected void checkAuthorized(PersonContact profile, String requesterId) throws InfosphereException {
		PimoService pimoService = getPimoService();
		PrivacyPreferenceService privacyPreferenceService = getPrivacyPreferenceService();

		try {
			Account account = pimoService.get(new URIImpl(requesterId), Account.class);
			if (!account.hasCreator()) {
				throw new InfosphereException("Couldn't check accesibility: creator not found for account "+requesterId);
			}

			PrivacyPreference profileCard = profileCardManager.get(profile.toString());
			if (!privacyPreferenceService.hasAccessTo(profileCard, account)) {
				throw new InfosphereException(account+" is not authorized to access "+profile);
			}
		} catch (NotFoundException e) {
			throw new InfosphereException("Cannot check authorization [item="+profile+", agent="+requesterId+"]: "+e.getMessage(), e);
		} catch (PrivacyPreferenceException e) {
			throw new InfosphereException("Cannot check authorization [item="+profile+", agent="+requesterId+"]: "+e.getMessage(), e);
		}
	}
	
}