package com.walmart.productgenome.pairComparison.utils.rule;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Set;

import org.junit.Test;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.walmart.productgenome.pairComparison.model.Constants;
import com.walmart.productgenome.pairComparison.model.rule.MatchAttribute;
import com.walmart.productgenome.pairComparison.model.rule.MatchEntity;

public class ItemMatchRulePostProcessorTest {

	@Test
	public void testGetTargetItemAttributes()
	{
		List<String> targetItemAttributes = Lists.newArrayList(Constants.ALL_ITEMPAIR_ATTRIBUTES);
		
		MatchEntity sourceItem = new MatchEntity("519225", "WALMART_SEARCH_EXTRACT", "A History of Civilizations");
		sourceItem.addAttribute(new MatchAttribute("attrId", "req_description", "Paperback, Penguin Books, 1995, ISBN # 0140124896"));
		sourceItem.addAttribute(new MatchAttribute("attrId", "req_isbn_13", "9780140124897"));
		sourceItem.addAttribute(new MatchAttribute("attrId", "normalized_binding", "Paperback"));
		sourceItem.addAttribute(new MatchAttribute("attrId", "extracted_color", ""));
		
		MatchEntity targetItem = new MatchEntity("9780140124897", "BOWKER", "History of Civilizations");
		targetItem.addAttribute(new MatchAttribute("attrId", "req_isbn_13", "9780140124897"));
		targetItem.addAttribute(new MatchAttribute("attrId", "normalized_binding", "Paperback"));
		targetItem.addAttribute(new MatchAttribute("attrId", "extracted_publication_year", "1995"));
		targetItem.addAttribute(new MatchAttribute("attrId", "extracted_color", ""));

		targetItemAttributes = 
			ItemMatchRulePostProcessor.getTargetItemAttributes(targetItemAttributes, sourceItem, targetItem);
		assertTrue(targetItemAttributes.size() > 1);
		
		targetItemAttributes = Lists.newArrayList(Constants.PD_TITLE);
		targetItemAttributes = 
				ItemMatchRulePostProcessor.getTargetItemAttributes(targetItemAttributes, sourceItem, targetItem);
		assertTrue(targetItemAttributes.size() == 1);
	}
	
	@Test
	public void testGetTargetItemAttributeValues()
	{
		Set<String> attributeValues = Sets.newHashSet("Electronics - General", "Computer Cases");
		String attributeName = "req_category";
		
		Set<String> attrNamePrefixedAttrValues = ItemMatchRulePostProcessor.getTargetItemAttributeValues(attributeValues, attributeName, true);
		assertTrue(attrNamePrefixedAttrValues.size() == 3);
		
		attrNamePrefixedAttrValues = ItemMatchRulePostProcessor.getTargetItemAttributeValues(attributeValues, attributeName, false);
		assertTrue(attrNamePrefixedAttrValues.size() == 2);
	}
	
	@Test
	public void testIsAllItemPairAttributesReqd()
	{
		List<String> targetItemAttrs = Lists.newArrayList(Constants.ALL_ITEMPAIR_ATTRIBUTES);
		boolean isAllItemPairAttributeReqd = 
			ItemMatchRulePostProcessor.isAllItemPairAttributesReqd(targetItemAttrs);
		assertTrue(isAllItemPairAttributeReqd);
		
		targetItemAttrs = Lists.newArrayList(Constants.PD_TITLE);
		isAllItemPairAttributeReqd = ItemMatchRulePostProcessor.isAllItemPairAttributesReqd(targetItemAttrs);
		assertFalse(isAllItemPairAttributeReqd);
	}
	
}
