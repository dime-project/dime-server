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

package eu.dime.commons.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlElement;

@javax.xml.bind.annotation.XmlRootElement
@javax.xml.bind.annotation.XmlAccessorType(XmlAccessType.FIELD)
public class UserRegister {
	
	public final static String CLIENT_WEB = "0";

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
