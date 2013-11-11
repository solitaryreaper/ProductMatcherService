package com.walmart.productgenome.pairComparison.model.rule;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.walmart.productgenome.pairComparison.audit.ClauseAuditEntity;
import com.walmart.productgenome.pairComparison.audit.TokenAuditEntity;
import com.walmart.productgenome.pairComparison.model.Constants;
import com.walmart.productgenome.pairComparison.utils.comparers.ComparersFactory;
import com.walmart.productgenome.pairComparison.utils.comparers.IComparer;
import com.walmart.productgenome.pairComparison.utils.data.DataCleanupUtils;
import com.walmart.productgenome.pairComparison.utils.evaluators.IAttributeMatchClauseEvaluator;
import com.walmart.productgenome.pairComparison.utils.evaluators.OneWayClauseEvaluator;
import com.walmart.productgenome.pairComparison.utils.evaluators.TwoWayClauseEvaluator;
import com.walmart.productgenome.pairComparison.utils.rule.ItemMatchRulePostProcessor;
import com.walmart.productgenome.pairComparison.utils.rule.calculator.IContainmentScoreCalculator;
import com.walmart.productgenome.pairComparison.utils.rule.calculator.NumericTokenContainmentScoreCalculator;
import com.walmart.productgenome.pairComparison.utils.rule.calculator.StringTokenContainmentScoreCalculator;
import com.walmart.productgenome.pairComparison.utils.tokenizers.IStringTokenizer;

/**
 * Smallest entity of a rule, that matches attributes of source and target item to see if they are
 * same or not. This matching is governed by some processing metadata like tokenizers, comparers etc.
 * @author sprasa4
 *
 */
public class AttributeMatchClause {
	
	//--------------------------------
	// Variables - Private
	//--------------------------------
	
	/** the source item attributes. As long as one of the source item attributes satisfies the condition,
	 * the attribute match clause is deemed to have passed. */
	private final List<String> m_sourceItemAttributes;
	
	/** the list of target item attributes */
	private final List<String> m_targetItemAttributes;

	private final AttributeMatchClauseMeta m_attributeMatchClauseMeta;
	
	private DecimalFormat df = new DecimalFormat("####0.00");

	/**
	 * @param sourceItemAttributes the source item attributes [NOT NULL]
	 * @param targetItemAttributes the list of target item attributes [NOT NULL]
	 * @param tokenizer the tokenizer to be used [NOT NULL]
	 * @param comparers the comparers to be used [NOT NULL]
	 * @param missingOkay the boolean that specifies whether it's okay for the source attribute to be missing or not [NOT NULL]
	 * @param clauseEvaluator the evaluator to be used [NOT NULL]
	 */
	public AttributeMatchClause(final List<String> sourceItemAttributes, final List<String> targetItemAttributes, 
								final IStringTokenizer sourceTokenizer, final IStringTokenizer targetTokenizer,
								final List<IComparer> comparers, final boolean missingOkay, 
								final IAttributeMatchClauseEvaluator clauseEvaluator, final double scoreThreshold) 
	{
		if (sourceItemAttributes == null) throw new IllegalArgumentException("source item attribute cannot be null");
		if (targetItemAttributes == null) throw new IllegalArgumentException("target item attributes cannot be null");
		
		m_sourceItemAttributes = sourceItemAttributes;
		m_targetItemAttributes = targetItemAttributes;
		m_attributeMatchClauseMeta = 
				new AttributeMatchClauseMeta(sourceTokenizer, targetTokenizer, comparers, missingOkay, clauseEvaluator, scoreThreshold
		);
	}
	
	public AttributeMatchClause(final List<String> sourceItemAttributes, final List<String> targetItemAttributes, 
								AttributeMatchClauseMeta attrMatchClauseMeta)
	{
		if (sourceItemAttributes == null) throw new IllegalArgumentException("source item attribute cannot be null");
		if (targetItemAttributes == null) throw new IllegalArgumentException("target item attributes cannot be null");

		m_sourceItemAttributes = sourceItemAttributes;
		m_targetItemAttributes = targetItemAttributes;
		m_attributeMatchClauseMeta = attrMatchClauseMeta;
	}
	
	//--------------------------------
	// Public - Methods
	//--------------------------------
	
	public List<String> getSourceItemAttributes() {
		return m_sourceItemAttributes;
	}

	public List<String> getTargetItemAttributes() {
		return m_targetItemAttributes;
	}

	public AttributeMatchClauseMeta getAttributeMatchClauseMeta() {
		return m_attributeMatchClauseMeta;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		StringBuilder srcAttrs = new StringBuilder();
		for(String srcAttr : m_sourceItemAttributes) {
			srcAttrs.append(srcAttr).append(" OR ");
		}
		String srcAttrsString = srcAttrs.toString();
		srcAttrsString = srcAttrsString.substring(0, srcAttrsString.lastIndexOf("OR")).trim();
		builder	.append("[" + srcAttrsString + "]")
				.append(" MATCH ")
				.append(m_targetItemAttributes);
		return builder.toString();
	}

	public String getClauseName()
	{
		StringBuilder builder = new StringBuilder();
		builder	.append(m_sourceItemAttributes)
				.append(" MATCH ")
				.append(m_targetItemAttributes);
		return builder.toString();
	}
	
	@Override
	public int hashCode() {
		return Objects.hashCode(m_sourceItemAttributes, m_targetItemAttributes);
	}

	@Override
	public boolean equals(Object obj) {
	    if (obj == null) return false;
	    if (getClass() != obj.getClass()) return false;
		AttributeMatchClause other = (AttributeMatchClause) obj;
		return 	Objects.equal(this.m_sourceItemAttributes, other.m_sourceItemAttributes) &&
				Objects.equal(this.m_targetItemAttributes, other.m_targetItemAttributes);
	}

	/**
	 * Returns true if the attribute match clause evaluates to true, false otherwise
	 * @param sourceItem the source item [NOT NULL]
	 * @param targetItem the target item [NOT NULL]
	 * @return true if the attribute match clause evaluates to true, false otherwise
	 */
	public boolean match(final MatchEntity sourceItem, final MatchEntity targetItem, ClauseAuditEntity clauseAuditCollector) {
		if (sourceItem == null) throw new IllegalArgumentException("source item cannot be null");
		if (targetItem == null) throw new IllegalArgumentException("target item cannot be null");
		
		double scoreThreshold = m_attributeMatchClauseMeta.getScoreThreshold();
		IAttributeMatchClauseEvaluator evaluator = m_attributeMatchClauseMeta.getEvaluator(); 
		// One way evaluator : Get the max score of source <-> target comparison
		if (evaluator instanceof OneWayClauseEvaluator) {
			clauseAuditCollector.setSourceItem(sourceItem.getSource());
			clauseAuditCollector.setTargetItem(targetItem.getSource());

			double containmentScore = getContainmentScoreOfSourceInTarget(sourceItem, targetItem, clauseAuditCollector);
			clauseAuditCollector.setCalculatedScore(containmentScore);
			
			String matchStatusReason = clauseAuditCollector.getMatchStatusReason();
			if(containmentScore == Double.MIN_VALUE && matchStatusReason != null) {
				clauseAuditCollector.setMatchStatusReason(matchStatusReason);
			}
			else {
				clauseAuditCollector.setMatchStatusReason(sourceItem.getSource() + "->" + targetItem.getSource() + " Score : " + df.format(containmentScore));
			}

			if (containmentScore >= scoreThreshold) {
				return true;
			}
			else {
				//try it the other way
				double sourceToTargetScore = containmentScore;
				List<TokenAuditEntity> sourceToTargetAuditTokens = clauseAuditCollector.getAuditTokenValues();

				clauseAuditCollector.setSourceItem(targetItem.getSource());
				clauseAuditCollector.setTargetItem(sourceItem.getSource());

				containmentScore = getContainmentScoreOfSourceInTarget(targetItem, sourceItem, clauseAuditCollector);
				
				// Only set target to source values, if this score is greater.
				if(containmentScore >= sourceToTargetScore) {
					if(sourceToTargetAuditTokens == clauseAuditCollector.getAuditTokenValues()) {
						clauseAuditCollector.setAuditTokenValues(null);
					}
					clauseAuditCollector.setCalculatedScore(containmentScore);
				}
				else {
					clauseAuditCollector.setCalculatedScore(sourceToTargetScore);
					clauseAuditCollector.setAuditTokenValues(sourceToTargetAuditTokens);
				}
				
				String matchReason = clauseAuditCollector.getMatchStatusReason();
				clauseAuditCollector.setMatchStatusReason(matchReason + "; " + targetItem.getSource() + "->" + sourceItem.getSource() + " Score : " + df.format(containmentScore));

				return (containmentScore >= scoreThreshold);
			}
		}
		//Two way evaluator : get the mean score both ways
		else if (evaluator instanceof TwoWayClauseEvaluator) {
			clauseAuditCollector.setSourceItem(sourceItem.getSource());
			clauseAuditCollector.setTargetItem(targetItem.getSource());			

			double containmentScore1 = getContainmentScoreOfSourceInTarget(sourceItem, targetItem, clauseAuditCollector);
			List<TokenAuditEntity> sourceToTargetMatchTokens = clauseAuditCollector.getAuditTokenValues();
			
			double containmentScore2 = getContainmentScoreOfSourceInTarget(targetItem, sourceItem, clauseAuditCollector);
			List<TokenAuditEntity> targetToSourceMatchTokens = clauseAuditCollector.getAuditTokenValues();
			
			// collect both the source -> target match tokens and vice-versa, as both are relevant
			// audit information that might be useful for debugging later.
			Set<TokenAuditEntity> allMatchedTokenValues = Sets.newHashSet();
			if(CollectionUtils.isNotEmpty(sourceToTargetMatchTokens)) {
				allMatchedTokenValues.addAll(sourceToTargetMatchTokens);				
			}
			if(CollectionUtils.isNotEmpty(targetToSourceMatchTokens)) {
				allMatchedTokenValues.addAll(targetToSourceMatchTokens);				
			}

			double meanScore = (containmentScore1 + containmentScore2)/2.0;
			
			// set the calculated audit values here
			clauseAuditCollector.setCalculatedScore(meanScore);
			clauseAuditCollector.setMatchStatusReason(sourceItem.getSource() + "<->" + targetItem.getSource() + " Score : " + df.format(meanScore));
			clauseAuditCollector.setAuditTokenValues(Lists.newArrayList(allMatchedTokenValues));
			
			if (meanScore >= scoreThreshold) {return true;}
			else {return false;}
		}
		else {
			throw new IllegalArgumentException("Unknown attribute clause evaluator");
		}
	}
 	
	//--------------------------------
	// Private - Helper Methods
	//--------------------------------
	
	private double getContainmentScoreOfSourceInTarget(final MatchEntity sourceItem, final MatchEntity targetItem, ClauseAuditEntity clauseAuditCollector) {
		//Generating the list of target attribute values
		final List<List<String>> targetItemAttributeValueTokenList = new ArrayList<List<String>>();
		
		// Get the set of target item attributes to match with. A post-processing step is required
		// for special cases where we want to match with all the target item attributes and these
		// attributes need to be dynamically pulled from the item being matched currently.
		boolean isAllItemPairAttrsReqd = ItemMatchRulePostProcessor.isAllItemPairAttributesReqd(m_targetItemAttributes);
		List<String> targetItemAttributes = 
			ItemMatchRulePostProcessor.getTargetItemAttributes(m_targetItemAttributes, sourceItem, targetItem);
		
		for (final String targetItemAttribute : targetItemAttributes) {
			Set<String> targetItemAttributeValueSet = targetItem.getValuesForAttributeName(targetItemAttribute);
			
			// HACK : For the special case ALL_ITEMPAIR_ATTRIBUTES, we need to prefix the name of the attribute
			// also the value of the attribute for advanced matching.
			targetItemAttributeValueSet = 
				ItemMatchRulePostProcessor.getTargetItemAttributeValues(
					targetItemAttributeValueSet, targetItemAttribute, isAllItemPairAttrsReqd
				);
			targetItemAttributeValueSet = DataCleanupUtils.removeNullOrEmptyStrings(targetItemAttributeValueSet);

			//move onto the next target item attribute
			if (CollectionUtils.isEmpty(targetItemAttributeValueSet)) {
				continue;
			}
						
			//add all the values of this attribute to the list
			for (String targetItemAttributeValue : targetItemAttributeValueSet) {
				// clean up the string for unnecessary characters like HTML etc. before tokenizing
				targetItemAttributeValue = DataCleanupUtils.cleanUpString(targetItemAttributeValue);
				List<String> targetTokens = m_attributeMatchClauseMeta.getTargetTokenizer().tokenize(targetItemAttributeValue);
				
				// defensive check : remove any "null" as string or empty from the list
				targetTokens = DataCleanupUtils.removeNullOrEmptyStrings(targetTokens);
				if(CollectionUtils.isNotEmpty(targetTokens)) {
					targetItemAttributeValueTokenList.add(targetTokens);					
				}
			}
		}

		// For the specific case where source attribute is a set of attributes, we just want to
		// retain the processed tokens for the attribute with the maximum containment score
		List<TokenAuditEntity> tokenAuditValuesForMaxScore = null;
		
		//iterating through the set of source item attributes. The containment score of each source
		//item attribute within the target item attributes will be computed and the maximum score will
		//be returned.
		double maxContainmentScore = Double.MIN_VALUE;
		for (final String sourceItemAttribute : m_sourceItemAttributes) {
			double currContainmentScore = getContainmentScoreOfSourceAttributeInTargetAttributes(
				sourceItem, sourceItemAttribute, targetItemAttributeValueTokenList, clauseAuditCollector
			);

			if (currContainmentScore > maxContainmentScore) {
				maxContainmentScore = currContainmentScore;
				tokenAuditValuesForMaxScore = clauseAuditCollector.getAuditTokenValues();
			}
				
		}
		clauseAuditCollector.setAuditTokenValues(tokenAuditValuesForMaxScore);
		
		return maxContainmentScore;
	}
	
	private double getContainmentScoreOfSourceAttributeInTargetAttributes(final MatchEntity sourceItem, 
																		  final String sourceItemAttribute,
																		  final List<List<String>> targetItemAttributeValueTokenList,
																		  ClauseAuditEntity clauseAuditCollector) 
	{
		Set<String> sourceAttributeValueSet = sourceItem.getValuesForAttributeName(sourceItemAttribute);
		sourceAttributeValueSet = DataCleanupUtils.removeNullOrEmptyStrings(sourceAttributeValueSet);
		if (CollectionUtils.isEmpty(sourceAttributeValueSet)) {
			clauseAuditCollector.setMatchStatusReason("Missing source attribute : " + sourceItemAttribute);
			
			//the source attribute is not present in the source item. the containment score of
			//the source attribute in the target now depends on the "missing okay" parameter
			if (m_attributeMatchClauseMeta.isMissingOkay() == true) {
				//it is acceptable for the attribute to be missing in the source item, so we
				//return a full containment score.
				return 1.0;
			}
			else {
				//it is not acceptable for the attribute to be missing. So we return a zero
				//containment score.
				return 0.0;
			}
		}
		
		//the source attribute has values. There can be more than one source attribute value for the source attribute. 
		//The maximum containment score of all the source attribute values will be chosen
		
		// Only retain the processed tokens for the source attribute value which would result in
		// maximum score.
		List<TokenAuditEntity> tokenAuditValuesForMaxScore = null;
		double currMaxScore = Double.MIN_VALUE;
		for (String sourceAttributeValue : sourceAttributeValueSet) {
			sourceAttributeValue = DataCleanupUtils.cleanUpString(sourceAttributeValue);
			List<String> sourceAttributeValueTokens = m_attributeMatchClauseMeta.getSourceTokenizer().tokenize(sourceAttributeValue);
			sourceAttributeValueTokens = DataCleanupUtils.removeNullOrEmptyStrings(sourceAttributeValueTokens);
			
			// Optimization : No need to process empty lists
			if(CollectionUtils.isEmpty(sourceAttributeValueTokens)) {
				continue;
			}
			
			// Run matching across all appropriate calculators based on the comparers and collect
			// the best containment score and token audit values for it.
			List<IComparer> comparers = m_attributeMatchClauseMeta.getComparers();
			Map<Double, List<TokenAuditEntity>> maxCalculatorScoreWithTokenAuditValues = 
				getMaxContainmentScore(sourceAttributeValueTokens, targetItemAttributeValueTokenList, comparers);
			double currScore = Double.MIN_VALUE;
			List<TokenAuditEntity> tokenAuditValues = null;
			for(Map.Entry<Double, List<TokenAuditEntity>> entry : maxCalculatorScoreWithTokenAuditValues.entrySet()) {
				currScore = entry.getKey();
				tokenAuditValues = entry.getValue();
			}
			
			if (currScore > currMaxScore) {
				currMaxScore = currScore;
				tokenAuditValuesForMaxScore = tokenAuditValues;
			}
		}
		
		if(tokenAuditValuesForMaxScore != null && !tokenAuditValuesForMaxScore.isEmpty()) {
			// Set the source and target item of the audit tokens for clause modification recommendation
			for(TokenAuditEntity tokenAuditEntity : tokenAuditValuesForMaxScore) {
				tokenAuditEntity.setSourceItem(clauseAuditCollector.getSourceItem());
				tokenAuditEntity.setTargetItem(clauseAuditCollector.getTargetItem());
			}
			
			clauseAuditCollector.setAuditTokenValues(tokenAuditValuesForMaxScore);
		}
		
		return currMaxScore;
	}
		
	/**
	 * Returns the max containment score across all applicable calculators.
	 * 
	 * Segregate the comparers into buckets based on which calculators they are applicable to. Then,
	 * determine the max score across all calculators to find the max containment.
	 * 
	 * This logic is needed because sometimes we might want to evaluate the some tokens for similarity
	 * based on their string representation or based on their value. This would require different
	 * calculators to determine their similarity. For example, part_number is generally a string
	 * and a simple string matching does the trick. But sometimes it is present as a numeric value
	 * with same value but different representation. Say, "0345" vs "345" . A simple string match
	 * might not be sufficient but a value match would give exact similarity.
	 * 
	 * @param sourceTokens
	 * @param targetTokenLists
	 * @param comparers
	 * @return
	 */
	private Map<Double, List<TokenAuditEntity>> getMaxContainmentScore(List<String> sourceTokens,
			   							  List<List<String>> targetTokenLists, 
			   							  List<IComparer> comparers)
	{
		if(CollectionUtils.isEmpty(comparers)) {
			throw new IllegalArgumentException("Comparers cannot be null for containment calculation.");
		}
		
		Map<String, List<IComparer>> comparersByType = ComparersFactory.getComparersByType(comparers);
		
		double currMaxScore = Double.MIN_VALUE;
		List<TokenAuditEntity> currBestScoreTokenValues = null;
		for(Map.Entry<String, List<IComparer>> entry : comparersByType.entrySet()) {
			List<IComparer> comparersForType = entry.getValue();
			IContainmentScoreCalculator calculator = getCalculatorByType(entry.getKey());
			List<TokenAuditEntity> currCalculatorTokenAuditValues = Lists.newArrayList();
			double currScore = calculator.containmentScore(
					sourceTokens, targetTokenLists, comparersForType, currCalculatorTokenAuditValues);
				
				if (currScore > currMaxScore) {
					currMaxScore = currScore;
					currBestScoreTokenValues = currCalculatorTokenAuditValues;
				}
		}
		
		Map<Double, List<TokenAuditEntity>> scoreWithTokenAuditValues = Maps.newHashMap();
		scoreWithTokenAuditValues.put(currMaxScore, currBestScoreTokenValues);
		
		return scoreWithTokenAuditValues;
	}
	
	// Gets the appropriate calculator depending on the type of comparers
	private IContainmentScoreCalculator getCalculatorByType(String type)
	{
		Map<String, IContainmentScoreCalculator> calculatorsByType = Maps.newHashMap();
		calculatorsByType.put(Constants.STRING_COMPARER, new StringTokenContainmentScoreCalculator());
		calculatorsByType.put(Constants.INTEGER_COMPARER, new NumericTokenContainmentScoreCalculator());
		calculatorsByType.put(Constants.DOUBLE_COMPARER, new NumericTokenContainmentScoreCalculator());
		
		IContainmentScoreCalculator calcualtor = calculatorsByType.get(type);
		// Default calculator is String Containment Calculator
		if(calcualtor == null) {
			calcualtor = calculatorsByType.get(Constants.STRING_COMPARER);
		}
		return calcualtor;
	}
	
}
