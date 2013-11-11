package com.walmart.productgenome.pairComparison.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Map;
import java.util.Set;

import org.junit.Test;

/**
 * Test cases to ensure that the itempair file is parsed properly into item objects.
 * @author sprasa4
 *
 */
public class ItemPairDataFileReaderTest {
	
	@Test
	public void testParseIDNames()
	{
		String idsNameLine = "ID|#17275596|#S6736872|#";
		Map<String, String> results = ItemPairDataFileReader.parseItemIDs(idsNameLine);
		assertEquals(results.size(), 2);
		assertTrue(results.values().contains("17275596"));
		assertTrue(results.values().contains("S6736872"));
	}
	
	@Test
	public void testParseItemNames1()
	{
		String titleLine = "Title|#Pelican 1660 Case|#Pelican 1660 - Case - stainless steel - black|#";
		Map<String, String> results = ItemPairDataFileReader.parseItemNames(titleLine);
		assertEquals(results.size(), 2);
		assertTrue(results.values().contains("Pelican 1660 Case"));
		assertTrue(results.values().contains("Pelican 1660 - Case - stainless steel - black"));		
	}

	@Test
	public void testParseItemNames2()
	{
		String titleLine = "Title|#[Equate Night-Time Firming Cream, 2 oz]|#[EQ NIGHTIME FIRM CRM]|#;";
		Map<String, String> results = ItemPairDataFileReader.parseItemNames(titleLine);
		assertEquals(results.size(), 2);
		assertTrue(results.values().contains("[Equate Night-Time Firming Cream, 2 oz]"));
		assertTrue(results.values().contains("[EQ NIGHTIME FIRM CRM]"));
	}

	@Test
	public void testParseSources()
	{
		String sourceLine = "Source|#WALMART_SEARCH_EXTRACT|#CNET|#";
		Map<String, String> results = ItemPairDataFileReader.parseSources(sourceLine);
		assertEquals(results.size(), 2);
		assertTrue(results.values().contains("WALMART_SEARCH_EXTRACT"));
		assertTrue(results.values().contains("CNET"));				
	}
	
	@Test
	public void testParseCommonAttributes()
	{
		String commonAttrLine = "req_raw_part_number|#[1660-020-110]|#[1660-020-110]|#";
		Map<String, Map<String, Set<String>>> results = ItemPairDataFileReader.parseCommonAttributesOfItems(commonAttrLine);
		assertEquals(results.size(), 2);
		
		Map<String, Set<String>> sourceAttrs = results.get("source_item_attr");
		assertTrue(sourceAttrs.containsKey("req_raw_part_number"));
		Set<String> sourceValues = sourceAttrs.get("req_raw_part_number");
		assertTrue(sourceValues.contains("1660-020-110"));
		
		Map<String, Set<String>> targetAttrs = results.get("target_item_attr");
		assertTrue(targetAttrs.containsKey("req_raw_part_number"));
		Set<String> targetValues = targetAttrs.get("req_raw_part_number");
		assertTrue(targetValues.contains("1660-020-110"));
	}
	
	@Test
	public void testParseSourceSpecificAttributes()
	{
		String sourceSpecificAttrLine = "BOWKER ISBN10|#[1848485530]|#null|#";
		Map<String, Set<String>> results = ItemPairDataFileReader.parseSourceSpecificAttributes("BOWKER", sourceSpecificAttrLine);
		assertEquals(results.size(), 1);
		assertTrue(results.containsKey("ISBN10"));
		Set<String> values = results.get("ISBN10");
		assertTrue(values.contains("1848485530"));				
	}
	
	@Test
	public void testParseValues()
	{
		String valueLine = "[80427272918 , 08042727291]";
		Set<String> values = ItemPairDataFileReader.parseValues(valueLine, "req_upc_11");
		assertTrue(values.size() == 2);
		assertTrue(values.contains("80427272918"));
		assertTrue(values.contains("08042727291"));
	}
	
	@Test
	public void testParseValuesForTitle()
	{
		String valueLine = "[Duracell Capless USB - USB flash drive - 16 GB - black, copper]";
		Set<String> values = ItemPairDataFileReader.parseValues(valueLine, "pd_title");
		assertTrue(values.size() == 1);
		assertTrue(values.contains("Duracell Capless USB - USB flash drive - 16 GB - black, copper"));
	}
	
}
