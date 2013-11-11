package com.walmart.productgenome.pairComparison.audit;

import java.util.List;

import com.walmart.productgenome.pairComparison.model.MatchStatus;
import com.walmart.productgenome.pairComparison.model.rule.ItemMatchRule;

/**
 * Collects audit information about a single rule and all its comprising clauses during the
 * matching process.
 * @author sprasa4
 *
 */
public class RuleAuditEntity {
	private ItemMatchRule rule;
	
	private MatchStatus status;
	
	private List<ClauseAuditEntity> clauseAuditValues;

	public RuleAuditEntity(ItemMatchRule rule)
	{
		this.rule = rule;
	}
	
	public ItemMatchRule getRule() {
		return rule;
	}

	public void setRule(ItemMatchRule rule) {
		this.rule = rule;
	}

	public MatchStatus getStatus() {
		return status;
	}

	public void setStatus(MatchStatus status) {
		this.status = status;
	}

	public List<ClauseAuditEntity> getClauseAuditValues() {
		return clauseAuditValues;
	}

	public void setClauseAuditValues(List<ClauseAuditEntity> clauseAuditValues) {
		this.clauseAuditValues = clauseAuditValues;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("\n\n -------------------- RULE -------------------").append(rule)
				.append(", \tstatus=").append(status)
				.append(", \n\nclauseAuditValues=").append(clauseAuditValues);
		return builder.toString();
	}
	
}
