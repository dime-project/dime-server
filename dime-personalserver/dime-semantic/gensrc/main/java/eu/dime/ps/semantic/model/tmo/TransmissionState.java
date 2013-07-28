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

package eu.dime.ps.semantic.model.tmo;

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
 *   <li> TransmissionStateChangesFrom </li>
 *   <li> TransmissionStateChangesTo </li>
 * </ul>
 *
 * class- This class was generated by <a href="http://RDFReactor.semweb4j.org">RDFReactor</a> */
public class TransmissionState extends eu.dime.ps.semantic.model.tmo.StateTypeRole {

    /** http://www.semanticdesktop.org/ontologies/2008/05/20/tmo#TransmissionState */
    @SuppressWarnings("hiding")
	public static final URI RDFS_CLASS = new URIImpl("http://www.semanticdesktop.org/ontologies/2008/05/20/tmo#TransmissionState", false);

    /** http://www.semanticdesktop.org/ontologies/2008/05/20/tmo#transmissionStateChangesFrom */
    @SuppressWarnings("hiding")
	public static final URI TRANSMISSIONSTATECHANGESFROM = new URIImpl("http://www.semanticdesktop.org/ontologies/2008/05/20/tmo#transmissionStateChangesFrom",false);

    /** http://www.semanticdesktop.org/ontologies/2008/05/20/tmo#transmissionStateChangesTo */
    @SuppressWarnings("hiding")
	public static final URI TRANSMISSIONSTATECHANGESTO = new URIImpl("http://www.semanticdesktop.org/ontologies/2008/05/20/tmo#transmissionStateChangesTo",false);

    /** 
     * All property-URIs with this class as domain.
     * All properties of all super-classes are also available. 
     */
    @SuppressWarnings("hiding")
    public static final URI[] MANAGED_URIS = {
      new URIImpl("http://www.semanticdesktop.org/ontologies/2008/05/20/tmo#transmissionStateChangesFrom",false),
      new URIImpl("http://www.semanticdesktop.org/ontologies/2008/05/20/tmo#transmissionStateChangesTo",false) 
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
	protected TransmissionState (Model model, URI classURI, org.ontoware.rdf2go.model.node.Resource instanceIdentifier, boolean write) {
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
	public TransmissionState (Model model, org.ontoware.rdf2go.model.node.Resource instanceIdentifier, boolean write) {
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
	public TransmissionState (Model model, String uriString, boolean write) throws ModelRuntimeException {
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
	public TransmissionState (Model model, BlankNode bnode, boolean write) {
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
	public TransmissionState (Model model, boolean write) {
		super(model, RDFS_CLASS, model.newRandomUniqueURI(), write);
	}

    ///////////////////////////////////////////////////////////////////
    // typing

	/**
	 * Return an existing instance of this class in the model. No statements are written.
	 * @param model an RDF2Go model
	 * @param instanceResource an RDF2Go resource
	 * @return an instance of TransmissionState  or null if none existst
	 *
	 * [Generated from RDFReactor template rule #class0] 
	 */
	public static TransmissionState  getInstance(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource) {
		return Base.getInstance(model, instanceResource, TransmissionState.class);
	}

	/**
	 * Create a new instance of this class in the model. 
	 * That is, create the statement (instanceResource, RDF.type, http://www.semanticdesktop.org/ontologies/2008/05/20/tmo#TransmissionState).
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
	public static ReactorResult<? extends TransmissionState> getAllInstances_as(Model model) {
		return Base.getAllInstances_as(model, RDFS_CLASS, TransmissionState.class );
	}

    /**
	 * Remove rdf:type TransmissionState from this instance. Other triples are not affected.
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
	 * @return all A's as RDF resources, that have a relation 'TransmissionState' to this TransmissionState instance
	 *
	 * [Generated from RDFReactor template rule #getallinverse1static] 
	 */
	public static ClosableIterator<org.ontoware.rdf2go.model.node.Resource> getAllTransmissionState_Inverse(Model model, Object objectValue) {
		return Base.getAll_Inverse(model, eu.dime.ps.semantic.model.tmo.TaskTransmission.TRANSMISSIONSTATE, objectValue);
	}

	/**
	 * @return all A's as RDF resources, that have a relation 'TransmissionState' to this TransmissionState instance
	 *
	 * [Generated from RDFReactor template rule #getallinverse1dynamic] 
	 */
	public ClosableIterator<org.ontoware.rdf2go.model.node.Resource> getAllTransmissionState_Inverse() {
		return Base.getAll_Inverse(this.model, eu.dime.ps.semantic.model.tmo.TaskTransmission.TRANSMISSIONSTATE, this.getResource() );
	}

	/**
	 * @param model an RDF2Go model
	 * @param objectValue
	 * @return all A's as a ReactorResult, that have a relation 'TransmissionState' to this TransmissionState instance
	 *
	 * [Generated from RDFReactor template rule #getallinverse-as1static] 
	 */
	public static ReactorResult<org.ontoware.rdf2go.model.node.Resource> getAllTransmissionState_Inverse_as(Model model, Object objectValue) {
		return Base.getAll_Inverse_as(model, eu.dime.ps.semantic.model.tmo.TaskTransmission.TRANSMISSIONSTATE, objectValue, org.ontoware.rdf2go.model.node.Resource.class);
	}


	/**
	 * @param model an RDF2Go model
	 * @param objectValue
	 * @return all A's as RDF resources, that have a relation 'TransmissionStateChangesFrom' to this TransmissionState instance
	 *
	 * [Generated from RDFReactor template rule #getallinverse1static] 
	 */
	public static ClosableIterator<org.ontoware.rdf2go.model.node.Resource> getAllTransmissionStateChangesFrom_Inverse(Model model, Object objectValue) {
		return Base.getAll_Inverse(model, eu.dime.ps.semantic.model.tmo.TransmissionState.TRANSMISSIONSTATECHANGESFROM, objectValue);
	}

	/**
	 * @return all A's as RDF resources, that have a relation 'TransmissionStateChangesFrom' to this TransmissionState instance
	 *
	 * [Generated from RDFReactor template rule #getallinverse1dynamic] 
	 */
	public ClosableIterator<org.ontoware.rdf2go.model.node.Resource> getAllTransmissionStateChangesFrom_Inverse() {
		return Base.getAll_Inverse(this.model, eu.dime.ps.semantic.model.tmo.TransmissionState.TRANSMISSIONSTATECHANGESFROM, this.getResource() );
	}

	/**
	 * @param model an RDF2Go model
	 * @param objectValue
	 * @return all A's as a ReactorResult, that have a relation 'TransmissionStateChangesFrom' to this TransmissionState instance
	 *
	 * [Generated from RDFReactor template rule #getallinverse-as1static] 
	 */
	public static ReactorResult<org.ontoware.rdf2go.model.node.Resource> getAllTransmissionStateChangesFrom_Inverse_as(Model model, Object objectValue) {
		return Base.getAll_Inverse_as(model, eu.dime.ps.semantic.model.tmo.TransmissionState.TRANSMISSIONSTATECHANGESFROM, objectValue, org.ontoware.rdf2go.model.node.Resource.class);
	}


	/**
	 * @param model an RDF2Go model
	 * @param objectValue
	 * @return all A's as RDF resources, that have a relation 'TransmissionStateChangesTo' to this TransmissionState instance
	 *
	 * [Generated from RDFReactor template rule #getallinverse1static] 
	 */
	public static ClosableIterator<org.ontoware.rdf2go.model.node.Resource> getAllTransmissionStateChangesTo_Inverse(Model model, Object objectValue) {
		return Base.getAll_Inverse(model, eu.dime.ps.semantic.model.tmo.TransmissionState.TRANSMISSIONSTATECHANGESTO, objectValue);
	}

	/**
	 * @return all A's as RDF resources, that have a relation 'TransmissionStateChangesTo' to this TransmissionState instance
	 *
	 * [Generated from RDFReactor template rule #getallinverse1dynamic] 
	 */
	public ClosableIterator<org.ontoware.rdf2go.model.node.Resource> getAllTransmissionStateChangesTo_Inverse() {
		return Base.getAll_Inverse(this.model, eu.dime.ps.semantic.model.tmo.TransmissionState.TRANSMISSIONSTATECHANGESTO, this.getResource() );
	}

	/**
	 * @param model an RDF2Go model
	 * @param objectValue
	 * @return all A's as a ReactorResult, that have a relation 'TransmissionStateChangesTo' to this TransmissionState instance
	 *
	 * [Generated from RDFReactor template rule #getallinverse-as1static] 
	 */
	public static ReactorResult<org.ontoware.rdf2go.model.node.Resource> getAllTransmissionStateChangesTo_Inverse_as(Model model, Object objectValue) {
		return Base.getAll_Inverse_as(model, eu.dime.ps.semantic.model.tmo.TransmissionState.TRANSMISSIONSTATECHANGESTO, objectValue, org.ontoware.rdf2go.model.node.Resource.class);
	}



    /**
     * Check if org.ontoware.rdfreactor.generator.java.JProperty@3a2bb9b6 has at least one value set 
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
     * @return true if this property has at least one value
	 *
	 * [Generated from RDFReactor template rule #get0has-static] 
     */
	public static boolean hasTransmissionStateChangesFrom(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource) {
		return Base.has(model, instanceResource, TRANSMISSIONSTATECHANGESFROM);
	}

    /**
     * Check if org.ontoware.rdfreactor.generator.java.JProperty@3a2bb9b6 has at least one value set 
     * @return true if this property has at least one value
	 *
	 * [Generated from RDFReactor template rule #get0has-dynamic] 
     */
	public boolean hasTransmissionStateChangesFrom() {
		return Base.has(this.model, this.getResource(), TRANSMISSIONSTATECHANGESFROM);
	}

    /**
     * Check if org.ontoware.rdfreactor.generator.java.JProperty@3a2bb9b6 has the given value (maybe among other values).  
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
	 * @param value the value to be checked
     * @return true if this property contains (maybe among other) the given value
	 *
	 * [Generated from RDFReactor template rule #get0has-value-static] 
     */
	public static boolean hasTransmissionStateChangesFrom(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource, org.ontoware.rdf2go.model.node.Node value ) {
		return Base.hasValue(model, instanceResource, TRANSMISSIONSTATECHANGESFROM);
	}

    /**
     * Check if org.ontoware.rdfreactor.generator.java.JProperty@3a2bb9b6 has the given value (maybe among other values).  
	 * @param value the value to be checked
     * @return true if this property contains (maybe among other) the given value
	 *
	 * [Generated from RDFReactor template rule #get0has-value-dynamic] 
     */
	public boolean hasTransmissionStateChangesFrom( org.ontoware.rdf2go.model.node.Node value ) {
		return Base.hasValue(this.model, this.getResource(), TRANSMISSIONSTATECHANGESFROM);
	}

    /**
     * Get single value of property TransmissionStateChangesFrom as an RDF2Go node 
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
     * @return the single value or null if no value is found
     * @throws RDFDataException at runtime, if the property has multiple values
	 *
	 * [Generated from RDFReactor template rule #get1static] 
     */
	public static org.ontoware.rdf2go.model.node.Node getTransmissionStateChangesFrom_asNode(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource) {
		return Base.get_asNode(model, instanceResource, TRANSMISSIONSTATECHANGESFROM);
	}
	
    /**
     * Get single value of property TransmissionStateChangesFrom as an RDF2Go node 
     * @return the single value or null if no value is found
     * @throws RDFDataException, if the property has multiple values
	 *
	 * [Generated from RDFReactor template rule #get2dynamic] 
     */
	public org.ontoware.rdf2go.model.node.Node getTransmissionStateChangesFrom_asNode() {
		return Base.get_asNode(this.model, this.getResource(), TRANSMISSIONSTATECHANGESFROM);
	}
     /**
     * Get single value of property TransmissionStateChangesFrom     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
     * @return the single value or null if no value is found
     * @throws RDFDataException, if the property has multiple values
	 *
	 * [Generated from RDFReactor template rule #get5static] 
     */
	public static eu.dime.ps.semantic.model.tmo.TransmissionState getTransmissionStateChangesFrom(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource) {
		return (eu.dime.ps.semantic.model.tmo.TransmissionState) Base.get(model, instanceResource, TRANSMISSIONSTATECHANGESFROM, eu.dime.ps.semantic.model.tmo.TransmissionState.class);
	}
	
    /**
     * Get single value of property TransmissionStateChangesFrom  .
     * This property has maxCardinality=1, that means the property takes only
     * a single value.
     * @return the single value or null if no value is found
     * @throws RDFDataException, if the property has multiple values
	 *
	 * [Generated from RDFReactor template rule #get6dynamic] 
     */
	public eu.dime.ps.semantic.model.tmo.TransmissionState getTransmissionStateChangesFrom() {
		return (eu.dime.ps.semantic.model.tmo.TransmissionState) Base.get(this.model, this.getResource(), TRANSMISSIONSTATECHANGESFROM, eu.dime.ps.semantic.model.tmo.TransmissionState.class);
	}
  
    /**
     * Adds a value to property TransmissionStateChangesFrom as an RDF2Go node 
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
	 * @param value the value to be added
	 * @throws CardinalityException if adding a value would bring the number 
	 *            of property values above the cardinality constraint.
	 *
	 * [Generated from RDFReactor template rule #add5static] 
     */
	public static void addTransmissionStateChangesFrom(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource, org.ontoware.rdf2go.model.node.Node value) 
    throws CardinalityException {
		Base.add(model, instanceResource, TRANSMISSIONSTATECHANGESFROM, value, 1);
	}
	
    /**
     * Adds a value to property TransmissionStateChangesFrom as an RDF2Go node 
	 * @param value the value to be added
	 * @throws CardinalityException if adding a value would bring the number 
	 *            of property values above the cardinality constraint.
	 *
	 * [Generated from RDFReactor template rule #add6dynamic] 
     */
	public void addTransmissionStateChangesFrom( org.ontoware.rdf2go.model.node.Node value) 
    throws CardinalityException {
		Base.add(this.model, this.getResource(), TRANSMISSIONSTATECHANGESFROM, value, 1);
	}
    /**
     * Adds a value to property TransmissionStateChangesFrom from an instance of TransmissionState 
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
	 * @throws CardinalityException if adding a value would bring the number 
	 *            of property values above the cardinality constraint.
	 *
	 * [Generated from RDFReactor template rule #add7static] 
     */
	public static void addTransmissionStateChangesFrom(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource, eu.dime.ps.semantic.model.tmo.TransmissionState value)
    throws CardinalityException {
		Base.add(model, instanceResource, TRANSMISSIONSTATECHANGESFROM, value, 1);
	}
	
    /**
     * Adds a value to property TransmissionStateChangesFrom from an instance of TransmissionState 
	 * @throws CardinalityException if adding a value would bring the number 
	 *            of property values above the cardinality constraint.
	 *
	 * [Generated from RDFReactor template rule #add8dynamic] 
     */
	public void addTransmissionStateChangesFrom(eu.dime.ps.semantic.model.tmo.TransmissionState value) 
    throws CardinalityException {
		Base.add(this.model, this.getResource(), TRANSMISSIONSTATECHANGESFROM, value, 1);
	}
 
 

    /**
     * Sets a value of property TransmissionStateChangesFrom from an RDF2Go node.
     * First, all existing values are removed, then this value is added.
     * Cardinality constraints are not checked, but this method exists only for properties with
     * no minCardinality or minCardinality == 1.
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
	 * @param value the value to be set
	 *
	 * [Generated from RDFReactor template rule #set1static] 
     */
	public static void setTransmissionStateChangesFrom( Model model, org.ontoware.rdf2go.model.node.Resource instanceResource, org.ontoware.rdf2go.model.node.Node value) {
		Base.set(model, instanceResource, TRANSMISSIONSTATECHANGESFROM, value);
	}
	
    /**
     * Sets a value of property TransmissionStateChangesFrom from an RDF2Go node.
     * First, all existing values are removed, then this value is added.
     * Cardinality constraints are not checked, but this method exists only for properties with
     * no minCardinality or minCardinality == 1.
	 * @param value the value to be added
	 *
	 * [Generated from RDFReactor template rule #set1dynamic] 
     */
	public void setTransmissionStateChangesFrom( org.ontoware.rdf2go.model.node.Node value) {
		Base.set(this.model, this.getResource(), TRANSMISSIONSTATECHANGESFROM, value);
	}
    /**
     * Sets a value of property TransmissionStateChangesFrom from an instance of TransmissionState 
     * First, all existing values are removed, then this value is added.
     * Cardinality constraints are not checked, but this method exists only for properties with
     * no minCardinality or minCardinality == 1.
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
	 * @param value the value to be added
	 *
	 * [Generated from RDFReactor template rule #set3static] 
     */
	public static void setTransmissionStateChangesFrom(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource, eu.dime.ps.semantic.model.tmo.TransmissionState value) {
		Base.set(model, instanceResource, TRANSMISSIONSTATECHANGESFROM, value);
	}
	
    /**
     * Sets a value of property TransmissionStateChangesFrom from an instance of TransmissionState 
     * First, all existing values are removed, then this value is added.
     * Cardinality constraints are not checked, but this method exists only for properties with
     * no minCardinality or minCardinality == 1.
	 * @param value the value to be added
	 *
	 * [Generated from RDFReactor template rule #set4dynamic] 
     */
	public void setTransmissionStateChangesFrom(eu.dime.ps.semantic.model.tmo.TransmissionState value) {
		Base.set(this.model, this.getResource(), TRANSMISSIONSTATECHANGESFROM, value);
	}
  


    /**
     * Removes a value of property TransmissionStateChangesFrom as an RDF2Go node 
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
	 * @param value the value to be removed
	 *
	 * [Generated from RDFReactor template rule #remove1static] 
     */
	public static void removeTransmissionStateChangesFrom( Model model, org.ontoware.rdf2go.model.node.Resource instanceResource, org.ontoware.rdf2go.model.node.Node value) {
		Base.remove(model, instanceResource, TRANSMISSIONSTATECHANGESFROM, value);
	}
	
    /**
     * Removes a value of property TransmissionStateChangesFrom as an RDF2Go node
	 * @param value the value to be removed
	 *
	 * [Generated from RDFReactor template rule #remove1dynamic] 
     */
	public void removeTransmissionStateChangesFrom( org.ontoware.rdf2go.model.node.Node value) {
		Base.remove(this.model, this.getResource(), TRANSMISSIONSTATECHANGESFROM, value);
	}
    /**
     * Removes a value of property TransmissionStateChangesFrom given as an instance of TransmissionState 
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
	 * @param value the value to be removed
	 *
	 * [Generated from RDFReactor template rule #remove3static] 
     */
	public static void removeTransmissionStateChangesFrom(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource, eu.dime.ps.semantic.model.tmo.TransmissionState value) {
		Base.remove(model, instanceResource, TRANSMISSIONSTATECHANGESFROM, value);
	}
	
    /**
     * Removes a value of property TransmissionStateChangesFrom given as an instance of TransmissionState 
	 * @param value the value to be removed
	 *
	 * [Generated from RDFReactor template rule #remove4dynamic] 
     */
	public void removeTransmissionStateChangesFrom(eu.dime.ps.semantic.model.tmo.TransmissionState value) {
		Base.remove(this.model, this.getResource(), TRANSMISSIONSTATECHANGESFROM, value);
	}
  
    /**
     * Removes all values of property TransmissionStateChangesFrom     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
	 *
	 * [Generated from RDFReactor template rule #removeall1static] 
     */
	public static void removeAllTransmissionStateChangesFrom( Model model, org.ontoware.rdf2go.model.node.Resource instanceResource) {
		Base.removeAll(model, instanceResource, TRANSMISSIONSTATECHANGESFROM);
	}
	
    /**
     * Removes all values of property TransmissionStateChangesFrom	 *
	 * [Generated from RDFReactor template rule #removeall1dynamic] 
     */
	public void removeAllTransmissionStateChangesFrom() {
		Base.removeAll(this.model, this.getResource(), TRANSMISSIONSTATECHANGESFROM);
	}
     /**
     * Check if org.ontoware.rdfreactor.generator.java.JProperty@5c5cd9cc has at least one value set 
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
     * @return true if this property has at least one value
	 *
	 * [Generated from RDFReactor template rule #get0has-static] 
     */
	public static boolean hasTransmissionStateChangesTo(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource) {
		return Base.has(model, instanceResource, TRANSMISSIONSTATECHANGESTO);
	}

    /**
     * Check if org.ontoware.rdfreactor.generator.java.JProperty@5c5cd9cc has at least one value set 
     * @return true if this property has at least one value
	 *
	 * [Generated from RDFReactor template rule #get0has-dynamic] 
     */
	public boolean hasTransmissionStateChangesTo() {
		return Base.has(this.model, this.getResource(), TRANSMISSIONSTATECHANGESTO);
	}

    /**
     * Check if org.ontoware.rdfreactor.generator.java.JProperty@5c5cd9cc has the given value (maybe among other values).  
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
	 * @param value the value to be checked
     * @return true if this property contains (maybe among other) the given value
	 *
	 * [Generated from RDFReactor template rule #get0has-value-static] 
     */
	public static boolean hasTransmissionStateChangesTo(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource, org.ontoware.rdf2go.model.node.Node value ) {
		return Base.hasValue(model, instanceResource, TRANSMISSIONSTATECHANGESTO);
	}

    /**
     * Check if org.ontoware.rdfreactor.generator.java.JProperty@5c5cd9cc has the given value (maybe among other values).  
	 * @param value the value to be checked
     * @return true if this property contains (maybe among other) the given value
	 *
	 * [Generated from RDFReactor template rule #get0has-value-dynamic] 
     */
	public boolean hasTransmissionStateChangesTo( org.ontoware.rdf2go.model.node.Node value ) {
		return Base.hasValue(this.model, this.getResource(), TRANSMISSIONSTATECHANGESTO);
	}

    /**
     * Get single value of property TransmissionStateChangesTo as an RDF2Go node 
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
     * @return the single value or null if no value is found
     * @throws RDFDataException at runtime, if the property has multiple values
	 *
	 * [Generated from RDFReactor template rule #get1static] 
     */
	public static org.ontoware.rdf2go.model.node.Node getTransmissionStateChangesTo_asNode(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource) {
		return Base.get_asNode(model, instanceResource, TRANSMISSIONSTATECHANGESTO);
	}
	
    /**
     * Get single value of property TransmissionStateChangesTo as an RDF2Go node 
     * @return the single value or null if no value is found
     * @throws RDFDataException, if the property has multiple values
	 *
	 * [Generated from RDFReactor template rule #get2dynamic] 
     */
	public org.ontoware.rdf2go.model.node.Node getTransmissionStateChangesTo_asNode() {
		return Base.get_asNode(this.model, this.getResource(), TRANSMISSIONSTATECHANGESTO);
	}
     /**
     * Get single value of property TransmissionStateChangesTo     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
     * @return the single value or null if no value is found
     * @throws RDFDataException, if the property has multiple values
	 *
	 * [Generated from RDFReactor template rule #get5static] 
     */
	public static eu.dime.ps.semantic.model.tmo.TransmissionState getTransmissionStateChangesTo(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource) {
		return (eu.dime.ps.semantic.model.tmo.TransmissionState) Base.get(model, instanceResource, TRANSMISSIONSTATECHANGESTO, eu.dime.ps.semantic.model.tmo.TransmissionState.class);
	}
	
    /**
     * Get single value of property TransmissionStateChangesTo  .
     * This property has maxCardinality=1, that means the property takes only
     * a single value.
     * @return the single value or null if no value is found
     * @throws RDFDataException, if the property has multiple values
	 *
	 * [Generated from RDFReactor template rule #get6dynamic] 
     */
	public eu.dime.ps.semantic.model.tmo.TransmissionState getTransmissionStateChangesTo() {
		return (eu.dime.ps.semantic.model.tmo.TransmissionState) Base.get(this.model, this.getResource(), TRANSMISSIONSTATECHANGESTO, eu.dime.ps.semantic.model.tmo.TransmissionState.class);
	}
  
    /**
     * Adds a value to property TransmissionStateChangesTo as an RDF2Go node 
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
	 * @param value the value to be added
	 * @throws CardinalityException if adding a value would bring the number 
	 *            of property values above the cardinality constraint.
	 *
	 * [Generated from RDFReactor template rule #add5static] 
     */
	public static void addTransmissionStateChangesTo(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource, org.ontoware.rdf2go.model.node.Node value) 
    throws CardinalityException {
		Base.add(model, instanceResource, TRANSMISSIONSTATECHANGESTO, value, 1);
	}
	
    /**
     * Adds a value to property TransmissionStateChangesTo as an RDF2Go node 
	 * @param value the value to be added
	 * @throws CardinalityException if adding a value would bring the number 
	 *            of property values above the cardinality constraint.
	 *
	 * [Generated from RDFReactor template rule #add6dynamic] 
     */
	public void addTransmissionStateChangesTo( org.ontoware.rdf2go.model.node.Node value) 
    throws CardinalityException {
		Base.add(this.model, this.getResource(), TRANSMISSIONSTATECHANGESTO, value, 1);
	}
    /**
     * Adds a value to property TransmissionStateChangesTo from an instance of TransmissionState 
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
	 * @throws CardinalityException if adding a value would bring the number 
	 *            of property values above the cardinality constraint.
	 *
	 * [Generated from RDFReactor template rule #add7static] 
     */
	public static void addTransmissionStateChangesTo(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource, eu.dime.ps.semantic.model.tmo.TransmissionState value)
    throws CardinalityException {
		Base.add(model, instanceResource, TRANSMISSIONSTATECHANGESTO, value, 1);
	}
	
    /**
     * Adds a value to property TransmissionStateChangesTo from an instance of TransmissionState 
	 * @throws CardinalityException if adding a value would bring the number 
	 *            of property values above the cardinality constraint.
	 *
	 * [Generated from RDFReactor template rule #add8dynamic] 
     */
	public void addTransmissionStateChangesTo(eu.dime.ps.semantic.model.tmo.TransmissionState value) 
    throws CardinalityException {
		Base.add(this.model, this.getResource(), TRANSMISSIONSTATECHANGESTO, value, 1);
	}
 
 

    /**
     * Sets a value of property TransmissionStateChangesTo from an RDF2Go node.
     * First, all existing values are removed, then this value is added.
     * Cardinality constraints are not checked, but this method exists only for properties with
     * no minCardinality or minCardinality == 1.
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
	 * @param value the value to be set
	 *
	 * [Generated from RDFReactor template rule #set1static] 
     */
	public static void setTransmissionStateChangesTo( Model model, org.ontoware.rdf2go.model.node.Resource instanceResource, org.ontoware.rdf2go.model.node.Node value) {
		Base.set(model, instanceResource, TRANSMISSIONSTATECHANGESTO, value);
	}
	
    /**
     * Sets a value of property TransmissionStateChangesTo from an RDF2Go node.
     * First, all existing values are removed, then this value is added.
     * Cardinality constraints are not checked, but this method exists only for properties with
     * no minCardinality or minCardinality == 1.
	 * @param value the value to be added
	 *
	 * [Generated from RDFReactor template rule #set1dynamic] 
     */
	public void setTransmissionStateChangesTo( org.ontoware.rdf2go.model.node.Node value) {
		Base.set(this.model, this.getResource(), TRANSMISSIONSTATECHANGESTO, value);
	}
    /**
     * Sets a value of property TransmissionStateChangesTo from an instance of TransmissionState 
     * First, all existing values are removed, then this value is added.
     * Cardinality constraints are not checked, but this method exists only for properties with
     * no minCardinality or minCardinality == 1.
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
	 * @param value the value to be added
	 *
	 * [Generated from RDFReactor template rule #set3static] 
     */
	public static void setTransmissionStateChangesTo(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource, eu.dime.ps.semantic.model.tmo.TransmissionState value) {
		Base.set(model, instanceResource, TRANSMISSIONSTATECHANGESTO, value);
	}
	
    /**
     * Sets a value of property TransmissionStateChangesTo from an instance of TransmissionState 
     * First, all existing values are removed, then this value is added.
     * Cardinality constraints are not checked, but this method exists only for properties with
     * no minCardinality or minCardinality == 1.
	 * @param value the value to be added
	 *
	 * [Generated from RDFReactor template rule #set4dynamic] 
     */
	public void setTransmissionStateChangesTo(eu.dime.ps.semantic.model.tmo.TransmissionState value) {
		Base.set(this.model, this.getResource(), TRANSMISSIONSTATECHANGESTO, value);
	}
  


    /**
     * Removes a value of property TransmissionStateChangesTo as an RDF2Go node 
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
	 * @param value the value to be removed
	 *
	 * [Generated from RDFReactor template rule #remove1static] 
     */
	public static void removeTransmissionStateChangesTo( Model model, org.ontoware.rdf2go.model.node.Resource instanceResource, org.ontoware.rdf2go.model.node.Node value) {
		Base.remove(model, instanceResource, TRANSMISSIONSTATECHANGESTO, value);
	}
	
    /**
     * Removes a value of property TransmissionStateChangesTo as an RDF2Go node
	 * @param value the value to be removed
	 *
	 * [Generated from RDFReactor template rule #remove1dynamic] 
     */
	public void removeTransmissionStateChangesTo( org.ontoware.rdf2go.model.node.Node value) {
		Base.remove(this.model, this.getResource(), TRANSMISSIONSTATECHANGESTO, value);
	}
    /**
     * Removes a value of property TransmissionStateChangesTo given as an instance of TransmissionState 
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
	 * @param value the value to be removed
	 *
	 * [Generated from RDFReactor template rule #remove3static] 
     */
	public static void removeTransmissionStateChangesTo(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource, eu.dime.ps.semantic.model.tmo.TransmissionState value) {
		Base.remove(model, instanceResource, TRANSMISSIONSTATECHANGESTO, value);
	}
	
    /**
     * Removes a value of property TransmissionStateChangesTo given as an instance of TransmissionState 
	 * @param value the value to be removed
	 *
	 * [Generated from RDFReactor template rule #remove4dynamic] 
     */
	public void removeTransmissionStateChangesTo(eu.dime.ps.semantic.model.tmo.TransmissionState value) {
		Base.remove(this.model, this.getResource(), TRANSMISSIONSTATECHANGESTO, value);
	}
  
    /**
     * Removes all values of property TransmissionStateChangesTo     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
	 *
	 * [Generated from RDFReactor template rule #removeall1static] 
     */
	public static void removeAllTransmissionStateChangesTo( Model model, org.ontoware.rdf2go.model.node.Resource instanceResource) {
		Base.removeAll(model, instanceResource, TRANSMISSIONSTATECHANGESTO);
	}
	
    /**
     * Removes all values of property TransmissionStateChangesTo	 *
	 * [Generated from RDFReactor template rule #removeall1dynamic] 
     */
	public void removeAllTransmissionStateChangesTo() {
		Base.removeAll(this.model, this.getResource(), TRANSMISSIONSTATECHANGESTO);
	}
 }