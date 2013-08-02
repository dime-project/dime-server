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

package eu.dime.ps.semantic.service.context;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.URI;

import eu.dime.ps.semantic.service.LiveContextSession;
import eu.dime.ps.semantic.service.impl.PimoService;

/**
 * 
 * @author Ismael Rivera
 */
public class LiveContextServiceImpl extends LiveContextServiceBase {

    private Model previousContext;
    private Model liveContext;
    private UpdateStrategy updateStrategy;

	public <T extends UpdateStrategy> LiveContextServiceImpl(PimoService pimoService, Class<T> strategyClass) {
		super(pimoService);
		this.previousContext = tripleStore.getModel(previousContextGraph);
		this.liveContext = tripleStore.getModel(liveContextGraph);
		
		Exception exception = null;
		try {
			Class[] parmTypes = { Model.class, Model.class };
			Constructor constructor = strategyClass.getConstructor(parmTypes);
			this.updateStrategy = (UpdateStrategy) constructor.newInstance(previousContext, liveContext);
		} catch (SecurityException e) {
			exception = e;
		} catch (NoSuchMethodException e) {
			exception = e;
		} catch (IllegalArgumentException e) {
			exception = e;
		} catch (InstantiationException e) {
			exception = e;
		} catch (IllegalAccessException e) {
			exception = e;
		} catch (InvocationTargetException e) {
			exception = e;
		}
		if (exception != null) {
			throw new RuntimeException("LiveContextService cannot be initialized. " +
					"The UpdateStrategy implementation class is expected to have a constructor with 2 parameters: " +
					"previous context and live context models.", exception);
		}

		// fallback call, if the update strategy initialization failed, a strategy is
		// created by default
		if (updateStrategy == null) {
			this.updateStrategy = new SnapshotBasedStrategy(previousContext, liveContext);
		}
	}
	
	@Override
	public LiveContextSession getSession(URI dataSource) {
		if (dataSource == null) {
			throw new IllegalArgumentException("dataSource must be a valid URI, not null");
		}
		return new LiveContextSessionImpl(pimoService.getName(), liveContext, updateStrategy, dataSource);
	}

}