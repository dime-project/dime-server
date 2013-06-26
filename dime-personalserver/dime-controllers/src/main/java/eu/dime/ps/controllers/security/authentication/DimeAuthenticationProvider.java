package eu.dime.ps.controllers.security.authentication;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;

import eu.dime.ps.controllers.UserManager;

public class DimeAuthenticationProvider extends
		AbstractUserDetailsAuthenticationProvider {

	private static Logger logger = LoggerFactory
			.getLogger(DimeAuthenticationProvider.class);

	private UserManager userManager;
	
	public void setUserManager(UserManager userManager) {
		this.userManager = userManager;
	}

	@Override
	protected void additionalAuthenticationChecks(UserDetails userDetails,
			UsernamePasswordAuthenticationToken authentication)
			throws AuthenticationException {
		
		
	}

	@Override
	protected UserDetails retrieveUser(String username,
			UsernamePasswordAuthenticationToken authentication)
			throws AuthenticationException {

		String password = (String) authentication.getCredentials();
		if (!StringUtils.hasText(password)) {
			throw new BadCredentialsException("Please enter password");
		}

		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		try {
			String roleString = userManager.getByUsernameAndPassword(username, password).getRole().name();
			authorities.add(new GrantedAuthorityImpl(roleString));		
		} catch (EmptyResultDataAccessException e) {
			throw new BadCredentialsException("Invalid username or password",e);
		} catch (EntityNotFoundException e) {
			throw new BadCredentialsException("Invalid username", e);
		} catch (NoResultException e) {
			throw new BadCredentialsException("You are looking for something does not existing!", e);
		} catch (NonUniqueResultException e) {
			throw new BadCredentialsException(
					"Non-unique user, contact the administrator = yourself!", e);
		} catch (Exception e) {
			logger.error("Following Excption has been catched: "
					+ e.getMessage());
		}

		return new User(username, password, 
				true, // enabled
				true, // account not expired
				true, // credentials not expired
				true, // account not locked
				authorities);
	}
}
