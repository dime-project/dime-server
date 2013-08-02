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


/**
 * This class manages access to these properties:
 * <ul>
 *   <li> SubTopic </li>
 *   <li> SuperTopic </li>
 * </ul>
 *
 * class- This class was generated by <a href="http://RDFReactor.semweb4j.org">RDFReactor</a> */
public class Topic extends eu.dime.ps.semantic.model.pimo.Tag {

    /** http://www.semanticdesktop.org/ontologies/2007/11/01/pimo#Topic */
    @SuppressWarnings("hiding")
	public static final URI RDFS_CLASS = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/11/01/pimo#Topic", false);

    /** http://www.semanticdesktop.org/ontologies/2007/11/01/pimo#subTopic */
    @SuppressWarnings("hiding")
	public static final URI SUBTOPIC = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/11/01/pimo#subTopic",false);

    /** http://www.semanticdesktop.org/ontologies/2007/11/01/pimo#superTopic */
    @SuppressWarnings("hiding")
	public static final URI SUPERTOPIC = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/11/01/pimo#superTopic",false);

    /** 
     * All property-URIs with this class as domain.
     * All properties of all super-classes are also available. 
     */
    @SuppressWarnings("hiding")
    public static final URI[] MANAGED_URIS = {
      new URIImpl("http://www.semanticdesktop.org/ontologies/2007/11/01/pimo#subTopic",false),
      new URIImpl("http://www.semanticdesktop.org/ontologies/2007/11/01/pimo#superTopic",false) 
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
	protected Topic (Model model, URI classURI, org.ontoware.rdf2go.model.node.Resource instanceIdentifier, boolean write) {
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
	public Topic (Model model, org.ontoware.rdf2go.model.node.Resource instanceIdentifier, boolean write) {
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
	public Topic (Model model, String uriString, boolean write) throws ModelRuntimeException {
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
	public Topic (Model model, BlankNode bnode, boolean write) {
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
	public Topic (Model model, boolean write) {
		super(model, RDFS_CLASS, model.newRandomUniqueURI(), write);
	}

    ///////////////////////////////////////////////////////////////////
    // typing

	/**
	 * Return an existing instance of this class in the model. No statements are written.
	 * @param model an RDF2Go model
	 * @param instanceResource an RDF2Go resource
	 * @return an instance of Topic  or null if none existst
	 *
	 * [Generated from RDFReactor template rule #class0] 
	 */
	public static Topic  getInstance(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource) {
		return Base.getInstance(model, instanceResource, Topic.class);
	}

	/**
	 * Create a new instance of this class in the model. 
	 * That is, create the statement (instanceResource, RDF.type, http://www.semanticdesktop.org/ontologies/2007/11/01/pimo#Topic).
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
	public static ReactorResult<? extends Topic> getAllInstances_as(Model model) {
		return Base.getAllInstances_as(model, RDFS_CLASS, Topic.class );
	}

    /**
	 * Remove rdf:type Topic from this instance. Other triples are not affected.
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
	 * @return all A's as RDF resources, that have a relation 'RootTopic' to this Topic instance
	 *
	 * [Generated from RDFReactor template rule #getallinverse1static] 
	 */
	public static ClosableIterator<org.ontoware.rdf2go.model.node.Resource> getAllRootTopic_Inverse(Model model, Object objectValue) {
		return Base.getAll_Inverse(model, eu.dime.ps.semantic.model.pimo.PersonalInformationModel.ROOTTOPIC, objectValue);
	}

	/**
	 * @return all A's as RDF resources, that have a relation 'RootTopic' to this Topic instance
	 *
	 * [Generated from RDFReactor template rule #getallinverse1dynamic] 
	 */
	public ClosableIterator<org.ontoware.rdf2go.model.node.Resource> getAllRootTopic_Inverse() {
		return Base.getAll_Inverse(this.model, eu.dime.ps.semantic.model.pimo.PersonalInformationModel.ROOTTOPIC, this.getResource() );
	}

	/**
	 * @param model an RDF2Go model
	 * @param objectValue
	 * @return all A's as a ReactorResult, that have a relation 'RootTopic' to this Topic instance
	 *
	 * [Generated from RDFReactor template rule #getallinverse-as1static] 
	 */
	public static ReactorResult<org.ontoware.rdf2go.model.node.Resource> getAllRootTopic_Inverse_as(Model model, Object objectValue) {
		return Base.getAll_Inverse_as(model, eu.dime.ps.semantic.model.pimo.PersonalInformationModel.ROOTTOPIC, objectValue, org.ontoware.rdf2go.model.node.Resource.class);
	}


	/**
	 * @param model an RDF2Go model
	 * @param objectValue
	 * @return all A's as RDF resources, that have a relation 'SubTopic' to this Topic instance
	 *
	 * [Generated from RDFReactor template rule #getallinverse1static] 
	 */
	public static ClosableIterator<org.ontoware.rdf2go.model.node.Resource> getAllSubTopic_Inverse(Model model, Object objectValue) {
		return Base.getAll_Inverse(model, eu.dime.ps.semantic.model.pimo.Topic.SUBTOPIC, objectValue);
	}

	/**
	 * @return all A's as RDF resources, that have a relation 'SubTopic' to this Topic instance
	 *
	 * [Generated from RDFReactor template rule #getallinverse1dynamic] 
	 */
	public ClosableIterator<org.ontoware.rdf2go.model.node.Resource> getAllSubTopic_Inverse() {
		return Base.getAll_Inverse(this.model, eu.dime.ps.semantic.model.pimo.Topic.SUBTOPIC, this.getResource() );
	}

	/**
	 * @param model an RDF2Go model
	 * @param objectValue
	 * @return all A's as a ReactorResult, that have a relation 'SubTopic' to this Topic instance
	 *
	 * [Generated from RDFReactor template rule #getallinverse-as1static] 
	 */
	public static ReactorResult<org.ontoware.rdf2go.model.node.Resource> getAllSubTopic_Inverse_as(Model model, Object objectValue) {
		return Base.getAll_Inverse_as(model, eu.dime.ps.semantic.model.pimo.Topic.SUBTOPIC, objectValue, org.ontoware.rdf2go.model.node.Resource.class);
	}


	/**
	 * @param model an RDF2Go model
	 * @param objectValue
	 * @return all A's as RDF resources, that have a relation 'SuperTopic' to this Topic instance
	 *
	 * [Generated from RDFReactor template rule #getallinverse1static] 
	 */
	public static ClosableIterator<org.ontoware.rdf2go.model.node.Resource> getAllSuperTopic_Inverse(Model model, Object objectValue) {
		return Base.getAll_Inverse(model, eu.dime.ps.semantic.model.pimo.Topic.SUPERTOPIC, objectValue);
	}

	/**
	 * @return all A's as RDF resources, that have a relation 'SuperTopic' to this Topic instance
	 *
	 * [Generated from RDFReactor template rule #getallinverse1dynamic] 
	 */
	public ClosableIterator<org.ontoware.rdf2go.model.node.Resource> getAllSuperTopic_Inverse() {
		return Base.getAll_Inverse(this.model, eu.dime.ps.semantic.model.pimo.Topic.SUPERTOPIC, this.getResource() );
	}

	/**
	 * @param model an RDF2Go model
	 * @param objectValue
	 * @return all A's as a ReactorResult, that have a relation 'SuperTopic' to this Topic instance
	 *
	 * [Generated from RDFReactor template rule #getallinverse-as1static] 
	 */
	public static ReactorResult<org.ontoware.rdf2go.model.node.Resource> getAllSuperTopic_Inverse_as(Model model, Object objectValue) {
		return Base.getAll_Inverse_as(model, eu.dime.ps.semantic.model.pimo.Topic.SUPERTOPIC, objectValue, org.ontoware.rdf2go.model.node.Resource.class);
	}



    /**
     * Check if org.ontoware.rdfreactor.generator.java.JProperty@764ad16d has at least one value set 
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
     * @return true if this property has at least one value
	 *
	 * [Generated from RDFReactor template rule #get0has-static] 
     */
	public static boolean hasSubTopic(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource) {
		return Base.has(model, instanceResource, SUBTOPIC);
	}

    /**
     * Check if org.ontoware.rdfreactor.generator.java.JProperty@764ad16d has at least one value set 
     * @return true if this property has at least one value
	 *
	 * [Generated from RDFReactor template rule #get0has-dynamic] 
     */
	public boolean hasSubTopic() {
		return Base.has(this.model, this.getResource(), SUBTOPIC);
	}

    /**
     * Check if org.ontoware.rdfreactor.generator.java.JProperty@764ad16d has the given value (maybe among other values).  
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
	 * @param value the value to be checked
     * @return true if this property contains (maybe among other) the given value
	 *
	 * [Generated from RDFReactor template rule #get0has-value-static] 
     */
	public static boolean hasSubTopic(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource, org.ontoware.rdf2go.model.node.Node value ) {
		return Base.hasValue(model, instanceResource, SUBTOPIC);
	}

    /**
     * Check if org.ontoware.rdfreactor.generator.java.JProperty@764ad16d has the given value (maybe among other values).  
	 * @param value the value to be checked
     * @return true if this property contains (maybe among other) the given value
	 *
	 * [Generated from RDFReactor template rule #get0has-value-dynamic] 
     */
	public boolean hasSubTopic( org.ontoware.rdf2go.model.node.Node value ) {
		return Base.hasValue(this.model, this.getResource(), SUBTOPIC);
	}

     /**
     * Get all values of property SubTopic as an Iterator over RDF2Go nodes 
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
     * @return a ClosableIterator of RDF2Go Nodes
	 *
	 * [Generated from RDFReactor template rule #get7static] 
     */
	public static ClosableIterator<org.ontoware.rdf2go.model.node.Node> getAllSubTopic_asNode(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource) {
		return Base.getAll_asNode(model, instanceResource, SUBTOPIC);
	}
	
    /**
     * Get all values of property SubTopic as a ReactorResult of RDF2Go nodes 
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
     * @return a List of RDF2Go Nodes
	 *
	 * [Generated from RDFReactor template rule #get7static-reactor-result] 
     */
	public static ReactorResult<org.ontoware.rdf2go.model.node.Node> getAllSubTopic_asNode_(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource) {
		return Base.getAll_as(model, instanceResource, SUBTOPIC, org.ontoware.rdf2go.model.node.Node.class);
	}

    /**
     * Get all values of property SubTopic as an Iterator over RDF2Go nodes 
     * @return a ClosableIterator of RDF2Go Nodes
	 *
	 * [Generated from RDFReactor template rule #get8dynamic] 
     */
	public ClosableIterator<org.ontoware.rdf2go.model.node.Node> getAllSubTopic_asNode() {
		return Base.getAll_asNode(this.model, this.getResource(), SUBTOPIC);
	}

    /**
     * Get all values of property SubTopic as a ReactorResult of RDF2Go nodes 
     * @return a List of RDF2Go Nodes
	 *
	 * [Generated from RDFReactor template rule #get8dynamic-reactor-result] 
     */
	public ReactorResult<org.ontoware.rdf2go.model.node.Node> getAllSubTopic_asNode_() {
		return Base.getAll_as(this.model, this.getResource(), SUBTOPIC, org.ontoware.rdf2go.model.node.Node.class);
	}
     /**
     * Get all values of property SubTopic     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
     * @return a ClosableIterator of $type
	 *
	 * [Generated from RDFReactor template rule #get11static] 
     */
	public static ClosableIterator<eu.dime.ps.semantic.model.pimo.Topic> getAllSubTopic(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource) {
		return Base.getAll(model, instanceResource, SUBTOPIC, eu.dime.ps.semantic.model.pimo.Topic.class);
	}
	
    /**
     * Get all values of property SubTopic as a ReactorResult of Topic 
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
     * @return a ReactorResult of $type which can conveniently be converted to iterator, list or array
	 *
	 * [Generated from RDFReactor template rule #get11static-reactorresult] 
     */
	public static ReactorResult<eu.dime.ps.semantic.model.pimo.Topic> getAllSubTopic_as(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource) {
		return Base.getAll_as(model, instanceResource, SUBTOPIC, eu.dime.ps.semantic.model.pimo.Topic.class);
	}

    /**
     * Get all values of property SubTopic     * @return a ClosableIterator of $type
	 *
	 * [Generated from RDFReactor template rule #get12dynamic] 
     */
	public ClosableIterator<eu.dime.ps.semantic.model.pimo.Topic> getAllSubTopic() {
		return Base.getAll(this.model, this.getResource(), SUBTOPIC, eu.dime.ps.semantic.model.pimo.Topic.class);
	}

    /**
     * Get all values of property SubTopic as a ReactorResult of Topic 
     * @return a ReactorResult of $type which can conveniently be converted to iterator, list or array
	 *
	 * [Generated from RDFReactor template rule #get12dynamic-reactorresult] 
     */
	public ReactorResult<eu.dime.ps.semantic.model.pimo.Topic> getAllSubTopic_as() {
		return Base.getAll_as(this.model, this.getResource(), SUBTOPIC, eu.dime.ps.semantic.model.pimo.Topic.class);
	}
 
    /**
     * Adds a value to property SubTopic as an RDF2Go node 
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
	 * @param value the value to be added
	 *
	 * [Generated from RDFReactor template rule #add1static] 
     */
	public static void addSubTopic(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource, org.ontoware.rdf2go.model.node.Node value) {
		Base.add(model, instanceResource, SUBTOPIC, value);
	}
	
    /**
     * Adds a value to property SubTopic as an RDF2Go node 
	 * @param value the value to be added
	 *
	 * [Generated from RDFReactor template rule #add1dynamic] 
     */
	public void addSubTopic( org.ontoware.rdf2go.model.node.Node value) {
		Base.add(this.model, this.getResource(), SUBTOPIC, value);
	}
    /**
     * Adds a value to property SubTopic from an instance of Topic 
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
	 *
	 * [Generated from RDFReactor template rule #add3static] 
     */
	public static void addSubTopic(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource, eu.dime.ps.semantic.model.pimo.Topic value) {
		Base.add(model, instanceResource, SUBTOPIC, value);
	}
	
    /**
     * Adds a value to property SubTopic from an instance of Topic 
	 *
	 * [Generated from RDFReactor template rule #add4dynamic] 
     */
	public void addSubTopic(eu.dime.ps.semantic.model.pimo.Topic value) {
		Base.add(this.model, this.getResource(), SUBTOPIC, value);
	}
  

    /**
     * Sets a value of property SubTopic from an RDF2Go node.
     * First, all existing values are removed, then this value is added.
     * Cardinality constraints are not checked, but this method exists only for properties with
     * no minCardinality or minCardinality == 1.
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
	 * @param value the value to be set
	 *
	 * [Generated from RDFReactor template rule #set1static] 
     */
	public static void setSubTopic( Model model, org.ontoware.rdf2go.model.node.Resource instanceResource, org.ontoware.rdf2go.model.node.Node value) {
		Base.set(model, instanceResource, SUBTOPIC, value);
	}
	
    /**
     * Sets a value of property SubTopic from an RDF2Go node.
     * First, all existing values are removed, then this value is added.
     * Cardinality constraints are not checked, but this method exists only for properties with
     * no minCardinality or minCardinality == 1.
	 * @param value the value to be added
	 *
	 * [Generated from RDFReactor template rule #set1dynamic] 
     */
	public void setSubTopic( org.ontoware.rdf2go.model.node.Node value) {
		Base.set(this.model, this.getResource(), SUBTOPIC, value);
	}
    /**
     * Sets a value of property SubTopic from an instance of Topic 
     * First, all existing values are removed, then this value is added.
     * Cardinality constraints are not checked, but this method exists only for properties with
     * no minCardinality or minCardinality == 1.
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
	 * @param value the value to be added
	 *
	 * [Generated from RDFReactor template rule #set3static] 
     */
	public static void setSubTopic(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource, eu.dime.ps.semantic.model.pimo.Topic value) {
		Base.set(model, instanceResource, SUBTOPIC, value);
	}
	
    /**
     * Sets a value of property SubTopic from an instance of Topic 
     * First, all existing values are removed, then this value is added.
     * Cardinality constraints are not checked, but this method exists only for properties with
     * no minCardinality or minCardinality == 1.
	 * @param value the value to be added
	 *
	 * [Generated from RDFReactor template rule #set4dynamic] 
     */
	public void setSubTopic(eu.dime.ps.semantic.model.pimo.Topic value) {
		Base.set(this.model, this.getResource(), SUBTOPIC, value);
	}
  


    /**
     * Removes a value of property SubTopic as an RDF2Go node 
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
	 * @param value the value to be removed
	 *
	 * [Generated from RDFReactor template rule #remove1static] 
     */
	public static void removeSubTopic( Model model, org.ontoware.rdf2go.model.node.Resource instanceResource, org.ontoware.rdf2go.model.node.Node value) {
		Base.remove(model, instanceResource, SUBTOPIC, value);
	}
	
    /**
     * Removes a value of property SubTopic as an RDF2Go node
	 * @param value the value to be removed
	 *
	 * [Generated from RDFReactor template rule #remove1dynamic] 
     */
	public void removeSubTopic( org.ontoware.rdf2go.model.node.Node value) {
		Base.remove(this.model, this.getResource(), SUBTOPIC, value);
	}
    /**
     * Removes a value of property SubTopic given as an instance of Topic 
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
	 * @param value the value to be removed
	 *
	 * [Generated from RDFReactor template rule #remove3static] 
     */
	public static void removeSubTopic(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource, eu.dime.ps.semantic.model.pimo.Topic value) {
		Base.remove(model, instanceResource, SUBTOPIC, value);
	}
	
    /**
     * Removes a value of property SubTopic given as an instance of Topic 
	 * @param value the value to be removed
	 *
	 * [Generated from RDFReactor template rule #remove4dynamic] 
     */
	public void removeSubTopic(eu.dime.ps.semantic.model.pimo.Topic value) {
		Base.remove(this.model, this.getResource(), SUBTOPIC, value);
	}
  
    /**
     * Removes all values of property SubTopic     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
	 *
	 * [Generated from RDFReactor template rule #removeall1static] 
     */
	public static void removeAllSubTopic( Model model, org.ontoware.rdf2go.model.node.Resource instanceResource) {
		Base.removeAll(model, instanceResource, SUBTOPIC);
	}
	
    /**
     * Removes all values of property SubTopic	 *
	 * [Generated from RDFReactor template rule #removeall1dynamic] 
     */
	public void removeAllSubTopic() {
		Base.removeAll(this.model, this.getResource(), SUBTOPIC);
	}
     /**
     * Check if org.ontoware.rdfreactor.generator.java.JProperty@1bd471e7 has at least one value set 
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
     * @return true if this property has at least one value
	 *
	 * [Generated from RDFReactor template rule #get0has-static] 
     */
	public static boolean hasSuperTopic(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource) {
		return Base.has(model, instanceResource, SUPERTOPIC);
	}

    /**
     * Check if org.ontoware.rdfreactor.generator.java.JProperty@1bd471e7 has at least one value set 
     * @return true if this property has at least one value
	 *
	 * [Generated from RDFReactor template rule #get0has-dynamic] 
     */
	public boolean hasSuperTopic() {
		return Base.has(this.model, this.getResource(), SUPERTOPIC);
	}

    /**
     * Check if org.ontoware.rdfreactor.generator.java.JProperty@1bd471e7 has the given value (maybe among other values).  
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
	 * @param value the value to be checked
     * @return true if this property contains (maybe among other) the given value
	 *
	 * [Generated from RDFReactor template rule #get0has-value-static] 
     */
	public static boolean hasSuperTopic(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource, org.ontoware.rdf2go.model.node.Node value ) {
		return Base.hasValue(model, instanceResource, SUPERTOPIC);
	}

    /**
     * Check if org.ontoware.rdfreactor.generator.java.JProperty@1bd471e7 has the given value (maybe among other values).  
	 * @param value the value to be checked
     * @return true if this property contains (maybe among other) the given value
	 *
	 * [Generated from RDFReactor template rule #get0has-value-dynamic] 
     */
	public boolean hasSuperTopic( org.ontoware.rdf2go.model.node.Node value ) {
		return Base.hasValue(this.model, this.getResource(), SUPERTOPIC);
	}

     /**
     * Get all values of property SuperTopic as an Iterator over RDF2Go nodes 
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
     * @return a ClosableIterator of RDF2Go Nodes
	 *
	 * [Generated from RDFReactor template rule #get7static] 
     */
	public static ClosableIterator<org.ontoware.rdf2go.model.node.Node> getAllSuperTopic_asNode(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource) {
		return Base.getAll_asNode(model, instanceResource, SUPERTOPIC);
	}
	
    /**
     * Get all values of property SuperTopic as a ReactorResult of RDF2Go nodes 
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
     * @return a List of RDF2Go Nodes
	 *
	 * [Generated from RDFReactor template rule #get7static-reactor-result] 
     */
	public static ReactorResult<org.ontoware.rdf2go.model.node.Node> getAllSuperTopic_asNode_(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource) {
		return Base.getAll_as(model, instanceResource, SUPERTOPIC, org.ontoware.rdf2go.model.node.Node.class);
	}

    /**
     * Get all values of property SuperTopic as an Iterator over RDF2Go nodes 
     * @return a ClosableIterator of RDF2Go Nodes
	 *
	 * [Generated from RDFReactor template rule #get8dynamic] 
     */
	public ClosableIterator<org.ontoware.rdf2go.model.node.Node> getAllSuperTopic_asNode() {
		return Base.getAll_asNode(this.model, this.getResource(), SUPERTOPIC);
	}

    /**
     * Get all values of property SuperTopic as a ReactorResult of RDF2Go nodes 
     * @return a List of RDF2Go Nodes
	 *
	 * [Generated from RDFReactor template rule #get8dynamic-reactor-result] 
     */
	public ReactorResult<org.ontoware.rdf2go.model.node.Node> getAllSuperTopic_asNode_() {
		return Base.getAll_as(this.model, this.getResource(), SUPERTOPIC, org.ontoware.rdf2go.model.node.Node.class);
	}
     /**
     * Get all values of property SuperTopic     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
     * @return a ClosableIterator of $type
	 *
	 * [Generated from RDFReactor template rule #get11static] 
     */
	public static ClosableIterator<eu.dime.ps.semantic.model.pimo.Topic> getAllSuperTopic(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource) {
		return Base.getAll(model, instanceResource, SUPERTOPIC, eu.dime.ps.semantic.model.pimo.Topic.class);
	}
	
    /**
     * Get all values of property SuperTopic as a ReactorResult of Topic 
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
     * @return a ReactorResult of $type which can conveniently be converted to iterator, list or array
	 *
	 * [Generated from RDFReactor template rule #get11static-reactorresult] 
     */
	public static ReactorResult<eu.dime.ps.semantic.model.pimo.Topic> getAllSuperTopic_as(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource) {
		return Base.getAll_as(model, instanceResource, SUPERTOPIC, eu.dime.ps.semantic.model.pimo.Topic.class);
	}

    /**
     * Get all values of property SuperTopic     * @return a ClosableIterator of $type
	 *
	 * [Generated from RDFReactor template rule #get12dynamic] 
     */
	public ClosableIterator<eu.dime.ps.semantic.model.pimo.Topic> getAllSuperTopic() {
		return Base.getAll(this.model, this.getResource(), SUPERTOPIC, eu.dime.ps.semantic.model.pimo.Topic.class);
	}

    /**
     * Get all values of property SuperTopic as a ReactorResult of Topic 
     * @return a ReactorResult of $type which can conveniently be converted to iterator, list or array
	 *
	 * [Generated from RDFReactor template rule #get12dynamic-reactorresult] 
     */
	public ReactorResult<eu.dime.ps.semantic.model.pimo.Topic> getAllSuperTopic_as() {
		return Base.getAll_as(this.model, this.getResource(), SUPERTOPIC, eu.dime.ps.semantic.model.pimo.Topic.class);
	}
 
    /**
     * Adds a value to property SuperTopic as an RDF2Go node 
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
	 * @param value the value to be added
	 *
	 * [Generated from RDFReactor template rule #add1static] 
     */
	public static void addSuperTopic(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource, org.ontoware.rdf2go.model.node.Node value) {
		Base.add(model, instanceResource, SUPERTOPIC, value);
	}
	
    /**
     * Adds a value to property SuperTopic as an RDF2Go node 
	 * @param value the value to be added
	 *
	 * [Generated from RDFReactor template rule #add1dynamic] 
     */
	public void addSuperTopic( org.ontoware.rdf2go.model.node.Node value) {
		Base.add(this.model, this.getResource(), SUPERTOPIC, value);
	}
    /**
     * Adds a value to property SuperTopic from an instance of Topic 
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
	 *
	 * [Generated from RDFReactor template rule #add3static] 
     */
	public static void addSuperTopic(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource, eu.dime.ps.semantic.model.pimo.Topic value) {
		Base.add(model, instanceResource, SUPERTOPIC, value);
	}
	
    /**
     * Adds a value to property SuperTopic from an instance of Topic 
	 *
	 * [Generated from RDFReactor template rule #add4dynamic] 
     */
	public void addSuperTopic(eu.dime.ps.semantic.model.pimo.Topic value) {
		Base.add(this.model, this.getResource(), SUPERTOPIC, value);
	}
  

    /**
     * Sets a value of property SuperTopic from an RDF2Go node.
     * First, all existing values are removed, then this value is added.
     * Cardinality constraints are not checked, but this method exists only for properties with
     * no minCardinality or minCardinality == 1.
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
	 * @param value the value to be set
	 *
	 * [Generated from RDFReactor template rule #set1static] 
     */
	public static void setSuperTopic( Model model, org.ontoware.rdf2go.model.node.Resource instanceResource, org.ontoware.rdf2go.model.node.Node value) {
		Base.set(model, instanceResource, SUPERTOPIC, value);
	}
	
    /**
     * Sets a value of property SuperTopic from an RDF2Go node.
     * First, all existing values are removed, then this value is added.
     * Cardinality constraints are not checked, but this method exists only for properties with
     * no minCardinality or minCardinality == 1.
	 * @param value the value to be added
	 *
	 * [Generated from RDFReactor template rule #set1dynamic] 
     */
	public void setSuperTopic( org.ontoware.rdf2go.model.node.Node value) {
		Base.set(this.model, this.getResource(), SUPERTOPIC, value);
	}
    /**
     * Sets a value of property SuperTopic from an instance of Topic 
     * First, all existing values are removed, then this value is added.
     * Cardinality constraints are not checked, but this method exists only for properties with
     * no minCardinality or minCardinality == 1.
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
	 * @param value the value to be added
	 *
	 * [Generated from RDFReactor template rule #set3static] 
     */
	public static void setSuperTopic(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource, eu.dime.ps.semantic.model.pimo.Topic value) {
		Base.set(model, instanceResource, SUPERTOPIC, value);
	}
	
    /**
     * Sets a value of property SuperTopic from an instance of Topic 
     * First, all existing values are removed, then this value is added.
     * Cardinality constraints are not checked, but this method exists only for properties with
     * no minCardinality or minCardinality == 1.
	 * @param value the value to be added
	 *
	 * [Generated from RDFReactor template rule #set4dynamic] 
     */
	public void setSuperTopic(eu.dime.ps.semantic.model.pimo.Topic value) {
		Base.set(this.model, this.getResource(), SUPERTOPIC, value);
	}
  


    /**
     * Removes a value of property SuperTopic as an RDF2Go node 
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
	 * @param value the value to be removed
	 *
	 * [Generated from RDFReactor template rule #remove1static] 
     */
	public static void removeSuperTopic( Model model, org.ontoware.rdf2go.model.node.Resource instanceResource, org.ontoware.rdf2go.model.node.Node value) {
		Base.remove(model, instanceResource, SUPERTOPIC, value);
	}
	
    /**
     * Removes a value of property SuperTopic as an RDF2Go node
	 * @param value the value to be removed
	 *
	 * [Generated from RDFReactor template rule #remove1dynamic] 
     */
	public void removeSuperTopic( org.ontoware.rdf2go.model.node.Node value) {
		Base.remove(this.model, this.getResource(), SUPERTOPIC, value);
	}
    /**
     * Removes a value of property SuperTopic given as an instance of Topic 
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
	 * @param value the value to be removed
	 *
	 * [Generated from RDFReactor template rule #remove3static] 
     */
	public static void removeSuperTopic(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource, eu.dime.ps.semantic.model.pimo.Topic value) {
		Base.remove(model, instanceResource, SUPERTOPIC, value);
	}
	
    /**
     * Removes a value of property SuperTopic given as an instance of Topic 
	 * @param value the value to be removed
	 *
	 * [Generated from RDFReactor template rule #remove4dynamic] 
     */
	public void removeSuperTopic(eu.dime.ps.semantic.model.pimo.Topic value) {
		Base.remove(this.model, this.getResource(), SUPERTOPIC, value);
	}
  
    /**
     * Removes all values of property SuperTopic     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
	 *
	 * [Generated from RDFReactor template rule #removeall1static] 
     */
	public static void removeAllSuperTopic( Model model, org.ontoware.rdf2go.model.node.Resource instanceResource) {
		Base.removeAll(model, instanceResource, SUPERTOPIC);
	}
	
    /**
     * Removes all values of property SuperTopic	 *
	 * [Generated from RDFReactor template rule #removeall1dynamic] 
     */
	public void removeAllSuperTopic() {
		Base.removeAll(this.model, this.getResource(), SUPERTOPIC);
	}
 }