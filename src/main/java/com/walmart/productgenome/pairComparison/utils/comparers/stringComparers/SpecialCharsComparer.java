package com.walmart.productgenome.pairComparison.utils.comparers.stringComparers;

import org.apache.commons.lang.StringEscapeUtils;

import com.walmart.productgenome.pairComparison.utils.comparers.IComparer;


/**
 * A comparer to determine similarity between special characters and their other representations.
 * Some of the special characters being considered are :
 * 		* HTML characters : &quot; and " are similar.
 * @author sprasa4
 *
 */
public class SpecialCharsComparer implements IComparer {

	public double compare(String str1, String str2) {
		if (str1 == null) throw new IllegalArgumentException("str1 cannot be null");
		if (str2 == null) throw new IllegalArgumentException("str2 cannot be null");

		double score = 0.0;
		 
		// check for equivalent html characters
		if(StringEscapeUtils.unescapeHtml(str1).equals(StringEscapeUtils.unescapeHtml(str2))) {
			score = 1.0;
		}
		else {
			// TODO : Fix this. It is including even non-control char string
			if(str1.contains("[^\\x20-\\x7e]") || str2.contains("[^\\x20-\\x7e]")) {
				// check for equivalent string representation after removing non-ascii characters
				String processedStr1 = str1.replaceAll("[^\\x20-\\x7e]", "");
				String processedStr2 = str2.replaceAll("[^\\x20-\\x7e]", "");
				if(processedStr1.equals(processedStr2)) {
					score = 1.0;
				}				
			}
		}
		
		return score;
	}

}
