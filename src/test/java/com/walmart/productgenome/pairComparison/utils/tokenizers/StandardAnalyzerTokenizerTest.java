package com.walmart.productgenome.pairComparison.utils.tokenizers;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

public class StandardAnalyzerTokenizerTest{

	private StandardAnalyzerTokenizer tokenizer = new StandardAnalyzerTokenizer();
	
	/**
	 * Test case to confirm the normal behaviour of lucene's standard analyzer.
	 */
	@Test
	public void testStandardAnalyzer() {
		String str = "L.E.I. Suri Colored Shortalls";		
		List<String> tokens = tokenizer.tokenize(str);

		assertEquals("l.e.i", tokens.get(0));
		assertEquals("suri", tokens.get(1));
		assertEquals("colored", tokens.get(2));
		assertEquals("shortalls", tokens.get(3));
	}
	
	/**
	 * Test case to confirm if the standard analyzer indeed eliminates the stop words.
	 * In thyis example, "for" and "the" must be eliminated. 
	 */
	@Test
	public void testStopWordElimination()
	{
		String str = "Macally BookStand3 Protective Case/Stand for the new iPad - Gray";		
		List<String> tokens = tokenizer.tokenize(str);
		
		assertEquals("macally", tokens.get(0));
		assertEquals("bookstand3", tokens.get(1));
		assertEquals("protective", tokens.get(2));
		assertEquals("case", tokens.get(3));
		assertEquals("stand", tokens.get(4));
		assertEquals("new", tokens.get(5));
		assertEquals("ipad", tokens.get(6));
		assertEquals("gray", tokens.get(7));
	}

	@Test
	public void testWhitespaceElimination()
	{
		String str = "Harry        Potter               V";		
		List<String> tokens = tokenizer.tokenize(str);

		assertEquals(tokens.size(), 3);
		assertEquals("harry", tokens.get(0));
		assertEquals("potter", tokens.get(1));
		assertEquals("v", tokens.get(2));		
	}
	
	@Test
	public void testEmptyTokens()
	{
		String str = "            ";		
		List<String> tokens = tokenizer.tokenize(str);

		assertEquals(tokens.size(), 0);		
	}
	
	/**
	 * Test case to show why it is important to remove special characters before tokenization.
	 * AT&T was finally tokenized as "t" after removing "AT" and "&".
	 */
	@Test
	public void test()
	{
		String str = "AT&T CL2909 - Corded phone w/ call waiting caller ID";
		
		List<String> tokens = tokenizer.tokenize(str);
		assertEquals(9, tokens.size());
		assertEquals("t", tokens.get(0));
		assertEquals("cl2909", tokens.get(1));
		assertEquals("corded", tokens.get(2));
		assertEquals("phone", tokens.get(3));
		assertEquals("w", tokens.get(4));
		assertEquals("call", tokens.get(5));
		assertEquals("waiting", tokens.get(6));
		assertEquals("caller", tokens.get(7));
		assertEquals("id", tokens.get(8));
	}
}
