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

package eu.dime.ps.datamining;

import ie.deri.smile.extractor.ExtractionException;
import ie.deri.smile.extractor.MetadataExtractor;
import ie.deri.smile.extractor.impl.URLMetadataExtractor;
import ie.deri.smile.extractor.opengraph.OpenGraphMetadataExtractor;
import ie.deri.smile.rdf.ResourceModel;
import ie.deri.smile.vocabulary.OG;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.dime.ps.datamining.exceptions.DataMiningException;
import eu.dime.ps.datamining.util.LinkExtractor;
import eu.dime.ps.semantic.model.DLPOFactory;
import eu.dime.ps.semantic.model.ModelFactory;
import eu.dime.ps.semantic.model.dlpo.LivePost;
import eu.dime.ps.semantic.model.dlpo.Status;

public class LivePostDecomposer {

	private static final Logger logger = LoggerFactory.getLogger(LivePostDecomposer.class);

	private final DLPOFactory dlpoFactory;
	private final MetadataExtractor urlExtractor;
	private final MetadataExtractor opengraphExtractor;

	public LivePostDecomposer() {
		this.dlpoFactory = (new ModelFactory()).getDLPOFactory();
		this.urlExtractor = new URLMetadataExtractor();
		this.opengraphExtractor = new OpenGraphMetadataExtractor();
	}
	
	/**
	 * <p>Decomposes an instance of liveposts into several more specific
	 * dlpo:LivePost subclasses such as dlpo:ImagePost, dlpo:Checkin, etc.</p>
	 * 
	 * <p>Prior to do the decomposition, it analysis the textual content
	 * of the livepost in order to extract hyperlinks and named entities,
	 * used to determine the types of the subclasses (e.g. a link to an
	 * image is found, an instance of ImagePost will be created, and
	 * the link/image will be set as the dlpo:definingResource of the
	 * ImagePost instance.</p>
	 * 
	 * @param livepost a livepost instance to decompose
	 * @return a list containing the livepost itself, and all the liveposts
	 * extracted from it
	 * @throws DataMiningException
	 */
	public List<LivePost> decompose(LivePost livepost) throws DataMiningException {
		List<LivePost> liveposts = new ArrayList<LivePost>();

		if (!livepost.hasTextualContent()) {
			throw new DataMiningException("Livepost "+livepost.asResource()+" cannot be decomposed, no textual content is provided.");
		} else {
			String text = livepost.getAllTextualContent().next();
			Calendar timestamp = livepost.hasTimestamp() ? livepost.getTimestamp() : Calendar.getInstance();
			
			LivePost superPost = dlpoFactory.createLivePost(livepost.asURI());
			superPost.getModel().addAll(livepost.getModel().iterator());
			
			try {
				LivePost subPost = null;
				for (String link : LinkExtractor.extract(text, true)) {
					URL url = null;
					try {
						url = new URL(link);
					} catch (MalformedURLException e) {
						logger.error(link+" is not a valid URL in livepost "+livepost.asResource(), e);
						continue;
					}

					// all links are related resources in the 'super' livepost
					superPost.addRelatedResource(new URIImpl(link));

					ResourceModel linkMetadata = urlExtractor.extract(url);
					String type = linkMetadata.getString(OG.type);

					// unknown link types don't create a new livepost
					if (type == null) {
						continue;
					} else if ("audio".equals(type)) {
						subPost = dlpoFactory.createAudioPost();
					} else if ("image".equals(type)) {
						subPost = dlpoFactory.createImagePost();
					} else if ("video".equals(type)) {
						subPost = dlpoFactory.createVideoPost();
					}
					
					superPost.addIsComposedOf(subPost.asResource());

					subPost.setTimestamp(timestamp);
					subPost.setTextualContent(text);
					subPost.setDefiningResource(new URIImpl(link));
					subPost.setDefiningResource(new URIImpl(link));
					subPost.getModel().addAll(linkMetadata.getModel().iterator());

					// retrieve Open Graph metadata
					subPost.getModel().addAll(opengraphExtractor.extract(url).getModel().iterator());

					// add subPost to the list of results
					liveposts.add(subPost);
				}
			} catch (ExtractionException e) {
				logger.error("Error occurred while decomposing livepost "+livepost.asResource(), e);
			}
			
			// always add a Status post
			Status status = dlpoFactory.createStatus();
			status.setTimestamp(timestamp);
			status.setTextualContent(text);
			superPost.addIsComposedOf(status);
			liveposts.add(status);
			
			// adds also main/super livepost
			liveposts.add(superPost);
		}
		
		return liveposts;
	}
	
}
