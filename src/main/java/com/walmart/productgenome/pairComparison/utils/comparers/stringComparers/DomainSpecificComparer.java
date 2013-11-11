package com.walmart.productgenome.pairComparison.utils.comparers.stringComparers;

import java.util.Map;

import com.google.common.collect.Maps;
import com.walmart.productgenome.pairComparison.utils.comparers.IComparer;

/**
 * Comparer to handle mapping of domain specific words based on a static dictionary. This would
 * be moved to the product discovery pipeline but as of now I am just adding it here.
 * 
 * This can be used to handle some synonyms like "Laptop" vs "Notebook".
 * This can be used to handle some complex abbreviations like "WS" vs "WideScreen".
 * 
 * @author sprasa4
 *
 */
public class DomainSpecificComparer implements IComparer {

	private final static Map<String, String> domainSpecificSimilarWordsMap = Maps.newHashMap();
	static {
		domainSpecificSimilarWordsMap.put("ws", "widescreen");
		domainSpecificSimilarWordsMap.put("laptop", "notebook");
		domainSpecificSimilarWordsMap.put("ff", "fullframe");
	}

	
	public double compare(String str1, String str2) {
		if (str1 == null) throw new IllegalArgumentException("str1 cannot be null");
		if (str2 == null) throw new IllegalArgumentException("str2 cannot be null");
		
		//if either string is empty, return a mismatch score of 0.0
		if (str1.isEmpty() == true) return 0.0;
		if (str2.isEmpty() == true) return 0.0;

		String normalizedStr1 = str1.toLowerCase();
		String normalizedStr2 = str2.toLowerCase();
		double score = 0.0;
		for(Map.Entry<String, String> entry : domainSpecificSimilarWordsMap.entrySet()) {
			String key = entry.getKey().toLowerCase();
			String value = entry.getValue().toLowerCase();
			
			if((key.equals(normalizedStr1) && value.equals(normalizedStr2)) ||
			   (key.equals(normalizedStr2) && value.equals(normalizedStr1)))
			{
				score = 1.0;
			}
		}
		
		return score;
	}

}
