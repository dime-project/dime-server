package eu.dime.context.model.impl;

import java.util.*;

import eu.dime.context.model.api.*;

import java.io.Serializable;

/**
 * An immutable implementation of a (IScope->IMetadatum) map. Maps the
 * {@link IScope} to the corresponding {@link IMetadata}.
 *
 * No copy constructor or support for a {@link #clone} method is needed because
 * this class is designed to be <i>immutable</i>.
 */
public class MetadataMap implements IMetadata, Map, Serializable
{
    public static final MetadataMap EMPTY_METADATA_MAP = new MetadataMap();

    private final Map hashMap;

    private MetadataMap()
    {
	this.hashMap = new HashMap();
    }

    /**
     * The main constructor for this class. It constructs a new instance of the
     * MetadataMap using as input the entries in the given map. The provided
     * map must be of the form (IScope->IMetadatum), otherwise an exception is
     * thrown.
     *
     * @param hashMap the input entries to be used for the construction of this
     * map
     */
    MetadataMap(final Map hashMap)
    {
	if(hashMap == null)
	{
	    throw new NullPointerException("Invalid null argument");
	}

	this.hashMap = new HashMap(hashMap.size());

	final Set keySet = hashMap.keySet();
	Iterator iterator = keySet.iterator();
	while(iterator.hasNext())
	{
	    final Object key = iterator.next();
	    final Object value = hashMap.get(key);

	    if(key == null || value == null)
	    {
		throw new IllegalArgumentException("Invalid null entries in " +
			"map: " + key + "->" + value);
	    }
	    if((!(key instanceof IScope)) || (!(value instanceof IMetadatum)))
	    {
		throw new IllegalArgumentException("Invalid types of entries" +
			" in map: " + key.getClass() + "->" + value.getClass());
	    }
	    this.hashMap.put(key, value);
	}
    }

    public int size()
    {
	return hashMap.size();
    }

    public boolean isEmpty()
    {
	return hashMap.isEmpty();
    }

    public boolean containsKey(Object key)
    {
	if(key == null)
	{
	    throw new IllegalArgumentException("Null key arguments are not " +
		    "allowed");
	}

	// assert key instanceof IScope
	if(!(key instanceof IScope))
	{
	    throw new IllegalArgumentException("Arguments must be of type: " +
		    IScope.class.toString());
	}

	return hashMap.containsKey(key);
    }

    public boolean containsValue(Object value)
    {
	if(value == null)
	{
	    throw new IllegalArgumentException("Null value arguments are not " +
		    "allowed");
	}

	// assert value instanceof IMetadatum
	if(!(value instanceof IMetadatum))
	{
	    throw new IllegalArgumentException("Arguments must be of type: " +
		    IMetadatum.class.toString());
	}

	return hashMap.containsValue(value);
    }

    public Object get(Object key)
    {
	if(key == null)
	{
	    throw new IllegalArgumentException("Null key arguments are not " +
		    "allowed");
	}

	// assert key instanceof String
	if(!(key instanceof IScope))
	{
	    throw new IllegalArgumentException("Arguments must be of type: " +
		    IScope.class.toString());
	}

	return hashMap.get(key);
    }

	public Object put(Object key, Object value)
    {
	throw new UnsupportedOperationException("Operation not supported in " +
		"immutable implementation of the map data-structure");
    }

    public Object remove(Object key)
    {
	throw new UnsupportedOperationException("Operation not supported in " +
		"immutable implementation of the map data-structure");
    }

    public void putAll(Map m)
    {
	throw new UnsupportedOperationException("Operation not supported in " +
		"immutable implementation of the map data-structure");
    }

    public void clear()
    {
	throw new UnsupportedOperationException("Operation not supported in " +
		"immutable implementation of the map data-structure");
    }

    /**
     * Returns a copy of the keys of the map. The returned set contains objects
     * of type {@link IScope}.
     *
     * @return a copy of the keys of the map
     */
    public Set keySet()
    {
	// copy keys in a new set to protect immutability
	return new HashSet(hashMap.keySet());
    }

    /**
     * Returns a copy of the values of the map. The returned collection
     * contains objects of type
     * {@link IMetadatum}.
     *
     * @return a copy of the values of the map
     */
    public Collection values()
    {
	// copy values in a new set to protect immutability
	return new HashSet(hashMap.values());
    }

    public Set entrySet()
    {
	throw new UnsupportedOperationException("Operation not supported in " +
		"immutable implementation of the map data-structure");
    }

    public int hashCode()
    {
	return hashMap.hashCode();
    }

    public boolean equals(Object otherObject)
    {
	if(otherObject == null) return false;

	if(this == otherObject) return true;

	if(otherObject.getClass() != this.getClass()) return false;

	final MetadataMap otherMetadataMap = (MetadataMap) otherObject;

	return hashMap.equals(otherMetadataMap.hashMap);
    }

    public String toString()
    {
	return "MetadataMap(" + hashMap.toString() + ")";
    }

    // From IMetadata

    public IValue getMetadatumValue(final IScope scopeKey)
    {
	final IMetadatum metadatum = getMetadatum(scopeKey);

	if(metadatum == null)
	{
	    return null;
	}

	return metadatum.getValue();
    }

    public IMetadatum getMetadatum(final IScope scopeKey)
    {
	if(scopeKey == null)
	{
	    throw new NullPointerException("Illegal null argument");
	}

	return (IMetadatum) this.get(scopeKey);
    }
}
