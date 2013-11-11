package com.walmart.productgenome.pairComparison.utils.comparers.numericComparers;

import com.walmart.productgenome.pairComparison.utils.NumberUtils;
import com.walmart.productgenome.pairComparison.utils.comparers.IComparer;

/**
 * A fuzzy comparer for determining the similarity of two double quantities. In this case the 
 * integer quantities might not be exactly equal to each other but are approximately equal. 
 * 
 * @author sprasa4
 *
 */
public class RatioComparer implements IComparer{

	public double compare(String obj1, String obj2) {
		if(obj1 == null || obj2 == null) {
			return 0;
		}
		
		// This comparer is only for double or integer numbers.
		if(!NumberUtils.isDouble(obj1) || NumberUtils.isDouble(obj2)) {
			return 0;
		}

		/**
		 * Sometimes item attributes might have integer values which are close to each other but
		 * not exactly same. They represent the same items with same UPCs but slightly different
		 * attribute values which can be attributed to human error.
		 * 
		 * Let ratio = value1/value2, assuming value1 <= value2
		 * if ratio == 1.0 			==> score = 1.0
		 *    ratio < 1.0 && >= 0.9 ==> score = 0.9
		 *    ratio < 0.9 && >= 0.8 ==> score = 0.8
		 *    
		 *    May lead to false positive, so be conservative with the min permissible ratio value.
		 *    ratio < 0.8  ==> score = 0.0
		 *   
		 */
		double score = 0.0;
		
		double obj1Double = Double.parseDouble(obj1);
		double obj2Double = Double.parseDouble(obj2);
		double ratio = (obj1Double > obj2Double) ? obj2Double/obj1Double : obj1Double/obj2Double;
		if(Double.compare(score, 1.0) == 0) {
			score = 1.0;
		}
		else if(ratio < 1.0 && ratio >= 0.9) {
			score = 0.9;
		}
		else if(ratio < 0.9 && ratio <= 0.8) {
			score = 0.8;
		}
		
		return score;
	}

}
