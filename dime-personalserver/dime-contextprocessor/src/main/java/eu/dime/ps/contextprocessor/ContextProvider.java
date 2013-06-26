/*
 * Copyright (c) 2011 Telecom Italia S.p.A.
 */

package eu.dime.ps.contextprocessor;

import eu.dime.context.model.api.IScope;

public class ContextProvider {

	private IContextProvider cp;
	private IScope outputScope;
	private IScope[] inputScopeArr;
	
	public IContextProvider getCp() {
		return cp;
	}
	public void setCp(IContextProvider cp) {
		this.cp = cp;
	}
	public IScope getOutputScope() {
		return outputScope;
	}
	public void setOutputScope(IScope outputScope) {
		this.outputScope = outputScope;
	}
	public IScope[] getInputScopeArr() {
		return inputScopeArr;
	}
	public void setInputScopeArr(IScope[] inputScopeArr) {
		this.inputScopeArr = inputScopeArr;
	}
	
}
