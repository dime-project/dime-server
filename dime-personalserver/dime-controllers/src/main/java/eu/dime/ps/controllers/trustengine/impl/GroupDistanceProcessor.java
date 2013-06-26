package eu.dime.ps.controllers.trustengine.impl;

import java.util.HashSet;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdfreactor.schema.rdfs.Resource;

import eu.dime.ps.semantic.model.pimo.Agent;
import eu.dime.ps.semantic.model.pimo.Person;
import eu.dime.ps.semantic.model.pimo.PersonGroup;

/**
 * 
 * @author marcel
 *
 */
public class GroupDistanceProcessor {

	public static double getGroupDistance(PersonGroup groupA, PersonGroup groupB){
		ClosableIterator<Resource> resourcesA = groupA.getAllIsRelated();
		ClosableIterator<Resource> resourcesB = groupB.getAllIsRelated();
		ClosableIterator<Agent> membersA = groupA.getAllMembers();
		ClosableIterator<Agent> membersB = groupB.getAllMembers();
		
		HashSet<Resource> tmp_persA = new HashSet<Resource>();
		HashSet<Resource> tmp_persB = new HashSet<Resource>();

		HashSet<Resource> tmp_resA = new HashSet<Resource>();
		HashSet<Resource> tmp_resB = new HashSet<Resource>();
		
		HashSet<Resource> union_persons = new HashSet<Resource>();
		HashSet<Resource> union_resources = new HashSet<Resource>();
		
		HashSet<Resource> symDiff_persons = new HashSet<Resource>();
		HashSet<Resource> symDiff_resources = new HashSet<Resource>();
				
		while (resourcesA.hasNext()) {
			Resource resource = resourcesA.next();
			tmp_resA.add(resource);

		}
		while (resourcesB.hasNext()) {
			Resource resource = resourcesB.next();
			tmp_resB.add(resource);
		}
		
		//union
		union_resources.addAll(tmp_resA);
		union_resources.addAll(tmp_resB);
		// resolve symmetric difference
		symDiff_resources.addAll(tmp_resA);
		symDiff_resources.removeAll(tmp_resB);
		tmp_resB.removeAll(tmp_resA);
		symDiff_resources.addAll(tmp_resB);
		
		while (membersA.hasNext()) {
			Resource resource = membersA.next();
			tmp_persA.add(resource);
		}
		while (membersB.hasNext()) {
			Resource resource = membersB.next();
			tmp_persB.add(resource);
		}
		// union
		union_persons.addAll(tmp_persA);
		union_persons.addAll(tmp_persB);
		// resolve symmetric difference
		symDiff_persons.addAll(tmp_persA);
		symDiff_persons.removeAll(tmp_persB);
		tmp_persB.removeAll(tmp_persA);
		symDiff_persons.addAll(tmp_persB);
		
		if (union_resources.isEmpty() || union_persons.isEmpty()){
			return 1;
		}
		double a = symDiff_resources.size()/union_resources.size();
		double x = (double) symDiff_persons.size();
		double y = (double) union_persons.size();
		double b = x/y;
		double dist = Math.sqrt((a*a) + (b*b));
		
		return dist/Math.sqrt(2);
	}
}
