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

package eu.dime.ps.communications.requestbroker.controllers.infosphere;

import ie.deri.smile.rdf.util.ModelUtils;
import ie.deri.smile.vocabulary.NSO;
import ie.deri.smile.vocabulary.PPO;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.Variable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.dime.ps.controllers.exception.InfosphereException;
import eu.dime.ps.controllers.infosphere.manager.AccountManager;
import eu.dime.ps.controllers.infosphere.manager.PersonGroupManager;
import eu.dime.ps.controllers.infosphere.manager.PersonManager;
import eu.dime.ps.dto.Include;
import eu.dime.ps.dto.Resource;
import eu.dime.ps.gateway.service.internal.DimeServiceAdapter;
import eu.dime.ps.semantic.model.RDFReactorThing;
import eu.dime.ps.semantic.model.dao.Account;
import eu.dime.ps.semantic.model.nfo.DataContainer;

public abstract class PSSharingControllerBase extends PSControllerBase {

	private static final Logger logger = LoggerFactory.getLogger(PSSharingControllerBase.class);

	public abstract PersonManager getPersonManager();
	public abstract AccountManager getAccountManager();
	public abstract PersonGroupManager getPersonGroupManager();

	//////////
	//METHODS FOR SHARING
	//methods that inject and read the nao:includes / nao:excludes parameters
	//////////

	////
	//Method for injecting the privacyPreferences 
	////  

	public void writeIncludes(eu.dime.ps.dto.Resource resource,RDFReactorThing privPref)
			throws ClassCastException, InfosphereException {
		ArrayList<Include> includes = new ArrayList<Include>();
		ArrayList<Include> excludes = new ArrayList<Include>();    	

		readPrivacyPreference(privPref,includes,excludes);	

		resource.put("nao:includes", includes);
		resource.put("nao:excludes", excludes);
	}


	protected void readPrivacyPreference (RDFReactorThing pp, ArrayList<Include>includes,ArrayList<Include> excludes) throws ClassCastException, InfosphereException{
		if (pp != null) {
			ClosableIterator<Statement> asIt = pp.getModel().findStatements(pp, PPO.hasAccessSpace, Variable.ANY);
			while (asIt.hasNext()) {
				URI accessSpace = asIt.next().getObject().asURI();
				Include include = new Include();
				Include exclude = new Include();

				Collection<Node> saidSender = ModelUtils.findObjects(pp.getModel(), accessSpace, NSO.sharedThrough);
				//watch out, it should only have one sharedThrough
				for (Node node:saidSender){
					if (node != null){
						include.setSaidSender(node.asURI().toString());
						exclude.setSaidSender(node.asURI().toString());
					}
					else{
						logger.warn("the accessSpace"+accessSpace+"has no SharedThrough agent");
					}
				}
				Collection<Node> nodes = ModelUtils.findObjects(pp.getModel(), accessSpace, NSO.includes);
				for (Node node:nodes){
					if (node != null){
						//for each node, see if it is a personId, a groupId or an accountId
						if (getPersonManager().isPerson(node.asURI().toString())){
							include.addPerson(node.asURI().toString(), null);
						}

						else if(getPersonGroupManager().isPersonGroup(node.asURI().toString())){
							include.addGroup(node.asURI().toString());
						}		    				
						else if (getAccountManager().isAccount(node.asURI().toString())){
							//if it is an account of di.me type, it is an account from a person
							//if not it is a service account
							if(getAccountManager().get(node.asURI().toString()).
									getAccountType().equals(DimeServiceAdapter.NAME)){
								include.addPerson(
										getAccountManager().get(node.asURI().toString()).getCreator_asNode().asURI().toString(),
										node.asURI().toString());
							}
							else{
								include.addService(node.asURI().toString());
							}
						}

					}
				}
				nodes = ModelUtils.findObjects(pp.getModel(), accessSpace, NSO.excludes);
				for (Node node : nodes){
					if (node != null){	    				
						//for each node, see if it is a personId, a groupId or an accountId
						if (getPersonManager().isPerson(node.asURI().toString())){
							exclude.addPerson(node.asURI().toString(), null);
						}			    				
						else if(getPersonGroupManager().isPersonGroup(node.asURI().toString())){
							exclude.addGroup(node.asURI().toString());
						}			    				
						else if (getAccountManager().isAccount(node.asURI().toString())){
							//if it is an account of di.me type, it is an account from a person
							//if not it is a service account
							if(getAccountManager().get(node.asURI().toString()).
									getAccountType().equals(DimeServiceAdapter.NAME)){
								exclude.addPerson(
										getAccountManager().get(node.asURI().toString()).getCreator_asNode().asURI().toString(),
										node.asURI().toString());
							}
							else{
								exclude.addService(node.asURI().toString());
							}
						}			    			
					}
					if(!include.getGroups().isEmpty() || !include.getServices().isEmpty() || !include.getPersons().isEmpty())
						includes.add(include);
					if(!exclude.getGroups().isEmpty() || !exclude.getServices().isEmpty() || !exclude.getPersons().isEmpty())
						excludes.add(exclude);
				}			
				if(!include.getGroups().isEmpty() || !include.getServices().isEmpty() || !include.getPersons().isEmpty())
					includes.add(include);
				if(!exclude.getGroups().isEmpty() || !exclude.getServices().isEmpty() || !exclude.getPersons().isEmpty())
					excludes.add(exclude);
			}
			asIt.close();
		} 

	}


	////
	//Method for reading the nao:includes and sharing (implemented in each controller)
	////

	abstract public List<Include> readIncludes(eu.dime.ps.dto.Resource resource,eu.dime.ps.semantic.model.RDFReactorThing modelObject) throws InfosphereException;


	//----------------------------------------------------------------------------
	//method for adding the sharedThrough and the include agents to an accessSpace
	//----------------------------------------------------------------------------



	protected  List<Include> buildIncludesFromMap(eu.dime.ps.dto.Resource resource){

		List<Include> includes = new ArrayList<Include>();
		if (resource.containsKey("nao:includes")){
			Collection<Object> result=(Collection<Object>) resource.get("nao:includes");
			for (Object include  : result)
			{	
				Include i = new Include();
				String saidSender = ((Map<String,Object>) include).get("saidSender").toString();
				i.setSaidSender(saidSender);
				Object o= ((Map<String,Object>) include).get("groups");
				for(Object o2 : (Collection)o){
					i.addGroup(o2.toString());				
				}
				Object o3= ((Map<String,Object>) include).get("services");
				for(Object o4 : (Collection)o3){
					i.addService(o4.toString());				
				}
				Object o5= ((Map<String,Object>) include).get("persons");
				for(Object o6 : (Collection)o5){
					if(((Map<String,Object>) o6).get("saidReceiver") != null){
						i.addPerson(((Map<String,Object>) o6).get("personId").toString(),
								((Map<String,Object>) o6).get("saidReceiver").toString());
					}
					else 
						i.addPerson(((Map<String,Object>) o6).get("personId").toString(),null);
				}
				includes.add(i);
			}
		}

		return includes;

	}

	//If the userId is retreived from sharedBy, it is pointing to an account
	// and the creator of that account should be set as the userId instead.
	protected void setUserId(Resource resource) {

		try {
			Account account = getAccountManager().get(resource.get("userId").toString());
			if (account != null)
			resource.put("userId", account.getCreator_asNode().toString());
		} catch (Exception e) {
			return;
		}
	}

}
