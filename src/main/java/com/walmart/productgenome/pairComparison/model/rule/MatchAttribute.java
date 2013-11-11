package com.walmart.productgenome.pairComparison.model.rule;

import java.util.Set;

import com.google.common.base.Objects;
import com.google.common.collect.Sets;

/**
 * A model object for a single attribute for any product.
 * @author sprasa4
 *
 */
public class MatchAttribute {

	private final String m_attributeID;
	private final String m_attributeName;
	private final String m_attributeValue;
	
	private static String CATEGORY_ATTRIBUTE = "req_category";
	private static String BRAND_ATTRIBUTE    = "req_brand_name";
	
	private static Set<String> categoryStopwords = 
		Sets.newHashSet("total", "TOTAL",  "all", "ALL", "Unassigned", "UNASSIGNED", "Special", "General", "GENERAL", 
				"generic", "Generic", "GENERIC",  "Other", "Others",  "Basic", "BASIC", "VARIETY", "Free");
	private static Set<String> brandStopwords = 
		Sets.newHashSet("ONLINE", "Generic", "GENERIC",  "Unbranded", "UNBRANDED",  "General", "GENERAL",  "NONE", "VENDOR");

	public MatchAttribute(final String attributeID, final String attributeName, final String attributeValue) {
		if (attributeID == null) throw new IllegalArgumentException("attribute ID cannot be null");
		if (attributeName == null) throw new IllegalArgumentException("attribute name cannot be null");
		if (attributeValue == null) throw new IllegalArgumentException("attribute value cannot be null");
		
		m_attributeID = attributeID;
		m_attributeName = attributeName;
		m_attributeValue = cleanupAttributeValue(attributeName, attributeValue);
	}

	//------------------------------------
	// Accessors
	//------------------------------------
	
	public String getAttributeID() {return m_attributeID;}
	public String getAttributeName() {return m_attributeName;}
	public String getAttributeValue() {return m_attributeValue;}
	
	//------------------------------------
	// Hashcode and Equals
	//------------------------------------
	
	@Override
	public int hashCode() {
		return Objects.hashCode(m_attributeID, m_attributeName, m_attributeValue);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MatchAttribute other = (MatchAttribute) obj;
		if (m_attributeID == null) {
			if (other.m_attributeID != null)
				return false;
		} else if (!m_attributeID.equals(other.m_attributeID))
			return false;
		if (m_attributeName == null) {
			if (other.m_attributeName != null)
				return false;
		} else if (!m_attributeName.equals(other.m_attributeName))
			return false;
		if (m_attributeValue == null) {
			if (other.m_attributeValue != null)
				return false;
		} else if (!m_attributeValue.equals(other.m_attributeValue))
			return false;
		return true;
	}

	// TODO : Temporary hack till the input data is cleaned up at the source.
	// Remove some special characters from the string/
	// This hack is done because the initial data dump given had the old delimiter led to some issues.
	// Some migrate this data to the new delimiter.
	private static String cleanupAttributeValue(String attributeName, String attributeValue)
	{
		if(attributeValue.contains("^A") || attributeValue.contains("\001")) {
			attributeValue = attributeValue.replaceAll("^A", ";#");
			attributeValue = attributeValue.replaceAll("\001", ";#");
		}

		//attributeValue = removeStopWords(attributeName, attributeValue);
		return attributeValue;
	}
	
	// Remove stop words from some key attributes like category and brand name etc.
	private static String removeStopWords(String attributeName, String attributeValue)
	{
		if(attributeName.contains(CATEGORY_ATTRIBUTE)) {
			for(String stopword : categoryStopwords) {
				if(attributeValue.contains(stopword)) {
					attributeValue = attributeValue.replaceAll(stopword, "");
				}
			}
		}
		
		if(attributeName.contains(BRAND_ATTRIBUTE)) {
			for(String stopword : brandStopwords) {
				if(attributeValue.contains(stopword)) {
					attributeValue = attributeValue.replaceAll(stopword, "");					
				}
			}			
		}
		
		return attributeValue;
	}
	

}
