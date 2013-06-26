/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.dime.ps.storage.exception;

/**
 *
 * @author simon
 */
public class ReadOnlyValueChangedOnUpdate extends Exception{

    public ReadOnlyValueChangedOnUpdate(String message) {
        super(message);
    }

    
}
