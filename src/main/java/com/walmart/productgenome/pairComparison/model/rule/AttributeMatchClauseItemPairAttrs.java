package com.walmart.productgenome.pairComparison.model.rule;

import java.util.List;

/**
 * Contains the item pair attribute values for the attributes involved in the attribute match clause.
 * This is very useful for the auditing to specifically show the attribute values that are being
 * evaluated as part of the current clause.
 * @author sprasa4
 *
 */
public class AttributeMatchClauseItemPairAttrs {
	private String attributeName;
	private List<String> sourceItemAttributeValues;
	private List<String> targetItemAttributeValues;

	public AttributeMatchClauseItemPairAttrs(String attrName, List<String> sourceItemAttrValues, List<String> targetItemAttrValues)
	{
		this.attributeName = attrName;
		this.sourceItemAttributeValues = sourceItemAttrValues;
		this.targetItemAttributeValues = targetItemAttrValues;
	}
	
	public String getAttributeName() {
		return attributeName;
	}
	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}
	public List<String> getSourceItemAttributeValues() {
		return sourceItemAttributeValues;
	}
	public void setSourceItemAttributeValues(List<String> sourceItemAttributeValues) {
		this.sourceItemAttributeValues = sourceItemAttributeValues;
	}
	public List<String> getTargetItemAttributeValues() {
		return targetItemAttributeValues;
	}
	public void setTargetItemAttributeValues(List<String> targetItemAttributeValues) {
		this.targetItemAttributeValues = targetItemAttributeValues;
	}
	
}
