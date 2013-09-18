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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PersistenceContext;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;
import javax.persistence.Version;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;
import org.springframework.transaction.annotation.Transactional;

import eu.dime.commons.notifications.DimeInternalNotification;

@Entity
@Configurable
@RooJavaBean
@RooToString
@RooEntity
public class SphereLog {

	public final static String EVALUATIONDATA_ACTION_REGISTER = "register";
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private Long id;
	
	private String tenantId;

	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(style = "S-")
	private Date evaluationdate;

	private String type;
	
	private String action;


	public String toString() {
		StringBuilder sb = new StringBuilder();		
		sb.append("type: ").append(getType()).append(", ");	
		sb.append("action: ").append(getAction()).append(", ");
		sb.append("Evaluationdate: ").append(getEvaluationdate()).append(", ");				
		sb.append("Id: ").append(getId()).append(", ");			
		sb.append("TenantId: ").append(getTenantId()).append(", ");	
		return sb.toString();
	}


	@PersistenceContext
	transient EntityManager entityManager;	
	
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
			SphereLog attached = SphereLog.findSphereLog(this.id);
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
	public SphereLog merge() {
		if (this.entityManager == null) this.entityManager = entityManager();
		SphereLog merged = this.entityManager.merge(this);
		this.entityManager.flush();
		return merged;
	}

	public static final EntityManager entityManager() {
		EntityManager em = new SphereLog().entityManager;
		if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
		return em;
	}

	public static long countSphereLogs() {
		return entityManager().createQuery("SELECT COUNT(o) FROM SphereLog o", Long.class).getSingleResult();
	}

	public static List<SphereLog> findAllSphereLogs() {
		return entityManager().createQuery("SELECT o FROM SphereLog o", SphereLog.class).getResultList();
	}
		

	public static SphereLog findSphereLog(Long id) {
		if (id == null) return null;
		return entityManager().find(SphereLog.class, id);
	}

	
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	

	public Date getEvaluationdate() {
		return this.evaluationdate;
	}

	public void setEvaluationdate(Date evaluationdate) {
		this.evaluationdate = evaluationdate;
	}
	
	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}
	

	public String getAction() {
		return this.action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}


	
}
