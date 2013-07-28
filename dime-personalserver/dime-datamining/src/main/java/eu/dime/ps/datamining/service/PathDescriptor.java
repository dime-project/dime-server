package eu.dime.ps.datamining.service;

import org.ontoware.rdfreactor.schema.rdfs.Resource;

/**
 * This class holds the information needed when retrieving information from 
 * services in a type safe manner.. 
 * 
 * @author Will Fleury
 */
public class PathDescriptor {
    
    /**
     * This is the path value as specified in the personal server API.
     */
    protected String path;
    
    /**
     * This is the return type to use when performing the transformation etc for 
     * the specified path..
     */
    protected Class<? extends Resource> returnType;
    
    
    /**
     * Constructs and default PathDescriptor with path of null and returnType 
     * of {@see Resource}.
     */
    public PathDescriptor() {
        this (null);
    }
    
    
    /**
     * Constructs a PathDescriptor with the given path and default 
     * returnType of {@see Resource}.
     * 
     * @param path the path value as specified in the personal server API
     */
    public PathDescriptor(String path) {
        this (path, Resource.class);
    } 
    
    
    /**
     * Constructs a PathDescriptor with the given path returnType.
     * 
     * @param path the path value as specified in the personal server API
     * @param returnType the return type to use
     */
    public PathDescriptor(String path, Class<? extends Resource> returnType) {
        this.path = path;
        this.returnType = returnType;
    }

    
    /**
     * Returns the path value
     * 
     * @return the personal server API path
     */
    public String getPath() {
        return path;
    }

    
    /**
     * Sets the path value
     * 
     * @param path the personal server API path
     */
    public void setPath(String path) {
        this.path = path;
    }

    
    /**
     * Returns the return type to use for objects received from the specified path
     * 
     * @return the return type
     */
    public Class<? extends Resource> getReturnType() {
        return returnType;
    }

    
    /**
     * Sets the return type to be used 
     * 
     * @param returnType  the return type to use
     */
    public void setReturnType(Class<? extends Resource> returnType) {
        this.returnType = returnType;
    }
    
    

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PathDescriptor other = (PathDescriptor) obj;
        if ((this.path == null) ? (other.path != null) : !this.path.equals(other.path)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + (this.path != null ? this.path.hashCode() : 0);
        return hash;
    }
}
