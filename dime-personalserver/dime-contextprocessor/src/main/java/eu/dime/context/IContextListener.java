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
