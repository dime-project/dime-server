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

package eu.dime.ps.communications.requestbroker.controllers.evaluation;

import eu.dime.ps.controllers.UserManager;
import eu.dime.ps.controllers.evaluationtool.EvaluationManager;
import eu.dime.ps.semantic.model.ModelFactory;
import eu.dime.ps.storage.entities.User;
import eu.dime.ps.storage.manager.EntityFactory;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring-config/controllers-tests-context.xml")
public class PSEvaluationControllerTestIt extends Assert {

	private PSEvaluationController controller = new PSEvaluationController();
	private static final String said = "juan";

	protected ModelFactory modelFactory = new ModelFactory();

	public PSEvaluationControllerTestIt() {
		UserManager mockedUserManager = buildUserManager();
		EvaluationManager mockedEvaluationManager = buildEvaluationManager();
		controller.setUserManager(mockedUserManager);
		controller.setEvaluationManager(mockedEvaluationManager);

	}

	private EvaluationManager buildEvaluationManager() {
		// TODO Auto-generated method stub
		return null;
	}

	private UserManager buildUserManager() {
		User user = EntityFactory.getInstance().buildUser();
		UserManager mockedManager = mock(UserManager.class);

		when(mockedManager.getByUsername(said)).thenReturn(user);

		return mockedManager;

	}

	@Ignore
	@Test
	public void test() {
            //TODO add test
        }

}
