package com.walmart.productgenome.pairComparison.utils.comparers.numericComparers;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ExactComparerTest {

	private ExactNumericComparer comparer = new ExactNumericComparer();
	
	@Test
	public void testSameValueSameRepresentationInteger()
	{
		String str1 = "12";
		String str2 = "12";
		
		double score = comparer.compare(str1, str2);
		assertEquals(1.0, score, 0.0);
	}
	
	@Test
	public void testSameValueDiffRepresentationInteger()
	{
		String str1 = "012";
		String str2 = "12";
		
		double score = comparer.compare(str1, str2);
		assertEquals(1.0, score, 0.0);		
	}
	
	@Test
	public void testSameValueSameRepresentationDouble()
	{
		String str1 = "12.0";
		String str2 = "12.0";
		
		double score = comparer.compare(str1, str2);
		assertEquals(1.0, score, 0.0);		
	}
	
	@Test
	public void testSameValueDiffRepresentationDouble()
	{
		String str1 = "012.0";
		String str2 = "012.0";
		
		double score = comparer.compare(str1, str2);
		assertEquals(1.0, score, 0.0);		
	}
}
