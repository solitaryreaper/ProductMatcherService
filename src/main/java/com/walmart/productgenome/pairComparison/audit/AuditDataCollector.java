package com.walmart.productgenome.pairComparison.audit;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.walmart.productgenome.pairComparison.model.Constants;
import com.walmart.productgenome.pairComparison.model.MatchStatus;
import com.walmart.productgenome.pairComparison.model.rule.AttributeMatchClause;
import com.walmart.productgenome.pairComparison.model.rule.MatchEntityPair;

/**
 * Collects audit data for all the item pairs being matched in the current run.
 * 
 * @author sprasa4
 *
 */
public class AuditDataCollector 
{
	private List<ItemPairAuditDataCollector> itemPairAuditExtract = Lists.newArrayList();
	
	// Which ruleset was used to invoke matching in this collector ?
	private String rulesetUsed;
	
	// Audit stats
	private int totalItemPairs;
	private int matchedItemPairs;
	private int mismatchedItemPairs;
	
	private Map<String, List<Integer>> ruleSuccessCountMap = Maps.newHashMap();
	private Map<String, List<Integer>> clauseSuccessCountMap = Maps.newHashMap();

	DecimalFormat df = new DecimalFormat("00.00");

	public AuditDataCollector()
	{
		
	}
	
	public AuditDataCollector(List<ItemPairAuditDataCollector> collector)
	{
		this.itemPairAuditExtract = collector;
	}

	public List<ItemPairAuditDataCollector> getItemPairAuditExtract() {
		return itemPairAuditExtract;
	}

	public void setItemPairAuditExtract(List<ItemPairAuditDataCollector> itemPairAuditExtract) {
		this.itemPairAuditExtract = itemPairAuditExtract;
	}
	
	public void addItemPairAuditInfo(ItemPairAuditDataCollector itemPairAudit)
	{
		this.itemPairAuditExtract.add(itemPairAudit);
	}

	public void generateAuditStatistics()
	{
		totalItemPairs = itemPairAuditExtract.size();
		for(ItemPairAuditDataCollector itemPairCollector : itemPairAuditExtract) {
			if(itemPairCollector.getStatus().equals(MatchStatus.SUCCESS)) {
				++matchedItemPairs;
			}
			else if(itemPairCollector.getStatus().equals(MatchStatus.FAILURE)) {
				++mismatchedItemPairs;
			}
			
			for(RuleAuditEntity ruleAudit : itemPairCollector.getRuleAuditValues()) {
				String ruleName = ruleAudit.getRule().getRuleName();
				boolean isRuleSuccess = (ruleAudit.getStatus() == MatchStatus.SUCCESS) ? true : false;
				
				// The idea is to calculate the ratio of number of times this rule was successful
				// to the number of times this rule was invoked.
				List<Integer> ruleStats = null;
				if(!ruleSuccessCountMap.containsKey(ruleName)) {
					ruleStats = Lists.newArrayList();
					// Dummy values to initialise the list
					ruleStats.add(0);
					ruleStats.add(0);
					
					if(isRuleSuccess) {
						ruleStats.set(0, 1);
					}
					else {
						ruleStats.set(0, 0);
					}
					ruleStats.set(1, 1);
				}
				else {
					ruleStats = ruleSuccessCountMap.get(ruleName);
					int ruleSuccessCount = ruleStats.get(0);
					int ruleInvocationCount = ruleStats.get(1);
					if(isRuleSuccess) {
						ruleStats.set(0, ruleSuccessCount+1);
					}
					else {
						ruleStats.set(0, ruleSuccessCount);
					}
					ruleStats.set(1, ruleInvocationCount+1);
				}
				ruleSuccessCountMap.put(ruleName, ruleStats);
				
				for(ClauseAuditEntity clauseAudit : ruleAudit.getClauseAuditValues()) {
					AttributeMatchClause clause = clauseAudit.getClause();
					String clauseName = clause.getSourceItemAttributes() + " MATCH " + clause.getTargetItemAttributes();
					boolean isClauseSuccess = (clauseAudit.getStatus().equals(MatchStatus.SUCCESS)) ? true : false;
					
					// The idea is to calculate the ratio of number of times this subrule was successful
					// to the number of times this subrule was invoked.
					List<Integer> clauseStats = null;
					if(!clauseSuccessCountMap.containsKey(clauseName)) {
						clauseStats = Lists.newArrayList();
						// Dummy values to initialize the list
						clauseStats.add(0);
						clauseStats.add(0);
						
						if(isClauseSuccess) {
							clauseStats.set(0, 1);
						}
						else {
							clauseStats.set(0, 0);
						}
						clauseStats.set(1, 1);
					}
					else {
						clauseStats = clauseSuccessCountMap.get(clauseName);
						int clauseSuccessCount = clauseStats.get(0);
						int clauseInvocationCount = clauseStats.get(1);
						if(isClauseSuccess) {
							clauseStats.set(0, clauseSuccessCount+1);
						}
						else {
							clauseStats.set(0, clauseSuccessCount);
						}
						clauseStats.set(1, clauseInvocationCount+1);
					}
					clauseSuccessCountMap.put(clauseName, clauseStats);
				} // end of clause audit processing
			}
		}

	}
	
	/**
	 * Returns the overall match summary statistics 
	 * @return
	 */
	public Map<String, Integer> getMatchSummaryStatistics()
	{
		Map<String, Integer> summaryStats = Maps.newHashMap();
		summaryStats.put(Constants.TOTAL_ITEMPAIRS, 		totalItemPairs);
		summaryStats.put(Constants.MATCHED_ITEMPAIRS, 		matchedItemPairs);
		summaryStats.put(Constants.MISMATCHED_ITEMPAIRS, 	mismatchedItemPairs);
				
		return summaryStats;
	}
	
	/**
	 * Returns the overall clause success/failure statistics
	 * @return
	 */
	public Map<String, String> getClauseSummaryStatistics()
	{
		Map<String, String> clauseSuccessMap = Maps.newHashMap();
		for(Map.Entry<String, List<Integer>> entry : clauseSuccessCountMap.entrySet()) {
			List<Integer> clauseStats = entry.getValue();
			int clauseSuccessCount = clauseStats.get(0);
			int clauseInvocationCount = clauseStats.get(1);
			
			double percentage = ((double)clauseSuccessCount*100)/(double)clauseInvocationCount;
			clauseSuccessMap.put(entry.getKey(), df.format(percentage));
		}
		
		return clauseSuccessMap;
	}
	
	/**
	 * Returns the success percentage of each of the rules in the current ruleset. This is obtained
	 * by dividing the number of times this rule was successful by number of times this rule was invoked.
	 * @return
	 */
	public Map<String, String> getRuleSummaryStatistics()
	{
		Map<String, String> ruleSuccessMap = Maps.newHashMap();
		for(Map.Entry<String, List<Integer>> entry : ruleSuccessCountMap.entrySet()) {
			List<Integer> ruleStats = entry.getValue();
			int ruleSuccessCount = ruleStats.get(0);
			int ruleInvocationCount = ruleStats.get(1);
			
			double percentage = ((double)ruleSuccessCount*100)/(double)ruleInvocationCount;
			ruleSuccessMap.put(entry.getKey(), df.format(percentage));
		}
		
		return ruleSuccessMap;
	}
	
	// List of all the itempairs that failed to match with each other in current trial run
	public List<MatchEntityPair> getMismatchedItemPairs()
	{
		if(CollectionUtils.isEmpty(itemPairAuditExtract)) {
			System.out.println("No itempair audit information available !!");
			return Lists.newArrayList();
		}
		
		List<MatchEntityPair> mismatchedItemPairs = Lists.newArrayList();
		for(ItemPairAuditDataCollector itemPairCollector : itemPairAuditExtract) {
			if(itemPairCollector.getStatus().equals(MatchStatus.FAILURE)) {
				mismatchedItemPairs.add(itemPairCollector.getItemPair());	
			}			
		}
		return mismatchedItemPairs;
	}
	
	// List of all the itempairs that matched with each other in current trial run
	public List<MatchEntityPair> getMatchedItemPairs()
	{
		if(CollectionUtils.isEmpty(itemPairAuditExtract)) {
			System.out.println("No itempair audit information available !!");
			return Lists.newArrayList();
		}

		List<MatchEntityPair> matchedItemPairs = Lists.newArrayList();
		for(ItemPairAuditDataCollector itemPairCollector : itemPairAuditExtract) {
			if(itemPairCollector.getStatus().equals(MatchStatus.SUCCESS)) {
				matchedItemPairs.add(itemPairCollector.getItemPair());	
			}			
		}
		return matchedItemPairs;		
	}

	public String getRulesetUsed() {
		return rulesetUsed;
	}

	public void setRulesetUsed(String rulesetUsed) {
		this.rulesetUsed = rulesetUsed;
	}
}
