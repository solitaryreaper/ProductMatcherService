package com.walmart.productgenome.pairComparison.model.rule;

import java.util.Map;
import java.util.Set;

import com.google.common.base.Objects;

public class MatchEntityPair implements Comparable<MatchEntityPair> {

	//------------------------------------
	// Variables - Private
	//------------------------------------
	
	/** the first item of the pair */
	private final MatchEntity m_sourceItem;
	
	/** the second item of the pair */
	private final MatchEntity m_targetItem;
	
	//------------------------------------
	// Public - Constructor
	//------------------------------------
	
	public MatchEntityPair(final MatchEntity item1, final MatchEntity item2) {
		if (item1 == null) throw new IllegalArgumentException("item 1 cannot be null");
		if (item2 == null) throw new IllegalArgumentException("item 2 cannot be null");
		
		//compare the two items and let the item that is lower be item1 and the 
		//other item be item2
		if (item1.compareTo(item2) <= 0) {
			m_sourceItem = item1;
			m_targetItem = item2;
		}
		else {
			m_sourceItem = item2;
			m_targetItem = item1;
		}
	}
	
	//------------------------------------
	// Public - Accessors
	//------------------------------------
	
	public MatchEntity getSourceItem() {return m_sourceItem;}
	public MatchEntity getTargetItem() {return m_targetItem;}

	//------------------------------------
	// Public - HashCode and Equals
	//------------------------------------
	
	
	public int hashCode() {
		return Objects.hashCode(m_sourceItem, m_targetItem);
	}

	public boolean equals(Object obj) {
	    if (obj == null) return false;
	    if (getClass() != obj.getClass()) return false;
	    final MatchEntityPair other = (MatchEntityPair) obj;
	    return 	Objects.equal(this.m_sourceItem, other.m_sourceItem) &&
	    		Objects.equal(this.m_targetItem, other.m_targetItem);
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(MatchEntityPair o) {
		//compare the first item of the pair
		int comparisonResult = m_sourceItem.compareTo(o.getTargetItem());
		if (comparisonResult != 0) return comparisonResult;
		
		//the first item of the pairs were the same, so now compare the
		//second item
		return m_targetItem.compareTo(o.getTargetItem());
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(" SOURCE ITEM = [").append(m_sourceItem.getItemID()).append(" | ").append(m_sourceItem.getSource()).append("]")
			   .append(", TARGET ITEM = [").append(m_targetItem.getItemID()).append(" | ").append(m_targetItem.getSource()).append("]");
		return builder.toString();
	}
	
	// Concise representation of common itempair attributes
	// TODO : hack to generate dynamic html to show this in popover. Trying to figure out how to
	// dynamically generate the html in popover using play templating features.
	public String getItemPairCommonAttrs()
	{
		StringBuilder commonItemPairAttrs = new StringBuilder();
		Map<String, Set<String>> sourceItemAttrsMap = getSourceItem().getAttributeNameValueSetMap();
		Map<String, Set<String>> targetItemAttrsMap = getTargetItem().getAttributeNameValueSetMap();
		commonItemPairAttrs.append("<table class='table table-hover table-bordered table-condensed tablesorter'>");
		commonItemPairAttrs.append("<thead><tr><th>Attribute</th><th>Source Item Value</th><th>Target Item Value</th></tr></thead>");
		
		commonItemPairAttrs.append("<tbody>");
		for(Map.Entry<String, Set<String>> entry : sourceItemAttrsMap.entrySet()) {
			String attrName = entry.getKey();
			String sourceAttrValue = getSourceItem().getValuesForAttributeName(attrName).toString();
			if(targetItemAttrsMap.containsKey(attrName)) {
				String targetAttrValue = getTargetItem().getValuesForAttributeName(attrName).toString();
				commonItemPairAttrs.append("<tr>");
				commonItemPairAttrs.append("<td>").append(attrName).append("</td>");
				commonItemPairAttrs.append("<td>").append(sourceAttrValue).append("</td>");
				commonItemPairAttrs.append("<td>").append(targetAttrValue).append("</td>");
				commonItemPairAttrs.append("</tr>");
			}
		}
		commonItemPairAttrs.append("</tbody>");
		commonItemPairAttrs.append("</table>");

		return commonItemPairAttrs.toString();
	}
	
}
