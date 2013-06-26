package eu.dime.ps.storage.manager;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eu.dime.ps.storage.entities.HistoryCache;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/spring/ps-storage-test-applicationContext.xml")
public class EntityFactoryTest {

    private EntityFactory entityFactory;
    
    @Autowired
    public void setEntityFactory(EntityFactory entityFactory) {
        this.entityFactory = entityFactory;
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testGetEntity() {

	EntityFactory ef = EntityFactory.getInstance();

	HistoryCache ch = ef.buildHistoryCache();

	Assert.assertNotNull(ch);
	
	Assert.assertNotNull(HistoryCache.entityManager());

    }
}
