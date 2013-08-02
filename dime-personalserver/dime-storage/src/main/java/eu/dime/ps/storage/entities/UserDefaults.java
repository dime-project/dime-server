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

import org.hibernate.annotations.SQLUpdate;
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
        
    @ManyToOne(fetch = FetchType.EAGER, optional = true)
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
        if (name == null || name.length() == 0) {
        	return entityManager().createQuery("SELECT o FROM UserDefaults o", UserDefaults.class).getResultList();
        } else {
	        EntityManager em = UserDefaults.entityManager();
	        TypedQuery<UserDefaults> q = em.createQuery("SELECT o FROM UserDefaults AS o WHERE o.name = :name", UserDefaults.class);
	        q.setParameter("name", name);
	        return q.getResultList();
        }
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

	public static UserDefaults findAllByNameAndAppliesTo(String name,
			String appliesTo) {
        if (name == null || name.length() == 0) {
            throw new IllegalArgumentException("The name argument is required");
        }
        if (appliesTo == null || appliesTo.length() == 0) {
            throw new IllegalArgumentException("The appliesTo argument is required");
        }
        EntityManager em = UserDefaults.entityManager();
        TypedQuery<UserDefaults> q = em.createQuery("SELECT o FROM UserDefaults AS o WHERE o.name = :name AND o.appliesTo = :appliesTo", UserDefaults.class);
        q.setParameter("name", name);
        q.setParameter("appliesTo", appliesTo);
        return QueryUtil.getSingleResultOrNull(q); 
	}

}
