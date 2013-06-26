/*
 * Copyright (c) 2005 - 2008 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Aperture BSD-style license.
 */
package eu.dime.commons.util;

/**
 * Methods related to actions on URIs. The definition of a URI is taken from <a
 * href="http://www.ietf.org/rfc/rfc2396.txt">RFC 2396: URI Generic Syntax</a>.
 */
public final class UriUtils {

    private static final String ESCAPE_CHARS = "<>%\"{}|\\^[]`";

    /**
     * Encodes a string according to RFC 2396. According to this spec, any characters outside the range 0x20 -
     * 0x7E must be escaped because they are not printable characters, except for characters in the fragment
     * identifier. Even within this range a number of characters must be escaped. This method will perform
     * this escaping.
     * 
     * @param uri The URI to encode.
     * @return The encoded URI.
     */
    public static String encodeUri(String uri) {
        StringBuilder result = new StringBuilder();
        encodeUri(uri, result);
        return result.toString();
    }

    /**
     * Encodes a string according to RFC 2396.
     * 
     * @param uri The URI to encode.
     * @param buffer The StringBuilder that the encoded URI will be appended to.
     * @see #encodeUri(java.lang.String)
     */
    public static void encodeUri(String uri, StringBuilder buffer) {
        int length = uri.length();
        char c;
        int cInt;
        String hexVal;

        for (int i = 0; i < length; i++) {
            c = uri.charAt(i);
            cInt = c;

            if (ESCAPE_CHARS.indexOf(c) >= 0 || cInt <= 0x20) {
                // escape character
                buffer.append('%');
                hexVal = Integer.toHexString(cInt);

                // ensure use of two characters
                if (hexVal.length() == 1) {
                    buffer.append('0');
                }
                buffer.append(hexVal);
            }
            else {
                buffer.append(c);
            }
        }
    }

    /**
     * Decodes a string according to RFC 2396. According to this spec, any characters outside the range 0x20 -
     * 0x7E must be escaped because they are not printable characters, except for any characters in the
     * fragment identifier. This method will translate any escaped characters back to the original.
     * 
     * @param uri The URI to decode.
     * @return The decoded URI.
     */
    public static String decodeUri(String uri) {
        StringBuilder result = new StringBuilder();
        decodeUri(uri, result);
        return result.toString();
    }

    /**
     * Decodes a string according to RFC 2396.
     * 
     * @param uri The URI to decode.
     * @param buffer The StringBuilder that the decoded URI will be appended to.
     * @see #decodeUri(java.lang.String)
     */
    public static void decodeUri(String uri, StringBuilder buffer) {
        int percentIdx = uri.indexOf('%');
        int startIdx = 0;

        while (percentIdx != -1) {
            buffer.append(uri.substring(startIdx, percentIdx));

            // the two character following the '%' contain a hexadecimal code for the original character,
            // i.e. '%20'
            String xx = uri.substring(percentIdx + 1, percentIdx + 3);
            buffer.append((char) Integer.parseInt(xx, 16));

            startIdx = percentIdx + 3;

            percentIdx = uri.indexOf('%', startIdx);
        }

        buffer.append(uri.substring(startIdx));
    }
}
