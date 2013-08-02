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

package com.ontotext.kim.model;

import com.ontotext.kim.client.query.KIMQueryException;
import com.ontotext.kim.client.semanticrepository.QueryResultListener;
import com.ontotext.kim.client.semanticrepository.SemanticRepositoryAPI;
import com.ontotext.kim.client.semanticrepository.QueryResultListener.Feed;

public class KIMDataFeed implements Feed {

	private final SemanticRepositoryAPI semRep;
	private final String language;
	private final String query;
		
	public KIMDataFeed(SemanticRepositoryAPI semRep, String language,
			String query) {
		super();
		this.semRep = semRep;
		this.language = language;
		this.query = query;
	}

	public void feedTo(QueryResultListener listener) throws KIMQueryException {
		semRep.evaluateQuery(query, language, listener);
	}

}
