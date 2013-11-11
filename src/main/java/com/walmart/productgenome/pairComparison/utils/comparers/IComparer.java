package com.walmart.productgenome.pairComparison.utils.comparers;

/**
 * A generic interface for comparing any two objects and returning a similarity score to indicate
 * their similarity to each other. 
 * @author sprasa4
 *
 */
public interface IComparer {

	/**
	 * Returns a double from 0.0 to 1.0, indicating the level of similarity between two objects
	 * @param obj1 	the first string to be compared [NOT NULL]
	 * @param obj2	the first string to be compared [NOT NULL]
	 * @return		a double from 0.0 to 1.0
	 */
	public double compare(String obj1, String obj2);
}
