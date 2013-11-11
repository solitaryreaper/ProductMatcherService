package com.walmart.productgenome.pairComparison.model.rule;

import java.util.Arrays;
import java.util.List;

import com.google.common.collect.Lists;
import com.walmart.productgenome.pairComparison.audit.ItemPairAuditDataCollector;
import com.walmart.productgenome.pairComparison.audit.RuleAuditEntity;
import com.walmart.productgenome.pairComparison.model.Constants;
import com.walmart.productgenome.pairComparison.model.MatchStatus;
import com.walmart.productgenome.pairComparison.utils.comparers.ComparersFactory;
import com.walmart.productgenome.pairComparison.utils.evaluators.ClauseEvaluatorFactory;
import com.walmart.productgenome.pairComparison.utils.tokenizers.TokenizerFactory;

/**
 * Collection of rules used for product matching of items in a specific category. For example, there
 * would be separate product matching rulesets for Books and Electronic Items etc. A ruleset is said
 * to have passed if any of the constitutent rule passes.
 * @author sprasa4
 *
 */
public class ItemMatchRuleset {
	private final String m_rulesetName;
	private List<ItemMatchRule> m_rules;
	
	public ItemMatchRuleset(String rulesetName, List<ItemMatchRule> rules)
	{
		m_rulesetName = rulesetName;
		m_rules = rules;
	}

	public List<ItemMatchRule> getRules() {
		return m_rules;
	}

	public void setRules(List<ItemMatchRule> m_rules) {
		this.m_rules = m_rules;
	}

	public String getRulesetName() {
		return m_rulesetName;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ItemMatchRuleset \n[m_rulesetName=")
				.append(m_rulesetName)
				.append(", \nm_rules=")
				.append(m_rules)
				.append("]");
		return builder.toString();
	}

	/**
	 * This method expands all the rules and displays them in its complete format.
	 * @return
	 */
	public String generateCompleteRules()
	{
		StringBuilder expandedRules = new StringBuilder();
		
		// Add the default ruleset level metadata information here
		expandedRules.append("//------------- RULESET DEFAULTS ------------------").append("\n");
		expandedRules.append("DECLARE DEFAULT_RULESET_ATTRIBUTES AS").append("\n");
		expandedRules.append("\t").append("COMPARER = EXACT;").append("\n");
		expandedRules.append("\t").append("SOURCE_TOKENIZER = STANDARD_ANALYZER;").append("\n");
		expandedRules.append("\t").append("TARGET_TOKENIZER = STANDARD_ANALYZER;").append("\n");
		expandedRules.append("\t").append("EVALUATOR = UNIDIRECTIONAL;").append("\n");
		expandedRules.append("\t").append("MISSING_ATTRIBUTE_ALLOWED = FALSE;").append("\n");
		expandedRules.append("\t").append("SCORE = 1.0;").append("\n");
		expandedRules.append("END\n\n");
		
		for(ItemMatchRule rule : m_rules) {
			expandedRules.append("//------------- RULE DEFINITION ------------------").append("\n");
			expandedRules.append("DECLARE RULE ").append(rule.getRuleName())
						 .append(" AS ").append("\n");
			for(AttributeMatchClause ruleClause : rule.getAttributeMatchClauses()) {
				AttributeMatchClauseMeta ruleClauseMeta = ruleClause.getAttributeMatchClauseMeta();
				
				List<String> sourceAttrs = ruleClause.getSourceItemAttributes();
				List<String> targetAttrs = ruleClause.getTargetItemAttributes();
				String comparerType = ComparersFactory.getComparersSubType(ruleClauseMeta.getComparers());
				String srcTokenizerType = TokenizerFactory.getTokenizerType(ruleClauseMeta.getSourceTokenizer());
				String targetTokenizerType = TokenizerFactory.getTokenizerType(ruleClauseMeta.getTargetTokenizer());
				String evaluatorType = ClauseEvaluatorFactory.getEvaluatorType(ruleClauseMeta.getEvaluator());
				boolean isMissingAttrAllowed = ruleClauseMeta.isMissingOkay();
				double scoreThreshold = ruleClauseMeta.getScoreThreshold();

				// get OR separated source attributes
				StringBuilder sourceAttrsStringBuilder = new StringBuilder();
				for(String srcAttr : sourceAttrs) {
					sourceAttrsStringBuilder.append(srcAttr).append(" OR ");
				}
				
				String sourceAttrsString = sourceAttrsStringBuilder.toString();
				sourceAttrsString = sourceAttrsString.substring(0, sourceAttrsString.lastIndexOf("OR")).trim();

				// Right-indent the start of clause for readability
				expandedRules.append("\t");
				// For most of the cases only ONE WAY match is required, so no need to clutter
				// the generated rules until unless it is a TWO WAY match.
				if(evaluatorType.equals(Constants.TWO_WAY_EVALUATOR)) {
					expandedRules.append(evaluatorType);	
				}
				expandedRules
						.append(" MATCH ")
						.append(" [").append(sourceAttrsString).append("] ")
						.append(" IN ")
						.append(Arrays.toString(targetAttrs.toArray()))
						
						// Push the clause metadata to next line and double right indent for readability
						.append("\n").append("\t\t")
						.append(" USING ")
						.append(" COMPARER=").append(comparerType).append(" AND ")
						.append(" SOURCE_TOKENIZER=").append(srcTokenizerType).append(" AND ");
					
						// Don't have a use case for TARGET TOKENIZER as of now. Would enable it 
						// later if required
						//.append(" TARGET_TOKENIZER=").append(targetTokenizerType).append(" AND ")
					
					// For most of the cases missing attributes are not allowed, so no need to clutter
					// the generated rules until unless missing attributes are indeed allowed.
					if(isMissingAttrAllowed) {
						expandedRules.append(" MISSING_ATTRIBUTE_ALLOWED=").append(Boolean.toString(isMissingAttrAllowed).toUpperCase()).append(" AND ");	
					}					
					expandedRules.append(" SCORE=").append(Double.toString(scoreThreshold)).append(" ;")
						.append("\n");

				
			}
			expandedRules.append("END\n\n");
		}
		
		expandedRules.append("\n\n");
		
		// Ruleset definition here
		expandedRules.append("//------------- RULESET DEFINITION ------------------").append("\n");
		expandedRules.append("DECLARE RULESET ").append(m_rulesetName).append(" AS \n");
		for(ItemMatchRule rule : m_rules) {
			expandedRules.append("\t").append("INCLUDE RULE ").append(rule.getRuleName()).append(" ;").append("\n");
		}
		expandedRules.append("END\n");
		
		return expandedRules.toString();
	}
	
	/**
	 * Determines if two items are same or not based on the rules in the ruleset.
	 * 
	 * @param sourceItem Source Item
	 * @param targetItem Target Item
	 * @return	Boolean - Indicates whether items are similar or not.
	 */
	public boolean executeRuleset(final MatchEntity sourceItem, final MatchEntity targetItem, ItemPairAuditDataCollector collector) {
		if (sourceItem == null) throw new IllegalArgumentException("source item cannot be null");
		if (targetItem == null) throw new IllegalArgumentException("target item cannot be null");
		
		boolean isRulesetSuccess = false;
		List<RuleAuditEntity> ruleAuditEntities = Lists.newArrayList();
		for (final ItemMatchRule rule : m_rules) {
			RuleAuditEntity ruleAuditValue = new RuleAuditEntity(rule);
			boolean isRuleSuccess = rule.executeRule(sourceItem, targetItem, ruleAuditValue);
			ruleAuditValue.setStatus(isRuleSuccess ? MatchStatus.SUCCESS : MatchStatus.FAILURE);
			ruleAuditEntities.add(ruleAuditValue);
			if (isRuleSuccess) {
				// if any rule passes, then the ruleset is considered successful
				isRulesetSuccess = true;
				break;
			}
		}
		
		// save the audit information collected for each rule during matching process.
		collector.setRuleAuditValues(ruleAuditEntities);
		
		//all failed, so return false
		return isRulesetSuccess;
	}

}
