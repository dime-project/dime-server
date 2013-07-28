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
 *   <li> IsDefinedBy </li>
 * </ul>
 *
 * class- This class was generated by <a href="http://RDFReactor.semweb4j.org">RDFReactor</a> */
public class ClassOrThingOrPropertyOrAssociation extends eu.dime.ps.semantic.model.RDFReactorThing {

    /** http://www.semanticdesktop.org/ontologies/2007/11/01/pimo#ClassOrThingOrPropertyOrAssociation */
    @SuppressWarnings("hiding")
	public static final URI RDFS_CLASS = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/11/01/pimo#ClassOrThingOrPropertyOrAssociation", false);

    /** http://www.semanticdesktop.org/ontologies/2007/11/01/pimo#isDefinedBy */
    @SuppressWarnings("hiding")
	public static final URI ISDEFINEDBY = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/11/01/pimo#isDefinedBy",false);

    /** 
     * All property-URIs with this class as domain.
     * All properties of all super-classes are also available. 
     */
    @SuppressWarnings("hiding")
    public static final URI[] MANAGED_URIS = {
      new URIImpl("http://www.semanticdesktop.org/ontologies/2007/11/01/pimo#isDefinedBy",false) 
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
	protected ClassOrThingOrPropertyOrAssociation (Model model, URI classURI, org.ontoware.rdf2go.model.node.Resource instanceIdentifier, boolean write) {
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
	public ClassOrThingOrPropertyOrAssociation (Model model, org.ontoware.rdf2go.model.node.Resource instanceIdentifier, boolean write) {
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
	public ClassOrThingOrPropertyOrAssociation (Model model, String uriString, boolean write) throws ModelRuntimeException {
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
	public ClassOrThingOrPropertyOrAssociation (Model model, BlankNode bnode, boolean write) {
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
	public ClassOrThingOrPropertyOrAssociation (Model model, boolean write) {
		super(model, RDFS_CLASS, model.newRandomUniqueURI(), write);
	}

    ///////////////////////////////////////////////////////////////////
    // typing

	/**
	 * Return an existing instance of this class in the model. No statements are written.
	 * @param model an RDF2Go model
	 * @param instanceResource an RDF2Go resource
	 * @return an instance of ClassOrThingOrPropertyOrAssociation  or null if none existst
	 *
	 * [Generated from RDFReactor template rule #class0] 
	 */
	public static ClassOrThingOrPropertyOrAssociation  getInstance(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource) {
		return Base.getInstance(model, instanceResource, ClassOrThingOrPropertyOrAssociation.class);
	}

	/**
	 * Create a new instance of this class in the model. 
	 * That is, create the statement (instanceResource, RDF.type, http://www.semanticdesktop.org/ontologies/2007/11/01/pimo#ClassOrThingOrPropertyOrAssociation).
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
	public static ReactorResult<? extends ClassOrThingOrPropertyOrAssociation> getAllInstances_as(Model model) {
		return Base.getAllInstances_as(model, RDFS_CLASS, ClassOrThingOrPropertyOrAssociation.class );
	}

    /**
	 * Remove rdf:type ClassOrThingOrPropertyOrAssociation from this instance. Other triples are not affected.
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
     * Check if org.ontoware.rdfreactor.generator.java.JProperty@7445aabf has at least one value set 
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
     * @return true if this property has at least one value
	 *
	 * [Generated from RDFReactor template rule #get0has-static] 
     */
	public static boolean hasIsDefinedBy(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource) {
		return Base.has(model, instanceResource, ISDEFINEDBY);
	}

    /**
     * Check if org.ontoware.rdfreactor.generator.java.JProperty@7445aabf has at least one value set 
     * @return true if this property has at least one value
	 *
	 * [Generated from RDFReactor template rule #get0has-dynamic] 
     */
	public boolean hasIsDefinedBy() {
		return Base.has(this.model, this.getResource(), ISDEFINEDBY);
	}

    /**
     * Check if org.ontoware.rdfreactor.generator.java.JProperty@7445aabf has the given value (maybe among other values).  
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
	 * @param value the value to be checked
     * @return true if this property contains (maybe among other) the given value
	 *
	 * [Generated from RDFReactor template rule #get0has-value-static] 
     */
	public static boolean hasIsDefinedBy(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource, org.ontoware.rdf2go.model.node.Node value ) {
		return Base.hasValue(model, instanceResource, ISDEFINEDBY);
	}

    /**
     * Check if org.ontoware.rdfreactor.generator.java.JProperty@7445aabf has the given value (maybe among other values).  
	 * @param value the value to be checked
     * @return true if this property contains (maybe among other) the given value
	 *
	 * [Generated from RDFReactor template rule #get0has-value-dynamic] 
     */
	public boolean hasIsDefinedBy( org.ontoware.rdf2go.model.node.Node value ) {
		return Base.hasValue(this.model, this.getResource(), ISDEFINEDBY);
	}

    /**
     * Get single value of property IsDefinedBy as an RDF2Go node 
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
     * @return the single value or null if no value is found
     * @throws RDFDataException at runtime, if the property has multiple values
	 *
	 * [Generated from RDFReactor template rule #get1static] 
     */
	public static org.ontoware.rdf2go.model.node.Node getIsDefinedBy_asNode(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource) {
		return Base.get_asNode(model, instanceResource, ISDEFINEDBY);
	}
	
    /**
     * Get single value of property IsDefinedBy as an RDF2Go node 
     * @return the single value or null if no value is found
     * @throws RDFDataException, if the property has multiple values
	 *
	 * [Generated from RDFReactor template rule #get2dynamic] 
     */
	public org.ontoware.rdf2go.model.node.Node getIsDefinedBy_asNode() {
		return Base.get_asNode(this.model, this.getResource(), ISDEFINEDBY);
	}
     /**
     * Get single value of property IsDefinedBy     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
     * @return the single value or null if no value is found
     * @throws RDFDataException, if the property has multiple values
	 *
	 * [Generated from RDFReactor template rule #get5static] 
     */
	public static eu.dime.ps.semantic.model.pimo.PersonalInformationModel getIsDefinedBy(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource) {
		return (eu.dime.ps.semantic.model.pimo.PersonalInformationModel) Base.get(model, instanceResource, ISDEFINEDBY, eu.dime.ps.semantic.model.pimo.PersonalInformationModel.class);
	}
	
    /**
     * Get single value of property IsDefinedBy  .
     * This property has maxCardinality=1, that means the property takes only
     * a single value.
     * @return the single value or null if no value is found
     * @throws RDFDataException, if the property has multiple values
	 *
	 * [Generated from RDFReactor template rule #get6dynamic] 
     */
	public eu.dime.ps.semantic.model.pimo.PersonalInformationModel getIsDefinedBy() {
		return (eu.dime.ps.semantic.model.pimo.PersonalInformationModel) Base.get(this.model, this.getResource(), ISDEFINEDBY, eu.dime.ps.semantic.model.pimo.PersonalInformationModel.class);
	}
  
    /**
     * Adds a value to property IsDefinedBy as an RDF2Go node 
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
	 * @param value the value to be added
	 * @throws CardinalityException if adding a value would bring the number 
	 *            of property values above the cardinality constraint.
	 *
	 * [Generated from RDFReactor template rule #add5static] 
     */
	public static void addIsDefinedBy(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource, org.ontoware.rdf2go.model.node.Node value) {
//    throws CardinalityException {
//		Base.add(model, instanceResource, ISDEFINEDBY, value, 1);
		Base.add(model, instanceResource, ISDEFINEDBY, value);
	}
	
    /**
     * Adds a value to property IsDefinedBy as an RDF2Go node 
	 * @param value the value to be added
	 * @throws CardinalityException if adding a value would bring the number 
	 *            of property values above the cardinality constraint.
	 *
	 * [Generated from RDFReactor template rule #add6dynamic] 
     */
	public void addIsDefinedBy( org.ontoware.rdf2go.model.node.Node value) {
//    throws CardinalityException {
//		Base.add(this.model, this.getResource(), ISDEFINEDBY, value, 1);
		Base.add(this.model, this.getResource(), ISDEFINEDBY, value);
	}
    /**
     * Adds a value to property IsDefinedBy from an instance of PersonalInformationModel 
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
	 * @throws CardinalityException if adding a value would bring the number 
	 *            of property values above the cardinality constraint.
	 *
	 * [Generated from RDFReactor template rule #add7static] 
     */
	public static void addIsDefinedBy(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource, eu.dime.ps.semantic.model.pimo.PersonalInformationModel value)
    throws CardinalityException {
		Base.add(model, instanceResource, ISDEFINEDBY, value, 1);
	}
	
    /**
     * Adds a value to property IsDefinedBy from an instance of PersonalInformationModel 
	 * @throws CardinalityException if adding a value would bring the number 
	 *            of property values above the cardinality constraint.
	 *
	 * [Generated from RDFReactor template rule #add8dynamic] 
     */
	public void addIsDefinedBy(eu.dime.ps.semantic.model.pimo.PersonalInformationModel value) 
    throws CardinalityException {
		Base.add(this.model, this.getResource(), ISDEFINEDBY, value, 1);
	}
 
 

    /**
     * Sets a value of property IsDefinedBy from an RDF2Go node.
     * First, all existing values are removed, then this value is added.
     * Cardinality constraints are not checked, but this method exists only for properties with
     * no minCardinality or minCardinality == 1.
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
	 * @param value the value to be set
	 *
	 * [Generated from RDFReactor template rule #set1static] 
     */
	public static void setIsDefinedBy( Model model, org.ontoware.rdf2go.model.node.Resource instanceResource, org.ontoware.rdf2go.model.node.Node value) {
		Base.set(model, instanceResource, ISDEFINEDBY, value);
	}
	
    /**
     * Sets a value of property IsDefinedBy from an RDF2Go node.
     * First, all existing values are removed, then this value is added.
     * Cardinality constraints are not checked, but this method exists only for properties with
     * no minCardinality or minCardinality == 1.
	 * @param value the value to be added
	 *
	 * [Generated from RDFReactor template rule #set1dynamic] 
     */
	public void setIsDefinedBy( org.ontoware.rdf2go.model.node.Node value) {
		Base.set(this.model, this.getResource(), ISDEFINEDBY, value);
	}
    /**
     * Sets a value of property IsDefinedBy from an instance of PersonalInformationModel 
     * First, all existing values are removed, then this value is added.
     * Cardinality constraints are not checked, but this method exists only for properties with
     * no minCardinality or minCardinality == 1.
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
	 * @param value the value to be added
	 *
	 * [Generated from RDFReactor template rule #set3static] 
     */
	public static void setIsDefinedBy(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource, eu.dime.ps.semantic.model.pimo.PersonalInformationModel value) {
		Base.set(model, instanceResource, ISDEFINEDBY, value);
	}
	
    /**
     * Sets a value of property IsDefinedBy from an instance of PersonalInformationModel 
     * First, all existing values are removed, then this value is added.
     * Cardinality constraints are not checked, but this method exists only for properties with
     * no minCardinality or minCardinality == 1.
	 * @param value the value to be added
	 *
	 * [Generated from RDFReactor template rule #set4dynamic] 
     */
	public void setIsDefinedBy(eu.dime.ps.semantic.model.pimo.PersonalInformationModel value) {
		Base.set(this.model, this.getResource(), ISDEFINEDBY, value);
	}
  


    /**
     * Removes a value of property IsDefinedBy as an RDF2Go node 
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
	 * @param value the value to be removed
	 * @throws CardinalityException if removing a value would bring the number 
	 *            of property values below the cardinality constraint.
	 *
	 * [Generated from RDFReactor template rule #remove5static] 
     */
	public static void removeIsDefinedBy( Model model, org.ontoware.rdf2go.model.node.Resource instanceResource, org.ontoware.rdf2go.model.node.Node value) { 
//    throws CardinalityException {
//		Base.remove(model, instanceResource, ISDEFINEDBY, value, 1);
		Base.remove(model, instanceResource, ISDEFINEDBY, value);
	}
	
    /**
     * Removes a value of property IsDefinedBy as an RDF2Go node 
	 * @param value the value to be removed
	 * @throws CardinalityException if removing a value would bring the number 
	 *            of property values below the cardinality constraint.
	 *
	 * [Generated from RDFReactor template rule #remove6dynamic] 
     */
	public void removeIsDefinedBy( org.ontoware.rdf2go.model.node.Node value) { 
//    throws CardinalityException {
//		Base.remove(this.model, this.getResource(), ISDEFINEDBY, value, 1);
		Base.remove(this.model, this.getResource(), ISDEFINEDBY, value);
	}
    /**
     * Removes a value of property IsDefinedBy from an instance of PersonalInformationModel 
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
	 * @param value the value to be removed
	 * @throws CardinalityException if removing a value would bring the number 
	 *            of property values below the cardinality constraint.
	 *
	 * [Generated from RDFReactor template rule #remove7static] 
     */
	public static void removeIsDefinedBy(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource, eu.dime.ps.semantic.model.pimo.PersonalInformationModel value)
    throws CardinalityException {
		Base.remove(model, instanceResource, ISDEFINEDBY, value, 1);
	}
	
    /**
     * Removes a value of property IsDefinedBy from an instance of PersonalInformationModel 
	 * @param value the value to be removed
	 * @throws CardinalityException if removing a value would bring the number 
	 *            of property values below the cardinality constraint.
	 *
	 * [Generated from RDFReactor template rule #remove8dynamic] 
     */
	public void removeIsDefinedBy(eu.dime.ps.semantic.model.pimo.PersonalInformationModel value) 
    throws CardinalityException {
		Base.remove(this.model, this.getResource(), ISDEFINEDBY, value, 1);
	}
 
  }