package eu.dime.ps.gateway.transformer.impl;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.ontoware.rdfreactor.schema.rdfs.Resource;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import eu.dime.ps.gateway.transformer.Transformer;
import eu.dime.ps.semantic.model.ModelFactory;
import eu.dime.ps.semantic.model.pimo.Location;

public class SocialRecsTransformer implements Transformer {
	
	Logger logger = Logger.getLogger(SocialRecsTransformer.class);
	private ModelFactory modelFactory;
	private XPathFactory xpf = XPathFactory.newInstance();
	private String xpathExpr = "//*[local-name() = 'parS']";
	
	public SocialRecsTransformer() {}

	public SocialRecsTransformer(ModelFactory modelFactory) {
		this.modelFactory = modelFactory;
	}

	@Override
	public <T extends Resource> Collection<T> deserialize(String xml,
			String serviceIdentifier, String path, Class<T> returnType) {
		
		Collection<T> resources = new ArrayList<T>();
		
		InputSource is = new InputSource(new StringReader(xml));
		
		try {
			
			NodeList parSNodes = (NodeList)xpf.newXPath().evaluate(xpathExpr,is,XPathConstants.NODESET);
		
			for (int i=0; i<parSNodes.getLength(); i++) {
				String placeId = null;
				String topn = null;
				String cf = null;
				String vote = null;
				Node parS = parSNodes.item(i);
				NodeList parList = parS.getChildNodes();
				for (int j=0; j<parList.getLength(); j++) {
					Node par = parList.item(j);
					String content = par.getTextContent();
					if (content != null && !content.equalsIgnoreCase("\n")) {
						NamedNodeMap map = par.getAttributes();
						Node n = map.getNamedItem("n");
						String value = n.getNodeValue();
						if (value.equalsIgnoreCase("item_id")) placeId = content;
						else if (value.equalsIgnoreCase("recs:vote")) vote = content;
						else if (value.equalsIgnoreCase("recs:engine:topn")) topn = content;
						else if (value.equalsIgnoreCase("recs:engine:cf")) cf = content;
					}
				}
				Location l = createLocation(placeId,vote,topn,cf);
				resources.add((T) l);
			}
		
		} catch (XPathExpressionException e) {
			logger.error(e.toString());
		}
		
		return resources;
		
		/*Placemark p1 = modelFactory.getNAOFactory().createResource(Placemark.class);
		p1.setPrefLabel("place1");
		p1.setScore(new Float(0.6));
		resources.add((T) p1);
		Placemark p2 = modelFactory.getNAOFactory().createResource(Placemark.class);
		p2.setPrefLabel("place2");
		p1.setScore(new Float(0.8));
		resources.add((T) p2);
		return resources;*/
	}

	private Location createLocation(String placeId, String vote,
			String topn, String cf) {
		Location l = modelFactory.getNAOFactory().createResource(Location.class);
		if (placeId != null) l.setPrefLabel(placeId);
		else return null;
		if (vote == null && topn == null && cf == null) {
			logger.error("No votes came from SocRecService for item " + placeId);
			return null;
		}
		if (vote != null) l.setNumericRating(normalizeVoteToInteger(vote));
		if (cf != null) l.setScore(normalizeVoteToFloat(cf));
		else if (topn != null) l.setScore(normalizeVoteToFloat(topn));
		return l;
	}

	private Float normalizeVoteToFloat(String vote) {
		try {
			Float f = Float.parseFloat(vote);
			return new Float(f/100);
		} catch (NumberFormatException ex) {
			logger.error(ex.toString());
			return new Float(0);
		}
		
	}
	
	private Integer normalizeVoteToInteger(String vote) {
		try {
			double d = Double.parseDouble(vote);
			int i = (int)d;
			return new Integer(i);
		} catch (NumberFormatException ex) {
			logger.error(ex.toString());
			return 0;
		}
	}

	@Override
	public <T extends Resource> Collection<Collection<T>> deserializeCollection(
			Collection<String> xml, String serviceIdentifier, String path,
			Class<T> returnType) {
		
		return null;
	}

	@Override
	public String serialize(Collection<? extends Resource> resources,
			String serviceIdentifier, String path) {
		
		return null;
	}

	@Override
	public Collection<String> serializeCollection(
			Collection<Collection<? extends Resource>> resources,
			String serviceIdentifier, String path) {
		
		return null;
	}

}
