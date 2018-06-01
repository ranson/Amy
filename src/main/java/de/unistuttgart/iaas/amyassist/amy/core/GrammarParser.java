/*
 * Amy Assist
 *
 * Personal Assistance System
 *
 * @author Tim Neumann, Leon Kiefer, Benno Krauss, Christian Braeuner, Felix Burk, Florian Bauer, Kai Menzel, Lars Buttgereit, Muhammed Kaya, Patrick Gebhardt, Patrick Singer, Tobias Siemonsen
 *
 */
package de.unistuttgart.iaas.amyassist.amy.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * generates a valid *.gram file, keywords get replaced by pre defined rules
 * JSGF specification: https://www.w3.org/TR/jsgf/
 *
 * @author Felix Burk
 */
public class GrammarParser {
	private String name;
	private String wakeup;
	private String sleep;
	private String shutdown;

	private Map<String, String> mapKeywordRule = new HashMap<>();

	private List<String> addedRules = new ArrayList<>();

	/**
	 * initializes the parser
	 *
	 * possible expansions: more custom keywords, weights may be supported,
	 * <NULL> and <VOID> support, Unary Operators (kleene star, plus operator
	 * and tags)
	 *
	 * @param name
	 *            The name of the grammar
	 *
	 * @param wakeup
	 *            The wakeup call in this grammar.
	 * @param sleep
	 *            The sleep call in this grammar.
	 * @param shutdown
	 *            The shutdown call in this grammar.
	 */
	public GrammarParser(String name, String wakeup, String sleep, String shutdown) {
		this.wakeup = wakeup;
		this.sleep = sleep;
		this.shutdown = shutdown;
		this.name = name;

		// TODO find another way to store pre defined rules
		this.mapKeywordRule.put("#", "<digit>");

	}

	/**
	 * @return The grammar generated
	 */
	public String getGrammar() {
		// header
		String grammar = "#JSGF V1.0;\n" + "\n" + "/**\n" + " * JSGF Grammar \n" + " */\n" + "\n";

		grammar += "grammar " + this.name + ";\n";
		grammar += "public <wakeup> = ( " + this.wakeup + " );\n";
		grammar += "public <sleep> = ( " + this.sleep + " );\n";
		grammar += "public <shutdown> = ( " + this.shutdown + " );\n";

		grammar += "\n#pre defined rules \n";

		// pre defined rules
		// TODO add them to external file via import rule in JSGF
		grammar += "<digit> = (one | two | three | four | five | six | seven |"
				+ "nine | ten | eleven | twelve | thirteen | fourteen | fifteen | "
				+ "sixteen | seventeen | eighteen | nineteen | twenty | thirty | forty | "
				+ "fifty | sixty  | seventy | eighty | ninety | hundred | thousand |" + "million | and )+ \n";

		grammar += "\n#custom rules \n";
		for (String s : this.addedRules) {
			grammar += s;
		}

		return grammar;
	}

	/**
	 * Adds a rule to the grammar
	 *
	 * @param ruleName
	 *            The name of the rule
	 * @param keyword
	 *            The keyword
	 */
	void addRule(String ruleName, String keyword, String grammar) {
		this.addedRules.add("public " + "<" + ruleName + ">" + " = " + this.parseKeyword(keyword) + " "
				+ this.parseKeyword(grammar) + "; \n");
	}

	/**
	 * replace keywords with corresponding pre defined rule
	 *
	 * @param keyword
	 *            The keyword
	 * @return the corresponding rule
	 */
	private String parseKeyword(String keyword) {
		String parsedKeyword = "";

		for (String s : this.mapKeywordRule.keySet()) {
			parsedKeyword = keyword.replace(s, this.mapKeywordRule.get(s));
		}
		return parsedKeyword;
	}

}
