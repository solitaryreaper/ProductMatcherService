package com.walmart.productgenome.pairComparison.service;

import java.io.File;
import java.io.FileNotFoundException;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Ignore;

import com.walmart.productgenome.pairComparison.audit.ItemPairAuditDataCollector;
import com.walmart.productgenome.pairComparison.model.Constants;
import com.walmart.productgenome.pairComparison.model.rule.MatchAttribute;
import com.walmart.productgenome.pairComparison.model.rule.MatchEntity;
import com.walmart.productgenome.pairComparison.model.rule.MatchEntityPair;
import com.walmart.productgenome.pairComparison.parser.ParseException;

public class WSECNETItemPairMatchedTest {

	private static ItemPairDataMatcher matcher = null;
	
	@BeforeClass
	public static void testSetup() throws FileNotFoundException, ParseException
	{
		String ruleFilePath = Constants.WSE_CNET_RULE_FILE_PATH;
		matcher = new ItemPairDataMatcher(new File(ruleFilePath));		
	}
	
	@Ignore
	public void testRule()
	{
		MatchEntity sourceItem = new MatchEntity("S9954035", "CNET", "Speck Products CandyShell Satin - Case for cellular phone - polycarbonate, rubber - gray, dark gray - for Apple iPhone 4, 4S");
		sourceItem.addAttribute(new MatchAttribute("attrId", "pd_title_without_number_units_and_variations", "Speck Products CandyShell Satin - Case for cellular phone - polycarbonate, rubber - , dark  - for  iPhone 4, 4S"));
		sourceItem.addAttribute(new MatchAttribute("attrId", "req_brand_name", "Speck Products"));
		sourceItem.addAttribute(new MatchAttribute("attrId", "req_manufacturer", "Speck Products"));
		sourceItem.addAttribute(new MatchAttribute("attrId", "req_upc_10", "8759120201"));
		sourceItem.addAttribute(new MatchAttribute("attrId", "extracted_color", "Grey"));
		sourceItem.addAttribute(new MatchAttribute("attrId", "pd_title_variation_phrases", "var_flavors=Apple, var_color=Grey"));
		
		MatchEntity targetItem = new MatchEntity("19417992", "WALMART_SEARCH_EXTRACT", "iPhone 4S - CandyShell Satin - Black/Dark Grey");
		targetItem.addAttribute(new MatchAttribute("attrId", "pd_title_without_number_units_and_variations", "iPhone 4S - CandyShell Satin - Black/Dark "));
		targetItem.addAttribute(new MatchAttribute("attrId", "req_brand_name", "Speck"));
		targetItem.addAttribute(new MatchAttribute("attrId", "req_upc_10", "8759120201"));
		targetItem.addAttribute(new MatchAttribute("attrId", "extracted_color", "Black, Grey"));
		targetItem.addAttribute(new MatchAttribute("attrId", "pd_title_variation_phrases", "var_color=Grey"));
		
		MatchEntityPair itemPair = new MatchEntityPair(sourceItem, targetItem);
		ItemPairAuditDataCollector collector = new ItemPairAuditDataCollector(itemPair);
		boolean result = matcher.matchItemPair(itemPair, collector);
		Assert.assertEquals("the verdict must be the same", true, result);
	}
}
