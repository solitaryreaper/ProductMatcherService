 package com.walmart.productgenome.pairComparison.audit;

import java.util.List;

import com.walmart.productgenome.pairComparison.model.MatchStatus;
import com.walmart.productgenome.pairComparison.model.rule.AttributeMatchClause;
import com.walmart.productgenome.pairComparison.model.rule.AttributeMatchClauseItemPairAttrs;
import com.walmart.productgenome.pairComparison.utils.NumberUtils;

/**
 * Collects audit information about a single clause and all the participating attribute value tokens.
 * @author sprasa4
 *
 */
public class ClauseAuditEntity {
	private AttributeMatchClause clause;
	private MatchStatus status;
	private String matchStatusReason = null;
	private double calculatedScore;
	private List<TokenAuditEntity> auditTokenValues;
	private List<AttributeMatchClauseItemPairAttrs> clauseAttrValues;

	private String sourceItem;
	private String targetItem;

	public ClauseAuditEntity(AttributeMatchClause clause)
	{
		this.clause = clause;
	}
	
	public AttributeMatchClause getClause() {
		return clause;
	}
	public void setClause(AttributeMatchClause clause) {
		this.clause = clause;
	}
	public MatchStatus getStatus() {
		return status;
	}
	public void setStatus(MatchStatus status) {
		this.status = status;
	}
	public String getMatchStatusReason() {
		return matchStatusReason;
	}

	public void setMatchStatusReason(String matchStatusReason) {
		this.matchStatusReason = matchStatusReason;
	}

	public String getCalculatedScore() {
		return NumberUtils.formatDouble(calculatedScore);
	}

	public void setCalculatedScore(double calculatedScore) {
		this.calculatedScore = calculatedScore;
	}

	public List<TokenAuditEntity> getAuditTokenValues() {
		return auditTokenValues;
	}
	public void setAuditTokenValues(List<TokenAuditEntity> auditTokenValues) {
		this.auditTokenValues = auditTokenValues;
	}

	public List<AttributeMatchClauseItemPairAttrs> getClauseAttrValues() {
		return clauseAttrValues;
	}

	public void setClauseAttrValues(
			List<AttributeMatchClauseItemPairAttrs> clauseAttrValues) {
		this.clauseAttrValues = clauseAttrValues;
	}

	public String getSourceItem() {
		return sourceItem;
	}

	public void setSourceItem(String sourceItem) {
		this.sourceItem = sourceItem;
	}

	public String getTargetItem() {
		return targetItem;
	}

	public void setTargetItem(String targetItem) {
		this.targetItem = targetItem;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		double expectedScore = getClause().getAttributeMatchClauseMeta().getScoreThreshold();
		builder	.append("\n\n ----------------------- CLAUSE --------------------------").append(clause)
				.append(", \nstatus=").append(status)
				.append(", \tmatchStatusReason=").append(matchStatusReason)
				.append(", \tcalculatedScore=").append(NumberUtils.formatDouble(calculatedScore))
				.append(", \texpectedScore=").append(NumberUtils.formatDouble(expectedScore))
				.append(", \n\nauditTokenValues=");
		
		for(TokenAuditEntity token : auditTokenValues) {
			if(token.getStatus().equals(MatchStatus.FAILURE)) {
				builder.append(token.toString());
			}
		}

		return builder.toString();
	}
	
	// Utility functions
	// TODO : Temporary hack to generate clause attr value HTML in popover
	public String getClauseAttrValuesHTML()
	{
		StringBuilder commonItemPairAttrs = new StringBuilder();
		commonItemPairAttrs.append("<table class='table table-hover table-bordered table-condensed tablesorter'>");
		commonItemPairAttrs.append("<thead><tr><th>Attribute</th><th>" + getSourceItem() + "</th><th>" + getTargetItem() + "</th></tr></thead>");

		commonItemPairAttrs.append("</tbody>");
		for(AttributeMatchClauseItemPairAttrs itemPairAttrValue : getClauseAttrValues()) {
			String attrName = itemPairAttrValue.getAttributeName();
			List<String> sourceItemAttrValues = itemPairAttrValue.getSourceItemAttributeValues();
			List<String> targetItemAttrValues = itemPairAttrValue.getTargetItemAttributeValues();
			commonItemPairAttrs.append("<tr>");
			commonItemPairAttrs.append("<td>").append(attrName).append("</td>");
			if(sourceItemAttrValues.isEmpty()) {
				commonItemPairAttrs.append("<td>").append("<label class='text-error'>NA</label>").append("</td>");
			}
			else {
				commonItemPairAttrs.append("<td>").append(sourceItemAttrValues.toString()).append("</td>");				
			}

			if(targetItemAttrValues.isEmpty()) {
				commonItemPairAttrs.append("<td>").append("<label class='text-error'>NA</label>").append("</td>");
			}
			else {
				commonItemPairAttrs.append("<td>").append(targetItemAttrValues.toString()).append("</td>");				
			}

			commonItemPairAttrs.append("</tr>");
		}

		commonItemPairAttrs.append("</tbody>");
		commonItemPairAttrs.append("</table>");

		return commonItemPairAttrs.toString();
		
	}
}
