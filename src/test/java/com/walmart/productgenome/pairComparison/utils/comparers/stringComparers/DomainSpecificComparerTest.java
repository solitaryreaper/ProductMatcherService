package com.walmart.productgenome.pairComparison.utils.comparers.stringComparers;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class DomainSpecificComparerTest {

	private static DomainSpecificComparer comparer = new DomainSpecificComparer();
	
	@Test
	public void testDomainSpecificKeywordsMap()
	{
		String str1 = "WS";
		String str2 = "WideScreen";
		double score = comparer.compare(str1, str2);
		assertEquals(1.0, score, 0.0);
		
		str1 = "Wide";
		str2 = "WideScreen";
		score = comparer.compare(str1, str2);
		assertEquals(0.0, score, 0.0);		
	}
}
