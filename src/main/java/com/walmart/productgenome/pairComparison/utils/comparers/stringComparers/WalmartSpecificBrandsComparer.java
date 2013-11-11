package com.walmart.productgenome.pairComparison.utils.comparers.stringComparers;

import java.util.Map;

import com.google.common.collect.Maps;
import com.walmart.productgenome.pairComparison.utils.comparers.IComparer;

/**
 * A comparer specific to matching Walmart-specific brands.
 * 
 * Walmart Stores in general has very abbreviated brand names whereas DOTCOM has fully readable brand
 * names. This comparer containa a static list of mappings between these two data sources
 * 
 * @author sprasa4
 *
 */
public class WalmartSpecificBrandsComparer implements IComparer {
	
	private final static Map<String, String> walmartStoresToDotcomBrandsMap = Maps.newHashMap();
	static {
		walmartStoresToDotcomBrandsMap.put("EQ", "Equate");
		walmartStoresToDotcomBrandsMap.put("MS", "Mainstays");
		walmartStoresToDotcomBrandsMap.put("GE", "George");
		walmartStoresToDotcomBrandsMap.put("CP", "Canopy");
		walmartStoresToDotcomBrandsMap.put("RML", "Rimmel");
		walmartStoresToDotcomBrandsMap.put("Rel", "ReliOn");
		walmartStoresToDotcomBrandsMap.put("SuperT", "SuperTech");
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
		for(Map.Entry<String, String> entry : walmartStoresToDotcomBrandsMap.entrySet()) {
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
