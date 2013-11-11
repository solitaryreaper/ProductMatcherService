package com.walmart.productgenome.pairComparison.utils.tokenizers;

import java.util.List;

import org.junit.Test;

import junit.framework.TestCase;

public class WhiteSpaceTokenizerTest extends TestCase{
	@Test
	public void testWhiteSpaceTokenizer() {
		String str = "L.E.I. Suri Colored Shortalls";
		
		WhiteSpaceTokenizer tokenizer = new WhiteSpaceTokenizer();
		List<String> tokens = tokenizer.tokenize(str);

		assertEquals("l.e.i.", tokens.get(0));
		assertEquals("suri", tokens.get(1));
		assertEquals("colored", tokens.get(2));
		assertEquals("shortalls", tokens.get(3));
	}	
	
}
