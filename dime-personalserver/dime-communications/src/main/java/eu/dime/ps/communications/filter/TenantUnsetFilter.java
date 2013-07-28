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

package eu.dime.ps.communications.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ContainerResponseFilter;

import eu.dime.ps.controllers.TenantContextHolder;

/**
 * Intercepts all responses from the API in order to unset/clear the tenant information
 * stored in the {@link TenantContextHolder}.
 * 
 * @author Ismael Rivera
 */
public class TenantUnsetFilter implements ContainerResponseFilter {

	private static final Logger logger = LoggerFactory.getLogger(TenantUnsetFilter.class);

	@Override
	public ContainerResponse filter(ContainerRequest request, ContainerResponse response) {
		TenantContextHolder.clear();
		logger.debug("TenantContextHolder now cleared, as request processing completed");
		return response;
	}

}
