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

package eu.dime.ps.storage;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.dime.context.exceptions.JsonConversionException;
import eu.dime.context.model.Constants;
//import eu.dime.context.model.JSONContextTransformer;
import eu.dime.context.model.api.IContextElement;
import eu.dime.context.model.api.IEntity;
import eu.dime.context.model.api.IScope;
import eu.dime.context.model.impl.Factory;
import eu.dime.ps.storage.entities.Tenant;

public class JDBCStorageImpl implements IStorage{
	
	private static final Logger logger = LoggerFactory.getLogger(JDBCStorageImpl.class);
	
    private String connStr = "jdbc:mysql://127.0.0.1:3306/dime";
    private String dbUser = "testuser"; 
    private String dbPwd = "testpwd";
    
    static{
    	try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
		} catch (Exception e) {
			logger.error(e.toString(),e);
		}
    }
    
    public void setDBCredentials(String connStr, String user, String pwd){
    	this.connStr = connStr;
    	dbUser = user;
    	dbPwd = pwd;
    }
   
	public void storeContextElement(Tenant t, IContextElement ctxEl) throws StorageException {

		Connection con = null;
		try {
			con = DriverManager.getConnection(connStr,
			        dbUser, dbPwd);

			con.setAutoCommit(false);
			
			// Invalidation of last stored valid context element, forcing its expire time to the timestamp value of the new one
			Statement st=null;
			ResultSet rs=null;
			st = con.createStatement();
			long newExpire = Factory.timestampFromXMLString(ctxEl.getMetadata().getMetadatumValue(Factory.createScope(Constants.SCOPE_METADATA_TIMESTAMP)).getValue().toString()); 

			String sql = "SELECT ID FROM cache_history " + 
					"WHERE ENTITY='" + ctxEl.getEntity().getEntityAsString() + "' AND SCOPE='" + ctxEl.getScope().getScopeAsString() +
					"' AND EXPIRE>" + newExpire + " ORDER BY TIMSTAMP DESC LIMIT 1;";
			rs = st.executeQuery(sql);
			int id = -1; 
			while (rs.next()) {
				id = rs.getInt(1);
				break;
			}

			if (id!=-1){
				sql = "UPDATE cache_history SET EXPIRE=" + newExpire + " WHERE ID=" + id + ";";
				PreparedStatement ps = null;
				ps = con.prepareStatement(sql);
			    ps.executeUpdate();
			}
			
			// Insertion of the new context element
			String insertSql = "INSERT INTO cache_history(ENTITY,SCOPE,TIMSTAMP,EXPIRE,CTXELOBJ,CTXELSTR) " +
					 " VALUES(?,?,?,?,?,?);";
			
			PreparedStatement ps = null;
			ps = con.prepareStatement(insertSql);
		    ps.setString(1, ctxEl.getEntity().getEntityAsString());
		    ps.setString(2, ctxEl.getScope().getScopeAsString());
		    ps.setLong(3, Factory.timestampFromXMLString(ctxEl.getMetadata().getMetadatumValue(Factory.createScope(Constants.SCOPE_METADATA_TIMESTAMP)).getValue().toString()));
		    ps.setLong(4, Factory.timestampFromXMLString(ctxEl.getMetadata().getMetadatumValue(Factory.createScope(Constants.SCOPE_METADATA_EXPIRES)).getValue().toString()));
		    ps.setObject(5,ctxEl);
////		    ps.setString(6, JSONContextTransformer.contextElement2json(ctxEl));
			// Java serialization to stream
			/*ObjectOutputStream oos;
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			try {
				oos = new ObjectOutputStream(baos);
				oos.writeObject(ctxEl);
				oos.close();
			} catch (IOException e) {}
			ps.setBlob(5, new ByteArrayInputStream(baos.toByteArray()));*/
		    ps.executeUpdate();
		    con.commit();
		    
		} catch (SQLException e) {
			//e.printStackTrace();
			throw new StorageException(e.getMessage());
/*		} catch (JsonConversionException ex){
			throw new StorageException(ex.getMessage());			
*/		} finally {
			try {
				if(con != null)
					con.close();
			} catch(SQLException e) {}
		}

	}
	
	public IContextElement[] getContextElements(Tenant t, IEntity entity, IScope scope) throws StorageException {
		return getContextElements(entity, scope, -1, -1, false, -1);
	}

	public IContextElement[] getContextElements(Tenant t, IEntity entity, IScope scope,
			long fromTimestamp, long toTimestamp) throws StorageException {
		return getContextElements(entity, scope, fromTimestamp, toTimestamp, false, -1);
	}

	public IContextElement[] getCurrentContextElements(Tenant t, IEntity entity, IScope scope) throws StorageException {
		return getContextElements(entity, scope, -1, -1, true, -1);
	}
	
	public IContextElement [] getContextElements(
			Tenant t, 
			IEntity entity,
			IScope scope,
			int limit) throws StorageException{
		return getContextElements(entity, scope, -1, -1, false,limit);
	}
	
    public void deleteContextElements(
	    final IEntity entity,
	    final IScope scope) throws StorageException{

    	Connection con = null;
		try {
			con = DriverManager.getConnection(connStr,
			        dbUser, dbPwd);
			Statement st = con.createStatement();
			// Since the database refers to a single user, if entityType is Constant.ENTITY_USER, every context
			// element is retrieved. If entity type is Constants.ENTITY_DEVICE, only context elements related to
			// the required entity are retrieved
			String entityStr = "";
			if (entity.getEntityTypeAsString().equals(Constants.ENTITY_USER))
				entityStr = "";
			else if (entity.getEntityTypeAsString().equals(Constants.ENTITY_DEVICE))
				entityStr = "ENTITY='" + entity.getEntityAsString() + "' AND ";
			
			String sql = "DELETE FROM cache_history " + 
			"WHERE " + entityStr + "SCOPE='" + scope.getScopeAsString() + "';";
			st.executeUpdate(sql);
		} catch (SQLException e) {
			//e.printStackTrace();
			throw new StorageException(e.getMessage());
		} finally {
			try {
				if(con != null)
					con.close();
			} catch(SQLException e) {
				logger.error(e.toString(),e);
			}
		}
    	return;    
    }
    
	private IContextElement[] getContextElements(IEntity entity, IScope scope,
			long since, long until, boolean current, int limit) throws StorageException {

		Connection con = null;
		Vector<IContextElement> ctxElVect = new Vector<IContextElement>();
		try {
			con = DriverManager.getConnection(connStr,
			        dbUser, dbPwd);
			
			Statement st=null;
			ResultSet rs=null;
			st = con.createStatement();

			// Since the database refers to a single user, if entityType is Constant.ENTITY_USER, every context
			// element is retrieved. If entity type is Constants.ENTITY_DEVICE, only context elements related to
			// the required entity are retrieved
			String entityStr = "";
			if (entity.getEntityTypeAsString().equals(Constants.ENTITY_USER))
				entityStr = "";
			else if (entity.getEntityTypeAsString().equals(Constants.ENTITY_DEVICE))
				entityStr = "ENTITY='" + entity.getEntityAsString() + "' AND ";
			
			String timeLimitStr = "";
			
			if (current){
				// The request involves only the context not expired
				long now = System.currentTimeMillis();
				timeLimitStr = " AND TIMSTAMP<" + now + " AND EXPIRE>" + now;
			}else{
				// The request involves historical context data
				if (since!=-1)
					timeLimitStr = " AND EXPIRE>" + since;
				if (until!=-1)
					timeLimitStr += " AND TIMSTAMP<" + until;
			}
			
			String optLimitStr = "";
			if (limit != -1)
				optLimitStr = " LIMIT " + limit;
				
			String sql = "SELECT ENTITY,SCOPE,TIMSTAMP,EXPIRE,CTXELOBJ FROM cache_history " + 
				//"WHERE ENTITY='" + entity.getEntityAsString() + "' AND SCOPE='" + scope.getScopeAsString() + "' " +
				"WHERE " + entityStr + "SCOPE='" + scope.getScopeAsString() + "' " +
				timeLimitStr + " ORDER BY TIMSTAMP DESC" + optLimitStr + ";";
			rs = st.executeQuery(sql);
		 	while (rs.next()) {
		 		try {
			 		byte[] buf = rs.getBytes(5);
			 		ObjectInputStream objectIn = null;
					if (buf != null){
						objectIn = new ObjectInputStream(new ByteArrayInputStream(buf));
						ctxElVect.add((IContextElement)objectIn.readObject());
					}
				} catch (Exception e) {
					logger.error(e.toString(),e);
				} 
			}
		} catch (SQLException e) {
			//e.printStackTrace();
			throw new StorageException(e.getMessage());
		} finally {
			try {
				if(con != null)
					con.close();
			} catch(SQLException e) {
				logger.error(e.toString(),e);
			}
		}

		return ctxElVect.toArray(new IContextElement[0]);

	}

}
