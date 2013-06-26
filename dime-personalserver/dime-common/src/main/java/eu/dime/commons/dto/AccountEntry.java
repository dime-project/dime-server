package eu.dime.commons.dto;

public class AccountEntry extends Entry {

    @javax.xml.bind.annotation.XmlElement(name = "role")
    private Integer role;
    @javax.xml.bind.annotation.XmlElement(name = "said")
    private String said;
    @javax.xml.bind.annotation.XmlElement(name = "username")
    private String username;
    @javax.xml.bind.annotation.XmlElement(name = "password")
    private String password;
    @javax.xml.bind.annotation.XmlElement(name = "enabled")
    private Boolean enabled;
    @javax.xml.bind.annotation.XmlElement(name = "uiLanguage")
    private String uiLanguage;
    @javax.xml.bind.annotation.XmlElement(name = "evaluationDataCapturingEnabled")
    private Boolean evaluationDataCapturingEnabled;
    @javax.xml.bind.annotation.XmlElement(name = "evaluationId")
    private String evaluationId;

    public AccountEntry() {
        super();
    }

    /**
     * @return the role
     */
    public Integer getRole() {
        return role;
    }

    /**
     * @return the said
     */
    public String getSaid() {
        return said;
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @return the enabled
     */
    public Boolean getEnabled() {
        return enabled;
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
     * @param role the role to set
     */
    public void setRole(Integer role) {
        this.role = role;
    }

    /**
     * @param said the said to set
     */
    public void setSaid(String said) {
        this.said = said;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @param enabled the enabled to set
     */
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * @param uiLanguage the uiLanguage to set
     */
    public void setUiLanguage(String uiLanguage) {
        this.uiLanguage = uiLanguage;
    }

    /**
     * @param evaluationDataCapturingEnabled the evaluationDataCapturingEnabled to set
     */
    public void setEvaluationDataCapturingEnabled(Boolean evaluationDataCapturingEnabled) {
        this.evaluationDataCapturingEnabled = evaluationDataCapturingEnabled;
    }

    /**
     * @param evaluationId the evaluationId to set
     */
    public void setEvaluationId(String evaluationId) {
        this.evaluationId = evaluationId;
    }
}
