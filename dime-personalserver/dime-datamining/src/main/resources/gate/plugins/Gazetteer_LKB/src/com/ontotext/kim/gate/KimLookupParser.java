package com.ontotext.kim.gate;

import java.util.Collection;

import org.apache.log4j.Logger;

import com.ontotext.kim.model.ParsingFrame;

/**
 * This class processes a textual input, finds the location of all known text
 * fragments stored in a dictionary and then passes these findings to a special
 * handler.<br>
 * <br>
 * The text processing is performed through the class <code>ParsingFrame</code>
 * .<br>
 * An instance of class <code>AliasCacheImpl</code> is used for dictionary.<br>
 * The findings are passed to a handler class that implements the interface
 * <code>EntityOccuranceHandler</code>.<br>
 * 
 * @author danko
 *
 */
public class KimLookupParser {

    private static Logger log = Logger.getLogger(KimLookupParser.class);
    
    public interface EntityOccuranceHandler {
        void processEntityOccurance(int start, int end,
                String instURI, String classURI);
    }
    
    public interface AliasLookupDictionary {
        /**
         * Looks up for matches given a ParsingFrame. This is used for
         * multiple lookups for different fragments of a parsed text.
         * @param pfm - a ParsingFrame which has already parsed a part
         * of the text
         * @return - collection of matching wrapped Aliases
         */
        public Collection<KimLookupParser.AliasWrapper> lookup(ParsingFrame pfm);
        /**
         * Checks if the lexeme phrase that is currently focused by the
         * passed ParsingFrame is a valid lexeme prefix of another alias.
         * If it is a valid prefix - then phrase can grow.
         * @param pfm - a ParsingFrame which has already parsed a part
         * of the text
         * @return - true is there is still a chance to find a larger 
         * phrase matching the current location of the parsed text
         */
        public boolean canPhraseGrow(ParsingFrame pfm);
    }
    
    /** This class implements a container to return the results of the
     * Alias Dictionary lookup */
    public static class AliasWrapper {
    	public final String instURI;  // The instance URI of the related Entity
    	public final String classURI;  // The class URI of the related Entity
    	public final int start;
    	public final int end;
    	public AliasWrapper(String instURI, String classURI,
    			int start, int end) {
    		this.instURI = instURI; this.classURI = classURI;
    		this.start = start; this.end = end;
    	}
    }

    private AliasLookupDictionary aliasDictionary;
    private boolean interrupted = false;

    public KimLookupParser(AliasLookupDictionary aliasCache) {
        this.aliasDictionary = aliasCache;
    }
    
    public void findLookups(String content, EntityOccuranceHandler entityHandler) {
    	this.interrupted = false;
        ParsingFrame pfm = new ParsingFrame(content);
        
        log.debug("Time tracing begins");     
        
        Collection<KimLookupParser.AliasWrapper> currentMatch;
        do {
            if (pfm.parseOne()) {
                currentMatch = aliasDictionary.lookup(pfm);
                if (currentMatch != null) {
                    for (KimLookupParser.AliasWrapper ent : currentMatch) {
                        entityHandler.processEntityOccurance(ent.start, ent.end, ent.instURI, ent.classURI);
                    }
                }
            }
            if (!aliasDictionary.canPhraseGrow(pfm) || !pfm.frameCanExpand())
                pfm.moveOne();
        } while (pfm.frameCanMove() && !this.interrupted);
        log.debug("Time tracing ends");       
    }

    public boolean isInterrupted() {
        return this.interrupted;
    }
    
    public void setInterrupted(boolean interrupted) {
        this.interrupted = interrupted;
    }

}
