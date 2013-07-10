package eu.dime.commons.dto;

import javax.xml.bind.annotation.XmlAccessType;

@javax.xml.bind.annotation.XmlRootElement
@javax.xml.bind.annotation.XmlAccessorType(XmlAccessType.FIELD)
public class SAccount extends SAdapter {

    private String serviceadapterguid = null;

    public SAccount() {
        super();

        this.type = "account";
    }

    /**
     * @return the serviceadapterguid
     */
    public String getServiceadapterguid() {
        return serviceadapterguid;
    }

    /**
     * @param serviceadapterguid the serviceadapterguid to set
     */
    public void setServiceadapterguid(String serviceadapterguid) {
        this.serviceadapterguid = serviceadapterguid;
    }
}
