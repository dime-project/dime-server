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

package eu.dime.ps.storage.dtos;

import static org.junit.Assert.*;

import org.junit.Test;

import eu.dime.commons.dto.SAdapter;
import eu.dime.commons.dto.SAdapterSetting;

public class SAdapterTest {
	
	private SAdapter adapter;
	
	private void init() {
		this.adapter = new SAdapter();
		this.adapter.addSetting(new SAdapterSetting("s1", true, SAdapterSetting.STRING, "s1"));
		this.adapter.addSetting(new SAdapterSetting("s2", false, SAdapterSetting.BOOLEAN, "true"));
	}

	@Test
	public void testExportSettings() {
		init();
		String expected = "name=s1##fieldtype=string##mandatory=true##value=s1###name=s2##fieldtype=boolean##mandatory=false##value=true";
		String settings=this.adapter.exportSettings();
		assertEquals(settings,expected);
	}

	@Test
	public void testImportSettingsString() {
		init();
		// /^(\\\"[^\"]*\\\"|.*?)=(\\\"[^\"]*\\\"|.*?)$/
		String data = "name=i1##type=string##mandatory=true##value=value1###name=i2##type=boolean##mandatory=false##value=true";
		this.adapter.importSettings(data);
		assertTrue(this.adapter.getSetting("i1").equals("value1"));
		assertTrue(this.adapter.getSetting("i2").equals("true"));
	}

	@Test
	public void testUpdateSetting() {
		init();
		this.adapter.updateSetting("s1", "new");
		assertTrue(this.adapter.getSetting("s1").equals("new"));
	}

}
