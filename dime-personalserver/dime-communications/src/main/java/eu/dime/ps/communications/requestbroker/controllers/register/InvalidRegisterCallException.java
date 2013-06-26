/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.dime.ps.communications.requestbroker.controllers.register;

/**
 *
 * @author simon
 */
public class InvalidRegisterCallException extends Exception{

    public InvalidRegisterCallException(String message) {
        super(message);
    }

}
