package com.walmart.productgenome.pairComparison.utils.comparers.stringComparers;

import junit.framework.Assert;

import org.junit.Test;

public class PrefixBasedComparerTest{

	private static PrefixBasedComparer comparer = new PrefixBasedComparer();

	@Test
	public void testPrefixBasedComparer() {
		{
			//sanity check - empty strings
			double score = comparer.compare("", "");
			Assert.assertEquals("the scores should be equal", 0.0, score);
		}
		
		{
			//sanity check - same strings
			double score = comparer.compare("absolute", "absolute");
			Assert.assertEquals("the scores should be equal", 1.0, score);
		}
		
		{
			double score = comparer.compare("Ribbed", "rib");
			Assert.assertEquals("the scores should be equal", 0.8, score);
		}
		
		{
			double score = comparer.compare("Ribbe", "ribb");
			Assert.assertEquals("the scores should be equal", 0.9, score);
		}
		
		{
			double score = comparer.compare("black", "bl");
			Assert.assertEquals("the scores should be equal", 0.0, score);
		}
	}
	
}
