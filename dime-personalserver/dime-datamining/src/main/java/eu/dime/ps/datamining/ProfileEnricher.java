package eu.dime.ps.datamining;

import eu.dime.ps.datamining.exceptions.DataMiningException;
import eu.dime.ps.semantic.model.nco.PersonContact;

public interface ProfileEnricher {

	PersonContact enrich(PersonContact profile) throws DataMiningException;

}