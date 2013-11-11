package com.walmart.productgenome.pairComparison.utils.comparers.stringComparers;

import com.walmart.productgenome.pairComparison.utils.comparers.IComparer;

public class ConsonantsOnlyComparer implements IComparer {

	/*
	 * (non-Javadoc)
	 * @see com.walmart.productgenome.pairComparison.stringComparers.IStringComparer#compare(java.lang.String, java.lang.String)
	 */
	
	public double compare(final String str1, final String str2) {
		if (str1 == null) throw new IllegalArgumentException("str1 cannot be null");
		if (str2 == null) throw new IllegalArgumentException("str2 cannot be null");
		
		//if either string is empty, return a mismatch score of 0.0
		if (str1.isEmpty() == true) return 0.0;
		if (str2.isEmpty() == true) return 0.0;
		
		String str1Cleaned = removeVowels(str1.toLowerCase());
		String str2Cleaned = removeVowels(str2.toLowerCase());
		
		//the cleaned strings must at least have three characters
		if (str1Cleaned.length() < 3) return 0.0;
		if (str2Cleaned.length() < 3) return 0.0;
		
		if (str1Cleaned.equals(str2Cleaned) == true) {
			//the two strings are equal, how long are they?
			if (str1Cleaned.length() >= 5) return 1.0;
			if (str1Cleaned.length() == 4) return 0.9;
			if (str1Cleaned.length() == 3) return 0.8;
		}
		
		return 0.0;
	}
	
	private static String removeVowels(final String str) {
		String normalizedStr = str.toLowerCase();
		
		StringBuilder newBuilder = new StringBuilder();
		
		newBuilder.append(normalizedStr.charAt(0)); //adding the first character irrespective of whether it's a vowel or not
		for (int index=1; index < normalizedStr.length() - 1; index++) {
			char currChar = normalizedStr.charAt(index);
			
			if (currChar == 'a') continue;
			if (currChar == 'e') continue;
			if (currChar == 'i') continue;
			if (currChar == 'o') continue;
			if (currChar == 'u') continue;
			
			newBuilder.append(currChar);
		}
		
		// TODO : Ask aswath this looks buggy !!
		// Because of this "nacho" and "nch" wouldn't pass through this comparer.
		newBuilder.append(normalizedStr.charAt(normalizedStr.length() - 1));
		
		return newBuilder.toString();
	}

}
