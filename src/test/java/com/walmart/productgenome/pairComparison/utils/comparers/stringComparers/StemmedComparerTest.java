package com.walmart.productgenome.pairComparison.utils.comparers.stringComparers;

import junit.framework.Assert;

import org.junit.Test;

public class StemmedComparerTest{

	private static StemmedComparer comparer = new StemmedComparer();
	
	@Test
	public void testStemmedComparer() throws Exception {
		{
			//sanity check - empty strings
			double score = comparer.compare("", "");
			Assert.assertEquals("the scores should be equal", 1.0, score);
		}
		
		{
			//sanity check - same strings
			double score = comparer.compare("absolute", "absolute");
			Assert.assertEquals("the scores should be equal", 1.0, score);
		}
		
		{
			double score = comparer.compare("drapes", "drape");
			Assert.assertEquals("the scores should be equal", 1.0, score);
		}
		
		{
			double score = comparer.compare("TALKING", "talks");
			Assert.assertEquals("the scores should be equal", 1.0, score);
		}
		
		{
			double score = comparer.compare("HOODED", "HOOD");
			Assert.assertEquals("the scores should be equal", 1.0, score);
		}
		
		{
			double score = comparer.compare("tee", "tees");
			Assert.assertEquals("the scores should be equal", 1.0, score);
		}
	}
	
}
