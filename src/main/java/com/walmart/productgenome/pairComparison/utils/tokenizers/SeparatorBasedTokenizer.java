package com.walmart.productgenome.pairComparison.utils.tokenizers;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public class SeparatorBasedTokenizer implements IStringTokenizer {

	private final String m_separator;
	
	public SeparatorBasedTokenizer(final String separator) {
		m_separator = separator;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.walmart.productgenome.tokenizers.IStringTokenizer#tokenize(java.lang.String)
	 */
	
	public List<String> tokenize(String stringToBeTokenized) {
		if (stringToBeTokenized == null) throw new IllegalArgumentException("string to be tokenized cannot be null");
		
		String[] tokens = stringToBeTokenized.trim().toLowerCase().split(m_separator);
		List<String> tokenList = new ArrayList<String>();
		for (String token : tokens) {
			if(StringUtils.isBlank(token)) {
				continue;
			}
			
			tokenList.add(token);
		}
		return tokenList;
	}

}
