package eu.dime.ps.storage.entities;

import eu.dime.ps.storage.exception.ReadOnlyValueChangedOnUpdate;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.PersistenceContext;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.persistence.TypedQuery;
import javax.validation.constraints.NotNull;



import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;
import org.springframework.transaction.annotation.Transactional;

import eu.dime.ps.storage.util.QueryUtil;
import java.util.UUID;

@Configurable
@Entity
@RooJavaBean
@RooToString
@RooEntity(finders = {"findByUserRoles", "findByUsernameLike", "findByPasswordLike", "findByUsernameAndPassword", "findByUsername"})
@Table(uniqueConstraints =
@UniqueConstraint(columnNames = {"id", "accountUri"}))
public class User {



    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;
    /**
     * Depending on the role: <ul> <li>OWNER: username used to log in the
     * system. <li>GUEST: account identifier used to access the system. <ul>
     */
    @NotNull
    private String username;
    /**
     * Password that an OWNER uses to log in the system, or another user's
     * account uses to access shared data.
     */
    private String password;

    @NotNull
    private Boolean enabled = false;

    @NotNull
    private String uiLanguage = "en";

    @NotNull
    private Boolean evaluationDataCapturingEnabled = false;

    @NotNull
    private String evaluationId = UUID.randomUUID().toString();
    /**
     * ADMIN, OWNER, GUEST
     */
    @NotNull
    @Enumerated
    private Role role;

    @ManyToOne(fetch = FetchType.EAGER, optional = true)
    private Tenant tenant;
    /**
     * URI of the account associated to this user. It is only required for
     * 'guest' users, since these users are actually accounts of other people
     * using di.me, and the access to data (sharing) is done at account level.
     */
    private String accountUri;

    private String emailAddress;

    public User() {
        this.role = Role.GUEST;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Tenant: ").append(getTenant()).append(", ");
        sb.append("Id: ").append(getId()).append(", ");
        sb.append("Username: ").append(getUsername()).append(", ");
        sb.append("Password: ").append(getPassword()).append(", ");
        sb.append("Role: ").append(getRole()).append(", ");
        sb.append("Enabled: ").append(isEnabled()).append(", ");
        sb.append("uiLanguage: ").append(uiLanguage).append(", ");
        sb.append("evaluationDataCapturingEnabled: ").append(evaluationDataCapturingEnabled).append(", ");
        sb.append("evaluationId: ").append(evaluationId);
        sb.append("emailAddress: ").append(emailAddress);
        return sb.toString();
    }
    @PersistenceContext
    transient EntityManager entityManager;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
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
            User attached = User.find(this.id);
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
    public User merge() {
        if (this.entityManager == null) {
            this.entityManager = entityManager();
        }
        User merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }

    public static final EntityManager entityManager() {
        EntityManager em = new User().entityManager;
        if (em == null) {
            throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        }
        return em;
    }

    public static long count() {
        return entityManager().createQuery("SELECT COUNT(o) FROM User o", Long.class).getSingleResult();
    }

    public static List<User> findAll() {
        return entityManager().createQuery("SELECT o FROM User o", User.class).getResultList();
    }

    public static User find(Long id) {
        if (id == null) {
            return null;
        }
        return entityManager().find(User.class, id);
    }

    public static List<User> find(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM User o", User.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Role getRole() {
        return this.role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Tenant getTenant() {
        return this.tenant;
    }

    public void setTenant(Tenant tenant) {
        this.tenant = tenant;
    }

    public String getAccountUri() {
        return this.accountUri;
    }

    public void setAccountUri(String accountUri) {
        this.accountUri = accountUri;
    }

    public static TypedQuery<User> findByPasswordLike(String password) {
        if (password == null || password.length() == 0) {
            throw new IllegalArgumentException("The password argument is required");
        }
        password = password.replace('*', '%');
        if (password.charAt(0) != '%') {
            password = "%" + password;
        }
        if (password.charAt(password.length() - 1) != '%') {
            password = password + "%";
        }
        EntityManager em = User.entityManager();
        TypedQuery<User> q = em.createQuery("SELECT o FROM User AS o WHERE LOWER(o.password) LIKE LOWER(:password)", User.class);
        q.setParameter("password", password);
        return q;
    }

    public static User findByTenantAndByUsername(Tenant tenant, String username) {
        if (tenant == null) {
            throw new IllegalArgumentException("The tenant argument is required");
        }
        if (username == null || username.length() == 0) {
            throw new IllegalArgumentException("The username argument is required");
        }
        EntityManager em = User.entityManager();
        TypedQuery<User> q = em.createQuery("SELECT o FROM User AS o WHERE o.tenant = :tenant AND o.username = :username", User.class);
        q.setParameter("tenant", tenant);
        q.setParameter("username", username);
        return QueryUtil.getSingleResultOrNull(q);
    }

    public static User findLocalUserByTenant(Tenant tenant) {
        if (tenant == null) {
            throw new IllegalArgumentException("The tenant argument is required");
        }

        EntityManager em = User.entityManager();
        TypedQuery<User> q = em.createQuery("SELECT o FROM User AS o WHERE o.tenant = :tenant AND o.role <> :role", User.class);
        q.setParameter("tenant", tenant);
        q.setParameter("role", Role.GUEST); //we search for owner and admins here
        return QueryUtil.getFirstResultOrNull(q);
    }


    public static List<User> findAllByUsername(String username) {
        if (username == null || username.length() == 0) {
            throw new IllegalArgumentException("The username argument is required");
        }
        EntityManager em = User.entityManager();
        TypedQuery<User> q = em.createQuery("SELECT o FROM User AS o WHERE o.username = :username", User.class);
        q.setParameter("username", username);
        return q.getResultList();
    }

    public static TypedQuery<User> findByUsernameAndPassword(String username, String password) {
        if (username == null || username.length() == 0) {
            throw new IllegalArgumentException("The username argument is required");
        }
        if (password == null || password.length() == 0) {
            throw new IllegalArgumentException("The password argument is required");
        }
        EntityManager em = User.entityManager();
        TypedQuery<User> q = em.createQuery("SELECT o FROM User AS o WHERE o.username = :username AND o.password = :password", User.class);
        q.setParameter("username", username);
        q.setParameter("password", password);
        return q;
    }

    public static TypedQuery<User> findByUsernameLike(String username) {
        if (username == null || username.length() == 0) {
            throw new IllegalArgumentException("The username argument is required");
        }
        username = username.replace('*', '%');
        if (username.charAt(0) != '%') {
            username = "%" + username;
        }
        if (username.charAt(username.length() - 1) != '%') {
            username = username + "%";
        }
        EntityManager em = User.entityManager();
        TypedQuery<User> q = em.createQuery("SELECT o FROM User AS o WHERE LOWER(o.username) LIKE LOWER(:username)", User.class);
        q.setParameter("username", username);
        return q;
    }

    public static TypedQuery<User> findByRole(String role) {
        if (role == null || role.length() == 0) {
            throw new IllegalArgumentException("The role argument is required");
        }
        EntityManager em = User.entityManager();
        TypedQuery<User> q = em.createQuery("SELECT o FROM User AS o WHERE o.role = :role", User.class);
        q.setParameter("role", role);
        return q;
    }

    public static User findByAccountUri(String accountUri) {
        if (accountUri == null || accountUri.length() == 0) {
            throw new IllegalArgumentException("The accountUri argument is required");
        }
        EntityManager em = User.entityManager();
        TypedQuery<User> q = em.createQuery("SELECT o FROM User AS o WHERE o.accountUri = :accountUri", User.class);
        q.setParameter("accountUri", accountUri);
        return QueryUtil.getSingleResultOrNull(q);
    }

    

    /**
     * @return the uiLanguage
     */
    public String getUiLanguage() {
        return uiLanguage;
    }

    /**
     * @return the evaluationDataCapturingEnabled
     */
    public Boolean getEvaluationDataCapturingEnabled() {
        return evaluationDataCapturingEnabled;
    }

    /**
     * @return the evaluationId
     */
    public String getEvaluationId() {
        return evaluationId;
    }

    /**
     * @param uiLanguage the uiLanguage to set
     */
    public void setUiLanguage(String uiLanguage) {
        this.uiLanguage = uiLanguage;
    }

    /**
     * @param evaluationDataCapturingEnabled the evaluationDataCapturingEnabled
     * to set
     */
    public void setEvaluationDataCapturingEnabled(Boolean evaluationDataCapturingEnabled) {
        this.evaluationDataCapturingEnabled = evaluationDataCapturingEnabled;
    }

    /**
     * @return the emailAddress
     */
    public String getEmailAddress() {
        return emailAddress;
    }

    /**
     * @param emailAddress the emailAddress to set
     */
    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }
}
