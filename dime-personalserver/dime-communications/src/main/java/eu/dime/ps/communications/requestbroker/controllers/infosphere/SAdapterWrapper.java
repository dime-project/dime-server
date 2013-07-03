package eu.dime.ps.communications.requestbroker.controllers.infosphere;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;

import org.ontoware.rdf2go.model.node.URI;

import eu.dime.commons.dto.SAdapterSetting;
import eu.dime.ps.dto.Resource;
import eu.dime.ps.semantic.model.dao.Account;


@javax.xml.bind.annotation.XmlRootElement
@javax.xml.bind.annotation.XmlAccessorType(XmlAccessType.FIELD)
public class SAdapterWrapper extends Resource {

	public SAdapterWrapper() {
	}
	
	public SAdapterWrapper(Account account, URI asURI) {
		super(account, asURI);
	}

	public void setSettings(List<SAdapterSetting> settings) {
		Iterator<SAdapterSetting> iter = settings.iterator();
		ArrayList<LinkedHashMap<String, String>> setMap = new ArrayList<LinkedHashMap<String, String>>();
		ArrayList<LinkedHashMap<String, Object>> ret = new ArrayList<LinkedHashMap<String, Object>>();
		
		while (iter.hasNext()) {
			LinkedHashMap<String, Object> lm = new LinkedHashMap<String, Object>();
			SAdapterSetting setting = iter.next();
			lm.put("name", setting.getName());
			lm.put("imageUrl", setting.getImageUrl());
			lm.put("mandatory", setting.getMandatory());
			if (setting.getValue() != null)
				lm.put("value", setting.getValue().toString());
			lm.put("type", setting.getType());
			ret.add(lm);
		}
		this.put("settings", ret);
	}
	
	public List<SAdapterSetting> getSettings() {
		  List<SAdapterSetting> settings = new ArrayList<SAdapterSetting>();
		  ArrayList<LinkedHashMap<String, Object>> arraySettings = (ArrayList<LinkedHashMap<String, Object>>) this.get("settings");
		  for (LinkedHashMap<String, Object> linkedHashMap : arraySettings) {
			 SAdapterSetting setting = new SAdapterSetting();
			 setting.setGuid((String)linkedHashMap.get("guid"));
			 setting.setImageUrl((String)linkedHashMap.get("imageUrl"));
			 setting.setMandatory((String)linkedHashMap.get("mandatory").toString());
			 setting.setName((String)linkedHashMap.get("name"));
			 setting.setType((String)linkedHashMap.get("type"));
			 setting.setValue((String)linkedHashMap.get("value").toString());
			 settings.add(setting);
		  }
		  return settings;
	}
}
