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

package eu.dime.ps.semantic.vocabulary;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.ontoware.rdf2go.model.Syntax;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.dime.ps.semantic.exception.OntologyInvalidException;
import eu.dime.ps.semantic.util.URLInputSource;

/**
 * Contains a list of default ontologies to be managed by the system, and
 * provides convenient methods to retrieve them from a local or remote location.
 * 
 * @author Ismael Rivera
 */
public class DefaultOntologies {

	private static final Logger logger = LoggerFactory.getLogger(DefaultOntologies.class);

	private static List<Ontology> defaults = new ArrayList<Ontology>();

	public static Ontology RDF = new Ontology(
			"http://www.w3.org/1999/02/22-rdf-syntax-ns",
			"vocabularies/rdf/rdf.rdf", Syntax.RdfXml, true);
	public static Ontology RDFS = new Ontology(
			"http://www.w3.org/2000/01/rdf-schema",
			"vocabularies/rdfs/rdf-schema.rdf", Syntax.RdfXml, true);
	public static Ontology DAO = new Ontology(
			"http://www.semanticdesktop.org/ontologies/2011/10/05/dao",
			"http://www.semanticdesktop.org/ontologies/2011/10/05/dao/metadata",
			"vocabularies/dao/dao.trig", Syntax.Trig, true);
	public static Ontology DCON = new Ontology(
			"http://www.semanticdesktop.org/ontologies/2011/10/05/dcon",
			"http://www.semanticdesktop.org/ontologies/2011/10/05/dcon/metadata",
			"vocabularies/dcon/dcon.trig", Syntax.Trig, true);
	public static Ontology DDO = new Ontology(
			"http://www.semanticdesktop.org/ontologies/2011/10/05/ddo",
			"http://www.semanticdesktop.org/ontologies/2011/10/05/ddo/metadata",
			"vocabularies/ddo/ddo.trig", Syntax.Trig, true);
	public static Ontology DLPO = new Ontology(
			"http://www.semanticdesktop.org/ontologies/2011/10/05/dlpo",
			"http://www.semanticdesktop.org/ontologies/2011/10/05/dlpo/metadata",
			"vocabularies/dlpo/dlpo.trig", Syntax.Trig, true);
	public static Ontology DUHO = new Ontology(
			"http://www.semanticdesktop.org/ontologies/2011/10/05/duho",
			"http://www.semanticdesktop.org/ontologies/2011/10/05/duho/metadata",
			"vocabularies/duho/duho.trig", Syntax.Trig, true);
	public static Ontology NAO = new Ontology(
			"http://www.semanticdesktop.org/ontologies/2007/08/15/nao",
			"http://www.semanticdesktop.org/ontologies/2007/08/15/nao/metadata",
			"vocabularies/nao/nao.trig", Syntax.Trig, true);
	public static Ontology NCAL = new Ontology(
			"http://www.semanticdesktop.org/ontologies/2007/04/02/ncal",
			"http://www.semanticdesktop.org/ontologies/2007/04/02/ncal/metadata",
			"vocabularies/ncal/ncal.trig", Syntax.Trig, true);
	public static Ontology NCO = new Ontology(
			"http://www.semanticdesktop.org/ontologies/2007/03/22/nco",
			"http://www.semanticdesktop.org/ontologies/2007/03/22/nco/metadata",
			"vocabularies/nco/nco.trig", Syntax.Trig, true);
	public static Ontology NDO = new Ontology(
			"http://www.semanticdesktop.org/ontologies/2010/04/30/ndo",
			"http://www.semanticdesktop.org/ontologies/2010/04/30/ndo/metadata",
			"vocabularies/ndo/ndo.trig", Syntax.Trig, true);
	public static Ontology NEXIF = new Ontology(
			"http://www.semanticdesktop.org/ontologies/2007/05/10/nexif",
			"http://www.semanticdesktop.org/ontologies/2007/05/10/nexif/metadata",
			"vocabularies/nexif/nexif.trig", Syntax.Trig, true);
	public static Ontology NFO = new Ontology(
			"http://www.semanticdesktop.org/ontologies/2007/03/22/nfo",
			"http://www.semanticdesktop.org/ontologies/2007/03/22/nfo/metadata",
			"vocabularies/nfo/nfo.trig", Syntax.Trig, true);
	public static Ontology NID3 = new Ontology(
			"http://www.semanticdesktop.org/ontologies/2007/05/10/nid3",
			"http://www.semanticdesktop.org/ontologies/2007/05/10/nid3/metadata",
			"vocabularies/nid3/nid3.trig", Syntax.Trig, true);
	public static Ontology NIE = new Ontology(
			"http://www.semanticdesktop.org/ontologies/2007/01/19/nie",
			"http://www.semanticdesktop.org/ontologies/2007/01/19/nie/metadata",
			"vocabularies/nie/nie.trig", Syntax.Trig, true);
	public static Ontology NMM = new Ontology(
			"http://www.semanticdesktop.org/ontologies/2009/02/19/nmm",
			"http://www.semanticdesktop.org/ontologies/2009/02/19/nmm/metadata",
			"vocabularies/nmm/nmm.trig", Syntax.Trig, true);
	public static Ontology NMO = new Ontology(
			"http://www.semanticdesktop.org/ontologies/2007/03/22/nmo",
			"http://www.semanticdesktop.org/ontologies/2007/03/22/nmo/metadata",
			"vocabularies/nmo/nmo.trig", Syntax.Trig, true);
	public static Ontology NRL = new Ontology(
			"http://www.semanticdesktop.org/ontologies/2007/08/15/nrl",
			"http://www.semanticdesktop.org/ontologies/2007/08/15/nrl/metadata",
			"vocabularies/nrl/nrl.trig", Syntax.Trig, true);
	public static Ontology NSO = new Ontology(
			"http://www.semanticdesktop.org/ontologies/2009/11/08/nso",
			"http://www.semanticdesktop.org/ontologies/2009/11/08/nso/metadata",
			"vocabularies/nso/nso.trig", Syntax.Trig, true);
	public static Ontology NUAO = new Ontology(
			"http://www.semanticdesktop.org/ontologies/2010/01/25/nuao",
			"http://www.semanticdesktop.org/ontologies/2010/01/25/nuao/metadata",
			"vocabularies/nuao/nuao.trig", Syntax.Trig, true);
	public static Ontology PIMO = new Ontology(
			"http://www.semanticdesktop.org/ontologies/2007/11/01/pimo",
			"http://www.semanticdesktop.org/ontologies/2007/11/01/pimo/metadata",
			"vocabularies/pimo/pimo.trig", Syntax.Trig, true);
	public static Ontology TMO = new Ontology(
			"http://www.semanticdesktop.org/ontologies/2008/05/20/tmo",
			"http://www.semanticdesktop.org/ontologies/2008/05/20/tmo/metadata",
			"vocabularies/tmo/tmo.trig", Syntax.Trig, true);
	public static Ontology PPO = new Ontology(
			"http://vocab.deri.ie/ppo",
			"vocabularies/ppo/ppo.rdf", Syntax.RdfXml, true);
	public static Ontology OG = new Ontology(
			"http://ogp.me/ns",
			"vocabularies/og/ogp.me.n3", Syntax.Turtle, true);
	public static Ontology DRMO = new Ontology(
			"http://www.semanticdesktop.org/ontologies/2012/03/06/drmo",
			"http://www.semanticdesktop.org/ontologies/2012/03/06/drmo/metadata",
			"vocabularies/drmo/drmo.trig", Syntax.Trig, true);	
	
    public static class Ontology {
    	String uri;
    	String metadataUri;
        String filename;
        Syntax syntax;
        
        public Ontology(String uri, String metadataUri, String filename, Syntax syntax, boolean isDefault) {
            super();
            this.uri = uri;
            this.metadataUri = metadataUri;
            this.filename = filename;
            this.syntax = syntax;
            if (isDefault) {
            	defaults.add(this);
            }
        }

        public Ontology(String uri, String filename, Syntax syntax, boolean isDefault) {
        	this(uri, null, filename, syntax, isDefault);
        }
        
        public Ontology(String uri, String filename, Syntax syntax) {
        	this(uri, filename, syntax, false);
        }

        /**
         * By default, an ontology is assumed to be serialised in RDF/XML.
         * 
         * @param uri
         * @param filename
         */
        public Ontology(String uri, String filename) {
        	this(uri, filename, Syntax.RdfXml);
        }
        
		public URI getUri() {
            return new URIImpl(uri);
        }
        
		public URI getMetadataUri() {
			if (metadataUri == null) {
				return null;
			}
            return new URIImpl(metadataUri);
		}
		
        public Syntax getSyntax() {
        	return syntax;
        }
        
        public InputStream getInputStream() throws OntologyInvalidException {
            InputStream result = DefaultOntologies.class.getClassLoader().getResourceAsStream(filename);
            if (result == null) {
                throw new OntologyInvalidException("cannot load input stream from resource: " + filename);
            }
            return result;
        }
        
        public URL getLocation() {
        	try {
				return getClass().getResource(filename).toURI().toURL();
			} catch (MalformedURLException e) {
				logger.error("cannot retrieve ontology location: " + e, e);
			} catch (URISyntaxException e) {
				logger.error("cannot retrieve ontology location: " + e, e);
			}
        	return null;
        }

    }
    
	/**
     * A public ontology is an ontology which you can find and access online.
	 * 
	 * @author Ismael Rivera
	 */
    public static class PublicOntology extends Ontology {
    	private String downloadUri;
    	
    	public PublicOntology(String uri, String downloadUri, Syntax syntax) {
    		this(uri, downloadUri, syntax, false);
    	}
		
    	public PublicOntology(String uri, String downloadUri, Syntax syntax, boolean isDefault) {
			super(uri, null, syntax, isDefault);
			this.downloadUri = downloadUri;
		}
		
		@Override
		public InputStream getInputStream() {
			try {
				URLInputSource source = new URLInputSource(new URL(downloadUri), syntax.getMimeType());
				return source.getInputStream();
			} catch (IOException e) {
				logger.error("Cannot load "+this+": "+e.getLocalizedMessage(), e);
			}

			return null;
        }
		
		@Override
		public URL getLocation() {
			try {
				return new URL(downloadUri);
			} catch (MalformedURLException e) {
				logger.error(downloadUri+" is not a valid URL: "+e.getLocalizedMessage(), e);
			}
			return null;
		}
		
		@Override
		public String toString() {
			return "Default Web Ontology downloaduri="+downloadUri;
		}
    	
    }

	/**
	 * Returns the default ontologies
	 * 
	 * @return the list of default ontologies
	 */
	public static List<Ontology> getDefaults() {
		return defaults;
	}

	/**
	 * Returns true if the ontology identified by the passed URI is a default
	 * ontology.
	 * 
	 * @param ontologyUri a ontology URI
	 * @return true, if this is a default ontology
	 */
	public static boolean containsOntologyUri(URI ontologyUri) {
		for (Ontology o : defaults) {
			if (o.uri.equals(ontologyUri.toString())) {
				return true;
			}
		}
		return false;
	}
	
}
