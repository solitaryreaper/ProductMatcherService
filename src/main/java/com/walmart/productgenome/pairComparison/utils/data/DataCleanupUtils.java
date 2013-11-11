package com.walmart.productgenome.pairComparison.utils.data;

import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.walmart.productgenome.pairComparison.model.Constants;

/**
 * Utility class to pre-process the itempair data file before initiating the product matching process.
 * Some of the cleanup undertaken by this class would be as follows :
 * 	1) Cleanup web characters like &amp; and replace with their corresponding english representaion.
 * 	2) Transform roman numerals into their corresponding numeric representation.
 * 	3) Replace special characters from the string like hyphens, quotes etc.
 * 
 * @author sprasa4
 *
 */
public class DataCleanupUtils {

	private static String HTML_TAG_REGEX = "<.+?>";
	private static String NONASCII_CHAR_REGEX = "[^\\x20-\\x7e]";
	
	/**
	 * Cleans up string from itempair data file before invoking the actual product matching.
	 * 
	 * @param inputLine
	 * @return
	 */
	public static String cleanUpString(String inputLine)
	{
		// http://stackoverflow.com/questions/2287473/how-do-i-convert-special-characters-using-java
		
		// Translate HTML characters like &amp; to & etc.
		String cleanedUpLine = StringEscapeUtils.unescapeHtml(inputLine);
		
		// Remove unnecessary HTML tags like <li> etc.
		cleanedUpLine = cleanedUpLine.replaceAll(HTML_TAG_REGEX, " ");
		
		// Remove any other special characters which aren't inside the printable ASCII range
		cleanedUpLine = cleanedUpLine.replaceAll(NONASCII_CHAR_REGEX, "");
	
		return cleanedUpLine.trim();
	}
	
	// Removes any empty or null string from the list of tokens.
	public static List<String> removeNullOrEmptyStrings(List<String> tokens)
	{
		boolean isNullOrEmptyStringPresent = false;
		for(String token : tokens) {
			if(StringUtils.isBlank(token) || token.toLowerCase().equals(Constants.NULL_AS_STRING)) {
				isNullOrEmptyStringPresent = true;
				break;
			}
		}
		
		// If no faulty token present, just return the original set of tokens.
		if(!isNullOrEmptyStringPresent) {
			return tokens;
		}
		
		List<String> cleanedSourceTokens = Lists.newArrayList();
		for(String token : tokens) {
			if(StringUtils.isBlank(token) || token.toLowerCase().equals(Constants.NULL_AS_STRING)) {
				continue;
			}
			cleanedSourceTokens.add(token);
		}
		
		return cleanedSourceTokens;
	}

	public static Set<String> removeNullOrEmptyStrings(Set<String> tokens)
	{
		if(tokens == null) {
			return null;
		}
		
		return Sets.newHashSet(Lists.newArrayList(tokens));
	}
}
