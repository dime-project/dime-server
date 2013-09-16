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
import javax.persistence.Version;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;
import org.springframework.transaction.annotation.Transactional;

@Configurable
@Entity
@RooJavaBean
@RooToString
@RooEntity(finders = { "findCrawlerHandlersByJobId" })
public class CrawlerHandler {

    private String className;

    @ManyToOne
    private CrawlerJob jobId;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    private Tenant tenant;

	public String getClassName() {
        return this.className;
    }

	public void setClassName(String className) {
        this.className = className;
    }

	public CrawlerJob getJobId() {
        return this.jobId;
    }

	public void setJobId(CrawlerJob jobId) {
        this.jobId = jobId;
    }

	public Tenant getTenant() {
        return this.tenant;
    }

	public void setTenant(Tenant tenant) {
        this.tenant = tenant;
    }

	public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ClassName: ").append(getClassName()).append(", ");
        sb.append("Id: ").append(getId()).append(", ");
        sb.append("JobId: ").append(getJobId()).append(", ");
        sb.append("Tenant: ").append(getTenant()).append(", ");
        sb.append("Version: ").append(getVersion());
        return sb.toString();
    }

	public static TypedQuery<CrawlerHandler> findCrawlerHandlersByJobId(CrawlerJob jobId) {
        if (jobId == null) throw new IllegalArgumentException("The jobId argument is required");
        EntityManager em = CrawlerHandler.entityManager();
        TypedQuery<CrawlerHandler> q = em.createQuery("SELECT o FROM CrawlerHandler AS o WHERE o.jobId = :jobId", CrawlerHandler.class);
        q.setParameter("jobId", jobId);
        return q;
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
            CrawlerHandler attached = CrawlerHandler.findCrawlerHandler(this.id);
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
    public CrawlerHandler merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        CrawlerHandler merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }

	public static final EntityManager entityManager() {
        EntityManager em = new CrawlerHandler().entityManager;
        if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }

	public static long countCrawlerHandlers() {
        return entityManager().createQuery("SELECT COUNT(o) FROM CrawlerHandler o", Long.class).getSingleResult();
    }

	public static List<CrawlerHandler> findAllCrawlerHandlers() {
        return entityManager().createQuery("SELECT o FROM CrawlerHandler o", CrawlerHandler.class).getResultList();
    }

	public static CrawlerHandler findCrawlerHandler(Long id) {
        if (id == null) return null;
        return entityManager().find(CrawlerHandler.class, id);
    }

	public static List<CrawlerHandler> findCrawlerHandlerEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM CrawlerHandler o", CrawlerHandler.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
	
	public static List<CrawlerHandler> findAllByTenant(Tenant tenant) {
		 if (tenant == null) {
	            throw new IllegalArgumentException("The tenant argument is required");
	        }
		  EntityManager em = CrawlerHandler.entityManager();
	        TypedQuery<CrawlerHandler> q = em.createQuery("SELECT o FROM CrawlerHandler AS o WHERE o.tenant = :tenant", CrawlerHandler.class);
	        q.setParameter("tenant", tenant);
	        return q.getResultList();
    }
}
