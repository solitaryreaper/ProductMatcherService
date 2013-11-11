package com.walmart.productgenome.pairComparison.model.rule;

import java.util.List;

import com.google.common.collect.Lists;
import com.walmart.productgenome.pairComparison.audit.ClauseAuditEntity;
import com.walmart.productgenome.pairComparison.audit.RuleAuditEntity;
import com.walmart.productgenome.pairComparison.model.MatchStatus;
import com.walmart.productgenome.pairComparison.utils.AuditUtils;

/**
 * Collection of attribute match clauses , where each attribute match clause matches attributes
 * of source and product item to determine if the items are same or not.
 * @author sprasa4
 *
 */
public class ItemMatchRule {

	private final String m_ruleName;
	private List<AttributeMatchClause> m_attributeMatchClauses;
	
	public ItemMatchRule(final String ruleName, final List<AttributeMatchClause> attributeMatchClauses) {
		if (ruleName == null) throw new IllegalArgumentException("rule name cannot be null");
		if (attributeMatchClauses == null) throw new IllegalArgumentException("attribute match clauses cannot be null");
		
		m_ruleName = ruleName;
		m_attributeMatchClauses = attributeMatchClauses;
	}
	
	public List<AttributeMatchClause> getAttributeMatchClauses() {
		return m_attributeMatchClauses;
	}

	public void setAttributeMatchClauses(List<AttributeMatchClause> m_attributeMatchClauses) {
		this.m_attributeMatchClauses = m_attributeMatchClauses;
	}

	public String getRuleName() {
		return m_ruleName;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("\n RULE = ").append(m_ruleName);
				//.append(", \nm_attributeMatchClauses=")
			//	.append(m_attributeMatchClauses)
				//.append("]");
		return builder.toString();
	}
	
	public boolean executeRule(final MatchEntity sourceItem, final MatchEntity targetItem, RuleAuditEntity ruleAuditCollector) {
		if (sourceItem == null) throw new IllegalArgumentException("source item cannot be null");
		if (targetItem == null) throw new IllegalArgumentException("target item cannot be null");
		
		boolean isRuleSuccess = true;
		List<ClauseAuditEntity> clauseAuditEntities = Lists.newArrayList();
		for (final AttributeMatchClause clause : m_attributeMatchClauses) {
			ClauseAuditEntity clauseAuditValue = new ClauseAuditEntity(clause);
			clauseAuditValue.setClauseAttrValues(AuditUtils.getClauseItemPairAttrValues(clause, sourceItem, targetItem));
			boolean isClauseSuccess = clause.match(sourceItem, targetItem, clauseAuditValue);
			clauseAuditValue.setStatus(isClauseSuccess ? MatchStatus.SUCCESS : MatchStatus.FAILURE);
			clauseAuditEntities.add(clauseAuditValue);
			
			if (!isClauseSuccess) {
				//all the clauses that comprise a rule need to pass for a given
				//item match rule to pass.
				isRuleSuccess = false;
				break;
			}
		}
		
		ruleAuditCollector.setClauseAuditValues(clauseAuditEntities);
		
		//all passed, so return true
		return isRuleSuccess;
	}
}
