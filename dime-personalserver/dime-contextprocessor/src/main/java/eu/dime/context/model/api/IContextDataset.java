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

package eu.dime.context.model.api;

import java.io.Serializable;

/**
 * This interface abstracts the structure and the functionality of a dataset
 *
 */
public interface IContextDataset extends Serializable
{
    /**
     * Retrieves all {@link eu.dime.context.model.api.IContextElement}s
     * which are included in this instance.
     *
     * @return an array of all context elements included in this context
     * dataset
     */
    public IContextElement [] getContextElements();

    /**
     * Retrieves all {@link eu.dime.context.model.api.IContextElement}s
     * which are included in this instance and which match the designated
     * {@link IEntity} and {@link IScope}.
     *
     * @param entity an instance of {@link eu.dime.context.model.api.IEntity}
     * @param scope an instance of {@link eu.dime.context.model.api.IScope}
     *
     * @return an array of all context elements included in this context
     * dataset and which match the designated entity and scope
     */
    public IContextElement [] getContextElements(final IEntity entity, final IScope scope);

    /**
     * Retrieves all {@link eu.dime.context.model.api.IContextElement}s
     * which are included in this instance and which match the designated
     * {@link IScope}.
     *
     * @param scope an instance of {@link eu.dime.context.model.api.IScope}
     *
     * @return an array of all context elements included in this context
     * dataset and which match the designated scope
     */
    public IContextElement [] getContextElements(final IScope scope);
    
    public String getTimeRef();
   
    /**
     * Retrieves the {@link eu.dime.context.model.api.IValue} of the most recent context datum (param)
     * or null if not present.
     *
     * @param entity an instance of {@link eu.dime.context.model.api.IEntity}
     * @param scope an instance of {@link eu.dime.context.model.api.IScope}
     * @param param scope of the context datum whose value is required 
     *
     * @return the required context value, if present
     */
    public IValue getLastValue(final IEntity entity, final IScope scope, final IScope param);
    
    /**
     * Retrieves the {@link eu.dime.context.model.api.IValue} of the current context datum (param)
     * or null if not present.
     *
     * @param entity an instance of {@link eu.dime.context.model.api.IEntity}
     * @param scope an instance of {@link eu.dime.context.model.api.IScope}
     * @param param scope of the context datum whose value is required 
     *
     * @return the required context value, if present
     */
    public IValue getCurrentValue(final IEntity entity, final IScope scope, final IScope param);
    
    /**
     * Retrieves the most recent {@link eu.dime.context.model.api.IContextElement}
     * which match the designated {@link IEntity} and {@link IScope}.
     *
     * @param entity an instance of {@link eu.dime.context.model.api.IEntity}
     * @param scope an instance of {@link eu.dime.context.model.api.IScope}
     *
     * @return the most recent context element included in this context
     * dataset and which match the designated entity and scope
     */
    public IContextElement getLastContextElement(final IEntity entity, final IScope scope);
    
    /**
     * Retrieves the current recent {@link eu.dime.context.model.api.IContextElement}
     * which match the designated {@link IEntity} and {@link IScope}, or null if not present.
     *
     * @param entity an instance of {@link eu.dime.context.model.api.IEntity}
     * @param scope an instance of {@link eu.dime.context.model.api.IScope}
     *
     * @return the current context element included in this context
     * dataset and which match the designated entity and scope
     */
    public IContextElement getCurrentContextElement(final IEntity entity, final IScope scope);
    
    /**
     * Checks if this instance of context dataset is empty or not.
     *
     * @return true if this instance of context dataset is empty, false otherwise
     */
    public boolean isEmpty();

    /**
     * A simple, empty implementation of the context dataset
     */
    public static final IContextDataset EMPTY_CONTEXT_DATASET = new IContextDataset()
    {
	public IContextElement [] getContextElements() { return IContextElement.EMPTY_CONTEXT_ELEMENT_ARRAY; }
	public IContextElement[] getContextElements(IEntity entity, IScope scope) { return IContextElement.EMPTY_CONTEXT_ELEMENT_ARRAY; }
	public IContextElement[] getContextElements(IScope scope) { return IContextElement.EMPTY_CONTEXT_ELEMENT_ARRAY; }
    public IValue getLastValue(final IEntity entity, final IScope scope, final IScope param) { return null; };
    public IValue getCurrentValue(final IEntity entity, final IScope scope, final IScope param){ return null; };
    public IContextElement getLastContextElement(final IEntity entity, final IScope scope){ return null;};
    public IContextElement getCurrentContextElement(final IEntity entity, final IScope scope){ return null;};
	public String getTimeRef(){return null;}
	public boolean isEmpty() { return true; }
    };
}
