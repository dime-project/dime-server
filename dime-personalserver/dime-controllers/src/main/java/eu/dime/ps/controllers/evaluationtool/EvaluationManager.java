package eu.dime.ps.controllers.evaluationtool;

import eu.dime.commons.dto.Evaluation;

public interface EvaluationManager {
	
	public boolean saveEvaluation(Evaluation ev,String tool);
}
