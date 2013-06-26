package eu.dime.ps.dto;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Include  {

	
	private String saidSender;
	
	
	public List<String> groups;
		
	
	public List <HashMap<String,String>> persons;
	
	
	public List<String> services;
	
	

	public String getSaidSender() {
		return saidSender;
	}

	public void setSaidSender(String saidSender) {
		this.saidSender = saidSender;
	}

	public List<String> getGroups() {
		return groups;
	}

	public void setGroups(List<String> groups) {
		this.groups = groups;
	}

	

	public List<HashMap<String, String>> getPersons() {
		return persons;
	}

	public void setPersons(List<HashMap<String, String>> persons) {
		this.persons = persons;
	}

	public List<String> getServices() {
		return services;
	}

	public void setServices(List<String> services) {
		this.services = services;
	}
	
	public Include(String Sender){
		this.saidSender=Sender;
		
	}

	public Include() {		
		this.saidSender="";
		this.groups=new ArrayList<String>();
		this.services=new ArrayList<String>();
		this.persons= new ArrayList<HashMap<String,String>>();
		
	}	
	
	public void addGroup(String group){
		if(groups == null){
    	    groups = new ArrayList<String>();
    	}
		this.groups.add(group);		
	}
	
	public void addService(String service){
		if(services == null){
			services = new ArrayList<String>();
    	}
		this.services.add(service);		
	}
	
	public void addPerson(String personId,String saidReceiver){
		HashMap<String,String> person = new HashMap<String,String>();		
		if(persons == null){
			persons = new ArrayList<HashMap<String,String>>();
    	}
		person.put("personId", personId);
		person.put("saidReceiver", saidReceiver);
		persons.add(person);
	}
	
	public Include writeInclude(Resource resource){
		
		
		return this;
	}
	
	
}
