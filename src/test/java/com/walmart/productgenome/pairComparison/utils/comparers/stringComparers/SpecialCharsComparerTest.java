package com.walmart.productgenome.pairComparison.utils.comparers.stringComparers;

import static org.junit.Assert.assertEquals;

import org.junit.Ignore;
import org.junit.Test;

import com.walmart.productgenome.pairComparison.utils.comparers.stringComparers.SpecialCharsComparer;

/**
 * Test class for 
 * @see {com.walmart.productgenome.pairComparison.utils.comparers.stringComparers.SpecialCharsComparer}
 * @author sprasa4
 *
 */
public class SpecialCharsComparerTest {

	SpecialCharsComparer comparer = new SpecialCharsComparer();
	
	@Test
	public void testHtmlCharsComparison()
	{
		String str1 = "&amp;";
		String str2 = "&";
		double score = comparer.compare(str1, str2);
		assertEquals(score, 1.0, 0.0);
		
		str1 = "amp;";
		str2 = "&";
		score = comparer.compare(str1, str2);
		assertEquals(score, 0.0, 0.0);				
	}
	
	@Ignore
	//TODO : Fix this
	public void testNonAsciiCharsComparison()
	{
		String str1 = "nï¿½vi";
		String str2 = "nvi";
		double score = comparer.compare(str1, str2);
		assertEquals(score, 1.0, 0.0);
		
		// TODO : Fix this
		/*
		str1 = "simple";
		str2 = "simple";
		score = comparer.compare(str1, str2);
		assertEquals(score, 0.0, 0.0);
		*/						
	}
}
