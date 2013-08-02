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
