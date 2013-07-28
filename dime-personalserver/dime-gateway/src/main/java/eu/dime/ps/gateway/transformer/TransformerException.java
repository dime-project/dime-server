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

package eu.dime.ps.gateway.transformer;

/**
 * This exception is generated whenever there is an error performing the 
 * lifting (deserialization) or lowering (serialization) of data to / from 
 * the digital me rdf representation to the service representation (xml,json).
 * 
 * I extend RuntimeExcpeiton as its cleaner. If it use is well documented then 
 * there should not be an issue. I haven't seen anywhere in the guidelines that
 * checked exceptions are preferred to unchecked..
 * 
 * @author Will Fleury
 */
public class TransformerException extends RuntimeException {
    
    public TransformerException() {  super();  }
    
    public TransformerException(Throwable e) { super(e); }
    
    public TransformerException(String msg) { super(msg); }
    
    public TransformerException(String msg, Throwable e) { super(msg, e); }
}
