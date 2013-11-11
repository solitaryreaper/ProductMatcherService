package com.walmart.productgenome.pairComparison.utils.comparers.numericComparers;

import java.util.List;

import com.google.common.collect.Lists;
import com.walmart.productgenome.pairComparison.utils.comparers.IComparer;

/**
 * Comparer to match an int with its corresponding english representation. Since finding words for
 * integers is mostly limited to only one word, we will target only such tokens in this comparer.
 * 
 * @author sprasa4
 *
 */
public class IntToEnglishWordComparer implements IComparer {

	// Static list of numbers and their english representation
	/**
	 * Some roman numerals are very tricky to catch. For example, roman numerals like "x" can
	 * may well represent x with different meanings. For example, Sony BNR25AP6 - 50 x BD-R - 25 GB 6x
	 * Here x means the speed and does not refer to roman numeral. If we treat it as roman numeral
	 * this can lead to incorrect matching. So, to be on the safer side we will only consider 
	 * the roman numerals which have greater than 1 character.
	 */
	private static List<List<String>> numberDisplayVariationsMap 	= Lists.newArrayList();
	static {
		numberDisplayVariationsMap.add(Lists.newArrayList("1", "1.0", "one", "first"));
		numberDisplayVariationsMap.add(Lists.newArrayList("2", "2.0", "two", "second", "ii"));
		numberDisplayVariationsMap.add(Lists.newArrayList("3", "3.0", "three", "third", "iii"));
		numberDisplayVariationsMap.add(Lists.newArrayList("4", "4.0", "four", "fourth", "iv"));
		numberDisplayVariationsMap.add(Lists.newArrayList("5", "5.0", "five", "fifth"));
		numberDisplayVariationsMap.add(Lists.newArrayList("6", "6.0", "six", "sixth", "vi"));
		numberDisplayVariationsMap.add(Lists.newArrayList("7", "7.0", "seven", "seventh", "vii"));
		numberDisplayVariationsMap.add(Lists.newArrayList("8", "8.0", "eight", "eighth", "viii"));
		numberDisplayVariationsMap.add(Lists.newArrayList("9", "9.0", "nine", "ninth", "ix"));
		numberDisplayVariationsMap.add(Lists.newArrayList("10", "10.0", "ten", "tenth"));
		numberDisplayVariationsMap.add(Lists.newArrayList("11", "11.0", "eleven", "eleventh", "xi"));
		numberDisplayVariationsMap.add(Lists.newArrayList("12", "12.0", "twelve", "twelfth", "xii"));
		numberDisplayVariationsMap.add(Lists.newArrayList("13", "13.0", "thirteen", "thirteenth", "xiii"));
		numberDisplayVariationsMap.add(Lists.newArrayList("14", "14.0", "fourteen", "fourteenth", "xiv"));
		numberDisplayVariationsMap.add(Lists.newArrayList("15", "15.0", "fifteen", "fifteenth", "xv"));
		numberDisplayVariationsMap.add(Lists.newArrayList("16", "16.0", "sixteen", "sixteenth", "xvi"));
		numberDisplayVariationsMap.add(Lists.newArrayList("17", "17.0", "seventeen", "seventeenth", "xvii"));
		numberDisplayVariationsMap.add(Lists.newArrayList("18", "18.0", "eighteen", "eighteenth", "xviii"));
		numberDisplayVariationsMap.add(Lists.newArrayList("19", "19.0", "nineteen", "nineteenth", "xix"));
		numberDisplayVariationsMap.add(Lists.newArrayList("20", "20.0", "twenty", "twentieth", "xx"));
		numberDisplayVariationsMap.add(Lists.newArrayList("30", "thirty"));
		numberDisplayVariationsMap.add(Lists.newArrayList("40", "forty"));
		numberDisplayVariationsMap.add(Lists.newArrayList("50", "fifty"));
		numberDisplayVariationsMap.add(Lists.newArrayList("60", "sixty"));
		numberDisplayVariationsMap.add(Lists.newArrayList("70", "seventy"));
		numberDisplayVariationsMap.add(Lists.newArrayList("80", "eighty"));
		numberDisplayVariationsMap.add(Lists.newArrayList("90", "ninety"));		
	}

	public double compare(String str1, String str2) {
		if(str1 == null || str2 == null || str1.isEmpty() || str2.isEmpty()) {
			return 0.0;
		}
		
		double score = 0.0;
		for(List<String> numberVariations : numberDisplayVariationsMap) {
			if(numberVariations.contains(str1.toLowerCase()) && numberVariations.contains(str2.toLowerCase())) {
				score = 1.0;
				break;
			}
		}
		
		return score;
	}
}
