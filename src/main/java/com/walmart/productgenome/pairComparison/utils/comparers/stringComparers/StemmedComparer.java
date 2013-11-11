package com.walmart.productgenome.pairComparison.utils.comparers.stringComparers;

import org.tartarus.martin.Stemmer;

import com.walmart.productgenome.pairComparison.utils.comparers.IComparer;

/**
 * Uses the Porter Stemmer to compare strings, so that strings like
 * "drapes" and "drape" will be considered the same.
 * 
 * @author amanoh1
 * 
 */
public class StemmedComparer implements IComparer {
	
	/*
	 * (non-Javadoc)
	 * @see com.walmart.productgenome.pairComparison.stringComparers.IStringComparer#compare(java.lang.String, java.lang.String)
	 */
	
	public double compare(String str1, String str2) {
		if (str1 == null) throw new IllegalArgumentException("str1 cannot be null");
		if (str2 == null) throw new IllegalArgumentException("str2 cannot be null");
		
		String str1Stemmed = stemString(str1.toLowerCase());
		String str2Stemmed = stemString(str2.toLowerCase());
		
		if (str1Stemmed.equals(str2Stemmed) == true) return 1.0;
		
		//the stemmed strings aren't equal
		return 0.0;
	}
	
	private String stemString(final String str) {
		//initialize the stemmer
		Stemmer stemmer = new Stemmer();
		
		//add the characters of the string to the stemmer
		for (int index=0; index < str.length(); index++) {
			stemmer.add(str.charAt(index));
		}
		
		//stem the word and return it
		stemmer.stem();
		return stemmer.toString();
	}

}
