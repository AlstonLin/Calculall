package com.trutech.calculall;
import java.util.ArrayList;

public class VRuleSet {

    public static int ADD = 1, SUBTRACT = 2, DOT = 3, CROSS = 4;

    ArrayList<VRule> VRules = new ArrayList<VRule>();
    ArrayList<Token> newExpression = new ArrayList<Token>();

    public VRuleSet () {
        VRules.add(new VRule (("[N,N,N]C[N,N,N]"), VRuleSet.CROSS, 3));
        VRules.add(new VRule (("[N,N]A[N,N]"), VRuleSet.ADD, 2));
        VRules.add(new VRule (("[N,N]S[N,N]"), VRuleSet.SUBTRACT, 2));
        VRules.add(new VRule (("[N,N,N]A[N,N,N]"), VRuleSet.ADD, 3));
        VRules.add(new VRule (("[N,N,N]S[N,N,N]"), VRuleSet.SUBTRACT, 3));
        VRules.add(new VRule (("[N,N]D[N,N]"), VRuleSet.DOT, 2));
        VRules.add(new VRule (("[N,N,N]D[N,N,N]"), VRuleSet.DOT, 3));
    }

    public ArrayList<Token> reduce(ArrayList<Token> expression) {
        newExpression = expression;
        for (VRule v : VRules) {
            newExpression = v.applyRule(newExpression);
        }
        return newExpression;
    }



}
