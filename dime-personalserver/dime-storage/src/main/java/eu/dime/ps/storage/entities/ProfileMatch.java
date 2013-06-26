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
public class ProfileMatch {

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    private Tenant tenant;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    private PersonMatch personMatch;

    @NotNull
    @Column
    private String source;
    
    @NotNull
    @Column
    private Integer attNoSource;
    
    @NotNull
    @Column
    private String target;
    
    @NotNull
    @Column
    private Integer attNoTarget;
    
    @NotNull
    @Column
	private Double similarityScore;

    @OneToMany(mappedBy = "profileMatch", cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    private Set<AttributeMatch> attributeMatches = new HashSet<AttributeMatch>();

	@PersistenceContext
    transient EntityManager entityManager;
	
	public ProfileMatch() {}
	
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

	public PersonMatch getPersonMatch() {
		return personMatch;
	}

	public void setPersonMatch(PersonMatch personMatch) {
		this.personMatch = personMatch;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public Integer getAttNoSource() {
		return attNoSource;
	}

	public void setAttNoSource(Integer attNoSource) {
		this.attNoSource = attNoSource;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public Integer getAttNoTarget() {
		return attNoTarget;
	}

	public void setAttNoTarget(Integer attNoTarget) {
		this.attNoTarget = attNoTarget;
	}
	
	public Double getSimilarityScore() {
		return similarityScore;
	}

	public void setSimilarityScore(Double similarityScore) {
		this.similarityScore = similarityScore;
	}

	public Set<AttributeMatch> getAttributeMatches() {
		return attributeMatches;
	}

	public void setAttributeMatches(Set<AttributeMatch> attributeMatches) {
		this.attributeMatches = attributeMatches;
	}

	public void addAttributeMatch(AttributeMatch attributeMatch) {
		this.attributeMatches.add(attributeMatch);
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
        	ProfileMatch attached = ProfileMatch.findProfileMatch(this.id);
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
    public ProfileMatch merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        ProfileMatch merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }

	public static final EntityManager entityManager() {
        EntityManager em = new ProfileMatch().entityManager;
        if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }

	public static long countProfileMatch() {
        return entityManager().createQuery("SELECT COUNT(o) FROM ProfileMatch o", Long.class).getSingleResult();
    }

	public static List<ProfileMatch> findAllProfileMatch() {
        return entityManager().createQuery("SELECT o FROM ProfileMatch o", ProfileMatch.class).getResultList();
    }

	public static ProfileMatch findProfileMatch(Long id) {
        if (id == null) return null;
        return entityManager().find(ProfileMatch.class, id);
    }

	public static List<ProfileMatch> findAllProfileMatch(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM ProfileMatch o", ProfileMatch.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }

	public static List<ProfileMatch> findAllProfileMatchByTenant(Tenant tenant) {
        if (tenant == null) throw new IllegalArgumentException("The 'tenant' argument is required");
        EntityManager em = Tenant.entityManager();
        TypedQuery<ProfileMatch> q = em.createQuery("SELECT o FROM ProfileMatch AS o WHERE o.tenant = :tenant", ProfileMatch.class);
        q.setParameter("tenant", tenant);
        return q.getResultList();
    }

}
