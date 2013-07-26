/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.dime.ps.gateway.userresolver.client;

import eu.dime.ps.gateway.exception.ServiceException;
import eu.dime.ps.gateway.exception.ServiceNotAvailableException;
import java.io.IOException;
import net.sf.json.JSONArray;

/**
 *
 * @author simon
 */
public interface DimeResolver {

    public String register(String token, String firstname, String surname, String nickname, String said) throws IOException;



}
