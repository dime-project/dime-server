package eu.dime.ps.semantic.connection;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryException;

import eu.dime.ps.semantic.rdf.RepositoryFactory;

/**
 * A connection provider for accessing RDF repositories and wrappers over them.
 * This provider also implements a very rudimentary connection pool.
 * 
 * @author Ismael Rivera
 */
public class ConnectionProvider {

	private final RepositoryFactory repositoryFactory;
	private final ConcurrentMap<String, Connection> connectionPool;
	
	public ConnectionProvider(RepositoryFactory repositoryFactory) {
		this.repositoryFactory = repositoryFactory;
		this.connectionPool = new ConcurrentHashMap<String, Connection>();
	}
	
	public Connection newConnection(String name, String username) throws RepositoryException {
		if (connectionPool.containsKey(name)) {
			throw new RepositoryException("There is another connection in the pool with the same name '"+name+"'");
		}
		
		final Repository repository = repositoryFactory.get(name);
		Connection conn = new Connection(name, repository);
		conn.initialize(username);
		connectionPool.putIfAbsent(name, conn);
		
		return conn;
	}
	
	/**
	 * Grab a connection from the pool, or register a new 
	 * @return a RDF repository connection
	 * @throws RepositoryException if a RDF repository access error occurs
	 */
	public Connection getConnection(String name) throws RepositoryException {
		Connection conn = connectionPool.get(name);
		
		// the connection doesn't exist, it's created on-the-fly
		if (conn == null) {
			final Repository repository = repositoryFactory.get(name);
			conn = new Connection(name, repository);
			connectionPool.putIfAbsent(name, conn);
		}
		
		return conn;
	}
	 
	/**
	 * Dispose of a used connection.
	 * @throws RepositoryException if a RDF repository access error occurs
	 */
	public void closeConnection(String name) throws RepositoryException {
		connectionPool.get(name).close();
		connectionPool.remove(name);
	}
	
	/**
	 * Removes permanently the {@link Repository} and all the data/metadata contained
	 * in it. It is also removed from the connection pool, so it won't be longer available.
	 * @param name name of the RDF repository
	 */
	public void remove(String name) throws RepositoryException {
		connectionPool.remove(name);
		repositoryFactory.remove(name);
	}

}
