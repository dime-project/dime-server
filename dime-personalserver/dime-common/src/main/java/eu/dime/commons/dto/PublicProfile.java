package eu.dime.commons.dto;

import javax.xml.bind.annotation.XmlAccessType;

@javax.xml.bind.annotation.XmlRootElement
@javax.xml.bind.annotation.XmlAccessorType(XmlAccessType.FIELD)
public class PublicProfile {

    @javax.xml.bind.annotation.XmlElement(name="nickname")
    private String nickname;
    @javax.xml.bind.annotation.XmlElement(name="name")
    private String name;
    @javax.xml.bind.annotation.XmlElement(name="surname")
    private String surname;
    @javax.xml.bind.annotation.XmlElement(name="said")
    private String said;
    
    
    public PublicProfile(){
	super();
    }
    
    public String getNickname() {
        return nickname;
    }
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getSurname() {
        return surname;
    }
    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getSaid() {
        return said;
    }

    public void setSaid(String said) {
        this.said = said;
    }
    

    

}
