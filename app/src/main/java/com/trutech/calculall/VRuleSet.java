package com.trutech.calculall;
import java.util.ArrayList;

public class VRuleSet {

    public static int ADD = 1, SUBTRACT = 2, DOT = 3, CROSS = 4, MAGNITUDE = 5, MULTIPLY = 6,
            UNITVECTOR = 7, ANGLE = 8, CHECK = 0;

    private static ArrayList<VRule> VRules = new ArrayList<VRule>();
    private static ArrayList<Token> newExpression = new ArrayList<Token>();
    private static boolean validOutput = false, pressedUnitVButton = false;

     static {
        VRules.add(new VRule (("N[N,N]"), VRuleSet.MULTIPLY, 2));
        VRules.add(new VRule (("N[N,N,N]"), VRuleSet.MULTIPLY, 3));
        VRules.add(new VRule (("[N,N,N]C[N,N,N]"), VRuleSet.CROSS, 3));
        VRules.add(new VRule (("[N,N]A[N,N]"), VRuleSet.ADD, 2));
        VRules.add(new VRule (("[N,N]S[N,N]"), VRuleSet.SUBTRACT, 2));
        VRules.add(new VRule (("[N,N,N]A[N,N,N]"), VRuleSet.ADD, 3));
        VRules.add(new VRule (("[N,N,N]S[N,N,N]"), VRuleSet.SUBTRACT, 3));
        VRules.add(new VRule (("[N,N]D[N,N]"), VRuleSet.DOT, 2));
        VRules.add(new VRule (("[N,N,N]D[N,N,N]"), VRuleSet.DOT, 3));
        VRules.add(new VRule (("|[N,N]|"), VRuleSet.MAGNITUDE, 2));
        VRules.add(new VRule (("|[N,N,N]|"), VRuleSet.MAGNITUDE, 3));
        VRules.add(new VRule (("[N,N]"), VRuleSet.UNITVECTOR, 2)); //Used to check if the user pressed the unitVectorButton
        VRules.add(new VRule (("[N,N,N]"), VRuleSet.UNITVECTOR, 3)); //Used to check if the user pressed the unitVectorButton
        VRules.add(new VRule (("N[N,N]"), VRuleSet.MULTIPLY, 2)); //Checked for again for unit vector calculation
        VRules.add(new VRule (("N[N,N,N]"), VRuleSet.MULTIPLY, 3)); //Checked for again for unit vector calculation
        VRules.add(new VRule (("[N,N]a[N,N]"), VRuleSet.ANGLE, 2)); //Used to find angle between 2 vectors
        VRules.add(new VRule (("[N,N]"), VRuleSet.CHECK, 2)); //Used to check if the output is valid
        VRules.add(new VRule (("[N,N,N]"), VRuleSet.CHECK, 3)); //Used to check if the output is valid
    }

    public static ArrayList<Token> reduce(ArrayList<Token> expression) {
        newExpression = expression;
        for (VRule v : VRules) {
            newExpression = v.applyRule(newExpression);
        }
        //Old
        //If a rule is applied and the size of newExpression is 1, which means it's a number or 5 or 7
        // with brackets at both ends, then return newExpression "&& (newExpression.size() == 1 || ((newExpression.size() == 5 || newExpression.size() == 7 )
        //&& newExpression.get(0) instanceof Bracket && newExpression.get(newExpression.size() - 1) instanceof Bracket)))""

        //New
        //If the output is valid or the output is a single number, then return newExpression
        if (validOutput  || (newExpression.size() == 1 && newExpression.get(0) instanceof Number) ||
                (newExpression.size() == 2 && (newExpression.get(0) instanceof Number && newExpression.get(1).getSymbol() == "d"))) {
            return newExpression;
        }
        else {
            throw new IllegalArgumentException();
        }
    }

    //Used to confirm the output is valid
    public static void setValidOutput (boolean inValidOutput) {
        validOutput = inValidOutput;
    }

    //Used to see if output is valid
    public static boolean getValidOutput () {
        return validOutput;
    }

    //Used to confirm the user pressed the unit vector button
    public static void setPressedUnitVButton (boolean inPressedUnitVButton) {
        pressedUnitVButton = inPressedUnitVButton;
    }

    //Used to confirm the user pressed the unit vector button
    public static boolean getPressedUnitVButton () {
        return pressedUnitVButton;
    }



}
