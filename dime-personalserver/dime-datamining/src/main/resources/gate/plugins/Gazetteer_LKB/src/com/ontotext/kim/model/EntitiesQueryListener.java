package com.ontotext.kim.model;

import java.io.IOException;

import com.ontotext.kim.client.model.OWLConstants;
import com.ontotext.kim.semanticrepository.SimpleTableListener;
import com.ontotext.kim.util.KimLogs;

public abstract class EntitiesQueryListener extends SimpleTableListener {

	private int loaded;
	private long startTime = 0;

	public EntitiesQueryListener() {
		super(4);
	}

	@Override
	public void startTableQueryResult() throws IOException {
		super.startTableQueryResult();
		this.loaded = 0;
		this.startTime = System.currentTimeMillis();
	}

	public void endTuple() throws IOException {
		try {
			String aliasLabel = row[0];
			String instUri    = row[1];
			String classUri   = row[2];

			if (classUri == null) {
				classUri = OWLConstants.CLASS_THING;
			}

			addEntity(instUri, classUri, aliasLabel);
			++loaded;

			logProgress(instUri, classUri, aliasLabel);
		}
		catch (Exception x) {
			KimLogs.logNERC_GAZETTEER.error("There has been an exception in endTuple()\n" +
					"dumping stack trace but continuing with the next tuples \n" +
					"Counters are cleared at finally{...}", x);
		}

	}

	private void logProgress(String instUri, String classUri, String aliasLabel) {
		// We log at the first, because sometimes the query runs slow and sometimes the query freezes.
		// If we know that we have received at least one result, then the query has not frozen.
		if (loaded == 1) {
			KimLogs.logSEMANTIC_REPOSITORY.info(
					String.format("Aliases loading started ... First one is:\nlabel - %s\nURI - %s\nClass URI - %s", 
							aliasLabel, instUri, classUri));
		}
		if (loaded % 10000 == 0) {
			long currentTime = System.currentTimeMillis();
			KimLogs.logSEMANTIC_REPOSITORY.info(
					String.format("Loaded %s aliases in %s second(s)." , loaded, (currentTime - startTime)/1000) );
		}
	}

	@Override
	public void endTableQueryResult() throws IOException {		
		super.endTableQueryResult();
		if (startTime == 0) {
			KimLogs.logSEMANTIC_REPOSITORY.info("Loaded no labels.");
			return;
		}
		long currentTime = System.currentTimeMillis();
		KimLogs.logSEMANTIC_REPOSITORY.info(
				String.format("Loading completed: %s aliases in %s second(s)." , loaded, (currentTime - startTime)/1000) );
	}

	protected abstract void addEntity(String instUri, String classUri, String aliasLabel);

}
