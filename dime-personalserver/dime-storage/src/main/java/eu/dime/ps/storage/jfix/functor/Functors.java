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
package eu.dime.ps.storage.jfix.functor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Functors {

	public static <T> T[] filter(T[] array, Predicate<T> predicate) {
		if (array == null) {
			return null;
		}
		List<T> result = new ArrayList<T>(array.length);
		for (T obj : array) {
			if (obj != null && predicate.test(obj)) {
				result.add(obj);
			}
		}
		return result.toArray((T[]) java.lang.reflect.Array.newInstance(array
				.getClass().getComponentType(), result.size()));
	}

	public static <T> List<T> filter(Collection<T> collection,
			Predicate<T> predicate) {
		if (collection == null) {
			return new ArrayList<T>();
		}
		List<T> result = new ArrayList<T>(collection.size());
		for (T obj : collection) {
			if (obj != null && predicate.test(obj)) {
				result.add(obj);
			}
		}
		return result;
	}

	public static <F, T> T[] transform(F[] array, Function<F, T> function) {
		if (array == null || array.length == 0) {
			return null;
		}
		List<T> result = new ArrayList<T>(array.length);
		for (F element : array) {
			result.add(function.evaluate(element));
		}
		return result.toArray((T[]) java.lang.reflect.Array.newInstance(
				((T) result.get(0)).getClass(), result.size()));
	}

	public static <F, T> List<T> transform(Collection<F> collection,
			Function<F, T> function) {
		if (collection == null) {
			return new ArrayList<T>();
		}
		List<T> result = new ArrayList<T>(collection.size());
		for (F element : collection) {
			result.add(function.evaluate(element));
		}
		return result;
	}

}
