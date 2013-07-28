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

package eu.dime.ps.semantic.query;

import java.util.Collection;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdfreactor.schema.rdfs.Resource;

import com.hp.hpl.jena.graph.Node;

import eu.dime.ps.semantic.query.impl.AbstractQuery;
import eu.dime.ps.semantic.query.impl.BasicQuery;
import eu.dime.ps.semantic.query.impl.PimoQuery;

/**
 * The interface for queries.
 * <p>Instantiable implementations are:
 * <ul>
 * <li> {@link AbstractQuery}
 * <li> {@link BasicQuery}
 * <li> {@link PimoQuery}
 * </ul>
 *
 * @author Ismael Rivera
 *
 */
public interface Query<T extends Resource> {

	public static final int ORDER_UNKNOW = com.hp.hpl.jena.query.Query.ORDER_UNKNOW;
	public static final int ORDER_DEFAULT = com.hp.hpl.jena.query.Query.ORDER_DEFAULT;
	public static final int ORDER_DESCENDING = com.hp.hpl.jena.query.Query.ORDER_DESCENDING;
	public static final int ORDER_ASCENDING = com.hp.hpl.jena.query.Query.ORDER_ASCENDING;

	/** A blank node to represent ANY value. */
	public static final Node ANY = Node.createAnon();
	
	/** A query variable called "x". */
	public static final Node X = Node.createVariable("x");
	
	/** A query variable called "y". */
	public static final Node Y = Node.createVariable("y");

	/** A query variable called "z". */
	public static final Node Z = Node.createVariable("z");
	
	public static final Node THIS = Node.createVariable("result");

	/**
	 * Specifies the datasets or graphs to be included to executre the query.
	 * Similar to a FROM clause in SPARQL.
	 *  
	 * @param uris list of URIs to restrict the query
	 * @return the query itself
	 */
	Query<T> from(URI... uris);

	/**
	 * Specifies the datasets or graphs to be included to executre the query.
	 * Similar to a FROM NAMED clause in SPARQL.
	 *  
	 * @param uris list of URIs to restrict the query
	 * @return the query itself
	 */
	Query<T> fromNamed(URI... uris);
	
	/**
	 * Adds the "DISTINCT" keyword to the query
	 * 
	 * @return the query itself
	 */
	Query<T> distinct();
	
	/**
	 * Allows to selective set or filter the properties to include in the
	 * objects returned by the query. This allows to retrieve partial data
	 * about a resource, for instance only the rdfs:label, etc.
	 * 
	 * If no properties are set, all the data about the resources will be
	 * returned.
	 * 
	 * If the methods 'select' and 'discard' are used in the same query,
	 * the properties to be returned would be the difference between
	 * selected and discarded.
	 * 
	 * @param properties a list of property URIs
	 * @return the query itself
	 */
	Query<T> select(URI... properties);
	
	/**
	 * Allows to selective set the properties to exclude from the
	 * objects returned by the query. This allows to retrieve all the
	 * metadata for some resource, except for some properties out
	 * of interest.
	 * 
	 * If no properties are set, all the data about the resources will be
	 * returned.
	 * 
	 * If the methods 'select' and 'discard' are used in the same query,
	 * the properties to be returned would be the difference between
	 * selected and discarded.
	 * 
	 * @param properties a list of property URIs
	 * @return the query itself
	 */
	// deprecated, wasn't really useful after all
//	Query<T> discard(URI... properties);
	
	/**
	 * Enables you to set WHERE patterns to restrict the query. The <em>subject</em> is 
	 * the 'result' of the query. The <em>predicate</em> is required as a parameter, and
	 * the <em>object</em> would be specify by calling 'a value setter' method such as 
	 * {@link #eq(Object), #is(Object), #like(String), #gt(Long)} and so on.
	 * 
	 * @param uri predicate of the triple pattern in the WHERE clause
	 * @return the query itself
	 */
	Query<T> where(URI uri);

	/**
	 * Enables you to set WHERE patterns to restrict the query, and specify a different
	 * <em>subject</em> rather than the 'result' of the query. The <em>predicate</em> 
	 * is required as a parameter, and the <em>object</em> would be specify by calling 
	 * 'a value setter' method such as {@link #eq(Object), #is(Object), #like(String),
	 * #gt(Long)} or similar.
	 * 
	 * @param subject subject of the triple pattern in the WHERE clause
	 * @param predicate predicate of the triple pattern in the WHERE clause
	 * @return the query itself
	 */
	Query<T> where(Object subject, Object predicate);

	/**
	 * Enables you to set WHERE patterns to restrict the query by specifing 
	 * <em>subject</em>, <em>predicate</em>, and <em>object</em> of the triple
	 * pattern. 
	 * 
	 * @param subject subject of the triple pattern in the WHERE clause
	 * @param predicate predicate of the triple pattern in the WHERE clause
	 * @param object object of the triple pattern in the WHERE clause
	 * @return the query itself
	 */
	Query<T> where(Object subject, Object predicate, Object object);
	
	/**
	 * Creates a triple pattern as {@link where(URI)}, but this pattern and
	 * following patterns created by 'where' calls are grouped together, and
	 * joined to previous groups/clauses by an UNION in SPARQL.
	 * 
	 * @param uri
	 * @return the query itself
	 */
	Query<T> orWhere(URI uri);
	
	/**
	 * Same as {@link where(Object, Object)}, except that acts as an OR as 
	 * in {@link orWhere(URI)}.
	 *  
	 * @param subject
	 * @param predicate
	 * @return
	 */
	Query<T> orWhere(Object subject, Object predicate);
	
	/**
	 * Same as {@link where(Object, Object, Object)}, except that acts as an OR as 
	 * in {@link orWhere(URI)}. 
	 * 
	 * @param subject
	 * @param predicate
	 * @param object
	 * @return
	 */
	Query<T> orWhere(Object subject, Object predicate, Object object);
	
	/**
	 * Sets the <em>object</em> value of the pattern in the WHERE group.  
	 * 
	 * @param value
	 * @return the query itself
	 */
	Query<T> eq(Object value);

	Query<T> neq(Object value);

	/**
	 * Same as {@link #eq(Object)}
	 * 
	 * @param value
	 * @return the query itself
	 */
	Query<T> is(Object value);

	Query<T> isNot(Object value);

	/**
	 * Allows to apply a condition where the pattern is not
	 * expressed, or in other words, the subject must not
	 * be related to any resource on the property specified.
	 * This must follow a call to {@link where(URI)} to specify
	 * which property/predicate to use in the pattern.  
	 * 
	 * This is called Negation as Failure in logic programming.
	 * 
	 * @return the query itself
	 */
	Query<T> isNull();

	/**
	 * Allows to match a value with a regular expression. By default, the
	 * comparison is case-insensitive.
	 *  
	 * @param value
	 * @return the query itself
	 */
	Query<T> like(String value);

	/**
	 * Same as {@link #like(String)}, except it is possible to specify if 
	 * the comparison is case-sensitive or not.
	 * 
	 * @param value
	 * @param isCaseSensitive
	 * @return
	 */
	Query<T> like(String value, boolean isCaseSensitive);

	/**
	 * Sets the expression 'greater than' to the numeric value as
	 * object of the pattern.
	 *  
	 * @param value
	 * @return the query itself
	 */
	Query<T> gt(Long value);
	
	/**
	 * Same as {@link gt(Long} but the value is a Double.
	 * 
	 * @param value
	 * @return the query itself
	 */
	Query<T> gt(Double value);
	
	/**
	 * Sets the expression 'greater than or equal to' to the numeric value as
	 * object of the pattern.
	 * 
	 * @param value
	 * @return the query itself
	 */
	Query<T> gte(Long value);
	
	/**
	 * Same as {@link gte(Long} but the value is a Double.
	 *  
	 * @param value
	 * @return the query itself
	 */
	Query<T> gte(Double value);
	
	/**
	 * Sets the expression 'less than' to the numeric value as
	 * object of the pattern.
	 * 
	 * @param value
	 * @return the query itself
	 */
	Query<T> lt(Long value);
	
	/**
	 * Same as {@link lt(Long} but the value is a Double.
	 * 
	 * @param value
	 * @return the query itself
	 */
	Query<T> lt(Double value);
	
	/**
	 * Sets the expression 'less than or equal to' to the numeric value as
	 * object of the pattern.
	 * 
	 * @param value
	 * @return the query itself
	 */
	Query<T> lte(Long value);
	
	/**
	 * Same as {@link lte(Long} but the value is a Double.
	 * 
	 * @param value
	 * @return the query itself
	 */
	Query<T> lte(Double value);
	
	/**
	 * Lets you set an ORDER clause
	 * 
	 * @param property
	 * @return the query itself
	 */
	Query<T> orderBy(URI property);
	
	/**
	 * Lets you set an ORDER clause
	 * 
	 * @param property	
	 * @param direction	
	 * @return the query itself
	 */
	Query<T> orderBy(URI property, int direction);
	
	/**
	 * Limits the number of elements to be returned by the query.
	 * 
	 * @param limit
	 * @return the query itself
	 */
	Query<T> limit(Long limit);
	
	/**
	 * Lets you set an OFFSET clause
	 * 
	 * @param offset
	 * @return the query itself
	 */
	Query<T> offset(Long offset);
	
	/**
	 * Returns the first item of the results of the query.
	 * 
	 * @return first result of the query 
	 */
	T first();
	
	/**
	 * Returns the last item of the results of the query.
	 * 
	 * @return last result of the query 
	 */
	T last();
	
	/**
	 * Returns all the items as result of the execution of the query.
	 * 
	 * @return all the results of the query 
	 */
	Collection<T> results();

	Collection<org.ontoware.rdf2go.model.node.Resource> ids();

	/**
	 * Returns the count of the result of the execution of the query.
	 * 
	 * @return number representing the count of elements 
	 */
	Long count();
	
}
