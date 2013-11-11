package com.walmart.productgenome.pairComparison.audit;

import java.util.List;

import com.walmart.productgenome.pairComparison.model.MatchStatus;
import com.walmart.productgenome.pairComparison.model.rule.MatchEntityPair;

/**
 * Collects audit data about the matching of every item pair. This includes information like
 * whether the item pair matched or not, which clauses of rules were fired, what tokens did not
 * match for the mismatched item pairs etc.
 *  
 * @author sprasa4
 *
 */
public class ItemPairAuditDataCollector {
	// the item pair whose match information has been captured for auditing
	private MatchEntityPair itemPair;
	
	// Whether matching succeeded for this item pair ?
	private MatchStatus status;
	
	// Audit information about all the constituent rules
	private List<RuleAuditEntity> ruleAuditValues;

	public ItemPairAuditDataCollector(MatchEntityPair itemPair)
	{
		this.itemPair = itemPair;
	}
	
	public MatchEntityPair getItemPair() {
		return itemPair;
	}

	public void setItemPair(MatchEntityPair itemPair) {
		this.itemPair = itemPair;
	}

	public MatchStatus getStatus() {
		return status;
	}

	public void setStatus(MatchStatus status) {
		this.status = status;
	}

	public List<RuleAuditEntity> getRuleAuditValues() {
		return ruleAuditValues;
	}

	public void setRuleAuditValues(List<RuleAuditEntity> ruleAuditValues) {
		this.ruleAuditValues = ruleAuditValues;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AuditDataCollector")
				.append("\n ============================================== \n")
		        .append("\n itemPair=").append(itemPair)
				.append(", \nstatus=").append(status)
				.append(", \n\n RULE AUDIT VALUES =\n").append(ruleAuditValues)
				.append("\n ============================================== \n");
		return builder.toString();
	}
}
