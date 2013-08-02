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

package com.ontotext.kim.gate;

import gate.util.Files;

import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.FileUtils;

import com.ontotext.kim.util.StringTransformations;

public class SettingsHashBuilder {

	public int getHash(URL configFile, String query) {
		query = StringTransformations.stripMultiWS(query);
		try {
			String configString =
        FileUtils.readFileToString(Files.fileFromURL(configFile));
			configString = StringTransformations.stripMultiWS(configString);
			return (query + ";" + configString).hashCode();
		}
		catch (IOException e) {
			return query.hashCode();
		}
	}
}
