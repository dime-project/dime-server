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
public class EvaluationData {

	public final static String EVALUATIONDATA_ACTION_REGISTER = "register";
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private Long id;
	
	private String tenantId;

	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(style = "S-")
	private Date evaluationdate;

	private String clientid;   

	private String viewstack;

	private String evaluationaction;

	private String currentplace;

	private String currsituationid;
	
	private String involvedItems;


	public String toString() {
		StringBuilder sb = new StringBuilder();		
		sb.append("Clientid: ").append(getClientid()).append(", ");
		sb.append("Currentplace: ").append(getCurrentplace()).append(", ");
		sb.append("Currsituationid: ").append(getCurrsituationid()).append(", ");
		sb.append("Evaluationaction: ").append(getEvaluationaction()).append(", ");
		sb.append("Evaluationdate: ").append(getEvaluationdate()).append(", ");
		sb.append("Viewstack: ").append(getViewstack()).append(", ");		
		sb.append("Id: ").append(getId()).append(", ");			
		sb.append("TenantId: ").append(getTenantId()).append(", ");
		sb.append("Version: ").append(getVersion());
		sb.append("InvolvedItems: ").append(getInvolvedItems());
		return sb.toString();
	}

	public String getInvolvedItems() {
		return involvedItems;
	}

	public void setInvolvedItems(String involvedItems) {
		this.involvedItems = involvedItems;
	}

	@PersistenceContext
	transient EntityManager entityManager;


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
			EvaluationData attached = EvaluationData.findEvaluationData(this.id);
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
	public EvaluationData merge() {
		if (this.entityManager == null) this.entityManager = entityManager();
		EvaluationData merged = this.entityManager.merge(this);
		this.entityManager.flush();
		return merged;
	}

	public static final EntityManager entityManager() {
		EntityManager em = new EvaluationData().entityManager;
		if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
		return em;
	}

	public static long countEvaluationDatas() {
		return entityManager().createQuery("SELECT COUNT(o) FROM EvaluationData o", Long.class).getSingleResult();
	}

	public static List<EvaluationData> findAllEvaluationDatas() {
		return entityManager().createQuery("SELECT o FROM EvaluationData o", EvaluationData.class).getResultList();
	}
	
	public static List<EvaluationData> findAllLogRegisterEvaluationDatas() {
		TypedQuery<EvaluationData> q = entityManager().createQuery("SELECT o FROM EvaluationData o WHERE o.evaluationaction LIKE :evaluationaction", EvaluationData.class);
		q.setParameter("evaluationaction", EvaluationData.EVALUATIONDATA_ACTION_REGISTER);
		return q.getResultList();
	}

	public static EvaluationData findEvaluationData(Long id) {
		if (id == null) return null;
		return entityManager().find(EvaluationData.class, id);
	}

	public static List<EvaluationData> findEvaluationDataEntries(int firstResult, int maxResults) {
		return entityManager().createQuery("SELECT o FROM EvaluationData o", EvaluationData.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
	}	

	public Date getEvaluationdate() {
		return this.evaluationdate;
	}

	public void setEvaluationdate(Date evaluationdate) {
		this.evaluationdate = evaluationdate;
	}

	public String getClientid() {
		return this.clientid;
	}

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public void setClientid(String clientid) {
		this.clientid = clientid;
	}
	

	public String getEvaluationaction() {
		return this.evaluationaction;
	}

	public void setEvaluationaction(String evaluationaction) {
		this.evaluationaction = evaluationaction;
	}

	public String getCurrentplace() {
		return this.currentplace;
	}

	public void setCurrentplace(String currentplace) {
		this.currentplace = currentplace;
	}

	public String getCurrsituationid() {
		return this.currsituationid;
	}

	public void setCurrsituationid(String currsituationid) {
		this.currsituationid = currsituationid;
	}

	public String getViewstack() {
		return viewstack;
	}

	public void setViewstack(String viewstack) {
		this.viewstack = viewstack;
	}
	
}
