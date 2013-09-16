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
public class AttributeMatch {

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    private Tenant tenant;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    private ProfileMatch profileMatch;

    @NotNull
    @Column
	private String attribute;
    
    @Column
	private String sourceType;

    @Column
    private String targetType;
    
    @NotNull
    @Column
	private Double similarityScore;
    
    @NotNull
    @Column
    private String technique;

	@PersistenceContext
    transient EntityManager entityManager;
	
	public AttributeMatch() {}

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

	public ProfileMatch getProfileMatch() {
		return profileMatch;
	}

	public void setProfileMatch(ProfileMatch profileMatch) {
		this.profileMatch = profileMatch;
	}

	public String getAttribute() {
		return attribute;
	}

	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}

	public String getSourceType() {
		return sourceType;
	}

	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}

	public String getTargetType() {
		return targetType;
	}

	public void setTargetType(String targetType) {
		this.targetType = targetType;
	}

	public Double getSimilarityScore() {
		return similarityScore;
	}

	public void setSimilarityScore(Double similarityScore) {
		this.similarityScore = similarityScore;
	}

	public String getTechnique() {
		return technique;
	}

	public void setTechnique(String technique) {
		this.technique = technique;
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
    public AttributeMatch merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        AttributeMatch merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }

	public static final EntityManager entityManager() {
        EntityManager em = new AttributeMatch().entityManager;
        if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }

	public static long countAttributeMatch() {
        return entityManager().createQuery("SELECT COUNT(o) FROM AttributeMatch o", Long.class).getSingleResult();
    }

	public static List<AttributeMatch> findAllAttributeMatch() {
        return entityManager().createQuery("SELECT o FROM AttributeMatch o", AttributeMatch.class).getResultList();
    }

	public static AttributeMatch findAttributeMatch(Long id) {
        if (id == null) return null;
        return entityManager().find(AttributeMatch.class, id);
    }

	public static List<AttributeMatch> findAllAttributeMatch(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM AttributeMatch o", AttributeMatch.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
	
	public static List<AttributeMatch> findAllAttributeMatchByTenant(Tenant tenant) {
        if (tenant == null) throw new IllegalArgumentException("The 'tenant' argument is required");
        EntityManager em = Tenant.entityManager();
        TypedQuery<AttributeMatch> q = em.createQuery("SELECT o FROM AttributeMatch AS o WHERE o.tenant = :tenant", AttributeMatch.class);
        q.setParameter("tenant", tenant);
        return q.getResultList();
    }


}
