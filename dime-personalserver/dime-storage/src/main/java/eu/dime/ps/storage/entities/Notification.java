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

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.PersistenceContext;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;
import org.springframework.transaction.annotation.Transactional;

import eu.dime.commons.notifications.DimeInternalNotification;

@Configurable
@Entity
@RooJavaBean
@RooToString
@RooEntity
public class Notification {

    @NotNull
    @Size(max = 255)
    private String target;

    @NotNull
    @Size(max = 255)
    private String sender;

    //@NotNull // TODO Candidate to be removed
    @Size(max = 255)
    private String name;
    
    @NotNull
    @Size(max = 255)
    private String notificationType;
    
    @Size(max = 255)
    private String operation;

    @Size(max = 255)
    private String itemType;

    @Size(max = 255)
    private String itemID;
    
    @Lob 
    @Basic(fetch = FetchType.LAZY)
    @Column(length=100000)
    private String unEntry;
    
    //@Column(columnDefinition="default false")
    private Boolean isRead;

    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(style = "S-")
    private Date ts;
    
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(style = "S-")
    private Date updateTs;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    private Tenant tenant;

    @Transactional
    public static List<Notification> findAllNotificationsOrderedbyTime(Integer firstResult, Integer maxResults) {
        EntityManager em = Notification.entityManager();
        TypedQuery<Notification> q = em.createQuery("SELECT o FROM Notification AS o ORDER BY o.ts DESC", Notification.class);
  	  q.setFirstResult(firstResult);
  	  q.setMaxResults(maxResults);
        return q.getResultList();
    }
    
    @Transactional
    public static List<Notification> findAllUserNotificationses(Integer firstResult, Integer maxResults) {
    	EntityManager em = Notification.entityManager();
    	  TypedQuery<Notification> q = em.createQuery("SELECT o FROM Notification AS o WHERE o.notificationType LIKE :notitype ORDER BY o.ts DESC", Notification.class);
    	  q.setParameter("notitype", DimeInternalNotification.USER_NOTIFICATION_TYPE);
    	  q.setFirstResult(firstResult);
    	  q.setMaxResults(maxResults);
    	  return q.getResultList();
	}
    
    @Transactional
    public static List<Notification> findAllUserNotificationsesByTenant(Tenant tenant, Integer firstResult, Integer maxResults) {
    	EntityManager em = Notification.entityManager();
    	  TypedQuery<Notification> q = em.createQuery("SELECT o FROM Notification AS o WHERE o.notificationType LIKE :notitype AND o.tenant = :tenant ORDER BY o.ts DESC", Notification.class);
    	  q.setParameter("notitype", DimeInternalNotification.USER_NOTIFICATION_TYPE);
    	  q.setParameter("tenant", tenant);
    	  q.setFirstResult(firstResult);
    	  q.setMaxResults(maxResults);
    	  return q.getResultList();
	}
    
    @Transactional
    public static List<Notification> findAllUserNotificationsesUnreadedByTenant(Tenant tenant, Integer firstResult, Integer maxResults) {
    	EntityManager em = Notification.entityManager();
    	  TypedQuery<Notification> q = em.createQuery("SELECT o FROM Notification AS o WHERE o.notificationType LIKE :notitype AND o.tenant = :tenant AND o.isRead = false ORDER BY o.ts DESC", Notification.class);
    	  q.setParameter("notitype", DimeInternalNotification.USER_NOTIFICATION_TYPE);
    	  q.setParameter("tenant", tenant);
    	  q.setFirstResult(firstResult);
    	  q.setMaxResults(maxResults);
    	  return q.getResultList();
	}
    
    @Transactional
    public static List<Notification> findAllNotificationsByDate(Date from, Date to, Integer firstResult, Integer maxResults) {
        EntityManager em = Notification.entityManager();
        TypedQuery<Notification> q = em.createQuery("SELECT o FROM Notification AS o WHERE o.ts BETWEEN :datefrom AND :dateto ORDER BY o.ts DESC", Notification.class);
        q.setParameter("datefrom", from);
        q.setParameter("dateto", to);
        q.setFirstResult(firstResult);
  	  	q.setMaxResults(maxResults);
        return q.getResultList();
    }
    
    @Transactional
    public static List<Notification> findAllNotificationsByTenant(Tenant tenant) {
    	 if (tenant == null) throw new IllegalArgumentException("The 'tenant' argument is required");
    	EntityManager em = Notification.entityManager();
        TypedQuery<Notification> q = em.createQuery("SELECT o FROM Notification AS o WHERE o.tenant = :tenant", Notification.class);
        q.setParameter("tenant",tenant);  
          return q.getResultList();
    }

	public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Id: ").append(getId()).append(", ");
        sb.append("ItemID: ").append(getItemID()).append(", ");
        sb.append("ItemType: ").append(getItemType()).append(", ");
        sb.append("Name: ").append(getName()).append(", ");
        sb.append("Operation: ").append(getOperation()).append(", ");
        sb.append("Sender: ").append(getSender()).append(", ");
        sb.append("Target: ").append(getTarget()).append(", ");
        sb.append("Tenant: ").append(getTenant()).append(", ");
        sb.append("Ts: ").append(getTs()).append(", ");
        sb.append("Version: ").append(getVersion());
        return sb.toString();
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
            Notification attached = Notification.findNotifications(this.id);
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
    public Notification merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        Notification merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }

	public static final EntityManager entityManager() {
        EntityManager em = new Notification().entityManager;
        if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }

	public static long countNotificationses() {
        return entityManager().createQuery("SELECT COUNT(o) FROM Notification o", Long.class).getSingleResult();
    }
	
	public static long countUserNotificationses() {
        return entityManager().createQuery("SELECT COUNT(o) FROM Notification AS o WHERE o.notificationType LIKE '"+ DimeInternalNotification.USER_NOTIFICATION_TYPE +"'", Long.class).getSingleResult();
    }

	public static long countSystemNotificationses() {
        return entityManager().createQuery("SELECT COUNT(o) FROM Notification AS o WHERE o.notificationType LIKE '"+ DimeInternalNotification.SYSTEM_NOTIFICATION_TYPE +"'", Long.class).getSingleResult();
    }
	
	public static List<Notification> findAllNotificationses() {
        return entityManager().createQuery("SELECT o FROM Notification o", Notification.class).getResultList();
    }

	public static Notification findNotifications(Long id) {
        if (id == null) return null;
        return entityManager().find(Notification.class, id);
    }

	public static List<Notification> findNotificationsEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM Notification o", Notification.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
		

	public String getTarget() {
        return this.target;
    }

	public void setTarget(String target) {
        this.target = target;
    }

	public String getSender() {
        return this.sender;
    }

	public void setSender(String sender) {
        this.sender = sender;
    }

	public String getOperation() {
        return this.operation;
    }

	public void setOperation(String operation) {
        this.operation = operation;
    }

	public String getItemType() {
        return this.itemType;
    }

	public void setItemType(String itemType) {
        this.itemType = itemType;
    }

	public String getItemID() {
        return this.itemID;
    }

	public void setItemID(String itemID) {
        this.itemID = itemID;
    }

	public String getName() {
        return this.name;
    }

	public void setName(String name) {
        this.name = name;
    }

	public Date getTs() {
        return this.ts;
    }

	public void setTs(Date ts) {
        this.ts = ts;
    }

	public Tenant getTenant() {
        return this.tenant;
    }

	public void setTenant(Tenant tenant) {
        this.tenant = tenant;
    }

	public String getNotificationType() {
		return notificationType;
	}

	public void setNotificationType(String notificationType) {
		this.notificationType = notificationType;
	}


	public Date getUpdateTs() {
		return updateTs;
	}

	public void setUpdateTs(Date updateTs) {
		this.updateTs = updateTs;
	}

	public Boolean getIsRead() {
		return isRead;
	}

	public void setIsRead(Boolean isRead) {
		this.isRead = isRead;
	}

	public String getUnEntry() {
		return unEntry;
	}

	public void setUnEntry(String unEntry) {
		this.unEntry = unEntry;
	}

	
}
