package eu.dime.ps.storage;

import java.io.ByteArrayInputStream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Connection;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import eu.dime.context.exceptions.JsonConversionException;
import eu.dime.context.model.Constants;
import eu.dime.context.model.api.IContextElement;
import eu.dime.context.model.api.IEntity;
import eu.dime.context.model.api.IScope;
import eu.dime.context.model.impl.Factory;
import eu.dime.ps.contextprocessor.utils.ContextUtility;
import eu.dime.ps.storage.datastore.DataStore;
import eu.dime.ps.storage.datastore.impl.DataStoreProvider;
import eu.dime.ps.storage.entities.HistoryCache;
import eu.dime.ps.storage.entities.Tenant;
import eu.dime.ps.storage.manager.EntityFactory;

public class DimeStorageImpl implements IStorage {
	
	Logger logger = Logger.getLogger(DimeStorageImpl.class);
	private static EntityFactory ef;
	private static DataStore ds;

	@Autowired
	DataStoreProvider dataStoreProvider;
	
	@Autowired
	public void setEntityFactory(EntityFactory entityFactory) {
		this.ef = entityFactory;
	}
	
	private DataStore getDataStore(Long tenantId){
		System.out.println("========================================= getDataStore from contextprocessor");
		return dataStoreProvider.getTenantStore(tenantId);
	}
	
	public void storeContextElement(Tenant t, IContextElement ctxEl) throws StorageException {

		try {
			//EntityFactory ef = new EntityFactory();
			//CacheHistory ch = ef.getCacheHistory();
			HistoryCache ch = ef.buildHistoryCache();
			
			// Invalidation of last stored valid context element, forcing its expire time to the timestamp value of the new one
			//long newExpire = Factory.timestampFromXMLString(ctxEl.getMetadata().getMetadatumValue(Factory.createScope(Constants.SCOPE_METADATA_TIMESTAMP)).getValue().toString());
			String newExpire = ctxEl.getMetadata().getMetadatumValue(Factory.createScope(Constants.SCOPE_METADATA_TIMESTAMP)).getValue().toString();
			newExpire = ContextUtility.convertCtxtDT2CacheDT(newExpire);
			
			String sql = "SELECT * FROM HistoryCache " + 
					"WHERE entity='" + ctxEl.getEntity().getEntityAsString() + "' AND cacheScope='" + ctxEl.getScope().getScopeAsString() +
					"' AND cacheExpire>'" + newExpire + "' ORDER BY cacheTimestamp DESC LIMIT 1;";
			
			//logger.debug("Executing query: " + sql);
			
			//List<CacheHistory> chList = ch.performQueryReturningItemList(sql);
			List<HistoryCache> chList = ch.performQueryReturningItemList(sql);
			long id = -1; 
			//Iterator<CacheHistory> it = chList.iterator();
			Iterator<HistoryCache> it = chList.iterator();
			while (it.hasNext()){
				id = it.next().getId();
				break;
			}

			if (id!=-1){
				sql = "UPDATE HistoryCache SET cacheExpire='" + newExpire + "' WHERE ID=" + id + ";";
				//logger.debug("Executing query: " + sql);
				ch.performQueryNotReturningItem(sql);
			}
			
			// Insertion of the new context element
			ch.setId(null);
			ch.setEntity(ctxEl.getEntity().getEntityAsString());
		    ch.setCacheScope(ctxEl.getScope().getScopeAsString());
		    ch.setCacheTimestamp(new Date(Factory.timestampFromXMLString(ctxEl.getMetadata().getMetadatumValue(Factory.createScope(Constants.SCOPE_METADATA_TIMESTAMP)).getValue().toString())));
		    ch.setCacheExpire(new Date(Factory.timestampFromXMLString(ctxEl.getMetadata().getMetadatumValue(Factory.createScope(Constants.SCOPE_METADATA_EXPIRES)).getValue().toString())));
		    ch.setTenant(t);
		    String blobId = ctxEl.getScope().getScopeAsString() + ":" + Long.toString(System.currentTimeMillis());
		    ch.setCtxelobjId(blobId);
		    ds = getDataStore(t.getId());
		    ds.storeBlob(blobId, new ByteArrayInputStream(ctxEl2byteArr(ctxEl)),t.getId().toString());
		    //ch.setCtxelobj(ctxEl2byteArr(ctxEl));
		    ch.setCtxelstr(ContextUtility.contextElement2JSON(ctxEl));
			ch.persist();
			
		} catch (Exception e) {
			//e.printStackTrace();
			throw new StorageException(e.getMessage(),e);
		} 

	}
	
	public IContextElement[] getContextElements(Tenant t, IEntity entity, IScope scope) throws StorageException {
		return getContextElements(t,entity, scope, -1, -1, false, -1);
	}

	public IContextElement[] getContextElements(Tenant t, IEntity entity, IScope scope,
			long fromTimestamp, long toTimestamp) throws StorageException {
		return getContextElements(t,entity, scope, fromTimestamp, toTimestamp, false, -1);
	}

	public IContextElement[] getCurrentContextElements(Tenant t, IEntity entity, IScope scope) throws StorageException {
		return getContextElements(t,entity, scope, -1, -1, true, -1);
	}
	
	public IContextElement [] getContextElements(
			Tenant t, 
			IEntity entity,
			IScope scope,
			int limit) throws StorageException{
		return getContextElements(t,entity, scope, -1, -1, false,limit);
	}
	
    public void deleteContextElements(
	    final IEntity entity,
	    final IScope scope) throws StorageException{

		try {
			
			//CacheHistory ch = ef.getCacheHistory();
			HistoryCache ch = ef.buildHistoryCache();
			// Since the database refers to a single user, if entityType is Constant.ENTITY_USER, every context
			// element is retrieved. If entity type is Constants.ENTITY_DEVICE, only context elements related to
			// the required entity are retrieved
			String entityStr = "";
			if (entity.getEntityTypeAsString().equals(Constants.ENTITY_USER))
				entityStr = "";
			else if (entity.getEntityTypeAsString().equals(Constants.ENTITY_DEVICE))
				entityStr = "ENTITY='" + entity.getEntityAsString() + "' AND ";
			
			String sql = "DELETE FROM HistoryCache " +
			"WHERE " + entityStr + "SCOPE='" + scope.getScopeAsString() + "';";
			//CacheHistory.entityManager().createNativeQuery(sql).executeUpdate();
			//logger.debug("Executing query: " + sql);
			ch.performQueryNotReturningItem(sql);
		} catch (Exception e) {
			//e.printStackTrace();
			throw new StorageException(e.getMessage(),e);
		} finally {
			/*try {
				if(con != null)
					con.close();
			} catch(SQLException e) {}*/
		}
    	return;    
    }
	
	private IContextElement[] getContextElements(Tenant t, IEntity entity, IScope scope,
			long since, long until, boolean current, int limit) throws StorageException {

		Connection con = null;
		Vector<IContextElement> ctxElVect = new Vector<IContextElement>();
		try {
//			EntityFactory ef = new EntityFactory();
			HistoryCache ch = ef.buildHistoryCache();

			// Since the database refers to a single user, if entityType is Constant.ENTITY_USER, every context
			// element is retrieved. If entity type is Constants.ENTITY_DEVICE, only context elements related to
			// the required entity are retrieved
			/*String entityStr = "";
			if (entity.getEntityTypeAsString().equals(Constants.ENTITY_USER))
				entityStr = "";
			else if (entity.getEntityTypeAsString().equals(Constants.ENTITY_DEVICE))
				entityStr = "entity='" + entity.getEntityAsString() + "' AND ";*/
			String entityStr = "entity='" + entity.getEntityAsString() + "' AND ";
			
			String timeLimitStr = "";
			
			if (current){
				// The request involves only the context not expired
				//long now = System.currentTimeMillis();
				String now = Factory.cacheDateFormat.format(System.currentTimeMillis());
				timeLimitStr = " AND cacheTimestamp<='" + now + "' AND cacheExpire>='" + now + "'";
			}else{
				// The request involves historical context data
				/*if (since!=-1)
					timeLimitStr = " AND cacheExpire>" + since;
				if (until!=-1)
					timeLimitStr += " AND cacheTimestamp<" + until;*/
				if (since!=-1)
					timeLimitStr = " AND cacheExpire>='" + Factory.cacheDateFormat.format(since) + "'";
				if (until!=-1)
					timeLimitStr += " AND cacheTimestamp<='" + Factory.cacheDateFormat.format(until) + "'";
			}
			
			String optLimitStr = "";
			if (limit != -1)
				optLimitStr = " LIMIT " + limit;
				
			String sql = "SELECT * FROM HistoryCache " + 
				//"WHERE ENTITY='" + entity.getEntityAsString() + "' AND SCOPE='" + scope.getScopeAsString() + "' " +
				"WHERE " + entityStr + "cacheScope='" + scope.getScopeAsString() + "' " +
				timeLimitStr + " ORDER BY cacheTimestamp DESC" + optLimitStr + ";";
			
			//logger.debug("Executing query: " + sql);
			
			//List<CacheHistory> chList = CacheHistory.entityManager().createNativeQuery(sql,CacheHistory.class).getResultList();
			List<HistoryCache> chList = ch.performQueryReturningItemList(sql);
			
			Iterator<HistoryCache> it = chList.iterator();
			while (it.hasNext()){
				try {
					String blobId = it.next().getCtxelobjId();
					ds = getDataStore(t.getId());
					InputStream is = ds.getBlob(blobId);
			 		ObjectInputStream objectIn = null;
					if (is != null){
						objectIn = new ObjectInputStream(is);
						ctxElVect.add((IContextElement)objectIn.readObject());
					}
				} catch (Exception e) {
					
					e.printStackTrace();
				} 
			}
		} catch (Exception e) {
			//e.printStackTrace();
			throw new StorageException(e.getMessage(),e);
		} finally {
			
		}

		return ctxElVect.toArray(new IContextElement[0]);

	}

	private byte[] ctxEl2byteArr(IContextElement ctxEl) throws IOException{
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream out = new ObjectOutputStream(bos);
		out.writeObject(ctxEl);
		byte[] byteArr = bos.toByteArray();  
		out.close(); 
		bos.close();
		return byteArr;
	}
	
	private IContextElement byteArr2ctxEl(byte[] byteArr) throws IOException{
		ByteArrayInputStream bis = new ByteArrayInputStream(byteArr);
		ObjectInputStream in = new ObjectInputStream(bis);
		Object o = null;
		try {
			o = in.readObject();
		} catch (ClassNotFoundException e) {}  
		bis.close();
		in.close();
		return (IContextElement)o;
	}
}
