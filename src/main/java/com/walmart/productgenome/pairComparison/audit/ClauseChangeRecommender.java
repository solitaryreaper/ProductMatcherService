package com.walmart.productgenome.pairComparison.audit;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.walmart.productgenome.pairComparison.model.MatchStatus;
import com.walmart.productgenome.pairComparison.model.rule.AttributeMatchClause;
import com.walmart.productgenome.pairComparison.model.rule.MatchEntity;
import com.walmart.productgenome.pairComparison.utils.comparers.ComparersFactory;
import com.walmart.productgenome.pairComparison.utils.comparers.IComparer;
import com.walmart.productgenome.pairComparison.utils.rule.calculator.IContainmentScoreCalculator;
import com.walmart.productgenome.pairComparison.utils.rule.calculator.StringTokenContainmentScoreCalculator;
import com.walmart.productgenome.pairComparison.utils.tokenizers.IStringTokenizer;
import com.walmart.productgenome.pairComparison.utils.tokenizers.StandardAnalyzerTokenizer;

/**
 * The basic idea is that this class would analyse the tokens which could not be properly matched
 * and recommend other attributes where it might be present. These new found attributes can then
 * be considered to be included in the clause attribute set definition
 * @author sprasa4
 *
 */
public class ClauseChangeRecommender {
	
	public static void testClauseRecommender(AuditDataCollector collector)
	{
		Map<String, MatchEntity> tokenToSearchInItemMap = Maps.newHashMap();
		Set<String> commonSourceTargetAttributes = Sets.newHashSet();
		
		List<ItemPairAuditDataCollector> itemPairAuditCollection = collector.getItemPairAuditExtract();
		for(ItemPairAuditDataCollector itemPairAudit : itemPairAuditCollection) {
			List<RuleAuditEntity> ruleAuditCollection = itemPairAudit.getRuleAuditValues();
			for(RuleAuditEntity ruleAudit : ruleAuditCollection) {
				for(ClauseAuditEntity clauseAudit : ruleAudit.getClauseAuditValues()) {
					
					// Extract the common attributes
					AttributeMatchClause clause = clauseAudit.getClause();
					commonSourceTargetAttributes.addAll(clause.getSourceItemAttributes());
					commonSourceTargetAttributes.addAll(clause.getTargetItemAttributes());
					
					List<TokenAuditEntity> tokenAuditCollection = clauseAudit.getAuditTokenValues();
					if(tokenAuditCollection == null || tokenAuditCollection.isEmpty()) {
						continue;
					}
					
					for(TokenAuditEntity tokenAudit : tokenAuditCollection) {
						if(tokenAudit.getStatus().equals(MatchStatus.FAILURE)) {
							String tokenTarget = tokenAudit.getTargetItem();
							MatchEntity source = itemPairAudit.getItemPair().getSourceItem();
							MatchEntity target = itemPairAudit.getItemPair().getTargetItem();
							
							MatchEntity itemToMatch = null;
							if(tokenTarget.equals(source.getSource())) {
								itemToMatch = source;
							}
							else {
								itemToMatch = target;
							}
							tokenToSearchInItemMap.put(tokenAudit.getSourceTokens(), itemToMatch);
						}
					}
				}
			}
		}

		Map<String, Map<String, Integer>> recoMap = Maps.newHashMap();
		
		// Start the searching
		for(Map.Entry<String, MatchEntity> entry : tokenToSearchInItemMap.entrySet()) {
			MatchEntity item = entry.getValue();
			String tokenToSearch = entry.getKey();
			
			String matchingTargetAttributes = findTokenInExtendedAttributes(tokenToSearch, item, commonSourceTargetAttributes);
			if(matchingTargetAttributes.length() > 2) {
				String source = item.getSource();
				System.out.println(
					"Missinng token \"" + tokenToSearch + 
					"\" found in target item (" + item.getItemID() + ", " + item.getSource() + ") " +
					" --> Matching attributes {" + matchingTargetAttributes +"}"
				);

				updateRecoMap(recoMap, source, matchingTargetAttributes);
			}
		}
		
		System.out.println("==== Summary Statistics ======");
		for(Map.Entry<String, Map<String, Integer>> entry : recoMap.entrySet()) {
			String source = entry.getKey();
			Map<String, Integer> attributeCountMap = entry.getValue();
			
			System.out.println("\n\nRecommendation for item source " + source);
			for(Map.Entry<String, Integer> entry2 : attributeCountMap.entrySet()) {
				System.out.println("Attribute : "  + entry2.getKey() + " --> " + entry2.getValue());
			}
		}
	}

	private static void updateRecoMap(Map<String, Map<String, Integer>> recoMap, String source, String matchingTargetAttributes)
	{
		Map<String, Integer> attributeCountMap = null;
		if(recoMap.containsKey(source)) {
			attributeCountMap = recoMap.get(source);
		}
		else {
			attributeCountMap = Maps.newHashMap();
		}
		
		String[] targetAttributes = matchingTargetAttributes.split(" ");
		for(String targetAttribute : targetAttributes) {
			if(targetAttribute.trim().length() < 2) {
				continue;
			}
			int count = 1;
			if(attributeCountMap.containsKey(targetAttribute)) {
				count += attributeCountMap.get(targetAttribute);
			}
			
			attributeCountMap.put(targetAttribute, count);
		}
		
		recoMap.put(source, attributeCountMap);
	}
	
	private static boolean isCommonItemAttribute(String attributeName, Set<String> commonSourceTargetAttributes)
	{
		return commonSourceTargetAttributes.contains(attributeName);
	}
	
	public static String findTokenInExtendedAttributes(String tokenToSearch, MatchEntity targetItem, Set<String> commonSourceTargetAttributes)
	{
		boolean isTokenFound = false;
		StringBuilder matchingTargetAttributes = new StringBuilder();
		for(Map.Entry<String, Set<String>> entry : targetItem.getAttributeNameValueSetMap().entrySet())
		{
			String targetItemAttributeName = entry.getKey();
			// Only search the extended attributes. The idea is to find out new attributes which
			// can be included in the rule definition.
			if(isCommonItemAttribute(targetItemAttributeName, commonSourceTargetAttributes)) {
				continue;
			}
			
			Set<String> targetItemAttributeValueSet = entry.getValue();
			
			List<String> targetItemAttributeValueTokenList = Lists.newArrayList();
			IStringTokenizer tokenizer = new StandardAnalyzerTokenizer();
			for (String targetItemAttributeValue : targetItemAttributeValueSet) {
				targetItemAttributeValueTokenList.addAll(tokenizer.tokenize(targetItemAttributeValue));
			}
			
			isTokenFound = isTokenPresentInTarget(tokenToSearch, targetItemAttributeValueTokenList);
			if(isTokenFound) {
				matchingTargetAttributes.append(targetItemAttributeName).append("  ");
			}
		}
		
		return matchingTargetAttributes.toString().trim();
	}
	
	private static boolean isTokenPresentInTarget(String sourceToken, List<String> targetTokens)
	{
		boolean isTokenPresent = false;
		
		List<String> sourceTokens = Lists.newArrayList(sourceToken);
		
		List<List<String>> targetTokensList = Lists.newArrayList();
		targetTokensList.add(targetTokens);
		
		List<IComparer> comparers = ComparersFactory.getComparers("FUZZY");
		List<TokenAuditEntity> tokenAuditValues = Lists.newArrayList();
		
		IContainmentScoreCalculator calculator = new StringTokenContainmentScoreCalculator();
		double containmentScore = 
				calculator.containmentScore(sourceTokens, targetTokensList, comparers, tokenAuditValues);
		if(containmentScore > Double.MIN_VALUE) {
			isTokenPresent = true;
			//System.out.println("#Found some containment " + containmentScore);
		}
		
		return isTokenPresent;
	}
}
