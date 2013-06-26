/*
 * Copyright (c) 2011 Telecom Italia S.p.A.
 */

package eu.dime.context;

import eu.dime.ps.contextprocessor.impl.RawContextNotification;

/**
 * This interface specifies the method(s) that must be implemented by any
 * prospective context listener.
 *
 */
public interface IContextListener
{
    /**
     * This method is asynchronously invoked by the context system to notify
     * the relevant {@link IContextListener} of a corresponding context change
     * event, as it is abstracted in the {@link ContextChangedEvent} argument.
     *
     * @param event abstracts the notified context change
     * @throws Exception 
     */
    public void contextChanged(final RawContextNotification notification) throws Exception;
}
