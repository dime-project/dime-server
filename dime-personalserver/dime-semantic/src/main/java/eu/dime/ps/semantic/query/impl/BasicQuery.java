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

package eu.dime.ps.semantic.query.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.model.QueryRow;
import org.ontoware.rdf2go.model.node.BlankNode;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.vocabulary.XSD;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.datatypes.BaseDatatype;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.rdf.model.AnonId;
import com.hp.hpl.jena.sparql.expr.E_Bound;
import com.hp.hpl.jena.sparql.expr.E_Equals;
import com.hp.hpl.jena.sparql.expr.E_GreaterThan;
import com.hp.hpl.jena.sparql.expr.E_GreaterThanOrEqual;
import com.hp.hpl.jena.sparql.expr.E_LessThan;
import com.hp.hpl.jena.sparql.expr.E_LessThanOrEqual;
import com.hp.hpl.jena.sparql.expr.E_LogicalNot;
import com.hp.hpl.jena.sparql.expr.E_NotEquals;
import com.hp.hpl.jena.sparql.expr.E_Regex;
import com.hp.hpl.jena.sparql.expr.E_Str;
import com.hp.hpl.jena.sparql.expr.Expr;
import com.hp.hpl.jena.sparql.expr.ExprVar;
import com.hp.hpl.jena.sparql.expr.NodeValue;
import com.hp.hpl.jena.sparql.syntax.Element;
import com.hp.hpl.jena.sparql.syntax.ElementFilter;
import com.hp.hpl.jena.sparql.syntax.ElementGroup;
import com.hp.hpl.jena.sparql.syntax.ElementOptional;
import com.hp.hpl.jena.sparql.syntax.ElementUnion;
import com.hp.hpl.jena.vocabulary.RDF;

import eu.dime.ps.semantic.exception.NotFoundException;
import eu.dime.ps.semantic.exception.QueryException;
import eu.dime.ps.semantic.query.Query;
import eu.dime.ps.semantic.query.Queryable;

/**
 * 
 * @author Ismael Rivera
 */
public class BasicQuery<T extends org.ontoware.rdfreactor.schema.rdfs.Resource> extends AbstractQuery<T> {

	private static final Logger logger = LoggerFactory.getLogger(BasicQuery.class);

	/**
	 * The type URI is extracted from the class 'returnType' attribute RDFS_CLASS
	 */
	private String typeUri = null;
	
	private boolean finished = false;

	private Node whereSubject;
	private Node wherePredicate;

	protected ElementGroup baseGroup = new ElementGroup();
	protected ElementGroup whereGroup = new ElementGroup();
	protected List<ElementGroup> unionGroups = new LinkedList<ElementGroup>();
	
	private int varCount = 0;
	
	public BasicQuery(Queryable queryable, Class<T> returnType) {
		super(queryable, returnType);

		try {
			typeUri = returnType.getDeclaredField("RDFS_CLASS").get("").toString();
		} catch (Exception e) {
			logger.error("'type' URI cannot be extracted from "+returnType, e);
		}

		baseGroup.addTriplePattern(new Triple(THIS, Node.createURI(RDF.type.toString()), Node.createURI(typeUri)));

		jenaQuery.setQuerySelectType();
		jenaQuery.addResultVar(THIS.getName());
	}

	public BasicQuery(Queryable queryable, Class<T> returnType, URI... types) {
		super(queryable, null);
		this.returnType = returnType;
		
		if (types.length == 1) {
			baseGroup.addTriplePattern(new Triple(THIS, Node.createURI(RDF.type.toString()), Node.createURI(types[0].toString())));
		} else {
			ElementUnion typeUnion = new ElementUnion();
			for (URI typeUri : types) {
				ElementGroup group = new ElementGroup();
				group.addTriplePattern(new Triple(THIS, Node.createURI(RDF.type.toString()), Node.createURI(typeUri.toString())));
				typeUnion.addElement(group);
			}
			baseGroup.addElement(typeUnion);
		}
		
		jenaQuery.setQuerySelectType();
		jenaQuery.addResultVar(THIS.getName());
	}

	/**
	 * @deprecated better use constructor
	 */
	@Deprecated
	public static <T extends org.ontoware.rdfreactor.schema.rdfs.Resource> Query<T> find(Queryable queryable, Class<T> returnType) {
		return new BasicQuery<T>(queryable, returnType);
	}
	
	/**
	 * @deprecated better use constructor
	 */
	@Deprecated
	public static Query<org.ontoware.rdfreactor.schema.rdfs.Resource> find(Queryable queryable, URI... types) {
		return new BasicQuery<org.ontoware.rdfreactor.schema.rdfs.Resource>(
				queryable, org.ontoware.rdfreactor.schema.rdfs.Resource.class, types);
	}

	private Node createUniqueVariable() {
		return Node.createVariable("v" + varCount++);
	}
	
	@Override
	public Query<T> where(URI uri) {
		whereSubject = THIS;
		wherePredicate = Node.createURI(uri.toString());
		return this;
	}

	@Override
	public Query<T> where(Object subject, Object predicate) throws QueryException {
		// extract resource identifier (URI or blank node) from a RDFReactor object
		if (subject instanceof org.ontoware.rdfreactor.schema.rdfs.Resource) {
			subject = ((org.ontoware.rdfreactor.schema.rdfs.Resource) subject).asResource();
		}
		
		if (THIS.equals(subject)) {
			whereSubject = THIS;
		} else if (ANY.equals(subject)) {
			whereSubject = Node.createAnon();
		} else if (subject instanceof Node) { // variable X, Y, Z case
			whereSubject = (Node) subject;
		} else if (subject instanceof BlankNode) {
			whereSubject = Node.createAnon(new AnonId(subject.toString()));
		} else if (subject instanceof URI) {
			whereSubject = Node.createURI(((URI) subject).toString());
		} else {
			throw new QueryException("Only an URI or a variable are permitted for the subject.");
		}

		if (predicate instanceof URI) {
			wherePredicate = Node.createURI(((URI) predicate).toString());
		} else if (ANY.equals(predicate)) {
			wherePredicate = createUniqueVariable();
		} else if (predicate instanceof Node) { // variable X, Y, Z case
			wherePredicate = (Node) predicate;
		} else {
			throw new QueryException("Only an URI is permitted for the predicate.");
		}

		return this;
	}
	
	@Override
	public Query<T> where(Object subject, Object predicate, Object object) throws QueryException {
		Node s = null, p = null, o = null;

		// extract resource identifier (URI or blank node) from a RDFReactor object
		if (subject instanceof org.ontoware.rdfreactor.schema.rdfs.Resource) {
			subject = ((org.ontoware.rdfreactor.schema.rdfs.Resource) subject).asResource();
		}
		if (object instanceof org.ontoware.rdfreactor.schema.rdfs.Resource) {
			object = ((org.ontoware.rdfreactor.schema.rdfs.Resource) object).asResource();
		}
		
		if (THIS.equals(subject)) {
			s = THIS;
		} else if (ANY.equals(subject)) {
			s = Node.createAnon();
		} else if (subject instanceof Node) { // variable X, Y, Z case
			s = (Node) subject;
		} else if (subject instanceof URI) {
			s = Node.createURI(((URI) subject).toString());
		} else if (subject instanceof BlankNode) {
			s = Node.createAnon(new AnonId(object.toString()));
		} else {
			throw new QueryException("Only an URI or a variable are permitted for the subject.");
		}

		if (predicate instanceof URI) {
			p = Node.createURI(((URI) predicate).toString());
		} else if (ANY.equals(predicate)) {
			p = createUniqueVariable();
		} else if (predicate instanceof Node) { // variable X, Y, Z case
			p = (Node) predicate;
		} else {
			throw new QueryException("Only an URI or a variable are permitted for the predicate.");
		}

		if (THIS.equals(object)) {
			o = THIS;
		} else if (ANY.equals(object)) {
			o = Node.createAnon();
		} else if (object instanceof Node) {
			o = (Node) object;
		} else if (object instanceof URI) {
			o = Node.createURI(object.toString());
		} else if (object instanceof BlankNode) {
			o = Node.createAnon(new AnonId(object.toString()));
		} else if (object instanceof Double){
			o = Node.createLiteral(Double.toString((Double) object), null, new BaseDatatype(XSD._double.toString()));
		} else if (object instanceof Float){
			o = Node.createLiteral(Float.toString((Float) object), null, new BaseDatatype(XSD._float.toString()));
		} else if (object instanceof Long){
			o = Node.createLiteral(Long.toString((Long) object), null, new BaseDatatype(XSD._long.toString()));
		} else if (object instanceof Integer){
			o = Node.createLiteral(Integer.toString((Integer) object), null, new BaseDatatype(XSD._integer.toString()));
		} else {
			o = Node.createLiteral(object.toString());
		}

		// a string is handled by the SPARQL function 'str' and a filter
		if (object instanceof String) {
			Node var = createUniqueVariable();
			Expr equals = new E_Equals(new E_Str(new ExprVar(var.getName())), NodeValue.makeString((String) object));
			whereGroup.addTriplePattern(new Triple(s, p, var));
			whereGroup.addElementFilter(new ElementFilter(equals));
		} else {
			whereGroup.addTriplePattern(new Triple(s, p, o));
		}
		
		return this;
	}

	@Override
	public Query<T> orWhere(URI uri) {
		unionGroups.add(whereGroup);
		whereGroup = new ElementGroup();
		whereSubject = THIS;
		wherePredicate = Node.createURI(uri.toString());
		return this;
	}
	
	@Override
	public Query<T> orWhere(Object subject, Object predicate) {
		unionGroups.add(whereGroup);
		whereGroup = new ElementGroup();
		return where(subject, predicate);
	}

	@Override
	public Query<T> orWhere(Object subject, Object predicate, Object object) {
		unionGroups.add(whereGroup);
		whereGroup = new ElementGroup();
		return where(subject, predicate, object);
	}

	@Override
	public Query<T> eq(Object value) {
		Node object = null; // object in single triple pattern for URI, blank nodes and variables
		Node var = createUniqueVariable();
		Expr eqExpr = null; // used for literals (double, string, etc.)
		
		// extract resource identifier (URI or blank node) from a RDFReactor object
		if (value instanceof org.ontoware.rdfreactor.schema.rdfs.Resource) {
			value = ((org.ontoware.rdfreactor.schema.rdfs.Resource) value).asResource();
		}

		if (THIS.equals(value)) {
			object = THIS;
		} else if (ANY.equals(value)) {
			object = Node.createAnon();
		} else if (value instanceof Node) {
			object = (Node) value;
		} else if (value instanceof URI) {
			object = Node.createURI(((URI) value).toString());
		} else if (value instanceof BlankNode) {
			object = Node.createAnon(new AnonId(value.toString()));
		} else if (value instanceof Double) {
			eqExpr = new E_Equals(new ExprVar(var.getName()), NodeValue.makeDouble((Double) value));
		} else if (value instanceof Float) {
			eqExpr = new E_Equals(new ExprVar(var.getName()), NodeValue.makeFloat((Float) value));
		} else if (value instanceof Long) {
			eqExpr = new E_Equals(new ExprVar(var.getName()), NodeValue.makeInteger((Long) value));
		} else if (value instanceof Integer) {
			eqExpr = new E_Equals(new ExprVar(var.getName()), NodeValue.makeInteger((Integer) value));
		} else if (value instanceof Enum) {
			eqExpr = new E_Equals(new E_Str(new ExprVar(var.getName())), NodeValue.makeString(value.toString()));
		} else if (value instanceof String) {
			// a string is handled by the SPARQL function 'str' and a filter
			eqExpr = new E_Equals(new E_Str(new ExprVar(var.getName())), NodeValue.makeString((String) value));
		} else {
			throw new QueryException("Object of type "+value.getClass()+" is not supported in eq().");
		}

		if (object == null) {
			whereGroup.addTriplePattern(new Triple(whereSubject, wherePredicate, var));
			whereGroup.addElementFilter(new ElementFilter(eqExpr));
		} else {
			whereGroup.addTriplePattern(new Triple(whereSubject, wherePredicate, object));
		}
		
		return this;
	}

	@Override
	public Query<T> neq(Object value) {
		Node var = createUniqueVariable();
		Expr neqExpr = null;

		// extract resource identifier (URI or blank node) from a RDFReactor object
		if (value instanceof org.ontoware.rdfreactor.schema.rdfs.Resource) {
			value = ((org.ontoware.rdfreactor.schema.rdfs.Resource) value).asResource();
		}

		if (THIS.equals(value)) {
			neqExpr = new E_NotEquals(new ExprVar(var.getName()), NodeValue.makeNode(THIS));
		} else if (ANY.equals(value)) {
			neqExpr = new E_NotEquals(new ExprVar(var.getName()), NodeValue.makeNode(Node.createAnon()));
		} else if (value instanceof Node) {
			neqExpr = new E_NotEquals(new ExprVar(var.getName()), NodeValue.makeNode((Node) value));
		} else if (value instanceof URI) {
			Node object = Node.createURI(value.toString());
			neqExpr = new E_NotEquals(new ExprVar(var.getName()), NodeValue.makeNode(object));
		} else if (value instanceof BlankNode) {
			Node object = Node.createAnon(new AnonId(value.toString()));
			neqExpr = new E_NotEquals(new ExprVar(var.getName()), NodeValue.makeNode(object));
		} else if (value instanceof Double) {
			neqExpr = new E_NotEquals(new ExprVar(var.getName()), NodeValue.makeDouble((Double) value));
		} else if (value instanceof Float) {
			neqExpr = new E_NotEquals(new ExprVar(var.getName()), NodeValue.makeFloat((Float) value));
		} else if (value instanceof Long) {
			neqExpr = new E_NotEquals(new ExprVar(var.getName()), NodeValue.makeInteger((Long) value));
		} else if (value instanceof Integer) {
			neqExpr = new E_NotEquals(new ExprVar(var.getName()), NodeValue.makeInteger((Integer) value));
		} else if (value instanceof Enum) {
			neqExpr = new E_NotEquals(new E_Str(new ExprVar(var.getName())), NodeValue.makeString(value.toString()));
		} else if (value instanceof String) {
			// a string is handled by the SPARQL function 'str' and a filter
			neqExpr = new E_NotEquals(new E_Str(new ExprVar(var.getName())), NodeValue.makeString((String) value));
		} else {
			throw new QueryException("Object of type "+value.getClass()+" is not supported in eq().");
		}

		whereGroup.addTriplePattern(new Triple(whereSubject, wherePredicate, var));
		whereGroup.addElementFilter(new ElementFilter(neqExpr));

		return this;
	}

	@Override
	public Query<T> is(Object value) {
		return eq(value);
	}

	@Override
	public Query<T> isNot(Object value) {
		return neq(value);
	}

	@Override
	public Query<T> isNull() {
		Node var = createUniqueVariable();
		
		ElementGroup group = new ElementGroup();
		group.addTriplePattern(new Triple(whereSubject, wherePredicate, var));
		ElementOptional optional = new ElementOptional(group);
		whereGroup.addElement(optional);
		
		Expr bound = new E_LogicalNot(new E_Bound(new ExprVar(var.getName())));
		whereGroup.addElementFilter(new ElementFilter(bound));

		return this;
	}
	
	@Override
	public Query<T> like(String value) {
		return like(value, "i"); // "i" flash indicates case-insensitive
	}
	
	@Override
	public Query<T> like(String value, boolean isCaseSensitive) {
		return like(value, "");
	}

	private Query<T> like(String value, String flags) {
		Node var = createUniqueVariable();
		// Regular expression for regex(?what, "value", flags) 
		Expr regex = new E_Regex(new E_Str(new ExprVar(var.getName())), value, flags);

		return filterByExpression(var, regex);
	}

	@Override
	public Query<T> gt(Long value) {
		return gt(createUniqueVariable(), NodeValue.makeInteger(value));
	}
	
	@Override
	public Query<T> gt(Double value) {
		return gt(createUniqueVariable(), NodeValue.makeDouble(value));
	}

	private Query<T> gt(Node left, NodeValue right) {
		return filterByExpression(left, new E_GreaterThan(new ExprVar(left.getName()), right));
	}
	
	@Override
	public Query<T> gte(Long value) {
		return gte(createUniqueVariable(), NodeValue.makeInteger(value));
	}
	
	@Override
	public Query<T> gte(Double value) {
		return gte(createUniqueVariable(), NodeValue.makeDouble(value));
	}

	private Query<T> gte(Node left, NodeValue right) {
		return filterByExpression(left, new E_GreaterThanOrEqual(new ExprVar(left.getName()), right));
	}
	
	@Override
	public Query<T> lt(Long value) {
		return lt(createUniqueVariable(), NodeValue.makeInteger(value));
	}

	@Override
	public Query<T> lt(Double value) {
		return lt(createUniqueVariable(), NodeValue.makeDouble(value));
	}

	private Query<T> lt(Node left, NodeValue right) {
		return filterByExpression(left, new E_LessThan(new ExprVar(left.getName()), right));
	}
	
	@Override
	public Query<T> lte(Long value) {
		return lte(createUniqueVariable(), NodeValue.makeInteger(value));
	}

	@Override
	public Query<T> lte(Double value) {
		return lte(createUniqueVariable(), NodeValue.makeDouble(value));
	}

	private Query<T> lte(Node left, NodeValue right) {
		return filterByExpression(left, new E_LessThanOrEqual(new ExprVar(left.getName()), right));
	}
	
	private Query<T> filterByExpression(Node left, Expr expr) {
		whereGroup.addTriplePattern(new Triple(whereSubject, wherePredicate, left));
		whereGroup.addElementFilter(new ElementFilter(expr));
		
		return this;
	}

	@Override
	public Query<T> orderBy(URI property) {
		return orderBy(property, ORDER_DEFAULT);
	}
	
	@Override
	public Query<T> orderBy(URI property, int direction) {
		Node orderVar = Node.createVariable("order");
		baseGroup.addTriplePattern(new Triple(THIS, Node.createURI(property.toString()), orderVar));
		this.jenaQuery.addOrderBy(orderVar, direction);
		return this;
	}
	
	@Override
	public T first() {
		buildQueryPattern();

		T first = null;
		ClosableIterator<QueryRow> it = null;
		try {
			it = rdfStore.sparqlSelect(jenaQuery.toString()).iterator();
			if (it.hasNext()) {
				QueryRow row = it.next();
				org.ontoware.rdf2go.model.node.Resource result = row.getValue(THIS.getName()).asResource();
				try {
					first = get(result);
				} catch (NotFoundException e) {
					logger.error("query result " + result + " cannot be found in the store: " + e, e);
					first = null;
				} 
			}
		} finally {
			if (it != null) it.close();
		}
		logger.debug("first result: "+first);
		return first;
	}

	@Override
	public T last() {
		buildQueryPattern();

		T last = null;
		ClosableIterator<QueryRow> it = null;
		try {
			it = rdfStore.sparqlSelect(jenaQuery.toString()).iterator();
			QueryRow lastRow = null;
			while (it.hasNext()) {
				lastRow = it.next();
			}
			if (lastRow != null) {
				URI result = lastRow.getValue(THIS.getName()).asURI();
				try {
					last = get(result);
				} catch (NotFoundException e) {
					logger.error("query result " + result + " cannot be found in the store: " + e, e);
					last = null;
				} 
			}
		} finally {
			if (it != null) it.close();
		}
		logger.debug("last result: "+last);
		return last;
	}

	@Override
	public Collection<T> results() {
		buildQueryPattern();
		
		Collection<T> results = new ArrayList<T>();
		ClosableIterator<QueryRow> it = null;
		try {
			it = rdfStore.sparqlSelect(jenaQuery.toString()).iterator();
			while (it.hasNext()) {
				QueryRow row = it.next();
				org.ontoware.rdf2go.model.node.Resource result = row.getValue(THIS.getName()).asResource();
				try {
					results.add(get(result));
				} catch (NotFoundException e) {
					logger.error("query result " + result + " not added to the results list, it cannot be found in the store: " + e, e);
				} 
			}
		} finally {
			if (it != null) it.close();
		}
		logger.debug("query results: "+Arrays.toString(results.toArray()));
		return results;
	}

	@Override
	public Collection<org.ontoware.rdf2go.model.node.Resource> ids() {
		buildQueryPattern();
		
		Collection<org.ontoware.rdf2go.model.node.Resource> ids = new ArrayList<org.ontoware.rdf2go.model.node.Resource>();
		ClosableIterator<QueryRow> it = null;
		try {
			it = rdfStore.sparqlSelect(jenaQuery.toString()).iterator();
			while (it.hasNext()) {
				ids.add(it.next().getValue(THIS.getName()).asResource());
			}
		} finally {
			if (it != null) it.close();
		}
		logger.debug("query results: "+Arrays.toString(ids.toArray()));
		return ids;
	}

	@Override
	public Long count() {
		buildQueryPattern();
		
		// it would be better to have a specific SPARQL query for the count, not retrieving
		// all unnecessary data just for a count, but...
		
		long count = 0;
		ClosableIterator<QueryRow> it = rdfStore.sparqlSelect(jenaQuery.toString()).iterator();
		while (it.hasNext()) {
			it.next();
			count++;
		}
		return count;
	}

	private void buildQueryPattern() {
		if (finished)
			return;

		ElementGroup queryPattern = new ElementGroup();
		if (unionGroups.size() > 0) {
			ElementUnion union = new ElementUnion();
			for (ElementGroup group : unionGroups) {
				union.addElement(merge(baseGroup, group));
			}
			if (!whereGroup.isEmpty()) {
				union.addElement(merge(baseGroup, whereGroup));
			}
			queryPattern.addElement(union);
		} else if (!whereGroup.isEmpty()) {
			for (Element el : baseGroup.getElements()) {
				queryPattern.addElement(el);
			}
			for (Element el : whereGroup.getElements()) {
				queryPattern.addElement(el);
			}
		} else {
			for (Element el : baseGroup.getElements()) {
				queryPattern.addElement(el);
			}
		}
		jenaQuery.setQueryPattern(queryPattern);
		logger.debug(jenaQuery.toString());
		finished = true;
	}
	
	private ElementGroup merge(ElementGroup groupA, ElementGroup groupB) {
		ElementGroup mergedGroup = new ElementGroup();
		for (Element el : groupA.getElements()) {
			mergedGroup.addElement(el);
		}
		for (Element el : groupB.getElements()) {
			mergedGroup.addElement(el);
		}
		return mergedGroup;
	}
	
}
