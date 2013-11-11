package com.walmart.productgenome.pairComparison.utils.rule.calculator;

import java.util.List;

import com.walmart.productgenome.pairComparison.audit.TokenAuditEntity;
import com.walmart.productgenome.pairComparison.utils.comparers.IComparer;

/**
 * An interface that represents a generic containment calculator (similarity calculator) between
 * source and target item attribute value tokens, based on the type of tokens. 
 * 
 * Specific containment calculators for various types of tokens are required. 
 * 	<p> String token calculator require advanced string processing techniques like abbreviation
 * 		checking, concatenated matches, prefix and suffix matches.
 *  <p> Integer and Double toke calculator require the context of numbers to determine the similarity
 *  	of tokens. For example, "14.56" and "14.69" are almost similar if analyzed from the context
 *  	of numbers as compared to simple string comparison.
 *  
 * @author sprasa4
 *
 */
public interface IContainmentScoreCalculator {
	
	/**
	 * Determines the containment (similarity) of source item attribute value tokens in target
	 * item attribute value tokens. This matching process is aided by comparers that are specifically
	 * suited to the type of tokens being processed (String, Integer, Double etc.). Audit information
	 * of the matching process is also gathered to allow debugging.
	 * 
	 * @param sourceTokens		List of source item attribute value tokens
	 * @param targetTokenLists	List of List of target item attributes value tokens
	 * @param comparers			Specific comparers based on the type of tokens.
	 * @param tokenAuditValues	Audit information collector for the matching process.
	 * @return					Similarity/Containment score between source and target item tokens.
	 */
	public double containmentScore(List<String> sourceTokens, 
								   List<List<String>> targetTokenLists, 
			  					   final List<IComparer> comparers, 
			  					   List<TokenAuditEntity> tokenAuditValues) ;
}
