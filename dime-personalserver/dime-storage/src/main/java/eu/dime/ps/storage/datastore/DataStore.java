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
