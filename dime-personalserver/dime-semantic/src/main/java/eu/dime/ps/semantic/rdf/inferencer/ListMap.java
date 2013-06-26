package eu.dime.ps.semantic.rdf.inferencer;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class ListMap<K,V> {

	private ConcurrentHashMap<K, List<V>> data;

	public ListMap() { 
		data = new ConcurrentHashMap<K,List<V>>();
	}
	
	public V put(K key, V value) {
		List<V> list;
		if (data.containsKey(key)) {
			list = data.get(key);
		} else { 
			list = new CopyOnWriteArrayList<V>();
			data.put(key, list);
		}
		list.add(value);
		return null;
	}
    
    /**
     * remove the value of the key.
     * If key or value were not set, returns null
     * @param key the key 
     * @param value the value to remove
     * @return null or the removed value.
     */
	public V remove(K key, V value) {
	    List<V> list;
	    if (data.containsKey(key)) {
	        list = data.get(key);
	    } else { 
	        return null;
	    }
        if (list.remove(value))
            return value;
        else 
            return null;
	}

	/**
	 * Return the list for the given key, 
	 * if no such key an empty list is returned.
	 * @param key
	 * @return
	 */
	public List<V>get(K key) {
		if (data.containsKey(key))
			return data.get(key);
		return Collections.emptyList();
	}

	public boolean containsKey(K key) {
		return data.containsKey(key);
	}

	@Override
	public String toString() {
		return data.toString();
	}

	public void remove(K key) {
		data.remove(key);
	}
    
}
