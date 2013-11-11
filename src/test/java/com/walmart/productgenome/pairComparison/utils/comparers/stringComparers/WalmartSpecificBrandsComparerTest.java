package com.walmart.productgenome.pairComparison.utils.comparers.stringComparers;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class WalmartSpecificBrandsComparerTest {

	private static WalmartSpecificBrandsComparer comparer = new WalmartSpecificBrandsComparer();
	
	@Test
	public void testDomainSpecificKeywordsMap()
	{
		String str1 = "Rml";
		String str2 = "Rimmel";
		double score = comparer.compare(str1, str2);
		assertEquals(1.0, score, 0.0);
		
		str1 = "Best Occasions";
		str2 = "Wilton";
		score = comparer.compare(str1, str2);
		assertEquals(0.0, score, 0.0);		
	}

}
