/**
 * 
 */
package eu.dime.ps.gateway.policy;

import static org.junit.Assert.*;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author Sophie.Wrobel
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring-config/storage-loading-tests-context.xml")
public class PolicyManagerTest {

	/**
	 * Test method for {@link eu.dime.ps.controllers.service.policy.PolicyManagerImpl#setAdapterPolicy(java.lang.String, java.lang.String, java.lang.String)}.
	 * Test method for {@link eu.dime.ps.controllers.service.policy.PolicyManagerImpl#setGlobalPolicy(java.lang.String, java.lang.String)}.
	 */
        @Ignore
	@Test
	public void testPolicySettings() {
		
		// Instantiate policy manager
		PolicyManagerImpl policyManager = PolicyManagerImpl.getInstance();
		
		// Set global policy
		policyManager.setGlobalPolicy("TESTINT", "123");
		policyManager.setGlobalPolicy("TESTSTRING", "TestString");
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
	@Ignore
	public void testPolicyRegistration() {
		PolicyManagerImpl policyManager = PolicyManagerImpl.getInstance();
		//policyManager.registerPolicyPlugin(plugin);
	}

}
