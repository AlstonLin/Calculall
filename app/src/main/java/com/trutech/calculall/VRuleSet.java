package com.trutech.calculall;
import java.util.ArrayList;

public class VRuleSet {

    public static int ADD = 1, SUBTRACT = 2, DOT = 3, CROSS = 4;

    private static ArrayList<VRule> VRules = new ArrayList<VRule>();
    private static ArrayList<Token> newExpression = new ArrayList<Token>();
    private static boolean appliedRule = false;

    public VRuleSet () {
        VRules.add(new VRule (("[N,N,N]C[N,N,N]"), VRuleSet.CROSS, 3, this));
        VRules.add(new VRule (("[N,N]A[N,N]"), VRuleSet.ADD, 2, this));
        VRules.add(new VRule (("[N,N]S[N,N]"), VRuleSet.SUBTRACT, 2, this));
        VRules.add(new VRule (("[N,N,N]A[N,N,N]"), VRuleSet.ADD, 3, this));
        VRules.add(new VRule (("[N,N,N]S[N,N,N]"), VRuleSet.SUBTRACT, 3, this));
        VRules.add(new VRule (("[N,N]D[N,N]"), VRuleSet.DOT, 2, this));
        VRules.add(new VRule (("[N,N,N]D[N,N,N]"), VRuleSet.DOT, 3, this));

    }

    public static ArrayList<Token> reduce(ArrayList<Token> expression) {
        newExpression = expression;
        for (VRule v : VRules) {
            newExpression = v.applyRule(newExpression);
        }

        if (appliedRule && (newExpression.size() == 1 || newExpression.size() == 5 || newExpression.size() == 7 )) {
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
