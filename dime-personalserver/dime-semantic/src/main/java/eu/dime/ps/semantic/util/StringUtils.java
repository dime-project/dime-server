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

package eu.dime.ps.semantic.util;

/**
 * Utility class to handle operations with strings.
 * 
 * @author Ismael Rivera
 */
public class StringUtils {

	private StringUtils() {}

	/**
	 * Same as {@link #strjoin(String, String)}, with a newline
	 * as the separator
	 */
	public static String strjoinNL(String... args) {
		return join("\n", args) ;
	}
	
	/**
	 * Concatenates strings, using a separator
	 */
	public static String strjoin(String sep, String... args) {
		return join(sep, args) ;
	}
	
	private static String join(String sep, String...args) {
		if (args.length == 0) {
			return "";
		}
		if (args.length == 1) {
			return args[0];
		}
		StringBuilder builder = new StringBuilder();
		builder.append(args[0]) ;
		for (int i = 1; i < args.length; i++) {
			if (sep != null) {
				builder.append(sep);
			}
			builder.append(args[i]);
		}
		return builder.toString();
	}

	public static int getLevenshteinDistance(String s, String t) {
		if (s == null || t == null) {
			throw new IllegalArgumentException("Strings must not be null");
		}
			
		/*
			The difference between this impl. and the previous is that, rather 
			 than creating and retaining a matrix of size s.length()+1 by t.length()+1, 
			 we maintain two single-dimensional arrays of length s.length()+1.	The first, d,
			 is the 'current working' distance array that maintains the newest distance cost
			 counts as we iterate through the characters of String s.	Each time we increment
			 the index of String t we are comparing, d is copied to p, the second int[].	Doing so
			 allows us to retain the previous cost counts as required by the algorithm (taking 
			 the minimum of the cost count to the left, up one, and diagonally up and to the left
			 of the current cost count being calculated).	(Note that the arrays aren't really 
			 copied anymore, just switched...this is clearly much better than cloning an array 
			 or doing a System.arraycopy() each time	through the outer loop.)

			 Effectively, the difference between the two implementations is this one does not 
			 cause an out of memory condition when calculating the LD over two very large strings.			
		*/		
			
		int n = s.length(); // length of s
		int m = t.length(); // length of t
			
		if (n == 0) {
			return m;
		} else if (m == 0) {
			return n;
		}

		int p[] = new int[n+1]; //'previous' cost array, horizontally
		int d[] = new int[n+1]; // cost array, horizontally
		int _d[]; //placeholder to assist in swapping p and d

		// indexes into strings s and t
		int i; // iterates through s
		int j; // iterates through t

		char t_j; // jth character of t

		int cost; // cost

		for (i = 0; i<=n; i++) {
			 p[i] = i;
		}
			
		for (j = 1; j<=m; j++) {
			 t_j = t.charAt(j-1);
			 d[0] = j;
			
			 for (i=1; i<=n; i++) {
					cost = s.charAt(i-1)==t_j ? 0 : 1;
					// minimum of cell to the left+1, to the top+1, diagonally left and up +cost				
					d[i] = Math.min(Math.min(d[i-1]+1, p[i]+1),	p[i-1]+cost);	
			 }

			 // copy current distance counts to 'previous row' distance counts
			 _d = p;
			 p = d;
			 d = _d;
		} 
			
		// our last action in the above loop was to switch d and p, so p now 
		// actually has the most recent cost counts
		return p[n];
	}
	
}
