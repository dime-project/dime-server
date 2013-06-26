package eu.dime.ps.semantic.model.nfo;

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
 *   <li> ContainsPlacemark </li>
 * </ul>
 *
 * class- This class was generated by <a href="http://RDFReactor.semweb4j.org">RDFReactor</a> */
public class PlacemarkContainer extends eu.dime.ps.semantic.model.nie.InformationElement {

    /** http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#PlacemarkContainer */
    @SuppressWarnings("hiding")
	public static final URI RDFS_CLASS = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#PlacemarkContainer", false);

    /** http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#containsPlacemark */
    @SuppressWarnings("hiding")
	public static final URI CONTAINSPLACEMARK = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#containsPlacemark",false);

    /** 
     * All property-URIs with this class as domain.
     * All properties of all super-classes are also available. 
     */
    @SuppressWarnings("hiding")
    public static final URI[] MANAGED_URIS = {
      new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#containsPlacemark",false) 
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
	protected PlacemarkContainer (Model model, URI classURI, org.ontoware.rdf2go.model.node.Resource instanceIdentifier, boolean write) {
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
	public PlacemarkContainer (Model model, org.ontoware.rdf2go.model.node.Resource instanceIdentifier, boolean write) {
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
	public PlacemarkContainer (Model model, String uriString, boolean write) throws ModelRuntimeException {
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
	public PlacemarkContainer (Model model, BlankNode bnode, boolean write) {
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
	public PlacemarkContainer (Model model, boolean write) {
		super(model, RDFS_CLASS, model.newRandomUniqueURI(), write);
	}

    ///////////////////////////////////////////////////////////////////
    // typing

	/**
	 * Return an existing instance of this class in the model. No statements are written.
	 * @param model an RDF2Go model
	 * @param instanceResource an RDF2Go resource
	 * @return an instance of PlacemarkContainer  or null if none existst
	 *
	 * [Generated from RDFReactor template rule #class0] 
	 */
	public static PlacemarkContainer  getInstance(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource) {
		return Base.getInstance(model, instanceResource, PlacemarkContainer.class);
	}

	/**
	 * Create a new instance of this class in the model. 
	 * That is, create the statement (instanceResource, RDF.type, http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#PlacemarkContainer).
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
	public static ReactorResult<? extends PlacemarkContainer> getAllInstances_as(Model model) {
		return Base.getAllInstances_as(model, RDFS_CLASS, PlacemarkContainer.class );
	}

    /**
	 * Remove rdf:type PlacemarkContainer from this instance. Other triples are not affected.
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
     * Check if org.ontoware.rdfreactor.generator.java.JProperty@3b91ebb0 has at least one value set 
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
     * @return true if this property has at least one value
	 *
	 * [Generated from RDFReactor template rule #get0has-static] 
     */
	public static boolean hasContainsPlacemark(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource) {
		return Base.has(model, instanceResource, CONTAINSPLACEMARK);
	}

    /**
     * Check if org.ontoware.rdfreactor.generator.java.JProperty@3b91ebb0 has at least one value set 
     * @return true if this property has at least one value
	 *
	 * [Generated from RDFReactor template rule #get0has-dynamic] 
     */
	public boolean hasContainsPlacemark() {
		return Base.has(this.model, this.getResource(), CONTAINSPLACEMARK);
	}

    /**
     * Check if org.ontoware.rdfreactor.generator.java.JProperty@3b91ebb0 has the given value (maybe among other values).  
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
	 * @param value the value to be checked
     * @return true if this property contains (maybe among other) the given value
	 *
	 * [Generated from RDFReactor template rule #get0has-value-static] 
     */
	public static boolean hasContainsPlacemark(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource, org.ontoware.rdf2go.model.node.Node value ) {
		return Base.hasValue(model, instanceResource, CONTAINSPLACEMARK);
	}

    /**
     * Check if org.ontoware.rdfreactor.generator.java.JProperty@3b91ebb0 has the given value (maybe among other values).  
	 * @param value the value to be checked
     * @return true if this property contains (maybe among other) the given value
	 *
	 * [Generated from RDFReactor template rule #get0has-value-dynamic] 
     */
	public boolean hasContainsPlacemark( org.ontoware.rdf2go.model.node.Node value ) {
		return Base.hasValue(this.model, this.getResource(), CONTAINSPLACEMARK);
	}

     /**
     * Get all values of property ContainsPlacemark as an Iterator over RDF2Go nodes 
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
     * @return a ClosableIterator of RDF2Go Nodes
	 *
	 * [Generated from RDFReactor template rule #get7static] 
     */
	public static ClosableIterator<org.ontoware.rdf2go.model.node.Node> getAllContainsPlacemark_asNode(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource) {
		return Base.getAll_asNode(model, instanceResource, CONTAINSPLACEMARK);
	}
	
    /**
     * Get all values of property ContainsPlacemark as a ReactorResult of RDF2Go nodes 
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
     * @return a List of RDF2Go Nodes
	 *
	 * [Generated from RDFReactor template rule #get7static-reactor-result] 
     */
	public static ReactorResult<org.ontoware.rdf2go.model.node.Node> getAllContainsPlacemark_asNode_(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource) {
		return Base.getAll_as(model, instanceResource, CONTAINSPLACEMARK, org.ontoware.rdf2go.model.node.Node.class);
	}

    /**
     * Get all values of property ContainsPlacemark as an Iterator over RDF2Go nodes 
     * @return a ClosableIterator of RDF2Go Nodes
	 *
	 * [Generated from RDFReactor template rule #get8dynamic] 
     */
	public ClosableIterator<org.ontoware.rdf2go.model.node.Node> getAllContainsPlacemark_asNode() {
		return Base.getAll_asNode(this.model, this.getResource(), CONTAINSPLACEMARK);
	}

    /**
     * Get all values of property ContainsPlacemark as a ReactorResult of RDF2Go nodes 
     * @return a List of RDF2Go Nodes
	 *
	 * [Generated from RDFReactor template rule #get8dynamic-reactor-result] 
     */
	public ReactorResult<org.ontoware.rdf2go.model.node.Node> getAllContainsPlacemark_asNode_() {
		return Base.getAll_as(this.model, this.getResource(), CONTAINSPLACEMARK, org.ontoware.rdf2go.model.node.Node.class);
	}
     /**
     * Get all values of property ContainsPlacemark     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
     * @return a ClosableIterator of $type
	 *
	 * [Generated from RDFReactor template rule #get11static] 
     */
	public static ClosableIterator<eu.dime.ps.semantic.model.nfo.Placemark> getAllContainsPlacemark(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource) {
		return Base.getAll(model, instanceResource, CONTAINSPLACEMARK, eu.dime.ps.semantic.model.nfo.Placemark.class);
	}
	
    /**
     * Get all values of property ContainsPlacemark as a ReactorResult of Placemark 
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
     * @return a ReactorResult of $type which can conveniently be converted to iterator, list or array
	 *
	 * [Generated from RDFReactor template rule #get11static-reactorresult] 
     */
	public static ReactorResult<eu.dime.ps.semantic.model.nfo.Placemark> getAllContainsPlacemark_as(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource) {
		return Base.getAll_as(model, instanceResource, CONTAINSPLACEMARK, eu.dime.ps.semantic.model.nfo.Placemark.class);
	}

    /**
     * Get all values of property ContainsPlacemark     * @return a ClosableIterator of $type
	 *
	 * [Generated from RDFReactor template rule #get12dynamic] 
     */
	public ClosableIterator<eu.dime.ps.semantic.model.nfo.Placemark> getAllContainsPlacemark() {
		return Base.getAll(this.model, this.getResource(), CONTAINSPLACEMARK, eu.dime.ps.semantic.model.nfo.Placemark.class);
	}

    /**
     * Get all values of property ContainsPlacemark as a ReactorResult of Placemark 
     * @return a ReactorResult of $type which can conveniently be converted to iterator, list or array
	 *
	 * [Generated from RDFReactor template rule #get12dynamic-reactorresult] 
     */
	public ReactorResult<eu.dime.ps.semantic.model.nfo.Placemark> getAllContainsPlacemark_as() {
		return Base.getAll_as(this.model, this.getResource(), CONTAINSPLACEMARK, eu.dime.ps.semantic.model.nfo.Placemark.class);
	}
 
    /**
     * Adds a value to property ContainsPlacemark as an RDF2Go node 
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
	 * @param value the value to be added
	 *
	 * [Generated from RDFReactor template rule #add1static] 
     */
	public static void addContainsPlacemark(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource, org.ontoware.rdf2go.model.node.Node value) {
		Base.add(model, instanceResource, CONTAINSPLACEMARK, value);
	}
	
    /**
     * Adds a value to property ContainsPlacemark as an RDF2Go node 
	 * @param value the value to be added
	 *
	 * [Generated from RDFReactor template rule #add1dynamic] 
     */
	public void addContainsPlacemark( org.ontoware.rdf2go.model.node.Node value) {
		Base.add(this.model, this.getResource(), CONTAINSPLACEMARK, value);
	}
    /**
     * Adds a value to property ContainsPlacemark from an instance of Placemark 
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
	 *
	 * [Generated from RDFReactor template rule #add3static] 
     */
	public static void addContainsPlacemark(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource, eu.dime.ps.semantic.model.nfo.Placemark value) {
		Base.add(model, instanceResource, CONTAINSPLACEMARK, value);
	}
	
    /**
     * Adds a value to property ContainsPlacemark from an instance of Placemark 
	 *
	 * [Generated from RDFReactor template rule #add4dynamic] 
     */
	public void addContainsPlacemark(eu.dime.ps.semantic.model.nfo.Placemark value) {
		Base.add(this.model, this.getResource(), CONTAINSPLACEMARK, value);
	}
  

    /**
     * Sets a value of property ContainsPlacemark from an RDF2Go node.
     * First, all existing values are removed, then this value is added.
     * Cardinality constraints are not checked, but this method exists only for properties with
     * no minCardinality or minCardinality == 1.
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
	 * @param value the value to be set
	 *
	 * [Generated from RDFReactor template rule #set1static] 
     */
	public static void setContainsPlacemark( Model model, org.ontoware.rdf2go.model.node.Resource instanceResource, org.ontoware.rdf2go.model.node.Node value) {
		Base.set(model, instanceResource, CONTAINSPLACEMARK, value);
	}
	
    /**
     * Sets a value of property ContainsPlacemark from an RDF2Go node.
     * First, all existing values are removed, then this value is added.
     * Cardinality constraints are not checked, but this method exists only for properties with
     * no minCardinality or minCardinality == 1.
	 * @param value the value to be added
	 *
	 * [Generated from RDFReactor template rule #set1dynamic] 
     */
	public void setContainsPlacemark( org.ontoware.rdf2go.model.node.Node value) {
		Base.set(this.model, this.getResource(), CONTAINSPLACEMARK, value);
	}
    /**
     * Sets a value of property ContainsPlacemark from an instance of Placemark 
     * First, all existing values are removed, then this value is added.
     * Cardinality constraints are not checked, but this method exists only for properties with
     * no minCardinality or minCardinality == 1.
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
	 * @param value the value to be added
	 *
	 * [Generated from RDFReactor template rule #set3static] 
     */
	public static void setContainsPlacemark(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource, eu.dime.ps.semantic.model.nfo.Placemark value) {
		Base.set(model, instanceResource, CONTAINSPLACEMARK, value);
	}
	
    /**
     * Sets a value of property ContainsPlacemark from an instance of Placemark 
     * First, all existing values are removed, then this value is added.
     * Cardinality constraints are not checked, but this method exists only for properties with
     * no minCardinality or minCardinality == 1.
	 * @param value the value to be added
	 *
	 * [Generated from RDFReactor template rule #set4dynamic] 
     */
	public void setContainsPlacemark(eu.dime.ps.semantic.model.nfo.Placemark value) {
		Base.set(this.model, this.getResource(), CONTAINSPLACEMARK, value);
	}
  


    /**
     * Removes a value of property ContainsPlacemark as an RDF2Go node 
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
	 * @param value the value to be removed
	 *
	 * [Generated from RDFReactor template rule #remove1static] 
     */
	public static void removeContainsPlacemark( Model model, org.ontoware.rdf2go.model.node.Resource instanceResource, org.ontoware.rdf2go.model.node.Node value) {
		Base.remove(model, instanceResource, CONTAINSPLACEMARK, value);
	}
	
    /**
     * Removes a value of property ContainsPlacemark as an RDF2Go node
	 * @param value the value to be removed
	 *
	 * [Generated from RDFReactor template rule #remove1dynamic] 
     */
	public void removeContainsPlacemark( org.ontoware.rdf2go.model.node.Node value) {
		Base.remove(this.model, this.getResource(), CONTAINSPLACEMARK, value);
	}
    /**
     * Removes a value of property ContainsPlacemark given as an instance of Placemark 
     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
	 * @param value the value to be removed
	 *
	 * [Generated from RDFReactor template rule #remove3static] 
     */
	public static void removeContainsPlacemark(Model model, org.ontoware.rdf2go.model.node.Resource instanceResource, eu.dime.ps.semantic.model.nfo.Placemark value) {
		Base.remove(model, instanceResource, CONTAINSPLACEMARK, value);
	}
	
    /**
     * Removes a value of property ContainsPlacemark given as an instance of Placemark 
	 * @param value the value to be removed
	 *
	 * [Generated from RDFReactor template rule #remove4dynamic] 
     */
	public void removeContainsPlacemark(eu.dime.ps.semantic.model.nfo.Placemark value) {
		Base.remove(this.model, this.getResource(), CONTAINSPLACEMARK, value);
	}
  
    /**
     * Removes all values of property ContainsPlacemark     * @param model an RDF2Go model
     * @param resource an RDF2Go resource
	 *
	 * [Generated from RDFReactor template rule #removeall1static] 
     */
	public static void removeAllContainsPlacemark( Model model, org.ontoware.rdf2go.model.node.Resource instanceResource) {
		Base.removeAll(model, instanceResource, CONTAINSPLACEMARK);
	}
	
    /**
     * Removes all values of property ContainsPlacemark	 *
	 * [Generated from RDFReactor template rule #removeall1dynamic] 
     */
	public void removeAllContainsPlacemark() {
		Base.removeAll(this.model, this.getResource(), CONTAINSPLACEMARK);
	}
 }