package eu.dime.ps.semantic.service.impl;

import ie.deri.smile.rdf.TripleStore;
import ie.deri.smile.rdf.util.ModelUtils;
import ie.deri.smile.rdf.util.ResourceUtils;
import ie.deri.smile.vocabulary.DLPO;
import ie.deri.smile.vocabulary.FOAF;
import ie.deri.smile.vocabulary.NAO;
import ie.deri.smile.vocabulary.NCAL;
import ie.deri.smile.vocabulary.NCO;
import ie.deri.smile.vocabulary.NIE;
import ie.deri.smile.vocabulary.NRL;
import ie.deri.smile.vocabulary.NSO;
import ie.deri.smile.vocabulary.PIMO;
import ie.deri.smile.vocabulary.PPO;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.io.IOUtils;
import org.ontoware.aifbcommons.collection.ClosableIterable;
import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.exception.MalformedQueryException;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.exception.QueryLanguageNotSupportedException;
import org.ontoware.rdf2go.model.FindableModelSet;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.ModelSet;
import org.ontoware.rdf2go.model.QueryResultTable;
import org.ontoware.rdf2go.model.QueryRow;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.Syntax;
import org.ontoware.rdf2go.model.node.DatatypeLiteral;
import org.ontoware.rdf2go.model.node.Literal;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.PlainLiteral;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.Variable;
import org.ontoware.rdf2go.model.node.impl.DatatypeLiteralImpl;
import org.ontoware.rdf2go.model.node.impl.PlainLiteralImpl;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdf2go.util.Iterators;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.ontoware.rdf2go.vocabulary.RDFS;
import org.ontoware.rdf2go.vocabulary.XSD;
import org.ontoware.rdfreactor.schema.rdfs.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.dime.ps.semantic.BroadcastManager;
import eu.dime.ps.semantic.Event;
import eu.dime.ps.semantic.exception.NameNotUniqueException;
import eu.dime.ps.semantic.exception.NotFoundException;
import eu.dime.ps.semantic.exception.OntologyInvalidException;
import eu.dime.ps.semantic.exception.ResourceExistsException;
import eu.dime.ps.semantic.model.ModelFactory;
import eu.dime.ps.semantic.model.PIMOFactory;
import eu.dime.ps.semantic.model.nao.Tag;
import eu.dime.ps.semantic.model.nco.PersonContact;
import eu.dime.ps.semantic.model.pimo.Person;
import eu.dime.ps.semantic.model.pimo.PersonalInformationModel;
import eu.dime.ps.semantic.model.pimo.Thing;
import eu.dime.ps.semantic.query.Query;
import eu.dime.ps.semantic.query.Queryable;
import eu.dime.ps.semantic.query.impl.PimoQuery;
import eu.dime.ps.semantic.rdf.ResourceStore;
import eu.dime.ps.semantic.rdf.URIGenerator;
import eu.dime.ps.semantic.rdf.impl.ResourceStoreImpl;
import eu.dime.ps.semantic.service.TaggingService;
import eu.dime.ps.semantic.service.exception.PimoConfigurationException;
import eu.dime.ps.semantic.service.exception.PimoException;
import eu.dime.ps.semantic.util.DateUtils;
import eu.dime.ps.semantic.util.StringUtils;
import eu.dime.ps.semantic.vocabulary.DefaultOntologies;

public class PimoService implements TaggingService, Queryable {
	
	private static final Logger logger = LoggerFactory.getLogger(PimoService.class);

	public static final URI CONFIG_GRAPH_URI = new URIImpl("urn:dime:local:config");

	/**
	 * use this in most SPARQL queries
	 */
	public static final String SPARQL_PREAMBLE = StringUtils.strjoinNL(
			"PREFIX rdfs:	<http://www.w3.org/2000/01/rdf-schema#>",
			"PREFIX rdf:	<http://www.w3.org/1999/02/22-rdf-syntax-ns#>",
			"PREFIX xsd:	<http://www.w3.org/2001/XMLSchema#>",
			"PREFIX nrl:	"+NRL.NS_NRL.toSPARQL(),
			"PREFIX nie:	"+NIE.NS_NIE.toSPARQL(),
			"PREFIX pimo:	"+PIMO.NS_PIMO.toSPARQL(),
			"PREFIX nao:	"+NAO.NS_NAO.toSPARQL(),
			"PREFIX nco:	"+NCO.NS_NCO.toSPARQL(),
			"PREFIX ppo:	"+PPO.NS_PPO.toSPARQL(),
			"PREFIX dlpo:	"+DLPO.NS_DLPO.toSPARQL(),
			"PREFIX ncal:	"+NCAL.NS_NCAL.toSPARQL(),
			"PREFIX nso:	"+NSO.NS_NSO.toSPARQL());

	private static final String CLASSES_PATH = "classes:";
	private static final String PROPERTIES_PATH = "properties:";
	private static final String UUID_PATH = "uuid:";
	private static final String NAMES_PATH = "names:";
	private static final String TAGS_PATH = "tags:";

	private final TripleStore tripleStore;
	private final ResourceStore resourceStore;
	private final String name;

	private final PIMOFactory pimoFactory;
	
	private URI userNamespace;
	private URI userPimoUri;
	private URI userUri;
	private String userName;
	private String userLanguage;
	private String defaultLanguage;
	
	private Model userPIM;
	
	private final ModelFactory modelFactory = new ModelFactory();

	// cache of URIs created while PimoService has been running, to avoid hitting the RDF store.
	public List<URI> createdURIs = new CopyOnWriteArrayList<URI>();
	
	/**
	 * Contains the property URIs of properties that are sub-properties of pimo:identifier.
	 */
	private Set<URI> identifyingProperties = null;
	
	private final BroadcastManager broadcastManager = BroadcastManager.getInstance();

	public PimoService(String userId, String username, TripleStore tripleStore) throws PimoConfigurationException, PimoException {
		logger.info("Setting up PIMO Service [username="+username+"]");
		
		this.tripleStore = tripleStore;
		this.resourceStore = new ResourceStoreImpl(this.tripleStore);
		this.name = tripleStore.getName();
		this.pimoFactory = modelFactory.getPIMOFactory();

		initialize(userId, username);
	}
	
	public PimoService(Model config, TripleStore tripleStore) throws PimoConfigurationException, PimoException {
		logger.info("Setting up PIMO Service [config="+config.serialize(Syntax.Turtle)+"]");
		
		this.tripleStore = tripleStore;
		this.resourceStore = new ResourceStoreImpl(this.tripleStore);
		this.name = tripleStore.getName();
		this.pimoFactory = modelFactory.getPIMOFactory();
		
		initialize(config);
	}

	public PimoService(String configFile, TripleStore tripleStore) throws PimoConfigurationException, PimoException {
		logger.info("Setting up PIMO Service [configFile="+configFile+"]");
		
		this.tripleStore = tripleStore;
		this.resourceStore = new ResourceStoreImpl(this.tripleStore);
		this.name = tripleStore.getName();
		this.pimoFactory = modelFactory.getPIMOFactory();
		
		// load config values from file
		Model configModel = RDF2Go.getModelFactory().createModel().open();
		try {
			ModelUtils.loadFromInputStream(
					this.getClass().getClassLoader().getResourceAsStream(configFile),
					Syntax.Trig,
					configModel);
		} catch (ModelRuntimeException e) {
			logger.error("PimoService cannot been initialized: "+e.getMessage(), e);
			throw new PimoConfigurationException("PimoService cannot been initialized.", e);
		} catch (IOException e) {
			logger.error("PimoService cannot been initialized: "+e.getMessage(), e);
			throw new PimoConfigurationException("PimoService cannot been initialized.", e);
		}

		initialize(configModel);
	}

	/**
	 * Creates a new PimoService using a given triple store for persistence. If the triple store does
	 * not contain a PIM, a default one will be created using the configuration from the graph 'urn:dime:local:config'.
	 * 
	 * @param tripleStore RDF store used for persistence
	 * @throws PimoConfigurationException
	 * @throws PimoException
	 */
	public PimoService(TripleStore tripleStore) throws PimoConfigurationException, PimoException {
		logger.info("Setting up PIMO Service");

		this.tripleStore = tripleStore;
		this.resourceStore = new ResourceStoreImpl(this.tripleStore);
		this.name = tripleStore.getName();
		this.pimoFactory = modelFactory.getPIMOFactory();
		
		initialize(tripleStore.getModel(CONFIG_GRAPH_URI));
	}

	public void initialize(Model config) throws PimoConfigurationException, PimoException {
		String query = StringUtils.strjoinNL(
				"PREFIX serverconfig: <http://ont.semanticdesktop.org/ontologies/2007/01/16/server-conf#>",
				"SELECT ?userNamespace ?userPimoUri ?userUri ?userName",
				"WHERE {",
				"	?config a serverconfig:User .",
				"	?config serverconfig:userNamespace ?userNamespace .",
				"	?config serverconfig:userPimoUri ?userPimoUri .",
				"	?config serverconfig:userUri ?userUri .",
				"	?config serverconfig:userName ?userName .",
				"}");
		QueryResultTable qres = config.sparqlSelect(query);
		ClosableIterator<QueryRow> res = qres.iterator();
		if (!res.hasNext()) {
			throw new PimoConfigurationException("cannot find user configuration in config-repository. Missing an instance of serverconfig:User with required properties.");
		}
		QueryRow row = res.next();
		res.close();

		// read config
		this.userNamespace = new URIImpl(row.getValue("userNamespace").toString());
		this.userPimoUri = new URIImpl(row.getValue("userPimoUri").toString());
		this.userUri = new URIImpl(row.getValue("userUri").toString());
		this.userName = row.getLiteralValue("userName");
		
		// only english is supported at the moment
		this.userLanguage = "en";
		this.defaultLanguage = "en";
		
		// saving config metadata, allowing a Pimo Service to self-contain its configuration data
		this.tripleStore.addModel(config, CONFIG_GRAPH_URI);
		
		// check if PIMO is created, and create it in case it's not
		if (checkPimoNeedsCreation()) {
			createNewPimo();
		}
	}
	
	private void initialize(String userId, String username) throws PimoConfigurationException, PimoException {
		Model configModel = RDF2Go.getModelFactory().createModel().open();

		URI config = new URIImpl("urn:dime:local:userconfig");
		String userNamespace = "user:" + userId + ":";
		configModel.addStatement(config, RDF.type, new URIImpl("http://ont.semanticdesktop.org/ontologies/2007/01/16/server-conf#User"));
		configModel.addStatement(config, new URIImpl("http://ont.semanticdesktop.org/ontologies/2007/01/16/server-conf#userNamespace"), new URIImpl(userNamespace));
		configModel.addStatement(config, new URIImpl("http://ont.semanticdesktop.org/ontologies/2007/01/16/server-conf#userUri"), new URIImpl(userNamespace + "me"));
		configModel.addStatement(config, new URIImpl("http://ont.semanticdesktop.org/ontologies/2007/01/16/server-conf#userPimoUri"), new URIImpl(userNamespace + "PIM"));
		configModel.addStatement(config, new URIImpl("http://ont.semanticdesktop.org/ontologies/2007/01/16/server-conf#userName"), new PlainLiteralImpl(username));
		
		initialize(configModel);
	}
	
	/**
	 * Access the underlying RDF Store.
	 * 
	 * @return the RDF store
	 */
	public TripleStore getTripleStore() {
		return tripleStore;
	}

	public String getName() {
		return name;
	}
	
	/**
	 * Returns the Personal Information Model of the user.
	 * 
	 * @return the Personal Information Model of the user
	 */
	public Model getUserPIM() {
		if (userPIM == null) {
			userPIM = tripleStore.getModel(this.userPimoUri);
		}
		return userPIM;
	}

	/**
	 * Returns the URI identifying the PIMO of the user.
	 * 
	 * @return the URI identifying the PIMO of the user
	 */
	public URI getPimoUri() {
		return userPimoUri;
	}

	/**
	 * Changes the URI identifying the PIMO of the user.
	 * 
	 * @param pimoUri the URI identifying the PIMO of the user
	 */
	public void setPimoUri(URI pimoUri) {
		List<Statement> pimo = new ArrayList<Statement>();
		Iterators.addAll(tripleStore.findStatements(
				this.userPimoUri, Variable.ANY, Variable.ANY, Variable.ANY), pimo);
		tripleStore.removeStatements(this.userPimoUri, Variable.ANY, Variable.ANY, Variable.ANY);
		for (Statement stmt : pimo) {
			tripleStore.addStatement(userPimoUri, stmt.getSubject(), stmt.getPredicate(), stmt.getObject());
		}
		tripleStore.replaceUri(this.userPimoUri, userPimoUri);
		this.userPimoUri = pimoUri;
	}

	/**
	 * Gets the namespace of the <em>user</em> that is the prefix for
	 * user-defined URIs.
	 * Example: http://digital.me/users/irivera/
	 * 
	 * @return the URI namespace
	 */
	public URI getUserNamespace() {
		return userNamespace;
	}

	/**
	 * Returns the URI representing the user inside the PIMO.
	 * 
	 * @return the URI of the resource of the user
	 */
	public URI getUserUri() {
		return userUri;
	}

	/**
	 * Changes the URI representing the user inside the PIMO.
	 * 
	 * @param userUri the URI of the resource of the user
	 */
	public void setUserUri(URI userUri) {
		tripleStore.replaceUri(this.userUri, userUri);
		this.userUri = userUri;
	}

	/**
	 * Returns the username of the user inside the PIMO.
	 * 
	 * @return username of the PIMO user
	 */
	public String getUserName() {
		return userName;
	}
	
	public Person getUser() throws NotFoundException {
		return get(userUri, Person.class);
	}
	
	/**
	 * Clears the RDF store, and re-initializes the PIMO service.
	 */
	public void clear() {
		// makes a copy of the config graph to restored after triple store is cleared
		List<Statement> config = new ArrayList<Statement>();
		ClosableIterator<Statement> statements = tripleStore.getModel(CONFIG_GRAPH_URI).iterator();
		while (statements.hasNext()) {
			config.add(statements.next());
		}
		statements.close();
		
		// clear triple store an restore config
		tripleStore.clear();
		tripleStore.addAll(CONFIG_GRAPH_URI, config.iterator());
		
		// create PIMO information
		try {
			createNewPimo();
		} catch (PimoConfigurationException e) {
			logger.error("PIMO graph cannot be created", e);
		}
	}

	/**
	 * Checks if the PIMO is not created yet and {@link #createNewPimo()} needs to be called.
	 * It checks if the default ontologies are there or not.
	 * 
	 * @return true, if {@link #createNewPimo()} needs to be called.
	 * @throws PimoException 
	 */
	public boolean checkPimoNeedsCreation() throws PimoException {
		try {
			// check if pimo user is in the repository
			Model pimo = tripleStore.getModel(this.userPimoUri);
			pimo.open();
			boolean result = !pimo.contains(getUserUri(), RDF.type, PIMO.Person);
			if (!result) {
				// are the default ontologies here?
				for (DefaultOntologies.Ontology ont : DefaultOntologies.getDefaults()) {
					boolean misses = (!tripleStore.containsOntology(ont.getUri()));
					logger.debug("Default ontology '"+ont.getUri()+"' is in the store: "+!misses);
					result = result || misses;
				}
			}
			pimo.close();
			return result;
		} catch (ModelRuntimeException e) {
			throw new PimoException("Unable to check if user is defined in pimo: "+e, e);
		}
	}
	
	/**
	 * Initializes the underlying RDF store, adding a set of default ontologies
	 * (@see {@link DefaultOntologies}), and adding the graphs and data for
	 * the PIMO owner. The initial configuration is read from the configuration
	 * graph 'urn:dime:local:config'
	 * 
	 * Note: should only be invoked to initialize the Pimo Service if 
	 * {@link #checkPimoNeedsCreation()} returns true.
	 * 
	 * @throws PimoConfigurationException
	 */
	public void createNewPimo() throws PimoConfigurationException {
		for (DefaultOntologies.Ontology ont : DefaultOntologies.getDefaults()) {
			try {
				logger.info("Adding default ontology '"+ ont.getUri()+"'");
				
				ModelSet ontModelSet = RDF2Go.getModelFactory().createModelSet();
				ontModelSet.open();
				ModelUtils.loadFromInputStream(ont.getInputStream(), ont.getSyntax(), ontModelSet);
				
				URI ontUri = ont.getUri();
				URI ontMetaUri = ont.getMetadataUri();
				Model ontModel = ontModelSet.getModel(ontUri);
				if (ontMetaUri == null) {
					tripleStore.addOntology(ontUri, ontModel);
				} else {
					Model ontMetaModel = ontModelSet.getModel(ont.getMetadataUri());
					tripleStore.addOntology(ontUri, ontModel, ontMetaModel);
				}
			} catch (Exception e) {
				logger.error("Cannot add default ontology '"+ ont.getUri()+"': "+ e, e);
			}
		}
		
		// adds pre-defined DPO instances
		try {
			byte[] bytes = IOUtils.toByteArray(PimoService.class.getClassLoader().getResourceAsStream("vocabularies/dpo/dpo_instances.trig"));
			String dpoInstances = new String(bytes);
			dpoInstances = dpoInstances.replaceAll("%namespace%", this.userNamespace.toString());
			dpoInstances = dpoInstances.replaceAll("%graph%", this.userPimoUri.toString());
			InputStream is = new ByteArrayInputStream(dpoInstances.getBytes());
			ModelSet modelSet = RDF2Go.getModelFactory().createModelSet();
			modelSet.open();
			ModelUtils.loadFromInputStream(is, Syntax.Trig, modelSet);
			tripleStore.addAll(modelSet.iterator());
		} catch (Exception e) {
			logger.error("DPO pre-defined instances could not be loaded: "+e.getMessage(), e);
		}
		
		if (userUri == null) {
			throw new PimoConfigurationException("user URI not configured");
		}

		logger.info("Adding data for user into his personal information model '"+this.userPimoUri+"'");
		DatatypeLiteral now = DateUtils.currentDateTimeAsLiteral();
		Model pimo = tripleStore.createModel(userPimoUri);

		// create user's PIMO
		pimo.addStatement(userPimoUri, RDF.type, PIMO.PersonalInformationModel);
		pimo.addStatement(userPimoUri, RDF.type, NRL.Ontology);
		pimo.addStatement(userPimoUri, NAO.lastModified, now);
		pimo.addStatement(userPimoUri, NAO.modified, now);
		pimo.addStatement(userPimoUri, PIMO.creator, getUserUri());
		pimo.addStatement(userPimoUri, PIMO.isWriteable, "true", XSD._boolean);
		
		// user
		pimo.addStatement(userUri, PIMO.createdPimo, userPimoUri);
		pimo.addStatement(userUri, RDF.type, PIMO.Person);
		pimo.addStatement(userUri, RDFS.label, getUserName());
		pimo.addStatement(userUri, NAO.prefLabel, getUserName());
		pimo.addStatement(userUri, NAO.created, now);
		pimo.addStatement(userUri, NAO.lastModified, now);
		pimo.addStatement(userUri, PIMO.isDefinedBy, userPimoUri);
		
		// add pimo to the RDF store
		tripleStore.addModel(pimo);
		
		// broadcast events: new PIM + new person (user)
		broadcastManager.sendBroadcast(new Event(this.name, Event.ACTION_RESOURCE_ADD, new PersonalInformationModel(pimo, userPimoUri, false)));
		broadcastManager.sendBroadcast(new Event(this.name, Event.ACTION_RESOURCE_ADD, new Person(pimo, userUri, false)));
	}
	
	/**
	 * Creates a new URI inside the namespace returned by {@link #getUserNamespace()}.
	 * 
	 * @return a new URI that is unique, new and from the namespace
	 */
	public URI createUri() {
		return URIGenerator.createNewRandomUniqueURI(getUserNamespace().toString() + UUID_PATH);
	}

	/**
	 * Creates a new URI inside the namespace returned by {@link #getUserNamespace()}.
	 * The name passed as parameter as the trailing part of the URI.
	 * If a URI with that name already exists, an exception is thrown.
	 * 
	 * @param name a name to include in the URI
	 * @return a new URI that is unique, new and from the namespace, and contains
	 * the passed name.
	 */
	public URI createUriWithName(String name) throws OntologyInvalidException {
		return createUriWithName(NAMES_PATH, name);
	}

	/**
	 * Creates a new URI inside the namespace returned by {@link #getUserNamespace()},
	 * followed by the path and the name passed as parameters as the trailing part of
	 * the URI. If a URI with that name already exists, an exception is thrown.
	 * 
	 * @param path the path for the URI
	 * @param name a name to include in the URI
	 * @return a new URI that is unique, new and from the namespace + path, and contains
	 * the passed name.
	 */
	public URI createUriWithName(String path, String name) throws OntologyInvalidException {
		URI result = new URIImpl(getUserNamespace() + pretty(getUserNamespace().toString(), path) + toCleanName(name));
		if (tripleStore.containsStatements(Variable.ANY, result, Variable.ANY, Variable.ANY)
				|| tripleStore.containsStatements(Variable.ANY, Variable.ANY, result, Variable.ANY)
				|| tripleStore.containsStatements(Variable.ANY, Variable.ANY, Variable.ANY, result)) {
			throw new OntologyInvalidException("cannot create URI for name '"+name+"' under the user's namespace: already exists.");
		}
		return result;
	}
	
	/**
	 * Creates a new URI inside the passed namespace. 
	 * The name passed as parameter
	 * will be used inside this url, to increase readability.
	 * If a URI with that name already exists, a random part will
	 * be added to the name (the name is treated as "seed").
	 * 
	 * @param name a name to include in the URI
	 * @param namespace a string prefix to use in the URIs
	 * @return a new URI created within the passed namespace
	 */
	public URI createUriWithNameInNamespace(String name, URI namespace) {
		return getCleanUniqueURI(namespace, name, false);
	}

	/**
	 * Creates an URI that was not used before, use the passed ontology to create
	 * the URI in there. If the URI is already taken, add some numbers to it,
	 * but only do that when nullifexists is false, otherwise return null to
	 * indicate that the uri is taken.
	 * <p>
	 * We should only use characters allowed in XML names, as RDF serialization
	 * may brake otherwise. Correct <a
	 * href="http://www.w3.org/TR/REC-xml/#NT-Name">XML names</a> are:
	 * </p>
	 * 
	 * <pre>
	 * [5] Name ::= (Letter | '_' | ':') (NameChar)*
	 * [4] NameChar ::= Letter | Digit | '.' | '-' | '_' | ':' | CombiningChar | Extender
	 * </pre>
	 * 
	 * @param namespace namespace to use
	 * @param name a seed for generating a good name
	 * @param nullifexists if this is true, this method will return null if the
	 *        passed name exists in that ontology
	 * @return a new URI
	 */
	public URI getCleanUniqueURI(
		URI namespace,
		String name,
		boolean nullifexists) {
		String cleanName = toCleanName(name);
		
		URI uri = new URIImpl(namespace + cleanName);

		boolean ok = false;
		while (!ok) {
			ok = true;
			ok = ok && !createdURIs.contains(uri);
			ok = ok
				&& !tripleStore.containsStatements(
					Variable.ANY,
					uri,
					Variable.ANY,
					Variable.ANY);
			ok = ok
				&& !tripleStore.containsStatements(
					Variable.ANY,
					Variable.ANY,
					Variable.ANY,
					uri);
			if (!ok) {
				if (nullifexists) {
					return null;
				}
				uri = new URIImpl(namespace + cleanName + "-" + UUID.randomUUID());
			}
		}
		createdURIs.add(uri);
		return uri;
	}

	private static String pretty(String namespace, String path) {
		if (namespace.startsWith("urn")) {
			if (path.endsWith(":")) {
				return path;
			} else if (path.endsWith("/")) {
				return path.substring(0, path.length() - 1) + ":";
			} else {
				return path + ":";
			}
		} else {
			if (path.endsWith("/")) {
				return path;
			} else if (path.endsWith(":")) {
				return path.substring(0, path.length() - 1) + "/";
			} else {
				return path + "/";
			}
		}
	}

	@Override
	public <T extends org.ontoware.rdfreactor.schema.rdfs.Resource> PimoQuery<T> find(Class<T> returnType) {
		return new PimoQuery<T>(this, returnType);
	}

	/**
	 * Set the Personal Identifier of a Thing.
	 * The identifier must only exist for one Thing within the user-PIMO,
	 * this will be checked by this method and an exception is thrown if the
	 * identifier was already used for another thing.
	 * 
	 * @param subject the resource to identify
	 * @param identifier the identifier. Either a String or <code>null</code> to indicate that the
	 * personal identifier of this subject is to be deleted.
	 * @throws NameNotUniqueException if the identifier was used for another entity before
	 */
	public void setPersonalIdentifier(URI subject, String identifier)
			throws NotFoundException, NameNotUniqueException {
		// set or unset
		if (identifier == null) {
			tripleStore.removeStatements(Variable.ANY, subject, NAO.personalIdentifier, Variable.ANY);
			Resource resource = get(subject);
			broadcastManager.sendBroadcast(new Event(this.name, Event.ACTION_RESOURCE_MODIFY, resource));
		} else {
			// first check, if there is a Thing with the same personal identifier
			assertPersonalIdentifier(identifier);
			
			// remove any old personal identifier
			tripleStore.removeStatements(Variable.ANY, subject, NAO.personalIdentifier, Variable.ANY);
			// set the new one
			tripleStore.addStatement(this.userPimoUri, subject, NAO.personalIdentifier, new PlainLiteralImpl(identifier));
			
			Resource resource = get(subject);
			broadcastManager.sendBroadcast(new Event(this.name, Event.ACTION_RESOURCE_MODIFY, resource));
		}
	}
	
	/**
	 * Retrieves a resource which the nao:personalIdentifier given by the
	 * user is known.
	 * 
	 * @param identifier the human-friendly identifier of the resource 
	 * @return the resource which is identified by the nao:personalIdentifier
	 */
	public Resource getByPersonalIdentifier(String identifier) throws NotFoundException {
		try {
			assertPersonalIdentifier(identifier);
			ClosableIterator<Statement> it = tripleStore.findStatements(Variable.ANY, Variable.ANY, NAO.personalIdentifier, new PlainLiteralImpl(identifier));
			URI resourceUri = it.next().getSubject().asURI();
			it.close();
			return resourceStore.get(resourceUri);
		} catch (NameNotUniqueException e) {
			return null;
		}
	}

	private void assertPersonalIdentifier(String identifier) throws NameNotUniqueException {
		LinkedList<URI> existing = null;
		PlainLiteral nameLiteral = new PlainLiteralImpl(identifier);
		ClosableIterator<Statement> it = tripleStore.findStatements(Variable.ANY, Variable.ANY, NAO.personalIdentifier, nameLiteral);
		while (it.hasNext()) {
			Statement s = it.next();
			// check if it is in the user's pimo
			if (tripleStore.containsStatements(
					Variable.ANY, s.getSubject(), PIMO.isDefinedBy, this.userPimoUri)) {
				if (existing == null) {
					existing = new LinkedList<URI>();
				}
				existing.add(s.getSubject().asURI());
			} else {
				logger.warn("Found resource <"+s.getSubject()+ "> with personal identifier '"+identifier+"' which is not " +
						"defined by the user's PIMO. Maybe this is a programming bug");
			}
		}
		it.close();
		if (existing != null) {
			throw new NameNotUniqueException("Identifier '"+identifier+"' was already used for Thing with URI <"+
				existing.get(0)+">", existing.get(0).toString());
		}
	}

	@Override
	public ClosableIterable<Statement> queryConstruct(String query,
			String querylanguage) throws QueryLanguageNotSupportedException,
			MalformedQueryException, ModelRuntimeException {
		return tripleStore.queryConstruct(query, querylanguage);
	}

	@Override
	public QueryResultTable querySelect(String query, String querylanguage)
			throws QueryLanguageNotSupportedException, MalformedQueryException,
			ModelRuntimeException {
		return tripleStore.querySelect(query, querylanguage);
	}

	@Override
	public boolean sparqlAsk(String query) throws ModelRuntimeException,
			MalformedQueryException {
		return tripleStore.sparqlAsk(query);
	}

	@Override
	public ClosableIterable<Statement> sparqlConstruct(String query)
			throws ModelRuntimeException, MalformedQueryException {
		return tripleStore.sparqlConstruct(query);
	}

	@Override
	public ClosableIterable<Statement> sparqlDescribe(String query)
			throws ModelRuntimeException {
		return tripleStore.sparqlDescribe(query);
	}

	@Override
	public QueryResultTable sparqlSelect(String queryString)
			throws MalformedQueryException, ModelRuntimeException {
		return tripleStore.sparqlSelect(queryString);
	}
	
	@Override
	public Resource get(org.ontoware.rdf2go.model.node.Resource instanceIdentifier)
			throws NotFoundException {
		return resourceStore.get(instanceIdentifier);
	}

	@Override
	public Resource get(org.ontoware.rdf2go.model.node.Resource instanceIdentifier,
			URI... properties) throws NotFoundException {
		return resourceStore.get(instanceIdentifier, properties);
	}

	@Override
	public <T extends Resource> T get(org.ontoware.rdf2go.model.node.Resource instanceIdentifier,
			Class<T> returnType) throws NotFoundException {
		return resourceStore.get(instanceIdentifier, returnType);
	}
	
	@Override
	public <T extends Resource> T get(org.ontoware.rdf2go.model.node.Resource instanceIdentifier,
			Class<T> returnType, URI... properties) throws NotFoundException {
		return resourceStore.get(instanceIdentifier, returnType, properties);
	}

	@Override
	public <T extends Resource> T get(URI graphUri,
			org.ontoware.rdf2go.model.node.Resource instanceIdentifier,
			Class<T> returnType) throws NotFoundException {
		return resourceStore.get(graphUri, instanceIdentifier, returnType);
	}

	@Override
	public <T extends Resource> T get(URI graphUri, org.ontoware.rdf2go.model.node.Resource instanceIdentifier,
			Class<T> returnType, URI... properties) throws NotFoundException {
		return resourceStore.get(graphUri, instanceIdentifier, returnType, properties);
	}

	/**
	 * check if the passed URI is a class and throw an exception if not
	 * 
	 * @param clazz the class to check
	 * @throws OntologyInvalidException if the class is not a class
	 */
	protected void assertClass(URI clazz) throws OntologyInvalidException {
		if (!isClass(clazz)) {
			throw new OntologyInvalidException(clazz+ " is not an rdfs:Class");
		}
	}
	
	/**
	 * check if the passed URI is a property and throw an exception if not
	 * @param property the property to check
	 * @throws OntologyInvalidException if the property is not a property
	 */
	protected void assertProperty(URI property) throws OntologyInvalidException {
		if (!isProperty(property)) {
			throw new OntologyInvalidException(property+" is not an rdf:Property");
		}
	}
	
	/**
	 * check if the passed URI is an instance of the passed class
	 * and throw an exception if not
	 * @param resource the resource in question
	 * @param clazz the class to check
	 * @throws OntologyInvalidException if the class is not a class
	 */
	protected void assertResource(URI resource, URI clazz) throws OntologyInvalidException {
		if (!isResource(resource, clazz)) {
			throw new OntologyInvalidException("Resource "+resource+" is not an instance of "+clazz);
		}
	}

	/**
	 * Checks if the passed URI is defined as an rdfs:Class.
	 * 
	 * @param clazz the URI to check.
	 * @return true, if the URI has a type rdfs:Class
	 */
	public boolean isClass(URI clazz) {
		return tripleStore.containsStatements(Variable.ANY, clazz, RDF.type, RDFS.Class);
	}

	/**
	 * check if the passed URI is defined as an rdf:Property.
	 * 
	 * @param property the property to check
	 * @return true, if the URI has a type rdf:Property
	 */
	private boolean isProperty(URI property) {
		return tripleStore.containsStatements(Variable.ANY, property, RDF.type, RDF.Property);
	}

	/**
	 * check if the passed URI is defined as instance of the passed class.
	 * 
	 * @param resource the resource in question
	 * @param clazz the URI to check.
	 * @return true, if the URI has a type rdfs:Class
	 */
	public boolean isResource(URI resource, URI clazz) {
		return tripleStore.containsStatements(Variable.ANY, resource, RDF.type, clazz);
	}

	/**
	 * Creates a new class in the user's PIM, and it gets typed an rdfs:Class.
	 * 
	 * @param name the name of the class, used as label and part of the URI
	 * @param superClass the URI of the superclass of the created class
	 * @return the URI of the created class
	 * @throws OntologyInvalidException if the passed superClass is not defined
	 *		 as class in the user's PIM. Or if the class exists or corresponding
	 *		 URI is already taken.
	 */
	public URI createClass(String name, URI superClass)
		throws OntologyInvalidException {

		URI classUri = null;
		
		// a class name starts always with upper case
		String properName = name.substring(0, 1).toUpperCase() + name.substring(1);
		
		// checks if the class doesn't exist yet
		try {
			classUri = createUriWithName(CLASSES_PATH, properName);
		} catch (OntologyInvalidException e) {
			if (tripleStore.containsStatements(Variable.ANY, classUri, RDF.type, RDFS.Class)) {
				throw new OntologyInvalidException("There already exists a class with the same name '"
						+name+"' with URI "+classUri, e);
			} else {
				throw new OntologyInvalidException("The URI "+classUri+" is already being used, but it is not an rdfs:Class", e);
			}
		}

		// checks if the super class exists
		assertClass(superClass);
		
		// adds the new class to the user's PIM
		getUserPIM().addStatement(classUri, RDF.type, RDFS.Class);
		getUserPIM().addStatement(classUri, RDFS.subClassOf, superClass);
		getUserPIM().addStatement(classUri, RDFS.label, properName);

		Literal now = DateUtils.currentDateTimeAsLiteral();
		getUserPIM().addStatement(classUri, NAO.created, now);
		getUserPIM().addStatement(classUri, NAO.lastModified, now);
		
		return classUri;
	}

	/**
	 * Creates a new property in the user's PIM. It will have no
	 * inverse property assigned.
	 * 
	 * @param name the name of the new property
	 * @param superProperty URI of the superProperty, if there is one. can be
	 *		null.
	 * @return the URI of the new property.
	 * @throws OntologyInvalidException if the passed superProperty is not a
	 *		 property
	 */
	public URI createProperty(String name, URI superProperty)
			throws OntologyInvalidException {

		// a property name starts always with lower case
		String properName = name.substring(0, 1).toLowerCase() + name.substring(1);

		if (superProperty != null) {
			assertProperty(superProperty);
		}
		URI propertyUri = createUriWithName(PROPERTIES_PATH, properName);
		getUserPIM().addStatement(propertyUri, RDF.type, RDF.Property);
		if (superProperty != null) {
			getUserPIM().addStatement(propertyUri, RDFS.subPropertyOf, superProperty);
		}

		getUserPIM().addStatement(propertyUri, RDFS.label, properName);
		
		Literal now = DateUtils.currentDateTimeAsLiteral();
		getUserPIM().addStatement(propertyUri, NAO.created, now);
		getUserPIM().addStatement(propertyUri, NAO.lastModified, now);

		return propertyUri;
	}
	
	/**
	 * Create a new resource, an instance of a rdfs:Class New resources are
	 * always created in the workModel, which is a new context.
	 * 
	 * @param name the name of the resource, used as label and part of the URI
	 * @param ofClass the URI of the class of the created resource
	 * @return the URI of the created resource
	 * @throws OntologyInvalidException if the passed ofClass is not defined as
	 *		 class in the PIM.
	 */
	public URI createResource(String name, URI ofClass)
			throws OntologyInvalidException {
		assertClass(ofClass);
		return createResource(createUriWithName(name), name, ofClass);
	}

	/* it doesn't check if ofClass exists and it's really a class */
	private URI createResource(URI resourceUri, String name, URI ofClass) {
		getUserPIM().addStatement(resourceUri, RDF.type, ofClass);
		getUserPIM().addStatement(resourceUri, RDFS.label, name);
		getUserPIM().addStatement(resourceUri, NAO.prefLabel, name);
		getUserPIM().addStatement(resourceUri, PIMO.isDefinedBy, this.userPimoUri);

		Literal now = DateUtils.currentDateTimeAsLiteral();
		getUserPIM().addStatement(resourceUri, NAO.created, now);
		getUserPIM().addStatement(resourceUri, NAO.lastModified, now);

		return resourceUri;
	}

	@Override
	public Tag getOrCreateTag(String name) throws OntologyInvalidException {
		Tag tag = null;
		
		// checks if there is a tag with the same name, and it returns if so
		String query = StringUtils.strjoinNL(
				SPARQL_PREAMBLE,
				"SELECT DISTINCT ?tag WHERE { ?tag rdf:type nao:Tag . ?tag nao:prefLabel ?label . FILTER regex(?label, \""+name+"\") }");
		ClosableIterator<QueryRow> results = tripleStore.sparqlSelect(query).iterator();
		try {
			if (results.hasNext()) {
				tag = get(results.next().getValue("tag").asResource(), Tag.class);
			}
			// okay, there is no tag with this name, we create one
			else {
				URI tagUri = createUriWithName(TAGS_PATH, name);
				tripleStore.addStatement(this.userPimoUri, tagUri, NAO.prefLabel, new PlainLiteralImpl(name));
				tripleStore.addStatement(this.userPimoUri, tagUri, RDF.type, NAO.Tag);
				createResource(tagUri, name, PIMO.Topic); // also types the tag as a pimo:Topic 
				logger.info("Created new tag with label \""+ name+"\"");
				tag = get(tagUri, Tag.class);
			}
		} catch (NotFoundException e) {
			throw new OntologyInvalidException("Cannot find/create the tag '"+name+"'", e);
		}
		
		return tag;
	}
	
	@Override
	public void addTag(URI resource, URI tag) throws NameNotUniqueException, OntologyInvalidException, NotFoundException {
		if (resource == null || tag == null) {
			throw new IllegalArgumentException("Neither the resource or the tag may be null.");
		}
		
		// is the tag of type nao:Tag?
		if (!tripleStore.containsStatements(Variable.ANY, tag, RDF.type, NAO.Tag)) {
			createTag(tag);
			logger.info(tag+" was not a nao:Tag, but it has been converted.");
		}
		
		// now we can add the tag to the resource
		tripleStore.addStatement(this.userPimoUri, resource, NAO.hasTag, tag);
		tripleStore.addStatement(this.userPimoUri, tag, NAO.isTagFor, resource);

	}

	@Override
	public URI createTag(String name) throws NameNotUniqueException, OntologyInvalidException {
		// checks if there is a tag with the same name
		String query = StringUtils.strjoinNL(
				SPARQL_PREAMBLE,
				"ASK { ?tag rdf:type nao:Tag . ?tag nao:prefLabel ?label . FILTER regex(?label, \""+name+"\") }");
		if (tripleStore.sparqlAsk(query)) {
			throw new NameNotUniqueException("There is already a tag with name ("+ name+")");
		}

		// okay, there is no tag with this name, we create one
		URI tagUri = createUriWithName(TAGS_PATH, name);
		tripleStore.addStatement(this.userPimoUri, tagUri, NAO.prefLabel, new PlainLiteralImpl(name));
		tripleStore.addStatement(this.userPimoUri, tagUri, RDF.type, NAO.Tag);
		createResource(tagUri, name, PIMO.Topic); // also types the tag as a pimo:Topic 
		logger.info("Created new tag with label \""+ name+"\"");
		return tagUri;
	}

	/**
	 * Makes the passed thing to a nao:Tag if the thing was defined by the user else it throws an exception
	 * 
	 * @param thing
	 * @throws OntologyInvalidException
	 * @throws NameNotUniqueException when there is already a thing which has the same label
	 */
	public void createTag(URI thing) throws OntologyInvalidException, NameNotUniqueException {
		if(!tripleStore.containsStatements(Variable.ANY, thing, PIMO.isDefinedBy, this.userPimoUri)) {
			throw new OntologyInvalidException("The tag: "+thing+" is not a nao:Tag and it is not possible"
					+" to create tags from things which are not definied by the user!");
		}
		if(!tripleStore.containsStatements(Variable.ANY, thing, RDF.type, NAO.Tag)) {
			// get the label from the thing
			ClosableIterator<? extends Statement> it = null;
			String label = "";
			try {
				it = tripleStore.findStatements(Variable.ANY, thing, NAO.prefLabel, Variable.ANY);
				while (it.hasNext()) {
					Statement s = it.next();
					Node node = s.getObject();
					label = node.asLiteral().toString();
				}
			} finally {
				it.close();
			}
			// get all tags
			Collection<URI> tags = null;
			try {
				it = tripleStore.findStatements(Variable.ANY, Variable.ANY, RDF.type, NAO.Tag);
				while (it.hasNext()) {
					Statement s = it.next();
					Node tag = s.getSubject();
					if (tag instanceof URI) {
						if (tags == null) {
							tags = new LinkedList<URI>();
						}
						tags.add(tag.asURI());
					}
				}
			} finally {
				it.close();
			}
			// is there a tag which has already this label
			if (tags != null) {
				for (URI tag : tags) {
					if (tripleStore.containsStatements(Variable.ANY, tag, NAO.prefLabel, new PlainLiteralImpl(label))) {
						// there is a thing which already has this label
						throw new NameNotUniqueException("The passed thing can't be a nao:Tag because "+tag+" is a tag with the label: "+label,
							tag.toString());
					}
				}
			}
			tripleStore.addStatement(this.userPimoUri, thing, RDF.type, NAO.Tag);
		} else {
			logger.info(thing+"is already a nao:Tag");
		}
	}

	@Override
	public void removeTag(URI resource, URI tag) {
//		if (resource == null || tag == null)
//			throw new NullPointerException("Either the resource or the tag is null");
//		if (resource == null || tag == null)
//			throw new NullPointerException("Either the resource or the tag is null");
//		
//		URI removeTagFrom = null;
//		// first we need to know, if this resource is in the pimo, or a groundingoccurrence
//		if (!getWorkModelSet().containsStatements(Variable.ANY, resource, PIMO.isDefinedBy, this.userPimoUri)) {
//			// we have to check if there is a thing in the pimo, which have this as grounding occurrence
//			removeTagFrom = findThingForOccurrence(resource);
//		} else {
//			removeTagFrom = resource;	
//		}
//		getWorkModelSet().removeStatements(Variable.ANY, removeTagFrom, NAO.hasTag, tag);
	}
	
	/**
	 * Checks if a thing exists in the PIM.
	 * 
	 * @param thing
	 * @return true if the thing exists
	 */
	public boolean exists(Thing thing) {
		return exists(thing.asResource());
	}
	
	/**
	 * Checks if a thing exists in the PIM.
	 * 
	 * @param thing
	 * @return true if the thing exists
	 */
	public boolean exists(org.ontoware.rdf2go.model.node.Resource thingIdentifier) {
		if (tripleStore.containsStatements(this.userPimoUri, thingIdentifier, RDF.type, PIMO.Thing)) {
			boolean exists = tripleStore.containsStatements(Variable.ANY, thingIdentifier, PIMO.isDefinedBy, this.userPimoUri);
			if (exists == false) {
				logger.error(thingIdentifier+" is a Thing, but it is missing the relation pimo:isDefinedBy to the PIM graph.");
			}
			return exists;
		} else {
			return tripleStore.containsStatements(this.userPimoUri, thingIdentifier, RDF.type, Variable.ANY);
		}
	}

	private void assertExist(org.ontoware.rdf2go.model.node.Resource thingIdentifier, String message) throws NotFoundException {
		if (!exists(thingIdentifier)) {
			throw new NotFoundException(thingIdentifier+" does not exist in the PIM: "+ message);
		}
	}
	
	private void assertNotExist(org.ontoware.rdf2go.model.node.Resource thingIdentifier, String message) throws ResourceExistsException {
		if (exists(thingIdentifier)) {
			throw new ResourceExistsException(thingIdentifier+" already exists in the PIM: "+ message);
		}
	}
	
	public void createThing(Thing thing) throws ResourceExistsException {
		assertNotExist(thing.asURI(), "cannot create it again.");
		thing.setIsDefinedBy(this.userPimoUri);
		thing.setCreated(DateUtils.now());
		thing.setLastModified(DateUtils.now());
		resourceStore.createOrUpdate(this.userPimoUri, thing);
	}
	
	public void updateThing(Thing thing) throws NotFoundException {
		assertExist(thing.asURI(), "cannot be updated.");
		thing.setIsDefinedBy(this.userPimoUri);
		thing.setLastModified(DateUtils.now());
		resourceStore.update(this.userPimoUri, thing);
	}
	
	public void updateThing(Thing thing, boolean isPartial) throws NotFoundException {
		assertExist(thing.asURI(), "cannot be updated.");

		thing.setIsDefinedBy(this.userPimoUri);
		thing.setLastModified(DateUtils.now());

		resourceStore.update(this.userPimoUri, thing, isPartial);
	}
	
	public void removeThing(org.ontoware.rdf2go.model.node.Resource thingId) throws NotFoundException {
		assertExist(thingId, "cannot be removed.");
		Thing thing = get(thingId, Thing.class);
		tripleStore.removeFromGraph(this.userPimoUri, thingId);
		broadcastManager.sendBroadcast(new Event(this.name, Event.ACTION_RESOURCE_DELETE, thing));
	}
	
	public void remove(org.ontoware.rdf2go.model.node.Resource resourceId) throws NotFoundException {
		assertExist(resourceId, "cannot be removed.");
		Resource resource = get(resourceId);
		tripleStore.removeFromGraph(this.userPimoUri, resourceId);
		broadcastManager.sendBroadcast(new Event(this.name, Event.ACTION_RESOURCE_DELETE, resource));
	}
	
	public boolean isTypedAs(org.ontoware.rdf2go.model.node.Resource instanceId,
			URI type) throws NotFoundException {
		assertExist(instanceId, "cannot be of any type.");
		return tripleStore.isTypedAs(instanceId, type);
	}

	/**
	 * Retrieves the property URIs of properties that are sub-properties of pimo:identifier.
	 * 
	 * @return URIs of properties that are sub-properties of pimo:identifier
	 */
	public Set<URI> getIdentifyingProperties() {
		if (identifyingProperties == null) {
			identifyingProperties = new HashSet<URI>();
			ClosableIterator<? extends Statement> stmts = null;
			try {
				// assuming subproperties are inferred transitively before
				stmts = tripleStore.findStatements(Variable.ANY, Variable.ANY, RDFS.subPropertyOf, NAO.identifier);
				while (stmts.hasNext()) {
					org.ontoware.rdf2go.model.node.Resource resource = stmts.next().getSubject();
					if (resource instanceof URI) {
						identifyingProperties.add(resource.asURI());
					} else {
						logger.error("Property "+ resource+" defined as subproperty of nao:identifier, but is not a URI.");
					}
				}
			} finally {
				if (stmts != null) {
					stmts.close();
				}
			}
		}
		return identifyingProperties;
	}
	
	/**
	 * Returns the thing which represent this occurrence, 
	 * using all possible properties that can connect a thing to a resource.
	 * See {@link #findThingForOccurrence(URI, boolean, boolean, boolean, boolean)}
	 * for all relations that are checked.
	 * 
	 * @param informationElement
	 * @return thing or null
	 */
	public Thing findThingForOccurrence(URI informationElement) {
		return findThingForOccurrence(informationElement, true, true, true, true);
	}
	
	/**
	 * Returns the thing which represent this occurrence.
	 * It will also check if the informationElement is a thing,
	 * if this is true, returns the informationElement.
	 * 
	 * @param informationElement the element to check
	 * @param occurrence check occurrence relation
	 * @param groundingOccurrence check grounding occurrence relation
	 * @param referencingOccurrence check referencing occurrence
	 * @param hasOtherRepresentation check hasOtherRepresentation
	 * @return the thing for this occurrence
	 */
	public Thing findThingForOccurrence(URI informationElement, boolean occurrence,
			boolean groundingOccurrence, boolean referencingOccurrence, boolean hasOtherRepresentation) {
		Thing thing = null;
		if (tripleStore.isTypedAs(informationElement, PIMO.Thing)) {
			thing = pimoFactory.createThing(informationElement.asURI());
			ModelUtils.fetch(tripleStore, thing.getModel(), thing.asResource());
		} else {
			Query<Thing> query = find(Thing.class).where(PIMO.occurrence).is(informationElement);
			
			if (groundingOccurrence)	query.where(PIMO.groundingOccurrence).is(informationElement);
			if (referencingOccurrence)	query.where(PIMO.referencingOccurrence).is(informationElement);
			if (hasOtherRepresentation)	query.where(PIMO.hasOtherRepresentation).is(informationElement);
			
			thing = query.first();
		}
		return thing;
	}
	
	/**
	 * Creates an instance of pimo:Thing in the user's PIM for a given resource
	 * occurrence. The created pimo:Thing is the canonical representation of the occurrence.
	 * 
	 * @param resource the occurrence which the Thing will be created for
	 * @return the created Thing for the occurrence, or null if the creation is not allowed
	 */
	public Thing getOrCreateThingForOccurrence(org.ontoware.rdf2go.model.node.Resource resource) {
		return getOrCreateThingForOccurrence(resource, false, false);
	}

	/**
	 * Same as {@link #getOrCreateThingForOccurrence(Resource)}, except that
	 * the new instance of pimo:Thing can be linked to the occurrence using
	 * the properties pimo:groundingOccurrence and pimo:referencingOccurrence,
	 * although they are only applicable if the occurrence is an instance of
	 * nie:InformationElement.
	 * 
	 * @param resource the occurrence which the Thing will be created for
	 * @param groundingOccurrence sets the occurrence as a grounding occurrence
	 * @param referencingOccurrence sets the occurrence as a referencing occurrence
	 * @return the created Thing for the occurrence, or null if the creation is not allowed
	 */
	public Thing getOrCreateThingForOccurrence(org.ontoware.rdf2go.model.node.Resource resource,
			boolean groundingOccurrence, boolean referencingOccurrence) {

		if (tripleStore.containsStatements(
				Variable.ANY, resource.asResource(), PIMO.groundingForDeletedThing, Variable.ANY)) {
			logger.info("Thing instance for occurrence "+ resource.asResource()+" not created."
				+" It was grounding occurrence for a Thing which was deleted, and no automatic creation of another Thing is allowed.");
			return null;
		}

		// if a resource is of one of these types, then no pimo:Thing is created for it
		if (tripleStore.isTypedAs(resource, NCO.PersonName)
				|| tripleStore.isTypedAs(resource, NCO.Name)
				|| tripleStore.isTypedAs(resource, NCO.EmailAddress)
				|| tripleStore.isTypedAs(resource, NCO.PhoneNumber)
				|| tripleStore.isTypedAs(resource, NCO.BirthDate)
				|| tripleStore.isTypedAs(resource, NCO.PostalAddress)
				|| tripleStore.isTypedAs(resource, NCO.Affiliation)
				|| tripleStore.isTypedAs(resource, NCO.FaxNumber)) {
			return null;
		}
		
		Thing thing = null;
		
		// search for a direct match
		thing = findThingForOccurrence(resource.asURI(), true, groundingOccurrence, referencingOccurrence, false);
		if (thing != null) {
			return thing;
		}

		thing = pimoFactory.createThing();

		// add the specific type, if found
		URI pimoType = null;
		
		// get internal RDFS class value from RDFReactor resources
		if (resource instanceof Resource)
			pimoType = getPIMOTypeForNIEType(((Resource) resource).getRDFSClassURI(), null);
		
		if (pimoType == null)
			pimoType = getPIMOTypeForOccurrenceNIEType(resource);

        // by default, it will be a pimo:Thing, so no need to specify it explicitly
		if (pimoType != null && !pimoType.equals(PIMO.Thing)) {
			thing.getModel().addStatement(thing.asResource(), RDF.type, pimoType);
		}
		
		// if the thing is a pimo:Person, it gets automatically related to the pimo user via foaf:knows
		if (pimoType != null && pimoType.equals(PIMO.Person)) {
			thing.getModel().addStatement(getUserUri(), FOAF.knows, thing.asResource());
		}
		
		// link thing to occurrence
		thing.setOccurrence(resource);

		String prefLabel = ResourceUtils.guessPreferredLabel(tripleStore, resource);
		org.ontoware.rdf2go.model.node.Resource prefSymbol = ResourceUtils.guessPreferredSymbol(tripleStore, resource);
		if (prefLabel != null) {
			thing.setPrefLabel(prefLabel);
		} else {
			logger.debug(thing.asResource()+" ["+thing.getRDFSClassURI()+"] has been created for grounding ocurrence "+
				resource+" but no prefLabel could be assigned.");
		}
		if (prefSymbol != null) {
			thing.setPrefSymbol(prefSymbol);
		} else {
			logger.debug(thing.asResource()+" ["+thing.getRDFSClassURI()+"] has been created for grounding ocurrence "+
				resource+" but no prefSymbol could be assigned.");
		}
		
		if (tripleStore.isTypedAs(resource, NIE.InformationElement)) {
			if (groundingOccurrence) {
				thing.setGroundingOccurrence(resource);
			}
			if (referencingOccurrence) {
				thing.setReferencingOccurrence(resource);
			}
		} else {
			logger.debug("groundingOccurrence and referencingOccurrence are not applicable: "
					+resource+" is not of type nie:InformationElement");
		}

		try {
			createThing(thing);
		} catch (ResourceExistsException e) {
			logger.error("this shouldn't happen, if so it's probably a programming bug, or the URI random generator is broken!");
			return null;
		}

		return thing;
	}

	// COPIED FROM SemanticApi.java
	public Person getOrCreatePersonForGroundingOccurrence(org.ontoware.rdf2go.model.node.Resource occurrence) {
		PersonContact contact = null;
		try {
			contact = get(occurrence, PersonContact.class);
		} catch (NotFoundException e) {
			logger.error("Cannot happen, sounds like a bug!", e);
			return null;
		}

		// if there is a pimo:Thing - groundingOcurrence - occurrence, it's returned
		org.ontoware.rdf2go.model.node.Resource thingIdentifier = ModelUtils.findSubject(tripleStore, PIMO.groundingOccurrence, occurrence);
		if (thingIdentifier != null) {
			try {
				if (!tripleStore.isTypedAs(thingIdentifier, PIMO.Person)) {
					logger.debug(occurrence+" is grounding occurrence of "+thingIdentifier+", but it is not a pimo:Person. Typed as a pimo:Person as well.");
					tripleStore.addStatement(this.userPimoUri, thingIdentifier, RDF.type, PIMO.Person);
				}
				return get(thingIdentifier, Person.class);
			} catch (NotFoundException e) {
				logger.error("Cannot happen, sounds like a bug!", e);
			}
		}

		// create a person for the occurrence
		Person person = null;
		Thing thing = getOrCreateThingForOccurrence(occurrence, true, false);
		if (thing != null
				&& tripleStore.isTypedAs(thing.asResource(), PIMO.Person)) {
			person = (Person) thing.castTo(Person.class);
			
			// if we add a PersonContact, a pimo:Person will be created, which is someone the pimo user knows
			tripleStore.addStatement(this.userPimoUri, getUserUri(), FOAF.knows, person.asResource());
			
			// add default trust level
			tripleStore.addStatement(this.userPimoUri, person.asResource(), NAO.trustLevel, new DatatypeLiteralImpl("0", XSD._double));
		}
		
		return person;
	}
	
	public Collection<URI> findThingMatchingResource(URI informationElement, boolean checkOccurrence) {
		Collection<URI> result = null;
		
		// check if we have a grounding or occurrence
		if (checkOccurrence) {
			Thing res = findThingForOccurrence(informationElement);
			if (res != null) {
				result = new HashSet<URI>(2);
				result.add(res.asURI());
			}
		}
		
		if (result == null || result.isEmpty()) {
			ClosableIterator<? extends Statement> it = tripleStore.findStatements(Variable.ANY, informationElement, Variable.ANY, Variable.ANY);
			while (it.hasNext()) {
				Statement stmt = it.next();
				if (getIdentifyingProperties().contains(stmt.getPredicate())) { // an identifying property!
					String query = StringUtils.strjoinNL(
							SPARQL_PREAMBLE,
							"SELECT ?thing WHERE { ?thing pimo:isDefinedBy "+userPimoUri.toSPARQL()+". ",
							"?thing "+ stmt.getPredicate().toSPARQL()+" "+ stmt.getObject().toSPARQL()+". }");
					try {
						QueryResultTable resultTable = tripleStore.sparqlSelect(query);
						for (ClosableIterator<QueryRow> ri = resultTable.iterator(); ri.hasNext(); ) {
							QueryRow row = ri.next();
							if (result == null) {
								result = new HashSet<URI>(2);
							}
							Node v = row.getValue("thing");
							if (v instanceof URI) {
								result.add(v.asURI());
							} else {
								logger.warn("Thing is not identified with a URI: "+v);
							}
						}
					} finally {
					}
				}
			}
			it.close();
		}

		if (result == null) {
			return Collections.emptySet();
		}
		return result;
	}
	
	public Collection<URI> findThingMatchingResource(Resource informationElement, ModelSet modelset, boolean checkOccurrence, URI... limitToContexts) {
		return null;
	}
	
	public Collection<URI> getPossiblePIMOTypeForNIEType(URI nieType) {
		ClosableIterator<? extends Statement> it = null;
		Collection<URI> result = null;
		try {
			it = tripleStore.findStatements(
					Variable.ANY, Variable.ANY, PIMO.hasOtherConceptualization, nieType);
			while (it.hasNext()) {
				if (result == null) {
					result = new HashSet<URI>();
				}
				result.add(it.next().getSubject().asURI());
			}
		} finally {
			it.close();
		}
		if (result == null) {
			return Collections.emptySet();
		}
		return result;
	}

	public URI getPIMOTypeForNIEType(URI nieType) {
		return getPIMOTypeForNIEType(nieType, PIMO.Thing);
	}
	
	public URI getPIMOTypeForNIEType(URI nieType, URI defaultType) {
		Collection<URI> types = getPossiblePIMOTypeForNIEType(nieType);
		
		// if no possible PIMO type exist, we return pimo:Thing
		if (types.isEmpty())				return defaultType;
		
		// one type, great
		if (types.size() == 1)				return types.iterator().next();
		
		// if there are more then one possible type we need to choose one
		if (types.contains(PIMO.Topic))		return PIMO.Topic;
		if (types.contains(PIMO.Document))	return PIMO.Document;
		
		return types.iterator().next();
	}
	
	public URI getPIMOTypeForOccurrenceNIEType(org.ontoware.rdf2go.model.node.Resource resource) {
		URI pimoType = null;
		URI possibleClass = null;

		Set<URI> occTypes = new HashSet<URI>();
		ClosableIterator<? extends Statement> it = tripleStore.findStatements(Variable.ANY, resource, RDF.type, Variable.ANY);
		while (it.hasNext()) {
			occTypes.add(it.next().getObject().asURI());
		}
		it.close();
		
		// there is none type
		if (occTypes.isEmpty()) {
			pimoType = PIMO.Thing;
		}
		
		if (pimoType == null) {
			for (URI occType : occTypes) {
				if (occType.equals(NCAL.Event)) {
					pimoType = PIMO.SocialEvent;
					break;
				} else if (occType.equals(NCO.PersonContact)) {
					pimoType = PIMO.Person;
					break;
				}
			}
		}
		
		if (pimoType == null) {
			for (URI occType : occTypes) {
				// we can use the mapping of classes
				Collection<URI> types = getPossiblePIMOTypeForNIEType(occType);
				if (possibleClass== null && !types.isEmpty())
					possibleClass = types.iterator().next();
				if (types.size() == 1) {
					// the best case is, if there is only one possible
					// PIMO type for this thing.
					pimoType = types.iterator().next();
					break;
				}
				types.clear();
			}
		}
		
		if (pimoType == null) {
			for (URI occType : occTypes) {
				// if the type of the information element is a subclass of a type of the pimo,
				// we can use this type from the information element
				ClosableIterator<? extends Statement> isupertypes = tripleStore.findStatements(
						Variable.ANY, occType, RDFS.subClassOf, Variable.ANY);
				while (isupertypes.hasNext()) {
					URI supertype = (URI) isupertypes.next().getObject();
					if (supertype.toString().startsWith(PIMO.NS_PIMO.toString())) {
						pimoType = occType;
						break;
					}
				}
			}
		}
        
		if (pimoType == null) {
        	// assign the first class which we found
        	pimoType = possibleClass;
        }

        return pimoType;
	}

	public Collection<URI> getPIMOTypesForNIEResource(URI resource) {
		String query = StringUtils.strjoinNL(
				SPARQL_PREAMBLE,
				"SELECT DISTINCT ?node WHERE { "+resource.toSPARQL()+" rdf:type ?nieType. ",
				"?nieType rdfs:subClassOf ?nieTypes. ",
				"?node "+PIMO.hasOtherConceptualization.toSPARQL()+" ?nieTypes. }");
		return queryToCollection(query, "node");
	}
	
	/**
	 * Run this query, iterate through the results, and
	 * creates a new collection (or return the empty collection)
	 * based on the result variable <code>paramname</code>.
	 * 
	 * @param queryString the query, it must contain a parameter 
	 * @param paramname the parameter name
	 * @return a collection
	 * @throws RuntimeException when the parameter is not always a URI
	 */
	private Collection<URI> queryToCollection(String queryString, String paramname) {
		Collection<URI> result = null;
		QueryResultTable qr = tripleStore.sparqlSelect(queryString);
		for (ClosableIterator<QueryRow> i = qr.iterator(); i.hasNext(); ) {
			if (result == null) {
				result = new LinkedList<URI>();
			}
			result.add(i.next().getValue(paramname).asURI());
		}
		if (result == null) {
			return Collections.emptyList();
		}
		return result;
	}

	/**
	 * Create a Thing from a grounding occurrence, read the label,
	 * type from the working repository.
	 * You must not call this if this occurrence was already linked to a 
	 * thing, this method does not check if a thing or occurrence 
	 * exist.
	 * All identifiers from the occurrence will be added to the new thing.
	 * @param occurrence the occurrence to look at
	 * @return the created thing
	 * @throws OntologyInvalidException if no class can be detected for 
	 * the information element.
	 */
	public URI createResourceFromGroundingOccurrence(URI occurrence)
			throws OntologyInvalidException {
		return createResourceFromGroundingOccurrence(occurrence, null, null);
	}
	

	/**
	 * Creates a Thing from a given occurrence in the working model.
	 * The URI of the occurrence will be added as groundingOccurrence.
	 * All identifiers from the occurrence, which can be found in occurrenceModelSet will be added
	 * as statements in the model.
	 * 
	 * @param occurrence the occurrence from which to create a resoucre
	 * @param occurrenceModelSet the FindableModelSet where to find statements over occurrence
	 * @param name the name of the resource, used as label and part of the uri
	 * @param ofClass the URI of the class of the created resource
	 * @return the URI of the created resource
	 * @throws OntologyInvalidException if the passed URI is not defined as
	 *		 class in the pimo.
	 */
	public URI createResourceFromGroundingOccurrence(
			URI occurrence, FindableModelSet occurrenceModelSet, String name, URI ofClass)
			throws OntologyInvalidException {
		
		URI newThing = createResource(name, ofClass);

		Model model = getUserPIM();
		
		// copy identifiers
		copyIdentifiers(newThing, occurrence, occurrenceModelSet, model);

		// copy the types from the occurrence to the pimo:Thing.
		// this is useful to get possible properties for this thing.
		ClosableIterator<? extends Statement> s = occurrenceModelSet.findStatements(
				Variable.ANY, occurrence, RDF.type, Variable.ANY);
		while (s.hasNext()) {
			Statement st = s.next();
			model.addStatement(newThing, st.getPredicate(), st.getObject());
		}
		
		// add some metadata
		model.addStatement(newThing, RDF.type, ofClass);
		model.addStatement(newThing, RDFS.label, name);
		model.addStatement(newThing, NAO.prefLabel, name);
		model.addStatement(newThing, PIMO.isDefinedBy, this.userPimoUri);
		
		Literal now = DateUtils.currentDateTimeAsLiteral();
		model.addStatement(newThing, NAO.created, now);
		model.addStatement(newThing, NAO.lastModified, now);
		
		// and the new resource is the grounding occurrence of the thing if it isn't PIMO.Topic
		if (ofClass.toString().equals(PIMO.Topic.toString())) {
			model.addStatement(newThing, PIMO.referencingOccurrence, occurrence);
		} else {
			model.addStatement(newThing, PIMO.groundingOccurrence, occurrence);
		}
		
		return newThing;
	}

	/**
	 * Create a Thing from a grounding occurrence, read the label,
	 * type from the working repository.
	 * You must not call this if this occurrence was already linked to a 
	 * thing, this method does not check if a thing or occurrence 
	 * exist.
	 * @param occurrence the occurrence to look at
	 * @param label the new label of the thing. If it is empty, the function search for a proper label 
	 * @return the created thing
	 * @throws OntologyInvalidException if no class can be detected for 
	 * the information element.
	 */
	public URI createResourceFromGroundingOccurrence(URI occurrence, String label)
			throws OntologyInvalidException {
		return createResourceFromGroundingOccurrence(occurrence, label, null);
	}

	/**
	 * Creates a Thing from a given occurrence. 
	 * The URI of the occurrence will be added as groundingOccurrence.
	 * All identifiers from the occurrence, which can be found in the pimo will be added
	 * as statements in the model.
	 * 
	 * @param occurrence the occurrence from which to create a resoucre
	 * @param label the name of the resource, used as label and part of the uri
	 * @param ofClass the URI of the class of the created resource
	 * @return the URI of the created resource
	 * @throws OntologyInvalidException if the passed URI is not defined as
	 *		 class in the pimo.
	 */
	public URI createResourceFromGroundingOccurrence(URI occurrence, String label, URI ofClass)
			throws OntologyInvalidException {
		// detect label and class
		URI clazz = ofClass;
		Set<URI> occTypes = new HashSet<URI>();
		
		if (clazz == null) {
			ClosableIterator<? extends Statement> it = tripleStore.findStatements(
					Variable.ANY, occurrence, RDF.type, Variable.ANY);
			while (it.hasNext()) {
				occTypes.add(it.next().getObject().asURI());
			}
			it.close();
			// no type was found
			if (occTypes.isEmpty()) {
				clazz = PIMO.Thing;
			}
		}
		
		URI candidateClass = null;
		if (clazz == null) {
			for (URI occType : occTypes) {
				// can use the mapping of classes
				Collection<URI> types = getPossiblePIMOTypeForNIEType(occType);
				if (candidateClass== null && !types.isEmpty())
					candidateClass = types.iterator().next();
				if (types.size() == 1) {
					// the best case is, if there is only one possible
					// PIMO type for this thing.
					clazz = types.iterator().next();
					break;
				}
				types.clear();
			}
		}
		if (clazz == null) {
			for (URI occType : occTypes) {
				// first check whether there is a type which has the same name
				// if the labels are the same, the possibility is high, that 
				// these are equal types.
				String occTypeLabel = ResourceUtils.guessPreferredLabel(tripleStore, occType);
				List<URI> candidates = new LinkedList<URI>(); 
				ClosableIterator<? extends Statement> statements = null;
				try {
					Literal rdfsLabel = new PlainLiteralImpl(occTypeLabel);
					for (statements = tripleStore.findStatements(PIMO.NS_PIMO, Variable.ANY, RDFS.label, rdfsLabel); statements.hasNext(); ) {
						candidates.add(statements.next().getSubject().asURI());
					}
				} finally {
					statements.close();
				}
				if (candidateClass == null && !candidates.isEmpty())
					candidateClass = candidates.iterator().next();
		   		// check whether the result contains more than one type.
		   		// if this is the case, we can't say anything about which
		   		// type is the better one.
		   		if (candidates.size() == 1) {
		   			clazz = candidates.iterator().next();
			   		break;
				}
			}
		}
		if (clazz == null) {
			for (URI occType : occTypes) {
				// if the type of the information element is a subclass of a type of the pimo,
				// we can use this type from the information element
				ClosableIterator<? extends Statement> isupertypes = tripleStore.findStatements(
						Variable.ANY, occType, RDFS.subClassOf, Variable.ANY);
				while (isupertypes.hasNext()) {
					URI supertype = (URI)isupertypes.next().getObject();
					if (supertype.toString().startsWith(PIMO.NS_PIMO.toString())) {
						clazz = occType;
						break;
					}
				}
				isupertypes.close();
			}
		}

		if (clazz == null)	clazz = candidateClass; // assign the first class which we found
		if (clazz == null)	clazz = PIMO.Topic;
		
		/**
		 * find a proper label for the new thing based on the resource
		 */
		if (label == null || label.equals("")) {
			label = ResourceUtils.guessPreferredLabel(tripleStore, occurrence);
		}
		
		// create thing
		return createResourceFromGroundingOccurrence(occurrence, tripleStore, label, clazz);
	}

	/**
	 * Makes this name clean of all characters that should not be in a URI.
	 * 
	 * @param name the name to clean
	 * @return a name without funny (not allowed) characters. 
	 */
	public static String toCleanName(String name) {
		String cleanName = name;

		// replaces all spaces for +
		cleanName = cleanName.replace(" ", "+");
		
		// replace all non-alphanum characters that are not allowed in XML names
		// with nothing
		// \w stands for alphas and nums
		cleanName = cleanName.replaceAll("[^\\w\\.\\-_:\\+]", "");
		
		if (cleanName.length() == 0) {
			cleanName = "x";
		}

		// check if cleanname starts with a character (XML spec),
		// if not, add x at the beginning.
		if (!cleanName.substring(0, 1).matches("\\A\\w")) {
			cleanName = "x" + cleanName;
		}
	
		return cleanName;
	}
	
	/**
	 * Copies the identifiers of the given source occurrence to the passed
	 * target PIMO thing.
	 * 
	 * @param target the thing to copy to.
	 * @param source the source to copy from.
	 * @return the number of identifiers that have been copied
	 */
	private int copyIdentifiers(URI target, URI source) {
		return copyIdentifiers(target, source, tripleStore, getUserPIM());
	}

	/**
	 * Copies the identifiers of the given source occurrence to the passed
	 * target PIMO thing. The source is queried using the passed occurrenceModelSet.
	 * Passing the sourceModelSet is supported to allow temporary models
	 * as basis for calculations.
	 * 
	 * @param target the thing to copy to
	 * @param source the source to copy from
	 * @param sourceModelSet the source data to read
	 * @param sinkModel where the new data is added to
	 * @return the number of identifiers that have been copied
	 */
	private int copyIdentifiers(URI target, URI source, FindableModelSet sourceModelSet, Model sinkModel) {
		int result = 0;
		Set<URI> identies = getIdentifyingProperties();
		ClosableIterator<? extends Statement> it = 
				sourceModelSet.findStatements(Variable.ANY, source, Variable.ANY, Variable.ANY);
		while (it.hasNext()) {
			Statement st = it.next();
			if(identies.contains(st.getPredicate())) {
				sinkModel.addStatement(target, st.getPredicate(), st.getObject());
				result++;
			}
		}
		it.close();
		return result;
	}
	
	/**
	 * Creates or saves a Resource in the persistent store.
	 * 
	 * @param resource the resource to be created or saved
	 * @throws ResourceExistsException if the a resource with the same URI
	 *         already exists
	 */
	public void create(Resource resource) throws ResourceExistsException {
		// if creating PIMO things, the pimo service is used to handle the
		// extra logic required by the PIM
		if (resource instanceof Thing) {
			createThing((Thing) resource);
		} else {
			assertNotExist(resource, "cannot be created again.");
			createWithoutAsserting(resource);
			getOrCreateThingForOccurrence(resource, true, false);
		}
	}
	
	/* Saves the resource in the RDF store without checking if the resource
	   already exists or not */
	private void createWithoutAsserting(Resource resource) {
		resourceStore.createOrUpdate(this.userPimoUri, resource);
	}

	/**
	 * Same as {@link update(Resource, boolean)}, except the update
	 * is complete by default.
	 * 
	 * @param resource
	 * @throws NotFoundException
	 */
	public void update(Resource resource) throws NotFoundException {
		update(resource, false);
	}
	
	/**
	 * Updates/modifies a Resource in the persistent store. If only
	 * part of the resource needs to be updated, <em>isPartial</em> flag
	 * must be set to true. If <em>isPartial</em> is set to false,
	 * all known data about the resource will be deleted, and the data
	 * will be persisted.
	 * 
	 * @param resource the resource to be updated
	 * @param isPartial indicates if the update is full or partial
	 * @throws NotFoundException if the resource does not exist
	 */
	public void update(Resource resource, boolean isPartial) throws NotFoundException {
		if (resource instanceof Thing) {
			updateThing((Thing) resource, isPartial);
		} else {
			assertExist(resource, "cannot be updated.");
			updateWithoutAsserting(resource, isPartial);
			getOrCreateThingForOccurrence(resource, true, false);
		}
	}

	/* Updates a resource, assuming is already in the RDF store */
	private void updateWithoutAsserting(Resource resource, boolean isPartial) {
		try {
			resourceStore.update(this.userPimoUri, resource, isPartial);
		} catch (NotFoundException e) {
			logger.warn(resource+" couldn't be updated, it doesn't exist.", e);
		}
	}
	
	/**
	 * Same as {@link #createOrUpdate(Resource, boolean), except if
	 * an update is need to be performed, it will be a full update
	 * by default.
	 *  
	 * @param resource
	 */
	public void createOrUpdate(Resource resource) {
		createOrUpdate(resource, false);
	}
	
	/**
	 * 
	 * @param resource
	 * @param isPartial
	 */
	public void createOrUpdate(Resource resource, boolean isPartial) {
		if (resource instanceof Thing) {
			try {
				if (exists(resource.asURI())) {
					updateThing((Thing) resource, isPartial);
				} else {
					createThing((Thing) resource);
				}
			} catch (NotFoundException e) {
				logger.error("cannot save/update the resource: " + e, e);
			} catch (ResourceExistsException e) {
				logger.error("cannot save/update the resource: " + e, e);
			}
		} else {
			if (exists(resource)) {
				updateWithoutAsserting(resource, isPartial);
			} else {
				createWithoutAsserting(resource);
			}
		}
	}
	
	public Person merge(URI master, URI... targets) throws NotFoundException {
		logger.debug("Merging "+master+" with "+Arrays.toString(targets));
		if (master == null || targets.length < 1) {
			throw new IllegalArgumentException("Cannot perform merge: 'master' must not be null, " +
					"and at least 1 person target must be passed.");
		} else {

			// check every person really exist
			for (URI target : targets) {
				if (!isTypedAs(target, PIMO.Person)) {
					throw new NotFoundException("Cannot perform merge: "+target
							+" does not exist, or it is not a pimo:Person.");
				}
			}

//			// each pimo:Person has one nco:PersonContact as groundingOccurrence
//			// all need to be merged into one grounding occurrence
//			Node masterOccurrence = ModelUtils.findObject(tripleStore, master, PIMO.groundingOccurrence);
//			List<URI> targetOccurrences = new ArrayList<URI>();
//			Node occ = null;
//			if (masterOccurrence != null) {
//				// collect other people grounding occurrences
//				for (URI target : targets) {
//					occ = ModelUtils.findObject(tripleStore, target, PIMO.groundingOccurrence);
//					if (occ != null) {
//						targetOccurrences.add(occ.asURI());
//					}
//				}
//
//				for (URI targetOccurrence : targetOccurrences) {
//					// removes the statements from 'target' resource which properties
//					// are restricted by cardinality to 1, and the 'master' already
//					// has a value for that property
//					List<URI> propertiesToRemove = getPropertiesWithMaxCardinality(master, 1);
//					for (URI property : propertiesToRemove) {
//						tripleStore.removeStatements(Variable.ANY, targetOccurrence, property, Variable.ANY);
//					}
//					
//					// replace all grounding occurrences for the URI of the first one found
//					logger.debug("replacing nco:PersonContact "+targetOccurrence+" by "+masterOccurrence);
//					tripleStore.replaceUri(targetOccurrence, masterOccurrence.asURI());
//				}
//			}
			
			// merge persons 1..n with 'master' person
			for (URI target : targets) {
				// removes the statements from 'target' resource which properties
				// are restricted by cardinality to 1, and the 'master' already
				// has a value for that property
				List<URI> propertiesToRemove = getPropertiesWithMaxCardinality(master, 1);
				for (URI property : propertiesToRemove) {
					tripleStore.removeStatements(Variable.ANY, target, property, Variable.ANY);
				}
				
				// replacing all references of 'target' person for 'master' person
				logger.debug("replacing pimo:Person "+target+" by "+master);
				tripleStore.replaceUri(target, master);
			}

			// loads the full merged person from the store
			return this.get(master, Person.class);
		}
	}
	

	/**
	 * Retrieves a list of properties used by a resource which have a maximum
	 * cardinality.
	 * @param resource the resource which may use properties restricted by cardinality
	 * @param cardinality the maximum cardinality for the properties
	 * @return a list of all properties used by the resource which cardinality is the
	 *         one passed as parameter
	 */
	private List<URI> getPropertiesWithMaxCardinality(URI resource, int cardinality) {
		List<URI> properties = new ArrayList<URI>();
		String query = StringUtils.strjoinNL(
				PimoService.SPARQL_PREAMBLE,
				"SELECT DISTINCT ?p WHERE {",
				"  ", resource.toSPARQL(), " ?p ?o .",
				"  ?p nrl:maxCardinality ?card .",
				"  FILTER (?card = %card%)".replaceFirst("%card%", Integer.toString(cardinality)),
				"}");
		ClosableIterator<QueryRow> rows = tripleStore.sparqlSelect(query).iterator();
		while (rows.hasNext()) {
			properties.add(rows.next().getValue("p").asURI());
		}
		rows.close();
		return properties;
	}

}