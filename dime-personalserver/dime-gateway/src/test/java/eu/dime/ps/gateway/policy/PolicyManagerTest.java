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

/**
 * 
 */
package eu.dime.ps.gateway.policy;

import static org.junit.Assert.*;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author Sophie.Wrobel
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring-config/storage-loading-tests-context.xml")
public class PolicyManagerTest {
	
	
	@Autowired
	private PolicyStoreImpl policyStore;
	
	/**
	 * Test method for {@link eu.dime.ps.controllers.service.policy.PolicyManagerImpl#setAdapterPolicy(java.lang.String, java.lang.String, java.lang.String)}.
	 * Test method for {@link eu.dime.ps.controllers.service.policy.PolicyManagerImpl#setGlobalPolicy(java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testPolicySettings() {
		
		// Instantiate policy manager
		PolicyManagerImpl policyManager = PolicyManagerImpl.getInstance();
		policyManager.setPolicyStore(policyStore);
		
		// Set global policy
		policyManager.setGlobalPolicy("TESTINT", "123");
		policyManager.setGlobalPolicy("TESTSTRING", "TestString");
		policyManager.setAdapterPolicy("TESTINT", "ADAPTERTEST", null);
		policyManager.setAdapterPolicy("TESTSTRING", "ADAPTERTEST", null);
		assertTrue("Testing global policy: getPolicyInteger(param, null): incorrect value detected.", 
				policyManager.getPolicyInteger("TESTINT", null).intValue() == 123);
		assertTrue("Testing global policy: getPolicyString(param, null): incorrect value detected.", 
				policyManager.getPolicyString("TESTSTRING", null).equals("TestString"));
		assertTrue("Testing global policy: getPolicyInteger(param, adapter): incorrect value detected.", 
				policyManager.getPolicyInteger("TESTINT", "ADAPTERTEST").intValue() == 123);
		assertTrue("Testing global policy: getPolicyString(param, adapter): incorrect value detected.", 
				policyManager.getPolicyString("TESTSTRING", "ADAPTERTEST").equals("TestString"));
		
		// Override global policy with adapter policy
		policyManager.setAdapterPolicy("TESTINT", "ADAPTERTEST", "456");
		policyManager.setAdapterPolicy("TESTSTRING", "ADAPTERTEST", "TestAdapterString");
		assertTrue("Testing global policy: getPolicyInteger(param, null): incorrect value detected.", 
				policyManager.getPolicyInteger("TESTINT", null).intValue() == 123);
		assertTrue("Testing global policy: getPolicyString(param, null): incorrect value detected.", 
				policyManager.getPolicyString("TESTSTRING", null).equals("TestString"));
		assertTrue("Testing adapter policy: getPolicyInteger(param, adapter): incorrect value detected.", 
				policyManager.getPolicyInteger("TESTINT", "ADAPTERTEST").intValue() == 456);
		assertTrue("Testing adapter policy: getPolicyString(param, adapter): incorrect value detected.", 
				policyManager.getPolicyString("TESTSTRING", "ADAPTERTEST").equals("TestAdapterString"));
		
		// Test independent adapter policy
		policyManager.setAdapterPolicy("TESTINTIND", "ADAPTERTEST", "789");
		policyManager.setAdapterPolicy("TESTSTRINGIND", "ADAPTERTEST", "TestIndAdapterString");
		assertTrue("Testing global policy: getPolicyInteger(param, null): incorrect value detected.", 
				policyManager.getPolicyInteger("TESTINTIND", null) == null);
		assertTrue("Testing global policy: getPolicyString(param, null): incorrect value detected.", 
				policyManager.getPolicyString("TESTSTRINGIND", null) == null);
		assertTrue("Testing adapter policy: getPolicyInteger(param, adapter): incorrect value detected.", 
				policyManager.getPolicyInteger("TESTINTIND", "ADAPTERTEST").intValue() == 789);
		assertTrue("Testing adapter policy: getPolicyString(param, adapter): incorrect value detected.", 
				policyManager.getPolicyString("TESTSTRINGIND", "ADAPTERTEST").equals("TestIndAdapterString"));
	}
	

	/**
	 * Test method for {@link eu.dime.ps.controllers.service.policy.PolicyManagerImpl#setAdapterPolicy(java.lang.String, java.lang.String, java.lang.String)}.
	 * Test method for {@link eu.dime.ps.controllers.service.policy.PolicyManagerImpl#setGlobalPolicy(java.lang.String, java.lang.String)}.
	 */
	public void testPolicyRegistration() {
		PolicyManagerImpl policyManager = PolicyManagerImpl.getInstance();
		//policyManager.registerPolicyPlugin(plugin);
	}

}
