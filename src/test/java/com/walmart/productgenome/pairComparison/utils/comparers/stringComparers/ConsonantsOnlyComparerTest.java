package com.walmart.productgenome.pairComparison.utils.comparers.stringComparers;

import org.junit.Ignore;
import org.junit.Test;

import com.walmart.productgenome.pairComparison.utils.comparers.stringComparers.ConsonantsOnlyComparer;

import junit.framework.Assert;
import junit.framework.TestCase;

public class ConsonantsOnlyComparerTest{
	private ConsonantsOnlyComparer comparer = new ConsonantsOnlyComparer();
	
	@Ignore
	public void testConstantsOnlyComparer() {
		{
			//sanity check - empty strings
			double score = comparer.compare("", "");
			Assert.assertEquals("the scores should be equal", 0.0, score);
		}
		
		{
			double score = comparer.compare("stick", "stck");
			Assert.assertEquals("the scores should be equal", 0.9, score);
		}
		
		{
			double score = comparer.compare("blaak", "blk");
			Assert.assertEquals("the scores should be equal", 0.8, score);
		}
		
		{
			double score = comparer.compare("skintimate", "skntmt");
			Assert.assertEquals("the scores should be equal", 0.0, score);
		}
	}

	@Test
	public void testConsonants2()
	{
		String str1 = "nacho";
		String str2 = "nch";
		
		double score = comparer.compare(str1, str2);
		System.out.println("Score : " + score);
	}
}
