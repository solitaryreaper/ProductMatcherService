package com.walmart.productgenome.pairComparison.utils;

/**
 * Utility class for basic string processing
 * 
 * @author sprasa4
 *
 */
public class StringUtils {
	
	/**
	 * Trims the last delimiter from the string
	 * 
	 * @param token
	 * @param delimiter
	 * @return
	 */
	public static String trimLastDelimiter(String token, String delimiter)
	{
		if(!token.contains(delimiter)) {
			return token;
		}
		
		int delimiterLastIndex = token.lastIndexOf(delimiter);
		return token.substring(0, delimiterLastIndex);
	}
}
