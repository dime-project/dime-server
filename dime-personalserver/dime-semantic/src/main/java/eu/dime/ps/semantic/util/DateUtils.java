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

package eu.dime.ps.semantic.util;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.ontoware.rdf2go.model.node.DatatypeLiteral;
import org.ontoware.rdf2go.model.node.impl.DatatypeLiteralImpl;
import org.ontoware.rdf2go.vocabulary.XSD;

/**
 * A utility class for parsing and formatting dates to use in the RDF models,
 * required by the literals with xsd:date and xsd:dateTime datatypes.
 * It handles dates as defined by ISO 8601.
 * 
 * @see http://www.iso.org/iso/support/faqs/faqs_widely_used_standards/widely_used_standards_other/date_and_time_format.htm
 * @author Ismael Rivera
 */
public class DateUtils {

	private static DatatypeFactory dtFactory;

	static {
		try {
			dtFactory = DatatypeFactory.newInstance();
		} catch (DatatypeConfigurationException e) {
			throw new RuntimeException(e);
		}
	}
		
	/**
	 * Formats the given date into its string representation
	 * according to the ISO 8601 (e.g. 2003-01-22).
	 * Time zone is ignored.
	 * 
	 * @param calendar the date to be formatted
	 * @return formatted date as specified in ISO 8601
	 */
	public static String dateToString(Calendar calendar) {
		GregorianCalendar utcCalendar = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
		utcCalendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE));
		
		XMLGregorianCalendar xmlCalendar = dtFactory.newXMLGregorianCalendar(utcCalendar);
		xmlCalendar = xmlCalendar.normalize();
		return xmlCalendar.toXMLFormat();
	}
	
	/**
	 * Formats the given datetime into its string representation
	 * according to the ISO 8601, and always normalize to UTC time zone
	 * (e.g. 2003-01-22T17:12:09.238Z).
	 * 
	 * @param calendar
	 * @return
	 */
	public static String dateTimeToString(Calendar calendar) {
		return dateTimeToString(calendar.getTimeInMillis());
	}
		
	/**
	 * Formats the given date in milliseconds (unix time)
	 * according to the ISO 8601 (e.g. 2003-01-22).
	 * Time zone is ignored.
	 * 
	 * @param calendar the date in millis to be formatted
	 * @return formatted date as specified in ISO 8601
	 */
	public static String dateTimeToString(long millis) {
		GregorianCalendar utcCalendar = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
		utcCalendar.setTimeInMillis(millis);

		XMLGregorianCalendar xmlCalendar = dtFactory.newXMLGregorianCalendar(utcCalendar);
		xmlCalendar = xmlCalendar.normalize();
		return xmlCalendar.toXMLFormat();
	}

	/**
	 * Returns a given date as a DatatypeLiteral (type = xsd:dateTime).
	 * 
	 * @param calendar the date to transform
	 * @return the literal object for the current date
	 */
	public static DatatypeLiteral dateTimeAsLiteral(Calendar calendar) {
		return new DatatypeLiteralImpl(dateTimeToString(calendar), XSD._dateTime);
	}

	/**
	 * Returns the current date in the default time zone with the default locale.
	 * 
	 * @return current date in UTC time zone
	 */
	public static Calendar now() {
		return Calendar.getInstance();
	}
	
	/**
	 * Returns the current date in a specific time zone with the default locale.
	 * 
	 * @param zone the time zone to use to generate the date
	 * @return current date in the given time zone
	 */
	public static Calendar now(String zone) {
		return Calendar.getInstance(TimeZone.getTimeZone(zone));
	}
	
	/**
	 * Returns the current date as a string.
	 * 
	 * @return the string for the current date
	 */
	public static String currentDateTimeAsString() {
		return dateTimeToString(now());
	}

	/**
	 * Returns the current date as a DatatypeLiteral (type = xsd:dateTime).
	 * 
	 * @return the literal object for the current date
	 */
	public static DatatypeLiteral currentDateTimeAsLiteral() {
		return new DatatypeLiteralImpl(dateTimeToString(now()), XSD._dateTime);
	}

}
