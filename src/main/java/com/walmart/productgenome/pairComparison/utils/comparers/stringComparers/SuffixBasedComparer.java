package com.walmart.productgenome.pairComparison.utils.comparers.stringComparers;

import com.walmart.productgenome.pairComparison.utils.comparers.IComparer;

/**
 * A comparer to determine the similarity of strings based on the presence of a common suffix.
 * @author sprasa4
 *
 */
public class SuffixBasedComparer implements IComparer{

	public double compare(String str1, String str2) 
	{
		if (str1 == null) throw new IllegalArgumentException("str1 cannot be null");
		if (str2 == null) throw new IllegalArgumentException("str2 cannot be null");
		
		String longerString;
		String shortString;
		
		if (str1.length() > str2.length()) {
			longerString = str1.toLowerCase();
			shortString = str2.toLowerCase();
		}
		else {
			longerString = str2.toLowerCase();
			shortString = str1.toLowerCase();
		}
		
		// Shorter string should be atleast 5 characters to be even considered for suffix search.
		// Also, difference of length between the two strings should not be greater than 3
		// characters as it can lead to false positives. Suffix search can be a bit tricky, so it is
		// better to be very conservative while matching suffix based strings.
		if(shortString.length() < 5 || (longerString.length() - shortString.length() > 3)) {
			return 0.0;
		}
		
		// Check if they even have the same suffix ?
		if(!longerString.endsWith(shortString)) {
			return 0.0;
		}
		
		/* Rules for suffix based similarity computation :
		 * Since we are allowing a difference of only 3 characters between small and long string,
		 * let us assume that with a difference of 3 characters similarity score is 0.8.
		 * Thus, 3 characters = 0.2 => per character score contribution = (0.2/3)
		 * 
		 * Examples :
		 * "ps643" vs "tmpps643" 		= 0.8
		 * "pps643" vs "tmpps643" 		= 0.8 + 1*(0.2/3)
		 * "mps643" vs "tmpps643" 		= 0.8 + 2*(0.2/3)
		 * "tmpps643" vs "tmpps643" 	= 0.8 + 3*(0.2/3)
		 */

		int suffixLength = shortString.length();
		int additionalSuffixCharacters = suffixLength - (longerString.length() - 3);
		
		return 0.8 + additionalSuffixCharacters*(0.2/3);
	}

}
