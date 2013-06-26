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
