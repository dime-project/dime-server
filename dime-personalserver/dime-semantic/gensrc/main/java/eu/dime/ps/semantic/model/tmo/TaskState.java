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
 *   <li> TaskStateChangesFrom </li>
 *   <li> TaskStateChangesTo </li>
 * </ul>
 *
 * class- This class was generated by <a href="http://RDFReactor.semweb4j.org">RDFReactor</a> */
public class TaskState extends eu.dime.ps.semantic.model.tmo.StateTypeRole {

    /** http://www.semanticdesktop.org/ontologies/2008/05/20/tmo#TaskState */
    @SuppressWarnings("hiding")
	public static final URI RDFS_CLASS = new URIImpl("http://www.semanticdesktop.org/ontologies/2008/05/20/tmo#TaskState", false);

    /** http://www.semanticdesktop.org/ontologies/2008/05/20/tmo#taskStateChangesFrom */
    @SuppressWarnings("hiding")
	public static final URI TASKSTATECHANGESFROM = new URIImpl("http://www.semanticdesktop.org/ontologies/2008/05/20/tmo#taskStateChangesFrom",false);

    /** http://www.semanticdesktop.org/ontologies/2008/05/20/tmo#taskStateChangesTo */
    @SuppressWarnings("hiding")
	public static final URI TASKSTATECHANGESTO = new URIImpl("http://www.semanticdesktop.org/ontologies/2008/05/20/tmo#taskStateChangesTo",false);

    /** 
     * All property-URIs with this class as domain.
     * All properties of all super-classes are also available. 
     */
    @SuppressWarnings("hiding")
    public static final URI[] MANAGED_URIS = {
      new URIImpl("http://www.semanticdesktop.org/ontologies/2008/05/20/tmo#taskStateChangesFrom",false),
      new URIImpl("http://www.semanticdesktop.org/ontologies/2008/05/20/tmo#taskStateChangesTo",false) 
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
	protected TaskState (Model model, URI classURI, org.ontoware.rdf2go.model.node.Resource instanceIdentifier, boolean write) {
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
	public TaskState (Model model, org.ontoware.rdf2go.model.node.Resource instanceIdentifier, boolean write) {
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
	public TaskState (Model model, String uriString, boolean write) throws ModelRuntimeException {
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
	public TaskState (Model model, BlankNode bnode, boolean write) {
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
	public TaskState (Model model, boolean write) {
		super(model, RDFS_CLASS, model.newRandomUniqueURI(), write);
	}

    ///////////////////////////////////////////////////////////////////
    // typing

	/**
	 * Return an existing instance of this class in the model. No statements are written.
	 * @param model an RDF2Go model
	 * @param instanceResource an RDF2Go resource
	 * @return an instance of TaskState  or null if none existst
	 *
	 * [Generated from RDFReactor template rule #class0] 
	 */
	public static TaskState  getInstance(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource) {
		return Base.getInstance(model, instanceResource, TaskState.class);
	}

	/**
	 * Create a new instance of this class in the model. 
	 * That is, create the statement (instanceResource, RDF.type, http://www.semanticdesktop.org/ontologies/2008/05/20/tmo#TaskState).
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
	public static ReactorResult<? extends TaskState> getAllInstances_as(Model model) {
		return Base.getAllInstances_as(model, RDFS_CLASS, TaskState.class );
	}

    /**
	 * Remove rdf:type TaskState from this instance. Other triples are not affected.
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
	 * @return all A's as RDF resources, that have a relation 'TaskStateChangesFrom' to this TaskState instance
	 *
	 * [Generated from RDFReactor template rule #getallinverse1static] 
	 */
	public static ClosableIterator<org.ontoware.rdf2go.model.node.Resource> getAllTaskStateChangesFrom_Inverse(Model model, Object objectValue) {
		return Base.getAll_Inverse(model, eu.dime.ps.semantic.model.tmo.TaskState.TASKSTATECHANGESFROM, objectValue);
	}

	/**
	 * @return all A's as RDF resources, that have a relation 'TaskStateChangesFrom' to this TaskState instance
	 *
	 * [Generated from RDFReactor template rule #getallinverse1dynamic] 
	 */
	public ClosableIterator<org.ontoware.rdf2go.model.node.Resource> getAllTaskStateChangesFrom_Inverse() {
		return Base.getAll_Inverse(this.model, eu.dime.ps.semantic.model.tmo.TaskState.TASKSTATECHANGESFROM, this.getResource() );
	}

	/**
	 * @param model an RDF2Go model
	 * @param objectValue
	 * @return all A's as a ReactorResult, that have a relation 'TaskStateChangesFrom' to this TaskState instance
	 *
	 * [Generated from RDFReactor template rule #getallinverse-as1static] 
	 */
	public static ReactorResult<org.ontoware.rdf2go.model.node.Resource> getAllTaskStateChangesFrom_Inverse_as(Model model, Object objectValue) {
		return Base.getAll_Inverse_as(model, eu.dime.ps.semantic.model.tmo.TaskState.TASKSTATECHANGESFROM, objectValue, org.ontoware.rdf2go.model.node.Resource.class);
	}


	/**
	 * @param model an RDF2Go model
	 * @param objectValue
	 * @return all A's as RDF resources, that have a relation 'TaskStateChangesTo' to this TaskState instance
	 *
	 * [Generated from RDFReactor template rule #getallinverse1static] 
	 */
	public static ClosableIterator<org.ontoware.rdf2go.model.node.Resource> getAllTaskStateChangesTo_Inverse(Model model, Object objectValue) {
		return Base.getAll_Inverse(model, eu.dime.ps.semantic.model.tmo.TaskState.TASKSTATECHANGESTO, objectValue);
	}

	/**
	 * @return all A's as RDF resources, that have a relation 'TaskStateChangesTo' to this TaskState instance
	 *
	 * [Generated from RDFReactor template rule #getallinverse1dynamic] 
	 */
	public ClosableIterator<org.ontoware.rdf2go.model.node.Resource> getAllTaskStateChangesTo_Inverse() {
		return Base.getAll_Inverse(this.model, eu.dime.ps.semantic.model.tmo.TaskState.TASKSTATECHANGESTO, this.getResource() );
	}

	/**
	 * @param model an RDF2Go model
	 * @param objectValue
	 * @return all A's as a ReactorResult, that have a relation 'TaskStateChangesTo' to this TaskState instance
	 *
	 * [Generated from RDFReactor template rule #getallinverse-as1static] 
	 */
	public static ReactorResult<org.ontoware.rdf2go.model.node.Resource> getAllTaskStateChangesTo_Inverse_as(Model model, Object objectValue) {
		return Base.getAll_Inverse_as(model, eu.dime.ps.semantic.model.tmo.TaskState.TASKSTATECHANGESTO, objectValue, org.ontoware.rdf2go.model.node.Resource.class);
	}


	/**
	 * @param model an RDF2Go model
	 * @param objectValue
	 * @return all A's as RDF resources, that have a relation 'TaskState' to this TaskState instance
	 *
	 * [Generated from RDFReactor template rule #getallinverse1static] 
	 */
	public static ClosableIterator<org.ontoware.rdf2go.model.node.Resource> getAllTaskState_Inverse(Model model, Object objectValue) {
		return Base.getAll_Inverse(model, eu.dime.ps.semantic.model.tmo.Task.TASKSTATE, objectValue);
	}

	/**
	 * @return all A's as RDF resources, that have a relation 'TaskState' to this TaskState instance
	 *
	 * [Generated from RDFReactor template rule #getallinverse1dynamic] 
	 */
	public ClosableIterator<org.ontoware.rdf2go.model.node.Resource> getAllTaskState_Inverse() {
		return Base.getAll_Inverse(this.model, eu.dime.ps.semantic.model.tmo.Task.TASKSTATE, this.getResource() );
	}

	/**
	 * @param model an RDF2Go model
	 * @param objectValue
	 * @return all A's as a ReactorResult, that have a relation 'TaskState' to this TaskState instance
	 *
	 * [Generated from RDFReactor template rule #getallinverse-as1static] 
	 */
	public static ReactorResult<org.ontoware.rdf2go.model.node.Resource> getAllTaskState_Inverse_as(Model model, Object objectValue) {
		return Base.getAll_Inverse_as(model, eu.dime.ps.semantic.model.tmo.Task.TASKSTATE, objectValue, org.ontoware.rdf2go.model.node.Resource.class);
	}



    /**
     * Check if org.ontoware.rdfreactor.generator.java.JProperty@15593ac has at least one value set 
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
     * @return true if this property has at least one value
	 *
	 * [Generated from RDFReactor template rule #get0has-static] 
     */
	public static boolean hasTaskStateChangesFrom(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource) {
		return Base.has(model, instanceResource, TASKSTATECHANGESFROM);
	}

    /**
     * Check if org.ontoware.rdfreactor.generator.java.JProperty@15593ac has at least one value set 
     * @return true if this property has at least one value
	 *
	 * [Generated from RDFReactor template rule #get0has-dynamic] 
     */
	public boolean hasTaskStateChangesFrom() {
		return Base.has(this.model, this.getResource(), TASKSTATECHANGESFROM);
	}

    /**
     * Check if org.ontoware.rdfreactor.generator.java.JProperty@15593ac has the given value (maybe among other values).  
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
	 * @param value the value to be checked
     * @return true if this property contains (maybe among other) the given value
	 *
	 * [Generated from RDFReactor template rule #get0has-value-static] 
     */
	public static boolean hasTaskStateChangesFrom(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource, org.ontoware.rdf2go.model.node.Node value ) {
		return Base.hasValue(model, instanceResource, TASKSTATECHANGESFROM);
	}

    /**
     * Check if org.ontoware.rdfreactor.generator.java.JProperty@15593ac has the given value (maybe among other values).  
	 * @param value the value to be checked
     * @return true if this property contains (maybe among other) the given value
	 *
	 * [Generated from RDFReactor template rule #get0has-value-dynamic] 
     */
	public boolean hasTaskStateChangesFrom( org.ontoware.rdf2go.model.node.Node value ) {
		return Base.hasValue(this.model, this.getResource(), TASKSTATECHANGESFROM);
	}

    /**
     * Get single value of property TaskStateChangesFrom as an RDF2Go node 
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
     * @return the single value or null if no value is found
     * @throws RDFDataException at runtime, if the property has multiple values
	 *
	 * [Generated from RDFReactor template rule #get1static] 
     */
	public static org.ontoware.rdf2go.model.node.Node getTaskStateChangesFrom_asNode(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource) {
		return Base.get_asNode(model, instanceResource, TASKSTATECHANGESFROM);
	}
	
    /**
     * Get single value of property TaskStateChangesFrom as an RDF2Go node 
     * @return the single value or null if no value is found
     * @throws RDFDataException, if the property has multiple values
	 *
	 * [Generated from RDFReactor template rule #get2dynamic] 
     */
	public org.ontoware.rdf2go.model.node.Node getTaskStateChangesFrom_asNode() {
		return Base.get_asNode(this.model, this.getResource(), TASKSTATECHANGESFROM);
	}
     /**
     * Get single value of property TaskStateChangesFrom     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
     * @return the single value or null if no value is found
     * @throws RDFDataException, if the property has multiple values
	 *
	 * [Generated from RDFReactor template rule #get5static] 
     */
	public static eu.dime.ps.semantic.model.tmo.TaskState getTaskStateChangesFrom(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource) {
		return (eu.dime.ps.semantic.model.tmo.TaskState) Base.get(model, instanceResource, TASKSTATECHANGESFROM, eu.dime.ps.semantic.model.tmo.TaskState.class);
	}
	
    /**
     * Get single value of property TaskStateChangesFrom  .
     * This property has maxCardinality=1, that means the property takes only
     * a single value.
     * @return the single value or null if no value is found
     * @throws RDFDataException, if the property has multiple values
	 *
	 * [Generated from RDFReactor template rule #get6dynamic] 
     */
	public eu.dime.ps.semantic.model.tmo.TaskState getTaskStateChangesFrom() {
		return (eu.dime.ps.semantic.model.tmo.TaskState) Base.get(this.model, this.getResource(), TASKSTATECHANGESFROM, eu.dime.ps.semantic.model.tmo.TaskState.class);
	}
  
    /**
     * Adds a value to property TaskStateChangesFrom as an RDF2Go node 
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
	 * @param value the value to be added
	 * @throws CardinalityException if adding a value would bring the number 
	 *            of property values above the cardinality constraint.
	 *
	 * [Generated from RDFReactor template rule #add5static] 
     */
	public static void addTaskStateChangesFrom(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource, org.ontoware.rdf2go.model.node.Node value) 
    throws CardinalityException {
		Base.add(model, instanceResource, TASKSTATECHANGESFROM, value, 1);
	}
	
    /**
     * Adds a value to property TaskStateChangesFrom as an RDF2Go node 
	 * @param value the value to be added
	 * @throws CardinalityException if adding a value would bring the number 
	 *            of property values above the cardinality constraint.
	 *
	 * [Generated from RDFReactor template rule #add6dynamic] 
     */
	public void addTaskStateChangesFrom( org.ontoware.rdf2go.model.node.Node value) 
    throws CardinalityException {
		Base.add(this.model, this.getResource(), TASKSTATECHANGESFROM, value, 1);
	}
    /**
     * Adds a value to property TaskStateChangesFrom from an instance of TaskState 
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
	 * @throws CardinalityException if adding a value would bring the number 
	 *            of property values above the cardinality constraint.
	 *
	 * [Generated from RDFReactor template rule #add7static] 
     */
	public static void addTaskStateChangesFrom(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource, eu.dime.ps.semantic.model.tmo.TaskState value)
    throws CardinalityException {
		Base.add(model, instanceResource, TASKSTATECHANGESFROM, value, 1);
	}
	
    /**
     * Adds a value to property TaskStateChangesFrom from an instance of TaskState 
	 * @throws CardinalityException if adding a value would bring the number 
	 *            of property values above the cardinality constraint.
	 *
	 * [Generated from RDFReactor template rule #add8dynamic] 
     */
	public void addTaskStateChangesFrom(eu.dime.ps.semantic.model.tmo.TaskState value) 
    throws CardinalityException {
		Base.add(this.model, this.getResource(), TASKSTATECHANGESFROM, value, 1);
	}
 
 

    /**
     * Sets a value of property TaskStateChangesFrom from an RDF2Go node.
     * First, all existing values are removed, then this value is added.
     * Cardinality constraints are not checked, but this method exists only for properties with
     * no minCardinality or minCardinality == 1.
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
	 * @param value the value to be set
	 *
	 * [Generated from RDFReactor template rule #set1static] 
     */
	public static void setTaskStateChangesFrom( Model model, org.ontoware.rdf2go.model.node.Resource instanceResource, org.ontoware.rdf2go.model.node.Node value) {
		Base.set(model, instanceResource, TASKSTATECHANGESFROM, value);
	}
	
    /**
     * Sets a value of property TaskStateChangesFrom from an RDF2Go node.
     * First, all existing values are removed, then this value is added.
     * Cardinality constraints are not checked, but this method exists only for properties with
     * no minCardinality or minCardinality == 1.
	 * @param value the value to be added
	 *
	 * [Generated from RDFReactor template rule #set1dynamic] 
     */
	public void setTaskStateChangesFrom( org.ontoware.rdf2go.model.node.Node value) {
		Base.set(this.model, this.getResource(), TASKSTATECHANGESFROM, value);
	}
    /**
     * Sets a value of property TaskStateChangesFrom from an instance of TaskState 
     * First, all existing values are removed, then this value is added.
     * Cardinality constraints are not checked, but this method exists only for properties with
     * no minCardinality or minCardinality == 1.
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
	 * @param value the value to be added
	 *
	 * [Generated from RDFReactor template rule #set3static] 
     */
	public static void setTaskStateChangesFrom(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource, eu.dime.ps.semantic.model.tmo.TaskState value) {
		Base.set(model, instanceResource, TASKSTATECHANGESFROM, value);
	}
	
    /**
     * Sets a value of property TaskStateChangesFrom from an instance of TaskState 
     * First, all existing values are removed, then this value is added.
     * Cardinality constraints are not checked, but this method exists only for properties with
     * no minCardinality or minCardinality == 1.
	 * @param value the value to be added
	 *
	 * [Generated from RDFReactor template rule #set4dynamic] 
     */
	public void setTaskStateChangesFrom(eu.dime.ps.semantic.model.tmo.TaskState value) {
		Base.set(this.model, this.getResource(), TASKSTATECHANGESFROM, value);
	}
  


    /**
     * Removes a value of property TaskStateChangesFrom as an RDF2Go node 
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
	 * @param value the value to be removed
	 *
	 * [Generated from RDFReactor template rule #remove1static] 
     */
	public static void removeTaskStateChangesFrom( Model model, org.ontoware.rdf2go.model.node.Resource instanceResource, org.ontoware.rdf2go.model.node.Node value) {
		Base.remove(model, instanceResource, TASKSTATECHANGESFROM, value);
	}
	
    /**
     * Removes a value of property TaskStateChangesFrom as an RDF2Go node
	 * @param value the value to be removed
	 *
	 * [Generated from RDFReactor template rule #remove1dynamic] 
     */
	public void removeTaskStateChangesFrom( org.ontoware.rdf2go.model.node.Node value) {
		Base.remove(this.model, this.getResource(), TASKSTATECHANGESFROM, value);
	}
    /**
     * Removes a value of property TaskStateChangesFrom given as an instance of TaskState 
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
	 * @param value the value to be removed
	 *
	 * [Generated from RDFReactor template rule #remove3static] 
     */
	public static void removeTaskStateChangesFrom(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource, eu.dime.ps.semantic.model.tmo.TaskState value) {
		Base.remove(model, instanceResource, TASKSTATECHANGESFROM, value);
	}
	
    /**
     * Removes a value of property TaskStateChangesFrom given as an instance of TaskState 
	 * @param value the value to be removed
	 *
	 * [Generated from RDFReactor template rule #remove4dynamic] 
     */
	public void removeTaskStateChangesFrom(eu.dime.ps.semantic.model.tmo.TaskState value) {
		Base.remove(this.model, this.getResource(), TASKSTATECHANGESFROM, value);
	}
  
    /**
     * Removes all values of property TaskStateChangesFrom     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
	 *
	 * [Generated from RDFReactor template rule #removeall1static] 
     */
	public static void removeAllTaskStateChangesFrom( Model model, org.ontoware.rdf2go.model.node.Resource instanceResource) {
		Base.removeAll(model, instanceResource, TASKSTATECHANGESFROM);
	}
	
    /**
     * Removes all values of property TaskStateChangesFrom	 *
	 * [Generated from RDFReactor template rule #removeall1dynamic] 
     */
	public void removeAllTaskStateChangesFrom() {
		Base.removeAll(this.model, this.getResource(), TASKSTATECHANGESFROM);
	}
     /**
     * Check if org.ontoware.rdfreactor.generator.java.JProperty@4915f239 has at least one value set 
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
     * @return true if this property has at least one value
	 *
	 * [Generated from RDFReactor template rule #get0has-static] 
     */
	public static boolean hasTaskStateChangesTo(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource) {
		return Base.has(model, instanceResource, TASKSTATECHANGESTO);
	}

    /**
     * Check if org.ontoware.rdfreactor.generator.java.JProperty@4915f239 has at least one value set 
     * @return true if this property has at least one value
	 *
	 * [Generated from RDFReactor template rule #get0has-dynamic] 
     */
	public boolean hasTaskStateChangesTo() {
		return Base.has(this.model, this.getResource(), TASKSTATECHANGESTO);
	}

    /**
     * Check if org.ontoware.rdfreactor.generator.java.JProperty@4915f239 has the given value (maybe among other values).  
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
	 * @param value the value to be checked
     * @return true if this property contains (maybe among other) the given value
	 *
	 * [Generated from RDFReactor template rule #get0has-value-static] 
     */
	public static boolean hasTaskStateChangesTo(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource, org.ontoware.rdf2go.model.node.Node value ) {
		return Base.hasValue(model, instanceResource, TASKSTATECHANGESTO);
	}

    /**
     * Check if org.ontoware.rdfreactor.generator.java.JProperty@4915f239 has the given value (maybe among other values).  
	 * @param value the value to be checked
     * @return true if this property contains (maybe among other) the given value
	 *
	 * [Generated from RDFReactor template rule #get0has-value-dynamic] 
     */
	public boolean hasTaskStateChangesTo( org.ontoware.rdf2go.model.node.Node value ) {
		return Base.hasValue(this.model, this.getResource(), TASKSTATECHANGESTO);
	}

    /**
     * Get single value of property TaskStateChangesTo as an RDF2Go node 
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
     * @return the single value or null if no value is found
     * @throws RDFDataException at runtime, if the property has multiple values
	 *
	 * [Generated from RDFReactor template rule #get1static] 
     */
	public static org.ontoware.rdf2go.model.node.Node getTaskStateChangesTo_asNode(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource) {
		return Base.get_asNode(model, instanceResource, TASKSTATECHANGESTO);
	}
	
    /**
     * Get single value of property TaskStateChangesTo as an RDF2Go node 
     * @return the single value or null if no value is found
     * @throws RDFDataException, if the property has multiple values
	 *
	 * [Generated from RDFReactor template rule #get2dynamic] 
     */
	public org.ontoware.rdf2go.model.node.Node getTaskStateChangesTo_asNode() {
		return Base.get_asNode(this.model, this.getResource(), TASKSTATECHANGESTO);
	}
     /**
     * Get single value of property TaskStateChangesTo     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
     * @return the single value or null if no value is found
     * @throws RDFDataException, if the property has multiple values
	 *
	 * [Generated from RDFReactor template rule #get5static] 
     */
	public static eu.dime.ps.semantic.model.tmo.TaskState getTaskStateChangesTo(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource) {
		return (eu.dime.ps.semantic.model.tmo.TaskState) Base.get(model, instanceResource, TASKSTATECHANGESTO, eu.dime.ps.semantic.model.tmo.TaskState.class);
	}
	
    /**
     * Get single value of property TaskStateChangesTo  .
     * This property has maxCardinality=1, that means the property takes only
     * a single value.
     * @return the single value or null if no value is found
     * @throws RDFDataException, if the property has multiple values
	 *
	 * [Generated from RDFReactor template rule #get6dynamic] 
     */
	public eu.dime.ps.semantic.model.tmo.TaskState getTaskStateChangesTo() {
		return (eu.dime.ps.semantic.model.tmo.TaskState) Base.get(this.model, this.getResource(), TASKSTATECHANGESTO, eu.dime.ps.semantic.model.tmo.TaskState.class);
	}
  
    /**
     * Adds a value to property TaskStateChangesTo as an RDF2Go node 
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
	 * @param value the value to be added
	 * @throws CardinalityException if adding a value would bring the number 
	 *            of property values above the cardinality constraint.
	 *
	 * [Generated from RDFReactor template rule #add5static] 
     */
	public static void addTaskStateChangesTo(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource, org.ontoware.rdf2go.model.node.Node value) 
    throws CardinalityException {
		Base.add(model, instanceResource, TASKSTATECHANGESTO, value, 1);
	}
	
    /**
     * Adds a value to property TaskStateChangesTo as an RDF2Go node 
	 * @param value the value to be added
	 * @throws CardinalityException if adding a value would bring the number 
	 *            of property values above the cardinality constraint.
	 *
	 * [Generated from RDFReactor template rule #add6dynamic] 
     */
	public void addTaskStateChangesTo( org.ontoware.rdf2go.model.node.Node value) 
    throws CardinalityException {
		Base.add(this.model, this.getResource(), TASKSTATECHANGESTO, value, 1);
	}
    /**
     * Adds a value to property TaskStateChangesTo from an instance of TaskState 
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
	 * @throws CardinalityException if adding a value would bring the number 
	 *            of property values above the cardinality constraint.
	 *
	 * [Generated from RDFReactor template rule #add7static] 
     */
	public static void addTaskStateChangesTo(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource, eu.dime.ps.semantic.model.tmo.TaskState value)
    throws CardinalityException {
		Base.add(model, instanceResource, TASKSTATECHANGESTO, value, 1);
	}
	
    /**
     * Adds a value to property TaskStateChangesTo from an instance of TaskState 
	 * @throws CardinalityException if adding a value would bring the number 
	 *            of property values above the cardinality constraint.
	 *
	 * [Generated from RDFReactor template rule #add8dynamic] 
     */
	public void addTaskStateChangesTo(eu.dime.ps.semantic.model.tmo.TaskState value) 
    throws CardinalityException {
		Base.add(this.model, this.getResource(), TASKSTATECHANGESTO, value, 1);
	}
 
 

    /**
     * Sets a value of property TaskStateChangesTo from an RDF2Go node.
     * First, all existing values are removed, then this value is added.
     * Cardinality constraints are not checked, but this method exists only for properties with
     * no minCardinality or minCardinality == 1.
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
	 * @param value the value to be set
	 *
	 * [Generated from RDFReactor template rule #set1static] 
     */
	public static void setTaskStateChangesTo( Model model, org.ontoware.rdf2go.model.node.Resource instanceResource, org.ontoware.rdf2go.model.node.Node value) {
		Base.set(model, instanceResource, TASKSTATECHANGESTO, value);
	}
	
    /**
     * Sets a value of property TaskStateChangesTo from an RDF2Go node.
     * First, all existing values are removed, then this value is added.
     * Cardinality constraints are not checked, but this method exists only for properties with
     * no minCardinality or minCardinality == 1.
	 * @param value the value to be added
	 *
	 * [Generated from RDFReactor template rule #set1dynamic] 
     */
	public void setTaskStateChangesTo( org.ontoware.rdf2go.model.node.Node value) {
		Base.set(this.model, this.getResource(), TASKSTATECHANGESTO, value);
	}
    /**
     * Sets a value of property TaskStateChangesTo from an instance of TaskState 
     * First, all existing values are removed, then this value is added.
     * Cardinality constraints are not checked, but this method exists only for properties with
     * no minCardinality or minCardinality == 1.
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
	 * @param value the value to be added
	 *
	 * [Generated from RDFReactor template rule #set3static] 
     */
	public static void setTaskStateChangesTo(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource, eu.dime.ps.semantic.model.tmo.TaskState value) {
		Base.set(model, instanceResource, TASKSTATECHANGESTO, value);
	}
	
    /**
     * Sets a value of property TaskStateChangesTo from an instance of TaskState 
     * First, all existing values are removed, then this value is added.
     * Cardinality constraints are not checked, but this method exists only for properties with
     * no minCardinality or minCardinality == 1.
	 * @param value the value to be added
	 *
	 * [Generated from RDFReactor template rule #set4dynamic] 
     */
	public void setTaskStateChangesTo(eu.dime.ps.semantic.model.tmo.TaskState value) {
		Base.set(this.model, this.getResource(), TASKSTATECHANGESTO, value);
	}
  


    /**
     * Removes a value of property TaskStateChangesTo as an RDF2Go node 
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
	 * @param value the value to be removed
	 *
	 * [Generated from RDFReactor template rule #remove1static] 
     */
	public static void removeTaskStateChangesTo( Model model, org.ontoware.rdf2go.model.node.Resource instanceResource, org.ontoware.rdf2go.model.node.Node value) {
		Base.remove(model, instanceResource, TASKSTATECHANGESTO, value);
	}
	
    /**
     * Removes a value of property TaskStateChangesTo as an RDF2Go node
	 * @param value the value to be removed
	 *
	 * [Generated from RDFReactor template rule #remove1dynamic] 
     */
	public void removeTaskStateChangesTo( org.ontoware.rdf2go.model.node.Node value) {
		Base.remove(this.model, this.getResource(), TASKSTATECHANGESTO, value);
	}
    /**
     * Removes a value of property TaskStateChangesTo given as an instance of TaskState 
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
	 * @param value the value to be removed
	 *
	 * [Generated from RDFReactor template rule #remove3static] 
     */
	public static void removeTaskStateChangesTo(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource, eu.dime.ps.semantic.model.tmo.TaskState value) {
		Base.remove(model, instanceResource, TASKSTATECHANGESTO, value);
	}
	
    /**
     * Removes a value of property TaskStateChangesTo given as an instance of TaskState 
	 * @param value the value to be removed
	 *
	 * [Generated from RDFReactor template rule #remove4dynamic] 
     */
	public void removeTaskStateChangesTo(eu.dime.ps.semantic.model.tmo.TaskState value) {
		Base.remove(this.model, this.getResource(), TASKSTATECHANGESTO, value);
	}
  
    /**
     * Removes all values of property TaskStateChangesTo     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
	 *
	 * [Generated from RDFReactor template rule #removeall1static] 
     */
	public static void removeAllTaskStateChangesTo( Model model, org.ontoware.rdf2go.model.node.Resource instanceResource) {
		Base.removeAll(model, instanceResource, TASKSTATECHANGESTO);
	}
	
    /**
     * Removes all values of property TaskStateChangesTo	 *
	 * [Generated from RDFReactor template rule #removeall1dynamic] 
     */
	public void removeAllTaskStateChangesTo() {
		Base.removeAll(this.model, this.getResource(), TASKSTATECHANGESTO);
	}
 }