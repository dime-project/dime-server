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

package eu.dime.ps.storage.entities;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.PersistenceContext;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;
import javax.persistence.Version;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;
import org.springframework.transaction.annotation.Transactional;

@Entity
@Configurable
@RooJavaBean
@RooToString
@RooEntity
public class HistoryCache {

	private String entity;

	private String cacheScope;

	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(style = "S-")
	private Date cacheTimestamp;

	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(style = "S-")
	private Date cacheExpire;

	@Column(columnDefinition = "mediumtext")
	private String ctxelstr;

	private String ctxelobjId;

	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	private Tenant tenant;

	@Transactional
	public List<HistoryCache> performQueryReturningItemList(String sql) {
		return this.entityManager().createNativeQuery(sql, HistoryCache.class).getResultList();
	}

	@Transactional
	public int performQueryNotReturningItem(String sql) {
		return this.entityManager().createNativeQuery(sql).executeUpdate();
	}

	public String getEntity() {
		return this.entity;
	}

	public void setEntity(String entity) {
		this.entity = entity;
	}

	public String getCacheScope() {
		return this.cacheScope;
	}

	public void setCacheScope(String cacheScope) {
		this.cacheScope = cacheScope;
	}

	public Date getCacheTimestamp() {
		return this.cacheTimestamp;
	}

	public void setCacheTimestamp(Date cacheTimestamp) {
		this.cacheTimestamp = cacheTimestamp;
	}

	public Date getCacheExpire() {
		return this.cacheExpire;
	}

	public void setCacheExpire(Date cacheExpire) {
		this.cacheExpire = cacheExpire;
	}

	public String getCtxelstr() {
		return this.ctxelstr;
	}

	public void setCtxelstr(String ctxelstr) {
		this.ctxelstr = ctxelstr;
	}

	public String getCtxelobjId() {
		return this.ctxelobjId;
	}

	public void setCtxelobjId(String ctxelobjId) {
		this.ctxelobjId = ctxelobjId;
	}

	public Tenant getTenant() {
		return this.tenant;
	}

	public void setTenant(Tenant tenant) {
		this.tenant = tenant;
	}

	@PersistenceContext
	transient EntityManager entityManager;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private Long id;

	@Version
	@Column(name = "version")
	private Integer version;

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@Transactional
	public void persist() {
		if (this.entityManager == null) this.entityManager = entityManager();
		this.entityManager.persist(this);
	}

	@Transactional
	public void remove() {
		if (this.entityManager == null) this.entityManager = entityManager();
		if (this.entityManager.contains(this)) {
			this.entityManager.remove(this);
		} else {
			HistoryCache attached = HistoryCache.findHistoryCache(this.id);
			this.entityManager.remove(attached);
		}
	}

	@Transactional
	public void flush() {
		if (this.entityManager == null) this.entityManager = entityManager();
		this.entityManager.flush();
	}

	@Transactional
	public void clear() {
		if (this.entityManager == null) this.entityManager = entityManager();
		this.entityManager.clear();
	}

	@Transactional
	public HistoryCache merge() {
		if (this.entityManager == null) this.entityManager = entityManager();
		HistoryCache merged = this.entityManager.merge(this);
		this.entityManager.flush();
		return merged;
	}

	public static final EntityManager entityManager() {
		EntityManager em = new HistoryCache().entityManager;
		if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
		return em;
	}

	public static long countHistoryCaches() {
		return entityManager().createQuery("SELECT COUNT(o) FROM HistoryCache o", Long.class).getSingleResult();
	}

	public static List<HistoryCache> findAllHistoryCaches() {
		return entityManager().createQuery("SELECT o FROM HistoryCache o", HistoryCache.class).getResultList();
	}

	public static HistoryCache findHistoryCache(Long id) {
		if (id == null) return null;
		return entityManager().find(HistoryCache.class, id);
	}

	public static List<HistoryCache> findHistoryCacheEntries(int firstResult, int maxResults) {
		return entityManager().createQuery("SELECT o FROM HistoryCache o", HistoryCache.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
	}

	public static List<HistoryCache> findAllByTenant(Tenant tenant) {
		if (tenant == null) throw new IllegalArgumentException("The 'tenant' argument is required");
		EntityManager em = HistoryCache.entityManager();
		TypedQuery<HistoryCache> q = em.createQuery("SELECT o FROM HistoryCache AS o WHERE o.tenant = :tenant", HistoryCache.class);
		q.setParameter("tenant", tenant);
		return q.getResultList();
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("CacheExpire: ").append(getCacheExpire()).append(", ");
		sb.append("CacheScope: ").append(getCacheScope()).append(", ");
		sb.append("CacheTimestamp: ").append(getCacheTimestamp()).append(", ");
		sb.append("CtxelobjId: ").append(getCtxelobjId()).append(", ");
		sb.append("Ctxelstr: ").append(getCtxelstr()).append(", ");
		sb.append("Entity: ").append(getEntity()).append(", ");
		sb.append("Id: ").append(getId()).append(", ");
		sb.append("Tenant: ").append(getTenant()).append(", ");
		sb.append("Version: ").append(getVersion());
		return sb.toString();
	}
}
