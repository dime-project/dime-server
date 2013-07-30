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
import javax.persistence.TypedQuery;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;
import org.springframework.transaction.annotation.Transactional;

import eu.dime.ps.storage.util.QueryUtil;

@Configurable
@Entity
@RooJavaBean
@RooToString
@RooEntity(finders = { "findAllByTenant" })
public class ServiceAccount {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private Long id;

    @Column(unique = true,nullable = true)
    private String name; // can be null for external services (not requiring a 'routing name')

	@ManyToOne(fetch = FetchType.EAGER)
	private ServiceProvider serviceProvider;

	private String accessToken;

	private String accessSecret;

	@Column(unique = true)
	private String accountUri; // URI of account in RDF store

	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	private Tenant tenant;
	
    @NotNull
	private Boolean enabled = false;

	@PersistenceContext
	transient EntityManager entityManager;

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
        return this.name;
    }

	public void setName(String name) {
        this.name = name;
    }

	public Boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
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
			ServiceAccount attached = ServiceAccount.find(this.id);
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
	public ServiceAccount merge() {
		if (this.entityManager == null)
			this.entityManager = entityManager();
		ServiceAccount merged = this.entityManager.merge(this);
		this.entityManager.flush();
		return merged;
	}

	public static final EntityManager entityManager() {
		EntityManager em = new ServiceAccount().entityManager;
		if (em == null)
			throw new IllegalStateException(
					"Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
		return em;
	}

	public static long count() {
		return entityManager().createQuery("SELECT COUNT(o) FROM ServiceAccount o", Long.class)
				.getSingleResult();
	}

	public static List<ServiceAccount> findAll() {
		return entityManager().createQuery("SELECT o FROM ServiceAccount o", ServiceAccount.class)
				.getResultList();
	}

	public static ServiceAccount find(Long id) {
		if (id == null)
			return null;
		return entityManager().find(ServiceAccount.class, id);
	}

	public static List<ServiceAccount> find(int firstResult, int maxResults) {
		return entityManager().createQuery("SELECT o FROM ServiceAccount o", ServiceAccount.class)
				.setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Tenant: ").append(getTenant()).append(", ");
		sb.append("Id: ").append(getId()).append(", ");
        sb.append("Name: ").append(getName()).append(", ");
		sb.append("ServiceProvider: ").append(getServiceProvider()).append(", ");
		sb.append("AccountUri: ").append(getAccountURI()).append(", ");
		sb.append("AccessSecret: ").append(getAccessSecret()).append(", ");
		sb.append("AccessToken: ").append(getAccessToken()).append(", ");
		sb.append("Enabled: ").append(isEnabled());
		return sb.toString();
	}

	public String getAccessToken() {
		return this.accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getAccessSecret() {
		return this.accessSecret;
	}

	public void setAccessSecret(String accessSecret) {
		this.accessSecret = accessSecret;
	}

	public ServiceProvider getServiceProvider() {
		return this.serviceProvider;
	}

	public void setServiceProvider(ServiceProvider serviceProvider) {
		this.serviceProvider = serviceProvider;
	}

	public String getAccountURI() {
		return accountUri;
	}

	public void setAccountURI(String accountUri) {
		this.accountUri = accountUri;
	}

	public Tenant getTenant() {
		return this.tenant;
	}

	public void setTenant(Tenant tenant) {
		this.tenant = tenant;
	}

	public static ServiceAccount findAllByAccountUri(String accountUri, Tenant localTenant) {
		if (accountUri == null || accountUri.length() == 0)
			throw new IllegalArgumentException("The accountUri argument is required");
		EntityManager em = ServiceAccount.entityManager();
		TypedQuery<ServiceAccount> q = em.createQuery(
				"SELECT o FROM ServiceAccount AS o WHERE o.tenant = :localTenant AND o.accountUri = :accountUri",
				ServiceAccount.class);
		q.setParameter("accountUri", accountUri);
		return QueryUtil.getSingleResultOrNull(q);
	}

	public static List<ServiceAccount> findAllByTenant(Tenant tenant) {
		if (tenant == null)
			throw new IllegalArgumentException("The tenant argument is required");
		EntityManager em = ServiceAccount.entityManager();
		TypedQuery<ServiceAccount> q = em.createQuery(
				"SELECT o FROM ServiceAccount AS o WHERE o.tenant = :tenant", ServiceAccount.class);
		q.setParameter("tenant", tenant);
		return q.getResultList();
	}
	
	public static List<ServiceAccount> findAllByTenantAndServiceProvider(Tenant tenant, ServiceProvider serviceProvider) {
		if (tenant == null)
			throw new IllegalArgumentException("The tenant argument is required");
		if (serviceProvider == null)
			throw new IllegalArgumentException("The serviceProvider argument is required");
		EntityManager em = ServiceAccount.entityManager();
		TypedQuery<ServiceAccount> q = em.createQuery(
				"SELECT o FROM ServiceAccount AS o WHERE o.tenant = :tenant AND o.serviceProvider = :serviceProvider", ServiceAccount.class);
		q.setParameter("tenant", tenant);
		q.setParameter("serviceProvider", serviceProvider);
		return q.getResultList();
	}

	public static ServiceAccount findByName(String name) {
		if (name == null || name.length() == 0)
			throw new IllegalArgumentException("The name argument is required");
		EntityManager em = ServiceAccount.entityManager();
		TypedQuery<ServiceAccount> q = em.createQuery(
				"SELECT o FROM ServiceAccount AS o WHERE o.name = :name",
				ServiceAccount.class);
		q.setParameter("name", name);
		return QueryUtil.getSingleResultOrNull(q);
		
	}
	
}
