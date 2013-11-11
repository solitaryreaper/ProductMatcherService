package com.walmart.productgenome.pairComparison.utils.tokenizers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import com.walmart.productgenome.pairComparison.model.Constants;

/**
 * Test class for
 * @see {com.walmart.productgenome.pairComparison.utils.tokenizers.SeparatorBasedTokenizer}
 * @author sprasa4
 *
 */
public class SeparatorBasedTokenizerTest {

	private static SeparatorBasedTokenizer tokenizer = new SeparatorBasedTokenizer(Constants.SEPARATOR_DELIMITER);
	
	@Test
	public void testValueDelimiterTokenizer()
	{
		String strToTest = "var_flavors=Apple;#var_color=Grey;#";		
		List<String> tokens = tokenizer.tokenize(strToTest);
		
		assertTrue(tokens.size() == 2);
		assertEquals("var_flavors=apple", tokens.get(0));
		assertEquals("var_color=grey", tokens.get(1));
	}
}
