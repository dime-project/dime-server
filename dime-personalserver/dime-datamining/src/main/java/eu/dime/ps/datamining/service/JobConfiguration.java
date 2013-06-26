package eu.dime.ps.datamining.service;

import org.ontoware.rdfreactor.schema.rdfs.Resource;

public class JobConfiguration {
	
	private String cronSchedule;
	private String path;
	private Class<? extends Resource> type;

	public JobConfiguration() {}

	public JobConfiguration(String path, Class<? extends Resource> type, String cronSchedule) {
		this.path = path;
		this.type = type;
		this.cronSchedule = cronSchedule;
	}

	public String getCronSchedule() {
		return cronSchedule;
	}

	public void setCronSchedule(String cronSchedule) {
		this.cronSchedule = cronSchedule;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
	
	public Class<? extends Resource> getType() {
		return type;
	}
	
	public void setType(Class<? extends Resource> type) {
		this.type = type;
	}

}
