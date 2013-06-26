package eu.dime.ps.semantic.rdf.impl;

import java.io.File;

import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.nativerdf.NativeStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.dime.ps.semantic.rdf.RepositoryFactory;
import eu.dime.ps.semantic.rdf.inferencer.SimpleInferencingSail;

/**
 * Factory which creates Sesame native (file-based) repositories.
 * 
 * @author Ismael Rivera
 */
public class SesameNativeRepositoryFactory implements RepositoryFactory {
    
    private static final Logger logger = LoggerFactory.getLogger(SesameNativeRepositoryFactory.class);

    private final String baseDir;
    
    public SesameNativeRepositoryFactory() {
    	this("rdf");
    }

    public SesameNativeRepositoryFactory(String repositoryDir) {
    	String appDataDir = System.getProperty("dime.appdata.basedir");
    	String homeDir = System.getProperty("user.home");
    	
    	if (appDataDir != null) {
    		this.baseDir = appDataDir + repositoryDir + File.separator;
    	} else if (homeDir != null) {
    		this.baseDir = homeDir + File.separator + ".dime" + File.separator + repositoryDir + File.separator;
    	} else {
    		throw new IllegalArgumentException("di.me needs a directory to store user's data. " +
    				"Please specify a directory in the 'dime.appdata.basedir' property (e.g. /path/to/dir/), " +
    				"or at least 'user.home' must be set to be used as default (e.g. $HOME/.dime/), used as " +
    				"default if 'dime.appdata.basedir' is not specified.");
    	}
    }

    @Override
    public Repository get(String name) throws RepositoryException {
		return getSesameNativeRepository(baseDir + name, "spoc,posc", true);
    }
    
    @Override
    public boolean remove(String name) throws RepositoryException {
    	boolean deleted = false;
		File path = new File(baseDir + name);
		if (path.exists()) {
			try {
				deleted = path.delete();
				if (deleted) {
					logger.info("Removed RDF repository '"+name+"' ["+path.getAbsolutePath()+"]");
				}
			} catch (SecurityException e) {
				throw new RepositoryException("Repository '"+name+"' couldn't be deleted.", e);
			}
		}
		return deleted;
    }

//    protected Repository getSesameNativeRepository(String dataDir) throws RepositoryException {
//    	return getSesameNativeRepository(dataDir);
//    }

//    protected Repository getSesameNativeRepository(String dataDir, boolean inferencing)
//	    throws RepositoryException {
//	
//		// dataDir relative path in user home 
//		File path = new File(System.getProperty("user.home") + File.separator + dataDir);
//		logger.info("RDF repository at " +path.getAbsolutePath()+" [inferencing="+inferencing+"]");
//
//		Repository repository = null;
//		if (inferencing) {
//		    repository = new SailRepository(new ForwardChainingRDFSInferencer(new NativeStore(path)));
//		} else {
//		    repository = new SailRepository(new NativeStore(path));
//		}
//
//		repository.initialize();
//		
//		return repository;
//    }
//
//    protected Repository getSesameNativeRepository(String dataDir, String indexes)
//    	    throws RepositoryException {
//    	return getSesameNativeRepository(dataDir, indexes, false);
//    }
    
    protected Repository getSesameNativeRepository(String dataDir, String indexes, boolean inferencing)
	    throws RepositoryException {
	
		File path = new File(dataDir);
		logger.info("RDF repository at " +path.getAbsolutePath()+" [indexes="+indexes+", inferencing="+inferencing+"]");
		
		Repository repository = null;
		if (inferencing) {
			// ForwardChainingRDFSInferencer is buggy: sometimes gets stuck in an almost infinite loop, and it's
			// also not thread-safe; we'll use a crappy inferencer which just gets the job done for what we need
//		    repository = new SailRepository(new ForwardChainingRDFSInferencer(new NativeStore(path, indexes)));
		    repository = new SailRepository(new SimpleInferencingSail(new NativeStore(path, indexes)));
		} else {
		    repository = new SailRepository(new NativeStore(path, indexes));
		}

		repository.initialize();
		
		return repository;
    }
    
}
