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
 *   <li> Dtend </li>
 *   <li> Dtstart </li>
 * </ul>
 *
 * class- This class was generated by <a href="http://RDFReactor.semweb4j.org">RDFReactor</a> */
public class ProcessConcept extends eu.dime.ps.semantic.model.pimo.Thing {

    /** http://www.semanticdesktop.org/ontologies/2007/11/01/pimo#ProcessConcept */
    @SuppressWarnings("hiding")
	public static final URI RDFS_CLASS = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/11/01/pimo#ProcessConcept", false);

    /** http://www.semanticdesktop.org/ontologies/2007/11/01/pimo#dtend */
    @SuppressWarnings("hiding")
	public static final URI DTEND = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/11/01/pimo#dtend",false);

    /** http://www.semanticdesktop.org/ontologies/2007/11/01/pimo#dtstart */
    @SuppressWarnings("hiding")
	public static final URI DTSTART = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/11/01/pimo#dtstart",false);

    /** 
     * All property-URIs with this class as domain.
     * All properties of all super-classes are also available. 
     */
    @SuppressWarnings("hiding")
    public static final URI[] MANAGED_URIS = {
      new URIImpl("http://www.semanticdesktop.org/ontologies/2007/11/01/pimo#dtend",false),
      new URIImpl("http://www.semanticdesktop.org/ontologies/2007/11/01/pimo#dtstart",false) 
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
	protected ProcessConcept (Model model, URI classURI, org.ontoware.rdf2go.model.node.Resource instanceIdentifier, boolean write) {
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
	public ProcessConcept (Model model, org.ontoware.rdf2go.model.node.Resource instanceIdentifier, boolean write) {
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
	public ProcessConcept (Model model, String uriString, boolean write) throws ModelRuntimeException {
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
	public ProcessConcept (Model model, BlankNode bnode, boolean write) {
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
	public ProcessConcept (Model model, boolean write) {
		super(model, RDFS_CLASS, model.newRandomUniqueURI(), write);
	}

    ///////////////////////////////////////////////////////////////////
    // typing

	/**
	 * Return an existing instance of this class in the model. No statements are written.
	 * @param model an RDF2Go model
	 * @param instanceResource an RDF2Go resource
	 * @return an instance of ProcessConcept  or null if none existst
	 *
	 * [Generated from RDFReactor template rule #class0] 
	 */
	public static ProcessConcept  getInstance(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource) {
		return Base.getInstance(model, instanceResource, ProcessConcept.class);
	}

	/**
	 * Create a new instance of this class in the model. 
	 * That is, create the statement (instanceResource, RDF.type, http://www.semanticdesktop.org/ontologies/2007/11/01/pimo#ProcessConcept).
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
	public static ReactorResult<? extends ProcessConcept> getAllInstances_as(Model model) {
		return Base.getAllInstances_as(model, RDFS_CLASS, ProcessConcept.class );
	}

    /**
	 * Remove rdf:type ProcessConcept from this instance. Other triples are not affected.
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
     * Check if org.ontoware.rdfreactor.generator.java.JProperty@48d901f7 has at least one value set 
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
     * @return true if this property has at least one value
	 *
	 * [Generated from RDFReactor template rule #get0has-static] 
     */
	public static boolean hasDtend(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource) {
		return Base.has(model, instanceResource, DTEND);
	}

    /**
     * Check if org.ontoware.rdfreactor.generator.java.JProperty@48d901f7 has at least one value set 
     * @return true if this property has at least one value
	 *
	 * [Generated from RDFReactor template rule #get0has-dynamic] 
     */
	public boolean hasDtend() {
		return Base.has(this.model, this.getResource(), DTEND);
	}

    /**
     * Check if org.ontoware.rdfreactor.generator.java.JProperty@48d901f7 has the given value (maybe among other values).  
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
	 * @param value the value to be checked
     * @return true if this property contains (maybe among other) the given value
	 *
	 * [Generated from RDFReactor template rule #get0has-value-static] 
     */
	public static boolean hasDtend(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource, org.ontoware.rdf2go.model.node.Node value ) {
		return Base.hasValue(model, instanceResource, DTEND);
	}

    /**
     * Check if org.ontoware.rdfreactor.generator.java.JProperty@48d901f7 has the given value (maybe among other values).  
	 * @param value the value to be checked
     * @return true if this property contains (maybe among other) the given value
	 *
	 * [Generated from RDFReactor template rule #get0has-value-dynamic] 
     */
	public boolean hasDtend( org.ontoware.rdf2go.model.node.Node value ) {
		return Base.hasValue(this.model, this.getResource(), DTEND);
	}

    /**
     * Get single value of property Dtend as an RDF2Go node 
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
     * @return the single value or null if no value is found
     * @throws RDFDataException at runtime, if the property has multiple values
	 *
	 * [Generated from RDFReactor template rule #get1static] 
     */
	public static org.ontoware.rdf2go.model.node.Node getDtend_asNode(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource) {
		return Base.get_asNode(model, instanceResource, DTEND);
	}
	
    /**
     * Get single value of property Dtend as an RDF2Go node 
     * @return the single value or null if no value is found
     * @throws RDFDataException, if the property has multiple values
	 *
	 * [Generated from RDFReactor template rule #get2dynamic] 
     */
	public org.ontoware.rdf2go.model.node.Node getDtend_asNode() {
		return Base.get_asNode(this.model, this.getResource(), DTEND);
	}
     /**
     * Get single value of property Dtend     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
     * @return the single value or null if no value is found
     * @throws RDFDataException, if the property has multiple values
	 *
	 * [Generated from RDFReactor template rule #get5static] 
     */
	public static java.util.Calendar getDtend(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource) {
		return (java.util.Calendar) Base.get(model, instanceResource, DTEND, java.util.Calendar.class);
	}
	
    /**
     * Get single value of property Dtend  .
     * This property has maxCardinality=1, that means the property takes only
     * a single value.
     * @return the single value or null if no value is found
     * @throws RDFDataException, if the property has multiple values
	 *
	 * [Generated from RDFReactor template rule #get6dynamic] 
     */
	public java.util.Calendar getDtend() {
		return (java.util.Calendar) Base.get(this.model, this.getResource(), DTEND, java.util.Calendar.class);
	}
  
    /**
     * Adds a value to property Dtend as an RDF2Go node 
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
	 * @param value the value to be added
	 * @throws CardinalityException if adding a value would bring the number 
	 *            of property values above the cardinality constraint.
	 *
	 * [Generated from RDFReactor template rule #add5static] 
     */
	public static void addDtend(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource, org.ontoware.rdf2go.model.node.Node value) 
    throws CardinalityException {
		Base.add(model, instanceResource, DTEND, value, 1);
	}
	
    /**
     * Adds a value to property Dtend as an RDF2Go node 
	 * @param value the value to be added
	 * @throws CardinalityException if adding a value would bring the number 
	 *            of property values above the cardinality constraint.
	 *
	 * [Generated from RDFReactor template rule #add6dynamic] 
     */
	public void addDtend( org.ontoware.rdf2go.model.node.Node value) 
    throws CardinalityException {
		Base.add(this.model, this.getResource(), DTEND, value, 1);
	}
    /**
     * Adds a value to property Dtend from an instance of java.util.Calendar 
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
	 * @throws CardinalityException if adding a value would bring the number 
	 *            of property values above the cardinality constraint.
	 *
	 * [Generated from RDFReactor template rule #add7static] 
     */
	public static void addDtend(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource, java.util.Calendar value)
    throws CardinalityException {
		Base.add(model, instanceResource, DTEND, value, 1);
	}
	
    /**
     * Adds a value to property Dtend from an instance of java.util.Calendar 
	 * @throws CardinalityException if adding a value would bring the number 
	 *            of property values above the cardinality constraint.
	 *
	 * [Generated from RDFReactor template rule #add8dynamic] 
     */
	public void addDtend(java.util.Calendar value) 
    throws CardinalityException {
		Base.add(this.model, this.getResource(), DTEND, value, 1);
	}
 
 

    /**
     * Sets a value of property Dtend from an RDF2Go node.
     * First, all existing values are removed, then this value is added.
     * Cardinality constraints are not checked, but this method exists only for properties with
     * no minCardinality or minCardinality == 1.
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
	 * @param value the value to be set
	 *
	 * [Generated from RDFReactor template rule #set1static] 
     */
	public static void setDtend( Model model, org.ontoware.rdf2go.model.node.Resource instanceResource, org.ontoware.rdf2go.model.node.Node value) {
		Base.set(model, instanceResource, DTEND, value);
	}
	
    /**
     * Sets a value of property Dtend from an RDF2Go node.
     * First, all existing values are removed, then this value is added.
     * Cardinality constraints are not checked, but this method exists only for properties with
     * no minCardinality or minCardinality == 1.
	 * @param value the value to be added
	 *
	 * [Generated from RDFReactor template rule #set1dynamic] 
     */
	public void setDtend( org.ontoware.rdf2go.model.node.Node value) {
		Base.set(this.model, this.getResource(), DTEND, value);
	}
    /**
     * Sets a value of property Dtend from an instance of java.util.Calendar 
     * First, all existing values are removed, then this value is added.
     * Cardinality constraints are not checked, but this method exists only for properties with
     * no minCardinality or minCardinality == 1.
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
	 * @param value the value to be added
	 *
	 * [Generated from RDFReactor template rule #set3static] 
     */
	public static void setDtend(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource, java.util.Calendar value) {
		Base.set(model, instanceResource, DTEND, value);
	}
	
    /**
     * Sets a value of property Dtend from an instance of java.util.Calendar 
     * First, all existing values are removed, then this value is added.
     * Cardinality constraints are not checked, but this method exists only for properties with
     * no minCardinality or minCardinality == 1.
	 * @param value the value to be added
	 *
	 * [Generated from RDFReactor template rule #set4dynamic] 
     */
	public void setDtend(java.util.Calendar value) {
		Base.set(this.model, this.getResource(), DTEND, value);
	}
  


    /**
     * Removes a value of property Dtend as an RDF2Go node 
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
	 * @param value the value to be removed
	 *
	 * [Generated from RDFReactor template rule #remove1static] 
     */
	public static void removeDtend( Model model, org.ontoware.rdf2go.model.node.Resource instanceResource, org.ontoware.rdf2go.model.node.Node value) {
		Base.remove(model, instanceResource, DTEND, value);
	}
	
    /**
     * Removes a value of property Dtend as an RDF2Go node
	 * @param value the value to be removed
	 *
	 * [Generated from RDFReactor template rule #remove1dynamic] 
     */
	public void removeDtend( org.ontoware.rdf2go.model.node.Node value) {
		Base.remove(this.model, this.getResource(), DTEND, value);
	}
    /**
     * Removes a value of property Dtend given as an instance of java.util.Calendar 
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
	 * @param value the value to be removed
	 *
	 * [Generated from RDFReactor template rule #remove3static] 
     */
	public static void removeDtend(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource, java.util.Calendar value) {
		Base.remove(model, instanceResource, DTEND, value);
	}
	
    /**
     * Removes a value of property Dtend given as an instance of java.util.Calendar 
	 * @param value the value to be removed
	 *
	 * [Generated from RDFReactor template rule #remove4dynamic] 
     */
	public void removeDtend(java.util.Calendar value) {
		Base.remove(this.model, this.getResource(), DTEND, value);
	}
  
    /**
     * Removes all values of property Dtend     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
	 *
	 * [Generated from RDFReactor template rule #removeall1static] 
     */
	public static void removeAllDtend( Model model, org.ontoware.rdf2go.model.node.Resource instanceResource) {
		Base.removeAll(model, instanceResource, DTEND);
	}
	
    /**
     * Removes all values of property Dtend	 *
	 * [Generated from RDFReactor template rule #removeall1dynamic] 
     */
	public void removeAllDtend() {
		Base.removeAll(this.model, this.getResource(), DTEND);
	}
     /**
     * Check if org.ontoware.rdfreactor.generator.java.JProperty@67f4d7d1 has at least one value set 
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
     * @return true if this property has at least one value
	 *
	 * [Generated from RDFReactor template rule #get0has-static] 
     */
	public static boolean hasDtstart(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource) {
		return Base.has(model, instanceResource, DTSTART);
	}

    /**
     * Check if org.ontoware.rdfreactor.generator.java.JProperty@67f4d7d1 has at least one value set 
     * @return true if this property has at least one value
	 *
	 * [Generated from RDFReactor template rule #get0has-dynamic] 
     */
	public boolean hasDtstart() {
		return Base.has(this.model, this.getResource(), DTSTART);
	}

    /**
     * Check if org.ontoware.rdfreactor.generator.java.JProperty@67f4d7d1 has the given value (maybe among other values).  
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
	 * @param value the value to be checked
     * @return true if this property contains (maybe among other) the given value
	 *
	 * [Generated from RDFReactor template rule #get0has-value-static] 
     */
	public static boolean hasDtstart(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource, org.ontoware.rdf2go.model.node.Node value ) {
		return Base.hasValue(model, instanceResource, DTSTART);
	}

    /**
     * Check if org.ontoware.rdfreactor.generator.java.JProperty@67f4d7d1 has the given value (maybe among other values).  
	 * @param value the value to be checked
     * @return true if this property contains (maybe among other) the given value
	 *
	 * [Generated from RDFReactor template rule #get0has-value-dynamic] 
     */
	public boolean hasDtstart( org.ontoware.rdf2go.model.node.Node value ) {
		return Base.hasValue(this.model, this.getResource(), DTSTART);
	}

    /**
     * Get single value of property Dtstart as an RDF2Go node 
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
     * @return the single value or null if no value is found
     * @throws RDFDataException at runtime, if the property has multiple values
	 *
	 * [Generated from RDFReactor template rule #get1static] 
     */
	public static org.ontoware.rdf2go.model.node.Node getDtstart_asNode(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource) {
		return Base.get_asNode(model, instanceResource, DTSTART);
	}
	
    /**
     * Get single value of property Dtstart as an RDF2Go node 
     * @return the single value or null if no value is found
     * @throws RDFDataException, if the property has multiple values
	 *
	 * [Generated from RDFReactor template rule #get2dynamic] 
     */
	public org.ontoware.rdf2go.model.node.Node getDtstart_asNode() {
		return Base.get_asNode(this.model, this.getResource(), DTSTART);
	}
     /**
     * Get single value of property Dtstart     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
     * @return the single value or null if no value is found
     * @throws RDFDataException, if the property has multiple values
	 *
	 * [Generated from RDFReactor template rule #get5static] 
     */
	public static java.util.Calendar getDtstart(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource) {
		return (java.util.Calendar) Base.get(model, instanceResource, DTSTART, java.util.Calendar.class);
	}
	
    /**
     * Get single value of property Dtstart  .
     * This property has maxCardinality=1, that means the property takes only
     * a single value.
     * @return the single value or null if no value is found
     * @throws RDFDataException, if the property has multiple values
	 *
	 * [Generated from RDFReactor template rule #get6dynamic] 
     */
	public java.util.Calendar getDtstart() {
		return (java.util.Calendar) Base.get(this.model, this.getResource(), DTSTART, java.util.Calendar.class);
	}
  
    /**
     * Adds a value to property Dtstart as an RDF2Go node 
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
	 * @param value the value to be added
	 * @throws CardinalityException if adding a value would bring the number 
	 *            of property values above the cardinality constraint.
	 *
	 * [Generated from RDFReactor template rule #add5static] 
     */
	public static void addDtstart(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource, org.ontoware.rdf2go.model.node.Node value) 
    throws CardinalityException {
		Base.add(model, instanceResource, DTSTART, value, 1);
	}
	
    /**
     * Adds a value to property Dtstart as an RDF2Go node 
	 * @param value the value to be added
	 * @throws CardinalityException if adding a value would bring the number 
	 *            of property values above the cardinality constraint.
	 *
	 * [Generated from RDFReactor template rule #add6dynamic] 
     */
	public void addDtstart( org.ontoware.rdf2go.model.node.Node value) 
    throws CardinalityException {
		Base.add(this.model, this.getResource(), DTSTART, value, 1);
	}
    /**
     * Adds a value to property Dtstart from an instance of java.util.Calendar 
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
	 * @throws CardinalityException if adding a value would bring the number 
	 *            of property values above the cardinality constraint.
	 *
	 * [Generated from RDFReactor template rule #add7static] 
     */
	public static void addDtstart(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource, java.util.Calendar value)
    throws CardinalityException {
		Base.add(model, instanceResource, DTSTART, value, 1);
	}
	
    /**
     * Adds a value to property Dtstart from an instance of java.util.Calendar 
	 * @throws CardinalityException if adding a value would bring the number 
	 *            of property values above the cardinality constraint.
	 *
	 * [Generated from RDFReactor template rule #add8dynamic] 
     */
	public void addDtstart(java.util.Calendar value) 
    throws CardinalityException {
		Base.add(this.model, this.getResource(), DTSTART, value, 1);
	}
 
 

    /**
     * Sets a value of property Dtstart from an RDF2Go node.
     * First, all existing values are removed, then this value is added.
     * Cardinality constraints are not checked, but this method exists only for properties with
     * no minCardinality or minCardinality == 1.
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
	 * @param value the value to be set
	 *
	 * [Generated from RDFReactor template rule #set1static] 
     */
	public static void setDtstart( Model model, org.ontoware.rdf2go.model.node.Resource instanceResource, org.ontoware.rdf2go.model.node.Node value) {
		Base.set(model, instanceResource, DTSTART, value);
	}
	
    /**
     * Sets a value of property Dtstart from an RDF2Go node.
     * First, all existing values are removed, then this value is added.
     * Cardinality constraints are not checked, but this method exists only for properties with
     * no minCardinality or minCardinality == 1.
	 * @param value the value to be added
	 *
	 * [Generated from RDFReactor template rule #set1dynamic] 
     */
	public void setDtstart( org.ontoware.rdf2go.model.node.Node value) {
		Base.set(this.model, this.getResource(), DTSTART, value);
	}
    /**
     * Sets a value of property Dtstart from an instance of java.util.Calendar 
     * First, all existing values are removed, then this value is added.
     * Cardinality constraints are not checked, but this method exists only for properties with
     * no minCardinality or minCardinality == 1.
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
	 * @param value the value to be added
	 *
	 * [Generated from RDFReactor template rule #set3static] 
     */
	public static void setDtstart(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource, java.util.Calendar value) {
		Base.set(model, instanceResource, DTSTART, value);
	}
	
    /**
     * Sets a value of property Dtstart from an instance of java.util.Calendar 
     * First, all existing values are removed, then this value is added.
     * Cardinality constraints are not checked, but this method exists only for properties with
     * no minCardinality or minCardinality == 1.
	 * @param value the value to be added
	 *
	 * [Generated from RDFReactor template rule #set4dynamic] 
     */
	public void setDtstart(java.util.Calendar value) {
		Base.set(this.model, this.getResource(), DTSTART, value);
	}
  


    /**
     * Removes a value of property Dtstart as an RDF2Go node 
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
	 * @param value the value to be removed
	 *
	 * [Generated from RDFReactor template rule #remove1static] 
     */
	public static void removeDtstart( Model model, org.ontoware.rdf2go.model.node.Resource instanceResource, org.ontoware.rdf2go.model.node.Node value) {
		Base.remove(model, instanceResource, DTSTART, value);
	}
	
    /**
     * Removes a value of property Dtstart as an RDF2Go node
	 * @param value the value to be removed
	 *
	 * [Generated from RDFReactor template rule #remove1dynamic] 
     */
	public void removeDtstart( org.ontoware.rdf2go.model.node.Node value) {
		Base.remove(this.model, this.getResource(), DTSTART, value);
	}
    /**
     * Removes a value of property Dtstart given as an instance of java.util.Calendar 
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
	 * @param value the value to be removed
	 *
	 * [Generated from RDFReactor template rule #remove3static] 
     */
	public static void removeDtstart(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource, java.util.Calendar value) {
		Base.remove(model, instanceResource, DTSTART, value);
	}
	
    /**
     * Removes a value of property Dtstart given as an instance of java.util.Calendar 
	 * @param value the value to be removed
	 *
	 * [Generated from RDFReactor template rule #remove4dynamic] 
     */
	public void removeDtstart(java.util.Calendar value) {
		Base.remove(this.model, this.getResource(), DTSTART, value);
	}
  
    /**
     * Removes all values of property Dtstart     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
	 *
	 * [Generated from RDFReactor template rule #removeall1static] 
     */
	public static void removeAllDtstart( Model model, org.ontoware.rdf2go.model.node.Resource instanceResource) {
		Base.removeAll(model, instanceResource, DTSTART);
	}
	
    /**
     * Removes all values of property Dtstart	 *
	 * [Generated from RDFReactor template rule #removeall1dynamic] 
     */
	public void removeAllDtstart() {
		Base.removeAll(this.model, this.getResource(), DTSTART);
	}
 }