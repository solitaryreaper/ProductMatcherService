package com.walmart.productgenome.pairComparison.utils.comparers.stringComparers;


import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.walmart.productgenome.pairComparison.utils.comparers.numericComparers.IntToEnglishWordComparer;

/**
 * Test cases for @see {com.walmart.productgenome.pairComparison.utils.comparers.stringComparers.IntToEnglishWordComparer}
 * 
 * Tests that the various variations of a number are properly matched.
 * @author sprasa4
 *
 */
public class IntToEnglishWordComparerTest {

	private static IntToEnglishWordComparer comparer = new IntToEnglishWordComparer();
	
	@Test
	public void testIntToRomanNumeralWordTest()
	{
		String str1 = "2";
		String str2 = "II";
		double score = comparer.compare(str1, str2);
		assertEquals(1.0, score, 0.0);
		
		str1 = "2";
		str2 = "III";
		score = comparer.compare(str1, str2);
		assertEquals(0.0, score, 0.0);
	}
	
	@Test
	public void testIntToSimpleEnglishEquivalentWordTest()
	{
		String str1 = "2";
		String str2 = "two";
		double score = comparer.compare(str1, str2);
		assertEquals(1.0, score, 0.0);
		
		str1 = "2";
		str2 = "to";
		score = comparer.compare(str1, str2);
		assertEquals(0.0, score, 0.0);
	}
	
	@Test
	public void testIntToComplexEnglishEquivalentWordTest1()
	{
		String str1 = "2";
		String str2 = "second";
		double score = comparer.compare(str1, str2);
		assertEquals(1.0, score, 0.0);
		
		str1 = "2";
		str2 = "sec";
		score = comparer.compare(str1, str2);
		assertEquals(0.0, score, 0.0);
	}
	
	@Test
	public void testIntToComplexEnglishEquivalentWordTest2()
	{
		String str1 = "two";
		String str2 = "second";
		double score = comparer.compare(str1, str2);
		assertEquals(1.0, score, 0.0);
		
		str1 = "2";
		str2 = "sec";
		score = comparer.compare(str1, str2);
		assertEquals(0.0, score, 0.0);
	}

}
