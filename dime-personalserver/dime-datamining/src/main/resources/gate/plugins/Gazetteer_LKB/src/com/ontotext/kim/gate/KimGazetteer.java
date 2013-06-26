package com.ontotext.kim.gate;

import gate.AnnotationSet;
import gate.Factory;
import gate.FeatureMap;
import gate.creole.AbstractLanguageAnalyser;
import gate.creole.ExecutionException;
import gate.creole.ResourceInstantiationException;
import gate.util.Files;
import gate.util.InvalidOffsetException;
import gate.util.LuckyException;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.ontotext.kim.KIMConstants;
import com.ontotext.kim.client.model.FeatureConstants;
import com.ontotext.kim.gate.KimLookupParser.AliasLookupDictionary;
import com.ontotext.kim.gate.KimLookupParser.EntityOccuranceHandler;
import com.ontotext.kim.model.AliasCacheImpl;


/**
 * The Large KB Gazetteer implemented gazetteer lookup over large knowledge bases
 * usually derived from RDF data
 * 
 * @author mnozchev
 */
public class KimGazetteer extends AbstractLanguageAnalyser {
	private static final long serialVersionUID = 3380L;

    private static Logger log = Logger.getLogger(KimGazetteer.class);
	
	private File dictionaryPath = new File(KIMConstants.KIM_CACHE_PATH);
	private boolean forceCaseSensitive = false;

	private class Annotater implements EntityOccuranceHandler {

		public int annotatedEntities = 0;
		public void processEntityOccurance(int start, int end, String instURI, String classURI) {

			FeatureMap fm = Factory.newFeatureMap();
			if (instURI != null) {
				fm.put(FeatureConstants.INSTANCE, instURI);
			}
			fm.put(FeatureConstants.CLASS, classURI);
			try {
				annotationSet.add(Long.valueOf(start), Long.valueOf(end),
						KIMConstants.LOOKUP, fm);
			}
			catch (InvalidOffsetException ioe) {
				throw new LuckyException(ioe.toString());
			}

			++annotatedEntities;

			if (!kimParser.isInterrupted() && annotationLimit > 0
					&& annotatedEntities > annotationLimit) {
			    log.warn("More than " + annotationLimit +
						" lookups found. Interrupting ...");
				kimParser.setInterrupted(true);
			}
		}
	}

	private int annotationLimit;

	/** the annotation set that results from the execution */
	protected AnnotationSet annotationSet;
	private transient KimLookupParser kimParser = null;
	private String annotationSetName;

	/** Does the actual loading and parsing of the lists. This method must be
	 * called before the gazetteer can be used.
	 * @throws ResourceInstantiationException
	 * @return returns this resource
	 */
	public gate.Resource init() throws ResourceInstantiationException {
		verifyLoggers("com.ontotext.kim");		
		verifyLoggers("org.openrdf.sesame");
		verifyLoggers("httpclient");
		verifyLoggers("org.apache.commons.httpclient");	
		return init(AliasCacheImpl.getInstance(dictionaryPath, getName()));
	} // Resource init()

	protected gate.Resource init(AliasLookupDictionary outerCache) {
		this.kimParser = new KimLookupParser(outerCache);
		return this;
	} // Resource init(EntitiesCache outerCache)

	@Override
	public void cleanup() {		
		super.cleanup();
		AliasCacheImpl.releaseCache(dictionaryPath, getName());
	}
	
	@Override
	public void reInit() throws ResourceInstantiationException {
		cleanup();
		init();
	}
	
	/**
	 * This method runs the gazetteer. It parses the document and looks-up
	 * the parsed phrases from the maps, in which the phrases vs. annotations
	 * are set, in order to generate an annotation set.
	 * It assumes that all the needed parameters
	 * are set. If they are not, an exception will be fired.
	 */
	public void execute() throws ExecutionException {
		//check initialization
		if (kimParser == null)
			throw new ExecutionException("init() must be called after the resource is created or deserialized");

		this.kimParser.setInterrupted(false);
		//check the input
		if (document == null) {
			throw new ExecutionException("Document is null!");
		} // if document is null

		if (annotationSetName == null ||
				annotationSetName.length() == 0) {
			annotationSet = document.getAnnotations();
		}
		else {
			annotationSet = document.getAnnotations(annotationSetName);
		}

		String content = document.getContent().toString();

		Annotater annot = new Annotater();
		this.kimParser.findLookups(content, annot);

		log.debug(annot.annotatedEntities + " lookup(s) annotated.");
		fireProcessFinished();
		if (isInterrupted())
			fireStatusChanged("Large KB Gazetteer processing interrupted!");
		else
			fireStatusChanged("Large KB Gazetteer processing finished!");

	} // execute ()

	@Override
	public synchronized void interrupt() {
		super.interrupt();
		if (this.kimParser != null)
			this.kimParser.setInterrupted(true);
	}

	public Integer getAnnotationLimit() {
		return annotationLimit;
	}

	public void setAnnotationLimit(Integer annotationLimit) {
		this.annotationLimit = annotationLimit != null ? annotationLimit : 0;
	}

	public URL getDictionaryPath() {
		try {
			return dictionaryPath.toURI().toURL();
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	public void setDictionaryPath(URL dictironaryPath) {
		this.dictionaryPath = Files.fileFromURL(dictironaryPath);
	} 

	/**
	 * Sets the AnnotationSet that will be used at the next run for the newly
	 * produced annotations.
	 */
	public void setAnnotationSetName(String newAnnotationSetName) {
		annotationSetName = newAnnotationSetName;
	}

	/**
	 * Gets the AnnotationSet that will be used at the next run for the newly
	 * produced annotations.
	 */
	public String getAnnotationSetName() {
		return annotationSetName;
	}

	public void setForceCaseSensitive(Boolean forceCaseSensitive) {
		if (forceCaseSensitive != null)
			this.forceCaseSensitive = forceCaseSensitive;
	}

	public Boolean getForceCaseSensitive() {
		return forceCaseSensitive;
	}	


	private void verifyLoggers(String loggerName) {
		Logger logger = Logger.getLogger(loggerName);
		if (logger.getLevel() == null && logger.getEffectiveLevel().equals(Level.DEBUG)) {
			logger.setLevel(Level.INFO);
			logger.info(
					"Logger " + loggerName + " level set to INFO, overriding the default effective level of DEBUG. " +
					"Set the level of " + loggerName + " explictly if required.");
		}

	}	
	
} // class KimGazetteer
