package com.walmart.productgenome.pairComparison.utils;

import java.text.DecimalFormat;

/**
 * Utility method to perform basic number based operations.
 * 
 * @author sprasa4
 * 
 */
public class NumberUtils {

	private static DecimalFormat df = new DecimalFormat("####0.00");

	public static String formatDouble(double number) {
		return df.format(number);
	}

	/**
	 * Determines if the string is a valid integer or not. Consider values like 5.0 also as pure
	 * integer because the decimal part is immaterial here.
	 */
	public static boolean isPureIntegerOrEquivalent(String token) {
		return token.matches("[0-9]+[.]?[0]*");
	}

	/**
	 * Determines if the string is a valid double number or not. 
	 */
	public static boolean isDouble(String token) {
		return token.matches("[0-9]+[.]?[0-9]*");
	}

	/**
	 * Parses pure int as string or int with decimal trailing zeroes as decimal
	 * part which are equivalent to pure integers.
	 * 
	 * @param token
	 * @return
	 */
	public static int parsePureIntegerOrEquivalent(String token) {
		if (token.contains(".")) {
			token = token.substring(0, token.lastIndexOf("."));
		}

		int result = -1;
		try {
			result = Integer.parseInt(token);
		}
		catch(Exception e) {
			//System.err.println("Failed to parse integer " + token);
		}

		return result;
	}

}
