/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.dime.ps.datamining.crawler.data;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Query;
import javax.persistence.Table;

/**
 * This class is responsible for all the data access to whatever storage 
 * is used. We do no know this yet and we are not resposible for this component
 * so the easiest thing is to create and Entity such as this and use it via
 * our own {@see javax.persistence.EntityManagerFactory }. 
 * 
 * When the time comes then we can get the EntityManager (and Factory) from
 * the appropriate component. Simply update the code in {@link EMF#get() } to
 * get it from the right place..
 *
 * @author Will Fleury
 */
@Entity
@Table (name="crawler")
public class CrawlerResource implements Serializable {
    private static final long serialVersionUID = 3266425788595597184L;
    
    @Id
    @Column (name="ID")
    private String hash;

    @Column (name="INSTANCES")
    private Integer instances;
    
    @Basic(fetch = FetchType.LAZY)
    @Lob
    @Column(name = "RESOURCE_DATA")
    private byte[] data;

    public CrawlerResource() {  }
    
    public CrawlerResource(String hash) {
        this.hash = hash;
        this.instances = 1;
    }


    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public Integer getInstances() {
        return instances;
    }

    public void setInstances(Integer instances) {
        this.instances = instances;
    }
    
    public void incrementInstances() {
        instances ++;
    }
    
    public void decrementInstances() {
        instances --;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CrawlerResource other = (CrawlerResource) obj;
        if (this.hash != other.hash && (this.hash == null || !this.hash.equals(other.hash))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return hash.hashCode();
    }
    
    @Override
    public String toString() {
        return "eu.dimi.datamining.crawlers.api.impl[hash=" + hash + "]";
    }
    
    
    public static CrawlerResource findResource(String hash) {
        return findResource(hash, false);
    }
    
    public static CrawlerResource findResource(String hash, boolean fetchData) {
        EntityManager em = null;

        try {
            em = EMF.get();

            //could use typesafe jpa2.0 stuff here if we want..
            Query query = em.createQuery("Select e from CrawlerResource r "
                    + "where r.hash=:hash");
            query.setParameter("hash", hash);

            CrawlerResource resource = JPAHelper.getSingleResultOrNull(query, CrawlerResource.class);
            
            
            if (resource != null && fetchData) {
                //force the fetch of the data.
                resource.getData();
            }
            
            return resource;
        } finally {
            em.close();
        }
    }
    
    public void merge() {
        EntityManager em = null;

        try {
            em = EMF.get();
            em.getTransaction().begin();
            em.merge(this);
            em.getTransaction().commit();
        }  finally {
            em.close();
        }
    }
    
    public void persist() {
        EntityManager em = null;

        try {
            em = EMF.get();
            em.getTransaction().begin();
            em.persist(this);
            em.getTransaction().commit();
        }  finally {
            em.close();
        }
    }

    public void remove() {
        EntityManager em = null;

        try {
            em = EMF.get();

            CrawlerResource attached = em.find(CrawlerResource.class, this.hash);

            em.getTransaction().begin();
            em.remove(attached);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }
}
