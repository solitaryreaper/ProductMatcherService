package com.walmart.productgenome.pairComparison.utils;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.walmart.productgenome.pairComparison.model.Constants;
import com.walmart.productgenome.pairComparison.model.rule.AttributeMatchClause;
import com.walmart.productgenome.pairComparison.model.rule.AttributeMatchClauseItemPairAttrs;
import com.walmart.productgenome.pairComparison.model.rule.MatchEntity;
import com.walmart.productgenome.pairComparison.utils.rule.ItemMatchRulePostProcessor;

/**
 * Utility class to aid in the generation of audit data during the matching process.
 * 
 * @author sprasa4
 *
 */
public class AuditUtils {
	
	public static List<AttributeMatchClauseItemPairAttrs> getClauseItemPairAttrValues(
			AttributeMatchClause clause, MatchEntity sourceItem, MatchEntity targetItem)
	{
		List<AttributeMatchClauseItemPairAttrs> attrMatchClauseAttrValues = Lists.newArrayList();
		Set<String> distinctAttrNames = Sets.newHashSet();
		
		// Add all the source attributes
		distinctAttrNames.addAll(clause.getSourceItemAttributes());		
		
		List<String> targetItemAttributes = clause.getTargetItemAttributes();		
		// For the special case ALL_ITEMPAIR_ATTRIBUTES fetch all the attributes of target item
		boolean isAllItempairAttrsReqd = ItemMatchRulePostProcessor.isAllItemPairAttributesReqd(targetItemAttributes);
		if(isAllItempairAttrsReqd) {
			distinctAttrNames.addAll(targetItem.getAttributeNameValueSetMap().keySet());
		}
		// Else just add all the attributes of target items
		else {
			distinctAttrNames.addAll(targetItemAttributes);
		}
		
		Map<String, Set<String>> sourceItemAttrValueMap = sourceItem.getAttributeNameValueSetMap();
		Map<String, Set<String>> targetItemAttrValueMap = targetItem.getAttributeNameValueSetMap();		
		for(String attrName : distinctAttrNames) {
			Set<String> sourceItemAttrValues = sourceItemAttrValueMap.get(attrName);
			Set<String> targetItemAttrValues = targetItemAttrValueMap.get(attrName);
			if(sourceItemAttrValues == null) {
				sourceItemAttrValues = Sets.newHashSet();
			}
			if(targetItemAttrValues == null) {
				targetItemAttrValues = Sets.newHashSet();
			}
			
			attrMatchClauseAttrValues.add(
				new AttributeMatchClauseItemPairAttrs(
					attrName, Lists.newArrayList(sourceItemAttrValues), Lists.newArrayList(targetItemAttrValues))
			);			
		}
		
		return attrMatchClauseAttrValues;
	}
}
