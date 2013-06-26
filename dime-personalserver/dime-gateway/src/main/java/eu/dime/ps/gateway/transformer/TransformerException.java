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
