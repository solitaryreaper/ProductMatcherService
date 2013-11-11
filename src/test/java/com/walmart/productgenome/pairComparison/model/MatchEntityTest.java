package com.walmart.productgenome.pairComparison.model;

import static org.junit.Assert.assertEquals;

import org.junit.Ignore;
import org.junit.Test;

import com.walmart.productgenome.pairComparison.model.rule.MatchAttribute;
import com.walmart.productgenome.pairComparison.model.rule.MatchEntity;

/**
 * Test cases for class @see {com.walmart.productgenome.pairComparison.model.MatchEntity}
 * @author sprasa4
 *
 */
public class MatchEntityTest {

	@Ignore
	public void testPdTitleReplaceWithSigningDescForStoresDS()
	{
		MatchEntity sourceItem = new MatchEntity("61131556", "WALMART_STORES", "DORITOS NCH 100 CAL");
		sourceItem.addAttribute(new MatchAttribute("attrId", "pd_title", "DORITOS NCH 100 CAL"));
		sourceItem.addAttribute(new MatchAttribute("attrId", "signing_desc", "DORITOS NACHO 100 CAL 3.4 OZ"));
		assertEquals("[DORITOS NACHO 100 CAL 3.4 OZ]", sourceItem.getValuesForAttributeName("pd_title").toString());

		sourceItem = new MatchEntity("61131556", "WALMART_STORES", "DORITOS NCH 100 CAL");
		sourceItem.addAttribute(new MatchAttribute("attrId", "pd_title", "DORITOS NCH 100 CAL"));
		assertEquals("[DORITOS NCH 100 CAL]", sourceItem.getValuesForAttributeName("pd_title").toString());
	}
	
	@Ignore
	public void testPdTitleReplaceWithSigningDescForStoresDSForBooks()
	{
		MatchEntity sourceItem = new MatchEntity("79441913", "WALMART_STORES", "AMAZON COM");
		sourceItem.addAttribute(new MatchAttribute("attrId", "pd_title", "AMAZON COM"));
		sourceItem.addAttribute(new MatchAttribute("attrId", "signing_desc", "9780066620428"));
		assertEquals("[AMAZON COM]", sourceItem.getValuesForAttributeName("pd_title").toString());

		sourceItem = new MatchEntity("79441913", "WALMART_STORES", "AMAZON COM");
		sourceItem.addAttribute(new MatchAttribute("attrId", "pd_title", "AMAZON COM"));
		assertEquals("[AMAZON COM]", sourceItem.getValuesForAttributeName("pd_title").toString());
	}
	
}
