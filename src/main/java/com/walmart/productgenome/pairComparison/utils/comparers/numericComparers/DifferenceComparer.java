package com.walmart.productgenome.pairComparison.utils.comparers.numericComparers;

import com.walmart.productgenome.pairComparison.utils.NumberUtils;
import com.walmart.productgenome.pairComparison.utils.comparers.IComparer;

/**
 * A fuzzy comparer for determining the similarity of two integers. In this case the integers might
 * not be exactly equal to each other but are approximately euq
 * 
 * @author sprasa4
 *
 */
public class DifferenceComparer implements IComparer{

	/**
	 * Under exact match semantics, tow integers are considered same if they are both non-null
	 * and have the exact same value.
	 */
	public double compare(String obj1, String obj2) {
		if(obj1 == null || obj2 == null || obj1.isEmpty() || obj2.isEmpty()) {
			return 0;
		}
		
		// This comparer is only for double or integer numbers.
		if(!NumberUtils.isDouble(obj1) || !NumberUtils.isDouble(obj2)) {
			return 0;
		}
		
		/**
		 * Sometimes item attributes might have integer values which are close to each other but
		 * not exactly same. They represent the same items with same UPCs but slightly different
		 * attribute values which can be attributes to human error.
		 * 
		 * Let delta = abs(value1 - value2)
		 * if delta == 0 ==> score = 1.0
		 *    delta == 1 ==> score = 0.9
		 *    delta == 2 ==> score = 0.8
		 *    
		 *    May lead to false positive, so be conservative with the max permissible delta value.
		 *    delta > 2  ==> score = 0.0
		 *   
		 */
		double score = 0.0;
		double diff = Math.abs(Double.parseDouble(obj1) - Double.parseDouble(obj2));
		if(diff == 0.0) {
			score = 1.0;
		}
		else if(diff < 1.0) {
			score = 0.9;
		}
		else if(diff < 2.0) {
			score = 0.8;
		}
		
		return score;
	}

}
