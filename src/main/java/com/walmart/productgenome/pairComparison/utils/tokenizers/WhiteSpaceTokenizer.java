package com.walmart.productgenome.pairComparison.utils.tokenizers;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public class WhiteSpaceTokenizer implements IStringTokenizer {

	public List<String> tokenize(final String stringToBeTokenized) {
		if (stringToBeTokenized == null) throw new IllegalArgumentException("string to be tokenized cannot be null");
		
		String[] tokens = stringToBeTokenized.trim().toLowerCase().split("\\s+");
		List<String> tokenList = new ArrayList<String>();
		for (String token : tokens) {
			if(token == null || token.isEmpty() || StringUtils.isBlank(token)) {
				continue;
			}
			
			tokenList.add(token);
		}
		return tokenList;
	}
}
