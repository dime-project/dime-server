package eu.dime.ps.gateway.service;

public class MediaType extends javax.ws.rs.core.MediaType {

    /** "application/ld+json" */
    public final static String APPLICATION_JSONLD = "application/ld+json";
    /** "application/ld+json" */
    public final static MediaType APPLICATION_JSONLD_TYPE = new MediaType("application","ld+json");

    public MediaType(String type, String subtype) {
    	super(type, subtype);
    }
    
}
