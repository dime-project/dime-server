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

package eu.dime.ps.semantic.model.pimo;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.BlankNode;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdfreactor.runtime.Base;
import org.ontoware.rdfreactor.runtime.ReactorResult;
import org.ontoware.rdfreactor.runtime.CardinalityException;


/**
 * This class manages access to these properties:
 * <ul>
 *   <li> Attendee </li>
 *   <li> Duration </li>
 * </ul>
 *
 * class- This class was generated by <a href="http://RDFReactor.semweb4j.org">RDFReactor</a> */
public class SocialEvent extends eu.dime.ps.semantic.model.pimo.Event {

    /** http://www.semanticdesktop.org/ontologies/2007/11/01/pimo#SocialEvent */
    @SuppressWarnings("hiding")
	public static final URI RDFS_CLASS = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/11/01/pimo#SocialEvent", false);

    /** http://www.semanticdesktop.org/ontologies/2007/11/01/pimo#attendee */
    @SuppressWarnings("hiding")
	public static final URI ATTENDEE = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/11/01/pimo#attendee",false);

    /** http://www.semanticdesktop.org/ontologies/2007/11/01/pimo#duration */
    @SuppressWarnings("hiding")
	public static final URI DURATION = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/11/01/pimo#duration",false);

    /** 
     * All property-URIs with this class as domain.
     * All properties of all super-classes are also available. 
     */
    @SuppressWarnings("hiding")
    public static final URI[] MANAGED_URIS = {
      new URIImpl("http://www.semanticdesktop.org/ontologies/2007/11/01/pimo#attendee",false),
      new URIImpl("http://www.semanticdesktop.org/ontologies/2007/11/01/pimo#duration",false) 
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
	protected SocialEvent (Model model, URI classURI, org.ontoware.rdf2go.model.node.Resource instanceIdentifier, boolean write) {
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
	public SocialEvent (Model model, org.ontoware.rdf2go.model.node.Resource instanceIdentifier, boolean write) {
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
	public SocialEvent (Model model, String uriString, boolean write) throws ModelRuntimeException {
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
	public SocialEvent (Model model, BlankNode bnode, boolean write) {
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
	public SocialEvent (Model model, boolean write) {
		super(model, RDFS_CLASS, model.newRandomUniqueURI(), write);
	}

    ///////////////////////////////////////////////////////////////////
    // typing

	/**
	 * Return an existing instance of this class in the model. No statements are written.
	 * @param model an RDF2Go model
	 * @param instanceResource an RDF2Go resource
	 * @return an instance of SocialEvent  or null if none existst
	 *
	 * [Generated from RDFReactor template rule #class0] 
	 */
	public static SocialEvent  getInstance(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource) {
		return Base.getInstance(model, instanceResource, SocialEvent.class);
	}

	/**
	 * Create a new instance of this class in the model. 
	 * That is, create the statement (instanceResource, RDF.type, http://www.semanticdesktop.org/ontologies/2007/11/01/pimo#SocialEvent).
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
	public static ReactorResult<? extends SocialEvent> getAllInstances_as(Model model) {
		return Base.getAllInstances_as(model, RDFS_CLASS, SocialEvent.class );
	}

    /**
	 * Remove rdf:type SocialEvent from this instance. Other triples are not affected.
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
	 * @return all A's as RDF resources, that have a relation 'AttendingMeeting' to this SocialEvent instance
	 *
	 * [Generated from RDFReactor template rule #getallinverse1static] 
	 */
	public static ClosableIterator<org.ontoware.rdf2go.model.node.Resource> getAllAttendingMeeting_Inverse(Model model, Object objectValue) {
		return Base.getAll_Inverse(model, eu.dime.ps.semantic.model.pimo.Attendee.ATTENDINGMEETING, objectValue);
	}

	/**
	 * @return all A's as RDF resources, that have a relation 'AttendingMeeting' to this SocialEvent instance
	 *
	 * [Generated from RDFReactor template rule #getallinverse1dynamic] 
	 */
	public ClosableIterator<org.ontoware.rdf2go.model.node.Resource> getAllAttendingMeeting_Inverse() {
		return Base.getAll_Inverse(this.model, eu.dime.ps.semantic.model.pimo.Attendee.ATTENDINGMEETING, this.getResource() );
	}

	/**
	 * @param model an RDF2Go model
	 * @param objectValue
	 * @return all A's as a ReactorResult, that have a relation 'AttendingMeeting' to this SocialEvent instance
	 *
	 * [Generated from RDFReactor template rule #getallinverse-as1static] 
	 */
	public static ReactorResult<org.ontoware.rdf2go.model.node.Resource> getAllAttendingMeeting_Inverse_as(Model model, Object objectValue) {
		return Base.getAll_Inverse_as(model, eu.dime.ps.semantic.model.pimo.Attendee.ATTENDINGMEETING, objectValue, org.ontoware.rdf2go.model.node.Resource.class);
	}


	/**
	 * @param model an RDF2Go model
	 * @param objectValue
	 * @return all A's as RDF resources, that have a relation 'Attends' to this SocialEvent instance
	 *
	 * [Generated from RDFReactor template rule #getallinverse1static] 
	 */
	public static ClosableIterator<org.ontoware.rdf2go.model.node.Resource> getAllAttends_Inverse(Model model, Object objectValue) {
		return Base.getAll_Inverse(model, eu.dime.ps.semantic.model.pimo.Person.ATTENDS, objectValue);
	}

	/**
	 * @return all A's as RDF resources, that have a relation 'Attends' to this SocialEvent instance
	 *
	 * [Generated from RDFReactor template rule #getallinverse1dynamic] 
	 */
	public ClosableIterator<org.ontoware.rdf2go.model.node.Resource> getAllAttends_Inverse() {
		return Base.getAll_Inverse(this.model, eu.dime.ps.semantic.model.pimo.Person.ATTENDS, this.getResource() );
	}

	/**
	 * @param model an RDF2Go model
	 * @param objectValue
	 * @return all A's as a ReactorResult, that have a relation 'Attends' to this SocialEvent instance
	 *
	 * [Generated from RDFReactor template rule #getallinverse-as1static] 
	 */
	public static ReactorResult<org.ontoware.rdf2go.model.node.Resource> getAllAttends_Inverse_as(Model model, Object objectValue) {
		return Base.getAll_Inverse_as(model, eu.dime.ps.semantic.model.pimo.Person.ATTENDS, objectValue, org.ontoware.rdf2go.model.node.Resource.class);
	}



    /**
     * Check if org.ontoware.rdfreactor.generator.java.JProperty@44d58a9c has at least one value set 
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
     * @return true if this property has at least one value
	 *
	 * [Generated from RDFReactor template rule #get0has-static] 
     */
	public static boolean hasAttendee(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource) {
		return Base.has(model, instanceResource, ATTENDEE);
	}

    /**
     * Check if org.ontoware.rdfreactor.generator.java.JProperty@44d58a9c has at least one value set 
     * @return true if this property has at least one value
	 *
	 * [Generated from RDFReactor template rule #get0has-dynamic] 
     */
	public boolean hasAttendee() {
		return Base.has(this.model, this.getResource(), ATTENDEE);
	}

    /**
     * Check if org.ontoware.rdfreactor.generator.java.JProperty@44d58a9c has the given value (maybe among other values).  
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
	 * @param value the value to be checked
     * @return true if this property contains (maybe among other) the given value
	 *
	 * [Generated from RDFReactor template rule #get0has-value-static] 
     */
	public static boolean hasAttendee(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource, org.ontoware.rdf2go.model.node.Node value ) {
		return Base.hasValue(model, instanceResource, ATTENDEE);
	}

    /**
     * Check if org.ontoware.rdfreactor.generator.java.JProperty@44d58a9c has the given value (maybe among other values).  
	 * @param value the value to be checked
     * @return true if this property contains (maybe among other) the given value
	 *
	 * [Generated from RDFReactor template rule #get0has-value-dynamic] 
     */
	public boolean hasAttendee( org.ontoware.rdf2go.model.node.Node value ) {
		return Base.hasValue(this.model, this.getResource(), ATTENDEE);
	}

     /**
     * Get all values of property Attendee as an Iterator over RDF2Go nodes 
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
     * @return a ClosableIterator of RDF2Go Nodes
	 *
	 * [Generated from RDFReactor template rule #get7static] 
     */
	public static ClosableIterator<org.ontoware.rdf2go.model.node.Node> getAllAttendee_asNode(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource) {
		return Base.getAll_asNode(model, instanceResource, ATTENDEE);
	}
	
    /**
     * Get all values of property Attendee as a ReactorResult of RDF2Go nodes 
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
     * @return a List of RDF2Go Nodes
	 *
	 * [Generated from RDFReactor template rule #get7static-reactor-result] 
     */
	public static ReactorResult<org.ontoware.rdf2go.model.node.Node> getAllAttendee_asNode_(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource) {
		return Base.getAll_as(model, instanceResource, ATTENDEE, org.ontoware.rdf2go.model.node.Node.class);
	}

    /**
     * Get all values of property Attendee as an Iterator over RDF2Go nodes 
     * @return a ClosableIterator of RDF2Go Nodes
	 *
	 * [Generated from RDFReactor template rule #get8dynamic] 
     */
	public ClosableIterator<org.ontoware.rdf2go.model.node.Node> getAllAttendee_asNode() {
		return Base.getAll_asNode(this.model, this.getResource(), ATTENDEE);
	}

    /**
     * Get all values of property Attendee as a ReactorResult of RDF2Go nodes 
     * @return a List of RDF2Go Nodes
	 *
	 * [Generated from RDFReactor template rule #get8dynamic-reactor-result] 
     */
	public ReactorResult<org.ontoware.rdf2go.model.node.Node> getAllAttendee_asNode_() {
		return Base.getAll_as(this.model, this.getResource(), ATTENDEE, org.ontoware.rdf2go.model.node.Node.class);
	}
     /**
     * Get all values of property Attendee     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
     * @return a ClosableIterator of $type
	 *
	 * [Generated from RDFReactor template rule #get11static] 
     */
	public static ClosableIterator<eu.dime.ps.semantic.model.pimo.Person> getAllAttendee(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource) {
		return Base.getAll(model, instanceResource, ATTENDEE, eu.dime.ps.semantic.model.pimo.Person.class);
	}
	
    /**
     * Get all values of property Attendee as a ReactorResult of Person 
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
     * @return a ReactorResult of $type which can conveniently be converted to iterator, list or array
	 *
	 * [Generated from RDFReactor template rule #get11static-reactorresult] 
     */
	public static ReactorResult<eu.dime.ps.semantic.model.pimo.Person> getAllAttendee_as(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource) {
		return Base.getAll_as(model, instanceResource, ATTENDEE, eu.dime.ps.semantic.model.pimo.Person.class);
	}

    /**
     * Get all values of property Attendee     * @return a ClosableIterator of $type
	 *
	 * [Generated from RDFReactor template rule #get12dynamic] 
     */
	public ClosableIterator<eu.dime.ps.semantic.model.pimo.Person> getAllAttendee() {
		return Base.getAll(this.model, this.getResource(), ATTENDEE, eu.dime.ps.semantic.model.pimo.Person.class);
	}

    /**
     * Get all values of property Attendee as a ReactorResult of Person 
     * @return a ReactorResult of $type which can conveniently be converted to iterator, list or array
	 *
	 * [Generated from RDFReactor template rule #get12dynamic-reactorresult] 
     */
	public ReactorResult<eu.dime.ps.semantic.model.pimo.Person> getAllAttendee_as() {
		return Base.getAll_as(this.model, this.getResource(), ATTENDEE, eu.dime.ps.semantic.model.pimo.Person.class);
	}
 
    /**
     * Adds a value to property Attendee as an RDF2Go node 
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
	 * @param value the value to be added
	 *
	 * [Generated from RDFReactor template rule #add1static] 
     */
	public static void addAttendee(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource, org.ontoware.rdf2go.model.node.Node value) {
		Base.add(model, instanceResource, ATTENDEE, value);
	}
	
    /**
     * Adds a value to property Attendee as an RDF2Go node 
	 * @param value the value to be added
	 *
	 * [Generated from RDFReactor template rule #add1dynamic] 
     */
	public void addAttendee( org.ontoware.rdf2go.model.node.Node value) {
		Base.add(this.model, this.getResource(), ATTENDEE, value);
	}
    /**
     * Adds a value to property Attendee from an instance of Person 
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
	 *
	 * [Generated from RDFReactor template rule #add3static] 
     */
	public static void addAttendee(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource, eu.dime.ps.semantic.model.pimo.Person value) {
		Base.add(model, instanceResource, ATTENDEE, value);
	}
	
    /**
     * Adds a value to property Attendee from an instance of Person 
	 *
	 * [Generated from RDFReactor template rule #add4dynamic] 
     */
	public void addAttendee(eu.dime.ps.semantic.model.pimo.Person value) {
		Base.add(this.model, this.getResource(), ATTENDEE, value);
	}
  

    /**
     * Sets a value of property Attendee from an RDF2Go node.
     * First, all existing values are removed, then this value is added.
     * Cardinality constraints are not checked, but this method exists only for properties with
     * no minCardinality or minCardinality == 1.
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
	 * @param value the value to be set
	 *
	 * [Generated from RDFReactor template rule #set1static] 
     */
	public static void setAttendee( Model model, org.ontoware.rdf2go.model.node.Resource instanceResource, org.ontoware.rdf2go.model.node.Node value) {
		Base.set(model, instanceResource, ATTENDEE, value);
	}
	
    /**
     * Sets a value of property Attendee from an RDF2Go node.
     * First, all existing values are removed, then this value is added.
     * Cardinality constraints are not checked, but this method exists only for properties with
     * no minCardinality or minCardinality == 1.
	 * @param value the value to be added
	 *
	 * [Generated from RDFReactor template rule #set1dynamic] 
     */
	public void setAttendee( org.ontoware.rdf2go.model.node.Node value) {
		Base.set(this.model, this.getResource(), ATTENDEE, value);
	}
    /**
     * Sets a value of property Attendee from an instance of Person 
     * First, all existing values are removed, then this value is added.
     * Cardinality constraints are not checked, but this method exists only for properties with
     * no minCardinality or minCardinality == 1.
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
	 * @param value the value to be added
	 *
	 * [Generated from RDFReactor template rule #set3static] 
     */
	public static void setAttendee(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource, eu.dime.ps.semantic.model.pimo.Person value) {
		Base.set(model, instanceResource, ATTENDEE, value);
	}
	
    /**
     * Sets a value of property Attendee from an instance of Person 
     * First, all existing values are removed, then this value is added.
     * Cardinality constraints are not checked, but this method exists only for properties with
     * no minCardinality or minCardinality == 1.
	 * @param value the value to be added
	 *
	 * [Generated from RDFReactor template rule #set4dynamic] 
     */
	public void setAttendee(eu.dime.ps.semantic.model.pimo.Person value) {
		Base.set(this.model, this.getResource(), ATTENDEE, value);
	}
  


    /**
     * Removes a value of property Attendee as an RDF2Go node 
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
	 * @param value the value to be removed
	 *
	 * [Generated from RDFReactor template rule #remove1static] 
     */
	public static void removeAttendee( Model model, org.ontoware.rdf2go.model.node.Resource instanceResource, org.ontoware.rdf2go.model.node.Node value) {
		Base.remove(model, instanceResource, ATTENDEE, value);
	}
	
    /**
     * Removes a value of property Attendee as an RDF2Go node
	 * @param value the value to be removed
	 *
	 * [Generated from RDFReactor template rule #remove1dynamic] 
     */
	public void removeAttendee( org.ontoware.rdf2go.model.node.Node value) {
		Base.remove(this.model, this.getResource(), ATTENDEE, value);
	}
    /**
     * Removes a value of property Attendee given as an instance of Person 
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
	 * @param value the value to be removed
	 *
	 * [Generated from RDFReactor template rule #remove3static] 
     */
	public static void removeAttendee(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource, eu.dime.ps.semantic.model.pimo.Person value) {
		Base.remove(model, instanceResource, ATTENDEE, value);
	}
	
    /**
     * Removes a value of property Attendee given as an instance of Person 
	 * @param value the value to be removed
	 *
	 * [Generated from RDFReactor template rule #remove4dynamic] 
     */
	public void removeAttendee(eu.dime.ps.semantic.model.pimo.Person value) {
		Base.remove(this.model, this.getResource(), ATTENDEE, value);
	}
  
    /**
     * Removes all values of property Attendee     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
	 *
	 * [Generated from RDFReactor template rule #removeall1static] 
     */
	public static void removeAllAttendee( Model model, org.ontoware.rdf2go.model.node.Resource instanceResource) {
		Base.removeAll(model, instanceResource, ATTENDEE);
	}
	
    /**
     * Removes all values of property Attendee	 *
	 * [Generated from RDFReactor template rule #removeall1dynamic] 
     */
	public void removeAllAttendee() {
		Base.removeAll(this.model, this.getResource(), ATTENDEE);
	}
     /**
     * Check if org.ontoware.rdfreactor.generator.java.JProperty@7ccf0951 has at least one value set 
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
     * @return true if this property has at least one value
	 *
	 * [Generated from RDFReactor template rule #get0has-static] 
     */
	public static boolean hasDuration(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource) {
		return Base.has(model, instanceResource, DURATION);
	}

    /**
     * Check if org.ontoware.rdfreactor.generator.java.JProperty@7ccf0951 has at least one value set 
     * @return true if this property has at least one value
	 *
	 * [Generated from RDFReactor template rule #get0has-dynamic] 
     */
	public boolean hasDuration() {
		return Base.has(this.model, this.getResource(), DURATION);
	}

    /**
     * Check if org.ontoware.rdfreactor.generator.java.JProperty@7ccf0951 has the given value (maybe among other values).  
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
	 * @param value the value to be checked
     * @return true if this property contains (maybe among other) the given value
	 *
	 * [Generated from RDFReactor template rule #get0has-value-static] 
     */
	public static boolean hasDuration(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource, org.ontoware.rdf2go.model.node.Node value ) {
		return Base.hasValue(model, instanceResource, DURATION);
	}

    /**
     * Check if org.ontoware.rdfreactor.generator.java.JProperty@7ccf0951 has the given value (maybe among other values).  
	 * @param value the value to be checked
     * @return true if this property contains (maybe among other) the given value
	 *
	 * [Generated from RDFReactor template rule #get0has-value-dynamic] 
     */
	public boolean hasDuration( org.ontoware.rdf2go.model.node.Node value ) {
		return Base.hasValue(this.model, this.getResource(), DURATION);
	}

    /**
     * Get single value of property Duration as an RDF2Go node 
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
     * @return the single value or null if no value is found
     * @throws RDFDataException at runtime, if the property has multiple values
	 *
	 * [Generated from RDFReactor template rule #get1static] 
     */
	public static org.ontoware.rdf2go.model.node.Node getDuration_asNode(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource) {
		return Base.get_asNode(model, instanceResource, DURATION);
	}
	
    /**
     * Get single value of property Duration as an RDF2Go node 
     * @return the single value or null if no value is found
     * @throws RDFDataException, if the property has multiple values
	 *
	 * [Generated from RDFReactor template rule #get2dynamic] 
     */
	public org.ontoware.rdf2go.model.node.Node getDuration_asNode() {
		return Base.get_asNode(this.model, this.getResource(), DURATION);
	}
     /**
     * Get single value of property Duration     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
     * @return the single value or null if no value is found
     * @throws RDFDataException, if the property has multiple values
	 *
	 * [Generated from RDFReactor template rule #get5static] 
     */
	public static org.ontoware.rdfreactor.schema.rdfs.Resource getDuration(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource) {
		return (org.ontoware.rdfreactor.schema.rdfs.Resource) Base.get(model, instanceResource, DURATION, org.ontoware.rdfreactor.schema.rdfs.Resource.class);
	}
	
    /**
     * Get single value of property Duration  .
     * This property has maxCardinality=1, that means the property takes only
     * a single value.
     * @return the single value or null if no value is found
     * @throws RDFDataException, if the property has multiple values
	 *
	 * [Generated from RDFReactor template rule #get6dynamic] 
     */
	public org.ontoware.rdfreactor.schema.rdfs.Resource getDuration() {
		return (org.ontoware.rdfreactor.schema.rdfs.Resource) Base.get(this.model, this.getResource(), DURATION, org.ontoware.rdfreactor.schema.rdfs.Resource.class);
	}
  
    /**
     * Adds a value to property Duration as an RDF2Go node 
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
	 * @param value the value to be added
	 * @throws CardinalityException if adding a value would bring the number 
	 *            of property values above the cardinality constraint.
	 *
	 * [Generated from RDFReactor template rule #add5static] 
     */
	public static void addDuration(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource, org.ontoware.rdf2go.model.node.Node value) 
    throws CardinalityException {
		Base.add(model, instanceResource, DURATION, value, 1);
	}
	
    /**
     * Adds a value to property Duration as an RDF2Go node 
	 * @param value the value to be added
	 * @throws CardinalityException if adding a value would bring the number 
	 *            of property values above the cardinality constraint.
	 *
	 * [Generated from RDFReactor template rule #add6dynamic] 
     */
	public void addDuration( org.ontoware.rdf2go.model.node.Node value) 
    throws CardinalityException {
		Base.add(this.model, this.getResource(), DURATION, value, 1);
	}
    /**
     * Adds a value to property Duration from an instance of org.ontoware.rdfreactor.schema.rdfs.Resource 
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
	 * @throws CardinalityException if adding a value would bring the number 
	 *            of property values above the cardinality constraint.
	 *
	 * [Generated from RDFReactor template rule #add7static] 
     */
	public static void addDuration(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource, org.ontoware.rdfreactor.schema.rdfs.Resource value)
    throws CardinalityException {
		Base.add(model, instanceResource, DURATION, value, 1);
	}
	
    /**
     * Adds a value to property Duration from an instance of org.ontoware.rdfreactor.schema.rdfs.Resource 
	 * @throws CardinalityException if adding a value would bring the number 
	 *            of property values above the cardinality constraint.
	 *
	 * [Generated from RDFReactor template rule #add8dynamic] 
     */
	public void addDuration(org.ontoware.rdfreactor.schema.rdfs.Resource value) 
    throws CardinalityException {
		Base.add(this.model, this.getResource(), DURATION, value, 1);
	}
 
 

    /**
     * Sets a value of property Duration from an RDF2Go node.
     * First, all existing values are removed, then this value is added.
     * Cardinality constraints are not checked, but this method exists only for properties with
     * no minCardinality or minCardinality == 1.
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
	 * @param value the value to be set
	 *
	 * [Generated from RDFReactor template rule #set1static] 
     */
	public static void setDuration( Model model, org.ontoware.rdf2go.model.node.Resource instanceResource, org.ontoware.rdf2go.model.node.Node value) {
		Base.set(model, instanceResource, DURATION, value);
	}
	
    /**
     * Sets a value of property Duration from an RDF2Go node.
     * First, all existing values are removed, then this value is added.
     * Cardinality constraints are not checked, but this method exists only for properties with
     * no minCardinality or minCardinality == 1.
	 * @param value the value to be added
	 *
	 * [Generated from RDFReactor template rule #set1dynamic] 
     */
	public void setDuration( org.ontoware.rdf2go.model.node.Node value) {
		Base.set(this.model, this.getResource(), DURATION, value);
	}
    /**
     * Sets a value of property Duration from an instance of org.ontoware.rdfreactor.schema.rdfs.Resource 
     * First, all existing values are removed, then this value is added.
     * Cardinality constraints are not checked, but this method exists only for properties with
     * no minCardinality or minCardinality == 1.
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
	 * @param value the value to be added
	 *
	 * [Generated from RDFReactor template rule #set3static] 
     */
	public static void setDuration(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource, org.ontoware.rdfreactor.schema.rdfs.Resource value) {
		Base.set(model, instanceResource, DURATION, value);
	}
	
    /**
     * Sets a value of property Duration from an instance of org.ontoware.rdfreactor.schema.rdfs.Resource 
     * First, all existing values are removed, then this value is added.
     * Cardinality constraints are not checked, but this method exists only for properties with
     * no minCardinality or minCardinality == 1.
	 * @param value the value to be added
	 *
	 * [Generated from RDFReactor template rule #set4dynamic] 
     */
	public void setDuration(org.ontoware.rdfreactor.schema.rdfs.Resource value) {
		Base.set(this.model, this.getResource(), DURATION, value);
	}
  


    /**
     * Removes a value of property Duration as an RDF2Go node 
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
	 * @param value the value to be removed
	 *
	 * [Generated from RDFReactor template rule #remove1static] 
     */
	public static void removeDuration( Model model, org.ontoware.rdf2go.model.node.Resource instanceResource, org.ontoware.rdf2go.model.node.Node value) {
		Base.remove(model, instanceResource, DURATION, value);
	}
	
    /**
     * Removes a value of property Duration as an RDF2Go node
	 * @param value the value to be removed
	 *
	 * [Generated from RDFReactor template rule #remove1dynamic] 
     */
	public void removeDuration( org.ontoware.rdf2go.model.node.Node value) {
		Base.remove(this.model, this.getResource(), DURATION, value);
	}
    /**
     * Removes a value of property Duration given as an instance of org.ontoware.rdfreactor.schema.rdfs.Resource 
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
	 * @param value the value to be removed
	 *
	 * [Generated from RDFReactor template rule #remove3static] 
     */
	public static void removeDuration(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource, org.ontoware.rdfreactor.schema.rdfs.Resource value) {
		Base.remove(model, instanceResource, DURATION, value);
	}
	
    /**
     * Removes a value of property Duration given as an instance of org.ontoware.rdfreactor.schema.rdfs.Resource 
	 * @param value the value to be removed
	 *
	 * [Generated from RDFReactor template rule #remove4dynamic] 
     */
	public void removeDuration(org.ontoware.rdfreactor.schema.rdfs.Resource value) {
		Base.remove(this.model, this.getResource(), DURATION, value);
	}
  
    /**
     * Removes all values of property Duration     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
	 *
	 * [Generated from RDFReactor template rule #removeall1static] 
     */
	public static void removeAllDuration( Model model, org.ontoware.rdf2go.model.node.Resource instanceResource) {
		Base.removeAll(model, instanceResource, DURATION);
	}
	
    /**
     * Removes all values of property Duration	 *
	 * [Generated from RDFReactor template rule #removeall1dynamic] 
     */
	public void removeAllDuration() {
		Base.removeAll(this.model, this.getResource(), DURATION);
	}
 }