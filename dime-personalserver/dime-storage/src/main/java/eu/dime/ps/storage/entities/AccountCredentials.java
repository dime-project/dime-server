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
import javax.persistence.Table;
import javax.persistence.TypedQuery;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.transaction.annotation.Transactional;

import eu.dime.ps.storage.util.QueryUtil;

/**
 * These are the credentials used for account-account communication: data sharing between
 * di.me nodes. Each user's account needs different credentials to access any other user's account.
 * 
 * @author Ismael Rivera
 */
@Configurable
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames={"tenant", "source","target"}))
public class AccountCredentials {

	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private Long id;

	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	private Tenant tenant;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    private ServiceAccount source;

    @NotNull
    private String target;

    @NotNull
	private String targetUri;
	
    @NotNull
    private String secret;

	@PersistenceContext
	transient EntityManager entityManager;

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public Tenant getTenant() {
		return this.tenant;
	}

	public void setTenant(Tenant tenant) {
		this.tenant = tenant;
	}

	public ServiceAccount getSource() {
		return source;
	}

	public void setSource(ServiceAccount source) {
		this.source = source;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	public String getTargetUri() {
		return targetUri;
	}

	public void setTargetUri(String targetUri) {
		this.targetUri = targetUri;
	}

	@Transactional
	public void persist() {
		if (this.entityManager == null)
			this.entityManager = entityManager();
		this.entityManager.persist(this);
	}

	@Transactional
	public void remove() {
		if (this.entityManager == null)
			this.entityManager = entityManager();
		if (this.entityManager.contains(this)) {
			this.entityManager.remove(this);
		} else {
			AccountCredentials attached = AccountCredentials.find(this.id);
			this.entityManager.remove(attached);
		}
	}

	@Transactional
	public void flush() {
		if (this.entityManager == null)
			this.entityManager = entityManager();
		this.entityManager.flush();
	}

	@Transactional
	public void clear() {
		if (this.entityManager == null)
			this.entityManager = entityManager();
		this.entityManager.clear();
	}

	@Transactional
	public AccountCredentials merge() {
		if (this.entityManager == null)
			this.entityManager = entityManager();
		AccountCredentials merged = this.entityManager.merge(this);
		this.entityManager.flush();
		return merged;
	}

	public static final EntityManager entityManager() {
		EntityManager em = new AccountCredentials().entityManager;
		if (em == null)
			throw new IllegalStateException(
					"Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
		return em;
	}

	public static long count() {
		return entityManager().createQuery("SELECT COUNT(o) FROM AccountCredentials o", Long.class)
				.getSingleResult();
	}

	public static List<AccountCredentials> findAll() {
		return entityManager().createQuery("SELECT o FROM AccountCredentials o", AccountCredentials.class)
				.getResultList();
	}

	public static AccountCredentials find(Long id) {
		if (id == null)
			return null;
		return entityManager().find(AccountCredentials.class, id);
	}

	public static List<AccountCredentials> find(int firstResult, int maxResults) {
		return entityManager().createQuery("SELECT o FROM AccountCredentials o", AccountCredentials.class)
				.setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Tenant: ").append(getTenant()).append(", ");
		sb.append("Id: ").append(getId()).append(", ");
        sb.append("Source: ").append(getSource()).append(", ");
		sb.append("Target: ").append(getTarget()).append(", ");
		sb.append("TargetUri: ").append(getTargetUri()).append(", ");
		return sb.toString();
	}

	public static AccountCredentials findAllByTenantAndBySourceAndByTarget(Tenant tenant, ServiceAccount source, String target) {
		if (tenant == null
				|| source == null
				|| target == null || target.length() == 0)
			throw new IllegalArgumentException("The arguments tenant, source and target are required");
		
		EntityManager em = AccountCredentials.entityManager();
		TypedQuery<AccountCredentials> q = em.createQuery(
				"SELECT o FROM AccountCredentials AS o WHERE o.tenant = :tenant AND o.source = :source AND o.target = :target",
				AccountCredentials.class);
		q.setParameter("tenant", tenant);
		q.setParameter("source", source);
		q.setParameter("target", target);
		return QueryUtil.getSingleResultOrNull(q);
	}
	
	public static AccountCredentials findAllByTenantAndBySourceAndByTargetUri(Tenant tenant, ServiceAccount source, String targetUri) {
		if (tenant == null
				|| source == null
				|| targetUri == null || targetUri.length() == 0)
			throw new IllegalArgumentException("The arguments tenant, source and targetUri are required");
		
		EntityManager em = AccountCredentials.entityManager();
		TypedQuery<AccountCredentials> q = em.createQuery(
				"SELECT o FROM AccountCredentials AS o WHERE o.tenant = :tenant AND o.source = :source AND o.targetUri = :targetUri",
				AccountCredentials.class);
		q.setParameter("tenant", tenant);
		q.setParameter("source", source);
		q.setParameter("targetUri", targetUri);
		return QueryUtil.getSingleResultOrNull(q);
	}

	public static List<AccountCredentials> findAllByTenant(Tenant tenant) {
		if (tenant == null)
			throw new IllegalArgumentException("The tenant argument is required");
		EntityManager em = AccountCredentials.entityManager();
		TypedQuery<AccountCredentials> q = em.createQuery(
				"SELECT o FROM AccountCredentials AS o WHERE o.tenant = :tenant", AccountCredentials.class);
		q.setParameter("tenant", tenant);
		return q.getResultList();
	}
	
	public static AccountCredentials findAllBySourceAndByTargetUri(ServiceAccount source, String targetUri) {
		EntityManager em = AccountCredentials.entityManager();
		TypedQuery<AccountCredentials> q = em.createQuery(
				"SELECT o FROM AccountCredentials AS o WHERE o.source = :source AND o.targetUri = :targetUri",
				AccountCredentials.class);
		q.setParameter("source", source);
		q.setParameter("targetUri", targetUri);
		return QueryUtil.getSingleResultOrNull(q);
	}

	public static List<AccountCredentials> findAllByTargetName(String saidName) {
		EntityManager em = AccountCredentials.entityManager();
		TypedQuery<AccountCredentials> q = em.createQuery(
				"SELECT o FROM AccountCredentials WHERE o.target = :said",
				AccountCredentials.class);
		q.setParameter("said", saidName);
		return q.getResultList();
	}

	public static AccountCredentials findAllByTenantAndByTargetUri(Tenant tenant, String targetUri) {
        if (tenant == null){
            throw new IllegalArgumentException("tenant must not be null!");
        }
        if (targetUri == null){
            throw new IllegalArgumentException("targetUri must not be null!");
        }
		EntityManager em = AccountCredentials.entityManager();
		TypedQuery<AccountCredentials> q = em.createQuery(
				"SELECT o FROM AccountCredentials AS o WHERE o.tenant = :tenant AND o.targetUri = :targetUri",
				AccountCredentials.class);
		q.setParameter("targetUri", targetUri);
        q.setParameter("tenant", tenant);
		return QueryUtil.getSingleResultOrNull(q);
	}
	
}
