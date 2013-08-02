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

package eu.dime.ps.controllers.context.raw;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import eu.dime.ps.controllers.context.raw.data.ContextGroup;

public class ContextGroupServiceTest {
	
	@Test
	public void testEqualsGroup() {
		
		Set<String> members1 = new HashSet<String>();
		Set<String> members2 = new HashSet<String>();
		
		String situation = "@work";
		
		members1.add("user1");
		members1.add("user2");
		
		members2.add("user1");
		members2.add("user2");
		
		ContextGroup oldGroup = new ContextGroup(members1,situation);
		ContextGroup newGroup = new ContextGroup(members2,situation);
		
		assertTrue(newGroup.equals(oldGroup));
	}
	
	public void testReducedMembersGroup() {
		
		// most members left
		Set<String> members1 = new HashSet<String>();
		Set<String> members2 = new HashSet<String>();
		
		String situation = "@work";
		
		members1.add("user1");
		members1.add("user2");
		members1.add("user3");
		members1.add("user4");
		members1.add("user5");
		members1.add("user6");
		members1.add("user7");
		
		members2.add("user1");
		members2.add("user2");
		members2.add("user3");
		members2.add("user4");
		members2.add("user6");
		members2.add("user7");
		
		ContextGroup oldGroup = new ContextGroup(members1,situation);
		ContextGroup newGroup = new ContextGroup(members2,situation);
		
		assertTrue(newGroup.equals(oldGroup));
	}
	
	@Test
	public void testReducedMoreMembersGroup() {
		
		// most members left
		Set<String> members1 = new HashSet<String>();
		Set<String> members2 = new HashSet<String>();
		
		String situation = "@work";
		
		members1.add("user1");
		members1.add("user2");
		members1.add("user3");
		members1.add("user4");
		members1.add("user5");
		members1.add("user6");
		members1.add("user7");
		
		members2.add("user1");
		members2.add("user2");
		
		ContextGroup oldGroup = new ContextGroup(members1,situation);
		ContextGroup newGroup = new ContextGroup(members2,situation);
		
		assertTrue(!newGroup.equals(oldGroup));
	}
	
	@Test
	public void testIncreasedMembersGroup() {
		
		Set<String> members1 = new HashSet<String>();
		Set<String> members2 = new HashSet<String>();
		
		String situation = "@work";
		
		members2.add("user1");
		members2.add("user2");
		members2.add("user3");
		members2.add("user4");
		
		members1.add("user1");
		members1.add("user2");
		members1.add("user3");
		members1.add("user4");
		members1.add("user5");
		
		ContextGroup oldGroup = new ContextGroup(members2,situation);
		ContextGroup newGroup = new ContextGroup(members1,situation);
		
		assertTrue(newGroup.equals(oldGroup));
	}
	
	@Test
	public void testIncreasedMoreMembersGroup() {
		
		Set<String> members1 = new HashSet<String>();
		Set<String> members2 = new HashSet<String>();
		
		String situation = "@work";
		
		members2.add("user1");
		members2.add("user2");
		
		members1.add("user1");
		members1.add("user2");
		members1.add("user3");
		members1.add("user4");
		members1.add("user5");
		members1.add("user6");
		members1.add("user7");
		
		ContextGroup oldGroup = new ContextGroup(members2,situation);
		ContextGroup newGroup = new ContextGroup(members1,situation);
		
		assertTrue(!newGroup.equals(oldGroup));
	}
	
	@Test
	public void testChangedMembersGroup() {
		
		// one has left, one has arrived
		Set<String> members1 = new HashSet<String>();
		Set<String> members2 = new HashSet<String>();
		
		String situation = "@work";
		
		members1.add("user1");
		members1.add("user2");
		members1.add("user3");
		members1.add("user4");
		members1.add("user5");
		members1.add("user6");
		members1.add("user7");
		
		members2.add("user1");
		members2.add("user2");
		members2.add("user4");
		members2.add("user5");
		members2.add("user6");
		members2.add("user7");
		members2.add("user8");
		
		ContextGroup oldGroup = new ContextGroup(members1,situation);
		ContextGroup newGroup = new ContextGroup(members2,situation);
		
		assertTrue(newGroup.equals(oldGroup));
	}
	
	@Test
	public void testChangedSituationGroup() {
		
		Set<String> members1 = new HashSet<String>();
		Set<String> members2 = new HashSet<String>();
		
		String situation1 = "@work";
		String situation2 = "@home";
		
		members1.add("user1");
		members1.add("user2");
		
		members2.add("user1");
		members2.add("user2");
		
		ContextGroup oldGroup = new ContextGroup(members1,situation1);
		ContextGroup newGroup = new ContextGroup(members2,situation2);
		
		assertTrue(!newGroup.equals(oldGroup));
	}

}
