package com.walmart.productgenome.pairComparison.utils;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.walmart.productgenome.pairComparison.utils.comparers.IComparer;

/**
 * Utilty class that contains various methods for facilitating containment of source item in
 * target item via complex string matching techniques and cases like concatenated matches,
 * abbreviation matches, roman numeral matches etc.
 * 
 * @author sprasa4
 *
 */
public class MatchUtils {
	
	// Static list of roman numerals that can occur in item attributes
	private static Map<String, Integer> romanNumeralMap = Maps.newHashMap();
	static {
		/**
		 * TODO : Check with Aswath.
		 * Some roman numerals are very tricky to catch. For example, roman numerals like "x" can
		 * may well represent x with different meanings. For example, Sony BNR25AP6 - 50 x BD-R - 25 GB 6x
		 * Here x means the speed and does not refer to roman numeral. If we treat it as roman numeral
		 * this can lead to incorrect matching. So, to be on the safer side we will only consider 
		 * the roman numerals which have greater than 1 character.
		 */
		//romanNumeralMap.put("I", 	1);
		romanNumeralMap.put("II", 	2);
		romanNumeralMap.put("III", 	3);
		romanNumeralMap.put("IV", 	4);
		//romanNumeralMap.put("V", 	5);
		romanNumeralMap.put("VI", 	6);
		romanNumeralMap.put("VII", 	7);
		romanNumeralMap.put("VIII", 8);
		romanNumeralMap.put("IX", 	9);
		//romanNumeralMap.put("X", 	10);
		romanNumeralMap.put("XI", 	11);
		romanNumeralMap.put("XII", 	12);
		romanNumeralMap.put("XIII", 13);
		romanNumeralMap.put("XIV", 	14);
		romanNumeralMap.put("XV", 	15);
		romanNumeralMap.put("XVI", 	16);
		romanNumeralMap.put("XVII", 17);
		romanNumeralMap.put("XVIII",18);
		romanNumeralMap.put("IX", 	19);
		romanNumeralMap.put("XX", 	20);		
	}
	
	// Static list of numbers and their english representation
	private static Map<Integer, String> numberMap = Maps.newHashMap();
	static {
		numberMap.put(1, "one");
		numberMap.put(2, "two");
		numberMap.put(3, "three");
		numberMap.put(4, "four");
		numberMap.put(5, "five");
		numberMap.put(6, "six");
		numberMap.put(7, "seven");
		numberMap.put(8, "eight");
		numberMap.put(9, "nine");
		numberMap.put(10, "ten");
		numberMap.put(11, "eleven");
		numberMap.put(12, "twelve");
		numberMap.put(13, "thirteen");
		numberMap.put(14, "fourteen");
		numberMap.put(15, "fifteen");
		numberMap.put(16, "sixteen");
		numberMap.put(17, "seventeen");
		numberMap.put(18, "eighteen");
		numberMap.put(19, "nineteen");
		numberMap.put(20, "twenty");
	}
	
	/**
	 * Checks for an abbreviation match between a source token and a list of target tokens.
	 * @param potentialAbbreviation eg: "POTC"
	 * @param potentialExpandedTokenList eg: "Pirates of the Carrribean"
	 * @param potentialExpandedTokenListStartIndex eg: 0 
	 */
	public static int checkAbbreviationMatch(final String potentialAbbreviation, 
											  final List<String> potentialExpandedTokenList, 
											  final int potentialExpandedTokenListStartIndex) 
	{
		// Skip abbreviation check if the token in question is :
		// 1) pure integer
		// 2) only one character
		// 3) empty string
		
		if(potentialAbbreviation.length() <= 1 ||
		   org.apache.commons.lang.StringUtils.isNumeric(potentialAbbreviation))
		{
			return 0;
		}
		
		StringBuilder abbreviationBuilder = new StringBuilder();
		int currIndex = potentialExpandedTokenListStartIndex;
		abbreviationBuilder.append(potentialExpandedTokenList.get(currIndex).charAt(0));
		
		currIndex++;
		while (abbreviationBuilder.toString().length() < potentialAbbreviation.length() && currIndex < potentialExpandedTokenList.size()) {
			abbreviationBuilder.append(potentialExpandedTokenList.get(currIndex).charAt(0));
			currIndex++;
		}
		
		//at this point, either we have run out of potential expansion tokens or the abbreviation builder has the 
		//same number of characters as the potential abbreviation
		if (potentialAbbreviation.equals(abbreviationBuilder.toString())) {
			//an abbreviation has been found, return the number of tokens comprised
			//in the abbreviation
			return currIndex - potentialExpandedTokenListStartIndex;
		}
		
		//no abbreviation found
		return 0;
	}

	/**
	 * Checks for concatenated matches between a source token and multiple target tokens.
	 * eg: targetToken: "SD400IS"
	 * eg: sourceToken: "SD400" "IS" ("IS" is the next token after "SD400")
	 * 
	 * @param longToken
	 * @param shortTokenList
	 * @param shortTokenListStartIndex
	 * @return
	 */
	public static int findConcatenatedMatches(final String longToken, final List<String> shortTokenList, final int shortTokenListStartIndex) {
		StringBuilder shortTokenBuilder = new StringBuilder();
		
		int currShortTokenListStartIndex = shortTokenListStartIndex;
		shortTokenBuilder.append(shortTokenList.get(currShortTokenListStartIndex));
		
		currShortTokenListStartIndex++;
		while (shortTokenBuilder.toString().length() < longToken.length() && currShortTokenListStartIndex < shortTokenList.size()) {
			//continue appending tokens
			shortTokenBuilder.append(shortTokenList.get(currShortTokenListStartIndex));
			currShortTokenListStartIndex++;
		}
		
		if (longToken.equals(shortTokenBuilder.toString()) == true) {
			//we have a match, return the number of short tokens matched
			return currShortTokenListStartIndex - shortTokenListStartIndex;
		}
		
		//if we have reached this point, that means no concatenated matches have been found
		return 0;
	}

	
	/**
	 * Returns the maximum similarity score and the best comparer between two strings based on 
	 * the provided list of comparers.
	 * 
	 * A map has been chosen as a return type because we need to return multiple results - best score
	 * and best comparer,  from the same function call. 
	 */
	public static Map<IComparer, Double> compareStrings(String str1, String str2, List<IComparer> comparers) {
		double maxScore = 0.0;
		Map<IComparer, Double> bestComparerResultMap = Maps.newHashMap();
		IComparer bestComparer = null;
		for (IComparer comparer : comparers) {
			double score = comparer.compare(str1, str2);
			
			// Optimization : If max score has been attained, no need to compare further
			// because this is the maximum possible attainable score.
			if(Double.compare(score, 1.0) == 0) {
				maxScore = 1.0;
				bestComparer = comparer;
				break;
			}
			// Else search for the max score
			else if (score > maxScore) {
				maxScore = score;
				bestComparer = comparer;
			}
		}
		
		bestComparerResultMap.put(bestComparer, maxScore);		
		return bestComparerResultMap;
	}

	/**
	 * A wrapper method that invokes a series of pre-processing on the item attribute value tokens.
	 * Examples of some of the processing include roman numeral conversion, removing special characters
	 * from the token. Once this preprocessing is done, the matching process can safely undergo
	 * the containment calculation.
	 * 
	 * @param itemAttributeTokens
	 * @return
	 */
	public static List<String> preprocessItemAttributeTokens(List<String> itemAttributeTokens)
	{
		List<String> processedTokens = itemAttributeTokens;
		
		// convert roman numerals to numeric representation
		processedTokens = processRomanNumeralTokens(processedTokens);
		
		// convert numeric tokens to english representation
		processedTokens = processSmallIntegerTokens(processedTokens);
		
		// remove tokens of length 1 as they don't impact the matching
		processedTokens = removeSingleCharTokens(processedTokens);
		
		return processedTokens;
	}
	
	/**
	 * Removes single character tokens as they don't impact the matching process
	 * @param inputTokens
	 * @return
	 */
	public static List<String> removeSingleCharTokens(List<String> inputTokens)
	{
		boolean isSingleCharTokenPresent = false;
		for(String token : inputTokens) {
			if(token.length() == 1) {
				isSingleCharTokenPresent = true;
				break;
			}
		}
		
		if(!isSingleCharTokenPresent) {
			return inputTokens;
		}
		
		List<String> processedTokens = Lists.newArrayList();
		for(String token : inputTokens) {
			if(token.length() == 1) {
				continue;
			}
			processedTokens.add(token);
		}
		
		return processedTokens;
	}
	
	/**
	 * TODO : This is a very simple implementation of roman number converter, as product names
	 * don't usually have large number in the title/description. Shall I implement a more generic
	 * roman number converter ? It is easy but slightly overkill.
	 * 
	 * Pre-processes the input string and converts any roman numerals to the corresponding numeric
	 * representation. This is required because some product titles contain roman numerals and have
	 * to be converted to numeric form for one-to-one item comparison.
	 * 
	 * Note : Roman numerals have been statically put in upper case. but the tokens that we get
	 * from tokenizer are in lower case, so make sure to upper case the token to look for its
	 * presence in the roman numeral map.
	 * @param input
	 * @return
	 */
	public static List<String> processRomanNumeralTokens(List<String> inputTokens)
	{
		boolean isRomanNumeralPresent = false;
		for(String token : inputTokens) {
			if(romanNumeralMap.containsKey(token.toUpperCase())) {
				isRomanNumeralPresent = true;
				break;
			}
		}
		
		// For most of the cases there wouldn't be any roman numerals in the string, so just
		// return with minimal processing overhead.
		if(!isRomanNumeralPresent) {
			return inputTokens;
		}
		
		List<String> romanNumeralProcessedTokens = Lists.newArrayList();
		for(String token : inputTokens) {
			if(romanNumeralMap.containsKey(token.toUpperCase())) {
				romanNumeralProcessedTokens.add(Integer.toString(romanNumeralMap.get(token.toUpperCase())));
			}
			else {
				romanNumeralProcessedTokens.add(token);	
			}
		}
		
		return romanNumeralProcessedTokens;
	}
	
	/**
	 * Converts small numeric token into its corresponding english representation. For example,
	 * 5 MW bulb ==> Five MW bulb. This is important because often across product descriptions,
	 * there are different representations of numbers.
	 * 
	 * To keep things simple, only most commonly occurring integer tokens are being transformed. 
	 * It is very uncommon for product names to have long integer tokens.
	 * 
	 * @param inputTokens
	 * @return
	 */
	public static List<String> processSmallIntegerTokens(List<String> inputTokens)
	{
		boolean isIntegerValuePresent = false;
		for(String token : inputTokens) {
			if(NumberUtils.isPureIntegerOrEquivalent(token)) {
				isIntegerValuePresent = true;
				break;
			}
		}
		
		// The cost of creating strings is much higher than doing a sanity check on the string
		// tokens to see if whether they have numeric tokens in the first place.
		if(!isIntegerValuePresent) {
			return inputTokens;
		}
		
		List<String> processedTokens = Lists.newArrayList();
		for(String token : inputTokens) {
			if(NumberUtils.isPureIntegerOrEquivalent(token)) {
				int number = NumberUtils.parsePureIntegerOrEquivalent(token);
				if(numberMap.containsKey(number)) {
					processedTokens.add(numberMap.get(number));
				}
				else {
					// This basically means that it was not able to find a number in our static
					// numeric list. LOG an error and insert the numeric token back to the list.
					//System.err.println("Failed to map token " + token + " to a small integer.");
					processedTokens.add(token);
				}
			}
			else {
				processedTokens.add(token);
			}
		}
		
		return processedTokens;
	}

}
