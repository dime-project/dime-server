/*
    Copyright (C) 2010 maik.jablonski@gmail.com

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package eu.dime.ps.storage.jfix.util;

import java.util.Collection;

/**
 * Common Validation-Utilities.
 */
public class Validations {

	/**
	 * Checks if given object is empty.
	 * 
	 * Checks if object is null or collection is empty or array is length == 0
	 * or trimmed string-representation of object is empty.
	 */
	public static boolean isEmpty(Object o) {
		if (o == null) {
			return true;
		}
		if (o instanceof Collection) {
			return ((Collection) o).isEmpty();
		}
		if (o.getClass().isArray()) {
			return ((Object[]) o).length == 0;
		}
		return "".equals(String.valueOf(o).trim());
	}

	/**
	 * Checks if given object is not empty.
	 */
	public static boolean isNotEmpty(Object o) {
		return !isEmpty(o);
	}
	
	/**
	 * Returns true if given objects are not null and equal.
	 */
	public static boolean equals(Object o1, Object o2) {
		return o1 != null && o2 != null && o1.equals(o2);
	}

}
