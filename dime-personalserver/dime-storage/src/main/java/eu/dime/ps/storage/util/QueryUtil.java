package eu.dime.ps.storage.util;

import java.util.List;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

/**
 * Some utility methods to help out with retrieving results from JPA queries.
 * 
 * @author Will Fleury
 */
public class QueryUtil {
    
    /**
     * Given the query, get a single result from it or return null if none 
     * exists. 
     * @param <T>
     * @param query
     * @param returnType
     * @return the result or null
     * @throws NonUniqueResultException if there is more than one result
     */
    public static <T> T getSingleResultOrNull(Query query, Class<T> returnType){
        List<T> results = query.getResultList();
        if (results.isEmpty()) {
            return null;
        }else if (results.size() == 1) {
            return results.get(0);
        }
        throw new NonUniqueResultException();
    }
    
    
    /**
     * Given the query, get a single result from it or return null if none 
     * exists. 
     * @param <T>
     * @param query
     * @return the result or null
     * @throws NonUniqueResultException if there is more than one result
     */
    public static <T> T getSingleResultOrNull(TypedQuery<T> query) {
        List<T> results = query.getResultList();
        if (results.isEmpty()) {
            return null;
            
        }else if (results.size() == 1) {
            return results.get(0);
        }
        throw new NonUniqueResultException();
    }

    /**
     * Given the query, get a single result from it or return null if none
     * exists.
     * @param <T>
     * @param query
     * @return the result or null
     * @throws NonUniqueResultException if there is more than one result
     */
    public static <T> T getFirstResultOrNull(TypedQuery<T> query) {
        List<T> results = query.getResultList();
        if (results.isEmpty()) {
            return null;
        }//else

        return results.get(0);

    }

}