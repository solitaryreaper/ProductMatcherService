package com.walmart.productgenome.pairComparison.utils.rule;

import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.walmart.productgenome.pairComparison.model.Constants;
import com.walmart.productgenome.pairComparison.model.rule.MatchAttribute;
import com.walmart.productgenome.pairComparison.model.rule.MatchEntity;

/**
 * A post-processor that is used to further enrich the rules specified in the domain specific 
 * language. This processing includes the context of items that are being matched and hence
 * needs to be done when the actual matching is in progres.
 * 
 * @author sprasa4
 *
 */
public class ItemMatchRulePostProcessor {
	
	/**
	 * Processes the input target item attributes specified in the attribute match clause under the
	 * context of the current target item and returns the appropriate set of target item attributes.
	 * Specifically, if there is a placeholder ALL_ATTRIBUTES present in the input target item
	 * attributes, it is replaced inline with all the attributes present in both the source and
	 * target item.
	 * 
	 * @param targetItemAttributes
	 * @param targetItem
	 * @return
	 */
	public static List<String> getTargetItemAttributes(List<String> targetItemAttributes, 
													   MatchEntity sourceItem, 
													   MatchEntity targetItem)
	{
		if(!isAllItemPairAttributesReqd(targetItemAttributes)) {
			return targetItemAttributes;
		}
		
		// Found a case where we have to process all the itempair attributes
		// Add all attributes of both source and target. This is because for two way evaluation
		// we need attributes of both source and target.
		Set<String> processedTargetItemAttributes = Sets.newHashSet();
		for(MatchAttribute attribute : sourceItem.getAllAttributes()) {
			processedTargetItemAttributes.add(attribute.getAttributeName());
		}
		for(MatchAttribute attribute : targetItem.getAllAttributes()) {
			processedTargetItemAttributes.add(attribute.getAttributeName());
		}
		
		// Defensive check
		processedTargetItemAttributes.addAll(targetItem.getAttributeNameValueSetMap().keySet());
		processedTargetItemAttributes.addAll(sourceItem.getAttributeNameValueSetMap().keySet());
		
		return Lists.newArrayList(processedTargetItemAttributes);
	}
	
	/**
	 *  For cases when ALL_ITEMPAIR_ATTRIBUTES keyword has been defined for target item attributes,
	 *  we need to prefix the attribute name also in the attribute name value. For other cases,
	 *  leave the value set untouched.
	 *  
	 *  For example, attr_name = "extracted_color", attr_value = "red" ==> "extracted_color red"
	 */
	public static Set<String> getTargetItemAttributeValues(
		Set<String> inputTargetItemAttrValues, String attributeName, boolean isAttrNameToPrefix)
	{
		if(!isAttrNameToPrefix) {
			return inputTargetItemAttrValues;
		}
		
		Set<String> attrNamePrefixedTargetItemAttrValues = Sets.newHashSet();
		// Add attribute name as prefix
		attrNamePrefixedTargetItemAttrValues.add(attributeName);
		
		// Add the remaining attribute values
		if(CollectionUtils.isNotEmpty(inputTargetItemAttrValues)) {
			attrNamePrefixedTargetItemAttrValues.addAll(inputTargetItemAttrValues);			
		}

		return attrNamePrefixedTargetItemAttrValues;
	}
	
	// Checks if all the attributes present in item pair have to be included for product matching.
	public static boolean isAllItemPairAttributesReqd(List<String> targetItemAttributes)
	{
		boolean isAllItemPairAttributesReqd = false;
		if(CollectionUtils.isNotEmpty(targetItemAttributes)) {
			if(targetItemAttributes.size() == 1 && targetItemAttributes.get(0).equals(Constants.ALL_ITEMPAIR_ATTRIBUTES)) {
				isAllItemPairAttributesReqd = true;
			}
		}
		
		return isAllItemPairAttributesReqd;
	}
}
