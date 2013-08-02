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
import javax.persistence.Version;
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
@RooEntity(finders = {})
public class CrawlerJob {

    @NotNull
    private String accountIdentifier;
    
    @NotNull
    private String path;

    @NotNull
    private String returnType;

    @NotNull
    private String cron;

    @NotNull
    private boolean suspended = false;

    @OneToMany(mappedBy = "jobId", cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    private Set<CrawlerHandler> crawlerHandlers = new HashSet<CrawlerHandler>();

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    private Tenant tenant;

	public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Tenant: ").append(getTenant()).append(", ");
        sb.append("Id: ").append(getId()).append(", ");
        sb.append("Account: ").append(getAccountIdentifier()).append(", ");
        sb.append("Path: ").append(getPath()).append(", ");
        sb.append("ReturnType: ").append(getReturnType()).append(", ");
        sb.append("Cron: ").append(getCron()).append(", ");
        sb.append("CrawlerHandlers: ").append(getCrawlerHandlers() == null ? "null" : getCrawlerHandlers().size()).append(", ");
        sb.append("Suspended: ").append(isSuspended()).append(", ");
        return sb.toString();
    }

	public static TypedQuery<CrawlerJob> findByAccountIdentifierEquals(String accountIdentifier) {
        if (accountIdentifier == null || accountIdentifier.length() == 0) throw new IllegalArgumentException("The accountIdentifier argument is required");
        EntityManager em = CrawlerJob.entityManager();
        TypedQuery<CrawlerJob> q = em.createQuery("SELECT o FROM CrawlerJob AS o WHERE o.accountIdentifier = :accountIdentifier", CrawlerJob.class);
        q.setParameter("accountIdentifier", accountIdentifier);
        return q;
    }

	public String getCron() {
        return this.cron;
    }

	public void setCron(String cron) {
        this.cron = cron;
    }

	public String getAccountIdentifier() {
        return this.accountIdentifier;
    }

	public void setAccountIdentifier(String accountIdentifier) {
        this.accountIdentifier = accountIdentifier;
    }

	public Boolean isSuspended() {
        return this.suspended;
    }

	public void setSuspended(Boolean suspended) {
        this.suspended = suspended;
    }

	public String getPath() {
        return this.path;
    }

	public void setPath(String path) {
        this.path = path;
    }

	public String getReturnType() {
        return this.returnType;
    }

	public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

	public Set<CrawlerHandler> getCrawlerHandlers() {
        return this.crawlerHandlers;
    }

	public void setCrawlerHandlers(Set<CrawlerHandler> crawlerHandlers) {
        this.crawlerHandlers = crawlerHandlers;
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
            CrawlerJob attached = CrawlerJob.find(this.id);
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
    public CrawlerJob merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        CrawlerJob merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }

	public static final EntityManager entityManager() {
        EntityManager em = new CrawlerJob().entityManager;
        if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }

	public static long count() {
        return entityManager().createQuery("SELECT COUNT(o) FROM CrawlerJob o", Long.class).getSingleResult();
    }

	public static List<CrawlerJob> findAll() {
        return entityManager().createQuery("SELECT o FROM CrawlerJob o", CrawlerJob.class).getResultList();
    }

	public static CrawlerJob find(Long id) {
        if (id == null) return null;
        return entityManager().find(CrawlerJob.class, id);
    }

	public static List<CrawlerJob> find(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM CrawlerJob o", CrawlerJob.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
}
