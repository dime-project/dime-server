package eu.dime.ps.semantic.model;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;

import eu.dime.ps.semantic.model.dpo.*;

/**
 * A factory for the Java classes generated automatically for the DPO vocabulary.
 * 
 * @author Ismael Rivera
 * 
 */

public class DPOFactory extends ResourceFactory {

	public Activity createActivity() {
		return new Activity(createModel(), generateUniqueURI(), true);
	}

	public Activity createActivity(URI resourceUri) {
		return new Activity(createModel(), resourceUri, true);
	}

	public Activity createActivity(String resourceUriString) {
		return new Activity(createModel(), new URIImpl(resourceUriString), true);
	}

	public Altitude createAltitude() {
		return new Altitude(createModel(), generateUniqueURI(), true);
	}

	public Altitude createAltitude(URI resourceUri) {
		return new Altitude(createModel(), resourceUri, true);
	}

	public Altitude createAltitude(String resourceUriString) {
		return new Altitude(createModel(), new URIImpl(resourceUriString), true);
	}

	public Availability createAvailability() {
		return new Availability(createModel(), generateUniqueURI(), true);
	}

	public Availability createAvailability(URI resourceUri) {
		return new Availability(createModel(), resourceUri, true);
	}

	public Availability createAvailability(String resourceUriString) {
		return new Availability(createModel(), new URIImpl(resourceUriString), true);
	}

	public Brightness createBrightness() {
		return new Brightness(createModel(), generateUniqueURI(), true);
	}

	public Brightness createBrightness(URI resourceUri) {
		return new Brightness(createModel(), resourceUri, true);
	}

	public Brightness createBrightness(String resourceUriString) {
		return new Brightness(createModel(), new URIImpl(resourceUriString), true);
	}

	public Direction createDirection() {
		return new Direction(createModel(), generateUniqueURI(), true);
	}

	public Direction createDirection(URI resourceUri) {
		return new Direction(createModel(), resourceUri, true);
	}

	public Direction createDirection(String resourceUriString) {
		return new Direction(createModel(), new URIImpl(resourceUriString), true);
	}

	public Movement createMovement() {
		return new Movement(createModel(), generateUniqueURI(), true);
	}

	public Movement createMovement(URI resourceUri) {
		return new Movement(createModel(), resourceUri, true);
	}

	public Movement createMovement(String resourceUriString) {
		return new Movement(createModel(), new URIImpl(resourceUriString), true);
	}

	public Noise createNoise() {
		return new Noise(createModel(), generateUniqueURI(), true);
	}

	public Noise createNoise(URI resourceUri) {
		return new Noise(createModel(), resourceUri, true);
	}

	public Noise createNoise(String resourceUriString) {
		return new Noise(createModel(), new URIImpl(resourceUriString), true);
	}

	public Place createPlace() {
		return new Place(createModel(), generateUniqueURI(), true);
	}

	public Place createPlace(URI resourceUri) {
		return new Place(createModel(), resourceUri, true);
	}

	public Place createPlace(String resourceUriString) {
		return new Place(createModel(), new URIImpl(resourceUriString), true);
	}

	public Temperature createTemperature() {
		return new Temperature(createModel(), generateUniqueURI(), true);
	}

	public Temperature createTemperature(URI resourceUri) {
		return new Temperature(createModel(), resourceUri, true);
	}

	public Temperature createTemperature(String resourceUriString) {
		return new Temperature(createModel(), new URIImpl(resourceUriString), true);
	}

	public TimePeriod createTimePeriod() {
		return new TimePeriod(createModel(), generateUniqueURI(), true);
	}

	public TimePeriod createTimePeriod(URI resourceUri) {
		return new TimePeriod(createModel(), resourceUri, true);
	}

	public TimePeriod createTimePeriod(String resourceUriString) {
		return new TimePeriod(createModel(), new URIImpl(resourceUriString), true);
	}

	public WeatherConditions createWeatherConditions() {
		return new WeatherConditions(createModel(), generateUniqueURI(), true);
	}

	public WeatherConditions createWeatherConditions(URI resourceUri) {
		return new WeatherConditions(createModel(), resourceUri, true);
	}

	public WeatherConditions createWeatherConditions(String resourceUriString) {
		return new WeatherConditions(createModel(), new URIImpl(resourceUriString), true);
	}

}