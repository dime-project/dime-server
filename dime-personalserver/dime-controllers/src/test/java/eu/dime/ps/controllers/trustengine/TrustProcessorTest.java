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

package eu.dime.ps.controllers.trustengine;

import static junit.framework.Assert.*;

import org.junit.Assert;
import org.junit.Test;

import eu.dime.ps.controllers.trustengine.exception.PrivacyValueNotValidException;
import eu.dime.ps.controllers.trustengine.exception.TrustValueNotValidException;
import eu.dime.ps.controllers.trustengine.impl.TrustProcessor;

/**
 * 
 * @author marcel
 *
 */
public class TrustProcessorTest {
	
	@Test
	public void testCalc3Atrust(){
		double trust = 0;
		 try {
			trust = TrustProcessor.calculateAdopted3AbasedDirectTrust(0, 0);
		} catch (Exception e) {
			Assert.assertTrue(e instanceof IllegalArgumentException);
		}
		trust = TrustProcessor.calculateAdopted3AbasedDirectTrust(1, 1);
		assertTrue(trust == 1);
		
		trust = TrustProcessor.calculateAdopted3AbasedDirectTrust(0, 1);
		assertTrue(trust == 0);
	}
	
	@Test
	public void testIsTrusted() throws Exception{
		boolean result = false;
		try {
			result = TrustProcessor.isTrusted(0.5, -0.5);
		} catch (Exception e) {
			assertTrue("Catched exception, but not the expected one.", (e instanceof PrivacyValueNotValidException));
		} 
		
		try {
			result = TrustProcessor.isTrusted(0.5, 2);
		} catch (Exception e) {
			assertTrue("Catched exception, but not the expected one.", (e instanceof PrivacyValueNotValidException));
		} 
		
		try {
			result = TrustProcessor.isTrusted(-0.5, 0.5);
		} catch (Exception e) {
			assertTrue("Catched exception, but not the expected one.", (e instanceof TrustValueNotValidException));
		} 
		
		try {
			result = TrustProcessor.isTrusted(2, 0.5);
		} catch (Exception e) {
			assertTrue("Catched exception, but not the expected one.", (e instanceof TrustValueNotValidException));
		}  
		
		result = TrustProcessor.isTrusted(0.7, 0.5);
		assertTrue(result);
		
		result = TrustProcessor.isTrusted(0.5, 0.9);
		assertFalse(result);
	}
	
	@Test
	public void testGetThreshold(){
		boolean result = false;
		
		try {
			result = TrustProcessor.getThreshold(0, 0.0);
		} catch (Exception e) {
			Assert.assertTrue(e instanceof IllegalArgumentException);
		}
		
		result = TrustProcessor.getThreshold(1, 1.0);
		assertFalse(result);
		
		result = TrustProcessor.getThreshold(1, 0.0);
		assertFalse(result);
		
		result = TrustProcessor.getThreshold(1, 0.01);
		assertFalse(result);
		
		result = TrustProcessor.getThreshold(1, 0.5);
		assertFalse(result);
		
		result = TrustProcessor.getThreshold(1, 0.99);
		assertFalse(result);
		
		result = TrustProcessor.getThreshold(100, 0.0);
		assertFalse(result);
		
		result = TrustProcessor.getThreshold(100, 0.01);
		assertFalse(result);
		
		result = TrustProcessor.getThreshold(100, 0.5);
		assertTrue(result);
		
		result = TrustProcessor.getThreshold(100, 0.99);
		assertTrue(result);
		
		result = TrustProcessor.getThreshold(100, 1.0);
		assertTrue(result);
	}
}
