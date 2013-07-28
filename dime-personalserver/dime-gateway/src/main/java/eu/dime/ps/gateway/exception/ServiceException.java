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

/**
 * 
 */
package eu.dime.ps.gateway.exception;

import java.util.GregorianCalendar;

/**
 * Implemented service exceptions:
 * <ul>
 *   <li>SERV-001: ServiceNotAvailableException</li>
 *   <li>SERV-002: AttributeNotSupportedException</li>
 *   <li>SERV-003: InvalidLoginException</li>
 *   <li>SERV-004: ServiceAdapterNotSupportedException</li>
 *   <li>SERV-005: InvalidDataException</li>
 * </ul>
 *  
 * @author Sophie.Wrobel
 */
public class ServiceException extends Exception {

	private static final long serialVersionUID = 1L;

	/**
	 * This code should be more specific the general exception name. Example: loginException,
	 * detailCode=UserUnknown
	 */
	private String detailCode;
	
	/** This exception has been created on this date in ms. */
	private long createdOn = GregorianCalendar.getInstance().getTimeInMillis();

	/**
	 * Constructs a new exception.
	 * 
	 * @param message
	 *           a message
	 * @param detailCode
	 *           the detailcode indicating the reason for the exception
	 */
	public ServiceException(final String message, final String detailCode) {
		super(message);
		this.detailCode = detailCode;
	}

	/**
	 * Constructs a new exception.
	 * 
	 * @param message
	 *           a message
	 * @param detailCode
	 *           the detailcode indicating the reason for the exception
	 * @param cause
	 *           a {@link Throwable} that caused this exception
	 */
	public ServiceException(final String message, final String detailCode,
			final Throwable cause) {
		super(message, cause);
		this.detailCode = detailCode;
	}

	/**
	 * Returns the detailcode.
	 * 
	 * @return the detailcode
	 */
	public String getDetailCode() {
		return detailCode;
	}

	/**
	 * Sets the detailcode.
	 * 
	 * @param detailCode
	 *           the detailcode to set
	 */
	public void setDetailCode(final String detailCode) {
		this.detailCode = detailCode;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see java.lang.Throwable#toString()
	 */
	@Override
	public String toString() {
		final StringBuilder buffer = new StringBuilder(this.getClass().getName());
		buffer.append(":");
		buffer.append(getDetailCode());
		buffer.append(" - ");
		buffer.append(getMessage());
		buffer.append(" created on:");
		buffer.append(createdOn);
		
		return buffer.toString();
	}

	/**
	 * @return Returns This exception has been creaton on this date (ms).
	 */
	public long getCreatedOn() {
		return createdOn;
	}

	/**
	 * @param createdOn The created on time (ms) to set.
	 */
	public void setCreatedOn(final long createdOn) {
		this.createdOn = createdOn;
	}
}
