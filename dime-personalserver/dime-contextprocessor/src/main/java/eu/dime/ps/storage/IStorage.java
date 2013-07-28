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

package eu.dime.ps.storage;

import eu.dime.context.model.api.IContextElement;
import eu.dime.context.model.api.IEntity;
import eu.dime.context.model.api.IScope;
import eu.dime.ps.storage.entities.Tenant;

public interface IStorage {
	
    /**
     * Used to store an instance of {@link IContextElement} in the repository.
     *
     * @param contextElement the instance to be stored in the repository.
     */
    void storeContextElement(Tenant t, IContextElement contextElement) throws StorageException;
    
    /**
     * Retrieve all context elements stored in the repository of the given
     * type.
     *
     * @param entity the {@link IEntity} of the given type
     * @param scope the {@link IScope} of the given type
     * @return an array containing all context elements stored in the
     * repository of the given type.
     */
    public IContextElement [] getContextElements(
    	Tenant t,
	    final IEntity entity,
	    final IScope scope) throws StorageException;

    /**
     *
     * Retrieve all context elements stored in the repository of the given
     * type and in the timerange specified by the from and to parameters.
     *
     * @param entity the {@link IEntity} of the given type
     * @param scope the {@link IScope} of the given type
     * @param fromTimestamp the from parameter (in milliseconds)
     * @param toTimestamp the to parameter (in milliseconds)
     * @return an array containing all context elements stored in the
     * repository of the given type and in the timerange specified by the from
     * and to parameters.
     */
    public IContextElement [] getContextElements(
    	Tenant t,
	    final IEntity entity,
	    final IScope scope,
	    final long fromTimestamp,
	    final long toTimestamp) throws StorageException;   
    
    /**
     * Returns the valid context element stored in the cache.
     *
     * @param entity the {@link IEntity} of
     * the context element to be returned
     * @param scope the {@link IScope} of the
     * context element to be returned
     *
     * @return the {@link IContextElement}
     * which was stored in the cache last, or <code>null</code> if no element
     * was stored yet.
     */
    public IContextElement[] getCurrentContextElements(
    	Tenant t,
	    final IEntity entity,
	    final IScope scope) throws StorageException;
    
    /**
     * Deletes all the context elements stored in the cache.
     *
     * @param entity the {@link IEntity} of
     * the context elements to be deleted
     * @param scope the {@link IScope} of the
     * context elements to be deleted
     *
     * @return
     */
    public void deleteContextElements(
	    final IEntity entity,
	    final IScope scope) throws StorageException;
    
    /**
    *
    * Retrieve the (limit) most recent (i.e. with higher timestamp) context
    * elements stored in the repository of the given type.
    *
    * @param entity the {@link IEntity} of the given type
    * @param scope the {@link IScope} of the given type
    * @param limit max number of context element
    * @return an array containing context elements stored in the
    * repository.
    */
   public IContextElement [] getContextElements(
		Tenant t,
	    final IEntity entity,
	    final IScope scope,
	    final int limit) throws StorageException;
   
}
