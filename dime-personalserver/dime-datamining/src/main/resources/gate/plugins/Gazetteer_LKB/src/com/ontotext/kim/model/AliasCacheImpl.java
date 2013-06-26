package com.ontotext.kim.model;

import gate.creole.ResourceInstantiationException;
import gate.util.profile.Profiler;
import gnu.trove.TIntHashSet;

import java.io.*;
import java.rmi.RemoteException;
import java.util.*;

import org.apache.commons.collections.Transformer;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;

import com.ontotext.kim.KIMConstants;
import com.ontotext.kim.client.KIMRuntimeException;
import com.ontotext.kim.client.query.KIMQueryException;
import com.ontotext.kim.client.semanticrepository.QueryResultListener;
import com.ontotext.kim.client.semanticrepository.QueryResultListener.Feed;
import com.ontotext.kim.gate.KimLookupParser;
import com.ontotext.kim.gate.KimLookupParser.AliasLookupDictionary;
import com.ontotext.kim.util.StringTransformations;


/**
 * This class is designed to serve as an Alias dictionary for the
 * <code>KimGazetteer</code>. It is used to store an image of the textual
 * representations (Aliases) of the known objects (Entities) described in the
 * KIM platform knowledge-base. Then it allows to check text fragments for
 * the presence of known Aliases.<br>
 * The aliases are not stored as plain text but as couple of hash codes.
 * The storage is implemented by the couple of classes <code>HashedAlias</code>
 * and <code>HashRegister</code>. Because of this specific - before storing
 * of an Alias and also before checking a text fragment - they both must
 * be pre-processed. The pre-processing is implemented in couple of classes.
 * The class <code>AliasTextTransformer</code> does preliminary text
 * normalization and the class <code>ParsingFrame</code> does further
 * normalization and hash-codes calculation.<br>
 * <br>
 * An instance of the <code>AliasCacheImpl</code> class is obtained through
 * a synchronized factory method.
 * 
 * @author danko
 *
 */
public class AliasCacheImpl implements AliasLookupDictionary {

  protected static Logger log = Logger.getLogger(AliasCacheImpl.class);
  private static DataFeedFactory feedFactory = new DataFeedFactory();

  /** The register containing the <code>HashedAlias</code> instances */
  protected HashRegister aliasRegister;
  /** The set of hash-codes of valid alias prefixes. This set is used in
   * text parsing and lookup phase. It helps to determine if an attempt
   * must be made to expand the span of the <code>ParsingFrame</code> used
   * to parse the searched text. An expansion is made only if currently
   * framed text generates <b>Alias-Hash-1</b> code which appears in the
   * <code>aliasPrefixes</code> set. (For details see
   * <code>ParsingFrame</code>) */
  protected TIntHashSet aliasPrefixes;
  /** Additional register which allows fast checking if a given Entity's
   * aliases has been stored in the alias register. */
  protected HashRegister aliasInstRegister;
  /** Array used for encoding/decoding the instance URI's name-spaces */
  protected ArrayList<String> instNS;
  /** Array used for encoding/decoding the semantic class URIs */
  protected ArrayList<String> classCache;
  /** Additional register containing exactly appointed aliases, which
   * must be ignored on storing */
  protected HashRegister aliasToIgnore;

  /** The general case sensitivity selector of the Alias cache */
  private String caseSensitivity;

  protected AliasCacheImpl (String caseSensitive) {
    this.caseSensitivity = caseSensitive;
  }

  //=========================================================================
  // Alias Cache: Instance Generation section
  //=========================================================================
  private static Map<File, LoadedCache> aliasDictionaries = new HashMap<File, LoadedCache>();
  private static Object instanceLock = new Object();

  public static AliasCacheImpl getInstance() throws ResourceInstantiationException {    
    return getInstance(new File(KIMConstants.KIM_CACHE_PATH), "<unknown>");
  }

  private static class LoadedCache {
    public AliasCacheImpl cache;
    public List<String> clients = new LinkedList<String>();
  }

  /**
   * A static method for generation/access to the one and only instance
   * of the alias cache
   * @param dictionaryPath 
   * @return - the instance of the cache
   * @throws ResourceInstantiationException
   */
  public static AliasCacheImpl getInstance(File dictionaryPath, String clientId) throws ResourceInstantiationException {
    synchronized(instanceLock) {
      if ( !aliasDictionaries.containsKey(dictionaryPath)) {     
        LoadedCache lc = new LoadedCache();
        lc.cache = createInstance(dictionaryPath);
        aliasDictionaries.put(dictionaryPath, lc);
      }
    }		
    LoadedCache lc = aliasDictionaries.get(dictionaryPath);
    lc.clients.add(clientId);
    return lc.cache;
  }

  public static void releaseCache(File dictionaryPath, String clientId) {
    synchronized(instanceLock) {
      LoadedCache lc = aliasDictionaries.get(dictionaryPath);
      if (lc == null)
        return;
      lc.clients.remove(clientId);
      if (lc.clients.isEmpty())
        aliasDictionaries.remove(dictionaryPath);
      else {
        log.info("The cache for " + dictionaryPath + " will not be unloaded or reloaded because some clients remain: " + lc.clients);
      }
    }		
  }

  public static AliasCacheImpl createInstance(File dictionaryPath)
  throws ResourceInstantiationException {
    Options opt = Options.load(dictionaryPath);
    AliasCacheImpl aliasCacheInstance = new AliasCacheImpl(opt.getCaseSensitivity());
    Feed feed = feedFactory.createFeed(dictionaryPath, opt);
    Set<String> ignoreList = Collections.emptySet();
    File ignoreListFile = opt.getIgnoreListPath();
    if (ignoreListFile != null) {
      if (ignoreListFile.isFile()) {
        try {
          ignoreList = new HashSet<String>(FileUtils.readLines(opt.getIgnoreListPath(), "UTF-8"));
          log.info(ignoreList.size() +  " unique entries loaded from ignore list at " + ignoreListFile.getAbsolutePath());
        }
        catch(IOException e) {
          log.warn("Could not read " + ignoreListFile.getAbsolutePath(), e);
        }
      } else {
        log.warn("Ignore list at " + ignoreListFile.getAbsolutePath() + " is not present or is not an accessible file.");
      }
    }
    try {											
      aliasCacheInstance.initCache(ignoreList, feed, dictionaryPath, opt.isCacheEnabled());
    } catch (RemoteException e) {
      throw new ResourceInstantiationException(e);
    }
    return aliasCacheInstance;
  }

  //=========================================================================
  // Alias Cache Persistence section
  //=========================================================================
  /**
   * Checks whether an alias can be added to the cache, honoring the
   * caseSensitivity setting.<br>
   * <br>
   * The ignore list check is performed here.<br>
   * 
   * @param alias
   * @return whether the alias can be added
   */
  private boolean verifyAlias(String alias) {
    if (alias==null || alias.trim().length() == 0)
      return false;

    alias = (String) ParsingFrame.frameTT.transform(alias);
    if (aliasToIgnore.exists(alias.hashCode(), alias)) {
      log.info("'" + alias
              + "' ignored, because it was found in the ignore list.");
      return false;
    }

    return true;
  }

  /** This class is used in deserialization process to initialize the
   * <code>aliasInstRegister</code> register */
  private static class InstanceRegisterLoader
  implements HashRegister.ContentProcessor {
    HashRegister instRegister;
    public InstanceRegisterLoader(HashRegister register) {
      instRegister = register;
    }
    public void process(Object[] elements) {
      if (elements != null) {
        for (int i=0; i<elements.length; i++) {
          String shortInst = ((HashedAlias) elements[i]).shortInstURI;
          instRegister.add(shortInst.hashCode(), shortInst);
        }
      }
    }
  }

  /** This is a blank initialization method. It creates the structure of a new
   * Alias cache without filling it with content. This code is separated from 
   * the data filling from the semantic repository - for class extension
   * convenience.<br>
   * @param ignoreAliases - a String list of aliases to be ignored.
   */
  protected void initBlankCache(Collection<String> ignoreAliases) {
    aliasRegister = new HashRegister();
    aliasPrefixes = new TIntHashSet();
    aliasInstRegister = new HashRegister();
    instNS = new ArrayList<String>();
    classCache = new ArrayList<String>();

    // Create a TextTransformer instance for Alias text normalization
    Transformer tt = new AliasTextTransformer(
            caseSensitivity.equals(Options.INSENSITIVE));
    ParsingFrame.frameTT = tt;
    aliasToIgnore = new HashRegister();
    if (ignoreAliases != null) {
      for (String alias : ignoreAliases) {
        // Apply same text normalization to the aliases to be ignored 
        alias = (String)tt.transform(alias);
        aliasToIgnore.add(alias.hashCode(), alias);
        if (caseSensitivity.equals(Options.ALL_UPPER)) {
          alias = alias.toUpperCase();
          aliasToIgnore.add(alias.hashCode(), alias);
        }
      }
    }
    log.info(
            "Aliases in IGNORE list:" + aliasToIgnore.getElementsCount());

  }

  /** This method implements the default full initialization process.
   * It creates an empty Alias cache and then fills it with data. The data
   * is collected either from the semantic repository or from a serialization
   * source (a file). 
   * @param ignoreAliases - a String list of aliases to be ignored.
   * @param semRep 
   * @param dictionaryPath 
   * @param cacheEnabled 
   * @throws RemoteException - on failure to access the semantic repository.
   */
  protected void initCache(
          Collection<String> ignoreAliases, QueryResultListener.Feed dataFeed, File dictionaryPath, boolean fileCacheEnabled) throws RemoteException {
    Profiler pro = new Profiler();
    pro.enableGCCalling(false);
    pro.printToSystemOut(true);
    pro.initRun("Loading of Entities Cache");
    pro.checkPoint("start loading");

    initBlankCache(ignoreAliases);

    File fileTCache = new File(dictionaryPath, "kim.trusted.entities.cache").getAbsoluteFile();

    // The presence of this flag marks a cache file, which is invalid due to an interruption in loading.   
    File flagTCache = new File(dictionaryPath, fileTCache.getName() + ".flag");

    if (fileCacheEnabled) {           
      try {
        ensureCachePath(dictionaryPath);
      } catch (IOException e1) {
        log.error(
                "Could not create entity cache.", e1);
      }
    }

    boolean flagTLoaded = false;
    if (fileCacheEnabled && fileTCache.exists() && !flagTCache.exists()) {
      log.info("Loading of trusted entities from "
              + fileTCache);
      flagTLoaded = loadDictionaryFromCacheFile(fileTCache, flagTLoaded);
    }

    if (!flagTLoaded) {
      loadTrustedMaps(dataFeed);

      if (fileCacheEnabled) {
        try {
          flagTCache.createNewFile();
          if (fileTCache.exists())
            fileTCache.delete();

          ObjectOutputStream oos = new ObjectOutputStream(
                  new FileOutputStream(fileTCache));
          oos.writeObject(
                  new Object[]{aliasRegister, aliasPrefixes,
                          instNS, classCache});
          oos.close();
          flagTCache.delete();
        } catch (Exception ex) {
          log.error("Saving of trusted entities to "
                  + fileTCache + " failed.", ex);
        }
      }
    }

    log.info("Aliases were loaded");
    pro.checkPoint("cache loaded");
  }

  private boolean loadDictionaryFromCacheFile(File fileTCache,
          boolean flagTLoaded) {
    try {
      ObjectInputStream ois = new ObjectInputStream(
              new FileInputStream(fileTCache));
      Object[] res = (Object[]) ois.readObject();
      ois.close();
      aliasRegister = (HashRegister) res[0];
      aliasPrefixes = (TIntHashSet) res[1];
      instNS = (ArrayList<String>) res[2];
      classCache = (ArrayList<String>) res[3];


      aliasInstRegister = new HashRegister();
      // The exactly same Entity InstURI strings are reused
      aliasRegister.processContent(
              new InstanceRegisterLoader(aliasInstRegister));				
      flagTLoaded = true;
      log.info(aliasRegister.getElementsCount() + " elements loaded.");
    } catch (Exception e) {
      log.error("Loading from "
              + fileTCache + " failed. "
              + "Continue with loading from Semantic Repository.", e);
    }
    return flagTLoaded;
  }

  private void ensureCachePath(File cachePath) throws IOException {
    if (cachePath.exists() && !cachePath.isDirectory())
      FileUtils.forceDelete(cachePath);
    if (!cachePath.exists())
      FileUtils.forceMkdir(cachePath);
  }

  private void loadTrustedMaps(QueryResultListener.Feed dataFeed) {
    log.info("Loading of trusted entities from Sesame");

    String filePath = System.getProperty("kim.home.dir", ".") +
    EntityPriority.PRIORITY_CONF_FILE.substring(1);
    existsClassPriority = (new File(filePath)).exists();

    if (existsClassPriority) {
      try {
        entPrior = new EntityPriority();
        entPrior.init();
        existsClassPriority = existsClassPriority
        && entPrior.getFilterLookups();
      } catch (Exception e) {
        log.error(
                "Cannot create instance of Priorities class", e);
        entPrior = null;
      }
    }
    EntitiesQueryListener entityListener = new TrustedEntitiesListener(entPrior);

    // Handler to preserve the same inner listener for the two queries		
    if ( log.isDebugEnabled() ) {
      entityListener = StatisticListener.wrap(entityListener, "Thrusted Entities");
    }
    try {
      dataFeed.feedTo(entityListener);
    } catch (KIMQueryException e) {       
      log.error("Loading failed.", e);
      throw new KIMRuntimeException("The loading failed.", e);
    } finally {
      log.info("The loading from Sesame finished");
    }
  }

  /** A class extending the <code>EntitiesQueryListener</code>, which is
   * used to process the input from the semantic repository. It is used
   * only when the data is loaded from there. */
  class TrustedEntitiesListener extends EntitiesQueryListener {
    private final EntityPriority m_entPrior;
    TrustedEntitiesListener(EntityPriority m_entPrior) {
      this.m_entPrior = m_entPrior;
    }
    @Override
    protected void addEntity(String instUri, String classUri,
            String aliasLabel) {
      addAlias(instUri, classUri, aliasLabel, true);
    }

    @Override
    public void endTableQueryResult() throws IOException {
      super.endTableQueryResult();
      if (existsClassPriority && allPrioritiesCompetition != null) {
        Iterator it = allPrioritiesCompetition.keySet().iterator();
        while (it.hasNext()) {
          ArrayList<priorityCompetition> pcList =
            allPrioritiesCompetition.get(it.next());
          int maxPrior = pcList.get(0).maxPriority;
          int treshold = m_entPrior.getThreshold();
          for (int i = 0; i < pcList.size(); i++) {
            priorityCompetition pc = pcList.get(i);
            if (i == 0 || maxPrior - pc.maxPriority <= treshold)
              addAlias(pc.instURI, pc.classURI, pc.alias, false);
          }
        }
        allPrioritiesCompetition = null;
      }
    }
  }


  //=========================================================================
  // Alias Cache: Statistics Collection section
  //=========================================================================
  /** The class is used to collect timing data for profiling purposes */
  public static class Stats {
    private static boolean doStats = false;
    private static final String[] statNames = {
      "AA_PrefixStore",
      "AA_URIStringReuse",
      "AA_InstUriRegisterInsert",
      "AA_AliasRegisterInsert",
      "AL_PreParsing",
      "AL_GetByAliasHash1",
      "AL_FilterByAliasHash2",
      "PF_MakeFrame",
      "PF_Find",
      "PF_RecalcFrame",
      "PF_MakeFrameSnapshot"
    };

    private static long[] statTimes = new long[statNames.length];

    public static void restartStats() {
      doStats = true;
      Arrays.fill(statTimes, 0);
    }
    public static void stopStats() {  doStats = false;  }
    public static boolean doStats() {  return doStats;  }
    private static long curr;
    private static long last;
    public static void markIt(int index) {
      if (doStats) {
        curr = System.currentTimeMillis();
        long duration = curr-last;
        if (index >= 0 && index < statTimes.length)
          statTimes[index] += duration;
        last = curr;
      }
    }
    public static void dumpStats() {
      for (int i=0; i<statNames.length; i++)
        if (statTimes[i] > 0)
          System.out.println(
                  "  " + statNames[i] + " = " + statTimes[i] + "ms.");
    }
  }


  //=========================================================================
  // Alias Cache: Population section
  //=========================================================================
  /** Adds an Alias with its instance and semantic class to the Alias cache.
   * A single call to this method could result in adding several records to
   * the alias cache. This is as a result to the standard Alias enrichment
   * logic which is applied over the given as input alias string.
   * @param instURI - the URI of the Entity instance corresponding to the
   * Alias 
   * @param classURI - the URI of the semantic class
   * @param alias - the string of the alias
   * @param primaryAccess - processing specific flag; if <b>true</b> - forces
   * class priority checks.
   */
  public void addAlias(String instURI, String classURI, String alias,
          boolean primaryAccess) {

    if (checkClassPriority(instURI, classURI, alias, primaryAccess))
      return;

    String[] enriched = aliasEnrichment(alias);
    for (int i=0; i< enriched.length; i++) {
      if (verifyAlias(enriched[i]))
        simpleAddAlias(instURI, classURI, enriched[i]);
    }
  }

  /** This method performs the standard alias enrichment. It covers
   * cases as variants with and without trailing punctuation. 
   * @param alias - the original alias string.
   * @return array of distinct strings which are accepted as equally valid
   * representation of the related to the Alias - Entity.
   */
  private String[] aliasEnrichment(String alias) {
    HashSet<String> aliases = new HashSet<String>();
    aliases.add(alias);
    String[] tmp;

    // Enrich with UPPER case versions if needed
    if (caseSensitivity.equals(Options.ALL_UPPER)) {
      tmp = aliases.toArray(new String[0]);
      for (int i=0; i<tmp.length; i++) {
        String tmpNew = tmp[i].toUpperCase();
        if (!tmpNew.equals(tmp[i]))
          aliases.add(tmpNew);
      }
    }

    // Enrich with versions with stripped punctual suffix
    tmp = aliases.toArray(new String[0]);
    for (int i=0; i<tmp.length; i++) {
      String tmpNew = StringTransformations.stripPunctAtEnd(tmp[i]);
      if (!tmpNew.equals(tmp[i]))
        aliases.add(tmpNew);
    }

    return aliases.toArray(new String[0]);
  }

  /** Method that implements a simple alias addition (as is) to the
   * cache structures
   * @param instURI - the Entity instance URI
   * @param classURI - the semantic class URI
   * @param alias - the string of the alias
   */
  private void simpleAddAlias(String instURI, String classURI,
          String alias) {
    String shortInstURI = packNS(instURI);

    // Calculate different hash values related with the alias
    ParsingFrame pfm = new ParsingFrame(alias);
    do {
      if (pfm.parseOne() && pfm.frameCanExpand()) {
        // Add the aliases prefixes to the alias prefix register
        Stats.markIt(-1);
        aliasPrefixes.add(pfm.getAliasHash1());
        Stats.markIt(0);
      }
    } while (pfm.frameCanExpand());

    // Add the corresponding instURI in the Entity instance URI register
    String oldURI = null;

    // Re-usage of the same string for EntityURI for all its Aliases
    // is expected to save 10% to 15% of the memory
    oldURI = (String) aliasInstRegister.get(
            shortInstURI.hashCode(), shortInstURI);

    Stats.markIt(1);

    if (oldURI != null)
      shortInstURI = oldURI;
    else
      aliasInstRegister.add(shortInstURI.hashCode(), shortInstURI);
    Stats.markIt(2);

    // Add the alias in the alias register
    aliasRegister.add(
            pfm.getAliasHash1(),
            new HashedAlias(pfm.getAliasHash2(),
                    pfm.getPrefixLen(), pfm.getSuffixLen(),
                    shortInstURI,
                    packClass(classURI)));
    Stats.markIt(3);
  }


  //=========================================================================
  // Alias Cache: Retrieval section
  //=========================================================================
  /**
   * Looks up for matches given an Alias string. This is useful for
   * single lookups and searches for exact matches. This means no
   * prefix/suffix variations are accepted.
   * @param alias - the label string of the alias
   * @return - array of matching HashedAlias instances 
   */
  public ArrayList<KimLookupParser.AliasWrapper> lookup(String alias) {
    ParsingFrame pfm = new ParsingFrame(alias);
    pfm.parseAll();
    return lookup(pfm, true);
  }

  public Collection<KimLookupParser.AliasWrapper> lookup(ParsingFrame pfm) {
    return lookup(pfm, false);
  }
  private ArrayList<KimLookupParser.AliasWrapper> lookup(
          ParsingFrame pfm, boolean exactlySame) {
    Stats.markIt(-1);
    ArrayList<KimLookupParser.AliasWrapper> res = new ArrayList<KimLookupParser.AliasWrapper>();

    Object[] tmp = aliasRegister.get(pfm.getAliasHash1());
    Stats.markIt(5);
    if (tmp == null || tmp.length==0)
      return res;

    for (int i=0; i<tmp.length; i++) {
      HashedAlias ha = (HashedAlias)tmp[i];
      if (!exactlySame) {
        pfm.setNewPrefSufLen(ha.prefLen, ha.suffLen);
      }
      if (pfm.getAliasHash2() == ha.aliasHash2) {
        res.add(new KimLookupParser.AliasWrapper(
                unpackNS(ha.shortInstURI),
                unpackClass(ha.classID),
                pfm.getAliasStart(), pfm.getAliasEnd()));
      }
    }
    Stats.markIt(6);

    return res;
  }

  public boolean canPhraseGrow(ParsingFrame pfm) {
    return aliasPrefixes.contains(pfm.getAliasHash1());
  }
  public boolean isTrustedEntityURI(String instURI) {
    String shortURI = packNS(instURI);
    return aliasInstRegister.exists(shortURI.hashCode(), shortURI);
  }
  public int getEntityCount() {
    return aliasInstRegister.getElementsCount();
  }
  public int getAliasCount() {
    return aliasRegister.getElementsCount();
  }


  //=========================================================================
  // Alias Cache: URI Pack/Unpack tools
  //=========================================================================
  private String packNS(String fullURI) {
    String ns, ln;
    try {
      URI uri = new URIImpl(fullURI);
      ns = uri.getNamespace();
      ln = uri.getLocalName();
    } catch (RuntimeException e) {
      ns = "";
      ln = fullURI;
    }
    int j = instNS.indexOf(ns);
    if (j < 0) {
      instNS.add(ns);
      j = instNS.size()-1;
    }
    return j+":"+ln;
  }
  private String unpackNS(String shortURI) {
    if (shortURI == null)  return null;
    int i = shortURI.indexOf(':');
    String ns;
    try {
      ns = instNS.get(Integer.parseInt(shortURI.substring(0,i)));
    } catch (Exception e) {
      log.debug(
              "Short URI unpack failed:" + shortURI, e);
      return shortURI;
    }
    return ns + shortURI.substring(i+1);
  }
  private int packClass(String classURI) {
    int i = classCache.indexOf(classURI);
    if (i < 0) {
      classCache.add(classURI);
      i = classCache.size() - 1;
    }
    return i;
  }
  private String unpackClass(int clasID) {
    return classCache.get(clasID);
  }


  //======================================================
  // Alias Cache: Class Priority competition & elimination
  //======================================================

  class priorityCompetition {
    String instURI;
    String classURI;
    String alias;
    int maxPriority;

    priorityCompetition(String instURI, String classURI, String alias,
            int maxPriority) {
      this.instURI = instURI;
      this.classURI = classURI;
      this.alias = alias;
      this.maxPriority = maxPriority;
    }
  }

  protected HashMap<String, ArrayList<priorityCompetition>> allPrioritiesCompetition = new HashMap();
  protected EntityPriority entPrior;
  protected boolean existsClassPriority = false;	

  /**
   * TODO
   * 
   * <p>
   *  If current label class is 'competitive', the label is put in a Map,
   * having List
   * for all concurrent classes descending sorted by weight
   * because of small number of classes for a label, direct insert is
   * chosen in comparison with binary
   * @param instURI
   * @param classURI
   * @param alias
   * @param primaryAccess
   * @return
   */
  private boolean checkClassPriority(String instURI, String classURI,
          final String alias, boolean primaryAccess) {
    boolean rejectedByPriority = false;
    if (primaryAccess && existsClassPriority) {
      URI origClass = new URIImpl(classURI);
      String priorityClassName = origClass.getLocalName();
      rejectedByPriority =
        entPrior.m_hClassPrio.containsKey(priorityClassName);
      if (rejectedByPriority) {
        int mp = (Integer) entPrior.m_hClassPrio.get(priorityClassName);
        log.info("COMPETITION:" + "\t" + instURI
                + "\t" + classURI + "\t" + alias + "\t" + mp);
        if (!allPrioritiesCompetition.containsKey(alias))
          allPrioritiesCompetition.put(alias,
                  new ArrayList<priorityCompetition>());
        ArrayList<priorityCompetition> pcList =
          allPrioritiesCompetition.get(alias);
        boolean foundLesser = false;
        for (int i = 0; i < pcList.size(); i++) {
          if (mp > pcList.get(i).maxPriority) {
            pcList.add(i, new priorityCompetition(
                    instURI, classURI, alias, mp));
            foundLesser = true;
            break;
          }
        }
        if (!foundLesser)
          pcList.add( new priorityCompetition(
                  instURI, classURI, alias, mp));                
      }
    }
    return rejectedByPriority;
  }

}