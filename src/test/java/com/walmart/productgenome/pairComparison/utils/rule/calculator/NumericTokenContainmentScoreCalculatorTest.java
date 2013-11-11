package com.walmart.productgenome.pairComparison.utils.rule.calculator;

import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;

import com.google.common.collect.Lists;
import com.walmart.productgenome.pairComparison.audit.TokenAuditEntity;
import com.walmart.productgenome.pairComparison.utils.comparers.IComparer;
import com.walmart.productgenome.pairComparison.utils.comparers.numericComparers.DifferenceComparer;
import com.walmart.productgenome.pairComparison.utils.comparers.numericComparers.ExactNumericComparer;
import com.walmart.productgenome.pairComparison.utils.tokenizers.WhiteSpaceTokenizer;

/**
 * Test cases for {@link com.walmart.productgenome.pairComparison.utils.rule.calculator.DoubleTokenContainmentScoreCalculator}
 * @author sprasa4
 *
 */
public class NumericTokenContainmentScoreCalculatorTest extends TestCase {
	
	private static List<TokenAuditEntity> dummyTokenAuditValues = Lists.newArrayList();
	
	private static IComparer EXACT_DOUBLE_COMPARER = new ExactNumericComparer();
	private static IComparer FUZZY_DOUBLE_COMPARER = new DifferenceComparer();	

	private static WhiteSpaceTokenizer TOKENIZER = new WhiteSpaceTokenizer();
	
	private static double FULL_MATCH	 	= 1.0;
	private static double NO_MATCH     		= 0.0;
	
	/**
	 * In this example, length dimension is taken as an example attribute to base the double token tests on.
	 */

	/**
	 * Test case to show no containment for exact match of double tokens. 
	 */
	@Test
	public void testCaseExactMatchZeroContainment()
	{
		List<String> sourceTokens = TOKENIZER.tokenize("19.7 in"); 
		List<List<String>> targetTokens = Lists.newArrayList();
		targetTokens.add(TOKENIZER.tokenize("19.9 in"));
		
		runTest(sourceTokens, targetTokens, Lists.newArrayList(EXACT_DOUBLE_COMPARER), NO_MATCH);		
	}

	/**
	 * Test case to show no containment for exact match of double tokens. If two books have
	 * the same year of publishing then the match succeeds.
	 */
	@Test
	public void testCaseExactMatchFullContainment()
	{
		List<String> sourceTokens = TOKENIZER.tokenize("19.7 in"); 
		List<List<String>> targetTokens = Lists.newArrayList();
		targetTokens.add(TOKENIZER.tokenize("19.7 in"));
		
		runTest(sourceTokens, targetTokens, Lists.newArrayList(EXACT_DOUBLE_COMPARER), FULL_MATCH);			
	}

	/**
	 * Test case to ensure that the calculator is only invoked for double tokens. For any other
	 * tokens it should return a score of 0.
	 */
	@Test
	public void testCaseInvalidAttributeInputToCalculator()
	{
		List<String> sourceTokens = TOKENIZER.tokenize("canon powershot camera SD");
		List<List<String>> targetTokens = Lists.newArrayList();
		targetTokens.add(TOKENIZER.tokenize("canon powershot"));
		targetTokens.add(TOKENIZER.tokenize("camera SD"));
		
		runTest(sourceTokens, targetTokens, Lists.newArrayList(EXACT_DOUBLE_COMPARER, FUZZY_DOUBLE_COMPARER), NO_MATCH);						
	}
	
	/**
	 * Test case to show no containment for fuzzy match of double tokens. 
	 */
	@Test
	public void testCaseFuzzyMatchNoContainment()
	{
		List<String> sourceTokens = TOKENIZER.tokenize("19.7 in"); 
		List<List<String>> targetTokens = Lists.newArrayList();
		targetTokens.add(TOKENIZER.tokenize("29.7 in"));
		
		runTest(sourceTokens, targetTokens, Lists.newArrayList(FUZZY_DOUBLE_COMPARER), NO_MATCH);			
	}

	/**
	 * Test case to show full containment for fuzzy match of double tokens. 
	 */
	@Test
	public void testCaseFuzzyMatchFullContainment()
	{
		List<String> sourceTokens = TOKENIZER.tokenize("19.7 in"); 
		List<List<String>> targetTokens = Lists.newArrayList();
		targetTokens.add(TOKENIZER.tokenize("19.7 in"));
		
		runTest(sourceTokens, targetTokens, Lists.newArrayList(FUZZY_DOUBLE_COMPARER), FULL_MATCH);			
	}
	
	/**
	 * Test case to show partial containment for fuzzy match of double tokens. 
	 */
	@Test
	public void testCaseFuzzyMatchPartialContainment1()
	{
		List<String> sourceTokens = TOKENIZER.tokenize("19.7 in"); 
		List<List<String>> targetTokens = Lists.newArrayList();
		targetTokens.add(TOKENIZER.tokenize("20.4 in"));
		
		runTest(sourceTokens, targetTokens, Lists.newArrayList(FUZZY_DOUBLE_COMPARER), 0.9);			
	}

	/**
	 * Test case to show partial containment for fuzzy match of double tokens. 
	 */
	@Test
	public void testCaseFuzzyMatchPartialContainment2()
	{
		List<String> sourceTokens = TOKENIZER.tokenize("19.7 in"); 
		List<List<String>> targetTokens = Lists.newArrayList();
		targetTokens.add(TOKENIZER.tokenize("20.8 in"));
		
		runTest(sourceTokens, targetTokens, Lists.newArrayList(FUZZY_DOUBLE_COMPARER), 0.8);			
	}
	
	@Test
	public void testCaseSameValueDiffRepresentation()
	{
		List<String> sourceTokens = TOKENIZER.tokenize("345"); 
		List<List<String>> targetTokens = Lists.newArrayList();
		targetTokens.add(TOKENIZER.tokenize("0345"));
		
		runTest(sourceTokens, targetTokens, Lists.newArrayList(EXACT_DOUBLE_COMPARER), 1.0);		
	}
	
	// Utility method that runs the containment tests on various token configurations
	private static void runTest(List<String> sourceTokens, List<List<String>> targetTokens, List<IComparer> comparers, double expectedScore)
	{
		NumericTokenContainmentScoreCalculator calculator = new NumericTokenContainmentScoreCalculator();
		double score = calculator.containmentScore(sourceTokens, targetTokens, comparers, dummyTokenAuditValues);
		assertEquals("Scores must be same .", expectedScore, score, Double.MIN_VALUE);		
	}
}
