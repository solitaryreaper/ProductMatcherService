package com.walmart.productgenome.pairComparison.utils.comparers.stringComparers;
import com.walmart.productgenome.pairComparison.utils.comparers.IComparer;

public class PrefixBasedComparer implements IComparer {

	/*
	 * (non-Javadoc)
	 * @see com.walmart.productgenome.pairComparison.stringComparers.IStringComparer#compare(java.lang.String, java.lang.String)
	 */
	
	public double compare(final String str1, final String str2) {
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
		
		//the short string is the putative prefix. The prefix should be 
		//at least three characters
		if (shortString.length() < 3) return 0.0;
		
		//is the short string is even a prefix?
		if (longerString.startsWith(shortString) == false) return 0.0;
		
		//alright, the short string is a pefix and is at least three characters,
		//now compute the prefix score
		
		//"Silver" vs "Sil" = 0.8
		//"Silver" vs "Silver" = 1.0 (3 characters = 0.2, per character score = 0.2/3)
		//"Silver" vs "Silv" = 0.8 + 1*(0.2/3)
		//"Silver" vs "Silve" = 0.8 + 2*(0.2/3)
		//"Silver" vs "Silver" = 0.8 + 3*(0.2/3)
		
		int prefixLength = shortString.length();
		int remainingCharacters = longerString.length() - 3;
		double remainingCharactersDouble = (double)remainingCharacters;
		int additionalPrefixCharacters = prefixLength - 3;
		
		return 0.8 + additionalPrefixCharacters*(0.2/remainingCharactersDouble);
		
		
	}

}
