package com.walmart.productgenome.pairComparison.utils.rule.generator;

import java.util.List;

/**
 * Utility class that stores the results of rule recommendations.
 * 
 * @author sprasa4
 *
 */
public class AttributeValueAnalysis {
	private String attrName;
	private String sourceItemDataSource;
	private String targetItemDataSource;
	
	// stats about the attribute in the dataset
	private double globalOccurencePercent;
	private double sourceOccurencePercent;
	private double targetOccurencePercent;
	private double sourceUniqueValuesPercent;
	private double targetUniqueValuesPercent;
	
	// recommendations of target attributes
	private List<String> relevantAttrsMapFromSourceToTarget;
	private List<String> relevantAttrsMapFromTargetToSource;
	
	public AttributeValueAnalysis()
	{
		
	}
	
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("RuleRecommendation [attrName=").append(attrName)
				.append(", sourceItemDataSource=").append(sourceItemDataSource)
				.append(", targetItemDataSource=").append(targetItemDataSource)
				.append(", globalOccurencePercent=")
				.append(globalOccurencePercent)
				.append(", sourceOccurencePercent=")
				.append(sourceOccurencePercent)
				.append(", targetOccurencePercent=")
				.append(targetOccurencePercent)
				.append(", sourceUniqueValuesPercent=")
				.append(sourceUniqueValuesPercent)
				.append(", targetUniqueValuesPercent=")
				.append(targetUniqueValuesPercent)
				.append(", relevantAttrsMapFromSourceToTarget=")
				.append(relevantAttrsMapFromSourceToTarget)
				.append(", relevantAttrsMapFromTargetToSource=")
				.append(relevantAttrsMapFromTargetToSource).append("]");
		return builder.toString();
	}


	public String getAttrName() {
		return attrName;
	}
	public void setAttrName(String attrName) {
		this.attrName = attrName;
	}
	public String getSourceItemDataSource() {
		return sourceItemDataSource;
	}
	public void setSourceItemDataSource(String sourceItemDataSource) {
		this.sourceItemDataSource = sourceItemDataSource;
	}
	public String getTargetItemDataSource() {
		return targetItemDataSource;
	}
	public void setTargetItemDataSource(String targetItemDataSource) {
		this.targetItemDataSource = targetItemDataSource;
	}
	public double getGlobalOccurencePercent() {
		return globalOccurencePercent;
	}
	public void setGlobalOccurencePercent(double globalOccurencePercent) {
		this.globalOccurencePercent = globalOccurencePercent;
	}
	public double getSourceOccurencePercent() {
		return sourceOccurencePercent;
	}
	public void setSourceOccurencePercent(double sourceOccurencePercent) {
		this.sourceOccurencePercent = sourceOccurencePercent;
	}
	public double getTargetOccurencePercent() {
		return targetOccurencePercent;
	}
	public void setTargetOccurencePercent(double targetOccurencePercent) {
		this.targetOccurencePercent = targetOccurencePercent;
	}
	public double getSourceUniqueValuesPercent() {
		return sourceUniqueValuesPercent;
	}
	public void setSourceUniqueValuesPercent(double sourceUniqueValuesPercent) {
		this.sourceUniqueValuesPercent = sourceUniqueValuesPercent;
	}
	public double getTargetUniqueValuesPercent() {
		return targetUniqueValuesPercent;
	}
	public void setTargetUniqueValuesPercent(double targetUniqueValuesPercent) {
		this.targetUniqueValuesPercent = targetUniqueValuesPercent;
	}
	public List<String> getRelevantAttrsMapFromSourceToTarget() {
		return relevantAttrsMapFromSourceToTarget;
	}
	public void setRelevantAttrsMapFromSourceToTarget(
			List<String> relevantAttrsMapFromSourceToTarget) {
		this.relevantAttrsMapFromSourceToTarget = relevantAttrsMapFromSourceToTarget;
	}
	public List<String> getRelevantAttrsMapFromTargetToSource() {
		return relevantAttrsMapFromTargetToSource;
	}
	public void setRelevantAttrsMapFromTargetToSource(
			List<String> relevantAttrsMapFromTargetToSource) {
		this.relevantAttrsMapFromTargetToSource = relevantAttrsMapFromTargetToSource;
	}
}
