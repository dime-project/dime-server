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
