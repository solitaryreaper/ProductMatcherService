package com.walmart.productgenome.pairComparison.utils.tokenizers;

import java.util.HashMap;
import java.util.Map;

import com.walmart.productgenome.pairComparison.model.Constants;

public class TokenizerFactory {

	private static final IStringTokenizer WHITE_SPACE_TOKENIZER = new WhiteSpaceTokenizer();
	private static final IStringTokenizer NO_TOKENIZER = new NoTokenizer();
	private static final IStringTokenizer AGGRESSIVE_TOKENIZER = new AggressiveTokenizer();
	private static final IStringTokenizer STANDARD_ANALYZER_TOKENIZER = new StandardAnalyzerTokenizer();
	
	private static final IStringTokenizer SEPARATOR_TOKENIZER = new SeparatorBasedTokenizer(Constants.SEPARATOR_DELIMITER);
	
	private static final Map<String, IStringTokenizer> TOKENIZER_MAP = new HashMap<String, IStringTokenizer>();
	static {
		TOKENIZER_MAP.put(Constants.WHITESPACE_TOKENIZER, 	WHITE_SPACE_TOKENIZER);
		TOKENIZER_MAP.put(Constants.NO_TOKENIZER, 			NO_TOKENIZER);
		TOKENIZER_MAP.put(Constants.AGGRESSIVE_TOKENIZER, 	AGGRESSIVE_TOKENIZER);
		TOKENIZER_MAP.put(Constants.STANDARD_TOKENIZER, 	STANDARD_ANALYZER_TOKENIZER);
		TOKENIZER_MAP.put(Constants.SEPARATOR_TOKENIZER, 	SEPARATOR_TOKENIZER);
	}
	
	public static IStringTokenizer getTokenizer(final String tokenizerType) {
		if (TOKENIZER_MAP.containsKey(tokenizerType) == false)
			throw new IllegalArgumentException("tokenizer type cannot be null");
		
		return TOKENIZER_MAP.get(tokenizerType);
	}
	
	public static String getTokenizerType(IStringTokenizer tokenizer)
	{
		String tokenizerType = null;
		for(Map.Entry<String, IStringTokenizer> entry : TOKENIZER_MAP.entrySet()) {
			IStringTokenizer currTokenizer = entry.getValue();
			if(currTokenizer.getClass().equals(tokenizer.getClass())) {
				tokenizerType = entry.getKey();
			}
		}
		
		return tokenizerType;
	}
}
