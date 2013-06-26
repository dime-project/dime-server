/**
 * Copyright (c) 2006-2009, NEPOMUK Consortium
 * 
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 * 
 *     * Redistributions of source code must retain the above copyright notice, 
 *       this list of conditions and the following disclaimer.
 *
 *     * Redistributions in binary form must reproduce the above copyright 
 *       notice, this list of conditions and the following disclaimer in the 
 * 	documentation and/or other materials provided with the distribution.
 *
 *     * Neither the name of the NEPOMUK Consortium nor the names of its 
 *       contributors may be used to endorse or promote products derived from 
 * 	this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 **/
package eu.dime.ps.semantic.rdf.inferencer;

import ie.deri.smile.vocabulary.NRL;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.query.Binding;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.repository.sail.SailRepositoryConnection;
import org.openrdf.sail.Sail;
import org.openrdf.sail.SailConnection;
import org.openrdf.sail.SailException;
import org.openrdf.sail.helpers.SailWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An inferencing sail, all the action happens in the connection
 * 
 * @see SimpleInferencingConnection
 * @author grimnes
 * 
 */
public class SimpleInferencingSail extends SailWrapper {

	private static final Logger logger = LoggerFactory.getLogger(SimpleInferencingSail.class);

	protected ConcurrentMap<URI, URI> inverseProperties;

    /**
     * key - nrl:directSubPropertyOf - value
     */
	protected ListMap<Resource, Resource> subProperties;

	/**
	 *  key = rdf:type , value = number of instances
	 */
	protected ConcurrentMap<Value, Long> classInstancesCount;
	
    /**
     * value - nrl:directSubPropertyOf - key
     */
    protected ListMap<Resource, Resource> superProperties;

	protected ListMap<Resource, Resource> subClasses;

	protected ListMap<Resource, Resource> superClasses;
	
	private SailRepository repository;

	public SimpleInferencingSail() {
		super();
	}

	public SimpleInferencingSail(Sail base) {
		super();
		setBaseSail(base);
	}

	@Override
	public void initialize() throws SailException {
		super.initialize();
		init();
	}

	private void init() {
		inverseProperties = new ConcurrentHashMap<URI, URI>();
		subClasses = new ListMap<Resource, Resource>();
		superClasses = new ListMap<Resource, Resource>();
		subProperties = new ListMap<Resource, Resource>();
		superProperties = new ListMap<Resource, Resource>();
		classInstancesCount = new ConcurrentHashMap<Value, Long>();
		try {
			createInferenceIndex();
		} catch (SailException e) {
			logger.warn("Exception while initializing CrappyInferencingSail");
		}

	}


	/**
     * a context has been deleted. 
     * Instead of handling this properly, just recreate all indizes
     * by loading them from the db.
     *
     */
    protected void reCreateInferenceIndex() {
        init();
    }

	private void createInferenceIndex() throws SailException {
		logger.debug("Creating inferencing indices...");
		// SailConnection c = getConnection();

		try {
			// wrap in SailRepositoryConnection
			SailRepositoryConnection src = getSailRepositoryConnection();

			TupleQueryResult r = src
					.prepareTupleQuery(
							QueryLanguage.SPARQL,
							"PREFIX nrl: <"+NRL.NS_NRL+"> SELECT ?x ?y WHERE { ?x nrl:inverseProperty ?y }")
					.evaluate();

			// is this ok, or do I need to close the iterator?
			while (r.hasNext()) {
				BindingSet s = r.next();
				inverseProperties.put((URI) s.getValue("x"), (URI) s
						.getValue("y"));
				inverseProperties.put((URI) s.getValue("y"), (URI) s
						.getValue("x"));
			}

			// cache subClass relationships

			r = src
					.prepareTupleQuery(
							QueryLanguage.SPARQL,
							"PREFIX nrl: <"+NRL.NS_NRL+"> SELECT ?x ?y WHERE { ?x nrl:directSubClassOf ?y }")
					.evaluate();
			while (r.hasNext()) {
				BindingSet s = r.next();
				subClasses.put((Resource) s.getValue("x"), (Resource) s
						.getValue("y"));
				superClasses.put((Resource) s.getValue("y"), (Resource) s
						.getValue("x"));
			}

			// cache subProperty relationships
			r = src
					.prepareTupleQuery(
							QueryLanguage.SPARQL,
							"PREFIX nrl: <"+NRL.NS_NRL+"> SELECT ?x ?y WHERE { ?x nrl:directSubPropertyOf ?y }")
					.evaluate();

			while (r.hasNext()) {
				BindingSet s = r.next();
				subProperties.put((URI) s.getValue("x"), (URI) s.getValue("y"));
				superProperties.put((URI) s.getValue("y"), (URI) s
						.getValue("x"));
			}
			
			//Query should return the count of instances for each distinct class.
			TupleQuery sparql = src.prepareTupleQuery(QueryLanguage.SPARQL,
					"PREFIX rdf:     <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" +
					"SELECT ?class WHERE { ?instance rdf:type ?class  }" 	);
			TupleQueryResult result = sparql.evaluate();
			try{
				while(result.hasNext()){
					BindingSet bindingSet = result.next();
					Binding type = bindingSet.getBinding("class");
					Long oldNumber = classInstancesCount.get(type.getValue());
					if(oldNumber == null){
						oldNumber = new Long(0);
					}
					classInstancesCount.put(type.getValue(), new Long(oldNumber.longValue() + 1));
				}
			}finally{
				result.close();
			}
			
			src.close();
		} catch (Exception x) {
			throw new SailException(x);
		}
	}

	@Override
	public SailConnection getConnection() throws SailException {
		return new SimpleInferencingConnection(this);
	}

	protected SailRepositoryConnection getSailRepositoryConnection()
			throws RepositoryException {
		return getRepository().getConnection();
	}

	/**
	 * wrap this sail in a repo to use the secret private
	 * SailRepositoryConnection's ability to parse queries.
	 * 
	 * @return
	 */
	protected SailRepository getRepository() {
		if (repository == null) {
			repository = new SailRepository(this);
		}
		return repository;
	}
	
	public void setParameter(String key, String value) {
		// mop
	}

}
