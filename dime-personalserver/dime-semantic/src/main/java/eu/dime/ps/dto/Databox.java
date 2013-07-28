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

package eu.dime.ps.dto;

import ie.deri.smile.rdf.util.ModelUtils;
import ie.deri.smile.vocabulary.NIE;
import ie.deri.smile.vocabulary.NSO;
import ie.deri.smile.vocabulary.PPO;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.Variable;

import eu.dime.ps.semantic.model.nfo.DataContainer;

public class Databox extends Resource {

	// also, we might be interested in renaming some properties
	private static final Map<URI, String> RENAMING_RULES;
	static {
		RENAMING_RULES = new HashMap<URI, String>();
		RENAMING_RULES.put(NIE.hasPart, "items");
	}

	public Databox() {
		super();
	}

	public Databox(DataContainer databox,URI me) {
		super();
		addToMap(databox, RENAMING_RULES,me);

		// this data shouldn't be returned 
		this.remove("ppo:appliesToResource");
		this.remove("nso:isPrivacyPreferenceOf");
		this.remove("ppo:hasAccessSpace");		

	}

}
