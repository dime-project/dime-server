package eu.dime.ps.datamining.crawler.data;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author Will Fleury
 */
public class EMF {

   
    private static EntityManagerFactory factory;


    public static synchronized EntityManager get() {
        if (factory == null)
            createEMF();

        return factory.createEntityManager();
    }

    static void createEMF() {
//        Map<String, Object> configOverrides = new HashMap<String, Object>();
//        configOverrides.put("hibernate.hbm2ddl.auto", "create-drop");
//        EntityManagerFactory factory =
//                Persistence.createEntityManagerFactory("DataMining", configOverrides);

        factory = Persistence.createEntityManagerFactory("DataMining");
    }

}
