package com.walmart.productgenome.pairComparison.utils.rule.calculator;

import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.List;

import org.junit.Test;

import com.google.common.collect.Lists;
import com.walmart.productgenome.pairComparison.audit.TokenAuditEntity;
import com.walmart.productgenome.pairComparison.model.Constants;
import com.walmart.productgenome.pairComparison.utils.comparers.ComparersFactory;
import com.walmart.productgenome.pairComparison.utils.comparers.IComparer;
import com.walmart.productgenome.pairComparison.utils.comparers.numericComparers.IntToEnglishWordComparer;
import com.walmart.productgenome.pairComparison.utils.comparers.stringComparers.ExactComparer;
import com.walmart.productgenome.pairComparison.utils.comparers.stringComparers.PrefixBasedComparer;
import com.walmart.productgenome.pairComparison.utils.comparers.stringComparers.StemmedComparer;
import com.walmart.productgenome.pairComparison.utils.comparers.stringComparers.SuffixBasedComparer;
import com.walmart.productgenome.pairComparison.utils.tokenizers.SeparatorBasedTokenizer;
import com.walmart.productgenome.pairComparison.utils.tokenizers.StandardAnalyzerTokenizer;
import com.walmart.productgenome.pairComparison.utils.tokenizers.WhiteSpaceTokenizer;

/**
 * Test methods for {@link com.walmart.productgenome.pairComparison.utils.rule.ContainmentScoreCalculator}
 * 
 * @author sprasa4
 *
 */
public class StringTokenContainmentScoreCalculatorTest{
	
	private static List<TokenAuditEntity> dummyTokenAuditValues = Lists.newArrayList();
	
	private static IComparer EXACT_STRING_COMPARER = new ExactComparer();
	private static IComparer PREFIX_STRING_COMPARER = new PrefixBasedComparer();
	private static IComparer SUFFIX_STRING_COMPARER = new SuffixBasedComparer();
	private static IComparer STEMMED_STRING_COMPARER = new StemmedComparer();
	private static IComparer INT_TO_ENGLISH_COMPARER = new IntToEnglishWordComparer();
	
	private static WhiteSpaceTokenizer TOKENIZER = new WhiteSpaceTokenizer();
	private static StandardAnalyzerTokenizer LUCENE_TOKENIZER = new StandardAnalyzerTokenizer();
	private static SeparatorBasedTokenizer SEPARATOR_TOKENIZER = new SeparatorBasedTokenizer(Constants.SEPARATOR_DELIMITER);
	
	private static double FULL_MATCH	 	= 1.0;
	private static double PARTIAL_MATCH 	= 0.8;
	private static double NO_MATCH     		= 0.0;
	
	/**
	 * Test case for verifying zero containment score of source item in target item. This implies
	 * that both the items are surely different.
	 */
	@Test
	public void testCaseZeroContainment()
	{
		List<String> sourceTokens = TOKENIZER.tokenize("canon powershot camera SD 430"); 
		List<List<String>> targetTokens = Lists.newArrayList();
		targetTokens.add(TOKENIZER.tokenize("nike air jordan shoe"));
		
		runTest(sourceTokens, targetTokens, Lists.newArrayList(EXACT_STRING_COMPARER), NO_MATCH);
	}
	
	/**
	 * Test case for verifying full containment score of source item in target item. This implies
	 * that both the items are exactly similar.
	 */
	@Test
	public void testCaseFullContainment()
	{
		List<String> sourceTokens = TOKENIZER.tokenize("canon powershot camera SD 430");
		List<List<String>> targetTokens = Lists.newArrayList();
		targetTokens.add(TOKENIZER.tokenize("canon powershot"));
		targetTokens.add(TOKENIZER.tokenize("camera SD"));
		targetTokens.add(TOKENIZER.tokenize("430"));
		
		runTest(sourceTokens, targetTokens, Lists.newArrayList(EXACT_STRING_COMPARER), FULL_MATCH);		
	}
	
	/**
	 * Test case to verify the containment of a source token that is a concatenation of multiple
	 * target tokens.
	 */
	@Test
	public void testCaseConcatenatedMatchSourceInTarget()
	{
		List<String> sourceTokens = TOKENIZER.tokenize("canon powershot camera SD430IS");
		List<List<String>> targetTokens = Lists.newArrayList();
		targetTokens.add(TOKENIZER.tokenize("canon powershot camera SD 430 IS"));
		
		runTest(sourceTokens, targetTokens, Lists.newArrayList(EXACT_STRING_COMPARER), FULL_MATCH);		
	}
	
	/**
	 * Test case to verify the containment of a target token that is a concatenation of multiple
	 * source tokens.
	 */
	@Test
	public void testCaseConcatenatedMatchTargetInSource()
	{
		List<String> sourceTokens = TOKENIZER.tokenize("canon powershot camera SD 430 IS");
		List<List<String>> targetTokens = Lists.newArrayList();
		targetTokens.add(TOKENIZER.tokenize("canon powershot camera SD430IS 430 IS"));
		
		runTest(sourceTokens, targetTokens, Lists.newArrayList(EXACT_STRING_COMPARER), FULL_MATCH);		
	}

	@Test
	public void testCaseConcatenatedMatchSourceInTarget2()
	{
		List<String> sourceTokens = TOKENIZER.tokenize("Claritin 2 oz");
		List<List<String>> targetTokens = Lists.newArrayList();
		targetTokens.add(TOKENIZER.tokenize("CLARITIN 2OZ GRAPE SYRUP"));
		
		runTest(sourceTokens, targetTokens, Lists.newArrayList(EXACT_STRING_COMPARER), FULL_MATCH);				
	}
	
	/**
	 * Test case to verify the containment of a source abbreviation token in multiple contiguous
	 * target tokens.
	 */
	@Test
	public void testCaseAbbreviationMatchSourceInTarget()
	{
		List<String> sourceTokens = TOKENIZER.tokenize("Movies Disc 1 POTC");
		List<List<String>> targetTokens = Lists.newArrayList();
		targetTokens.add(TOKENIZER.tokenize("Movies Disc 1 Pirates Of The Carribbean"));
		
		runTest(sourceTokens, targetTokens, Lists.newArrayList(EXACT_STRING_COMPARER), FULL_MATCH);		
	}
	
	/**
	 * Test case to verify the containment of a target abbreviation token in multiple contiguous
	 * source tokens.
	 */
	@Test
	public void testCaseAbbreviationMatchTargetInSource()
	{
		List<String> sourceTokens = TOKENIZER.tokenize("Movies Disc 1 Pirates Of The Carribbean");
		List<List<String>> targetTokens = Lists.newArrayList();
		targetTokens.add(TOKENIZER.tokenize("Movies Disc 1 POTC"));
		
		runTest(sourceTokens, targetTokens, Lists.newArrayList(EXACT_STRING_COMPARER), FULL_MATCH);		
	}
	
	/**
	 * Test case to verify a case where some of the tokens in source are stemmed variation of tokens
	 * in target item.
	 */
	@Test
	public void testCaseStemmedComparer()
	{
		List<String> sourceTokens = TOKENIZER.tokenize("drapes for sale");
		List<List<String>> targetTokens = Lists.newArrayList();
		targetTokens.add(TOKENIZER.tokenize("drape for sale"));
		
		runTest(sourceTokens, targetTokens, Lists.newArrayList(EXACT_STRING_COMPARER, STEMMED_STRING_COMPARER), FULL_MATCH);		
	}
	
	/**
	 * Test case to verify a case where some of the tokens in source share the same prefix with
	 * tokens in target item.
	 */
	@Test
	public void testCasePrefixComparer()
	{
		List<String> sourceTokens = TOKENIZER.tokenize("red shoe for sale");
		List<List<String>> targetTokens = Lists.newArrayList();
		targetTokens.add(TOKENIZER.tokenize("reddish shoe for sale"));
		
		runTest(sourceTokens, targetTokens, Lists.newArrayList(EXACT_STRING_COMPARER, STEMMED_STRING_COMPARER, PREFIX_STRING_COMPARER), 3.8/4.0);		
	}
	
	/**
	 * Test case to verify a case where some of the tokens in source share the same suffix with
	 * tokens in target items. For completeness, a negative case has also been provided.
	 */
	@Test
	public void testCaseSuffixComparer()
	{
		// Genuine suffix match case : "ps643" vs "tmpps643"
		List<String> sourceTokens = Lists.newArrayList("ps643");
		List<List<String>> targetTokens = Lists.newArrayList();
		targetTokens.add(TOKENIZER.tokenize("tmpps643 playstation"));
		runTest(sourceTokens, targetTokens, Collections.singletonList(SUFFIX_STRING_COMPARER), PARTIAL_MATCH);
		
		// Should not match this suffix case. Difference between string length > 3
		sourceTokens = Lists.newArrayList("station");
		targetTokens = Lists.newArrayList();
		targetTokens.add(Lists.newArrayList("playstation"));
		runTest(sourceTokens, targetTokens, Collections.singletonList(SUFFIX_STRING_COMPARER), NO_MATCH);
	}
	
	/**
	 * Test case to verify a case where an integer is present as a token but with different
	 * representation at both source and target item, possibly an english and arabic numeral 
	 * representation.
	 */
	@Test
	public void testCaseSmallIntegerConversion()
	{
		// Genuine match case : "1 440W bulb" vs "One 440W bulb"
		List<String> sourceTokens = TOKENIZER.tokenize("1 440W bulb");
		List<List<String>> targetTokens = Lists.newArrayList();
		targetTokens.add(TOKENIZER.tokenize("One 440W bulb"));
		runTest(sourceTokens, targetTokens, Lists.newArrayList(EXACT_STRING_COMPARER, INT_TO_ENGLISH_COMPARER), FULL_MATCH);
		
		// Genuine match case : "5.0 ampere bulb" vs "Five ampere bulb"
		sourceTokens = TOKENIZER.tokenize("5.0 ampere bulb");
		targetTokens = Lists.newArrayList();
		targetTokens.add(TOKENIZER.tokenize("Five ampere bulb"));
		runTest(sourceTokens, targetTokens, Lists.newArrayList(EXACT_STRING_COMPARER, INT_TO_ENGLISH_COMPARER), FULL_MATCH);
	}
	
	/**
	 * Test case to verify a case where conversion of roman numeral to integer should lead to
	 * exact match on both the ends.
	 */
	@Test
	public void testCaseRomanNumeralConversion()
	{
		List<String> sourceTokens = TOKENIZER.tokenize("Otterbox Defender Series Case for Samsung Galaxy Tab II");
		List<List<String>> targetTokens = Lists.newArrayList();
		targetTokens.add(TOKENIZER.tokenize("OtterBox Defender Series - Case for web tablet - polycarbonate - black - for Samsung Galaxy Tab 2 (7.0), Galaxy Tab 2 (7.0) WiFi"));
		runTest(sourceTokens, targetTokens, Lists.newArrayList(EXACT_STRING_COMPARER, INT_TO_ENGLISH_COMPARER), 0.95);		
	}
	
	/**
	 * Should fail to match "Pack" token.
	 */
	
	public void testCaseFuzzyMatch1()
	{
		List<String> sourceTokens = LUCENE_TOKENIZER.tokenize("Sony 50-Pack BD-R Spindle");
		List<List<String>> targetTokens = Lists.newArrayList();
		targetTokens.add(LUCENE_TOKENIZER.tokenize("Sony BNR25AP6 - 50 x BD-R - 25 GB 6x - ink jet printable surface - spindle - storage media"));
		runTest(sourceTokens, targetTokens, ComparersFactory.getComparers(Constants.FUZZY_STRING_COMPARER), 0.80);		
	}
	
	/**
	 * Only difference is between "mount" and "mounting" token, so should match well.
	 */
	@Test
	public void testCaseFuzzyMatch2()
	{
		List<String> sourceTokens = LUCENE_TOKENIZER.tokenize("Humminbird MS M Quick Disconnect Mount");
		List<List<String>> targetTokens = Lists.newArrayList();
		targetTokens.add(LUCENE_TOKENIZER.tokenize("Humminbird MS M - Mounting kit - for 300 Series; 500 Series; 700 Series; Matrix 12, 47 3D"));
		targetTokens.add(LUCENE_TOKENIZER.tokenize("Quick Disconnect Mounting system"));
		runTest(sourceTokens, targetTokens, ComparersFactory.getComparers(Constants.FUZZY_STRING_COMPARER), 0.95);				
	}
	
	/*
	@Test
	public void testCaseProdError()
	{
		List<String> sourceTokens = Lists.newArrayList("var_color=black", "var_tea_types=black", "var_finishes_materials=steel", "var_animals=pelican");
		List<List<String>> targetTokens = null;
		List<IComparer> comparers = Lists.newArrayList(ComparersFactory.getComparers(Constants.FUZZY_STRING_COMPARER));
		runTest(sourceTokens, targetTokens, comparers, 0.95);
		
	}
	*/
	
	@Test
	public void testPdTitleVariationPhrases()
	{
		List<String> sourceTokens = SEPARATOR_TOKENIZER.tokenize("var_types_of_wine=White;#var_school_subjects=Band;#");
		List<List<String>> targetTokens = Lists.newArrayList();
		targetTokens.add(SEPARATOR_TOKENIZER.tokenize("var_school_subjects=Band;#var_types_of_wine=White;#"));
		
		runTest(sourceTokens, targetTokens, ComparersFactory.getComparers(Constants.EXACT_STRING_COMPARER), 1.00);
	}
	
	@Test
	public void testPdTitleNumberUnits()
	{
		List<String> sourceTokens = SEPARATOR_TOKENIZER.tokenize("42.0 Inch;#");
		List<List<String>> targetTokens = Lists.newArrayList();
		targetTokens.add(SEPARATOR_TOKENIZER.tokenize("42.0 Inch;#"));
		
		runTest(sourceTokens, targetTokens, ComparersFactory.getComparers(Constants.EXACT_STRING_COMPARER), 1.00);		
	}
	
	// Utility method to invoke the actual product matching for string tokens
	private static void runTest(List<String> sourceTokens, List<List<String>> targetTokens, List<IComparer> comparers, double expectedScore)
	{
		StringTokenContainmentScoreCalculator calculator = new StringTokenContainmentScoreCalculator();
		double score = calculator.containmentScore(sourceTokens, targetTokens, comparers, dummyTokenAuditValues);
		assertTrue(score >= expectedScore);		
	}
}
