package com.trutech.calculall;
import java.util.ArrayList;

public class CRuleSet {

    ArrayList<CRule> CRules = new ArrayList<CRule>();
    ArrayList<Token> newExpression = new ArrayList<Token>();

    public CRuleSet () {
        CRules.add(new CRule ("SNMSN", "S(NMN)", false));
        CRules.add(new CRule ("CNMCN", "C(NMN)", false));
        CRules.add(new CRule ("NRNMNRN", "NR(NMN)", false));
        CRules.add(new CRule ("C(NMN)MCN", "C(NMNMN)", false));
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
