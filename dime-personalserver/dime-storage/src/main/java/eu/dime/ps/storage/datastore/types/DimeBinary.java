package eu.dime.ps.storage.datastore.types;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.db4o.ext.Status;
import com.db4o.internal.BlobImpl;
import com.db4o.types.Blob;

import eu.dime.ps.storage.util.CMSInitHelper;

/**
 * wrapper for blobs
 * @author marcel
 *
 */
public class DimeBinary extends PersistentDimeObject{

	private final String fileId = UUID.randomUUID().toString();
	
	Logger logger = Logger.getLogger(DimeBinary.class);
	
	private boolean isStored = false;
	
	private Blob blob;
	private String path;
	private String tmpOut;
	private String tmpIn;
	
	private String type; //image, document etc (mime?)
	private String hash;
	private long tenantId;
	
	private Date lastUpdate;
	
	public DimeBinary(){	
	}
	
	public DimeBinary(String dbPath) {
		new File(dbPath).mkdirs();
		this.path = dbPath;
		this.tmpIn = path + File.separator + "tmp-read";
		this.tmpOut = path + File.separator + "tmp-write";
		new File(tmpIn).mkdirs();
		new File(tmpOut).mkdirs();
	}
		
	public DimeBinary(String blobPath, File file) throws IOException {
		this.path = blobPath;
		readFile(file);
	}

	public boolean updateFile(InputStream is){
		try {
			streamToFile(is, getFile());
			readFile(getFile());
		} catch (IOException e) {
			logger.warn("error when updating file.", e);
			return false;
		}
		return true;
	}
	
	public String getRdfUri() {
		return id;
	}
	
	public Date getLastUpdate(){
		return lastUpdate;
	}

	public void setRdfUri(String rdfUri) {
		this.id = rdfUri;
	}

	public long getTenantId() {
		return tenantId;
	}

	public void setTenantId(long tenantId) {
		this.tenantId = tenantId;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void delete(){
		try {
			blob.deleteFile();
		} catch (IOException e) {
			logger.error("Could not delete blob with id: "+fileId, e);
		}
	}
	
	public String getFileName() {
		return fileId;
	}
	
	public boolean isStored() {
		return isStored;
	}

	public File getFile() throws IOException{
		writeFile();
		return new File(tmpOut + File.separator + fileId);
	}
		
	private void streamToFile(InputStream inputStream,
			java.io.File file) throws IOException {
		OutputStream outputStream = new FileOutputStream(file);
		IOUtils.copy(inputStream, outputStream);
		outputStream.close();
	}

	public boolean readFile(File file) throws java.io.IOException  {
	    blob.readFrom(file);
	    double status = blob.getStatus();
	    while(status >  Status.COMPLETED) {
	      try  {
	        Thread.sleep(20);
	        status = blob.getStatus();
	      } catch (InterruptedException e) {
	        	logger.warn(e);
	      }
	    }
	    boolean successful = (status == Status.COMPLETED);
	    if (successful){
	    	this.lastUpdate = new Date();
	    	isStored = true;
	    }
	    return successful;    
  }
		  
	public boolean writeFile() throws java.io.IOException  {
		blob.writeTo(new File(tmpOut + File.separator + fileId));
		double status = blob.getStatus();
	    while(status > Status.COMPLETED) {
	    	try  {
	    		Thread.sleep(20);
	    		status = blob.getStatus();
	    	} catch (InterruptedException e) {
	        	logger.warn(e);
	        }
	    }
	    return (status == Status.COMPLETED);
	}
	
	@Override
	public void onDelete() {
		new File(tmpOut + File.separator + fileId).delete();
		//new File(path + File.separator + CMSInitHelper.getBlobFolder() + File.separator + fileId).delete();
	}

	public void addBlob(File file) throws IOException {
		readFile(file);
		
	}
	
	public void addBlob(InputStream is) throws IOException{
		File file = new File(tmpIn + File.separator + fileId);
		
		streamToFile(is, file);
		readFile(file);
		file.delete();
	}
	
	
	
	
}

