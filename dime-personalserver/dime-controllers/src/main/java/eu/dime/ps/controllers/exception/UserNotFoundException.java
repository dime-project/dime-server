/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.dime.ps.controllers.exception;

/**
 *
 * @author simon
 */
public class UserNotFoundException extends Exception{

    public UserNotFoundException(String message) {
        super(message);
    }

    
}
