package com.walmart.productgenome.pairComparison.utils.comparers.numericComparers;

import com.walmart.productgenome.pairComparison.utils.NumberUtils;
import com.walmart.productgenome.pairComparison.utils.comparers.IComparer;

/**
 * An exact comparer for determining the similarity of two integers.
 * 
 * @author sprasa4
 *
 */
public class ExactNumericComparer implements IComparer{

	/**
	 * Under exact match semantics, tow integers are considered same if they are both non-null
	 * and have the exact same value.
	 */
	public double compare(String obj1, String obj2) {
		if(obj1 == null || obj2 == null || obj1.isEmpty() || obj2.isEmpty()) {
			return 0;
		}
		
		// This comparer is only for double or integer numbers.
		if(NumberUtils.isDouble(obj1) && NumberUtils.isDouble(obj2)) {
			double obj1Double = Double.parseDouble(obj1);
			double obj2Double = Double.parseDouble(obj2);
			return Double.compare(obj1Double, obj2Double) == 0 ? 1 : 0;
		}

		return 0.0;
	}

}
