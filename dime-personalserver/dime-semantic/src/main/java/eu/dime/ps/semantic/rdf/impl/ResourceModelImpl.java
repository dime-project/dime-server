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

package eu.dime.ps.semantic.rdf.impl;

import java.util.Calendar;
import java.util.Date;

import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.Variable;
import org.ontoware.rdfreactor.runtime.Base;

import eu.dime.ps.semantic.rdf.ResourceModel;

public class ResourceModelImpl implements ResourceModel {

	/**
	 * The RDF2Go Model wrapped and edited by this ResourceModelImpl.
	 * This Model may or may not be shared with other ResourceModelImpl.
	 */
	private Model model;

	/**
	 * Identifier (URI or BN) of the resource that is described in the contents of this ResourceModelImpl.
	 */
	private Resource resourceIdentifier;
	
	public ResourceModelImpl(Model model) {
		this(model, null);
	}

	public ResourceModelImpl(Model model, Resource resourceIdentifier) {
		this.model = model;
		this.resourceIdentifier = resourceIdentifier;
	}

	@Override
	public Resource getResourceIdentifier() {
		return this.resourceIdentifier;
	}
	
	@Override
	public void setResourceIdentifier(Resource resourceIdentifier) {
		this.resourceIdentifier = resourceIdentifier;
	}

	@Override
	public Model getModel() {
		return this.model;
	}

	@Override
	public void put(URI property, String value) {
		Base.set(this.model, this.resourceIdentifier, property, value);
	}

	@Override
	public void put(URI property, Date value) {
		Base.set(this.model, this.resourceIdentifier, property, value);
	}

	@Override
	public void put(URI property, Calendar value) {
		Base.set(this.model, this.resourceIdentifier, property, value);
	}

	@Override
	public void put(URI property, boolean value) {
		Base.set(this.model, this.resourceIdentifier, property, value);
	}

	@Override
	public void put(URI property, int value) {
		Base.set(this.model, this.resourceIdentifier, property, value);
	}

	@Override
	public void put(URI property, long value) {
		Base.set(this.model, this.resourceIdentifier, property, value);
	}

	@Override
	public void put(URI property, Node value) {
		Base.set(this.model, this.resourceIdentifier, property, value);
	}

	@Override
	public void add(URI property, String value) {
		Base.add(this.model, this.resourceIdentifier, property, value);
	}
	
	@Override
	public void add(URI property, Date value) {
		Base.add(this.model, this.resourceIdentifier, property, value);
	}

	@Override
	public void add(URI property, Calendar value) {
		Base.add(this.model, this.resourceIdentifier, property, value);
	}

	@Override
	public void add(URI property, boolean value) {
		Base.add(this.model, this.resourceIdentifier, property, value);
	}

	@Override
	public void add(URI property, int value) {
		Base.add(this.model, this.resourceIdentifier, property, value);
	}

	@Override
	public void add(URI property, long value) {
		Base.add(this.model, this.resourceIdentifier, property, value);
	}

	@Override
	public void add(URI property, Node value) {
		Base.add(this.model, this.resourceIdentifier, property, value);
	}

	@Override
	public String getString(URI property) {
		return (String) Base.get(this.model, this.resourceIdentifier, property, String.class);
	}

	@Override
	public Date getDate(URI property) {
		Calendar cal = (Calendar) Base.get(this.model, this.resourceIdentifier, property, Calendar.class);
		return cal.getTime();
	}

	@Override
	public Calendar getCalendar(URI property) {
		return (Calendar) Base.get(this.model, this.resourceIdentifier, property, Calendar.class);
	}

	@Override
	public Boolean getBoolean(URI property) {
		return (Boolean) Base.get(this.model, this.resourceIdentifier, property, Boolean.class);
	}

	@Override
	public Integer getInteger(URI property) {
		return (Integer) Base.get(this.model, this.resourceIdentifier, property, Integer.class);
	}

	@Override
	public Long getLong(URI property) {
		return (Long) Base.get(this.model, this.resourceIdentifier, property, Long.class);
	}

	@Override
	public URI getURI(URI property) {
		return (URI) Base.get(this.model, this.resourceIdentifier, property, URI.class);
	}

	@Override
	public Node getNode(URI property) {
		return (Node) Base.get(this.model, this.resourceIdentifier, property, Node.class);
	}

	@Override
	public void remove(URI property) {
		this.model.removeStatements(this.resourceIdentifier, property, Variable.ANY);
	}
	
	@Override
	public String getNamespace(String prefix) {
		return model.getNamespace(prefix);
	}

	// Not used, instead using class Base from RDFReactor
	// to be independent of RDFReactor, this method should be extended to cover
	// all basic types (String, integer, etc.) and Node (URI, blank node).
//	private Node getInternal(URI property) {
//		ClosableIterator<? extends Statement> statements = null;
//		try {
//			statements = model.findStatements(resourceIdentifier, property, Variable.ANY);
//			Node result = null;
//
//			if (statements.hasNext()) {
//				Statement firstStatement = (Statement) statements.next();
//				if (statements.hasNext()) {
//					// throw new MultipleValuesException(describedUri, property);
//				}
//				result = firstStatement.getObject();
//			}
//
//			return result;
//		} catch (ModelRuntimeException me) {
//			logger.error("Could not find statements", me);
//			return null;
//		} finally {
//			if (statements != null) {
//				statements.close();
//			}
//		}
//	}
	
//	@Override
//    public ResourceModel copy(Resource newIdentifier) {
//    	Model copyModel = RDF2Go.getModelFactory().createModel().open(); 
//    	ResourceModel copy = new ResourceModelImpl(copyModel, newIdentifier);
//		
//    	ClosableIterator<? extends Statement> statements = null;
//		try {
//			statements = model.iterator();
//			while (statements.hasNext()) {
//				Statement statement = statements.next();
//				Resource subject = statement.getSubject();
//				if (subject.equals(this.resourceIdentifier)) {
//					copyModel.addStatement(newIdentifier, statement.getPredicate(), statement.getObject());
//				} else {
//					copyModel.addStatement(statement);
//				}
//			}
//		} catch (ModelRuntimeException me) {
//			logger.error("Could not find statements", me);
//		} finally {
//			if (statements != null) {
//				statements.close();
//			}
//		}
//		
//		return copy;
//    }

}
