package com.walmart.productgenome.pairComparison.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.walmart.productgenome.pairComparison.audit.ItemPairAuditDataCollector;
import com.walmart.productgenome.pairComparison.config.ItemMatchRulesetConfig;
import com.walmart.productgenome.pairComparison.model.MatchStatus;
import com.walmart.productgenome.pairComparison.model.rule.ItemMatchRuleset;
import com.walmart.productgenome.pairComparison.model.rule.MatchEntity;
import com.walmart.productgenome.pairComparison.model.rule.MatchEntityPair;
import com.walmart.productgenome.pairComparison.parser.ParseException;
import com.walmart.productgenome.pairComparison.parser.ProductMatchingGrammar;

/**
 * Service used for loading rules from the custom Domain Specific Language (DSL) for product matching.
 * @author sprasa4
 *
 * TODO : Shouldn't this be a service class with no member variables and all behaviour captured via
 * parameters ? m_ruleset should be a method parameter and not defined as a member variable.
 */
public class ItemPairDataMatcher {
	private ItemMatchRuleset m_ruleset = null;
	
	/**
	 * Generates ruleset from the rule language file.
	 * @param ruleFile		File object pointing to the location of rule file
	 * @throws FileNotFoundException
	 * @throws ParseException
	 */
	public ItemPairDataMatcher(File ruleFile) throws FileNotFoundException, ParseException
	{
		m_ruleset = getItemMatchRuleset(ruleFile);
	}
	
	/**
	 * Generates ruleset by reading the rule file. This rule file is present in the classpath and
	 * thus can be directly read without specifying the absolute path of the file.
	 * 
	 * @param ruleFileName	Name of the rule file
	 * @throws ParseException 
	 */
	public ItemPairDataMatcher(String ruleFileName) throws ParseException
	{
		InputStream in = ItemPairDataMatcher.class.getClassLoader().getResourceAsStream(ruleFileName);
		m_ruleset = getItemMatchRuleset(new InputStreamReader(in));		
	}
	
	public ItemPairDataMatcher(ItemMatchRulesetConfig config) throws FileNotFoundException, ParseException
	{
		m_ruleset = getItemMatchRuleset(config);
	}
	
	public ItemPairDataMatcher(ItemMatchRuleset ruleset)
	{
		m_ruleset = ruleset;
	}
	
	public ItemMatchRuleset getRuleset() {
		return m_ruleset;
	}

	public void setRuleset(ItemMatchRuleset m_ruleset) {
		this.m_ruleset = m_ruleset;
	}

	/**
	 * Returns an enriched ruleset collection containing all the matching rules specified in the
	 * specified rule file.
	 * 
	 * @param ruleFileLoc Path of the rule file
	 * @return ItemMatchRuleset
	 * @throws FileNotFoundException 
	 * @throws ParseException 
	 */
	private ItemMatchRuleset getItemMatchRuleset(File ruleFile) throws FileNotFoundException, ParseException
	{
		return getItemMatchRuleset(new ItemMatchRulesetConfig(ruleFile, null));
	}
	
	/**
	 * 
	 * @param config Configuration object for this ruleset containing rule file path location etc.
	 * @return
	 * @throws FileNotFoundException
	 * @throws ParseException 
	 */
	private ItemMatchRuleset getItemMatchRuleset(ItemMatchRulesetConfig config) throws FileNotFoundException, ParseException
	{
        BufferedReader sr = new java.io.BufferedReader(new java.io.FileReader(config.getRuleFile()));
        ProductMatchingGrammar parser = new ProductMatchingGrammar(sr);
        ItemMatchRuleset productMatchingRuleset = parser.getProductMatchingRuleset();

        return productMatchingRuleset;
	}
	
	/**
	 * Reads a rule file and generates ruleset for matching.
	 *  
	 * @param in	InputStreamReader to the rule file
	 * @return	Ruleset corresponding to the rule file.
	 * 
	 * @throws ParseException
	 */
	private ItemMatchRuleset getItemMatchRuleset(InputStreamReader in) throws ParseException
	{
        BufferedReader sr = new java.io.BufferedReader(in);
        ProductMatchingGrammar parser = new ProductMatchingGrammar(sr);
        ItemMatchRuleset productMatchingRuleset = parser.getProductMatchingRuleset();

        return productMatchingRuleset;		
	}
	
	/**
	 * Matches sources item and target item and returns a boolean to indicate the match status.
	 * 
	 * @param sourceItem	Source product item
	 * @param targetItem	Target product item
	 * @param collector		Collects fine-grained audit information about the matching between this
	 * 						source and target item.
	 * @return	boolean		Whether the source item matched with the target item or not ?
	 */
	public boolean matchItemPair(MatchEntity sourceItem, MatchEntity targetItem, ItemPairAuditDataCollector collector)
	{
		MatchEntityPair itemPair = new MatchEntityPair(sourceItem, targetItem);
		return matchItemPair(itemPair, collector);
	}

	/**
	 * Matches sources item and target item and returns a boolean to indicate the match status.
	 *  
	 *  Note : This method is just provided for backward compatibility. It is highly encouraged to
	 *  use the newer API where you can also pass a collector object to capture audit information.
	 *  In case you are not interested in audit information, use this API where a dummy audit collector
	 *  object is passed but you cannot access that audit information since it is local variable.
	 *  
	 * @param sourceItem	Source item to be matched.
	 * @param targetItem	Target item to be matched against.
	 * @return	Boolean to indicate if the two items matched or not.
	 */
	public boolean matchItemPair(MatchEntity sourceItem, MatchEntity targetItem)
	{
		MatchEntityPair itemPair = new MatchEntityPair(sourceItem, targetItem);
		return matchItemPair(itemPair, new ItemPairAuditDataCollector(itemPair));
	}
	
	/**
	 * Note : This new matching API is similar to old one except the following :
	 * a) Explicitly using item pair, instead of source and target items as input.
	 * b) Pass a collector object for getting audit information.
	 * c) Use a MatchEntityPair object instead of ItemPair defined in AbstractProductDiscovery
	 *    codebase.
	 *    
	 * @param itemPair	An itempair that has to be matched.
	 * @param collector Collects audit information about the matching for this itempair.
	 * @return	A boolean indicating whether the item pair matched correctly.
	 */
	public boolean matchItemPair(MatchEntityPair itemPair, ItemPairAuditDataCollector collector)
	{
		if (itemPair.getSourceItem() == null) throw new IllegalArgumentException("source item cannot be null");
		if (itemPair.getTargetItem() == null) throw new IllegalArgumentException("target item cannot be null");

		if(collector == null) {
			collector = new ItemPairAuditDataCollector(itemPair);
		}
		
		boolean isMatchSuccess = m_ruleset.executeRuleset(itemPair.getSourceItem(), itemPair.getTargetItem(), collector);
		collector.setStatus(isMatchSuccess ? MatchStatus.SUCCESS : MatchStatus.FAILURE);
		
		return isMatchSuccess;
	}
	
	/**
	 * Matches sources item and target item and returns a boolean to indicate the match status.
	 *  
	 *  Note : This method is just provided for backward compatibility. It is highly encouraged to
	 *  use the newer API where you can also pass a collector object to capture audit information.
	 *  In case you are not interested in audit information, use this API where a dummy audit collector
	 *  object is passed but you cannot access that audit information since it is local variable.
	 *  
	 * @param itemPair	ItemPair to be matched.
	 * @return	Boolean to indicate if the two items matched or not.
	 */
	public boolean matchItemPair(MatchEntityPair itemPair)
	{
		return matchItemPair(itemPair, new ItemPairAuditDataCollector(itemPair));
	}
	
}
