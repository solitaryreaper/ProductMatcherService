package com.walmart.productgenome.pairComparison.utils.tokenizers;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public class AggressiveTokenizer implements IStringTokenizer {

	/*
	 * (non-Javadoc)
	 * @see com.crowdmull.catalog.tokenizers.IStringTokenizer#tokenize(java.lang.String)
	 */
	
	public List<String> tokenize(String stringToBeTokenized) {
		if (stringToBeTokenized == null) throw new IllegalArgumentException("string to be tokenized cannot be null");
		
		List<String> tokenList = new ArrayList<String>();
		
		//first tokenize the string based on white space
		String[] tokens = stringToBeTokenized.trim().split("\\s+");
		
		for (String token : tokens) {
			if(token == null || token.isEmpty() || StringUtils.isBlank(token)) {
				continue;
			}
						
			//split this token based on the "-" character
			String[] subTokens = token.split("\\-");
			for (String subToken : subTokens) {
				tokenList.add(subToken);
			}
		}
		
		return tokenList;
	}

}
