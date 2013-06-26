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
