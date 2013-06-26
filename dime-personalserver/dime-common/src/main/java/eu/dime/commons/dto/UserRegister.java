package eu.dime.commons.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlElement;

@javax.xml.bind.annotation.XmlRootElement
@javax.xml.bind.annotation.XmlAccessorType(XmlAccessType.FIELD)
public class UserRegister {

    @javax.xml.bind.annotation.XmlElement(name = "username")
    private String username;
    @javax.xml.bind.annotation.XmlElement(name = "password")
    private String password;
    @javax.xml.bind.annotation.XmlElement(name = "nickname")
    private String nickname;
    @javax.xml.bind.annotation.XmlElement(name = "firstname")
    private String firstname;
    @javax.xml.bind.annotation.XmlElement(name = "lastname")
    private String lastname;
    @javax.xml.bind.annotation.XmlElement(name = "checkbox_agree")
    private Boolean checkbox_agree;
    @XmlElement
    private String emailAddress;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }


    public void setPassword(String password) {
        this.password = password;
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

    /**
     * @return the checkbox_agree
     */
    public Boolean getCheckbox_agree() {
        return checkbox_agree;
    }

    /**
     * @param checkbox_agree the checkbox_agree to set
     */
    public void setCheckbox_agree(Boolean checkbox_agree) {
        this.checkbox_agree = checkbox_agree;
    }
}
