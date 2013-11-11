package com.walmart.productgenome.pairComparison.utils;

import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.junit.Test;

import com.google.common.collect.Lists;
import com.walmart.productgenome.pairComparison.utils.comparers.IComparer;
import com.walmart.productgenome.pairComparison.utils.comparers.stringComparers.ConsonantsOnlyComparer;
import com.walmart.productgenome.pairComparison.utils.comparers.stringComparers.ExactComparer;
import com.walmart.productgenome.pairComparison.utils.comparers.stringComparers.StemmedComparer;

/**
 * Test cases for @{MatchUtils} 
 * 
 * Matching has been implemented as a case-insensitive comparison in our product matching library.
 * So, all the test tokens used here are put in lowercase because this is how the input arrives
 * to this utility class during the matching process.
 *  
 * @author sprasa4
 *
 */
public class MatchUtilsTest extends TestCase {

	/**
	 * Test case to determine if a source token and a list of target tokens represent an 
	 * abbreviation match or not.
	 */
	@Test
	public void testAbbreviations()
	{
		String abbrevToCheck = null;
		List<String> potentialAbbrevExpansion = null;
		int result = -1;

		int FAIL_SCORE = 0;
		
		// An empty string should not be matched as abbreviation
		abbrevToCheck = "";
		potentialAbbrevExpansion = Lists.newArrayList("m");
		result = MatchUtils.checkAbbreviationMatch(abbrevToCheck, potentialAbbrevExpansion, 0);
		assertEquals(FAIL_SCORE, result);
		
		// A single character potential abbreviation should not be matched
		abbrevToCheck = "m";
		potentialAbbrevExpansion = Lists.newArrayList("match");
		result = MatchUtils.checkAbbreviationMatch(abbrevToCheck, potentialAbbrevExpansion, 0);
		assertEquals(FAIL_SCORE, result);
		
		// Numeric abbreviations should not be matched
		abbrevToCheck = "1";
		potentialAbbrevExpansion = Lists.newArrayList("123");
		result = MatchUtils.checkAbbreviationMatch(abbrevToCheck, potentialAbbrevExpansion, 0);
		assertEquals(FAIL_SCORE, result);
		
		// Genuine abbreviation case - this should match
		// Note : The matching process always converts the strings to lower case using tokenizer.
		abbrevToCheck = "potc";
		potentialAbbrevExpansion = Lists.newArrayList("pirates", "of", "the" , "carribean");
		result = MatchUtils.checkAbbreviationMatch(abbrevToCheck, potentialAbbrevExpansion, 0);
		assertEquals(4, result);		
		
		// Genuine mismatch
		abbrevToCheck = "potc";
		potentialAbbrevExpansion = Lists.newArrayList("pirates", "of", "the" , "pacific");
		result = MatchUtils.checkAbbreviationMatch(abbrevToCheck, potentialAbbrevExpansion, 0);
		assertEquals(FAIL_SCORE, result);		
		
	}
	
	/**
	 * Test case to determine if a source token is a concatenated match of target tokens.
	 */
	@Test
	public void testConcatenatedMatches()
	{
		String tokenToMatch = null;
		List<String> tokensToMatchAgainst = null;
		int result = -1;
		
		// Genuine concatenated match case
		tokenToMatch = "sd400is";
		tokensToMatchAgainst = Lists.newArrayList("sd400", "is");
		result = MatchUtils.findConcatenatedMatches(tokenToMatch, tokensToMatchAgainst, 0);
		assertTrue(result > 0);
		
		// This should not match.
		tokenToMatch = "sd400is";
		tokensToMatchAgainst = Lists.newArrayList("sd400", "rs");
		result = MatchUtils.findConcatenatedMatches(tokenToMatch, tokensToMatchAgainst, 0);
		assertEquals(0, result);
	}
	
	/**
	 * Test case to check the correct conversion of roman numeral to corresponding numeric version.
	 */
	@Test
	public void testRomanNumeralConversion()
	{
		List<String> tokensToTest = null;
		
		// Test genuine case
		tokensToTest = Lists.newArrayList("Samsung", "Galaxy", "ii");
		tokensToTest = MatchUtils.processRomanNumeralTokens(tokensToTest);
		assertEquals(tokensToTest.get(2), "2");
		
		// No processing case
		tokensToTest = Lists.newArrayList("Samsung", "Galaxy");
		List<String> tokensAfterProcessing = MatchUtils.processRomanNumeralTokens(tokensToTest);
		assertEquals(tokensToTest, tokensAfterProcessing);
	}

	/**
	 * Test case to check whether a pure integer in the token stream is correctly mapped to 
	 * corresponding english representation or not.
	 */
	@Test
	public void testPureIntegerToStringConversion()
	{
		List<String> tokensToTest = null;
		
		// Test genuine case : Samsung Galaxy 2 -> Samsung Galaxy two
		tokensToTest = Lists.newArrayList("Samsung", "Galaxy", "2");
		tokensToTest = MatchUtils.processSmallIntegerTokens(tokensToTest);
		assertEquals("two", tokensToTest.get(2));
		
		// Another genuine case : Samsung Galaxy 2.0 -> Samsung Galaxy two
		tokensToTest = Lists.newArrayList("Samsung", "Galaxy", "2.0");
		tokensToTest = MatchUtils.processSmallIntegerTokens(tokensToTest);
		assertEquals("two", tokensToTest.get(2));
		
		// Negative case. Only pure integer or equivalent tokens are processed. Real numbers are 
		// retained as it is.
		tokensToTest = Lists.newArrayList("Samsung", "Galaxy", "2.3");
		tokensToTest = MatchUtils.processSmallIntegerTokens(tokensToTest);
		assertEquals("2.3", tokensToTest.get(2));		
	}
	
	@Test
	public void testCompareStrings()
	{
		String str1 = "fan";
		String str2 = "fan";
		List<IComparer> comparers = Lists.newArrayList();
		comparers.add(new ConsonantsOnlyComparer());
		comparers.add(new ExactComparer());
		comparers.add(new StemmedComparer());
		
		Map<IComparer, Double> resultMap = MatchUtils.compareStrings(str1, str2, comparers);
		IComparer resultComparer = null;
		Double score = null;
		for(Map.Entry<IComparer, Double> entry : resultMap.entrySet()) {
			resultComparer = entry.getKey();
			score = entry.getValue();
		}
				
		assertEquals(1, resultMap.size());
		assertEquals(ExactComparer.class, resultComparer.getClass());
		assertEquals(1.0, score);
	}
}
