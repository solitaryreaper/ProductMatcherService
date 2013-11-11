package com.walmart.productgenome.pairComparison.service;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.FileNotFoundException;

import org.junit.Test;

import com.walmart.productgenome.pairComparison.model.Constants;
import com.walmart.productgenome.pairComparison.model.rule.ItemMatchRuleset;
import com.walmart.productgenome.pairComparison.parser.ParseException;

/**
 * Simple driver test class to check if the rules are getting loaded properly from the rule language files.
 * @author sprasa4
 *
 */
public class ItemPairMatcherTest{

	/**
	 * Tests if the WSE-CNET book ruleset is parsed properly.
	 */
	@Test
	public void testBookCondensedRulesParsing()
	{
		String bookRuleFileLoc = Constants.WSE_CNET_BOOK_CONDENSED_RULE_FILE_PATH;
		ItemMatchRuleset bookRuleset = loadRuleset(bookRuleFileLoc);
		
		assertNotNull(bookRuleset);
		assertFalse(bookRuleset.getRules().isEmpty());
	}

	/**
	 * Tests if the WSE-CNET book ruleset is parsed properly.
	 */
	@Test
	public void testBookExpandedRulesParsing()
	{
		String bookRuleFileLoc = Constants.WSE_CNET_BOOK_RULE_FILE_PATH;
		ItemMatchRuleset bookRuleset = loadRuleset(bookRuleFileLoc);
		
		assertNotNull(bookRuleset);
		assertFalse(bookRuleset.getRules().isEmpty());
	}

	/**
	 * Tests if the WSE Bowker condensed ruleset is parsed properly.
	 */
	@Test
	public void testWSEBowkerCondensedRulesParsing()
	{
		String ruleFilePath = Constants.WSE_BOWKER_CONDENSED_RULE_FILE_PATH;
		ItemMatchRuleset wseBowkerRuleset = loadRuleset(ruleFilePath);
		
		assertNotNull(wseBowkerRuleset);
		assertFalse(wseBowkerRuleset.getRules().isEmpty());		
	}

	/**
	 * Tests if the WSE Bowker completely expanded ruleset is parsed properly.
	 */
	@Test
	public void testWSEBowkerExpandedRulesParsing()
	{
		String ruleFilePath = Constants.WSE_BOWKER_RULE_FILE_PATH;
		ItemMatchRuleset wseBowkerRuleset = loadRuleset(ruleFilePath);
		
		assertNotNull(wseBowkerRuleset);
		assertFalse(wseBowkerRuleset.getRules().isEmpty());		
	}

	/**
	 * Tests if the WSE Bowker ruleset is parsed properly.
	 */
	@Test
	public void testWSECNETCondensedRulesParsing()
	{
		String ruleFilePath = Constants.WSE_CNET_CONDENSED_RULE_FILE_PATH;
		ItemMatchRuleset wseCnetRuleset = loadRuleset(ruleFilePath);
		
		assertNotNull(wseCnetRuleset);
		assertFalse(wseCnetRuleset.getRules().isEmpty());		
	}

	/**
	 * Tests if the WSE Bowker ruleset is parsed properly.
	 */
	@Test
	public void testWSECNETExpandedRulesParsing()
	{
		String ruleFilePath = Constants.WSE_CNET_RULE_FILE_PATH;
		ItemMatchRuleset wseCnetRuleset = loadRuleset(ruleFilePath);
		
		assertNotNull(wseCnetRuleset);
		assertFalse(wseCnetRuleset.getRules().isEmpty());		
	}

	@Test
	public void testWSEStoresRulesParsing()
	{
		String ruleFilePath = Constants.WSE_STORES_CONDENSED_RULE_FILE_PATH;
		ItemMatchRuleset wseStoresRuleset = loadRuleset(ruleFilePath);
		
		assertNotNull(wseStoresRuleset);
		assertFalse(wseStoresRuleset.getRules().isEmpty());		
	}
	
	// Utility method that parses the rule file and generates in-memory rule objects.
	private ItemMatchRuleset loadRuleset(String ruleFilePath)
	{
		File ruleFile = new File(ruleFilePath);
		ItemMatchRuleset ruleset = null;
		try {
			ItemPairDataMatcher matcher = new ItemPairDataMatcher(ruleFile);
			ruleset = matcher.getRuleset();
		} catch (FileNotFoundException e) {
			System.err.println("Failed to locate rule file : " + ruleFilePath);
			e.printStackTrace();
		} catch (ParseException e) {
			System.err.println("Failed to parse rule file : " + ruleFilePath);
			e.printStackTrace();
		}

		return ruleset;
	}
}
