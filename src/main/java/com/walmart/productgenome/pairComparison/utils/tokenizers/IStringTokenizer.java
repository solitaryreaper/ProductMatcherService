package com.walmart.productgenome.pairComparison.utils.tokenizers;

import java.util.List;

public interface IStringTokenizer {

	/**
	 * Tokenizes the given string and returns the list of strings.
	 * 
	 * Note : Make sure that the attribute value is lowercased before processing to
	 *		  enable case-insensitive matching. In case, lowercasing of string has to be avoided
	 *        for some tokenizers, don't lowercase the string to be tokenized in the specific
	 *        tokenizer.
	 *		  
	 * @param stringToBeTokenized the string to be tokenized [NOT NULL]
	 * @return the list of tokenized strings
	 */
	public List<String> tokenize(final String stringToBeTokenized);
}
