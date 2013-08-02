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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;
import org.springframework.transaction.annotation.Transactional;

@Configurable
@Entity
@RooJavaBean
@RooToString
@RooEntity
public class PersonMatch {
	
	public static final String ACCEPTED = "accepted";
	public static final String PENDING = "pending";
	public static final String DISMISSED = "dismissed";
	
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    private Tenant tenant;

    @NotNull
    @Column
    private String source;
    
    @NotNull
    @Column
    private String target;
    
    @NotNull
    @Column
	private Double similarityScore;
    
    @OneToMany(mappedBy = "personMatch", cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    private Set<ProfileMatch> profileMatches = new HashSet<ProfileMatch>();

    @NotNull
    @Column
	private String status;

    @Column
    private Integer technique;
    
    @Column
    private boolean semanticExtension;

    @NotNull
    @Column
    private Date lastPerformed;

	@PersistenceContext
    transient EntityManager entityManager;
	
	public PersonMatch() {}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Tenant getTenant() {
		return tenant;
	}

	public void setTenant(Tenant tenant) {
		this.tenant = tenant;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public Double getSimilarityScore() {
		return similarityScore;
	}

	public void setSimilarityScore(Double similarityScore) {
		this.similarityScore = similarityScore;
	}

	public Set<ProfileMatch> getProfileMatches() {
		return profileMatches;
	}

	public void setProfileMatches(Set<ProfileMatch> profileMatches) {
		this.profileMatches = profileMatches;
	}

	public void addProfileMatch(ProfileMatch profileMatch) {
		this.profileMatches.add(profileMatch);
	}
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Integer getTechnique() {
		return technique;
	}

	public void setTechnique(Integer technique) {
		this.technique = technique;
	}
	
	public boolean isSemanticExtension() {
		return semanticExtension;
	}

	public void setSemanticExtension(boolean semanticExtension) {
		this.semanticExtension = semanticExtension;
	}

	public Date getLastPerformed() {
		return lastPerformed;
	}

	public void setLastPerformed(Date lastPerformed) {
		this.lastPerformed = lastPerformed;
	}

	public EntityManager getEntityManager() {
		return entityManager;
	}

	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
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
        	PersonMatch attached = PersonMatch.find(this.id);
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
    public PersonMatch merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        PersonMatch merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }

	public static final EntityManager entityManager() {
        EntityManager em = new PersonMatch().entityManager;
        if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }

	public static long count() {
        return entityManager().createQuery("SELECT COUNT(o) FROM PersonMatch o", Long.class).getSingleResult();
    }

	public static List<PersonMatch> findAll() {
        return entityManager().createQuery("SELECT o FROM PersonMatch o", PersonMatch.class).getResultList();
    }

	public static PersonMatch find(Long id) {
        if (id == null) return null;
        return entityManager().find(PersonMatch.class, id);
    }

	public static List<PersonMatch> findAll(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM PersonMatch o", PersonMatch.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }

	public static List<PersonMatch> findAllByTenant(Tenant tenant) {
        if (tenant == null)
        	throw new IllegalArgumentException("The 'tenant' argument is required");
        EntityManager em = Tenant.entityManager();
        TypedQuery<PersonMatch> q = em.createQuery("SELECT o FROM PersonMatch AS o WHERE o.tenant = :tenant", PersonMatch.class);
        q.setParameter("tenant", tenant);
        return q.getResultList();
    }

	public static List<PersonMatch> findAllByTenantAndByThreshold(Tenant tenant, Double threshold) {
        if (tenant == null || threshold == null)
        	throw new IllegalArgumentException("The arguments 'tenant' and 'threshold' are required");
        EntityManager em = Tenant.entityManager();
        TypedQuery<PersonMatch> q = em.createQuery("SELECT o FROM PersonMatch AS o WHERE o.tenant = :tenant AND o.similarityScore > :threshold", PersonMatch.class);
        q.setParameter("tenant", tenant);
        q.setParameter("threshold", threshold);
        return q.getResultList();
    }

	public static List<PersonMatch> findAllByTenantAndBySource(Tenant tenant, String source) {
        if (tenant == null || source == null)
        	throw new IllegalArgumentException("The arguments 'tenant' and 'source' are required");
        EntityManager em = Tenant.entityManager();
        TypedQuery<PersonMatch> q = em.createQuery("SELECT o FROM PersonMatch AS o WHERE o.tenant = :tenant AND o.source = :source", PersonMatch.class);
        q.setParameter("tenant", tenant);
        q.setParameter("source", source);
        return q.getResultList();
    }

	public static List<PersonMatch> findAllByTenantAndBySourceAndByThreshold(Tenant tenant, String source, Double threshold) {
        if (tenant == null || source == null || threshold == null)
        	throw new IllegalArgumentException("The arguments 'tenant', 'source' and 'threshold' are required");
        EntityManager em = Tenant.entityManager();
        TypedQuery<PersonMatch> q = em.createQuery("SELECT o FROM PersonMatch AS o WHERE o.tenant = :tenant AND o.source = :source AND o.similarityScore > :threshold", PersonMatch.class);
        q.setParameter("tenant", tenant);
        q.setParameter("source", source);
        q.setParameter("threshold", threshold);
        return q.getResultList();
    }

	public static List<PersonMatch> findAllByTenantAndByTechnique(Tenant tenant, Integer technique) {
        if (tenant == null || technique == null)
        	throw new IllegalArgumentException("The arguments 'tenant' and 'technique' are required");
        EntityManager em = Tenant.entityManager();
        TypedQuery<PersonMatch> q = em.createQuery("SELECT o FROM PersonMatch AS o WHERE o.tenant = :tenant AND o.technique = :technique", PersonMatch.class);
        q.setParameter("tenant", tenant);
        q.setParameter("technique", technique);
        return q.getResultList();
    }
	
	public static List<PersonMatch> findAllByTenantAndByTechniqueAndByThreshold(Tenant tenant, Integer technique, Double threshold) {
        if (tenant == null || technique == null || threshold == null)
        	throw new IllegalArgumentException("The arguments 'tenant', 'technique' and 'threshold' are required");
        EntityManager em = Tenant.entityManager();
        TypedQuery<PersonMatch> q = em.createQuery("SELECT o FROM PersonMatch AS o WHERE o.tenant = :tenant AND o.technique = :technique AND o.similarityScore > :threshold", PersonMatch.class);
        q.setParameter("tenant", tenant);
        q.setParameter("technique", technique);
        q.setParameter("threshold", threshold);
        return q.getResultList();
    }

	public static List<PersonMatch> findAllByTenantAndByTechniqueAndByStatus(Tenant tenant, Integer technique, String status) {
        if (tenant == null || technique == null || status == null)
        	throw new IllegalArgumentException("The arguments 'tenant', 'technique' and 'status' are required");
        EntityManager em = Tenant.entityManager();
        TypedQuery<PersonMatch> q = em.createQuery("SELECT o FROM PersonMatch AS o WHERE o.tenant = :tenant AND o.technique = :technique AND o.status = :status", PersonMatch.class);
        q.setParameter("tenant", tenant);
        q.setParameter("technique", technique);
        q.setParameter("status", status);
        return q.getResultList();
    }
	
	public static List<PersonMatch> findAllByTenantAndByTechniqueAndByStatusAndByThreshold(Tenant tenant, Integer technique, String status, Double threshold) {
        if (tenant == null || technique == null || status == null || threshold == null)
        	throw new IllegalArgumentException("The arguments 'tenant', 'technique', 'status' and 'threshold' are required");
        EntityManager em = Tenant.entityManager();
        TypedQuery<PersonMatch> q = em.createQuery("SELECT o FROM PersonMatch AS o WHERE o.tenant = :tenant AND o.technique = :technique AND o.status = :status AND o.similarityScore > :threshold", PersonMatch.class);
        q.setParameter("tenant", tenant);
        q.setParameter("technique", technique);
        q.setParameter("status", status);
        q.setParameter("threshold", threshold);
        return q.getResultList();
    }

	public static List<PersonMatch> findAllByTenantAndByTechniqueAndBySource(Tenant tenant, Integer technique, String source) {
        if (tenant == null || technique == null || source == null)
        	throw new IllegalArgumentException("The arguments 'tenant', 'technique' and 'source' are required");
        EntityManager em = Tenant.entityManager();
        TypedQuery<PersonMatch> q = em.createQuery("SELECT o FROM PersonMatch AS o WHERE o.tenant = :tenant AND o.source = :source AND o.technique = :technique", PersonMatch.class);
        q.setParameter("tenant", tenant);
        q.setParameter("source", source);
        q.setParameter("technique", technique);
        return q.getResultList();
    }
	
	public static List<PersonMatch> findAllByTenantAndByTechniqueAndBySourceAndByThreshold(Tenant tenant, Integer technique, String source, Double threshold) {
        if (tenant == null || technique == null || source == null || threshold == null)
        	throw new IllegalArgumentException("The arguments 'tenant', 'technique', 'source' and 'threshold' are required");
        EntityManager em = Tenant.entityManager();
        TypedQuery<PersonMatch> q = em.createQuery("SELECT o FROM PersonMatch AS o WHERE o.tenant = :tenant AND o.source = :source AND o.technique = :technique AND o.similarityScore > :threshold", PersonMatch.class);
        q.setParameter("tenant", tenant);
        q.setParameter("source", source);
        q.setParameter("technique", technique);
        q.setParameter("threshold", threshold);
        return q.getResultList();
    }

	public static List<PersonMatch> findAllByTenantAndByTechniqueAndBySourceAndByStatus(Tenant tenant, Integer technique, String source, String status) {
        if (tenant == null || technique == null || source == null || status == null)
        	throw new IllegalArgumentException("The arguments 'tenant', 'technique', 'source' and 'status' are required");
        EntityManager em = Tenant.entityManager();
        TypedQuery<PersonMatch> q = em.createQuery("SELECT o FROM PersonMatch AS o WHERE o.tenant = :tenant AND o.source = :source AND o.technique = :technique AND o.status = :status", PersonMatch.class);
        q.setParameter("tenant", tenant);
        q.setParameter("source", source);
        q.setParameter("technique", technique);
        q.setParameter("status", status);
        return q.getResultList();
    }
	
	public static List<PersonMatch> findAllByTenantAndByTechniqueAndBySourceAndByStatusAndByThreshold(Tenant tenant, Integer technique, String source, String status, Double threshold) {
        if (tenant == null || technique == null || source == null || status == null || threshold == null)
        	throw new IllegalArgumentException("The arguments 'tenant', 'technique', 'source', 'status' and 'threshold' are required");
        EntityManager em = Tenant.entityManager();
        TypedQuery<PersonMatch> q = em.createQuery("SELECT o FROM PersonMatch AS o WHERE o.tenant = :tenant AND o.source = :source AND o.technique = :technique AND o.status = :status AND o.similarityScore > :threshold", PersonMatch.class);
        q.setParameter("tenant", tenant);
        q.setParameter("source", source);
        q.setParameter("technique", technique);
        q.setParameter("status", status);
        q.setParameter("threshold", threshold);
        return q.getResultList();
    }

	public static List<PersonMatch> findAllByTenantAndBySourceAndByTarget(Tenant tenant, String source, String target) {
        if (tenant == null || source == null || target == null)
        	throw new IllegalArgumentException("The arguments 'tenant', 'source' and 'target' required");
        EntityManager em = Tenant.entityManager();
        TypedQuery<PersonMatch> q = em.createQuery("SELECT o FROM PersonMatch AS o WHERE o.tenant = :tenant AND o.source = :source AND o.target = :target", PersonMatch.class);
        q.setParameter("tenant", tenant);
        q.setParameter("source", source);
        q.setParameter("target", target);
        return q.getResultList();
	}
	
	public static List<PersonMatch> findAllByTenantAndBySourceAndByTargetAndByThreshold(Tenant tenant, String source, String target, Double threshold) {
        if (tenant == null || source == null || target == null || threshold == null)
        	throw new IllegalArgumentException("The arguments 'tenant', 'source', 'target' and 'threshold' are required");
        EntityManager em = Tenant.entityManager();
        TypedQuery<PersonMatch> q = em.createQuery("SELECT o FROM PersonMatch AS o WHERE o.tenant = :tenant AND o.source = :source AND o.target = :target AND o.similarityScore > :threshold", PersonMatch.class);
        q.setParameter("tenant", tenant);
        q.setParameter("source", source);
        q.setParameter("target", target);
        q.setParameter("threshold", threshold);
        return q.getResultList();
	}
	
	public static PersonMatch findByTenantAndBySourceAndByTargetAndByTechnique(Tenant tenant, String source, String target, Integer technique) {
        if (tenant == null || source == null || target == null || technique == null)
        	throw new IllegalArgumentException("The arguments 'tenant', 'source', 'target' and 'technique' are required");
        EntityManager em = Tenant.entityManager();
        TypedQuery<PersonMatch> q = em.createQuery("SELECT o FROM PersonMatch AS o WHERE o.tenant = :tenant AND o.source = :source AND o.target = :target AND o.technique = :technique", PersonMatch.class);
        q.setParameter("tenant", tenant);
        q.setParameter("source", source);
        q.setParameter("target", target);
        q.setParameter("technique", technique);
        List<PersonMatch> results = q.getResultList();
        if (results.size() > 1)
        	throw new IllegalArgumentException("More than one record was found, this must be a bug!");
        return results.size() == 0 ? null : results.get(0);
	}
	
}
