package com.walmart.productgenome.pairComparison.utils.data.tools;

import java.io.File;
import java.util.List;

import com.google.common.collect.Lists;
import com.walmart.productgenome.pairComparison.audit.AuditDataCollector;
import com.walmart.productgenome.pairComparison.audit.ClauseAuditEntity;
import com.walmart.productgenome.pairComparison.audit.ItemPairAuditDataCollector;
import com.walmart.productgenome.pairComparison.audit.RuleAuditEntity;
import com.walmart.productgenome.pairComparison.model.MatchStatus;
import com.walmart.productgenome.pairComparison.model.rule.MatchEntityPair;
import com.walmart.productgenome.pairComparison.service.ItemPairDataMatchService;

/**
 * Simple tool that would take as input a collection of itempair files, run matching against a 
 * ruleset and dump the matching analysis on an excelsheet for quick analysis.
 * 
 * @author sprasa4
 *
 */
public class BulkItempairMatchAnalyzer {

	/**
	 * Returns match analysis data in format suited to putting in excel sheets
	 * @param dataFilePath
	 * @param ruleFilePath
	 * @return
	 */
	public static List<List<String>> analyzeItemPairFile(File dataFile, File ruleFile)
	{
		List<List<String>> itempairsAnalysis = Lists.newArrayList();
		AuditDataCollector collector = ItemPairDataMatchService.runProductMatchJob(ruleFile, dataFile);
		List<ItemPairAuditDataCollector> itempairsAudit = collector.getItemPairAuditExtract();
		for(ItemPairAuditDataCollector itempairAudit : itempairsAudit) {
			MatchEntityPair itempair = itempairAudit.getItemPair();
			String sourceItem = itempair.getSourceItem().getName();
			String targetItem = itempair.getTargetItem().getName();
			boolean isMatched = itempairAudit.getStatus().equals(MatchStatus.SUCCESS);
			
			String ruleName = null;
			String subruleName = null;
			Double expectedScore = null;
			Double calculatedScore = null;
			List<RuleAuditEntity> ruleAuditValues = itempairAudit.getRuleAuditValues();
			for(RuleAuditEntity ruleAudit : ruleAuditValues) {
				ruleName = ruleAudit.getRule().getRuleName();
				if(!ruleAudit.getStatus().equals(MatchStatus.SUCCESS)) {
					ruleName = ruleAudit.getRule().getRuleName();
					for(ClauseAuditEntity clauseAudit : ruleAudit.getClauseAuditValues()) {
						if(clauseAudit.getStatus().equals(MatchStatus.FAILURE)) {
							subruleName = clauseAudit.getClause().getClauseName();
							expectedScore = clauseAudit.getClause().getAttributeMatchClauseMeta().getScoreThreshold();
							calculatedScore = Double.parseDouble(clauseAudit.getCalculatedScore());
							break;
						}
					}
				}
			}
			
			List<String> itempairAuditInfo = Lists.newArrayList();
			itempairAuditInfo.add(sourceItem);
			itempairAuditInfo.add(targetItem);
			itempairAuditInfo.add(Boolean.toString(isMatched));
			itempairAuditInfo.add(ruleName);
			itempairAuditInfo.add(subruleName);
			if(expectedScore != null) {
				itempairAuditInfo.add(Double.toString(expectedScore));				
			}
			else {
				itempairAuditInfo.add("NA");
			}

			if(calculatedScore != null) {
				itempairAuditInfo.add(Double.toString(calculatedScore));				
			}
			else {
				itempairAuditInfo.add("NA");
			}

			itempairsAnalysis.add(itempairAuditInfo);
		}
		
		return itempairsAnalysis;
	}
}
