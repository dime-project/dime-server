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
