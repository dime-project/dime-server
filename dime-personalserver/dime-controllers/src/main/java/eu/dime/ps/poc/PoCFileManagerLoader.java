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

package eu.dime.ps.poc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.dime.ps.controllers.infosphere.manager.FileManager;

public class PoCFileManagerLoader {

	private static final Logger logger = LoggerFactory.getLogger(PoCFileManagerLoader.class);
	
	private FileManager fileManager;
	private String user;
	
	public PoCFileManagerLoader(FileManager fileManager, String user) {
		this.fileManager = fileManager;
		this.user = user;
	}
	
	public void init() {
		if (this.user.equalsIgnoreCase("anna")) {
			update("303d651e4c23e50b4cb0788e2c742ca6a3a75f67",
				"file:/Users/Anna/Documents/Media%20clipping/Architecture%20Summercamp/elpais_20110524.jpg",
				"elpais_20110524.jpg", this.user);
			update("4471625a6d9f32c11258a8afc792e30bcc43fa00",
				"file:/Users/Anna/Documents/Toronto%20Urban%20planning%202013-2020/Public%20transport%20systems/construction_plan_university_building.jpg",
				"construction_plan_university_building.jpg", this.user);
			update("ea2735f40b4badf055ce382112814645eecfb26f",
				"file:/Users/Anna/Documents/Toronto%20Urban%20planning%202013-2020/Social%20studies/sociodemographic_evolution_2012-2018.dot",
				"sociodemographic_evolution_2012-2018.dot", this.user);
		} else if (this.user.equalsIgnoreCase("juan")) {
			update("3024fcd6c979f5cc83fc8baa9bcc05907dc8425c",
				"file:/Users/Juan/Documents/Construction%20plans/MediaTIC%20Barcelona/project_offer_2006.xml",
				"project_offer_2006.xml", this.user);	
			update("c6fe2f8bce947b65e19929631749ca2bf018db20",
				"file:/Users/Juan/Documents/Construction%20plans/MediaTIC%20Barcelona/construction_plan_bridge_Brown.jpg",
				"construction_plan_bridge_Brown.jpg", this.user);
			update("b26002607508052d8b0563b301602f529bfe989a",
				"file:/Users/Juan/Documents/Construction%20plans/Tour%20First%20Paris/Construction_plan_presentation.ppt",
				"Construction_plan_presentation.ppt", this.user);
			update("ea2735f40b4badf055ce382112814645eecfb26f",
				"file:/Users/Juan/Documents/Construction%20plans/Tour%20First%20Paris/Tour_First_detailed_planning.dot",
				"Tour_First_detailed_planning.dot", this.user);
			update("303d651e4c23e50b4cb0788e2c742ca6a3a75f67",
				"file:/Users/Juan/Documents/Project%20portfolio/Phoenix%20Park%20Dublin/Phoenix_Park_CorporateOffices.jpg",
				"Phoenix_Park_CorporateOffices.jpg", this.user);
		} else if (this.user.equalsIgnoreCase("norbert")) {
			update("3024fcd6c979f5cc83fc8baa9bcc05907dc8425c",
				"file:/Users/Norbert/Documents/Open%20Home%20automation%20projects/HospitalBuilding_Berlin/project_budget_building_berlin.xml",
				"project_budget_building_berlin.xml", this.user);
			update("b26002607508052d8b0563b301602f529bfe989a",
				"file:/Users/Norbert/Documents/Open%20Home%20automation%20projects/ITCompany_HQ_Lisboa/Press_info_ITCompany_HQ_Lisboa.ppt",
				"Press_info_ITCompany_HQ_Lisboa.ppt", this.user);
			update("4471625a6d9f32c11258a8afc792e30bcc43fa00",
				"file:/Users/Norbert/Documents/Open%20Home%20automation%20projects/SmartHome_Helsinki/construction_plan_SmartHome_Helsinki.jpg",
				"construction_plan_SmartHome_Helsinki.jpg", this.user);
		}
	}
	
	private void add(String hash, String uri, String file, String user) {
		try {
			fileManager.add(uri, file, getClass().getClassLoader().getResourceAsStream("poc-files/"+user+"/"+file));
		} catch (Exception e) {
			logger.error("Cannot load file in FileManager", e);
		}
	}
	
	private void update(String hash, String uri, String file, String user) {
		try {
			fileManager.update(uri, getClass().getClassLoader().getResourceAsStream("poc-files/"+user+"/"+file));
		} catch (Exception e) {
			logger.error("Cannot load file in FileManager", e);
		}
	}
	
}
