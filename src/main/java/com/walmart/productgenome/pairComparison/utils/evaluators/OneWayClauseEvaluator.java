package com.walmart.productgenome.pairComparison.utils.evaluators;

import com.walmart.productgenome.pairComparison.model.rule.AttributeMatchClause;
import com.walmart.productgenome.pairComparison.model.rule.MatchEntity;

public class OneWayClauseEvaluator implements IAttributeMatchClauseEvaluator {

	/*
	 * (non-Javadoc)
	 * @see com.walmart.productgenome.pairComparison.evaluate.IAttributeMatchClauseEvaluator#evaluate(com.walmart.productgenome.common.dataTypes.Item, com.walmart.productgenome.common.dataTypes.Item, com.walmart.productgenome.pairComparison.AttributeMatchClause)
	 */
	
	public boolean evaluate(final MatchEntity sourceItem, final MatchEntity targetItem, final AttributeMatchClause attributeMatchClause) {
		//return attributeMatchClause.evaluateClause(sourceItem, targetItem);
		return false;
	}

}
