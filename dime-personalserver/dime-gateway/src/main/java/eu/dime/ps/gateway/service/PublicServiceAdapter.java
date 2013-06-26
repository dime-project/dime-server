/**
 * 
 */
package eu.dime.ps.gateway.service;

import eu.dime.ps.gateway.exception.*;

/**
 * Public services (with no authentication requirements)
 * 
 * @author Sophie.Wrobel
 *
 */
public abstract class PublicServiceAdapter extends ServiceAdapterBase {


		
	public PublicServiceAdapter(String identifier) 
	throws ServiceNotAvailableException {
		super();
		try {
			this.setIdentifer(identifier);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceNotAvailableException();
		}
	}

}
