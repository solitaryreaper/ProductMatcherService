package com.walmart.productgenome.pairComparison.service;

import java.io.File;
import java.io.FileNotFoundException;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;

import com.walmart.productgenome.pairComparison.audit.ItemPairAuditDataCollector;
import com.walmart.productgenome.pairComparison.model.Constants;
import com.walmart.productgenome.pairComparison.model.rule.MatchAttribute;
import com.walmart.productgenome.pairComparison.model.rule.MatchEntity;
import com.walmart.productgenome.pairComparison.model.rule.MatchEntityPair;
import com.walmart.productgenome.pairComparison.parser.ParseException;

public class WSEBowkerItemPairMatchTest {

	private static ItemPairDataMatcher matcher = null;
	
	@BeforeClass
	public static void testSetup() throws FileNotFoundException, ParseException
	{
		String ruleFilePath = Constants.WSE_BOWKER_RULE_FILE_PATH;
		matcher = new ItemPairDataMatcher(new File(ruleFilePath));		
	}
	
	@Test
	public void testCaseBooks1()
	{
		MatchEntity sourceItem = new MatchEntity("519225", "WALMART_SEARCH_EXTRACT", "A History of Civilizations");
		sourceItem.addAttribute(new MatchAttribute("attrId", "req_description", "Paperback, Penguin Books, 1995, ISBN # 0140124896"));
		sourceItem.addAttribute(new MatchAttribute("attrId", "req_isbn_13", "9780140124897"));
		sourceItem.addAttribute(new MatchAttribute("attrId", "normalized_binding", "Paperback"));
		sourceItem.addAttribute(new MatchAttribute("attrId", "extracted_color", ""));
		
		MatchEntity targetItem = new MatchEntity("9780140124897", "BOWKER", "History of Civilizations");
		targetItem.addAttribute(new MatchAttribute("attrId", "req_isbn_13", "9780140124897"));
		targetItem.addAttribute(new MatchAttribute("attrId", "normalized_binding", "Paperback"));
		targetItem.addAttribute(new MatchAttribute("attrId", "extracted_publication_year", "1995"));
		sourceItem.addAttribute(new MatchAttribute("attrId", "extracted_color", ""));
		
		MatchEntityPair itemPair = new MatchEntityPair(sourceItem, targetItem);
		ItemPairAuditDataCollector collector = new ItemPairAuditDataCollector(itemPair);
		boolean result = matcher.matchItemPair(itemPair, collector);
		Assert.assertEquals("the verdict must be the same", true, result);
	}
	
	/**
	 * This is a false negative case where two books are indeed same but are not being recognized
	 * by our matching infrastructure.
	 */
	@Test
	public void testCaseBooks2()
	{
		MatchEntity sourceItem = new MatchEntity("22220121", "WALMART_SEARCH_EXTRACT", "Rockaway");
		sourceItem.addAttribute(new MatchAttribute("attrId", "pd_title", "Rockaway"));		
		sourceItem.addAttribute(new MatchAttribute("attrId", "req_description", "Paperback, Pgw, 2013, ISBN13 9781593765163, ISBN10 1593765169"));
		sourceItem.addAttribute(new MatchAttribute("attrId", "req_isbn_13", "9781593765163"));
		sourceItem.addAttribute(new MatchAttribute("attrId", "normalized_binding", "Paperback"));
		
		MatchEntity targetItem = new MatchEntity("9781593765163", "BOWKER", "Rockaway : A Novel");
		targetItem.addAttribute(new MatchAttribute("attrId", "pd_title", "Rockaway : A Novel"));		
		targetItem.addAttribute(new MatchAttribute("attrId", "req_isbn_13", "9781593765163"));
		targetItem.addAttribute(new MatchAttribute("attrId", "normalized_binding", "Paperback"));
		targetItem.addAttribute(new MatchAttribute("attrId", "extracted_publication_year", "2013"));
		
		MatchEntityPair itemPair = new MatchEntityPair(sourceItem, targetItem);
		ItemPairAuditDataCollector collector = new ItemPairAuditDataCollector(itemPair);
		boolean result = matcher.matchItemPair(itemPair, collector);
		Assert.assertNotSame(true, result);
	}
	
}
