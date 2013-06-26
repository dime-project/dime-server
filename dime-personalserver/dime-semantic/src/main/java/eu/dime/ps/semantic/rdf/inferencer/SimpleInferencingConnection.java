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
import info.aduna.iteration.CloseableIteration;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.RDFS;
import org.openrdf.sail.SailException;
import org.openrdf.sail.helpers.SailConnectionWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * This is a CrappyInferencer(TM), it is unsound, incomplete and at times
 * completely crazy. It only covers 3 cases:
 * 
 * <ul>
 * <li> Inverse Properties: (x, p1, y) => (y, p2, x) - where ( p1,
 * nrl:inverseProperty, p2 )</li>
 * <li> ABox Subclass Inference: (x, rdfs:subClass, y) => (x, rdfs:subClass, z)
 * for all z that is super-classes of y</li>
 * <li> ABox SubProperty Inference: (x, rdfs:subProperty, y) => (x,
 * rdfs:subProperty, z) for all z that is super-properties of y</li>
 * </ul>
 * 
 * The full RDFS entailments are described in 
 * <a href="http://www.w3.org/TR/rdf-mt/#RDFSRules"> RDFS entailment rules</a>.
 * 
 * For subclass and subproperty relations, the self-referencing relation is
 * added when the UU type rdfs:Class is added. 
 * 
 * Note that for inverseProperty the order of insertion is important, i.e. when
 * adding a new (a, inverseProperty, b), existing triples using a or b will not
 * be updated. Yes, this makes it wrong and inconsistent. I might fix this
 * later.
 * 
 * <h2>Queries</h2>
 * No type inference is performed for queries, so to get "inference" you have to
 * query for rdfs:subClassOf relationship. For example, imagine you want all
 * instance of MyClass (and all it's subclasses), you need this query:
 * 
 * <blockquote> SELECT ?X WHERE { ?X rdf:type ?t . ?t rdfs:subClassOf
 * prefix:MyClass } </blockquote>
 * 
 * If you really need to know the direct subClass of something, use the
 * nrl:directSubClassOf property. (This is made up for this crappy
 * implementation and is not really a part of NRL)
 * 
 * The same holds for subProperty "inference".
 * 
 * <h2>Note for deleting triples:</h2>
 * 
 * <ul>
 * <li>Deleting a triple with a predicate that has an inverse will also delete
 * the inverse triple. </li>
 * <li>Deleting subClassOf, subPropertyOf or nrl:inverseProperty triples will
 * <strong>NOT</strong> attempt to "fix" the triples already inferred from
 * this. Only future insertions will be correct. Yes this means it will be
 * inconsistent and wrong. I said it was crappy. I will not fix this. For
 * instance, if (A subclass B), (B subclass C) is in the store and (B subclass
 * C) is deleted, I will still think A is a subClass of C. </li>
 * </ul>
 * 
 * 
 * 
 * Implementation note: There is also an InferencerConnectionWrapper, not sure
 * what I gain if I would extend that instead. If nothing else, it requires that
 * you keep track of inferred and normal statements, which we dont' (cause we
 * are crappy) So I'll do it without.
 * 
 * 
 * 
 * TODO: Add a method for reverting to normal subClassOf - removing all
 * directSubClass triples and "inferred" subClassOf TODO: Make adding new
 * InverseProperties fix existing triples? - done for subClass, but requires
 * querying the store => slow
 * 
 * TODO: this must subclass InferencerConnectionWrapper and mark inferred statements
 * then deletion is better to achieve 
 * 
 * @author grimnes, sauermann
 * 
 * 
 * Added RDFS Entailments 7 and 9:
 * aaa rdfs:subPropertyOf bbb AND uuu aaa yyy => uuu bbb yyy
 * uuu rdfs:subClassOf xxx AND vvv rdf:type uuu => vvv rdf:type xxx
 * 
 * Inferred triples are stored in the graph/context of the original triples. 
 * 
 * @author Ismael Rivera
 */
public class SimpleInferencingConnection extends SailConnectionWrapper {
	
	public static final Resource INF_CONTEXT = new URIImpl("urn:graph:inferencing");

	public static final Resource COUNT_CONTEXT = new URIImpl("urn:graph:instance-count");

	private static final Resource[] NULL_CONTEXT = new Resource[]{null};

	private static final Logger logger = LoggerFactory.getLogger(SimpleInferencingSail.class);

	/**
	 * Note: these are also in pimo-ClientSession
	 * Never replace this with NRL.inverseProperty !
	 * We need a Sesame URI here.
	 * The comparison will not work any more!
	 */
	public static final URI INVERSE_PROPERTY = new URIImpl(NRL.NS_NRL + "inverseProperty");

	public static final URI DIRECT_SUBCLASS = new URIImpl(NRL.NS_NRL + "directSubClassOf");

	public static final URI DIRECT_SUBPROPERTY = new URIImpl(NRL.NS_NRL + "directSubPropertyOf");
	
	public static final URI INSTANCE_COUNT = new URIImpl(NRL.NS_NRL	+ "instanceCount" );
	
	final ConcurrentMap<Resource, Long> classInstancesCountBuffer = new ConcurrentHashMap<Resource, Long>();
	
	private SimpleInferencingSail sail;

	public SimpleInferencingConnection(SimpleInferencingSail sail)
			throws SailException {
		super(sail.getBaseSail().getConnection());
		this.sail = sail;
	}
	
	/**
	 * return true, when T is equals to RDFS:Class
	 * @param t
	 * @return
	 */
	public boolean isClassType(Value t) {
		if (RDFS.CLASS.equals(t))
			return true;
		return false;
	}
	
	private void instanceCount(Value obj, int delta) {
		if (!(obj instanceof Resource))
			return;
		//instance of type obj
		Long oldNumber = classInstancesCountBuffer.get(obj);
		if(oldNumber == null){
			oldNumber = sail.classInstancesCount.get(obj);
			if (oldNumber == null)
				oldNumber = 0L;
		}
		// actually, this counting is imaginary - addign the same triple again and again will skew it,
		// but alas, who cares
		oldNumber = oldNumber + delta;
		if (oldNumber < 0)
			oldNumber = 0L;
		classInstancesCountBuffer.put((Resource)obj, oldNumber);
	}

	@Override
	public void addStatement(Resource subj, URI pred, Value obj,
			Resource... contexts) throws SailException {
		if (contexts.length == 0)
			contexts = NULL_CONTEXT;
	
		// COUNT CLASS INSTANCES
		if(RDF.TYPE.equals(pred))
			instanceCount(obj, +1);
		
		for (Resource context : contexts) {
			
			if (pred.equals(INVERSE_PROPERTY)) {
				if (subj instanceof URI && obj instanceof URI) {
					sail.inverseProperties.put((URI) subj, (URI) obj);
					sail.inverseProperties.put((URI) obj, (URI) subj);
					super.addStatement(subj, pred, obj, context);
					super.addStatement((Resource) obj, pred, subj, contexts);
				}
			} else if (pred.equals(RDFS.SUBCLASSOF)) {
				if (obj instanceof Resource) {
                    addInference(subj, RDFS.SUBCLASSOF, (Resource)obj, context,
                        sail.subClasses, sail.superClasses, DIRECT_SUBCLASS);
				} else {
					logger.warn("Incorrect RDF encountered, objects of rdfs:subClass must be a Resource, was: "+obj);
				}
			} else if (pred.equals(RDFS.SUBPROPERTYOF)) {
				if (obj instanceof URI && subj instanceof URI) {
                    addInference(subj, RDFS.SUBPROPERTYOF, (URI)obj, context,
                        sail.subProperties, sail.superProperties, DIRECT_SUBPROPERTY);
                    
				} else {
					logger.warn("Incorrect RDF encountered, subject and object of rdfs:subProperty must be URIs, was: "+subj+", "+obj);
				}
			} else if (sail.inverseProperties.containsKey(pred)) {
				if (obj instanceof Resource) {
					super.addStatement((Resource) obj, sail.inverseProperties
							.get(pred), subj, contexts);
				} else {
					// hmm?
				}
				super.addStatement(subj, pred, obj, context);
			} else if (RDF.TYPE.equals(pred) ) {
				
				// Implement RDFS Entailments 6 and 10
				// check if object is class or pred
				if (RDF.PROPERTY.equals(obj))
					super.addStatement(subj, RDFS.SUBPROPERTYOF, subj, contexts);
				// check if object is class or pred
				if (RDFS.CLASS.equals(obj))
					super.addStatement(subj, RDFS.SUBCLASSOF, subj, contexts);
				
				// RDFS Entailment 9
				CloseableIteration<? extends Statement, SailException> it = super.getStatements((URI)obj, RDFS.SUBCLASSOF, null, true);
				try {
					while (it.hasNext()) {
						Statement statement = it.next();
						Value o = statement.getObject();
						
						super.addStatement(subj, RDF.TYPE, o, contexts);
					}
				} finally {
					if (it != null) {
						try {
							it.close();
						} catch (Exception x) {
							logger.warn("cannot do add-inference: "+x,x);
						}
					}
				}

				super.addStatement(subj, pred, obj, context);
			} else {
				
				// RDFS Entailment 7
				CloseableIteration<? extends Statement, SailException> it = super.getStatements(pred, RDFS.SUBPROPERTYOF, null, true);
				try {
					while (it.hasNext()) {
						Statement statement = it.next();
						URI p = (URI) statement.getObject();
						
						super.addStatement(subj, p, obj, contexts);
					}
				} finally {
					if (it != null) {
						try {
							it.close();
						} catch (Exception x) {
							logger.warn("cannot do add-inference: "+x,x);
						}
					}
				}

				super.addStatement(subj, pred, obj, context);
			}
		}
	}

	/**
	 * Called by addStatement, this uses the original context to store triples and
	 * then the INFCONTEXT for further recursion
	 */
	private void addInference(Resource subj, URI predicate, Resource obj, Resource context,
	        ListMap<Resource, Resource> subRelationOf, ListMap<Resource, Resource> superRelationOf,
	        URI directPredicate)
	    throws SailException {
		
	        subRelationOf.put(subj, obj);
	        superRelationOf.put(obj, subj);
	        
	        super.addStatement(subj, directPredicate, obj, context);	        
	        super.addStatement(subj, predicate, obj, context);	        
	        addInference(subj, predicate, obj, context, subRelationOf, superRelationOf, directPredicate, new HashSet<Statement>());
	}
	
	/**
	 * Note - this has one more parameter than the one above - a set keeping track of added statements
	 * @param subj
	 * @param predicate
	 * @param obj
	 * @param context
	 * @param subRelationOf
	 * @param superRelationOf
	 * @param directPredicate
	 * @param addedTriples
	 * @throws SailException
	 */
	private void addInference(Resource subj, URI predicate, Resource obj, Resource context, 
	        ListMap<Resource, Resource> subRelationOf, ListMap<Resource, Resource> superRelationOf,
	        URI directPredicate, Set<Statement> addedTriples)
	    throws SailException {
	        
		StatementImpl s = new StatementImpl(subj,predicate,obj);
		if (addedTriples.contains(s)) return;
		
		super.addStatement(subj, predicate, obj, context);
		addedTriples.add(s);
		for(Resource superThing: subRelationOf.get(obj)) {
			addInference(subj, predicate, superThing, context, subRelationOf, superRelationOf, directPredicate, addedTriples);
		}
		for(Resource subThing: superRelationOf.get(subj)) {
			addInference(subThing, predicate, obj, context, subRelationOf, superRelationOf, directPredicate, addedTriples);
		}
	        
	}

    
	/**
	 * remove the passed spo triple, also remove the direct triple using the 
	 * directpredicate.
	 * Using the listmaps pointing to super-relations of this prop
	 * and sub-relatopns of this prop, also remove the closure.
	 * @param subj
	 * @param predicate
	 * @param obj
	 * @param context
	 * @param subRelationOf
	 * @param superRelationOf
	 * @param directPredicate
	 * @throws SailException
	 */
	private void removeInference(Resource subj, URI predicate, Resource obj, Resource context,
	    ListMap<Resource, Resource> subRelationOf, ListMap<Resource, Resource> superRelationOf,
	    URI directPredicate)
	throws SailException {
	    subRelationOf.remove(subj, obj);
	    superRelationOf.remove(obj, subj);
	    
	    // INFERENCE
	    // remove marker for direct
	    super.removeStatements(subj, directPredicate, obj, context);
	    super.removeStatements(subj, predicate, obj, context);
	    
	    removeInference(subj, predicate, obj, context, subRelationOf, superRelationOf, directPredicate, new HashSet<Statement>());
	}
	
	private void removeInference(Resource subj, URI predicate, Resource obj, Resource context,
		    ListMap<Resource, Resource> subRelationOf, ListMap<Resource, Resource> superRelationOf,
		    URI directPredicate, Set<Statement> removedTriple)
		throws SailException {
		
		StatementImpl s = new StatementImpl(subj,predicate,obj);
		if (removedTriple.contains(s)) return; 
		
		super.removeStatements(subj, predicate, obj, context);
		removedTriple.add(s);
		
		for(Resource superThing: subRelationOf.get(obj)) {
			removeInference(subj, predicate, superThing, context, subRelationOf, superRelationOf, directPredicate, removedTriple);
		}
		for(Resource subThing: superRelationOf.get(subj)) {
			removeInference(subThing, predicate, obj, context, subRelationOf, superRelationOf, directPredicate, removedTriple);
		}
	}


	@Override
	public void removeStatements(Resource subj, URI pred, Value obj,
			Resource... contexts) throws SailException {
		
		// OLD PREPARATION, SHITTY
        // no context? remove it from any context where it exists
//        if (contexts.length == 0)
//        {
//            HashSet<Resource> contextsss = new HashSet<Resource>();
//            CloseableIteration<? extends Statement, SailException> i = getStatements(subj, pred, obj, true);
//            while (i.hasNext())
//                contextsss.add(i.next().getContext());
//            contexts = contextsss.toArray(new Resource[contextsss.size()]);
//        }
///		for (Resource context : contexts) {
		
		// PREPARATION
		CloseableIteration<? extends Statement, SailException> it = super.getStatements(subj, pred, obj, false, contexts);
		try {
			while (it.hasNext())
			{
				Statement s = it.next();
				subj = s.getSubject();
				pred = s.getPredicate();
				obj = s.getObject();
				Resource context = s.getContext();
				
				// COUNT CLASS INSTANCES
				if(RDF.TYPE.equals(pred)){
					//instance of type obj
					instanceCount(obj, -1);
				}
				
				// ACTUAL CODE
				// now remove on triple
				if (pred.equals(INVERSE_PROPERTY)) {
					sail.inverseProperties.remove(subj);
					sail.inverseProperties.remove(obj);
					super.removeStatements(subj, pred, obj, context);
				} else if (pred.equals(RDFS.SUBCLASSOF)) {
	                if (obj instanceof Resource) {
	                    removeInference(subj, RDFS.SUBCLASSOF, (Resource)obj, context,
	                        sail.subClasses, sail.superClasses, DIRECT_SUBCLASS);
	                } else {
	                    logger.warn("Incorrect RDF encountered, objects of rdfs:subClass must be a Resource, was: "+obj);
	                }
				} else if (pred.equals(RDFS.SUBPROPERTYOF)) {
					if (subj instanceof URI) {
						sail.subProperties.remove((URI) subj);
					} else {
						logger.warn("Incorrect RDF: attempting to remove rdfs:subPropertyOf triple where subject is not a URI, was: "+subj);
					}
					if (obj instanceof URI) {
						sail.superProperties.remove((URI) obj);
					} else {
						logger.warn("Incorrect RDF: attempting to remove rdfs:subPropertyOf triple where object is not a URI, was: "+obj);
					}
					super.removeStatements(subj, RDFS.SUBPROPERTYOF, obj, contexts);
					super.removeStatements(subj, DIRECT_SUBPROPERTY, obj, contexts);

				} else if (sail.inverseProperties.containsKey(pred)) {
	
					// remove both - to ensure at least something vaguely like
					// consistency.
					super.removeStatements(subj, pred, obj, context);
					if (obj instanceof Resource) {
						super.removeStatements((Resource) obj,
								sail.inverseProperties.get(pred), subj, contexts);
					} else {
						// hmm...
					}
				} else if (RDF.TYPE.equals(pred) ) {

					// Implement RDFS Entailments 6 and 10
					// check if object is class or pred
					if (RDF.PROPERTY.equals(obj))
						super.removeStatements(subj, RDFS.SUBPROPERTYOF, subj, contexts);
					// check if object is class or pred
					if (RDFS.CLASS.equals(obj))
						super.removeStatements(subj, RDFS.SUBCLASSOF, subj, contexts);

					// RDFS Entailment 9
					CloseableIteration<? extends Statement, SailException> cit = super.getStatements((URI)obj, RDFS.SUBCLASSOF, null, true);
					try {
						while (cit.hasNext()) {
							Statement statement = cit.next();
							Value o = statement.getObject();
							
							super.removeStatements(subj, RDF.TYPE, o, contexts);
						}
					} finally {
						if (cit != null) {
							try {
								cit.close();
							} catch (Exception x) {
								logger.warn("cannot do remove-inference: "+x,x);
							}
						}
					}
					
					super.removeStatements(subj, pred, obj, context);
				} else {
					
					// RDFS Entailment 7
					CloseableIteration<? extends Statement, SailException> cit = super.getStatements(pred, RDFS.SUBPROPERTYOF, null, true);
					try {
						while (cit.hasNext()) {
							Statement statement = cit.next();
							URI p = (URI) statement.getObject();
							
							super.removeStatements(subj, p, obj, contexts);
						}
					} finally {
						if (cit != null) {
							try {
								cit.close();
							} catch (Exception x) {
								logger.warn("cannot do remove-inference: "+x,x);
							}
						}
					}

					super.removeStatements(subj, pred, obj, context);
				}
			}
		} finally {
			if (it!=null)
			{
				try {
					it.close();
				} catch (Exception x) {
					logger.warn("cannot do delete-inference: "+x,x);
				}
			}
		}
	}

    @Override
    public void clear(Resource... context) throws SailException {
        super.clear(context);
        // re-initialize the buffers
        sail.reCreateInferenceIndex();
        classInstancesCountBuffer.clear();
    }
    
    @Override
    public void commit() throws SailException {
    	
    	//flush counter buffer 
    	for (Map.Entry<Resource, Long> entry : classInstancesCountBuffer.entrySet()) {
    		//store triples in store
    		//REOMVE all triples: s instanceCount ?
    		
    		super.removeStatements(entry.getKey(), INSTANCE_COUNT, null, COUNT_CONTEXT);
    		
    		//Add all tripples: s instanceCount ?
    		super.addStatement(entry.getKey(), INSTANCE_COUNT, new LiteralImpl(entry.getValue().toString()) , COUNT_CONTEXT);
    		
    		//update sail map
    		sail.classInstancesCount.put(entry.getKey(), entry.getValue());
    		
    	}
    	
    	super.commit();
    	classInstancesCountBuffer.clear();
    }
    
    @Override
    public void rollback() throws SailException {
    	super.rollback();
    	//discard counter buffer 
    	classInstancesCountBuffer.clear();
    }

}
