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
