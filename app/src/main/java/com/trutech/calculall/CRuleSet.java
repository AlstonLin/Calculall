package com.trutech.calculall;
import java.util.ArrayList;

public class CRuleSet {

    public static int ADD = 1, SUBTRACT = 2, DOT = 3, CROSS = 4;

	ArrayList<CRule> CRules = new ArrayList<CRule>();
	ArrayList<Token> newExpression = new ArrayList<Token>();

	public CRuleSet () {
		CRules.add(new CRule ("[N,N]d[N,N]"));
                //CRules.add(new CRule ("", "", true));
	}

	public ArrayList<Token> reduce(ArrayList<Token> expression) {
		newExpression = expression;
		for (CRule c : CRules) {
			newExpression = c.applyRule(newExpression);
		}
		return newExpression;
	}



}
