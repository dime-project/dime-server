package com.ontotext.kim.model;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.openrdf.model.Value;

/**
 * Gathers statistics on the entries in the entity cache.
 * 
 * <p>
 * See KIM-489 and its parent task for details for the statistics and sample results.
 * 
 * @author mnozchev
 */
public class StatisticListener extends EntitiesQueryListener {
	private static final Logger log = Logger.getLogger(StatisticListener.class);
	
    private static final int EXAMINED_TOKENS_CNT = 20;
    private final int[] tokens4alias = new int[EXAMINED_TOKENS_CNT + 1];
    private final String t4a_StatTitle;
    private int maxOfMinTokLen = 0;
    private int minOfMaxTokLen = 100;
    private final int[] tokens4len = new int[100];
    
    private final EntitiesQueryListener innerListener;
    
    public StatisticListener(EntitiesQueryListener innerListener, String title) {     
        this.innerListener = innerListener;
        this.t4a_StatTitle = title;
    }
    
    public void endTableQueryResult() throws IOException {    
    	// We don't call super here on purpose. Calling the inner listener should take care of that.
        this.innerListener.endTableQueryResult();
        log.debug("Tokens-for-alias distribution chart of: " + t4a_StatTitle);
        for (int i = 0; i < (EXAMINED_TOKENS_CNT + 1); i++)
            log.debug(String.format("%02d: %10d", i, tokens4alias[i]));

        log.debug("MiniMax range for tokens is [" +
                minOfMaxTokLen + "," + maxOfMinTokLen + "]");
        log.debug("Tokens-for-length distribution chart of: " + t4a_StatTitle);
        for (int i = 0; i < 100; i++)
            if ( tokens4len[i] > 0 )
                log.debug(
                        String.format("%02d: %10d", i, tokens4len[i]));
    }

    public void startTableQueryResult(String[] arg0) throws IOException {
    	// We don't call super here on purpose. Calling the inner listener should take care of that.
        this.innerListener.startTableQueryResult(arg0);
        for (int i = 0; i < (EXAMINED_TOKENS_CNT + 1); i++)  tokens4alias[i] = 0;
        for (int i = 0; i < 100; i++)  tokens4len[i] = 0;
    }

    public void startTableQueryResult() throws IOException {
    	// We don't call super here on purpose. Calling the inner listener should take care of that.
        this.innerListener.startTableQueryResult();
        for (int i = 0; i < (EXAMINED_TOKENS_CNT + 1); i++)  tokens4alias[i] = 0;
        for (int i = 0; i < 100; i++)  tokens4len[i] = 0;
    }
    
    @Override
    protected void addEntity(String instUri, String classUri, String aliasLabel) {
    	// We don't call super here on purpose. Calling the inner listener should take care of that.
        this.innerListener.addEntity(instUri, classUri, aliasLabel);
        
        int minTokLen = 100;
        int maxTokLen = 0;
        java.util.regex.Matcher mt = 
            java.util.regex.Pattern.compile(
                    "((\\d){1,}|"+
                    "(\\p{javaUpperCase}{0,}\\p{javaLowerCase}{1,})|"+
                    "(\\p{javaUpperCase}{1,}))"
                    ).matcher(aliasLabel);
        int i = 0;
        while ( mt.find() ) {
            int lenTmp = mt.group().length();
            if (minTokLen > lenTmp)  minTokLen = lenTmp;
            if (maxTokLen < lenTmp)  maxTokLen = lenTmp;
            if ( lenTmp > 99 )  lenTmp = 99;
            (tokens4len[lenTmp])++;
            i++;
        }
        if ( i > EXAMINED_TOKENS_CNT )  i = EXAMINED_TOKENS_CNT;
        (tokens4alias[i])++;               
        if (maxOfMinTokLen < minTokLen)  maxOfMinTokLen = minTokLen;
        if (minOfMaxTokLen > maxTokLen)  minOfMaxTokLen = maxTokLen;
    }

    public static EntitiesQueryListener wrap(EntitiesQueryListener tmpLsnr,
            String title) {        
        return new ClassStatisticListener(new StatisticListener(tmpLsnr, title));
    }

    @Override
    public void endTuple() throws IOException {    	
    	// We don't call super here on purpose. Calling the inner listener should take care of that.
    	this.innerListener.endTuple();
    }   
    
    @Override
    public void tupleValue(Value value) throws IOException {
    	// We don't call super here on purpose. Calling the inner listener should take care of that.
    	this.innerListener.tupleValue(value);
    } 
    
    @Override
    public void startTuple() throws IOException {
    	// We don't call super here on purpose. Calling the inner listener should take care of that.
    	this.innerListener.startTuple();
    }    
}
