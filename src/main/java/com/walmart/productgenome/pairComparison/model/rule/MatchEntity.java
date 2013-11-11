package com.walmart.productgenome.pairComparison.model.rule;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.walmart.productgenome.pairComparison.model.Constants;

/**
 * A basic entity that can be processed by the product matching framework. This represents a single
 * product item. This has been completely derived from 
 * {com.walmart.productgenome.common.dataTypes.Item.java}
 * @author sprasa4
 *
 */
public class MatchEntity implements Comparable<MatchEntity> {

	//------------------------------------
	// Variables - Private
	//------------------------------------
	
	/** the id of the item */
	private final String m_itemID;
	
	/** the source of the item */
	private final String m_source;
	
	/** the name of the item */
	private final String m_name;
	
	/** the set of all attributes */
	private final Set<MatchAttribute> m_attributeSet;
	
	/** maps each attribute name to the set of all values present for that attribute name */
	private Map<String, Set<String>> m_attributeNameValueSetMap;
	
	/** maps each attribute id to the corresponding attribute */
	private final Map<String, MatchAttribute> m_idAttributeMap;
	
	/** */
	private final Map<String, Set<String>> m_generatedKeysAndValuesMap;
	
	//------------------------------------
	// Public - Constructor
	//------------------------------------
	
	public MatchEntity(final String itemID, final String source, final String name) {
		if (itemID == null) throw new IllegalArgumentException("itemID cannot be null");
		if (source == null) throw new IllegalArgumentException("source cannot be null");
		if (name == null) throw new IllegalArgumentException("name cannot be null");
		
		m_itemID = itemID;
		m_source = source;
		m_name = name;
		
		m_attributeSet = new HashSet<MatchAttribute>();
		m_attributeNameValueSetMap = Maps.newLinkedHashMap();
		m_idAttributeMap = Maps.newLinkedHashMap();
		m_generatedKeysAndValuesMap = Maps.newLinkedHashMap();
		
		//add the name as an attribute
		MatchAttribute titleAttr = new MatchAttribute("attrId", SourceAttributeNormalizationMap.TITLE_ATTRIBUTE, m_name);
		addAttribute(titleAttr);
	}
	
	public MatchEntity(final String itemID, final String source, final String name, final Set<MatchAttribute> attributeSet) {
		if (itemID == null) throw new IllegalArgumentException("itemID cannot be null");
		if (source == null) throw new IllegalArgumentException("source cannot be null");
		if (name == null) throw new IllegalArgumentException("name cannot be null");
		
		m_itemID = itemID;
		m_source = source;
		m_name = name;
		
		m_attributeSet = new HashSet<MatchAttribute>();
		m_attributeNameValueSetMap = new HashMap<String, Set<String>>();
		m_idAttributeMap = new HashMap<String, MatchAttribute>();
		
		//add the attributes
		for (MatchAttribute attr : attributeSet) {
			addAttribute(attr);
		}
		
		//add the name as an attribute
		MatchAttribute titleAttr = new MatchAttribute("attrId", SourceAttributeNormalizationMap.TITLE_ATTRIBUTE, m_name);
		addAttribute(titleAttr);
		
		m_generatedKeysAndValuesMap = new HashMap<String, Set<String>>();
	}
	
	//------------------------------------
	// Public - Accessors
	//------------------------------------
	
	public String getItemID() {return m_itemID;}
	public String getSource() {return m_source;}
	public Set<MatchAttribute> getAllAttributes() {return m_attributeSet;}
	public String getName() 
	{
		String title = getValuesForAttributeName(Constants.PD_TITLE).toString();
		title = title.replaceAll("\\[", "");
		title = title.replaceAll("]", "");
		return title;
	} 
	
	public Set<String> getValuesForAttributeName(final String attributeName) {
		// TODO : Slight hack to return signing_desc attribute value for pd_title
		// when the source is WALMART STORES. But when the category is books "signing_desc" contains
		// ISBN, so in those cases just return the existing title name. 
		
		// This needs to be fixed at the source but for the time being, this is the best solution.
		
		/*
		Set<String> attributeValues = null;
		
		// TODO : Hack for title for Walmart Stores
		if(attributeName.equals(Constants.PD_TITLE) && getSource().equals("WALMART_STORES")) {
			// try to use the value of "signing_desc" attribute instead
			attributeValues = getValuesForAttributeName("signing_desc");
			boolean isSigningDescNumeric = isSigningDescNumeric(attributeValues);
			
			// if "signing_desc" attribute is empty, then use the value of title
			// if "signing_desc" attribute is numeric, then use the value of title		
			if(attributeValues == null || isSigningDescNumeric) {
				attributeValues = m_attributeNameValueSetMap.get(attributeName);
			}
		}
		else {
			attributeValues = m_attributeNameValueSetMap.get(attributeName);
		}
				
		return attributeValues;
		
		*/
		return m_attributeNameValueSetMap.get(attributeName);
	}
	
	public MatchAttribute getAttributeForID(final String attributeID) {
		return m_idAttributeMap.get(attributeID);
	}
	
	public Set<String> getGeneratedKeyValueSet(final String generatedKeyName) {
		return m_generatedKeysAndValuesMap.get(generatedKeyName);
	}
	
	public Map<String, Set<String>> getAllGeneratedKeys() {
		return m_generatedKeysAndValuesMap;
	}
	
	//------------------------------------
	// Public - Mutators
	//------------------------------------
	
	/**
	 * Adds an attribute to this item
	 * @param attribute the attribute to be added [NOT NULL]
	 */
	public void addAttribute(final MatchAttribute attribute) {
		if (attribute == null) throw new IllegalArgumentException("attribute cannot be null");
		
		//1) add it to attribute set
		m_attributeSet.add(attribute);
		
		//2) add the the attribute to the attributeNameValueSet Map
		final String attributeName = attribute.getAttributeName();
		final String attributeValue = attribute.getAttributeValue();
		
		Set<String> attributeValueSet = m_attributeNameValueSetMap.get(attributeName);
		if (attributeValueSet == null) {
			attributeValueSet = Sets.newHashSet();
		}
		attributeValueSet.add(attributeValue);
		m_attributeNameValueSetMap.put(attributeName, attributeValueSet);
		
		//3) add the attribute to the idAttribute Map
		m_idAttributeMap.put(attribute.getAttributeID(), attribute);
	}
	
	public Map<String, Set<String>> getAttributeNameValueSetMap() {
		return m_attributeNameValueSetMap;
	}

	public void setAttributeNameValueSetMap(
			Map<String, Set<String>> m_attributeNameValueSetMap) {
		this.m_attributeNameValueSetMap = m_attributeNameValueSetMap;
	}

	public void addGeneratedKey(final String generatedKeyName, final String value) {
		Set<String> valueSet = m_generatedKeysAndValuesMap.get(generatedKeyName);
		if (valueSet == null) {
			valueSet = new HashSet<String>();
			m_generatedKeysAndValuesMap.put(generatedKeyName, valueSet);
		}
		valueSet.add(value);
	}
	
	public void removeAttribute(final String attributeName) {
		//remove it from the attribute set
		Set<MatchAttribute> attributesToRemove = new HashSet<MatchAttribute>();
		for (MatchAttribute attr : m_attributeSet) {
			if (attr.getAttributeName().equals(attributeName) == true) {
				attributesToRemove.add(attr);
			}
		}
		m_attributeSet.removeAll(attributesToRemove);
		
		//remove it from the name value map
		m_attributeNameValueSetMap.remove(attributeName);
		
		//remove it from the id name map
		for (MatchAttribute attr : attributesToRemove) {
			m_idAttributeMap.remove(attr.getAttributeID());
		}
	}
	
	//------------------------------------
	// Public - Hashcode and Equals
	//------------------------------------
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((m_itemID == null) ? 0 : m_itemID.hashCode());
		result = prime * result
				+ ((m_source == null) ? 0 : m_source.hashCode());
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
		MatchEntity other = (MatchEntity) obj;
		if (m_itemID == null) {
			if (other.m_itemID != null)
				return false;
		} else if (!m_itemID.equals(other.m_itemID))
			return false;
		if (m_source == null) {
			if (other.m_source != null)
				return false;
		} else if (!m_source.equals(other.m_source))
			return false;
		return true;
	}

	//------------------------------------
	// Public - Implementation
	//------------------------------------
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(MatchEntity other) {
		//first compare the sources
		int sourceComparisonResult = m_source.compareTo(other.getSource());
		if (sourceComparisonResult != 0) return sourceComparisonResult;
		
		//the sources are the same, so compare the ids
		return m_itemID.compareTo(other.getItemID());
	}
	
	private static class SourceAttributeNormalizationMap {

		public static final String UPC_ATTRIBUTE = "pd_upc";
		public static final String BRAND_ATTRIBUTE = "pd_brand";
		public static final String MANUFACTURER_ATTRIBUTE = "pd_manufacturer";
		public static final String TITLE_ATTRIBUTE = "pd_title";
		
		private static final Map<String, Map<String, String>> ATTR_MAP = new HashMap<String, Map<String,String>>();
		static {
			Map<String, String> WALMART_DOT_COM_MAP = new HashMap<String, String>();
			
			WALMART_DOT_COM_MAP.put("upc", UPC_ATTRIBUTE);
			WALMART_DOT_COM_MAP.put("standard_upc", UPC_ATTRIBUTE);
			WALMART_DOT_COM_MAP.put("gtin", UPC_ATTRIBUTE);
			
			WALMART_DOT_COM_MAP.put("manufacturer", MANUFACTURER_ATTRIBUTE);
			WALMART_DOT_COM_MAP.put("manufacturer name", MANUFACTURER_ATTRIBUTE);
			WALMART_DOT_COM_MAP.put("manufacturer_name", MANUFACTURER_ATTRIBUTE);
			
			WALMART_DOT_COM_MAP.put("brand", BRAND_ATTRIBUTE);
			WALMART_DOT_COM_MAP.put("brand name", BRAND_ATTRIBUTE);
			WALMART_DOT_COM_MAP.put("brand_name", BRAND_ATTRIBUTE);
			
			ATTR_MAP.put("WALMART_DOTCOM", WALMART_DOT_COM_MAP);
			
			Map<String, String> WALMART_STORES_MAP = new HashMap<String, String>();
		
			WALMART_STORES_MAP.put("upc_nbr", UPC_ATTRIBUTE);
			WALMART_STORES_MAP.put("case_upc_nbr", UPC_ATTRIBUTE);
			
			WALMART_STORES_MAP.put("brand_name", BRAND_ATTRIBUTE);
			
			ATTR_MAP.put("WALMART_STORES", WALMART_STORES_MAP);
			
			Map<String, String> WALMART_SEARCH_EXTRACT_MAP = new HashMap<String, String>();
			
			WALMART_SEARCH_EXTRACT_MAP.put("upc", UPC_ATTRIBUTE);
			WALMART_SEARCH_EXTRACT_MAP.put("standard_upc", UPC_ATTRIBUTE);
			
			ATTR_MAP.put("WALMART_SEARCH_EXTRACT", WALMART_SEARCH_EXTRACT_MAP);
		}
		
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MatchEntity [m_itemID=").append(m_itemID)
				.append(", m_source=").append(m_source).append(", m_name=")
				.append(m_name).append(", m_attributeSet=")
				.append(m_attributeSet).append(", m_attributeNameValueSetMap=")
				.append(m_attributeNameValueSetMap)
				.append(", m_idAttributeMap=").append(m_idAttributeMap)
				.append(", m_generatedKeysAndValuesMap=")
				.append(m_generatedKeysAndValuesMap).append("]");
		return builder.toString();
	}

	/**
	 * Temporary hack method to test if signing desc attribute is numeric or not.
	 * @param values
	 * @return
	 */
	private static boolean isSigningDescNumeric(Set<String> values)
	{
		boolean isSigningDescNumeric = false;
		if(CollectionUtils.isNotEmpty(values)) {
			for(String value : values) {
				if(StringUtils.isNumeric(value)) {
					isSigningDescNumeric = true;
					break;
				}
			}
		}
		
		return isSigningDescNumeric;
	}
}
