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

package eu.dime.ps.semantic.model;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;

import eu.dime.ps.semantic.model.ddo.*;

/**
 * A factory for the Java classes generated automatically for the DDO vocabulary.
 * 
 * @author Ismael Rivera
 * 
 */
public class DDOFactory extends ResourceFactory {

	public Bluetooth createBluetooth() {
		return new Bluetooth(createModel(), generateUniqueURI(), true);
	}

	public Bluetooth createBluetooth(URI resourceUri) {
		return new Bluetooth(createModel(), resourceUri, true);
	}

	public Bluetooth createBluetooth(String resourceUriString) {
		return new Bluetooth(createModel(), new URIImpl(resourceUriString), true);
	}

	public CellularNetwork createCellularNetwork() {
		return new CellularNetwork(createModel(), generateUniqueURI(), true);
	}

	public CellularNetwork createCellularNetwork(URI resourceUri) {
		return new CellularNetwork(createModel(), resourceUri, true);
	}

	public CellularNetwork createCellularNetwork(String resourceUriString) {
		return new CellularNetwork(createModel(), new URIImpl(resourceUriString), true);
	}

	public ComputerNetwork createComputerNetwork() {
		return new ComputerNetwork(createModel(), generateUniqueURI(), true);
	}

	public ComputerNetwork createComputerNetwork(URI resourceUri) {
		return new ComputerNetwork(createModel(), resourceUri, true);
	}

	public ComputerNetwork createComputerNetwork(String resourceUriString) {
		return new ComputerNetwork(createModel(), new URIImpl(resourceUriString), true);
	}

	public Ddo3G createDdo3G() {
		return new Ddo3G(createModel(), generateUniqueURI(), true);
	}

	public Ddo3G createDdo3G(URI resourceUri) {
		return new Ddo3G(createModel(), resourceUri, true);
	}

	public Ddo3G createDdo3G(String resourceUriString) {
		return new Ddo3G(createModel(), new URIImpl(resourceUriString), true);
	}

	public Device createDevice() {
		return new Device(createModel(), generateUniqueURI(), true);
	}

	public Device createDevice(URI resourceUri) {
		return new Device(createModel(), resourceUri, true);
	}

	public Device createDevice(String resourceUriString) {
		return new Device(createModel(), new URIImpl(resourceUriString), true);
	}

	public Ethernet createEthernet() {
		return new Ethernet(createModel(), generateUniqueURI(), true);
	}

	public Ethernet createEthernet(URI resourceUri) {
		return new Ethernet(createModel(), resourceUri, true);
	}

	public Ethernet createEthernet(String resourceUriString) {
		return new Ethernet(createModel(), new URIImpl(resourceUriString), true);
	}

	public GSM createGSM() {
		return new GSM(createModel(), generateUniqueURI(), true);
	}

	public GSM createGSM(URI resourceUri) {
		return new GSM(createModel(), resourceUri, true);
	}

	public GSM createGSM(String resourceUriString) {
		return new GSM(createModel(), new URIImpl(resourceUriString), true);
	}

	public LocalAreaNetwork createLocalAreaNetwork() {
		return new LocalAreaNetwork(createModel(), generateUniqueURI(), true);
	}

	public LocalAreaNetwork createLocalAreaNetwork(URI resourceUri) {
		return new LocalAreaNetwork(createModel(), resourceUri, true);
	}

	public LocalAreaNetwork createLocalAreaNetwork(String resourceUriString) {
		return new LocalAreaNetwork(createModel(), new URIImpl(resourceUriString), true);
	}

	public Mode createMode() {
		return new Mode(createModel(), generateUniqueURI(), true);
	}

	public Mode createMode(URI resourceUri) {
		return new Mode(createModel(), resourceUri, true);
	}

	public Mode createMode(String resourceUriString) {
		return new Mode(createModel(), new URIImpl(resourceUriString), true);
	}

	public Network createNetwork() {
		return new Network(createModel(), generateUniqueURI(), true);
	}

	public Network createNetwork(URI resourceUri) {
		return new Network(createModel(), resourceUri, true);
	}

	public Network createNetwork(String resourceUriString) {
		return new Network(createModel(), new URIImpl(resourceUriString), true);
	}

	public PersonalAreaNetwork createPersonalAreaNetwork() {
		return new PersonalAreaNetwork(createModel(), generateUniqueURI(), true);
	}

	public PersonalAreaNetwork createPersonalAreaNetwork(URI resourceUri) {
		return new PersonalAreaNetwork(createModel(), resourceUri, true);
	}

	public PersonalAreaNetwork createPersonalAreaNetwork(String resourceUriString) {
		return new PersonalAreaNetwork(createModel(), new URIImpl(resourceUriString), true);
	}

	public WiFi createWiFi() {
		return new WiFi(createModel(), generateUniqueURI(), true);
	}

	public WiFi createWiFi(URI resourceUri) {
		return new WiFi(createModel(), resourceUri, true);
	}

	public WiFi createWiFi(String resourceUriString) {
		return new WiFi(createModel(), new URIImpl(resourceUriString), true);
	}

}