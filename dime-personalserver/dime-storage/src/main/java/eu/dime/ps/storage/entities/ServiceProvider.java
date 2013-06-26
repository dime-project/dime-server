package eu.dime.ps.storage.entities;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;
import org.springframework.transaction.annotation.Transactional;

import eu.dime.ps.storage.util.QueryUtil;

@Entity
@Configurable
@RooJavaBean
@RooToString
@RooEntity
public class ServiceProvider {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private Long id;

	@NotNull
	@Column(unique = true)
	private String serviceName;

	@NotNull
	private String consumerKey;

	@NotNull
	private String consumerSecret;

	private Boolean enabled = false;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "serviceProvider")
	private Set<ServiceAccount> serviceAccounts = new HashSet<ServiceAccount>();

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Id: ").append(getId());
		sb.append("ServiceName: ").append(getServiceName()).append(", ");
		sb.append("ConsumerKey: ").append(getConsumerKey()).append(", ");
		sb.append("ConsumerSecret: ").append(getConsumerSecret()).append(", ");
		sb.append("ServiceAccounts: ").append(getServiceAccounts() == null ? "null" : getServiceAccounts().size()).append(", ");
		sb.append("Enabled: ").append(isEnabled());
		return sb.toString();
	}

	@PersistenceContext
	transient EntityManager entityManager;

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
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
			ServiceProvider attached = ServiceProvider.find(this.id);
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
	public ServiceProvider merge() {
		if (this.entityManager == null)
			this.entityManager = entityManager();
		ServiceProvider merged = this.entityManager.merge(this);
		this.entityManager.flush();
		return merged;
	}

	public static final EntityManager entityManager() {
		EntityManager em = new ServiceProvider().entityManager;
		if (em == null)
			throw new IllegalStateException(
					"Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
		return em;
	}

	public static long count() {
		return entityManager().createQuery(
				"SELECT COUNT(o) FROM ServiceProvider o", Long.class)
				.getSingleResult();
	}

	public static List<ServiceProvider> findAll() {
		return entityManager().createQuery("SELECT o FROM ServiceProvider o",
				ServiceProvider.class).getResultList();
	}

	public static ServiceProvider find(Long id) {
		if (id == null)
			return null;
		return entityManager().find(ServiceProvider.class, id);
	}

	public static List<ServiceProvider> find(int firstResult, int maxResults) {
		return entityManager()
				.createQuery("SELECT o FROM ServiceProvider o",
						ServiceProvider.class).setFirstResult(firstResult)
				.setMaxResults(maxResults).getResultList();
	}

	public static ServiceProvider findByName(String serviceName) {
		if (serviceName == null || serviceName.length() == 0)
			throw new IllegalArgumentException(
					"The serviceName argument is required");
		EntityManager em = ServiceProvider.entityManager();
		TypedQuery<ServiceProvider> query = em
				.createQuery(
						"SELECT o FROM ServiceProvider AS o WHERE o.serviceName = :serviceName",
						ServiceProvider.class);
		query.setParameter("serviceName", serviceName);
		return QueryUtil.getSingleResultOrNull(query);
	}

	public String getServiceName() {
		return this.serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getConsumerKey() {
		return this.consumerKey;
	}

	public void setConsumerKey(String consumerKey) {
		this.consumerKey = consumerKey;
	}

	public String getConsumerSecret() {
		return this.consumerSecret;
	}

	public void setConsumerSecret(String consumerSecret) {
		this.consumerSecret = consumerSecret;
	}

	public Set<ServiceAccount> getServiceAccounts() {
		return this.serviceAccounts;
	}

	public void setServiceAccounts(Set<ServiceAccount> serviceAccounts) {
		this.serviceAccounts = serviceAccounts;
	}

}
