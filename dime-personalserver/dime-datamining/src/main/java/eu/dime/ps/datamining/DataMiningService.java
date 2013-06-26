package eu.dime.ps.datamining;

import eu.dime.commons.datamining.CrawlData;
import eu.dime.ps.datamining.exceptions.DataMiningException;

public interface DataMiningService {
	
	void addCrawlData(CrawlData data) throws DataMiningException;

	void updateCrawlData(CrawlData data) throws DataMiningException;
	
	void removeCrawlData(String uri) throws DataMiningException;
	
	boolean existData(String uri);

	boolean existDataByHash(String hash);
	
	String getHash(String uri) throws DataMiningException;
	
}
