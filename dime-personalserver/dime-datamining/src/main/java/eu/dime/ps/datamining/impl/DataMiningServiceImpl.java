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

package eu.dime.ps.datamining.impl;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Syntax;

import eu.dime.commons.datamining.CrawlData;
import eu.dime.ps.datamining.DataMiningService;
import eu.dime.ps.datamining.exceptions.DataMiningException;
import eu.dime.ps.semantic.service.impl.PimoService;
import eu.dime.ps.semantic.util.StringUtils;

// TODO Ismael: this class is not being used, it should probably need to be removed.
public class DataMiningServiceImpl implements DataMiningService {

//	private SemanticApi semanticApi;
//	
//	public void setSemanticApi(SemanticApi semanticApi) {
//		this.semanticApi = semanticApi;
//	}
	
	@Override
	public void addCrawlData(CrawlData data) throws DataMiningException {
		Model rModel = null;
		try {
			Syntax syntax = Syntax.forMimeType(data.getMetadata().getMimeType());
			rModel = RDF2Go.getModelFactory().createModel().open();
			rModel.readFrom(IOUtils.toInputStream(data.getMetadata().getContent(), "UTF-8"), syntax);
//			semanticApi.addCrawledResource(new URIImpl(data.getUri()), rModel);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (rModel != null) {
				rModel.close();
			}
		}
	}

	@Override
	public void updateCrawlData(CrawlData data) throws DataMiningException {
		Model rModel = null;
		try {
			Syntax syntax = Syntax.forMimeType(data.getMetadata().getMimeType());
			rModel = RDF2Go.getModelFactory().createModel().open();
			rModel.readFrom(IOUtils.toInputStream(data.getMetadata().getContent(), "UTF-8"), syntax);
//			semanticApi.updateCrawledResource(new URIImpl(data.getUri()), rModel);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (rModel != null) {
				rModel.close();
			}
		}
	}

	@Override
	public void removeCrawlData(String uri) throws DataMiningException {
//		semanticApi.removeCrawledResource(new URIImpl(uri));
	}

	@Override
	public boolean existData(String uri) {
		// TODO should also check the type of the resource, and perhaps
		// if it came from other crawls, or not...
//		return semanticApi.exists(new URIImpl(uri));
		return false;
	}

	@Override
	public boolean existDataByHash(String hash) {
		String query = StringUtils.strjoinNL(
				PimoService.SPARQL_PREAMBLE,
				"ASK {",
				"  ?f a nie:DataObject .",
				"  ?f nfo:hasHash ?hash .",
				"  ?hash nfo:hashValue \""+hash+"\" .",
				"}");
//		return semanticApi.sparqlAsk(query);
		return false;
	}

	@Override
	public String getHash(String uri) throws DataMiningException {
//		FileDataObject fdo;
//		try {
//			fdo = semanticApi.get(uri, FileDataObject.class);
//			return fdo.getAllHash().next().getHValue();
//		} catch (NotFoundException e) {
//			throw new DataMiningException("cannot retrieve hash for "+uri+": "+e, e);
//		} catch (NullPointerException e) {
//			throw new DataMiningException("cannot retrieve hash, "+uri+" does not contain any hash information.", e);
//		}
		return null;
	}

}
