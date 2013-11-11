package com.walmart.productgenome.pairComparison.utils.tokenizers;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.Version;

import com.google.common.collect.Lists;

/**
 * Implements the standard lucene tokenizer
 * @author sprasa4
 *
 */
public class StandardAnalyzerTokenizer implements IStringTokenizer {
	
	/*
	 * (non-Javadoc)
	 * @see com.walmart.productgenome.tokenizers.IStringTokenizer#tokenize(java.lang.String)
	 */
	
	public List<String> tokenize(final String stringToBeTokenized) {
		if (stringToBeTokenized == null) throw new IllegalArgumentException("string to be tokenized cannot be null");
		
		//constructing the standard analyzer and using it
		StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_33);
		TokenStream stream = analyzer.tokenStream(null, new StringReader(stringToBeTokenized.trim().toLowerCase()));
		
		//reading the tokens into the result list
		List<String> result = Lists.newArrayList();
		String token = null;
		try {
            while(stream.incrementToken()) {
            	if(stream.getAttribute(CharTermAttribute.class) != null) {
                	token = stream.getAttribute(CharTermAttribute.class).toString().trim();
                	// Don't add null or empty tokens
                	if(token == null || token.isEmpty() || StringUtils.isBlank(token)) {
                		continue;
                	}
                	
                    result.add(token);            		
            	}
            }
        }
        catch(IOException e) {
            // not thrown b/c we're using a string reader...
        }

		//closing the analyzer
		analyzer.close();
		
        return result;
	}

}
