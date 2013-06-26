package eu.dime.ps.controllers.context.raw.ifc;

import eu.dime.context.model.api.IContextDataset;
import eu.dime.context.model.api.IContextElement;
import eu.dime.ps.storage.entities.Tenant;

public interface ILocationService {
	
	//IContextDataset getLocation(IContextElement[] wfs, IContextElement[] cells);
	
	IContextDataset getCivilAddress(Tenant t, IContextDataset dataset);

}
