package com.walmart.productgenome.pairComparison.audit;

import com.walmart.productgenome.pairComparison.model.MatchStatus;
import com.walmart.productgenome.pairComparison.utils.NumberUtils;

/**
 * Collects audit information about a single source token while being matched against a list of 
 * target attribute tokens.
 * 
 * @author sprasa4
 *
 */
public class TokenAuditEntity {
	private String sourceToken;
	private MatchStatus status = MatchStatus.FAILURE; // Default initial value
	private String targetTokens;
	private double tokenScore = 0.0; // Default initial value
	private String tokenMatchReason = "NO REASON FOUND"; // Default initial value
	
	private String sourceItem;
	private String targetItem;
	
	public TokenAuditEntity()
	{
		
	}
	
	public TokenAuditEntity(String sourceToken, MatchStatus status, String targetTokens, double tokenScore, String tokenMatchReason) {
		super();
		this.sourceToken = sourceToken;
		this.status = status;
		this.targetTokens = targetTokens;
		this.tokenScore = tokenScore;
		this.tokenMatchReason = tokenMatchReason;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((sourceItem == null) ? 0 : sourceItem.hashCode());
		result = prime * result
				+ ((sourceToken == null) ? 0 : sourceToken.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result
				+ ((targetItem == null) ? 0 : targetItem.hashCode());
		result = prime * result
				+ ((targetTokens == null) ? 0 : targetTokens.hashCode());
		result = prime
				* result
				+ ((tokenMatchReason == null) ? 0 : tokenMatchReason.hashCode());
		long temp;
		temp = Double.doubleToLongBits(tokenScore);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TokenAuditEntity other = (TokenAuditEntity) obj;
		if (sourceItem == null) {
			if (other.sourceItem != null)
				return false;
		} else if (!sourceItem.equals(other.sourceItem))
			return false;
		if (sourceToken == null) {
			if (other.sourceToken != null)
				return false;
		} else if (!sourceToken.equals(other.sourceToken))
			return false;
		if (status != other.status)
			return false;
		if (targetItem == null) {
			if (other.targetItem != null)
				return false;
		} else if (!targetItem.equals(other.targetItem))
			return false;
		if (targetTokens == null) {
			if (other.targetTokens != null)
				return false;
		} else if (!targetTokens.equals(other.targetTokens))
			return false;
		if (tokenMatchReason == null) {
			if (other.tokenMatchReason != null)
				return false;
		} else if (!tokenMatchReason.equals(other.tokenMatchReason))
			return false;
		if (Double.doubleToLongBits(tokenScore) != Double
				.doubleToLongBits(other.tokenScore))
			return false;
		return true;
	}

	public String getSourceTokens() {
		return sourceToken;
	}
	
	public void setSourceToken(String sourceToken) {
		this.sourceToken = sourceToken;
	}
	
	public MatchStatus getStatus() {
		return status;
	}
	
	public void setStatus(MatchStatus status) {
		this.status = status;
	}
	
	public String getTargetTokens() {
		return targetTokens;
	}
	
	public void setTargetTokens(String targetTokens) {
		this.targetTokens = targetTokens;
	}

	public String getTokenScore() {
		return NumberUtils.formatDouble(tokenScore);
	}

	public void setTokenScore(double tokenScore) {
		this.tokenScore = tokenScore;
	}


	public String getTokenMatchReason() {
		return tokenMatchReason;
	}

	public boolean doesTokenNeedAnalysis()
	{
		return (Double.compare(tokenScore, 1.00) < 0);
	}

	public void setTokenMatchReason(String tokenMatchReason) {
		this.tokenMatchReason = tokenMatchReason;
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
		builder	.append("\n sourceToken=").append(sourceToken)
				.append(", \ttargetTokens=").append(targetTokens)
				.append(", \ttokenScore=").append(tokenScore)
				.append(", \nstatus=").append(status)
				.append(", \ttokenMatchReason=").append(tokenMatchReason.toString())
				.append(", \tmatchDirection=").append(sourceItem).append(" -> ").append(targetItem);
		return builder.toString();
	}
	
}
