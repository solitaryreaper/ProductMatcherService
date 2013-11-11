package com.walmart.productgenome.pairComparison.utils.tokenizers;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.google.common.collect.Lists;

public class NoTokenizer implements IStringTokenizer {

	/*
	 * (non-Javadoc)
	 * @see com.crowdmull.catalog.tokenizers.IStringTokenizer#tokenize(java.lang.String)
	 */
	
	public List<String> tokenize(String stringToBeTokenized) {
		if (stringToBeTokenized == null) throw new IllegalArgumentException("string to be tokenized cannot be null");
		
		//this is a no-op
		List<String> noTokenizationList = Lists.newArrayList();
		// Optimization : No need to add blank lines
		if(StringUtils.isNotBlank(stringToBeTokenized)) {
			noTokenizationList.add(stringToBeTokenized.trim().toLowerCase());
		}
		return noTokenizationList;
	}

}
