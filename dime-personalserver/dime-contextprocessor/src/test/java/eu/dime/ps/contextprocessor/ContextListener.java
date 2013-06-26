package eu.dime.ps.contextprocessor;

import eu.dime.context.IContextListener;
import eu.dime.context.model.api.IContextDataset;
import eu.dime.ps.contextprocessor.helper.ContextDataPrinter;
import eu.dime.ps.contextprocessor.impl.RawContextNotification;

public class ContextListener implements IContextListener{

	public void contextChanged(RawContextNotification notification) {

		System.out.println("A context notification has been received: (entity,scope)=("
				+ notification.getName() + ")");

	}

}
