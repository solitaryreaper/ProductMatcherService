package com.walmart.productgenome.pairComparison.service;

import static org.junit.Assert.assertNotNull;

import java.io.File;

import org.junit.Test;

import com.walmart.productgenome.pairComparison.model.Constants;
import com.walmart.productgenome.pairComparison.parser.ParseException;

/**
 * Test class for @see {com.walmart.productgenome.pairComparison.service.ItemPairDataMatcher}
 * @author sprasa4
 *
 */
public class ItemPairDataMatcherTest {

	@Test
	public void testReadRulesFromFile()
	{
		String ruleFilePath = Constants.WSE_BOWKER_RULE_FILE_PATH;
		File ruleFile = new File(ruleFilePath);
		
		ItemPairDataMatcher matcher = null;
		try {
			matcher = new ItemPairDataMatcher(ruleFile);
		} catch (Exception e) {
			System.err.println("Failed to read rule file from absolute path !!");
			e.printStackTrace();
		}
		assertNotNull(matcher);
		assertNotNull(matcher.getRuleset());
	}
	
	@Test
	public void testReadRulesFromStream()
	{
		ItemPairDataMatcher matcher = null;		
		try {
			matcher = new ItemPairDataMatcher("rules/item-matching-rules-bowker-wse-baseline-expanded.txt");
		} catch (ParseException e) {
			System.err.println("Failed to read rule file as a stream !!");
		}

		assertNotNull(matcher);
		assertNotNull(matcher.getRuleset());
	}
	
}
