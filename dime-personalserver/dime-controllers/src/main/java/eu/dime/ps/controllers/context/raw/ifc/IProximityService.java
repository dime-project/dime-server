package eu.dime.ps.controllers.context.raw.ifc;

import eu.dime.context.model.api.IContextDataset;
import eu.dime.context.model.api.IEntity;
import eu.dime.ps.storage.entities.Tenant;

public interface IProximityService {

	IContextDataset getProximity(IEntity entity, Tenant t);
	
}
