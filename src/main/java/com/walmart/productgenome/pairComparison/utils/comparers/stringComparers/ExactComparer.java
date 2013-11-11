package com.walmart.productgenome.pairComparison.utils.comparers.stringComparers;
import com.walmart.productgenome.pairComparison.utils.comparers.IComparer;



public class ExactComparer implements IComparer  {

	/*
	 * (non-Javadoc)
	 * @see com.walmart.productgenome.pairComparison.stringComparers.IStringComparer#compare(java.lang.String, java.lang.String)
	 */
	
	public double compare(final String str1, final String str2) {
		if (str1 == null) throw new IllegalArgumentException("str1 cannot be null");
		if (str2 == null) throw new IllegalArgumentException("str2 cannot be null");
		
		if (str1.toLowerCase().equals(str2.toLowerCase()) == true)
			return 1.0;
		else
			return 0.0;
	}

}
