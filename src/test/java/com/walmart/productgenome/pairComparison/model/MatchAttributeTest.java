package com.walmart.productgenome.pairComparison.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Ignore;
import org.junit.Test;

import com.walmart.productgenome.pairComparison.model.rule.MatchAttribute;

/**
 * Test cases for class @see {com.walmart.productgenome.pairComparison.model.MatchAttribute}
 * @author sprasa4
 *
 */
public class MatchAttributeTest {

	/**
	 * Test to check if stopwords in the category attribute are eliminated properly.
	 */
	@Ignore
	public void testCategoryCleanup()
	{
		MatchAttribute attribute = new MatchAttribute("attrId", "req_category", "Movie-General");
		assertFalse(attribute.getAttributeValue().contains("General"));
		
		attribute = new MatchAttribute("attrId", "req_category", "Movies");
		assertEquals("Movies", attribute.getAttributeValue());
	}
	
}
