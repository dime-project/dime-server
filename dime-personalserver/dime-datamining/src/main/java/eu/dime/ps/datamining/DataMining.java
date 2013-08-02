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

package eu.dime.ps.datamining;


import eu.dime.commons.datamining.Data;
import eu.dime.ps.datamining.exceptions.DataMiningException;

/**
 *
 * @author Will Fleury
 */
public interface DataMining {


	/**
	 * Might split this into three separate components
	 * 
	 * 1: DataMinner
	 * 2: ServiceSubscriber
	 * 3: DataMapper
	 */


	/***************************************
	 * Data Receiving Methods from Client(s)
	 ***************************************/

	/**
	 * The data consistency over all the clients is managed by this module. 
	 * 
	 * The crawlers (clients) should call one of the four data handling methods
	 * below 
	 * 1: processNewData(..)
	 * 2: processDeletedData(..)
	 * 3: processDuplicatedData(..)
	 * 4: processUpdatedData(..)
	 * 
	 * These are the most common actions a crawler will encounter and therefore
	 * should be sufficient. If more are needed they can be added. 
	 */

	/**
	 * When a crawler on any of the clients discovers new data which has not 
	 * been processed yet it should call this method. 
	 * 
	 * If the data has already been added by another client it will
	 * not be reprocessed but the duplication will be recorded for consistency
	 * purposes.
	 * 
	 * @param data 
	 * @throws DataMiningException when data processing error occurs
	 */
	public void processNewData(Data data) throws DataMiningException;

	/**
	 * This method should be called when some piece of crawled data is removed 
	 * from any of the clients. 
	 * 
	 * If it still exists on other clients that is not a problem as the 
	 * consistency is managed by this component. If no other instance of this
	 * data exists on any of the other clients then it will be removed fully.
	 * 
	 * @param hash the UID identifying the piece of data which has been removed
	 * @throws DataMiningException when data processing error occurs
	 */
	public void processDeletedData(String uri) throws DataMiningException;

	/**
	 * This method is responsible for handling duplicate instances of data on 
	 * a client. If the crawler on the client finds multiple instances of the 
	 * same document or information in different locations it should call this
	 * method.
	 * 
	 * @param the data representing the instance which is duplicated.
	 * @throws DataMiningException when data processing error occurs
	 */
	public void processDuplicatedData(Data data) throws DataMiningException;

	/**
	 * Call this method when some piece of data was updated on a client. 
	 * 
	 * The consistency of the information including is managed by this module.
	 * 
	 * @param oldHash the UID which originally represented the data
	 * @param newData the updated Data to process. 
	 * @throws DataMiningException when data processing error occurs
	 */
	public void processUpdatedData(Data newData) throws DataMiningException;

	public boolean existData(String uri);

	public boolean existDataByHash(String hash);
}
