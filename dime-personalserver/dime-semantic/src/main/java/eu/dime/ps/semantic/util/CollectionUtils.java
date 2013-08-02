/*
* Copyright 2013 by the digital.me project (http://www.dime-project.eu).
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

package eu.dime.ps.semantic.util;

import java.util.Collection;
import java.util.Iterator;

import org.springframework.util.ObjectUtils;

public class CollectionUtils extends org.springframework.util.CollectionUtils {

	/**
	 * Return <code>true</code> if any element in '<code>candidates</code>' is
	 * contained in '<code>source</code>'; otherwise returns <code>false</code>.
	 * @param source the source Collection
	 * @param candidates the candidates to search for
	 * @return whether any of the candidates has been found
	 */
	public static boolean containsAny(Collection source, Object[] candidates) {
		if (isEmpty(source) || candidates.length == 0) {
			return false;
		}
		for (Object candidate : candidates) {
			if (source.contains(candidate)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Check whether the given Iterator contains any of the given elements.
	 * @param iterator the Iterator to check
	 * @param elements the elements to look for
	 * @return <code>true</code> if found, <code>false</code> else
	 */
	public static boolean containsAny(Iterator iterator, Object[] elements) {
		if (iterator != null) {
			while (iterator.hasNext()) {
				Object candidate = iterator.next();
				for (Object element : elements) {
					if (ObjectUtils.nullSafeEquals(candidate, element)) {
						return true;
					}
				}
			}
		}
		return false;
	}


}
