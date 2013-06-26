package eu.dime.ps.controllers;

import org.openrdf.repository.RepositoryException;

import eu.dime.ps.semantic.connection.Connection;
import eu.dime.ps.semantic.connection.ConnectionProvider;

public class SingleConnectionProviderMock extends ConnectionProvider {

	private Connection connection;
	
	public SingleConnectionProviderMock() {
		super(null);
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}
	
	@Override
	public Connection getConnection(String name) throws RepositoryException {
		return connection;
	}
	
	@Override
	public void closeConnection(String name) throws RepositoryException {
		// do nothing
	}

}
