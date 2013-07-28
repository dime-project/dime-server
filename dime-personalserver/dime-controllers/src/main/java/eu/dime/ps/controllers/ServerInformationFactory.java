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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.dime.ps.controllers;

import eu.dime.commons.dto.ServerInformation;
import java.io.IOException;
import java.util.Properties;
import org.springframework.core.io.support.PropertiesLoaderUtils;

/**
 *
 * @author simon
 */
public class ServerInformationFactory {
    private static Properties properties = null;

    private static Properties getProps() throws IOException{
        if (properties==null){
            properties = PropertiesLoaderUtils.loadAllProperties("services.properties");
        }
        return properties;
    }


    public static ServerInformation getServerInformation() throws IOException{
        ServerInformation result = new ServerInformation();

        Properties myProps=getProps();

        result.setName(myProps.getProperty("GLOBAL_SERVER_NAME"));
        result.setImageUrl(myProps.getProperty("GLOBAL_SERVER_LOGO"));
        result.setAffiliation(myProps.getProperty("GLOBAL_SERVER_AFFILIATION"));
        result.setBaseUrl(myProps.getProperty("GLOBAL_SERVER_BASEURL"));
        result.setIpAddress(myProps.getProperty("GLOBAL_IPADDRESS"));
        
        return result;
    }
}
