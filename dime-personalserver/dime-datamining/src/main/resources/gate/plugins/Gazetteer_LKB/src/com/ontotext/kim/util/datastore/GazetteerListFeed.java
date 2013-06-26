package com.ontotext.kim.util.datastore;

import gate.creole.ResourceInstantiationException;
import gate.creole.gazetteer.GazetteerList;
import gate.creole.gazetteer.GazetteerNode;
import gate.creole.gazetteer.LinearDefinition;
import gate.creole.gazetteer.LinearNode;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.log4j.Logger;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.model.impl.URIImpl;

import com.ontotext.kim.client.query.KIMQueryException;
import com.ontotext.kim.client.semanticrepository.QueryResultListener;
import com.ontotext.kim.client.semanticrepository.QueryResultListener.Feed;

/**
 * @author mnozchev
 * 
 */
public class GazetteerListFeed implements Feed {

  public static final String DEF_EXTENSION = "*.def";

  private static Logger log = Logger.getLogger(GazetteerListFeed.class);
  
  private final File dictionaryPath;

  public GazetteerListFeed(File dictionaryPath) {
    this.dictionaryPath = dictionaryPath;
  }

  /**
   * Feeds all list entries in all lists in all definitions to the
   * specified listener.
   */
  public void feedTo(QueryResultListener listener) throws KIMQueryException {

    if(!dictionaryPath.exists()) {
      log.error("The specified dictionary path does not exist!");
      throw new KIMQueryException("Invalid dictionary path specified!");
    }

    try {
      listener.startTableQueryResult();
      log.debug("Starting loading of definition lists from: " + dictionaryPath);
      loadDefinitions(listener);
      log.debug("Definition loading complete.");
      listener.endTableQueryResult();
    }
    catch(IOException e) {
      throw new KIMQueryException(e);
    }
    catch(ResourceInstantiationException e) {
      throw new KIMQueryException(e);
    }
  }

  private void loadDefinitions(QueryResultListener listener)
          throws ResourceInstantiationException, IOException {

    @SuppressWarnings("unchecked")
    List<File> definitionPaths = (List<File>)FileUtils.listFiles(dictionaryPath,
            new WildcardFileFilter(DEF_EXTENSION), null);
    for(File definitionPath : definitionPaths) {

      log.debug("Loading definition file: " + definitionPath.toString());
      LinearDefinition definition = new LinearDefinition();
      definition.setURL(definitionPath.toURI().toURL());
      definition.load();
      loadLists(listener, definition);
    }
  }

  @SuppressWarnings("rawtypes")
  private void loadLists(QueryResultListener listener,
          LinearDefinition definition) throws ResourceInstantiationException,
          IOException {

    Map listsByNode = definition.loadLists();
    Iterator inodes = definition.iterator();
    LinearNode node;
    while(inodes.hasNext()) {

      node = (LinearNode)inodes.next();
      if(null == node) {
    	log.error("LinearNode node is null!");
        throw new ResourceInstantiationException("LinearNode node is null!");
      }

      GazetteerList gazList = (GazetteerList)listsByNode.get(node);
      if(null == gazList) {
   	    log.error("Gazetteer list not found by node");
        throw new ResourceInstantiationException(
                "Gazetteer list not found by node");
      }

      log.debug("Loading " + gazList.size() + " definitions for list: " + gazList.getName());
      Iterator iline = gazList.iterator();
      while(iline.hasNext()) {
        GazetteerNode gazNode = (GazetteerNode)iline.next();
        addEntity(listener, node.getMinorType(), node.getMajorType(),
                gazNode.getEntry());
      }
    }
  }

  private void addEntity(QueryResultListener listener, String minorType,
          String majorType, String label) throws IOException {

    listener.startTuple();
    listener.tupleValue(new LiteralImpl(label));
    listener.tupleValue(new URIImpl("urn:" + minorType));
    listener.tupleValue(new URIImpl("urn:" + majorType));
    listener.endTuple();
  }

}
