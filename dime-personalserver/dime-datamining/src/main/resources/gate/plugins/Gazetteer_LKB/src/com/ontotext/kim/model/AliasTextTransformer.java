package com.ontotext.kim.model;

import org.apache.commons.collections.Transformer;

/**
 * This class implements specific text normalization logic used by the class
 * <code>ParsingFrame</code>. It is intended to remove insignificant specifics
 * of the stored/searched aliases before their comparison. Such specifics
 * are:<br>
 *  - Multiple white spaces but no more than 10 at a time.<br>
 *  - The case of letters (only in case-insensitive mode).<br>
 *  <br>
 *  This class is left open for addition of other normalization procedures
 *  if such need arises.
 *  
 * @author danko
 *
 */
public class AliasTextTransformer implements Transformer {
    private final boolean toLower;
    private boolean isWs(char c) { return Character.isWhitespace(c); }
    private boolean isUc(char c) {  return Character.isUpperCase(c); }
    private char toLc(char c) {  return Character.toLowerCase(c); }
    public Object transform(Object input) {
    	if (input == null)  return "";
    	String in = input.toString();
    	if (in.length() == 0) return "";

        //trim
        int i=0, j=in.length()-1;
        while (i < j && isWs(in.charAt(i))) ++i;
        while (j > i && isWs(in.charAt(j))) --j;
        boolean changed = !(i==0 && j==in.length()-1);
        
        // remove consecutive whitespace but max 10 of them
        int mergedWS = 0;
        StringBuilder strip = null;
        if (changed) {
            // If change case is detected create the change buff
            strip = new StringBuilder(j-i+1);
        }
        for (int k = i; k<=j ; k++) {
            char c = in.charAt(k);
            boolean isWs = isWs(c);
            
            // This block detects the first change. If no change
            // criterion is fulfilled - no string manipulations are done
            if (!changed) {
                if (isWs) {
                    if (mergedWS>0)  // detects WS merger case
                        changed=true;
                    else
                        mergedWS=1;
                }
                else {
                    if (toLower && isUc(c)) // detects to-lower case
                        changed = true;
                    else
                        mergedWS=0;
                }
                
                if (changed) {
                    // If change case is detected create the change buff
                    strip = new StringBuilder(j-i+1);
                    // append the string so far
                    strip.append(in.substring(i,k));
                }
                else
                    continue;
            }
            
            if (isWs && mergedWS<10) {
                if (mergedWS < 1)  strip.append(c);
                mergedWS++;
            }
            else {
                if (toLower)  strip.append(toLc(c));
                else          strip.append(c);
                mergedWS=0;
            }
        }
        return (changed)? strip.toString(): in;
    }
    public AliasTextTransformer(boolean toLower) {
        super();
        this.toLower = toLower;
    }
}
