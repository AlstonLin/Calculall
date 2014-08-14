package com.trutech.calculall;
import java.util.ArrayList;

public class VRuleSet {

    public static int ADD = 1, SUBTRACT = 2, DOT = 3, CROSS = 4, MAGNITUDE = 5;

    ArrayList<VRule> VRules = new ArrayList<VRule>();
    ArrayList<Token> newExpression = new ArrayList<Token>();
    private boolean appliedRule = false;

    public VRuleSet () {
        VRules.add(new VRule (("[N,N,N]C[N,N,N]"), VRuleSet.CROSS, 3, this));
        VRules.add(new VRule (("[N,N]A[N,N]"), VRuleSet.ADD, 2, this));
        VRules.add(new VRule (("[N,N]S[N,N]"), VRuleSet.SUBTRACT, 2, this));
        VRules.add(new VRule (("[N,N,N]A[N,N,N]"), VRuleSet.ADD, 3, this));
        VRules.add(new VRule (("[N,N,N]S[N,N,N]"), VRuleSet.SUBTRACT, 3, this));
        VRules.add(new VRule (("[N,N]D[N,N]"), VRuleSet.DOT, 2, this));
        VRules.add(new VRule (("[N,N,N]D[N,N,N]"), VRuleSet.DOT, 3, this));
        VRules.add(new VRule (("|N,N|"), VRuleSet.MAGNITUDE, 2, this));
        VRules.add(new VRule (("|N,N,N|"), VRuleSet.MAGNITUDE, 3, this));
    }

    public ArrayList<Token> reduce(ArrayList<Token> expression) {
        newExpression = expression;
        for (VRule v : VRules) {
            newExpression = v.applyRule(newExpression);
        }
        //If a rule is applied and the size of newExpression is 1, which means it's a number or 5 or 7
        // with brackets at both ends, then return newExpression
        if (appliedRule && (newExpression.size() == 1 || ((newExpression.size() == 5 || newExpression.size() == 7 )
                && newExpression.get(0) instanceof Bracket && newExpression.get(newExpression.size() - 1) instanceof Bracket))) {
            return newExpression;
        }
        else {
            throw new IllegalArgumentException();
        }
    }

    public void setAppliedRule () {
        appliedRule = true;
    }


}
