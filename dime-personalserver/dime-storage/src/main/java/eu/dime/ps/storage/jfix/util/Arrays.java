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

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import eu.dime.ps.storage.jfix.functor.Functors;
import eu.dime.ps.storage.jfix.functor.Predicate;


/**
 * Common utilitity-methods to handle arrays.
 */
public class Arrays {

	/**
	 * Converts given collection into array of given class-type.
	 */
	public static <T> T[] cast(Collection<T> collection, Class<T> clazz) {
		return collection.toArray((T[]) java.lang.reflect.Array.newInstance(
				clazz, collection.size()));
	}

	/**
	 * Converts object-array into array of given class-type.
	 */
	public static <T> T[] cast(Object[] array, Class<T> clazz) {
		if (array == null) {
			return (T[]) java.lang.reflect.Array.newInstance(clazz, 0);
		}
		T[] newArray = (T[]) java.lang.reflect.Array.newInstance(clazz,
				array.length);
		for (int i = 0; i < array.length; i++) {
			newArray[i] = (T) array[i];
		}
		return newArray;
	}

	/**
	 * Appends given object to array and return typed array of given result
	 * class. Please note: append is only executed if object is not contained in
	 * array already.
	 */
	public static <T> T[] append(Object[] array, Object object,
			Class<T> resultClass) {
		if (!Arrays.contains(array, object)) {
			return Arrays.cast(addAll(array, object), resultClass);
		} else {
			return Arrays.cast(array, resultClass);
		}
	}

	/**
	 * Prepends given object to array and return typed array of given result
	 * class. Please note: prepend is only executed if object is not contained in
	 * array already.
	 */
	public static <T> T[] prepend(Object[] array, Object object,
			Class<T> resultClass) {
		if(array == null) {
			T[] result = (T[]) java.lang.reflect.Array.newInstance(resultClass, 1);
			result[0] = (T) object;
			return result;
		}
		if (!Arrays.contains(array, object)) {
			Object[] result = new Object[array.length + 1];
			result[0] = object;
			System.arraycopy(array, 0, result, 1, array.length);
			return Arrays.cast(result, resultClass);
		} else {
			return Arrays.cast(array, resultClass);
		}
	}

	/**
	 * Removes given object from array and return typed array of given result
	 * class.
	 */
	public static <T> T[] remove(Object[] array, Object object,
			Class<T> resultClass) {
		if (Arrays.contains(array, object)) {
			return Arrays.cast(removeElement(array, object), resultClass);
		} else {
			return Arrays.cast(array, resultClass);
		}
	}

	/**
	 * Returns array where all objects are filtered against given class. The
	 * resulting array only contains elements which are instances of given
	 * class.
	 */
	public static <E> E[] filter(Object[] array, final Class<E> clazz) {
		return Arrays.cast(Functors.filter(array, new Predicate<Object>() {
			public boolean test(Object obj) {
				return clazz.isAssignableFrom(obj.getClass());
			}
		}), clazz);
	}

	/**
	 * Checks if given object is contained in given array.
	 */
	public static boolean contains(Object[] array, Object object) {
		return Arrays.indexOf(array, object) != -1;
	}

	/**
	 * Returns index of given object in given array. If array doesn't contain
	 * object, -1 is returned.
	 */
	public static int indexOf(Object[] array, Object objectToFind) {
		if (array == null) {
			return -1;
		}
		if (objectToFind == null) {
			for (int i = 0; i < array.length; i++) {
				if (array[i] == null) {
					return i;
				}
			}
		} else if (array.getClass().getComponentType().isInstance(objectToFind)) {
			for (int i = 0; i < array.length; i++) {
				if (objectToFind.equals(array[i])) {
					return i;
				}
			}
		}
		return -1;
	}

	/**
	 * Reverses order in given array.
	 */
	public static <E> E[] reverse(E[] array) {
		if (array == null) {
			return null;
		}
		E[] b = array.clone();
		for (int left = 0, right = b.length - 1; left < right; left++, right--) {
			E temp = b[left];
			b[left] = b[right];
			b[right] = temp;
		}
		return b;
	}

	/**
	 * Converts given array into typed list.
	 */
	public static <T> List<T> asList(T[] array) {
		return java.util.Arrays.asList(array);
	}

	/**
	 * Converts given array into typed set.
	 */
	public static <T> Set<T> asSet(T[] array) {
		return new HashSet<T>(Arrays.asList(array));
	}

	/**
	 * Joins given array with given separator into a string.
	 */
	public static String join(Object[] array, String separator) {
		if (array == null) {
			return null;
		}
		if (array.length == 0) {
			return "";
		}
		StringBuilder sb = new StringBuilder(256);
		for (int i = 0; i < array.length; i++) {
			if (i != 0) {
				sb.append(separator);
			}
			if (array[i] != null) {
				sb.append(array[i]);
			}
		}
		return sb.toString();
	}

	private static <T> T[] addAll(T[] array1, T... array2) {
		if (array1 == null) {
			return array2 != null ? array2.clone() : null;
		} else if (array2 == null) {
			return array1 != null ? array1.clone() : null;
		}
		Class<?> type1 = array1.getClass().getComponentType();
		T[] result = (T[]) Array.newInstance(type1, array1.length
				+ array2.length);
		System.arraycopy(array1, 0, result, 0, array1.length);
		System.arraycopy(array2, 0, result, array1.length, array2.length);
		return result;
	}

	private static <T> T[] removeElement(T[] array, Object element) {
		int index = indexOf(array, element);
		if (index == -1) {
			return array != null ? array.clone() : null;
		}
		int length = array.length;
		if (index < 0 || index >= length) {
			throw new IndexOutOfBoundsException("Index: " + index
					+ ", Length: " + length);
		}
		Object result = Array.newInstance(array.getClass().getComponentType(),
				length - 1);
		System.arraycopy(array, 0, result, 0, index);
		if (index < length - 1) {
			System.arraycopy(array, index + 1, result, index, length - index
					- 1);
		}
		return (T[]) result;
	}

}
