package com.walmart.productgenome.pairComparison.utils.tokenizers;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

public class NoTokenizerTest {
	
	private NoTokenizer tokenizer = new NoTokenizer();
	
	@Test
	public void testMultiString()
	{
		String testString = "Hello world";
		List<String> tokens = tokenizer.tokenize(testString);
		
		assertEquals(1, tokens.size());
		assertEquals("hello world", tokens.get(0));
	}
	
	@Test 
	public void testBlankString()
	{
		String testString = "     ";
		List<String> tokens = tokenizer.tokenize(testString);
		
		assertEquals(0, tokens.size());
	}
	
	@Test
	public void testSingleWordString()
	{
		String testString = "Hello";
		List<String> tokens = tokenizer.tokenize(testString);
		
		assertEquals(1, tokens.size());
		assertEquals("hello", tokens.get(0));
	}
}
