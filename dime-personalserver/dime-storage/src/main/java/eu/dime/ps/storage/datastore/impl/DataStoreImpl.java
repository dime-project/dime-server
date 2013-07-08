package eu.dime.ps.storage.datastore.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.query.Predicate;

import eu.dime.ps.storage.datastore.DataStore;
import eu.dime.ps.storage.datastore.types.DimeBinary;
import eu.dime.ps.storage.datastore.types.PersistentDimeObject;
import eu.dime.ps.storage.util.CMSInitHelper;
/**
 * DataStore is responsible for storing binary data. (context-blobs, documents, images etc.)
 * @author marcel
 *
 */
public class DataStoreImpl implements DataStore{
	
	private Logger logger = Logger.getLogger(DataStoreImpl.class);
	
	private ObjectContainer db;
	private long tenantId;
	private String blobPath;
	
	public static int instances;

	

	public DataStoreImpl(ObjectContainer container, long tenantId) {
		this.db = container;
		this.tenantId = tenantId;
		this.blobPath = CMSInitHelper.getCMSFolder() + File.separator 
				+ this.tenantId;
		new File(blobPath).mkdirs();
		
		instances++;
	}

	@Override
	public boolean addFile(String hash, String uri, InputStream is) {
		return addFileWithType(hash, uri, "file", is);
	}
	
	public boolean addFileWithType(String hash, String uri, String type, InputStream is){
		if (fileExists(uri)){
			logger.info("file exists, trying update");
			return true;//update(hash, uri, is);
		}
		try {
			DimeBinary binary = new DimeBinary(blobPath);

			binary.setHash(hash);
			binary.setRdfUri(uri);
			binary.setTenantId(tenantId);
			binary.setType(type);
			db.store(binary);
			binary.addBlob(is);
			db.commit();
		} catch (IOException e) {
			logger.error("Could not store File with uri: "+uri);
			return false;
		}	
		return true;
	}

	@Override
	public void storeBlob(String id, InputStream is, String tenant) {
		addFileWithType("", id, "blob", is);	
	}

	@Override
	public InputStream getBlob(final String id) throws FileNotFoundException {

		PersistentDimeObject object = getObject(id);
		if (object != null && object instanceof DimeBinary){
			try {
				return new FileInputStream(((DimeBinary) object).getFile());
			} catch (IOException e) {
				logger.warn("Could not read File. "+id, e);
				throw new FileNotFoundException("Could not load blob for id: "+id + " catched IOException");
			}
		} else {
			throw new FileNotFoundException("Could not find blob for id: "+id);
		}
	}
	
	public InputStream get(String uri) throws FileNotFoundException {
		return getBlob(uri);
	}

	
	@Override
	public PersistentDimeObject getObject(final String uri) {
		
		List <DimeBinary> objects = db.query(new Predicate<DimeBinary>() {
			private static final long serialVersionUID = 1L;

			public boolean match(DimeBinary object) {
		        return object.getRdfUri().equals(uri);
		    }
		});
		
		if (objects.isEmpty()){
			return null;
		} else {
			return objects.get(0);
		}
//		ObjectSet<PersistentDimeObject> set =  db.ext().query(PersistentDimeObject.class);
//		int size = set.size();
//		if (size > 0){
//			for (PersistentDimeObject persistentDimeObject : set) {
//				if (persistentDimeObject.getId().equals(uri)){
//					return persistentDimeObject;
//				}
//			}
//		} 
//		return null;
	}

	@Override
	public boolean update(String hash, String uri, InputStream is) {
			DimeBinary bin = (DimeBinary) getObject(uri);
			if (bin == null){
				return addFile(hash, uri, is);
			}
			if (hash.equals(bin.getHash())){
				return false;
			} else {
				bin.setHash(hash);
				try {
					bin.updateFile(is);
				} catch (IOException e) {
					logger.error("Could not update file: "+uri, e);
				}
				db.commit();
			}
		return false;
	}

	@Override
	public InputStream getByHash(String hash) throws FileNotFoundException {
		try {
			return new FileInputStream(getFileByHash(hash).getFile());
		} catch (IOException e) {
			throw new FileNotFoundException("Could not load blob for hash: "+hash + " catched IOException");
		}
	}

	@Override
	public boolean fileExists(String uri) {
		return getObject(uri) != null;
	}

	@Override
	public boolean isUnique(final String uri) throws FileNotFoundException {
		List <PersistentDimeObject> objects = db.query(new Predicate<PersistentDimeObject>() {

			private static final long serialVersionUID = 1L;

			@Override
			public boolean match(PersistentDimeObject binary) {
				return binary.getId().equals(uri);
			}
		});
		if (objects.isEmpty()){
			throw new FileNotFoundException("File with id: "+uri+" does not exist. ");
		} else {
			return (objects.size() == 1);
		}
	}

	@Override
	public ListIterator<DimeBinary> getAllFiles() {
		return db.query(DimeBinary.class).listIterator();
	}
	
	public ListIterator<PersistentDimeObject> getAll() {
		return db.query(PersistentDimeObject.class).listIterator();
	} 
	
	@Override
	public DimeBinary getFileByHash(final String hash) {
		List <DimeBinary> files = db.query(new Predicate<DimeBinary>() {

			private static final long serialVersionUID = 1L;

			@Override
			public boolean match(DimeBinary binary) {
				return binary.getHash().equals(hash);
			}
		});
		if (files.isEmpty()){
			return null;
		} else {
			return files.get(0);
		}
	}


	@Override
	public boolean delete(String uri) throws FileNotFoundException {
		PersistentDimeObject obj = getObject(uri);
		return delete(obj);
	}
	
	private File tempFile(InputStream is) throws IOException{
		String path = System.getProperty("java.io.tmpdir");
		File file = new File(path + File.separator + UUID.randomUUID().toString().substring(0,5));
		copyStreamToFile(is, file);
		return file;
	}
	
	private static void copyStreamToFile(InputStream inputStream,
			java.io.File file) throws IOException {
		OutputStream outputStream = new FileOutputStream(file);
		IOUtils.copy(inputStream, outputStream);
		outputStream.close();
	}

	@Override
	protected void finalize() throws Throwable {
		db.close();
		FileUtils.cleanDirectory(new File(blobPath + File.separator + "tmp-write"));
		instances--;
		super.finalize();
	}
	
	@Override
	public void close(){
		db.close();
	}

	@Override
	public void deleteAll() {
		ListIterator<PersistentDimeObject> listIt = getAll();
		while (listIt.hasNext()) {
			PersistentDimeObject persistentDimeObject = (PersistentDimeObject) listIt
					.next();
			delete(persistentDimeObject);
			
		}
		
	}

	private boolean delete(PersistentDimeObject obj) {
		String uri = obj.getId();
		if (obj instanceof DimeBinary){
			try {
				((DimeBinary) obj).delete();
			} catch (IOException e) {
				logger.error("Could not delete file "+obj.getId(), e);
			}
		}
		db.delete(obj);
		db.commit();
		return !fileExists(uri);
		
	}

}
