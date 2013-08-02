/*
* Copyright 2013 by the digital.me project (http:\\www.dime-project.eu).
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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;
import org.springframework.transaction.annotation.Transactional;

import eu.dime.ps.storage.util.QueryUtil;

@Configurable
@Entity
@RooJavaBean
@RooToString
@RooEntity(finders = { "findByName", "findByUser" })
public class Tenant {

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(unique = true) // TODO needs to check that it doesn't contain - (to avoid collisions with names in ServiceAccount)
    private String name;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "tenant")
    private Set <User> users = new HashSet<User>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "tenant")
    private Set<ServiceAccount> accounts = new HashSet<ServiceAccount>();

	@PersistenceContext
    transient EntityManager entityManager;
	
	public Tenant() {}
	
	public Tenant(String name) {
		this.name = name;
	}

	public Tenant(String name, User user) {
		if (user == null) {
			throw new IllegalArgumentException("The argument 'user' is required");
		}
		this.name = name;
		this.users.add(user);
	}

	public static Tenant findByName(String name) {
        if (name == null || name.length() == 0) throw new IllegalArgumentException("The name argument is required");
        EntityManager em = Tenant.entityManager();
        TypedQuery<Tenant> q = em.createQuery("SELECT o FROM Tenant AS o WHERE o.name = :name", Tenant.class);
        q.setParameter("name", name);
        return QueryUtil.getSingleResultOrNull(q);
    }

	public Long getId() {
        return this.id;
    }

	public void setId(Long id) {
        this.id = id;
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
            Tenant attached = Tenant.find(this.id);
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
    public Tenant merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        Tenant merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }

	public static final EntityManager entityManager() {
        EntityManager em = new Tenant().entityManager;
        if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }

	public static long count() {
        return entityManager().createQuery("SELECT COUNT(o) FROM Tenant o", Long.class).getSingleResult();
    }

	public static List<Tenant> findAll() {
        return entityManager().createQuery("SELECT o FROM Tenant o", Tenant.class).getResultList();
    }

	public static Tenant find(Long id) {
        if (id == null) return null;
        return entityManager().find(Tenant.class, id);
    }

	public static List<Tenant> find(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM Tenant o", Tenant.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }

	public String getName() {
        return this.name;
    }

	public void setName(String name) {
        this.name = name;
    }

	public Set<User> getUsers() {
        return this.users;
    }

	public void setUsers(Set<User> users) {
		this.users = users;
    }

	public void addUser(User user){
		this.users.add(user);
	}

	public Set<ServiceAccount> getAccounts() {
        return this.accounts;
    }

	public void setAccounts(Set<ServiceAccount> accounts) {
        this.accounts = accounts;
    }

	public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Id: ").append(getId()).append(", ");
        sb.append("Name: ").append(getName()).append(", ");
        sb.append("Accounts: ").append(getAccounts() == null ? "null" : getAccounts().size()).append(", ");
        return sb.toString();
    }

}
