/*
* Copyright 2013 by the digital.me project (http:\\www.dime-project.eu).
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

package eu.dime.ps.communications;

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
