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

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.transaction.annotation.Transactional;

import eu.dime.ps.storage.util.QueryUtil;
/**
 * Table storing User (tenant) specific settings 
 * @author marcel
 *
 */
@Entity
@Configurable
@Table(uniqueConstraints =@UniqueConstraint(columnNames = {"id"}))
public class UserDefaults {

    @PersistenceContext
    transient EntityManager entityManager;
	
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;
        
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    private Tenant tenant;
    
    private String name;
    
    private String value;
    
    private String appliesTo;
    
    private Boolean allowOverride;
    
    private String targetElementName;
     
     
	public Long getId() {
        return this.id;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getAppliesTo() {
		return appliesTo;
	}

	public void setAppliesTo(String appliesTo) {
		this.appliesTo = appliesTo;
	}

	public Boolean getAllowOverride() {
		return allowOverride;
	}

	public void setAllowOverride(Boolean allowOverride) {
		this.allowOverride = allowOverride;
	}
	
	public String getTargetElementName() {
		return targetElementName;
	}

	public void setTargetElementName(String targetElementName) {
		this.targetElementName = targetElementName;
	}
    
    @Transactional
    public void persist() {
        if (this.entityManager == null) {
            this.entityManager = entityManager();
        }
        this.entityManager.persist(this);
    }

    @Transactional
    public void remove() {
        if (this.entityManager == null) {
            this.entityManager = entityManager();
        }
        if (this.entityManager.contains(this)) {
            this.entityManager.remove(this);
        } else {
        	UserDefaults attached = UserDefaults.find(this.id);
            this.entityManager.remove(attached);
        }
        this.entityManager.flush();
    }

    @Transactional
    public void flush() {
        if (this.entityManager == null) {
            this.entityManager = entityManager();
        }
        this.entityManager.flush();
    }

    @Transactional
    public void clear() {
        if (this.entityManager == null) {
            this.entityManager = entityManager();
        }
        this.entityManager.clear();
    }

    @Transactional
    public UserDefaults merge() {
        if (this.entityManager == null) {
            this.entityManager = entityManager();
        }
        UserDefaults merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }

    public static final EntityManager entityManager() {
        EntityManager em = new UserDefaults().entityManager;
        if (em == null) {
            throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        }
        return em;
    }

    public static long count() {
        return entityManager().createQuery("SELECT COUNT(o) FROM UserDefaults o", Long.class).getSingleResult();
    }

    public static List<UserDefaults> findAll() {
        return entityManager().createQuery("SELECT o FROM UserDefaults o", UserDefaults.class).getResultList();
    }

    public static UserDefaults find(Long id) {
        if (id == null) {
            return null;
        }
        return entityManager().find(UserDefaults.class, id);
    }
    
    public static List<UserDefaults> findAllByName(String name) {
        return entityManager().createQuery("SELECT o FROM UserDefaults o", UserDefaults.class).getResultList();
    }
    
    public static UserDefaults findAllByTenantAndName(Tenant tenant, String name) {
        if (name == null || name.length() == 0) {
            throw new IllegalArgumentException("The username argument is required");
        }
        if (tenant == null) {
            throw new IllegalArgumentException("The tenant argument is required");
        }
        EntityManager em = UserDefaults.entityManager();
        TypedQuery<UserDefaults> q = em.createQuery("SELECT o FROM UserDefaults AS o WHERE o.name = :name AND o.tenant = :tenant", UserDefaults.class);
        q.setParameter("name", name);
        q.setParameter("tenant", tenant);
        return QueryUtil.getSingleResultOrNull(q);
    }

}
