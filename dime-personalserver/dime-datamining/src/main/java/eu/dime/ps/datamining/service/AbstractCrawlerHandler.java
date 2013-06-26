/*
 * Copyright (c) 2011 DERI, NUIG 
 */
package eu.dime.ps.datamining.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import org.ontoware.rdfreactor.schema.rdfs.Resource;

/**
 * This class will contain utility methods which are useful to all implementations
 * of the CrawlerHandler.
 * 
 * @author Will Fleury
 */
public abstract class AbstractCrawlerHandler implements CrawlerHandler {
    
    
    /**
     * This is a utility method which can be used to extract the proper types
     * for the returned resources map in a type safe manner. 
     * 
     * @param <T>
     * @param resources the resources for all path descriptors
     * @param descriptor the path descriptor 
     * @param returnType the return type to use and also match against
     * @return the typed collection with only matching types included.
     * 
     * @throws IllegalArgumentException if the {@see PathDescriptor#getReturnType()} 
     * does not match the type argument.
     */
    protected <T extends Resource> Collection<T> extractTypedCollection(
            Map<PathDescriptor, Collection<? extends Resource>> resources, 
            PathDescriptor descriptor,
            Class<T> returnType) {
        
        if (!descriptor.getReturnType().equals(returnType)) {
            throw new IllegalArgumentException("Return type (" + returnType.getName() + 
                    " and PathDescriptor type (" + descriptor.getReturnType().getName() + ")" +
                    " must match!!!");
        }
        
        Collection<T> dest = new ArrayList<T>();
        for (Resource r : resources.get(descriptor)) {
            if (returnType.isInstance(r)) 
                dest.add(returnType.cast(r));
        }
        return dest;
    }
}
