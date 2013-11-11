package com.walmart.productgenome.pairComparison.config;

import java.io.File;

/**
 * Lists the configuration parameters for generating ruleset. This can include parameters like
 * location of source rule file, whether an expanded version of rule file also needs to be 
 * generated etc.
 * 
 * @author sprasa4
 *
 */
public class ItemMatchRulesetConfig {
	private File ruleFile;
	private String expandedRuleFileLoc;
	
	public ItemMatchRulesetConfig()
	{
		
	}
	
	public ItemMatchRulesetConfig(File ruleFile, String expandedRuleFileLoc)
	{
		this.ruleFile = ruleFile;
		this.expandedRuleFileLoc = expandedRuleFileLoc;
	}
	
	public File getRuleFile() {
		return ruleFile;
	}
	
	public void setRuleFileLoc(File ruleFile) {
		this.ruleFile = ruleFile;
	}
	
	public String getExpandedRuleFileLoc() {
		return expandedRuleFileLoc;
	}
	
	public void setExpandedRuleFileLoc(String expandedRuleFileLoc) {
		this.expandedRuleFileLoc = expandedRuleFileLoc;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ItemMatchRulesetConfig [ruleFileLoc=")
				.append(ruleFile).append(", expandedRuleFileLoc=")
				.append(expandedRuleFileLoc).append("]");
		return builder.toString();
	}
}
