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
    @javax.xml.bind.annotation.XmlElement(name = "userStatusFlag")
    private Integer userStatusFlag=0;



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

    /**
     * @return the userStatusFlag
     */
    public Integer getUserStatusFlag() {
        return userStatusFlag;
    }

    /**
     * @param userStatusFlag the userStatusFlag to set
     */
    public void setUserStatusFlag(Integer userStatusFlag) {
        this.userStatusFlag = userStatusFlag;
    }
}
