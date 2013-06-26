package eu.dime.ps.datamining;

import ie.deri.smile.rdf.ResourceModel;
import ie.deri.smile.rdf.impl.ResourceModelImpl;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.model.Model;

import eu.dime.ps.datamining.exceptions.DataMiningException;
import eu.dime.ps.datamining.gate.FullnameExtractor;
import eu.dime.ps.datamining.gate.PostalAddressExtractor;
import eu.dime.ps.semantic.model.nco.PersonContact;
import eu.dime.ps.semantic.model.nco.PersonName;
import eu.dime.ps.semantic.model.nco.PostalAddress;

public class ProfileEnricherImpl implements ProfileEnricher {

	private final FullnameExtractor fullnameExtractor;
	private final PostalAddressExtractor postalAddressExtractor;
	
	private ProfileEnricherImpl() {
		this.fullnameExtractor = new FullnameExtractor();
		this.postalAddressExtractor = new PostalAddressExtractor();
	}
	
	private static class ProfileEnricherHolder {
		public static final ProfileEnricher INSTANCE = new ProfileEnricherImpl();
	}
	
	public static ProfileEnricher getInstance() {
		return ProfileEnricherHolder.INSTANCE;
	}
	
	@Override
	public PersonContact enrich(PersonContact profile) throws DataMiningException {
		
		// model containing all profile metadata (both non-enriched and enriched/extracted)
		Model sinkModel = RDF2Go.getModelFactory().createModel().open();
		sinkModel.addAll(profile.getModel().iterator());

		// extract metadata from people's fullnames
		ClosableIterator<PersonName> nameIt = profile.getAllPersonName();
		while (nameIt.hasNext()) {
			PersonName personName = nameIt.next();
			ResourceModel name = new ResourceModelImpl(profile.getModel(), personName.asResource());
			sinkModel.addAll(fullnameExtractor.extract(name).getModel().iterator());
		}
		nameIt.close();
		
		// extract metadata from people's postal addresses
		ClosableIterator<PostalAddress> addressIt = profile.getAllPostalAddress();
		while (addressIt.hasNext()) {
			PostalAddress postalAddress = addressIt.next();
			ResourceModel address = new ResourceModelImpl(profile.getModel(), postalAddress.asResource());
			sinkModel.addAll(postalAddressExtractor.extract(address).getModel().iterator());
		}
		addressIt.close();
		
		return new PersonContact(sinkModel, profile.asResource(), false);
	}
	
}
