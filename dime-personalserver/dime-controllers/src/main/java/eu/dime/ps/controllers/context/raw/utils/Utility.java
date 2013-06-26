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
