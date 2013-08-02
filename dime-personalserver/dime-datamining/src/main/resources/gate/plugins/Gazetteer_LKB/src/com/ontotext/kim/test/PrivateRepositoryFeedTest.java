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

package com.ontotext.kim.test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;

import junit.framework.TestCase;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.openrdf.repository.http.PrivateRepositoryFeed;

import com.ontotext.kim.client.query.KIMQueryException;
import com.ontotext.kim.gate.SettingsHashBuilder;
import com.ontotext.kim.model.Options;
import com.ontotext.kim.query.QueryResultCounter;

public class PrivateRepositoryFeedTest extends TestCase {

	public void testWithLLD() throws IOException, KIMQueryException {
		URL configReader = this.getClass().getClassLoader().getResource("config.ttl");
		String query = IOUtils.toString(this.getClass().getClassLoader().getResourceAsStream("query.txt"));
		int settingsHash = new SettingsHashBuilder().getHash(configReader, query);
		PrivateRepositoryFeed feed = new PrivateRepositoryFeed(configReader, query, settingsHash, new Options(Collections.EMPTY_MAP, new File(".")));
		QueryResultCounter counter = new QueryResultCounter();
		feed.feedTo(counter);
		assertEquals(100, counter.getCount());
	}
	
	public void testWithLocal() throws IOException, KIMQueryException {
		File configFile = new File("plugins/Gazetteer_LKB/samples/dictionary_from_local_ontology/config.ttl").getCanonicalFile();
		URL configReader = configFile.toURI().toURL();
		String query = FileUtils.readFileToString(new File(configFile.getParentFile(), "query.txt"));
		int settingsHash = new SettingsHashBuilder().getHash(configReader, query);
		PrivateRepositoryFeed feed = new PrivateRepositoryFeed(configReader, query, settingsHash, new Options(Collections.EMPTY_MAP, new File(".")));
		QueryResultCounter counter = new QueryResultCounter();
		feed.feedTo(counter);
		assertEquals(327, counter.getCount());
	}	
}
