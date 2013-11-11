package com.walmart.productgenome.pairComparison.utils.data;

import java.util.List;

import org.junit.Test;

import com.google.common.collect.Lists;

import junit.framework.TestCase;

/**
 * Test cases for {@link com.walmart.productgenome.pairComparison.utils.data.DataCleanupUtils}
 * @author sprasa4
 *
 */
public class DataCleanupUtilsTest extends TestCase{
	
	/**
	 * Test case to check if the data cleanup is able to transform the HTML characters that sometimes
	 * appear in the product meta information.
	 */
	@Test
	public void testHTMLCharsConversion()
	{
		String lineToClean = "Targus 14&quot; Impax Laptop &amp; Sleeve";
		
		String cleanedupLine = DataCleanupUtils.cleanUpString(lineToClean);
		assertEquals("Targus 14\" Impax Laptop & Sleeve", cleanedupLine);
	}
	
	/**
	 * Tests if the HTML characters are properly cleaned up in the string. 
	 */
	@Test
	public void testHTMLTagsCleanup()
	{
		String lineToClean = "<li>A great-tasting snack with perfect portions right-sized just for you<li>Quick, easy and portable";

		String cleanedupLine = DataCleanupUtils.cleanUpString(lineToClean).trim();
		assertEquals("A great-tasting snack with perfect portions right-sized just for you Quick, easy and portable", cleanedupLine);
	}
	
	/**
	 * Test case to check if the data cleanup is able to remove the special characters that sometimes
	 * appear in the product meta information.
	 */
	@Test
	public void testSpecialCharsCleanup()
	{
		String lineToClean = "Garmin nï¿½vi 2555LMT - GPS receiver - automotive - 5\" - widescreen";
		
		String cleanedupLine = DataCleanupUtils.cleanUpString(lineToClean);
		assertEquals("Garmin nvi 2555LMT - GPS receiver - automotive - 5\" - widescreen", cleanedupLine);
	}
	
	@Test
	public void testDelimitersNotStrippedOff()
	{
		String lineToClean = "Test ;# |# , 123";
		
		String cleanedUpLine = DataCleanupUtils.cleanUpString(lineToClean);
		assertEquals(lineToClean, cleanedUpLine);
	}
	
	@Test
	public void testNoCleanup()
	{
		String lineToClean = "AT&T CL2909 - Corded phone w/ call waiting caller ID";
		
		String cleanedUpLine = DataCleanupUtils.cleanUpString(lineToClean);
		assertEquals(lineToClean, cleanedUpLine);
	}
		
	@Test
	public void testRemoveNullOrEmptyStrings()
	{
		List<String> testTokens = Lists.newArrayList();
		testTokens.add(null);
		testTokens.add("");
		testTokens.add("      ");
		testTokens.add("null");
		testTokens.add("only survivor");
		
		List<String> cleanedTokens = DataCleanupUtils.removeNullOrEmptyStrings(testTokens);
		assertEquals(1, cleanedTokens.size());
		assertEquals("only survivor", cleanedTokens.get(0));
	}
}
