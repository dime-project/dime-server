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

package eu.dime.ps.controllers.context.raw.data;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

public class ContextGroup {
	
	private Set<String> members;
	private String place = "";
	private double similarityThreshold = 0.7;
	
	public ContextGroup(Set members, String place) {
		this.members = members;
		this.place = place;
	}
	
	public Set<String> getMembers() {
		return members;
	}
	
	public void setMembers(Set members) {
		this.members = members;
	}
	
	public String getPlace() {
		return place;
	}
	
	public void setPlace(String place) {
		this.place = place;
	}
	
	@Override
	public boolean equals(Object object) {
		if (object == null) return false;
		if (this == object) return true;
		if (object instanceof ContextGroup) {
			ContextGroup group = (ContextGroup)object;
			if (!this.place.equalsIgnoreCase(group.getPlace())) return false;
			if (areMembersSimilar(this.getMembers(),group.getMembers())) return true;
			else return false;
		} else return false;
	}
	
	// setA is new, setB is old
	private boolean areMembersSimilar(Set<String> setA, Set<String> setB) {
		
		// if equals ==> similar
		if (setA.equals(setB)) return true;
	    
		Set<String> intersection = intersection(setA, setB);
		
		if (setB.size() >= setA.size()) {
			// group is reducing and commons members are >threshold than previous members 
			if (intersection.size() > (setB.size() * similarityThreshold)) return true;
		} else {
			// group is increasing and commons members are >threshold than new members
			if (intersection.size() > (setA.size() * similarityThreshold)) return true;
		}
		return false;
	}
	
	private <T> Set<T> union(Set<T> setA, Set<T> setB) {
	    Set<T> tmp = new TreeSet<T>(setA);
	    tmp.addAll(setB);
	    return tmp;
	}

	private <T> Set<T> intersection(Set<T> setA, Set<T> setB) {
	    Set<T> tmp = new TreeSet<T>();
	    for (T x : setA)
	      if (setB.contains(x))
	        tmp.add(x);
	    return tmp;
	}

	private <T> Set<T> difference(Set<T> setA, Set<T> setB) {
	    Set<T> tmp = new TreeSet<T>(setA);
	    tmp.removeAll(setB);
	    return tmp;
	}

	private <T> Set<T> symDifference(Set<T> setA, Set<T> setB) {
	    Set<T> tmpA;
	    Set<T> tmpB;

	    tmpA = union(setA, setB);
	    tmpB = intersection(setA, setB);
	    return difference(tmpA, tmpB);
	}

	private <T> boolean isSubset(Set<T> setA, Set<T> setB) {
	    return setB.containsAll(setA);
	}

	private <T> boolean isSuperset(Set<T> setA, Set<T> setB) {
	    return setA.containsAll(setB);
	  }
	
	@Override 
	public String toString() {
		String strMembers = "";
		Iterator<String> it = this.members.iterator();
		while (it.hasNext()) {
			strMembers += it.next();
			strMembers += ",";
		}
		if (strMembers.endsWith(",")) strMembers = strMembers.substring(0,strMembers.length()-1);
		return strMembers + this.place;
	}

}
