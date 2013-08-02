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

package eu.dime.ps.storage.datastore;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ListIterator;

import eu.dime.ps.storage.datastore.types.DimeBinary;
import eu.dime.ps.storage.datastore.types.PersistentDimeObject;

public interface DataStore {

	public boolean addFile(String hash, String uri, InputStream is);

	public void storeBlob(String id, InputStream is, String tenant);

	public InputStream getBlob(String id) throws FileNotFoundException;

	public boolean update(String hash, String uri, InputStream is);

	public InputStream getByHash(String hash) throws FileNotFoundException;

	public boolean fileExists(String uri);

	public boolean delete(String uri) throws FileNotFoundException;

	public InputStream get(String uri) throws FileNotFoundException;

	public boolean isUnique(String uri) throws FileNotFoundException;

	public DimeBinary getFileByHash(String hash);

	public PersistentDimeObject getObject(String uri);

	public void close();

	public ListIterator<DimeBinary> getAllFiles();

	public void deleteAll();

}
