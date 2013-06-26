package eu.dime.ps.semantic.service;

import java.util.Collection;
import java.util.Map;

import org.ontoware.rdfreactor.schema.rdfs.Resource;

public interface ResourceMatchingService {

	<T extends Resource> Map<String, String> match(T resource);
	
	<T extends Resource> Map<String, String> match(Collection<T> resources);

}
