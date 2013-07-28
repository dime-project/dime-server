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

package eu.dime.context.model.impl;

import java.util.*;

import eu.dime.context.model.api.IScope;
import eu.dime.context.model.api.IContextValue;
import eu.dime.context.model.api.IContextData;

/**
 * An immutable implementation of a (IScope->IContextValue) map. Maps the {@link
 * IScope} of a value to the corresponding {@link IContextValue}.
 *
 * No copy constructor or support for a {@link #clone} method is needed because
 * this class is designed to be <i>immutable</i>.
 */
public class ContextValueMap extends AbstractContextData implements Map
{
    public static final ContextValueMap EMPTY_CONTEXT_VALUE_MAP
	    = new ContextValueMap();

    private final Map hashMap;

    /**
     * Constructs an empty {@link ContextValueMap}. Private access to prohibit
     * external instantiation of empty maps. Rather, the final static
     * {@link #EMPTY_CONTEXT_VALUE_MAP} should be used.
     */
    private ContextValueMap()
    {
	this.hashMap = new HashMap();
    }

    /**
     * The main constructor for this class. It constructs a new instance of the
     * ContextValueMap using as input the entries in the given map. The
     * provided map must be of the form (String->IContextValue), otherwise an
     * exception is thrown.
     *
     * @param hashMap the input entries to be used for the construction of this
     * map
     */
    ContextValueMap(final Map hashMap)
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
	    if((!(key instanceof IScope)) ||
		    (!(value instanceof IContextValue)))
	    {
		throw new IllegalArgumentException("Invalid types of entries" +
			" in map: " + key.getClass() + "->" + value.getClass() + " (it should be IScope->IContextValue");
	    }
	    this.hashMap.put(key, value);
	}
    }

    ContextValueMap(final IContextData contextData)
    {
	if(contextData == null)
	{
	    throw new NullPointerException("Invalid null argument");
	}

	final Set keySet = contextData.keySet();
	this.hashMap = new HashMap(keySet.size());

	final Iterator iterator = keySet.iterator();
	while(iterator.hasNext())
	{
	    final Object key = iterator.next();
	    final Object value = hashMap.get(key);

	    if(key == null || value == null)
	    {
		throw new IllegalArgumentException("Invalid null entries in " +
			"contextData: " + key + "->" + value);
	    }
	    if((!(key instanceof IScope)) ||
		    (!(value instanceof IContextValue)))
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

    public boolean containsKey(final Object key)
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
		    String.class.toString());
	}

	return hashMap.containsKey(key);
    }

    public boolean containsValue(final Object value)
    {
	if(value == null)
	{
	    throw new IllegalArgumentException("Null value arguments are not " +
		    "allowed");
	}

	// assert value instanceof ContextValue
	if(!(value instanceof IContextValue))
	{
	    throw new IllegalArgumentException("Arguments must be of type: " +
		    IContextValue.class.toString());
	}

	return hashMap.containsValue(value);
    }

    public Object get(final Object key)
    {
	if(key == null)
	{
	    throw new IllegalArgumentException("Null key arguments are not " +
		    "allowed");
	}

	// assert key instanceof Scope
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
     * Returns a copy of the keys of the map. The returned set will contain
     * objects of the {@link IScope} type.
     *
     * @return a copy of the keys of the map
     */
    public Set keySet()
    {
	// copy keys in a new set to protect immutability
	return new HashSet(hashMap.keySet());
    }

    /**
     * Returns a copy of the values of the map. The returned collection will
     * contain objects of {@link IContextValue} type.
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

	final ContextValueMap otherContextValueMap
		= (ContextValueMap) otherObject;

	return hashMap.equals(otherContextValueMap.hashMap);
    }

    public String toString()
    {
	return "ContextValueMap(" + hashMap.toString() + ")";
    }

    public IContextValue getContextValue(IScope scopeKey)
    {
	if(scopeKey == null)
	{
	    throw new NullPointerException("Illegal null argument");
	}

	return (IContextValue) hashMap.get(scopeKey);
    }
}
