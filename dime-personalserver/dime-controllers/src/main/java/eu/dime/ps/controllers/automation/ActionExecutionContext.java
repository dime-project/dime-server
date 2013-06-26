package eu.dime.ps.controllers.automation;

import org.ontoware.rdf2go.model.ModelSet;

public class ActionExecutionContext {

	private final String tenant;
	private final ModelSet modelSet;

	public ActionExecutionContext(String tenant, ModelSet modelSet) {
		this.tenant = tenant;
		this.modelSet = modelSet;
	}
	
	public String getTenant() {
		return tenant;
	}

	public ModelSet getModelSet() {
		return modelSet;
	}

}
