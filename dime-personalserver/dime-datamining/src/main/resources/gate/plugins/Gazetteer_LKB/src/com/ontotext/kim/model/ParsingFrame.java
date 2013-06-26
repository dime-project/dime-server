package com.ontotext.kim.model;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.TransformerUtils;

import com.ontotext.kim.model.AliasCacheImpl.Stats;


/**
 * This class is used as a text parsing tool by classes
 * <code>AliasCacheImpl</code> and <code>KimLookupParser</code> and produces
 * special output for the class <code>HashedAlias</code>.<br>
 * The class provides means to parse a string into sequence of plain numeric
 * and letter lexemes delimited by optional non-alpha-numeric character
 * sequences of arbitrary length. This is the base for creation of a Hashed
 * Alias and also the base for searching it in a text.<br>
 * The class offers a frame view over the parsed string. The frame spans one or
 * more sequential alpha-numeric lexemes (ANL) and their non-alpha-numeric
 * context (surrounding puctuation and white spaces).<br>
 * The frame contains three parts:<br>
 *  * middle - this is a part of the initial string which starts in the 
 * beginning of the first ANL of the frame and ends in the end of the last ANL
 * of the frame.<br>
 *  * prefix - this is the non-alpha-numeric sequence before the first ANL of
 * the frame.<br>
 *  * suffix - this is the non-alpha-numeric sequence after the last ANL of the
 * frame.<br>
 * The class has couple of methods for manipulating the frame. The first one
 * is for extending the frame by one lexeme. The second one is for moving
 * the frame start by one lexeme, which resets the frame length to one
 * lexeme.<br>
 * The class has retrieval methods that get the current frame offset and
 * the lengths of the three parts. There are dedicated methods for calculation
 * of the two Hash Codes specific to a Hashed Alias.
 * 
 * @author danko
 *
 */
public class ParsingFrame {
	
	public static final boolean SPLIT_AT_SMALL_TO_CAPITAL_CASE_CHANGE = false;
	//================================================
	// ParsingFrame: Lexeme parsing regular expression
	//================================================
	/** This is the Reg-Ex pattern that is used search the ANLs */
	private static final Pattern LEXEME_MATCH_PAT = Pattern.compile(
			"(" +
			// Number only lexemes
			"(?:\\d)+|" +
			
			(SPLIT_AT_SMALL_TO_CAPITAL_CASE_CHANGE 
					? "(?:\\p{javaUpperCase}{0,}\\p{javaLowerCase}{1,})|(?:\\p{javaUpperCase}{1,})"			    
					: "(?:[\\p{javaUpperCase}\\p{javaLowerCase}]+)" ) + 
			")"
			// Non-Alpha-Numeric suffix
			+ "([^\\d\\p{javaUpperCase}\\p{javaLowerCase}]*)"
	);
	/** This is the delimiting character used for the normalized form
	 *  of the parsed string. The normalized for is used to calculate
	 *  the H1 hash value of the <code>HashAlias</code> */
	private static final String H1_DELIMITER = "^";

	public static Transformer frameTT = TransformerUtils.nopTransformer();

	//================================================
	// ParsingFrame: Parse result buffering
	//================================================
	/** This class represents a single alpha-numeric lexeme (ANL) and its
	 * non-alpha-numeric context. */
	private static class ParsingBufferElement {
		public final int pref;
		public final int midd;
		public final int suff;
		public final String middTxt;
		public final int offset;
		public ParsingBufferElement(int pref, int midd, int suff,
				String middTxt, int offset) {
			this.pref = pref;  this.midd = midd;
			this.suff = suff;  this.middTxt = middTxt; this.offset = offset;
		}
	}
	private final static ParsingBufferElement EMPTY_ELEMENT =
		new ParsingBufferElement(0,0,0,"",0);

	/** This is the parsing buffer that stores the results from parsing of 
	 * the input text. The result is stored as a sequence of ANL elements
	 * with the sizes of their non-alpha-numeric prefix and suffix. */
	private ArrayList<ParsingBufferElement> parsingBuffer =
		new ArrayList<ParsingBufferElement>();

	//================================================
	// ParsingFrame: The definition data of the frame
	//================================================
	private int parsingIx = -1;
	private int frameIx = 0;
	private int restartIx = 0;
	private boolean frameCanExpand = true;

	private int prefix = 0;
	private int middle = 0;
	private int suffix = 0;
	private final String source;
	private String normalizedAlias = "";
	private int lexemeCount = 0;

	//================================================
	// ParsingFrame: class constructor
	//================================================
	/** The constructor parses the source string with the regular expression
	 * based <code>java.util.regex.Matcher</code> and stores the results in
	 * the parsing buffer <code>parsingBuffer</code>.
	 * @param source - the String to be processed through the
	 * <code>ParsingFrame</code> class
	 */
	public ParsingFrame (String source) {
		Stats.markIt(-1);
		this.source = source;
		Matcher lexMatcher = LEXEME_MATCH_PAT.matcher(source);
		int lastSuff = -1;
		while (lexMatcher.find()) {
			if (lastSuff < 0)
				lastSuff = lexMatcher.start();
			parsingBuffer.add( new ParsingBufferElement(
					lastSuff,
					lexMatcher.end(1) - lexMatcher.start(1),
					lexMatcher.end(2) - lexMatcher.start(2),
					lexMatcher.group(1),
					lexMatcher.start() - lastSuff));
			lastSuff = lexMatcher.end(2) - lexMatcher.start(2);
		}

		Stats.markIt(7);
	}

	//================================================
	// ParsingFrame: LookUp and Annotation data
	//================================================
	private int aliasHash1 = 0;
	private int aliasHash2 = 0;
	private int aliasOffset1 = 0;
	private int aliasOffset2 = 0;
	private int oldPref=-1;
	private int oldSuff=-1;
	private void resetLAD() {
		aliasHash1 = 0;
		aliasHash2 = 0;
		oldPref=-1;
		oldSuff=-1;
		this.setNewPrefSufLen(prefix, suffix);
	}
	/** This method changes the effective lengths of the non-alpha-numeric
	 * prefix and suffix. This change affects the results of methods:<br>
	 * <code>getAliasHash2</code>, <code>getLength</code>,
	 * <code>getAliasStart</code> and <code>getAliasEnd</code>.<br>
	 * The last values of the prefix and suffix are stored so if called
	 * again with the same values - no recalculations will be performed. 
	 * @param prefLen - the new effective prefix length.
	 * @param suffLen - the new effective suffix length.
	 */
	public void setNewPrefSufLen(int prefLen, int suffLen) {
		if (oldPref!=prefLen) {
			prefLen = Math.min(prefLen, prefix);
			int offset = (parsingBuffer.size()>0)?
					parsingBuffer.get(frameIx).offset:
						0;
					aliasOffset1 = offset + prefix - prefLen;
		}
		if (oldPref!=prefLen || oldSuff!=suffLen) {
			suffLen = Math.min(suffLen, suffix);
			aliasOffset2 = aliasOffset1 + prefLen + middle + suffLen;

			aliasHash2 = 0;  // Recalculation will be done when and if required
		}
		oldPref=prefLen;
		oldSuff=suffLen;
	}

	//================================================
	// ParsingFrame: Frame manipulation methods
	//================================================
	/** Method attempts to extend the frame to cover one more parsing-buffer
	 * element. If successful - recalculates the frame state metrics.  
	 * @return <b>true</b> if the content of the frame was extended with
	 * one more buffer element
	 */
	public boolean parseOne() {
		if (!frameCanMove())  return false;

		Stats.markIt(-1);
		ParsingBufferElement pbe = EMPTY_ELEMENT;
		boolean oneParsed = false;
		if (restartIx >= 0) {
			frameIx = restartIx;
			lexemeCount = 0;
			oneParsed = (restartIx < parsingBuffer.size());
			if (oneParsed)
				parsingIx = restartIx;
		}
		else {
			oneParsed = ((parsingIx+1) < parsingBuffer.size());
			if (oneParsed)
				parsingIx++;
		}

		if (oneParsed) {
			pbe = parsingBuffer.get(parsingIx);
			lexemeCount++;
		}
		Stats.markIt(8);

		Stats.markIt(-1);
		if (restartIx >= 0) {
			prefix = pbe.pref;
			middle = pbe.midd;
			suffix = pbe.suff;
			normalizedAlias = H1_DELIMITER + pbe.middTxt + H1_DELIMITER;
		}
		else {
			middle += suffix + pbe.midd;
			suffix = pbe.suff;
			normalizedAlias += pbe.middTxt + H1_DELIMITER;
		}

		// Allows for preliminary detection of parse ending
		frameCanExpand = oneParsed && ((parsingIx+1) < parsingBuffer.size());
		restartIx = -1;

		resetLAD();
		Stats.markIt(9);

		return oneParsed;
	}

	/** A shortcut method that extends the frame to cover the whole input
	 * string */
	public void parseAll() {
		while (frameCanExpand)  parseOne();
	}

	/** The method moves the start of the frame - one buffer element forward
	 * and shrinks the size of the frame to one element. */
	public void moveOne() {
		if (restartIx >= 0)  return;
		// Calculate the offset for new parsing start
		restartIx = frameIx + 1;

		// Conditions under which the frame-movement attempt fails
		if (restartIx >= parsingBuffer.size())
			restartIx = -1;
	}

	//==================================================
	// ParsingFrame: Extraction of frame dynamic flags
	//==================================================
	public boolean frameCanExpand() { return frameCanExpand; }
	public boolean frameCanMove() { return frameCanExpand || restartIx >= 0; }

	//==================================================
	// ParsingFrame: Extraction of frame state metrics
	//==================================================
	/** Retrieves the <code>HashedAlias</code> related Hash-Code-1. It is 
	 * calculated over the normalized form of the underlying text. A text
	 * transformation with externally provided transformation logic is 
	 * performed prior to hash-code calculation.
	 * @return - the value of the hash-code.
	 */
	public int getAliasHash1() {
		checkValid();
		if (aliasHash1==0)
			aliasHash1 = frameTT.transform(normalizedAlias).hashCode(); 
		return aliasHash1;
	}
	/** Retrieves the <code>HashedAlias</code> related Hash-Code-2. It is 
	 * calculated over the plain underlying text. A text
	 * transformation with externally provided transformation logic is 
	 * performed prior to hash-code calculation.
	 * @return - the value of the hash-code.
	 */
	public int getAliasHash2() {
		checkValid();
		if (aliasHash2==0)
			aliasHash2 = frameTT.transform(source.substring(
					aliasOffset1, aliasOffset2)).hashCode();
		return aliasHash2;
	}
	/** Retrieves the length of the non-alpha-numeric prefix of the frame
	 * @return non-alpha-numeric prefix length
	 */
	public int getPrefixLen() { checkValid(); return prefix; }
	/** Retrieves the length of the middle part of the frame
	 * @return middle part length
	 */
	public int getMiddleLen() { checkValid(); return middle; }
	/** Retrieves the length of the non-alpha-numeric suffix of the frame
	 * @return non-alpha-numeric suffix length
	 */
	public int getSuffixLen() { checkValid(); return suffix; }
	/** Retrieves the offset in the original input text of the frame start
	 * @return frame start offset
	 */
	public int getAliasStart() { checkValid(); return aliasOffset1; }
	/** Retrieves the offset in the original input text of the frame end
	 * @return frame end offset
	 */
	public int getAliasEnd() { checkValid(); return aliasOffset2; }
	/** Retrieves the length of the text corresponding to the current frame
	 * @return frame text length
	 */
	public int getLength() { checkValid(); return aliasOffset2-aliasOffset1; }
	/** Retrieves the count of ANLs in the frame
	 * @return ANL count
	 */
	public int getLexemeCount() { checkValid(); return lexemeCount; }

	private void checkValid() {
		if (restartIx >= 0)
			throw new RuntimeException("No parsed content in the Frame!");
	}

	//========================================
	// Methods overridden for Testing purposes
	//========================================
	@Override
	public boolean equals(Object o) {
		if (o instanceof ParsingFrame) {
			ParsingFrame pf = (ParsingFrame) o;
			return (pf.parsingBuffer.get(pf.frameIx).offset ==
				this.parsingBuffer.get(this.frameIx).offset &&
				pf.lexemeCount == this.lexemeCount &&
				pf.getAliasHash1() == this.getAliasHash1() );
		}
		return false;
	}

	@Override
	public int hashCode() {
		return 1;
	}
	
	@Override
	public String toString() {
		return this.normalizedAlias +
		"(" + parsingBuffer.get(frameIx).offset + "/" +
		source.length() + ")" +
		prefix + "-" + middle + "-" + suffix +
		((frameCanExpand)? " CAN-expand": " NO-expand") + " " +
		((restartIx>=0)? ("RELOCATE="+restartIx): "");
	}
}
