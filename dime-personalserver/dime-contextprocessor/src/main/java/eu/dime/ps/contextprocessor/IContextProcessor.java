package eu.dime.ps.contextprocessor;

import eu.dime.context.IContextListener;

import eu.dime.context.exceptions.ContextException;
import eu.dime.context.model.api.*;
import eu.dime.ps.semantic.model.dao.Account;
import eu.dime.ps.storage.entities.Tenant;


/**
 * This is the main point of interaction with the context service (and the
 * context system in general). Any context client requiring to interact
 * with the context system can achieve it through this interface.
 *
 */
public interface IContextProcessor {
	
	public void addProximityAccount(String said, Account account);
	
	public Account getProximityAccount(String said);
	
    /**
     * Provides synchronous access to the specified context data,
     * not expired at the time being.
     *
     * @param entity describes the entity of the desired context data
     * @param scope describes the scope of the desired context data
     * @return a dataset containing the context which corresponds to the
     * requested entity/scope
     * @throws eu.dime.context.exceptions.ContextException when a error
     * occurs
     */
    public IContextDataset getContext(
    	Tenant tenant,
	    final IEntity entity,
	    final IScope scope)
	    throws ContextException;
    
    /**
     * Updates new context data.
     *
     * @param context the new context information to be updated
     * @throws eu.dime.context.exceptions.ContextException when a error
     * occurs
     */
    public void contextUpdate(Tenant tenant,
    	final IContextDataset context)
    	throws ContextException;
    
    /**
     * Provides asynchronous access to the specified context data.
     *
     * @param entity describes the entity of the desired context data
     * @param scope describes the scope of the desired context data
     * @param listener typically a reference to the client which requests the
     * asynchronous notification for the given query
     * @throws eu.dime.context.exceptions.ContextException when a error
     * occurs
     */
    public void subscribeContext(
	    final IEntity entity,
	    final IScope scope,
	    final IContextListener listener)
	    throws ContextException;
    
    /**
     * Deletes context data.
     *
     * @param entity describes the entity of the context data to be removed
     * @param scope describes the scope of the context data to be removed
     * @throws eu.dime.context.exceptions.ContextException when a error
     * occurs
     */
    public void deleteContext(
	    final IEntity entity,
	    final IScope scope)
	    throws ContextException;
    
    /**
     * Provides synchronous access to the specified context data on history.
     *
     * @param entity describes the entity of the desired context data
     * @param scope describes the scope of the desired context data
     * @param since start limit on timestamp (xs:DateTime string)
     * @param until end limit on timestamp (xs:DateTime string)
     * @return a dataset containing the requested context data
     * @throws eu.dime.context.exceptions.ContextException when a error
     * occurs
     */
    public IContextDataset getContext(
    	Tenant tenant,	
	    final IEntity entity,
	    final IScope scope,
	    String since,
	    String until)
	    throws ContextException;
    
    /**
     * Provides synchronous access to the specified context data, last (limit)
     * context elements are returned. The ContextDataset is ordered from the
     * most to the less recent context element.
     *
     * @param entity describes the entity of the desired context data
     * @param scope describes the scope of the desired context data
     * @param limit max number of returned context element 
     * @return a dataset containing the requested context data
     * @throws eu.dime.context.exceptions.ContextException when a error
     * occurs
     */
    public IContextDataset getContext(
    		Tenant tenant,
    	    final IEntity entity,
    	    final IScope scope,
    	    int limit)
    throws ContextException;
}
