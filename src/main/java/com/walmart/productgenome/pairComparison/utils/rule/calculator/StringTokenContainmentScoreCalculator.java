package com.walmart.productgenome.pairComparison.utils.rule.calculator;

import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.springframework.util.CollectionUtils;

import com.google.common.collect.Sets;
import com.walmart.productgenome.pairComparison.audit.TokenAuditEntity;
import com.walmart.productgenome.pairComparison.model.Constants;
import com.walmart.productgenome.pairComparison.model.MatchStatus;
import com.walmart.productgenome.pairComparison.utils.MatchUtils;
import com.walmart.productgenome.pairComparison.utils.StringUtils;
import com.walmart.productgenome.pairComparison.utils.comparers.ComparersFactory;
import com.walmart.productgenome.pairComparison.utils.comparers.IComparer;

public class StringTokenContainmentScoreCalculator implements IContainmentScoreCalculator {

	/**
	 * Computes the containment score of source item attribute tokens in the target item attribute
	 * tokens list.
	 * 
	 * TODO : This has become a GOD method. Refactor it !!
	 * 
	 * @param sourceTokens		Source item attribute value tokens.
	 * @param targetTokenLists	Target item attribute value tokens.
	 * @param comparers			List of apt comparers to be used for string matching.
	 * @param tokenAuditValues	Collector to gather the matching audit information for debugging.
	 * @return	The similarity score of source tokens with target tokens.
	 */
	public double containmentScore(List<String> sourceTokens, List<List<String>> targetTokenLists, 
										  final List<IComparer> comparers, List<TokenAuditEntity> tokenAuditValues) 
	{
		if (sourceTokens == null) throw new IllegalArgumentException("source tokens cannot be null");
		if (targetTokenLists == null) throw new IllegalArgumentException("target token list cannot be null");
		if (comparers == null) throw new IllegalArgumentException("comparers cannot be null");
		
		double sourceTokensMatchedScore = 0.0;
		double prevSourceTokensMatchedScore = 0.0;
		int currSourceTokenIndex = 0;
		int numTokensSkipped = 0;
		
		while (currSourceTokenIndex < sourceTokens.size()) {
			int sourceTokenIndexIncrement = 0;
			String sourceToken = sourceTokens.get(currSourceTokenIndex).trim();
			
			// Skip string source tokens of length 0 or 1 as they don't have any impact to the matching.
			// Make sure that the source index and number of skipped tokens is also incremented
			// as they impact the final calculated score.
			if(sourceToken.length() == 1 && org.apache.commons.lang.StringUtils.isAlpha(sourceToken)) {
				++numTokensSkipped;
				++currSourceTokenIndex;
				continue;
			}
			
			prevSourceTokensMatchedScore = sourceTokensMatchedScore;
			
			//The goal is to find the best match for this source token amongst all the target tokens
			double bestSingleTokenMatchedScoreForSourceToken = 0.0;
			String bestTargetTokenForSourceToken = null;
			IComparer bestComparer = null;
			
			// Variables for auditing the matching process for source vs target tokens
			TokenAuditEntity tokenAuditValue = null;
			StringBuilder auditSourceTokens = new StringBuilder();
			StringBuilder auditTargetTokens = new StringBuilder();
			MatchStatus auditStatus = MatchStatus.FAILURE;
			double auditSourceTokenScore = 0.0;
			String tokenMatchReason = null;

			for (List<String> targetTokenList : targetTokenLists) {
				// Ignore empty token lists
				if(CollectionUtils.isEmpty(targetTokenList)) {
					continue;
				}
				
				int currTargetTokenIndex = 0;
				for (String targetToken : targetTokenList) {
					targetToken = targetToken.trim();
					
					// Case: Source token might be a concatenated match of multiple target tokens
					if (sourceToken.startsWith(targetToken) == true) {
						int concatenatedMatches = MatchUtils.findConcatenatedMatches(sourceToken, targetTokenList, currTargetTokenIndex);
						if (concatenatedMatches > 0) {
							sourceTokensMatchedScore += 1.0; //since the source token matched perfectly
							sourceTokenIndexIncrement = 1; //move onto the next source token, since only one source token matched
							
							auditSourceTokens.append(sourceToken);
							auditSourceTokenScore = 1.0;
							auditStatus = MatchStatus.SUCCESS;
							
							StringBuilder matchedTargetTokens = new StringBuilder();
							if(concatenatedMatches == 1) {
								tokenMatchReason = "EXACT_STRING_MATCH";
								matchedTargetTokens.append(targetTokenList.get(currTargetTokenIndex));
							}
							else {
								tokenMatchReason = "CONCATENATION_MATCH";	
								for(int i=currTargetTokenIndex; i < (currTargetTokenIndex + concatenatedMatches) ; i++ ) {
									matchedTargetTokens.append(targetTokenList.get(i)).append(Constants.COMMA);
								}
							}
							
							auditTargetTokens.append(StringUtils.trimLastDelimiter(matchedTargetTokens.toString(), Constants.COMMA));

							break; 
						}
					}
					
					// Case : Target token might be a concatenated match of multiple source tokens
					if (targetToken.startsWith(sourceToken) == true) {
						int concatenatedMatches = MatchUtils.findConcatenatedMatches(targetToken, sourceTokens, currSourceTokenIndex);
						if (concatenatedMatches > 0) {
							sourceTokensMatchedScore += (double)concatenatedMatches;
							sourceTokenIndexIncrement += concatenatedMatches; //we need to skip to the next unmatched source token
							
							auditTargetTokens.append(targetToken);
							auditSourceTokenScore = (double)concatenatedMatches;
							auditStatus = MatchStatus.SUCCESS;
							
							StringBuilder matchedSourceTokens = new StringBuilder();
							if(concatenatedMatches == 1) {
								tokenMatchReason = "EXACT_STRING_MATCH";
								matchedSourceTokens.append(sourceTokens.get(currSourceTokenIndex));
							}
							else {
								tokenMatchReason = "CONCATENATION_MATCH";	
								
								for(int i=currSourceTokenIndex; i < (currSourceTokenIndex + concatenatedMatches) ; i++ ) {
									matchedSourceTokens.append(sourceTokens.get(i)).append(Constants.COMMA);
								}
							}
							
							auditSourceTokens.append(StringUtils.trimLastDelimiter(matchedSourceTokens.toString(), Constants.COMMA));

							break; //move on
						}
					}
					
					// Case : Source token might be an abbreviation of target tokens
					if (sourceToken.charAt(0) == targetToken.charAt(0)) {						
						int numAbbreviationTokens = MatchUtils.checkAbbreviationMatch(sourceToken, targetTokenList, currTargetTokenIndex);
						if (numAbbreviationTokens == 0) {
							//no abbreviation found, so try the other way around
							
							// Case: Target token might be an abbreviation of source tokens
							numAbbreviationTokens = MatchUtils.checkAbbreviationMatch(targetToken, sourceTokens, currSourceTokenIndex);
							if (numAbbreviationTokens > 0) {
								//abbreviation found
								sourceTokensMatchedScore += (double)numAbbreviationTokens;
								sourceTokenIndexIncrement += numAbbreviationTokens; //we need to skip to the next unmatched source token
								
								StringBuilder matchedSourceTokens = new StringBuilder();
								for(int i=currSourceTokenIndex; i < (currSourceTokenIndex + numAbbreviationTokens) ; i++ ) {
									matchedSourceTokens.append(sourceTokens.get(i)).append(Constants.COMMA);
								}
								
								auditSourceTokens.append(StringUtils.trimLastDelimiter(matchedSourceTokens.toString(), Constants.COMMA));
								auditTargetTokens.append(targetToken);
								auditStatus = MatchStatus.SUCCESS;
								auditSourceTokenScore = (double)numAbbreviationTokens;
								tokenMatchReason = "ABBREVIATION_MATCH";
								
								break;
							}
						}
						else {
							//abbreviation found
							sourceTokensMatchedScore += 1.0;
							sourceTokenIndexIncrement = 1; //move onto the next source token, since only one source token matched

							StringBuilder matchedTargetTokens = new StringBuilder();
							for(int i=currTargetTokenIndex; i < (currTargetTokenIndex + numAbbreviationTokens) ; i++ ) {
								matchedTargetTokens.append(targetTokenList.get(i)).append(Constants.COMMA);
							}
							
							auditTargetTokens.append(StringUtils.trimLastDelimiter(matchedTargetTokens.toString(), Constants.COMMA));
							
							auditSourceTokens.append(sourceToken);
							auditStatus = MatchStatus.SUCCESS;
							auditSourceTokenScore = 1.0;
							tokenMatchReason = "ABBREVIATION_MATCH";
							
							break; 
						}
					}

					//Case : the source token neither qualifies for a concatenated match nor an abbreviation match
					//       find the best single token match for the current source token
					
					// TODO : A simple optimization can be done here. In case a match score of 1.0
					// is achieved for some token exit.  No need to evaluate the other tokens.
					Map<IComparer, Double> bestComparerScore = MatchUtils.compareStrings(sourceToken, targetToken, comparers);
					double comparisonScore = 0.0;
					IComparer comparer = null;
					for(Map.Entry<IComparer, Double> entry : bestComparerScore.entrySet()) {
						comparisonScore = entry.getValue();
						comparer = entry.getKey();
					}
					if (comparisonScore > bestSingleTokenMatchedScoreForSourceToken) {
						bestSingleTokenMatchedScoreForSourceToken = comparisonScore;
						bestTargetTokenForSourceToken = targetToken;
						bestComparer = comparer;
					}
					
					currTargetTokenIndex++;
				} //end for (iterating through a single target token list)
				
				if (sourceTokenIndexIncrement > 0) break;
				
			}	//end for (iterating through all the target token lists)
		
			// Set iterator values for non-trivial comparison cases here.
			if (sourceTokenIndexIncrement >= 1) {
				currSourceTokenIndex += sourceTokenIndexIncrement;
			}
			// Set iterator and audit values for normal comparison cases
			else if(bestSingleTokenMatchedScoreForSourceToken > 0.0) {
				currSourceTokenIndex += 1;
				sourceTokensMatchedScore += bestSingleTokenMatchedScoreForSourceToken;
								
				auditSourceTokens.append(sourceToken);
				auditTargetTokens.append(bestTargetTokenForSourceToken);
				auditStatus = MatchStatus.SUCCESS;
				auditSourceTokenScore = bestSingleTokenMatchedScoreForSourceToken;
				
				// Put the name of specific comparer used for matching
				if(bestComparer != null) {
					tokenMatchReason = ComparersFactory.getComparerShortName(bestComparer.getClass().getSimpleName());	
				}
				// Couldn't find name of specific comparer. Keep it generic !!
				else {
					tokenMatchReason = "COMPARER_MATCH";
				}
				
			}
			
			// handle the failure to match case here
			if(sourceTokensMatchedScore - prevSourceTokensMatchedScore == 0) {
				currSourceTokenIndex += 1;
				
				auditSourceTokens.append(sourceToken);
				auditSourceTokenScore = 0.0;
				auditStatus = MatchStatus.FAILURE;
				auditTargetTokens.append(getProcessedTargetTokens(targetTokenLists));
				tokenMatchReason = "NO_CONTAINMENT";
			}
			
			// Set the audit information for all the matched tokens
			tokenAuditValue = collectTokenAuditValue(
					auditSourceTokens.toString(), auditStatus,
					auditTargetTokens.toString(), auditSourceTokenScore, tokenMatchReason);
			tokenAuditValues.add(tokenAuditValue);

		} //end while (iterating through the source tokens)

		//now compute the containment score and return it
		if (sourceTokens.isEmpty()) return 0.0; //no source tokens, so return 0
		
		double numTotalSourceTokensDouble = (double)sourceTokens.size() - numTokensSkipped;
		
		return sourceTokensMatchedScore/numTotalSourceTokensDouble;
	}
		
	private static TokenAuditEntity collectTokenAuditValue(String sourceToken, MatchStatus status, String targetTokens, double tokenScore, String tokenMatchReason)
	{
		return new TokenAuditEntity(sourceToken, status, targetTokens, tokenScore, tokenMatchReason);
	}
	
	/**
	 * Returns the unique list of target tokens. Sorted tokens ensure that it is easier to debug
	 * why a source token didn't match in the list of sorted target tokens. That is why TreeSet
	 * has been used.
	 */
	private static String getProcessedTargetTokens(List<List<String>> targetTokenLists)
	{
		TreeSet<String> targetTokens = Sets.newTreeSet();
		for(List<String> targetTokenList : targetTokenLists) {
			for(String targetToken : targetTokenList) {
				targetTokens.add(targetToken.trim());
			}
		}
		
		return targetTokens.toString();
	}
}
