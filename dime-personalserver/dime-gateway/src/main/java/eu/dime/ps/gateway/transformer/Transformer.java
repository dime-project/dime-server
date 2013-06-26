package eu.dime.ps.gateway.transformer;

import java.util.Collection;

import org.ontoware.rdfreactor.schema.rdfs.Resource;


/**
 * Implementations of this interface are responsible for performing the 
 * serialization / deserialization of data to / from the digital me representations
 * for different external services such as as LinkedIn, Facebook etc. 
 * 
 * @author Will Fleury
 */
public interface Transformer {
	
	/**
	 * Maps the data to instance(s) of Resource. Use this when there is more than
	 * one Resource of a given type contained in the data. This method expects
	 * that the service data is in XML format. 
	 *
	 * @param xml the XML data to be transformed to dime types
	 * @param serviceIdentifier the identifier of which service the data originated
	 * @param path the Personal Server API path used to retrieve the data
	 * @param returnType type to cast result to. 
	 * @return one or more dime objects of the required type.
	 *
	 * @throws TransformerException if there is a problem during the deserialization
	 * @throws UnsupportedOperationException if there is no mapper available for
	 * the given serviceIdentifier/path combination
	 */
	public <T extends Resource> Collection<T> deserialize(String xml, String serviceIdentifier,
			String path, Class<T> returnType);
		
	/** 
	 * Performs the same as {@link #deserialize(String, String, String, Class) }
	 * except on collections.
	 * 
	 * @param <T> The return type to use for the result.	  
	 * @param xml the XML data to be transformed to dime types
	 * @param serviceIdentifier the identifier of which service the data originated
	 * @param path the Personal Server API path used to retrieve the data
	 * @param returnType type to cast result to. 
	 * @return a Collection of dime objects
	 * 
	 * @throws TransformerException if there is a problem during the deserialization
	 * @throws UnsupportedOperationException if there is no mapper available for  
	 * the given serviceIdentifier/path combination
	 */
	public <T extends Resource> Collection<Collection<T>> deserializeCollection(Collection<String> xml,
			String serviceIdentifier, String path, Class<T> returnType);
	
	/**
	 * This method transforms the digital me Resource to the appropriate xml 
	 * format for passing to a given service.
	 * 
	 * @param serviceIdentifier the identifier of the service the data is destined
	 * @param path the Personal Server API path used to data will be passed to
	 * @param resources the Resource to map to XML
	 * @return the serialized Resource object in XML format
	 * 
	 * @throws TransformerException if there is a problem during the deserialization
	 * @throws UnsupportedOperationException if there is no mapper available for 
	 * the particular serviceIdentifier/path combination
	 */
	public String serialize(Collection<? extends Resource> resources, String serviceIdentifier, String path);
	
	/**
	 * Performs the same as {@link #serialize(String, String, org.ontoware.rdfreactor.schema.rdfs.Resource) }
	 * except on collections.
	 * 
	 * @param serviceIdentifier the identifier of the service the data is destined
	 * @param path the Personal Server API path used to data will be passed to
	 * @param resources a collection of Resources to map to XML
	 * @return a collection of serialized Resource objects in XML format
	 * 
	 * @throws TransformerException if there is a problem during the deserialization
	 * @throws UnsupportedOperationException if there is no mapper available for 
	 * the particular serviceIdentifier/path combination
	 */
	public Collection<String> serializeCollection(Collection<Collection<? extends Resource>> resources, 
			String serviceIdentifier, String path);

}
