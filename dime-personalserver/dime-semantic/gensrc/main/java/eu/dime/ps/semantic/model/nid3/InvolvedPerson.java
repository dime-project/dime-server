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

package eu.dime.ps.semantic.model.nid3;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.BlankNode;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdfreactor.runtime.Base;
import org.ontoware.rdfreactor.runtime.ReactorResult;


/**
 * This class manages access to these properties:
 * <ul>
 *   <li> InvolvedPersonContact </li>
 *   <li> Involvment </li>
 * </ul>
 *
 * class- This class was generated by <a href="http://RDFReactor.semweb4j.org">RDFReactor</a> */
public class InvolvedPerson extends eu.dime.ps.semantic.model.RDFReactorThing {

    /** http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#InvolvedPerson */
    @SuppressWarnings("hiding")
	public static final URI RDFS_CLASS = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#InvolvedPerson", false);

    /** http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#involvedPersonContact */
    @SuppressWarnings("hiding")
	public static final URI INVOLVEDPERSONCONTACT = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#involvedPersonContact",false);

    /** http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#involvment */
    @SuppressWarnings("hiding")
	public static final URI INVOLVMENT = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#involvment",false);

    /** 
     * All property-URIs with this class as domain.
     * All properties of all super-classes are also available. 
     */
    @SuppressWarnings("hiding")
    public static final URI[] MANAGED_URIS = {
      new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#involvedPersonContact",false),
      new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#involvment",false) 
    };


	// protected constructors needed for inheritance
	
	/**
	 * Returns a Java wrapper over an RDF object, identified by URI.
	 * Creating two wrappers for the same instanceURI is legal.
	 * @param model RDF2GO Model implementation, see http://rdf2go.semweb4j.org
	 * @param classURI URI of RDFS class
	 * @param instanceIdentifier Resource that identifies this instance
	 * @param write if true, the statement (this, rdf:type, TYPE) is written to the model
	 *
	 * [Generated from RDFReactor template rule #c1] 
	 */
	protected InvolvedPerson (Model model, URI classURI, org.ontoware.rdf2go.model.node.Resource instanceIdentifier, boolean write) {
		super(model, classURI, instanceIdentifier, write);
	}

	// public constructors

	/**
	 * Returns a Java wrapper over an RDF object, identified by URI.
	 * Creating two wrappers for the same instanceURI is legal.
	 * @param model RDF2GO Model implementation, see http://rdf2go.ontoware.org
	 * @param instanceIdentifier an RDF2Go Resource identifying this instance
	 * @param write if true, the statement (this, rdf:type, TYPE) is written to the model
	 *
	 * [Generated from RDFReactor template rule #c2] 
	 */
	public InvolvedPerson (Model model, org.ontoware.rdf2go.model.node.Resource instanceIdentifier, boolean write) {
		super(model, RDFS_CLASS, instanceIdentifier, write);
	}


	/**
	 * Returns a Java wrapper over an RDF object, identified by a URI, given as a String.
	 * Creating two wrappers for the same URI is legal.
	 * @param model RDF2GO Model implementation, see http://rdf2go.ontoware.org
	 * @param uriString a URI given as a String
	 * @param write if true, the statement (this, rdf:type, TYPE) is written to the model
	 * @throws ModelRuntimeException if URI syntax is wrong
	 *
	 * [Generated from RDFReactor template rule #c7] 
	 */
	public InvolvedPerson (Model model, String uriString, boolean write) throws ModelRuntimeException {
		super(model, RDFS_CLASS, new URIImpl(uriString,false), write);
	}

	/**
	 * Returns a Java wrapper over an RDF object, identified by a blank node.
	 * Creating two wrappers for the same blank node is legal.
	 * @param model RDF2GO Model implementation, see http://rdf2go.ontoware.org
	 * @param bnode BlankNode of this instance
	 * @param write if true, the statement (this, rdf:type, TYPE) is written to the model
	 *
	 * [Generated from RDFReactor template rule #c8] 
	 */
	public InvolvedPerson (Model model, BlankNode bnode, boolean write) {
		super(model, RDFS_CLASS, bnode, write);
	}

	/**
	 * Returns a Java wrapper over an RDF object, identified by 
	 * a randomly generated URI.
	 * Creating two wrappers results in different URIs.
	 * @param model RDF2GO Model implementation, see http://rdf2go.ontoware.org
	 * @param write if true, the statement (this, rdf:type, TYPE) is written to the model
	 *
	 * [Generated from RDFReactor template rule #c9] 
	 */
	public InvolvedPerson (Model model, boolean write) {
		super(model, RDFS_CLASS, model.newRandomUniqueURI(), write);
	}

    ///////////////////////////////////////////////////////////////////
    // typing

	/**
	 * Return an existing instance of this class in the model. No statements are written.
	 * @param model an RDF2Go model
	 * @param instanceResource an RDF2Go resource
	 * @return an instance of InvolvedPerson  or null if none existst
	 *
	 * [Generated from RDFReactor template rule #class0] 
	 */
	public static InvolvedPerson  getInstance(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource) {
		return Base.getInstance(model, instanceResource, InvolvedPerson.class);
	}

	/**
	 * Create a new instance of this class in the model. 
	 * That is, create the statement (instanceResource, RDF.type, http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#InvolvedPerson).
	 * @param model an RDF2Go model
	 * @param instanceResource an RDF2Go resource
	 *
	 * [Generated from RDFReactor template rule #class1] 
	 */
	public static void createInstance(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource) {
		Base.createInstance(model, RDFS_CLASS, instanceResource);
	}

	/**
	 * @param model an RDF2Go model
	 * @param instanceResource an RDF2Go resource
	 * @return true if instanceResource is an instance of this class in the model
	 *
	 * [Generated from RDFReactor template rule #class2] 
	 */
	public static boolean hasInstance(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource) {
		return Base.hasInstance(model, RDFS_CLASS, instanceResource);
	}

	/**
	 * @param model an RDF2Go model
	 * @return all instances of this class in Model 'model' as RDF resources
	 *
	 * [Generated from RDFReactor template rule #class3] 
	 */
	public static ClosableIterator<org.ontoware.rdf2go.model.node.Resource> getAllInstances(Model model) {
		return Base.getAllInstances(model, RDFS_CLASS, org.ontoware.rdf2go.model.node.Resource.class);
	}

	/**
	 * @param model an RDF2Go model
	 * @return all instances of this class in Model 'model' as a ReactorResult,
	 * which can conveniently be converted to iterator, list or array.
	 *
	 * [Generated from RDFReactor template rule #class3-as] 
	 */
	public static ReactorResult<? extends InvolvedPerson> getAllInstances_as(Model model) {
		return Base.getAllInstances_as(model, RDFS_CLASS, InvolvedPerson.class );
	}

    /**
	 * Remove rdf:type InvolvedPerson from this instance. Other triples are not affected.
	 * To delete more, use deleteAllProperties
	 * @param model an RDF2Go model
	 * @param instanceResource an RDF2Go resource
	 *
	 * [Generated from RDFReactor template rule #class4] 
	 */
	public static void deleteInstance(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource) {
		Base.deleteInstance(model, RDFS_CLASS, instanceResource);
	}

	/**
	 * Delete all (this, *, *), i.e. including rdf:type
	 * @param model an RDF2Go model
	 * @param resource
	 */
	public static void deleteAllProperties(Model model,	org.ontoware.rdf2go.model.node.Resource instanceResource) {
		Base.deleteAllProperties(model, instanceResource);
	}

    ///////////////////////////////////////////////////////////////////
    // property access methods

	/**
	 * @param model an RDF2Go model
	 * @param objectValue
	 * @return all A's as RDF resources, that have a relation 'InvolvedPerson' to this InvolvedPerson instance
	 *
	 * [Generated from RDFReactor template rule #getallinverse1static] 
	 */
	public static ClosableIterator<org.ontoware.rdf2go.model.node.Resource> getAllInvolvedPerson_Inverse(Model model, Object objectValue) {
		return Base.getAll_Inverse(model, eu.dime.ps.semantic.model.nid3.ID3Audio.INVOLVEDPERSON, objectValue);
	}

	/**
	 * @return all A's as RDF resources, that have a relation 'InvolvedPerson' to this InvolvedPerson instance
	 *
	 * [Generated from RDFReactor template rule #getallinverse1dynamic] 
	 */
	public ClosableIterator<org.ontoware.rdf2go.model.node.Resource> getAllInvolvedPerson_Inverse() {
		return Base.getAll_Inverse(this.model, eu.dime.ps.semantic.model.nid3.ID3Audio.INVOLVEDPERSON, this.getResource() );
	}

	/**
	 * @param model an RDF2Go model
	 * @param objectValue
	 * @return all A's as a ReactorResult, that have a relation 'InvolvedPerson' to this InvolvedPerson instance
	 *
	 * [Generated from RDFReactor template rule #getallinverse-as1static] 
	 */
	public static ReactorResult<org.ontoware.rdf2go.model.node.Resource> getAllInvolvedPerson_Inverse_as(Model model, Object objectValue) {
		return Base.getAll_Inverse_as(model, eu.dime.ps.semantic.model.nid3.ID3Audio.INVOLVEDPERSON, objectValue, org.ontoware.rdf2go.model.node.Resource.class);
	}



    /**
     * Check if org.ontoware.rdfreactor.generator.java.JProperty@72afb185 has at least one value set 
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
     * @return true if this property has at least one value
	 *
	 * [Generated from RDFReactor template rule #get0has-static] 
     */
	public static boolean hasInvolvedPersonContact(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource) {
		return Base.has(model, instanceResource, INVOLVEDPERSONCONTACT);
	}

    /**
     * Check if org.ontoware.rdfreactor.generator.java.JProperty@72afb185 has at least one value set 
     * @return true if this property has at least one value
	 *
	 * [Generated from RDFReactor template rule #get0has-dynamic] 
     */
	public boolean hasInvolvedPersonContact() {
		return Base.has(this.model, this.getResource(), INVOLVEDPERSONCONTACT);
	}

    /**
     * Check if org.ontoware.rdfreactor.generator.java.JProperty@72afb185 has the given value (maybe among other values).  
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
	 * @param value the value to be checked
     * @return true if this property contains (maybe among other) the given value
	 *
	 * [Generated from RDFReactor template rule #get0has-value-static] 
     */
	public static boolean hasInvolvedPersonContact(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource, org.ontoware.rdf2go.model.node.Node value ) {
		return Base.hasValue(model, instanceResource, INVOLVEDPERSONCONTACT);
	}

    /**
     * Check if org.ontoware.rdfreactor.generator.java.JProperty@72afb185 has the given value (maybe among other values).  
	 * @param value the value to be checked
     * @return true if this property contains (maybe among other) the given value
	 *
	 * [Generated from RDFReactor template rule #get0has-value-dynamic] 
     */
	public boolean hasInvolvedPersonContact( org.ontoware.rdf2go.model.node.Node value ) {
		return Base.hasValue(this.model, this.getResource(), INVOLVEDPERSONCONTACT);
	}

     /**
     * Get all values of property InvolvedPersonContact as an Iterator over RDF2Go nodes 
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
     * @return a ClosableIterator of RDF2Go Nodes
	 *
	 * [Generated from RDFReactor template rule #get7static] 
     */
	public static ClosableIterator<org.ontoware.rdf2go.model.node.Node> getAllInvolvedPersonContact_asNode(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource) {
		return Base.getAll_asNode(model, instanceResource, INVOLVEDPERSONCONTACT);
	}
	
    /**
     * Get all values of property InvolvedPersonContact as a ReactorResult of RDF2Go nodes 
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
     * @return a List of RDF2Go Nodes
	 *
	 * [Generated from RDFReactor template rule #get7static-reactor-result] 
     */
	public static ReactorResult<org.ontoware.rdf2go.model.node.Node> getAllInvolvedPersonContact_asNode_(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource) {
		return Base.getAll_as(model, instanceResource, INVOLVEDPERSONCONTACT, org.ontoware.rdf2go.model.node.Node.class);
	}

    /**
     * Get all values of property InvolvedPersonContact as an Iterator over RDF2Go nodes 
     * @return a ClosableIterator of RDF2Go Nodes
	 *
	 * [Generated from RDFReactor template rule #get8dynamic] 
     */
	public ClosableIterator<org.ontoware.rdf2go.model.node.Node> getAllInvolvedPersonContact_asNode() {
		return Base.getAll_asNode(this.model, this.getResource(), INVOLVEDPERSONCONTACT);
	}

    /**
     * Get all values of property InvolvedPersonContact as a ReactorResult of RDF2Go nodes 
     * @return a List of RDF2Go Nodes
	 *
	 * [Generated from RDFReactor template rule #get8dynamic-reactor-result] 
     */
	public ReactorResult<org.ontoware.rdf2go.model.node.Node> getAllInvolvedPersonContact_asNode_() {
		return Base.getAll_as(this.model, this.getResource(), INVOLVEDPERSONCONTACT, org.ontoware.rdf2go.model.node.Node.class);
	}
     /**
     * Get all values of property InvolvedPersonContact     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
     * @return a ClosableIterator of $type
	 *
	 * [Generated from RDFReactor template rule #get11static] 
     */
	public static ClosableIterator<eu.dime.ps.semantic.model.nco.Contact> getAllInvolvedPersonContact(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource) {
		return Base.getAll(model, instanceResource, INVOLVEDPERSONCONTACT, eu.dime.ps.semantic.model.nco.Contact.class);
	}
	
    /**
     * Get all values of property InvolvedPersonContact as a ReactorResult of Contact 
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
     * @return a ReactorResult of $type which can conveniently be converted to iterator, list or array
	 *
	 * [Generated from RDFReactor template rule #get11static-reactorresult] 
     */
	public static ReactorResult<eu.dime.ps.semantic.model.nco.Contact> getAllInvolvedPersonContact_as(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource) {
		return Base.getAll_as(model, instanceResource, INVOLVEDPERSONCONTACT, eu.dime.ps.semantic.model.nco.Contact.class);
	}

    /**
     * Get all values of property InvolvedPersonContact     * @return a ClosableIterator of $type
	 *
	 * [Generated from RDFReactor template rule #get12dynamic] 
     */
	public ClosableIterator<eu.dime.ps.semantic.model.nco.Contact> getAllInvolvedPersonContact() {
		return Base.getAll(this.model, this.getResource(), INVOLVEDPERSONCONTACT, eu.dime.ps.semantic.model.nco.Contact.class);
	}

    /**
     * Get all values of property InvolvedPersonContact as a ReactorResult of Contact 
     * @return a ReactorResult of $type which can conveniently be converted to iterator, list or array
	 *
	 * [Generated from RDFReactor template rule #get12dynamic-reactorresult] 
     */
	public ReactorResult<eu.dime.ps.semantic.model.nco.Contact> getAllInvolvedPersonContact_as() {
		return Base.getAll_as(this.model, this.getResource(), INVOLVEDPERSONCONTACT, eu.dime.ps.semantic.model.nco.Contact.class);
	}
 
    /**
     * Adds a value to property InvolvedPersonContact as an RDF2Go node 
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
	 * @param value the value to be added
	 *
	 * [Generated from RDFReactor template rule #add1static] 
     */
	public static void addInvolvedPersonContact(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource, org.ontoware.rdf2go.model.node.Node value) {
		Base.add(model, instanceResource, INVOLVEDPERSONCONTACT, value);
	}
	
    /**
     * Adds a value to property InvolvedPersonContact as an RDF2Go node 
	 * @param value the value to be added
	 *
	 * [Generated from RDFReactor template rule #add1dynamic] 
     */
	public void addInvolvedPersonContact( org.ontoware.rdf2go.model.node.Node value) {
		Base.add(this.model, this.getResource(), INVOLVEDPERSONCONTACT, value);
	}
    /**
     * Adds a value to property InvolvedPersonContact from an instance of Contact 
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
	 *
	 * [Generated from RDFReactor template rule #add3static] 
     */
	public static void addInvolvedPersonContact(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource, eu.dime.ps.semantic.model.nco.Contact value) {
		Base.add(model, instanceResource, INVOLVEDPERSONCONTACT, value);
	}
	
    /**
     * Adds a value to property InvolvedPersonContact from an instance of Contact 
	 *
	 * [Generated from RDFReactor template rule #add4dynamic] 
     */
	public void addInvolvedPersonContact(eu.dime.ps.semantic.model.nco.Contact value) {
		Base.add(this.model, this.getResource(), INVOLVEDPERSONCONTACT, value);
	}
  

    /**
     * Sets a value of property InvolvedPersonContact from an RDF2Go node.
     * First, all existing values are removed, then this value is added.
     * Cardinality constraints are not checked, but this method exists only for properties with
     * no minCardinality or minCardinality == 1.
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
	 * @param value the value to be set
	 *
	 * [Generated from RDFReactor template rule #set1static] 
     */
	public static void setInvolvedPersonContact( Model model, org.ontoware.rdf2go.model.node.Resource instanceResource, org.ontoware.rdf2go.model.node.Node value) {
		Base.set(model, instanceResource, INVOLVEDPERSONCONTACT, value);
	}
	
    /**
     * Sets a value of property InvolvedPersonContact from an RDF2Go node.
     * First, all existing values are removed, then this value is added.
     * Cardinality constraints are not checked, but this method exists only for properties with
     * no minCardinality or minCardinality == 1.
	 * @param value the value to be added
	 *
	 * [Generated from RDFReactor template rule #set1dynamic] 
     */
	public void setInvolvedPersonContact( org.ontoware.rdf2go.model.node.Node value) {
		Base.set(this.model, this.getResource(), INVOLVEDPERSONCONTACT, value);
	}
    /**
     * Sets a value of property InvolvedPersonContact from an instance of Contact 
     * First, all existing values are removed, then this value is added.
     * Cardinality constraints are not checked, but this method exists only for properties with
     * no minCardinality or minCardinality == 1.
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
	 * @param value the value to be added
	 *
	 * [Generated from RDFReactor template rule #set3static] 
     */
	public static void setInvolvedPersonContact(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource, eu.dime.ps.semantic.model.nco.Contact value) {
		Base.set(model, instanceResource, INVOLVEDPERSONCONTACT, value);
	}
	
    /**
     * Sets a value of property InvolvedPersonContact from an instance of Contact 
     * First, all existing values are removed, then this value is added.
     * Cardinality constraints are not checked, but this method exists only for properties with
     * no minCardinality or minCardinality == 1.
	 * @param value the value to be added
	 *
	 * [Generated from RDFReactor template rule #set4dynamic] 
     */
	public void setInvolvedPersonContact(eu.dime.ps.semantic.model.nco.Contact value) {
		Base.set(this.model, this.getResource(), INVOLVEDPERSONCONTACT, value);
	}
  


    /**
     * Removes a value of property InvolvedPersonContact as an RDF2Go node 
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
	 * @param value the value to be removed
	 *
	 * [Generated from RDFReactor template rule #remove1static] 
     */
	public static void removeInvolvedPersonContact( Model model, org.ontoware.rdf2go.model.node.Resource instanceResource, org.ontoware.rdf2go.model.node.Node value) {
		Base.remove(model, instanceResource, INVOLVEDPERSONCONTACT, value);
	}
	
    /**
     * Removes a value of property InvolvedPersonContact as an RDF2Go node
	 * @param value the value to be removed
	 *
	 * [Generated from RDFReactor template rule #remove1dynamic] 
     */
	public void removeInvolvedPersonContact( org.ontoware.rdf2go.model.node.Node value) {
		Base.remove(this.model, this.getResource(), INVOLVEDPERSONCONTACT, value);
	}
    /**
     * Removes a value of property InvolvedPersonContact given as an instance of Contact 
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
	 * @param value the value to be removed
	 *
	 * [Generated from RDFReactor template rule #remove3static] 
     */
	public static void removeInvolvedPersonContact(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource, eu.dime.ps.semantic.model.nco.Contact value) {
		Base.remove(model, instanceResource, INVOLVEDPERSONCONTACT, value);
	}
	
    /**
     * Removes a value of property InvolvedPersonContact given as an instance of Contact 
	 * @param value the value to be removed
	 *
	 * [Generated from RDFReactor template rule #remove4dynamic] 
     */
	public void removeInvolvedPersonContact(eu.dime.ps.semantic.model.nco.Contact value) {
		Base.remove(this.model, this.getResource(), INVOLVEDPERSONCONTACT, value);
	}
  
    /**
     * Removes all values of property InvolvedPersonContact     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
	 *
	 * [Generated from RDFReactor template rule #removeall1static] 
     */
	public static void removeAllInvolvedPersonContact( Model model, org.ontoware.rdf2go.model.node.Resource instanceResource) {
		Base.removeAll(model, instanceResource, INVOLVEDPERSONCONTACT);
	}
	
    /**
     * Removes all values of property InvolvedPersonContact	 *
	 * [Generated from RDFReactor template rule #removeall1dynamic] 
     */
	public void removeAllInvolvedPersonContact() {
		Base.removeAll(this.model, this.getResource(), INVOLVEDPERSONCONTACT);
	}
     /**
     * Check if org.ontoware.rdfreactor.generator.java.JProperty@56530e4 has at least one value set 
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
     * @return true if this property has at least one value
	 *
	 * [Generated from RDFReactor template rule #get0has-static] 
     */
	public static boolean hasInvolvment(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource) {
		return Base.has(model, instanceResource, INVOLVMENT);
	}

    /**
     * Check if org.ontoware.rdfreactor.generator.java.JProperty@56530e4 has at least one value set 
     * @return true if this property has at least one value
	 *
	 * [Generated from RDFReactor template rule #get0has-dynamic] 
     */
	public boolean hasInvolvment() {
		return Base.has(this.model, this.getResource(), INVOLVMENT);
	}

    /**
     * Check if org.ontoware.rdfreactor.generator.java.JProperty@56530e4 has the given value (maybe among other values).  
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
	 * @param value the value to be checked
     * @return true if this property contains (maybe among other) the given value
	 *
	 * [Generated from RDFReactor template rule #get0has-value-static] 
     */
	public static boolean hasInvolvment(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource, org.ontoware.rdf2go.model.node.Node value ) {
		return Base.hasValue(model, instanceResource, INVOLVMENT);
	}

    /**
     * Check if org.ontoware.rdfreactor.generator.java.JProperty@56530e4 has the given value (maybe among other values).  
	 * @param value the value to be checked
     * @return true if this property contains (maybe among other) the given value
	 *
	 * [Generated from RDFReactor template rule #get0has-value-dynamic] 
     */
	public boolean hasInvolvment( org.ontoware.rdf2go.model.node.Node value ) {
		return Base.hasValue(this.model, this.getResource(), INVOLVMENT);
	}

     /**
     * Get all values of property Involvment as an Iterator over RDF2Go nodes 
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
     * @return a ClosableIterator of RDF2Go Nodes
	 *
	 * [Generated from RDFReactor template rule #get7static] 
     */
	public static ClosableIterator<org.ontoware.rdf2go.model.node.Node> getAllInvolvment_asNode(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource) {
		return Base.getAll_asNode(model, instanceResource, INVOLVMENT);
	}
	
    /**
     * Get all values of property Involvment as a ReactorResult of RDF2Go nodes 
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
     * @return a List of RDF2Go Nodes
	 *
	 * [Generated from RDFReactor template rule #get7static-reactor-result] 
     */
	public static ReactorResult<org.ontoware.rdf2go.model.node.Node> getAllInvolvment_asNode_(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource) {
		return Base.getAll_as(model, instanceResource, INVOLVMENT, org.ontoware.rdf2go.model.node.Node.class);
	}

    /**
     * Get all values of property Involvment as an Iterator over RDF2Go nodes 
     * @return a ClosableIterator of RDF2Go Nodes
	 *
	 * [Generated from RDFReactor template rule #get8dynamic] 
     */
	public ClosableIterator<org.ontoware.rdf2go.model.node.Node> getAllInvolvment_asNode() {
		return Base.getAll_asNode(this.model, this.getResource(), INVOLVMENT);
	}

    /**
     * Get all values of property Involvment as a ReactorResult of RDF2Go nodes 
     * @return a List of RDF2Go Nodes
	 *
	 * [Generated from RDFReactor template rule #get8dynamic-reactor-result] 
     */
	public ReactorResult<org.ontoware.rdf2go.model.node.Node> getAllInvolvment_asNode_() {
		return Base.getAll_as(this.model, this.getResource(), INVOLVMENT, org.ontoware.rdf2go.model.node.Node.class);
	}
     /**
     * Get all values of property Involvment     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
     * @return a ClosableIterator of $type
	 *
	 * [Generated from RDFReactor template rule #get11static] 
     */
	public static ClosableIterator<java.lang.String> getAllInvolvment(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource) {
		return Base.getAll(model, instanceResource, INVOLVMENT, java.lang.String.class);
	}
	
    /**
     * Get all values of property Involvment as a ReactorResult of java.lang.String 
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
     * @return a ReactorResult of $type which can conveniently be converted to iterator, list or array
	 *
	 * [Generated from RDFReactor template rule #get11static-reactorresult] 
     */
	public static ReactorResult<java.lang.String> getAllInvolvment_as(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource) {
		return Base.getAll_as(model, instanceResource, INVOLVMENT, java.lang.String.class);
	}

    /**
     * Get all values of property Involvment     * @return a ClosableIterator of $type
	 *
	 * [Generated from RDFReactor template rule #get12dynamic] 
     */
	public ClosableIterator<java.lang.String> getAllInvolvment() {
		return Base.getAll(this.model, this.getResource(), INVOLVMENT, java.lang.String.class);
	}

    /**
     * Get all values of property Involvment as a ReactorResult of java.lang.String 
     * @return a ReactorResult of $type which can conveniently be converted to iterator, list or array
	 *
	 * [Generated from RDFReactor template rule #get12dynamic-reactorresult] 
     */
	public ReactorResult<java.lang.String> getAllInvolvment_as() {
		return Base.getAll_as(this.model, this.getResource(), INVOLVMENT, java.lang.String.class);
	}
 
    /**
     * Adds a value to property Involvment as an RDF2Go node 
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
	 * @param value the value to be added
	 *
	 * [Generated from RDFReactor template rule #add1static] 
     */
	public static void addInvolvment(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource, org.ontoware.rdf2go.model.node.Node value) {
		Base.add(model, instanceResource, INVOLVMENT, value);
	}
	
    /**
     * Adds a value to property Involvment as an RDF2Go node 
	 * @param value the value to be added
	 *
	 * [Generated from RDFReactor template rule #add1dynamic] 
     */
	public void addInvolvment( org.ontoware.rdf2go.model.node.Node value) {
		Base.add(this.model, this.getResource(), INVOLVMENT, value);
	}
    /**
     * Adds a value to property Involvment from an instance of java.lang.String 
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
	 *
	 * [Generated from RDFReactor template rule #add3static] 
     */
	public static void addInvolvment(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource, java.lang.String value) {
		Base.add(model, instanceResource, INVOLVMENT, value);
	}
	
    /**
     * Adds a value to property Involvment from an instance of java.lang.String 
	 *
	 * [Generated from RDFReactor template rule #add4dynamic] 
     */
	public void addInvolvment(java.lang.String value) {
		Base.add(this.model, this.getResource(), INVOLVMENT, value);
	}
  

    /**
     * Sets a value of property Involvment from an RDF2Go node.
     * First, all existing values are removed, then this value is added.
     * Cardinality constraints are not checked, but this method exists only for properties with
     * no minCardinality or minCardinality == 1.
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
	 * @param value the value to be set
	 *
	 * [Generated from RDFReactor template rule #set1static] 
     */
	public static void setInvolvment( Model model, org.ontoware.rdf2go.model.node.Resource instanceResource, org.ontoware.rdf2go.model.node.Node value) {
		Base.set(model, instanceResource, INVOLVMENT, value);
	}
	
    /**
     * Sets a value of property Involvment from an RDF2Go node.
     * First, all existing values are removed, then this value is added.
     * Cardinality constraints are not checked, but this method exists only for properties with
     * no minCardinality or minCardinality == 1.
	 * @param value the value to be added
	 *
	 * [Generated from RDFReactor template rule #set1dynamic] 
     */
	public void setInvolvment( org.ontoware.rdf2go.model.node.Node value) {
		Base.set(this.model, this.getResource(), INVOLVMENT, value);
	}
    /**
     * Sets a value of property Involvment from an instance of java.lang.String 
     * First, all existing values are removed, then this value is added.
     * Cardinality constraints are not checked, but this method exists only for properties with
     * no minCardinality or minCardinality == 1.
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
	 * @param value the value to be added
	 *
	 * [Generated from RDFReactor template rule #set3static] 
     */
	public static void setInvolvment(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource, java.lang.String value) {
		Base.set(model, instanceResource, INVOLVMENT, value);
	}
	
    /**
     * Sets a value of property Involvment from an instance of java.lang.String 
     * First, all existing values are removed, then this value is added.
     * Cardinality constraints are not checked, but this method exists only for properties with
     * no minCardinality or minCardinality == 1.
	 * @param value the value to be added
	 *
	 * [Generated from RDFReactor template rule #set4dynamic] 
     */
	public void setInvolvment(java.lang.String value) {
		Base.set(this.model, this.getResource(), INVOLVMENT, value);
	}
  


    /**
     * Removes a value of property Involvment as an RDF2Go node 
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
	 * @param value the value to be removed
	 *
	 * [Generated from RDFReactor template rule #remove1static] 
     */
	public static void removeInvolvment( Model model, org.ontoware.rdf2go.model.node.Resource instanceResource, org.ontoware.rdf2go.model.node.Node value) {
		Base.remove(model, instanceResource, INVOLVMENT, value);
	}
	
    /**
     * Removes a value of property Involvment as an RDF2Go node
	 * @param value the value to be removed
	 *
	 * [Generated from RDFReactor template rule #remove1dynamic] 
     */
	public void removeInvolvment( org.ontoware.rdf2go.model.node.Node value) {
		Base.remove(this.model, this.getResource(), INVOLVMENT, value);
	}
    /**
     * Removes a value of property Involvment given as an instance of java.lang.String 
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
	 * @param value the value to be removed
	 *
	 * [Generated from RDFReactor template rule #remove3static] 
     */
	public static void removeInvolvment(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource, java.lang.String value) {
		Base.remove(model, instanceResource, INVOLVMENT, value);
	}
	
    /**
     * Removes a value of property Involvment given as an instance of java.lang.String 
	 * @param value the value to be removed
	 *
	 * [Generated from RDFReactor template rule #remove4dynamic] 
     */
	public void removeInvolvment(java.lang.String value) {
		Base.remove(this.model, this.getResource(), INVOLVMENT, value);
	}
  
    /**
     * Removes all values of property Involvment     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
	 *
	 * [Generated from RDFReactor template rule #removeall1static] 
     */
	public static void removeAllInvolvment( Model model, org.ontoware.rdf2go.model.node.Resource instanceResource) {
		Base.removeAll(model, instanceResource, INVOLVMENT);
	}
	
    /**
     * Removes all values of property Involvment	 *
	 * [Generated from RDFReactor template rule #removeall1dynamic] 
     */
	public void removeAllInvolvment() {
		Base.removeAll(this.model, this.getResource(), INVOLVMENT);
	}
 }