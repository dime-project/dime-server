package com.ontotext.kim.model;

import java.io.Serializable;

/**
 * This class represents an Entity Alias in the form of couple hash codes.<br>
 * <br>
 * For this representation the text is considered a sequence of plain numeric
 * and letter lexemes delimited by optional non-alpha-numeric character
 * sequences of arbitrary length. The text of the Alias is considered in two
 * forms:<br>
 * "Normalized Form" - the letter and number lexemes of the Alias delimited
 * by special symbol and concatenated in a string;<br>
 * "Plain Form" - the string of the Alias without modifications;<br>
 * The Normalized Form string is used to calculate <code>aliasHash1</code> hash
 * value and the Plain Form string is used to calculate <code>aliasHash2</code>
 * hash value. This couple of hash values are used to identify the presence of
 * the Alias in the parsed text.<br>
 * The <code>aliasHash1</code> is used for preliminary detection of a possible
 * Alias location. Because several Aliases can share same value of
 * <code>aliasHash1</code> - it is not stored as member of the
 * <code>HashedAlias</code> class, but in the containing structure, which is the
 * specially designed class <code>HashRegister</code>.<br>
 * <br>
 * The <code>aliasHash2</code> hash value is used for final verification and
 * is stored inside the <code>HashedAlias</code> class. The searched text
 * is processed lexeme-by-lexeme through the class <code>ParsingFrame</code>.
 * The <code>aliasHash2</code> is calculated from the plain text of the checked
 * text fragment. For fast localization of the text fragment which is
 * candidate for matching the Alias, the fields <code>prefLen</code> and
 * <code>suffLen</code> are added. They contain the lengths of the
 * non-alpha-numeric prefix and suffix of the stored Alias. This couple of
 * values is intended for use with the method
 * <code>ParsingFrame.setNewPrefSufLen</code><br>
 * Last two members of the class <code>HashedAlias</code> are the instance
 * and the semantic class identifiers of the Entity corresponding to the Alias.
 * They are stored in packed form to save memory.
 * 
 * @author danko
 *
 */
public class HashedAlias implements Serializable, Comparable<HashedAlias> {
  private static final long serialVersionUID = 4500L;

  //===========================
  // Hashed alias - data fields 
  //===========================
  /** The hash-code of the whole Alias */
  public final int aliasHash2;

  /** The size of the symbolic prefix */
  public final byte prefLen;
  /** The size of the symbolic suffix */
  public final byte suffLen;

  // Properties of the related Entity
  /** The internal class identifier of the Entity */
  public final int classID;
  /** The compressed instance URI of the Entity*/
  public final String shortInstURI;

  /**
   * This constructor initializes all final fields of the class and checks
   * for valid value ranges.
   * @param aliasHash2 - the hash-code value derived from the plain Alias text
   * @param prefLen - the size of the non-alpha-numeric prefix of the Alias
   * @param suffLen - the size of the non-alpha-numeric suffix of the Alias
   * @param shortInstURI - the compressed instance identifier
   * @param classID - the encoded semantic class identifier
   */
  public HashedAlias(int aliasHash2, int prefLen, int suffLen,
          String shortInstURI, int classID) {
    if (prefLen > 127 || suffLen > 127 )
      throw new RuntimeException("Cannot create HashedAlias with" +
      "symbolic prefix/suffix longer than 127!");
    this.aliasHash2 = aliasHash2;
    this.prefLen = (byte) prefLen;
    this.suffLen = (byte) suffLen;
    this.classID = classID;
    this.shortInstURI = shortInstURI;
  }

  public int compareTo(HashedAlias o) {
    if (this.prefLen < o.prefLen)  return -1;
    else if (this.prefLen == o.prefLen && this.suffLen < o.suffLen) return -1;
    else if (this.prefLen == o.prefLen && this.suffLen == o.suffLen
            && this.aliasHash2 < o.aliasHash2) return -1;
    else if (this.prefLen == o.prefLen && this.suffLen == o.suffLen
            && this.aliasHash2 == o.aliasHash2) {
      // Here null is considered the smallest possible string value 
      if (this.shortInstURI == null) {
        if (o.shortInstURI == null)
          return 0;
        else
          return -1;
      }
      else {
        if (o.shortInstURI == null)
          return 1;
        else
          return this.shortInstURI.compareTo(o.shortInstURI);
      }
    }
    return 1;
  }

  @Override
  public boolean equals(Object obj) {
    if (! (obj instanceof HashedAlias) )  return false;
    HashedAlias he = (HashedAlias) obj;

    if (he.aliasHash2 != this.aliasHash2)
      return false;

    if (he.prefLen != this.prefLen)
      return false;

    if (he.suffLen != this.suffLen)
      return false;

    if (he.shortInstURI == null) {
      if (this.shortInstURI != null)
        return false;
    }
    else {
      if (!he.shortInstURI.equals(this.shortInstURI))
        return false;
    }

    return classID == he.classID;
  }

  @Override
  public String toString() {
    return "(" + this.prefLen + "<"+ this.aliasHash2 +">" + this.suffLen + ")"+
    this.shortInstURI;
  }

}
