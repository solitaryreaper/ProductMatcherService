package com.walmart.productgenome.pairComparison.utils.evaluators;

import com.walmart.productgenome.pairComparison.model.rule.AttributeMatchClause;
import com.walmart.productgenome.pairComparison.model.rule.MatchEntity;

public interface IAttributeMatchClauseEvaluator {

	public boolean evaluate(final MatchEntity sourceItem, final MatchEntity targetItem, final AttributeMatchClause attributeMatchClause);
}
