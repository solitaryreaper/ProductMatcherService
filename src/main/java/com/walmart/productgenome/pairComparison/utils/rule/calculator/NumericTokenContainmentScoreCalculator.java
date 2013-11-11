package com.walmart.productgenome.pairComparison.utils.rule.calculator;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.google.common.collect.Lists;
import com.walmart.productgenome.pairComparison.audit.TokenAuditEntity;
import com.walmart.productgenome.pairComparison.model.MatchStatus;
import com.walmart.productgenome.pairComparison.utils.MatchUtils;
import com.walmart.productgenome.pairComparison.utils.comparers.ComparersFactory;
import com.walmart.productgenome.pairComparison.utils.comparers.IComparer;

/**
 * Computes the containment/similarity of double tokens between source and target item attributes.
 * 
 * @author sprasa4
 *
 */
public class NumericTokenContainmentScoreCalculator implements IContainmentScoreCalculator {

	/**
	 * Determining the containment score for doubles is slightly different than strings. The basic
	 * idea is as follows :
	 * 	1) 	Only retain double tokens. All other tokens would be removed from the list of tokens. It
	 * 	   	is the duty of rule designer to ensure that only attributes which are strictly of double
	 * 	   	type are used with double comparers.
	 *  2) 	For each double token at source, find its containment in target item. The nature of 
	 *  	containment would depend on the specific double comparers that are passed to this
	 *  	calculator.
	 *  3) 	Return the max containment score of a source double token in all the list of list of
	 *     	target double tokens.	
	 */
	public double containmentScore(List<String> sourceTokens,
								   List<List<String>> targetTokenLists, 
								   List<IComparer> comparers,
								   List<TokenAuditEntity> tokenAuditValues) 
	{
		if (sourceTokens == null) throw new IllegalArgumentException("source tokens cannot be null");
		if (targetTokenLists == null) throw new IllegalArgumentException("target token list cannot be null");
		if (comparers == null) throw new IllegalArgumentException("comparers cannot be null");
				
		// Start matching the cleaned up data
		double totalScore = 0.0;
		int validSourceTokens = 0;
		for(String sourceToken : sourceTokens) {
			// Skip non-numeric tokens for a numeric calculator
			if(StringUtils.isAlpha(sourceToken)) {
				continue;
			}
			
			TokenAuditEntity tokenAuditValue = null;
			double bestScoreForSourceToken = 0.0;
			String bestMatchingTargetTokens = null;
			String bestTokenMatchReason = null;
			MatchStatus auditStatus = MatchStatus.FAILURE;
			List<String> allComparedTargetTokens = Lists.newArrayList();
			
			for(List<String> targetTokens : targetTokenLists) {
				for(String targetToken : targetTokens) {
					if(StringUtils.isAlpha(targetToken)) {
						continue;
					}
					
					allComparedTargetTokens.add(targetToken);
					Map<IComparer, Double> bestComparerScore = MatchUtils.compareStrings(sourceToken, targetToken, comparers);
					double comparisonScore = 0.0;
					IComparer comparer = null;
					for(Map.Entry<IComparer, Double> entry : bestComparerScore.entrySet()) {
						comparisonScore = entry.getValue();
						comparer = entry.getKey();
					}
					
					if(Double.compare(comparisonScore, bestScoreForSourceToken) > 0) {
						bestScoreForSourceToken = comparisonScore;
						bestMatchingTargetTokens = targetToken;
						if(comparer != null) {
							bestTokenMatchReason = ComparersFactory.getComparerShortName(comparer.getClass().getSimpleName());							
						}
						else {
							bestTokenMatchReason = "COMPARER_MATCH";
						}
					}
				} 
			} // end of target tokens
	
			totalScore += bestScoreForSourceToken;
			++validSourceTokens;
			
			if(Double.compare(bestScoreForSourceToken, 0.0) == 0.0) {
				bestTokenMatchReason = "NO_CONTAINMENT";
				bestMatchingTargetTokens = allComparedTargetTokens.toString();
			}
			else {
				auditStatus = MatchStatus.SUCCESS;
			}
			
			// collect audit values for this matched source token
			tokenAuditValue = collectTokenAuditValue(sourceToken, auditStatus, bestMatchingTargetTokens, bestScoreForSourceToken, bestTokenMatchReason);
			tokenAuditValues.add(tokenAuditValue);
		} // end of all source tokens
		
		// No double tokens in the attribute value
		if(validSourceTokens == 0) {
			return 0.0;
		}
		
		return totalScore/(double)validSourceTokens;
	}

	private static TokenAuditEntity collectTokenAuditValue(String sourceToken, MatchStatus status, String targetTokens, double tokenScore, String tokenMatchReason)
	{
		return new TokenAuditEntity(sourceToken, status, targetTokens, tokenScore, tokenMatchReason);
	}

}
