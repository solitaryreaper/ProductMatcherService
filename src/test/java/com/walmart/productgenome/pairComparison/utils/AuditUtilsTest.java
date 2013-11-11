package com.walmart.productgenome.pairComparison.utils;

import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;

import com.google.common.collect.Lists;
import com.walmart.productgenome.pairComparison.model.rule.AttributeMatchClause;
import com.walmart.productgenome.pairComparison.model.rule.AttributeMatchClauseItemPairAttrs;
import com.walmart.productgenome.pairComparison.model.rule.MatchAttribute;
import com.walmart.productgenome.pairComparison.model.rule.MatchEntity;

public class AuditUtilsTest {

	@Test
	public void testAttrValuesForClause()
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

		List<String> sourceItemAttributes = Lists.newArrayList("pd_title");
		List<String> targetItemAttributes = Lists.newArrayList("pd_title", "req_isbn_13");
		AttributeMatchClause clause = new AttributeMatchClause(sourceItemAttributes, targetItemAttributes, null);
		
		List<AttributeMatchClauseItemPairAttrs> itemPairAttrValues = 
			AuditUtils.getClauseItemPairAttrValues(clause, sourceItem, targetItem);
		assertNotNull(itemPairAttrValues);
	}
}
