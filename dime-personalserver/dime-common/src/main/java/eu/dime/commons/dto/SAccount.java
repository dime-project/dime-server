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

import javax.xml.bind.annotation.XmlAccessType;

@javax.xml.bind.annotation.XmlRootElement
@javax.xml.bind.annotation.XmlAccessorType(XmlAccessType.FIELD)
public class SAccount extends SAdapter {

    private String serviceadapterguid = null;

    private Boolean isActive=true;

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

    /**
     * @return the isActive
     */
    public Boolean getIsActive() {
        return isActive;
    }

    /**
     * @param isActive the isActive to set
     */
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
}
