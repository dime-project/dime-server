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

package eu.dime.ps.controllers.context.raw.utils;

import java.util.Date;

import org.apache.log4j.Logger;

import eu.dime.context.model.Constants;
import eu.dime.context.model.api.IContextElement;
import eu.dime.context.model.impl.Factory;

public class Utility {
	
	static Logger logger = Logger.getLogger(Utility.class);
	
	public static int getFirstSignificativeItem(IContextElement[] ctxEls, int period, int tolerance) {
		logger.debug("search first significative item between " + 
				Factory.timestampAsXMLString(new Date(System.currentTimeMillis() - ((period + tolerance)*1000))) 
				+ " and " + Factory.timestampAsXMLString(new Date(System.currentTimeMillis() - (period*1000))));
		int index = -1;
		for (int i=0; i<ctxEls.length; i++) {
			String timestamp = (String)ctxEls[i].getMetadata().getMetadatumValue(Factory.createScope(Constants.SCOPE_METADATA_TIMESTAMP)).getValue();
			long ts = Factory.timestampFromXMLString(timestamp);
			if (ts < (System.currentTimeMillis() - (period*1000)) &&
					(ts > (System.currentTimeMillis() - ((period + tolerance)*1000)))) {
				logger.debug("First significative item is at index " + i + ": " + timestamp);
				return i;
			}
		}
		return index;
	}

}
