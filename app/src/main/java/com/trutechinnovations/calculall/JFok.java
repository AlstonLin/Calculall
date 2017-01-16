package com.trutechinnovations.calculall;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.Stack;

/**
 * Contains the JFok Algorithm and the core of the mathematical system.
 *
 * @author J(ason)Fok, Ejaaz Merali, Alston Lin
 * @version 3.0
 */
public class JFok {

    public static final double FRACTIONALIZE_ERROR = 1e-12;
    public static final double FRACTIONALIZE_DENOM_LIMIT = 10000;

    /**
     * Simplify and factor the given mathematical expression. There should be no
     * variables in the expression given.
     *
     * @param expression The expression to simplify
     * @return The fully simplified expression
     */
    public static ArrayList<Token> simplifyExpression(ArrayList<Token> expression) {
        //Sets up the tree
        Node<Token> root = setupAndConvertToTree(expression);
        //This is where the magic happens
        root = simplify(root);
        //Converts it back to human-readable form and cleans it up
        expression = traverseTree(root);
        expression = cleanupExpression(expression);
        return expression;
    }

    /**
     * Transforms the given expression into proper mathematical form by evaluating any sub-expressions that
     * would return an exact value (eg. ln(e)), as well as taking away any negative exponents.
     *
     * @param expression The expression to jFok
     * @return The expression that has been jFoked
     */
    public static ArrayList<Token> jFokExpression(ArrayList<Token> expression) {
        //Sets up the tree
        Node<Token> root = setupAndConvertToTree(expression);
        root = jFok(root);
        //Converts it back to human-readable form and cleans it up
        expression = traverseTree(root);
        expression = cleanupExpression(expression);
        return expression;
    }

    /**
     * Converts the expression into standard form.
     *
     * @param expression The expression to convert
     * @return The expression in standard form
     */
    public static ArrayList<Token> convertToStandardForm(ArrayList<Token> expression) {
        //Sets up the tree
        Node<Token> root = setupAndConvertToTree(expression);
        //This is where the magic happens
        root = expand(root);
        //Converts it back to human-readable form
        expression = traverseTree(root);
        expression = cleanupExpression(expression);
        return expression;
    }

    /**
     * Converts the given Number into a fraction. If the denominator if more
     * than 1000, it will assume the result is inaccurate and leave it.
     *
     * @param number The number Token
     * @return An equivalent fraction
     */
    public static ArrayList<Token> fractionalize(Number number) {
        double value = number.getValue();
        int n = (int) Math.floor(value);
        ArrayList<Token> output = new ArrayList<>();
        value -= n;

        //Checks if it is an integer
        if (value < FRACTIONALIZE_ERROR) {
            output.add(new Number(n));
            return output;
        } else if (1 - FRACTIONALIZE_ERROR < value) {
            output.add(new Number(n + 1));
            return output;
        }

        //The lower fraction starts at 0 / 1
        int lowerN = 0;
        int lowerD = 1;
        //The upper fraction starts at 1 / 1
        int upperN = 1;
        int upperD = 1;

        while (true) { //Continues until returns

            //Finds the average of the upper and lower fractions
            int middleN = lowerN + upperN;
            int middleD = lowerD + upperD;

            if (middleD * (value + FRACTIONALIZE_ERROR) < middleN) {
                upperN = middleN;
                upperD = middleD;
            } else if (middleN < (value - FRACTIONALIZE_ERROR) * middleD) {
                lowerN = middleN;
                lowerD = middleD;
            } else {
                if (middleD < FRACTIONALIZE_DENOM_LIMIT) { //Denom is within the limit
                    Number num = new Number(n * middleD + middleN);
                    Number denom = new Number(middleD);

                    output.add(BracketFactory.makeFracOpen());
                    output.add(BracketFactory.makeNumOpen());
                    output.add(num);
                    output.add(BracketFactory.makeNumClose());
                    output.add(OperatorFactory.makeFraction());
                    output.add(BracketFactory.makeDenomOpen());
                    output.add(denom);
                    output.add(BracketFactory.makeDenomClose());
                    output.add(BracketFactory.makeFracClose());
                    return output;
                } else {
                    ArrayList<Token> original = new ArrayList<>();
                    original.add(number);
                    return original;
                }
            }
        }
    }

    /**
     * Calls all the preparation methods for the expression and then converts the list
     * of tokens into a expression tree.
     *
     * @param expression The list of tokens
     * @return The expression tree
     */
    public static Node<Token> setupAndConvertToTree(ArrayList<Token> expression) {
        expression = Utility.condenseDigits(expression);
        expression = Utility.setupExpression(expression);
        expression = Utility.convertToReversePolish(expression);
        return setupTree(expression);
    }

    /**
     * Expands the given binary expression tree by applying rules.
     *
     * @param root The root of the tree
     * @return The root of the new expanded tree
     */
    private static Node<Token> expand(Node<Token> root) {
        if (root.getNumOfChildren() > 0) { //Not a leaf
            Node<Token> n1 = root.getNumOfChildren() > 0 ? root.getChildren().get(0) : null;
            Node<Token> n2 = root.getNumOfChildren() > 1 ? root.getChildren().get(1) : null;
            //Applies rules to the subtrees first, if any are found
            if ((n1 != null && n1.getNumOfChildren() != 0) || (n2 != null && n2.getNumOfChildren() != 0)) { //The children are not leaves
                //Evaluates the subtrees first
                n1 = expand(n1);
                if (n2 != null) {
                    n2 = expand(n2);
                }
                //Adds the new subtrees to the root
                root.getChildren().clear();
                root.addChild(n1);
                if (n2 != null) {
                    root.addChild(n2);
                }
                //Now applies the rules
                root = applyExpandingRules(root);
                return root;
            } else { //Children are leaves; applies the rules
                root = applyExpandingRules(root);
                return root;
            }
        } else { //Base case: the root is a leaf
            return root;
        }
    }

    /**
     * Applies the math rules to expand and then simplify the expression into
     * standard form.
     *
     * @param root The root of the tree/subtree
     * @return The root of the new tree/subtree
     */
    private static Node<Token> applyExpandingRules(Node<Token> root) {
        root = applyPowers(root);
        root = processMultipleFractions(root, new Command<Node<Token>, Node<Token>>() {
            @Override
            public Node<Token> execute(Node<Token> o) {
                return expand(o);
            }
        });
        root = applyDistributiveProperty(root);
        root = multiplyPolynomials(root);
        root = multiplyTerms(root);
        //Rules to be applied before constants are evaluated
        root = applyCommutativeProperty(root);
        root = evaluateConstants(root, false, false);
        //Rules to be applied after constants are evaluated
        //root = addLikeTerms(root);
        root = multiplyVariables(root);
        return root;
    }

    /**
     * Applies the math rules to simplify and factor the expression/
     *
     * @param root The root of the tree/subtree
     * @return The new root of the tree/subtree
     */
    private static Node<Token> applySimplifyingRules(Node<Token> root) {
        Command<Node<Token>, Node<Token>> command = new Command<Node<Token>, Node<Token>>() {
            @Override
            public Node<Token> execute(Node<Token> o) {
                return simplify(o);
            }
        };
        root = multiplyDecimalFractions(root);
        root = applyTrigSpecialValues(root);
        root = applyInvTrigSpecialValues(root);
        root = processMultipleFractions(root, command);
        root = applySquareRootRules(root);
        root = applyCommutativeProperty(root);
        //Rules to be applied before constants are evaluated
        root = evaluateConstants(root, true, true);
        //Rules to be applied after constants are evaluated
        root = multiplyFractions(root, command);
        root = addLikeFractions(root);
        root = simplifyFraction(root);
        root = simplifyIdenticalNumDenom(root);
        root = removeMultiplicationsOfOne(root);
        root = applyTrigRules(root);
        return root;
    }

    /**
     * Applies special values of inverse trig functions in Radians.
     *
     * @param root The node to apply this rule to
     * @return The new node
     */
    private static Node<Token> applyInvTrigSpecialValues(Node<Token> root) {
        if (Function.angleMode == Function.RADIAN && root.getContent() instanceof Function &&
                (root.getContent().getType() == Function.ARCSIN || root.getContent().getType() == Function.ARCCOS || root.getContent().getType() == Function.ARCTAN)) {
            ArrayList<Token> expression = traverseTree(root.getChildren().get(0));
            double value = Utility.process(expression);
            value = Utility.round(value, 7);
            if (root.getContent().getType() == Function.ARCSIN) {
                if (value == -1) { //arcsin(-1) = -pi / 2
                    return ExpressionFactory.makePiOverN(true, 2);
                } else if (value == -0.8660254) {//arcsin(-sqrt3/2) = -pi / 3
                    return ExpressionFactory.makePiOverN(true, 3);
                } else if (value == -0.7071068) {//arcsin(-sqrt2/2) = -pi / 4
                    return ExpressionFactory.makePiOverN(true, 4);
                } else if (value == -0.5) {//arcsin(-1/2) = -pi / 6
                    return ExpressionFactory.makePiOverN(true, 6);
                } else if (value == 0) { //arcsin(0) = 0
                    return new Node<Token>(new Number(0));
                } else if (value == 0.5) {//arcsin(1/2) = pi / 6
                    return ExpressionFactory.makePiOverN(false, 6);
                } else if (value == 0.7071068) {//arcsin(sqrt2/2) = pi / 4
                    return ExpressionFactory.makePiOverN(false, 4);
                } else if (value == 0.8660254) {//arcsin(sqrt3/2) = pi / 3
                    return ExpressionFactory.makePiOverN(false, 3);
                } else if (value == 1) { //arcsin(1) = pi / 2
                    return ExpressionFactory.makePiOverN(false, 2);
                }
            } else if (root.getContent().getType() == Function.ARCCOS) {
                if (value == -1) { //arccos(-1) = pi
                    return new Node<Token>(VariableFactory.makePI());
                } else if (value == -0.8660254) {//arccos(-sqrt3/2) = 5pi / 6
                    return ExpressionFactory.makeNPiOverM(5, 6);
                } else if (value == -0.7071068) {//arccos(-sqrt2/2) = 3pi / 4
                    return ExpressionFactory.makeNPiOverM(3, 4);
                } else if (value == -0.5) {//arccos(-1/2) = 2pi / 3
                    return ExpressionFactory.makeNPiOverM(2, 3);
                } else if (value == 0) { //arccos(0) = pi / 2
                    return ExpressionFactory.makePiOverN(false, 2);
                } else if (value == 0.5) {//arccos(1/2) = pi / 3
                    return ExpressionFactory.makePiOverN(false, 3);
                } else if (value == 0.7071068) {//arccos(sqrt2/2) = pi / 4
                    return ExpressionFactory.makePiOverN(false, 4);
                } else if (value == 0.8660254) {//arccos(sqrt3/2) = pi / 6
                    return ExpressionFactory.makePiOverN(false, 6);
                } else if (value == 1) { //arccos(1) = 0
                    return new Node<Token>(new Number(0));
                }
            } else if (root.getContent().getType() == Function.ARCTAN) {
                if (value == -1.732051) { //arctan(-sqrt3) = -pi / 3
                    return ExpressionFactory.makePiOverN(true, 3);
                } else if (value == -1) { //arctan(-1) = -pi / 4
                    return ExpressionFactory.makePiOverN(true, 4);
                } else if (value == -0.5773503) { //arctan(-sqrt3/3) = -pi / 6
                    return ExpressionFactory.makePiOverN(true, 6);
                } else if (value == 0) { //arctan(0) = 0
                    return new Node<Token>(new Number(0));
                } else if (value == 0.5773503) { //arctan(sqrt3/3) = pi / 6
                    return ExpressionFactory.makePiOverN(false, 6);
                } else if (value == 1) { //arctan(1) = pi / 4
                    return ExpressionFactory.makePiOverN(false, 4);
                } else if (value == 1.732051) { //arctan(sqrt3) = pi / 3
                    return ExpressionFactory.makePiOverN(false, 3);
                }
            } else {
                return root;
            }
        }
        return root;
    }

    /**
     * Multiplies decimals so that there would be no decimal fractions
     *
     * @param root The root of the fraction to evaluate
     * @return The new root
     */
    private static Node<Token> multiplyDecimalFractions(Node<Token> root) {
        if (root.getContent() instanceof Operator && (root.getContent().getType() == Operator.FRACTION || root.getContent().getType() == Operator.DIVIDE)) {
            Token child1 = root.getChildren().get(0).getContent();
            Token child2 = root.getChildren().get(1).getContent();
            if (child1 instanceof Number && child2 instanceof Number) {
                double value1 = ((Number) child1).getValue();
                double value2 = ((Number) child2).getValue();
                if (value1 % 1 != 0 || value2 % 1 != 0) {
                    String s1 = Double.toString(Math.abs(value1));
                    String s2 = Double.toString(Math.abs(value2));
                    int decimals1 = s1.length() - s1.indexOf('.') - 1;
                    int decimals2 = s2.length() - s2.indexOf('.') - 1;
                    int multiplier = (int) (decimals1 > decimals2 ? Math.pow(10, decimals1) : Math.pow(10, decimals2));
                    value1 *= multiplier;
                    value2 *= multiplier;
                    Node<Token> div = new Node<Token>(OperatorFactory.makeFraction());
                    div.addChild(new Node<Token>(new Number(Math.round(value1))));
                    div.addChild(new Node<Token>(new Number(Math.round(value2))));
                    return div;
                }
            }
        }
        return root;
    }

    private static Node<Token> applyTrigSpecialValues(Node<Token> root) {
        if (root.getContent() instanceof Function && root.getChildren().get(0).getContent() instanceof Number) {
            double num = ((Number) root.getChildren().get(0).getContent()).getValue();

            //Converts to degrees
            if (Function.angleMode == Function.RADIAN) {
                num = Math.toDegrees(num);
            } else if (Function.angleMode == Function.GRADIAN) {
                num = 0.9 * num;
            }

            //Converts it into a positive number
            if (num < 0) {
                int multiple = (int) (-(num / 360) + 1);
                num += multiple * 360;
            }
            //Rounds to 1e-7 tolerance
            double tolerance = 1e-7;
            num = Math.round(num / tolerance) * tolerance;
            if (root.getContent().getType() == Function.SIN) {
                double degree = num % 360;
                boolean negative = false;
                //Converts to <90 degree
                if (degree > 90 && degree < 180) { // Q2 -> 180 - angle
                    degree = 180 - degree;
                } else if (degree > 180 && degree < 270) { // Q3 -> angle - 180 & negative
                    degree = degree - 180;
                    negative = true;
                } else if (degree > 270) { //Q4 -> 360 - angle & negative
                    degree = 360 - degree;
                    negative = true;
                }

                if (degree == 0) { //sin(0) = 0
                    return new Node<Token>(new Number(0));
                } else if (degree == 30) { //sin(30) = 1/2
                    return ExpressionFactory.makeOneHalf(negative);
                } else if (degree == 45) { //sin(45) = sqrt2 / 2
                    return ExpressionFactory.makeSqrt2Over2(negative);
                } else if (degree == 60) { //sin(60) = sqrt3 / 2
                    return ExpressionFactory.makeSqrt3Over2(negative);
                } else if (degree == 90) { //sin(90) = 1
                    return new Node<Token>(new Number(1));
                }
            } else if (root.getContent().getType() == Function.COS) {
                double degree = num % 360;
                boolean negative = false;
                //Converts to <90 degree
                if (degree > 90 && degree < 180) { // Q2 -> 180 - angle & negative
                    degree = 180 - degree;
                    negative = true;
                } else if (degree > 180 && degree < 270) { // Q3 -> angle - 180 & negative
                    degree = degree - 180;
                    negative = true;
                } else if (degree > 270) { //Q4 -> 360 - angle
                    degree = 360 - degree;
                }
                if (degree == 0) { //cos(0) = 1
                    return new Node<Token>(new Number(1));
                } else if (degree == 30) { //cos(30) = sqrt3 / 2
                    return ExpressionFactory.makeSqrt3Over2(negative);
                } else if (degree == 45) { //cos(45) = sqrt2 / 2
                    return ExpressionFactory.makeSqrt2Over2(negative);
                } else if (degree == 60) { //cos(60) = 1 / 2
                    return ExpressionFactory.makeOneHalf(negative);
                } else if (degree == 90) { //cos(90) = 0
                    return new Node<Token>(new Number(0));
                }
            } else if (root.getContent().getType() == Function.TAN) {
                double degree = num % 360;
                boolean negative = false;
                //Converts to <90 degree
                if (degree > 90 && degree < 180) { // Q2 -> 180 - angle & negative
                    degree = 180 - degree;
                    negative = true;
                } else if (degree > 180 && degree < 270) { // Q3 -> angle - 180
                    degree = degree - 180;
                } else if (degree > 270) { //Q4 -> 360 - angle & negative
                    degree = 360 - degree;
                    negative = true;
                }
                if (degree == 0) { //tan(0) = 0
                    return new Node<Token>(new Number(1));
                }
                if (degree == 30) { //tan(30) = sqrt 3 / 3
                    return ExpressionFactory.makeSqrt3Over3(negative);
                } else if (degree == 60) { //tan(30) = sqrt 3
                    return ExpressionFactory.makeSqrt3(negative);
                } else if (degree == 90) { //tan(90) = UNDEFINED
                    throw new IllegalArgumentException("tan is not defined at this value!");
                }
            }
        }
        return root;
    }

    /**
     * Applies simplifying trig rules to the treee if it applies.
     *
     * @param root The root to apply trig rules
     * @return The new tree
     */
    private static Node<Token> applyTrigRules(Node<Token> root) {
        if (root.getContent() instanceof Operator && root.getContent().getType() == Operator.DIVIDE) {
            Node child1 = root.getChildren().get(0);
            Node child2 = root.getChildren().get(1);
            if (child1.getContent() instanceof Function && child2.getContent() instanceof Function) {
                if ((((Function) child1.getContent()).getType() == Function.SIN && ((Function) child2.getContent()).getType() == Function.COS)) { //sin / cos -> tan
                    Node<Token> inside1 = (Node<Token>) child1.getChildren().get(0);
                    Node inside2 = (Node) child2.getChildren().get(0);
                    if (isBranchesEqual(inside1, inside2)) {
                        Node<Token> tan = new Node<Token>(FunctionFactory.makeTan());
                        tan.addChild(inside1);
                        return tan;
                    }
                } else if ((((Function) child1.getContent()).getType() == Function.COS && ((Function) child2.getContent()).getType() == Function.SIN)) { //cos / sin -> 1/tan
                    Node<Token> inside1 = (Node<Token>) child1.getChildren().get(0);
                    Node<Token> inside2 = (Node<Token>) child2.getChildren().get(0);
                    if (isBranchesEqual(inside1, inside2)) {
                        Node<Token> div = new Node<Token>(OperatorFactory.makeDivide());
                        Node<Token> tan = new Node<Token>(FunctionFactory.makeTan());
                        tan.addChild(inside1);
                        div.addChild(new Node<Token>(new Number(1)));
                        div.addChild(tan);
                        return div;
                    }
                }
            }
        }
        return root;
    }

    /**
     * Simplifies fractions that have identical Num and Denoms to 1.
     *
     * @param root The root of the tree to apply this rule to
     * @return The new root of the tree
     */
    private static Node<Token> simplifyIdenticalNumDenom(Node<Token> root) {
        if (root.getContent() instanceof Operator && (root.getContent().getType() == Operator.DIVIDE || root.getContent().getType() == Operator.FRACTION)) {
            Node<Token> child1 = root.getChildren().get(0);
            Node<Token> child2 = root.getChildren().get(1);
            if (isBranchesEqual(child1, child2)) {
                return new Node(new Number(1));
            } else {
                return root;
            }
        } else {
            return root;
        }
    }

    /**
     * Determines if both nodes contain the exact same tokens
     *
     * @param root1 The root of the first tree
     * @param root2 The root of the second tree
     * @return If the first and second tree contains the same tokens
     */
    private static boolean isBranchesEqual(Node<Token> root1, Node<Token> root2) {
        if (root1 == null && root2 == null) { //Base case
            return true;
        } else if (root1 == null || root2 == null) { //Trees are different side
            return false;
        } else {
            boolean equal = root1.getContent().getClass().equals(root2.getContent().getClass()) && root1.getContent().getType() == root2.getContent().getType()
                    && (root1.getContent().getType() != -1 || ((Number) root1.getContent()).getValue() == ((Number) root2.getContent()).getValue());
            if (equal && root1.getNumOfChildren() == root2.getNumOfChildren()) { //Same number of subbranches
                if (root1.getNumOfChildren() == 1) {
                    equal = equal && isBranchesEqual(root1.getChildren().get(0), root2.getChildren().get(0));
                } else if (root1.getNumOfChildren() == 2) {
                    equal = equal && ((isBranchesEqual(root1.getChildren().get(0), root2.getChildren().get(0)) && isBranchesEqual(root1.getChildren().get(1), root2.getChildren().get(1)))
                            || (root1.getContent() instanceof Operator && (root1.getContent().getType() == Operator.ADD || root1.getContent().getType() == Operator.MULTIPLY)
                            && (isBranchesEqual(root1.getChildren().get(1), root2.getChildren().get(0)) && isBranchesEqual(root1.getChildren().get(0), root2.getChildren().get(1)))));
                }
                return equal;
            } else {
                return false;
            }
        }
    }

    /**
     * Cleans up the expression and renders it more human-readable by removing
     * redundancies.
     *
     * @param expression The expression to clean up
     * @return The cleaned up expression
     */
    private static ArrayList<Token> cleanupExpression(ArrayList<Token> expression) {
        expression = removeNegatives(expression);
        expression = removeMultiplicationsOfOne(expression);
        expression = removeDivisionsOfOne(expression);
        expression = addFractionalBrackets(expression);
        return expression;
    }

    private static ArrayList<Token> addFractionalBrackets(ArrayList<Token> expression) {
        ArrayList<Token> newExp = new ArrayList<>();
        for (int i = 0; i < expression.size(); i++) {
            Token t = expression.get(i);
            if (t instanceof Operator && (t.getType() == Operator.FRACTION || t.getType() == Operator.DIVIDE)) {
                //Finds the numerator first
                ArrayList<Token> num = new ArrayList<>();
                int bracketCount = 0;
                int j = newExp.size() - 1;
                while (j >= 0 && !((bracketCount == 0 && newExp.get(j) instanceof Operator && ((Operator) newExp.get(j)).getPrecedence() < 3) || bracketCount < 0)) {
                    Token token = newExp.get(j);
                    if (token instanceof Bracket) {
                        Bracket b = (Bracket) token;
                        if (b.getType() == Bracket.OPEN || b.getType() == Bracket.SUPERSCRIPT_OPEN) {
                            bracketCount--;
                        } else if (b.getType() == Bracket.CLOSE || b.getType() == Bracket.SUPERSCRIPT_CLOSE) {
                            bracketCount++;
                        }
                    }
                    if (bracketCount >= 0) {
                        num.add(0, newExp.remove(j));
                    }
                    j--;
                }
                //Now denom
                j = i + 1;
                bracketCount = 0;
                ArrayList<Token> denom = new ArrayList<>();
                while (j < expression.size() && !((bracketCount == 0 && expression.get(j) instanceof Operator && ((Operator) expression.get(j)).getPrecedence() <= 3) || bracketCount < 0)) {
                    Token token = expression.get(j);
                    if (token instanceof Bracket) {
                        Bracket b = (Bracket) token;
                        if (b.getType() == Bracket.OPEN || b.getType() == Bracket.SUPERSCRIPT_OPEN) {
                            bracketCount++;
                        } else if (b.getType() == Bracket.CLOSE || b.getType() == Bracket.SUPERSCRIPT_CLOSE) {
                            bracketCount--;
                        }
                    }
                    if (bracketCount >= 0) {
                        denom.add(expression.get(j));
                        i++;
                    }
                    j++;
                }

                //Removes any brackets encompassing the num and denom, if any
                if (num.get(0) instanceof Bracket && num.get(0).getType() == Bracket.OPEN && num.get(num.size() - 1) instanceof Bracket
                        && num.get(num.size() - 1).getType() == Bracket.CLOSE) { //Num
                    num.remove(0);
                    num.remove(num.size() - 1);
                }
                if (denom.get(0) instanceof Bracket && denom.get(0).getType() == Bracket.OPEN && denom.get(denom.size() - 1) instanceof Bracket
                        && denom.get(denom.size() - 1).getType() == Bracket.CLOSE) { //Denom
                    denom.remove(0);
                    denom.remove(denom.size() - 1);
                }
                //And now joins the numerator and denom
                newExp.add(BracketFactory.makeNumOpen());
                newExp.addAll(num);
                newExp.add(BracketFactory.makeNumClose());
                newExp.add(OperatorFactory.makeFraction());
                newExp.add(BracketFactory.makeDenomOpen());
                newExp.addAll(denom);
                newExp.add(BracketFactory.makeDenomClose());
            } else {
                newExp.add(t);
            }
        }
        return newExp;
    }

    /**
     * Simplifies the given binary expression tree by applying rules.
     *
     * @param root The root of the tree
     * @return The root of the new expanded tree
     */
    private static Node<Token> simplify(Node<Token> root) {
        if (root.getNumOfChildren() > 0) { //Not a leaf
            Node<Token> n1 = root.getNumOfChildren() > 0 ? root.getChildren().get(0) : null;
            Node<Token> n2 = root.getNumOfChildren() > 1 ? root.getChildren().get(1) : null;
            //Applies rules to the subtrees first, if any are found
            if ((n1 != null && n1.getNumOfChildren() != 0) || (n2 != null && n2.getNumOfChildren() != 0)) { //The children are not leaves
                //Evaluates the subtrees first
                n1 = simplify(n1);
                if (n2 != null) {
                    n2 = simplify(n2);
                }
                //Adds the new subtrees to the root
                root.getChildren().clear();
                root.addChild(n1);
                if (n2 != null) {
                    root.addChild(n2);
                }
                //Now applies rules to this branch
                root = applySimplifyingRules(root);
                return root;
            } else { //Children are leaves; applies the rules
                root = applySimplifyingRules(root);
                return root;
            }
        } else { //Base case: the root is a leaf
            return root;
        }
    }

    /**
     * Applies a set of rules recursively that makes the expression better.
     *
     * @param root The root of the tree
     * @return The root of the new expanded tree
     */
    private static Node<Token> jFok(Node<Token> root) {
        if (root.getNumOfChildren() > 0) { //Not a leaf
            Node<Token> n1 = root.getNumOfChildren() > 0 ? root.getChildren().get(0) : null;
            Node<Token> n2 = root.getNumOfChildren() > 1 ? root.getChildren().get(1) : null;
            //Applies rules to the subtrees first, if any are found
            if ((n1 != null && n1.getNumOfChildren() != 0) || (n2 != null && n2.getNumOfChildren() != 0)) { //The children are not leaves
                //Evaluates the subtrees first
                n1 = jFok(n1);
                if (n2 != null) {
                    n2 = jFok(n2);
                }
                //Adds the new subtrees to the root
                root.getChildren().clear();
                root.addChild(n1);
                if (n2 != null) {
                    root.addChild(n2);
                }
                //Now applies rules to this branch
                root = applyJFokRules(root);
                return root;
            } else { //Children are leaves; applies the rules
                root = applyJFokRules(root);
                return root;
            }
        } else { //Base case: the root is a leaf
            return root;
        }
    }

    /**
     * To be called by jFok() to apply the relevants rules to sub-expression.
     *
     * @param node The sub-expression to apply the rules to
     * @return The new sub-expression
     */
    private static Node<Token> applyJFokRules(Node<Token> node) {
        Command<Node<Token>, Node<Token>> command = new Command<Node<Token>, Node<Token>>() {
            @Override
            public Node<Token> execute(Node<Token> o) {
                return jFok(o);
            }
        };
        node = evaluateConstants(node, true, true);
        node = removeNegativeExponents(node);
        node = addSqrts(node);
        node = removeMultiplicationsOfOne(node);
        node = multiplyFractions(node, command);
        node = processMultipleFractions(node, command);
        node = removeExponentsOfOne(node);
        return node;
    }

    /**
     * Searches for exponents of ones and removes them (eg. (x+1)^1 -> (x+1))
     *
     * @param node The node to apply this rule
     * @return The node with the rule applied
     */
    private static Node<Token> removeExponentsOfOne(Node<Token> node) {
        Token t = node.getContent();
        if (t instanceof Operator && t.getType() == Operator.EXPONENT) {
            Node<Token> base = node.getChildren().get(0);
            Node<Token> exp = node.getChildren().get(1);
            if (getValue(exp) == 1) {
                return jFok(base);
            }
        }
        return node;
    }

    /**
     * A tree version of removing mutliplications of one (E * 1) -> E
     *
     * @param node The original root
     * @return The root of the new tree with the rule applied
     */
    public static Node<Token> removeMultiplicationsOfOne(Node<Token> node) {
        if (node.getContent() instanceof Operator && node.getContent().getType() == Operator.MULTIPLY) {
            Node<Token> child1 = node.getChildren().get(0);
            Node<Token> child2 = node.getChildren().get(1);
            if (child2.getContent() instanceof Number && ((Number) child2.getContent()).getValue() == 1) {
                return applyJFokRules(child1);
            } else if (child1.getContent() instanceof Number && ((Number) child1.getContent()).getValue() == 1) {
                return applyJFokRules(child2);
            }
        }
        return node;
    }

    /**
     * Replaces all instances of E ^ (1/2) to SQRT E
     *
     * @param node The node to apply this rule
     * @return The node with the rule applied
     */
    private static Node<Token> addSqrts(Node<Token> node) {
        if (node.getContent() instanceof Operator && node.getContent().getType() == Operator.EXPONENT) {
            Node<Token> child1 = node.getChildren().get(0);
            Node<Token> child2 = node.getChildren().get(1);
            if (getValue(child2) == 0.5f) { //E ^ 0.5
                Node<Token> newRoot = new Node<Token>(FunctionFactory.makeSqrt());
                newRoot.addChild(child1);
                return newRoot;
            }
        }
        return node;
    }

    /**
     * @return The numerical value of this sub-expression, or Float.NaN is there is none (eg. has a variable)
     */
    private static float getValue(Node<Token> child2) {
        ArrayList<Token> expression = Utility.convertToReversePolish(traverseTree(child2));
        try {
            return (float) Utility.evaluateExpression(expression);
        } catch (IllegalArgumentException | EmptyStackException e) {
            return Float.NaN;
        }
    }

    /**
     * Changes the negative exponents into recipricols.
     *
     * @param node The node to apply this rule
     * @return The node with the negative exponents, if any, removed
     */
    private static Node<Token> removeNegativeExponents(Node<Token> node) {
        if (node.getContent() instanceof Operator && node.getContent().getType() == Operator.EXPONENT) {
            Node<Token> exp = node.getChildren().get(1);
            if (isNegative(exp)) {
                node.getChildren().remove(exp);
                Node<Token> newExp = new Node<Token>(OperatorFactory.makeMultiply());
                newExp.addChild(new Node<Token>(new Number(-1)));
                newExp.addChild(exp);
                node.addChild(newExp);
                return jFok(reciprocal(node));
            }
        }
        return node;
    }

    /**
     * Determines if the given sub-expression is negative (only for
     * single Variables and Numbers and purely numerical expressions only).
     *
     * @param node The root of the sub-expression
     * @return Whether or not the sub-expression is negative
     */
    private static boolean isNegative(Node<Token> node) {
        try { //Tries to evaluate it as a purely numerical expression
            return getValue(node) < 0;
        } catch (IllegalArgumentException | EmptyStackException e) { //Not a purely numerical expression; tries doing it individually
            Token t = node.getContent();
            if (t instanceof Variable) {
                return ((Variable) t).isNegative();
            } else if (t instanceof Number) {
                return ((Number) t).getValue() < 0;
            }
            return false;
        }
    }

    /**
     * Applies specific rules using the commutative and associative properties
     * to prepare the tree for evaluateConstants().
     *
     * @param root The root of the tree
     * @return The root of the new tree
     */
    private static Node<Token> applyCommutativeProperty(Node<Token> root) {
        Token token = root.getContent();
        if (token instanceof Operator) {
            Operator o = (Operator) token;
            Node<Token> childNode1 = root.getChildren().get(0);
            Node<Token> childNode2 = root.getChildren().get(1);
            Node<Token> interiorExpression;
            Token child1 = childNode1.getContent();
            Token child2 = childNode2.getContent();
            Operator expression; //Operator of the interior subclass
            Number n1;
            Number n2;
            Token expressionChild1;
            Token expressionChild2;
            Node<Token> expressionNode1;
            Node<Token> expressionNode2;
            if (child1 instanceof Operator && (child1.getType() == Operator.ADD || child1.getType() == Operator.SUBTRACT) && child2 instanceof Number) {
                expression = (Operator) child1;
                n1 = (Number) child2;
                expressionNode1 = childNode1.getChildren().get(0);
                expressionNode2 = childNode1.getChildren().get(1);
                expressionChild1 = expressionNode1.getContent();
                expressionChild2 = expressionNode2.getContent();
            } else if (child2 instanceof Operator && (child2.getType() == Operator.ADD || child2.getType() == Operator.SUBTRACT) && child1 instanceof Number) {
                expression = (Operator) child2;
                n1 = (Number) child1;
                expressionNode1 = childNode2.getChildren().get(0);
                expressionNode2 = childNode2.getChildren().get(1);
                expressionChild1 = expressionNode1.getContent();
                expressionChild2 = expressionNode2.getContent();

            } else { //No rules apply
                return root;
            }
            //Finds the second number, if there is one
            if (expressionChild1 instanceof Number || expressionChild2 instanceof Number) {
                n2 = expressionChild1 instanceof Number ? (Number) expressionChild1 : (Number) expressionChild2;
                interiorExpression = expressionChild1 instanceof Number ? expressionNode2 : expressionNode1;
            } else { //Rule deos not apply
                return root;
            }
            if (o.getType() == Operator.ADD) {
                //Now applies the appropriate rule
                if (expression.getType() == Operator.ADD) { //(E + N1) + N2 -> E + (N1 + N2)
                    root = new Node<Token>(OperatorFactory.makeAdd());
                    //Creates the new subtree (N1 + N2)
                    Node<Token> addition = new Node<Token>(OperatorFactory.makeAdd());
                    addition.addChild(new Node<Token>(n1));
                    addition.addChild(new Node<Token>(n2));
                    //Evaluates the expression
                    addition = evaluateConstants(addition, false, true);
                    //Adds both the the root and sends it off
                    root.addChild(interiorExpression);
                    root.addChild(addition);
                    return root;
                } else if (expression.getType() == Operator.SUBTRACT) { //(E - N1) + N2 -> E + (N2 - N1)
                    root = new Node<Token>(OperatorFactory.makeAdd());
                    //Creates the new subtree (N1 + N2)
                    Node<Token> subtract = new Node<Token>(OperatorFactory.makeSubtract());
                    subtract.addChild(new Node<Token>(n1));
                    subtract.addChild(new Node<Token>(n2));
                    //Evaluates the expression
                    subtract = evaluateConstants(subtract, false, true);
                    //Adds both the the root and sends it off
                    root.addChild(interiorExpression);
                    root.addChild(subtract);
                    return root;
                } else { //No rules apply
                    return root;
                }
            } else if (o.getType() == Operator.SUBTRACT) { //(E + N1) - N2 -> E + (N1 - N2)
                //Now applies the appropriate rule
                if (expression.getType() == Operator.ADD) { //(E + N1) - N2 -> E + (N1 - N2)
                    root = new Node<Token>(OperatorFactory.makeAdd());
                    //Creates the new subtree (N1 + N2)
                    Node<Token> subtract = new Node<Token>(OperatorFactory.makeSubtract());
                    subtract.addChild(new Node<Token>(n2));
                    subtract.addChild(new Node<Token>(n1));
                    //Evaluates the expression
                    subtract = evaluateConstants(subtract, false, true);
                    //Adds both the the root and sends it off
                    root.addChild(interiorExpression);
                    root.addChild(subtract);
                    return root;
                } else if (expression.getType() == Operator.SUBTRACT) { //(E - N1) - N2 -> E - (N2 + N1)
                    root = new Node<Token>(OperatorFactory.makeSubtract());
                    //Creates the new subtree (N1 + N2)
                    Node<Token> addition = new Node<Token>(OperatorFactory.makeAdd());
                    addition.addChild(new Node<Token>(n1));
                    addition.addChild(new Node<Token>(n2));
                    //Evaluates the expression
                    addition = evaluateConstants(addition, false, true);
                    //Adds both the the root and sends it off
                    root.addChild(interiorExpression);
                    root.addChild(addition);
                    return root;
                } else { //No rules apply
                    return root;
                }
            } else { //No rules apply
                return root;
            }
        } else { //No rules apply
            return root;
        }
    }

    /**
     * Evaluate any constants such that any numerical expressions, as well as
     * functions, would be calculated.
     *
     * @param root                   The root of the subtree
     * @param exactValue             If true, this will not evaluate square roots
     * @param doNotEvaluateFunctions If true, functions will not be evaluated unless they return an integer
     * @return The new root of the subtree
     * @throws IllegalArgumentException Invalid Expression
     */
    private static Node<Token> evaluateConstants(Node<Token> root, boolean exactValue, boolean doNotEvaluateFunctions) {
        Token token = root.getContent();
        //Evaluates this node
        if (root.getNumOfChildren() == 2) { //Operator
            //Now checks this node after the subtrees has been checked
            Token t1 = root.getChildren().get(0).getContent();
            Token t2 = root.getChildren().get(1).getContent();
            if (t1 instanceof Number && t2 instanceof Number && (!(token.getType() == Operator.DIVIDE || token.getType() == Operator.FRACTION) || !exactValue)) { //Rule applies (deos not do divisions)
                //Both numbers, rule can be applied
                double result = ((Operator) token).operate(((Number) t1).getValue(), ((Number) t2).getValue());
                return new Node<Token>(new Number(result));
            } else { //Rule deos not apply
                return root;
            }
        } else if (root.getNumOfChildren() == 1) { //Function
            Token child = root.getChildren().get(0).getContent();
            //Changes PI and e to their numbers
            if (child instanceof Variable && child.getType() == Variable.PI) {
                child = new Number(Math.PI);
            } else if (child instanceof Variable && child.getType() == Variable.E) {
                child = new Number(Math.E);
            }
            Function f = (Function) token;
            if (child instanceof Number) {
                double result = ((Function) token).perform(((Number) child).getValue());
                if (!doNotEvaluateFunctions || result % 1 == 0) {
                    return new Node<Token>(new Number(result));
                } else {
                    return root;
                }
            } else {
                return root;
            }
        } else if (root.getNumOfChildren() == 0) { //Leaf
            return root;
        } else {
            throw new IllegalArgumentException(); //Invalid Expression
        }
    }

    /**
     * Applies all square root rules to any square roots found within the
     * function/
     *
     * @param root The root of the tree / subtree
     * @return The root of the resulting tree
     */
    private static Node<Token> applySquareRootRules(Node<Token> root) {
        //TODO: ALLOW IT TO APPLY RULES FOR MULTIPLES OF ROOTS (EG. 2SQRT2 * 3SQRT 3 -> 6SQRT6)
        Token token = root.getContent();
        if (token instanceof Function && token.getType() == Function.SQRT) { //A square root; rules may apply
            Token child = root.getChildren().get(0).getContent();
            if (child instanceof Number) {
                double value = ((Number) child).getValue();
                //Rules specificly for Numbers
                double result = ((Function) token).perform(value);
                if (result % 1 == 0) { //The result is a integer
                    return new Node<Token>(new Number(result));
                } else { //Simplifies the square root
                    if (value % 1 == 0) { //Integer
                        int outside = 1;
                        int inside = (int) value;
                        int factor = 2;
                        while (factor * factor <= inside) {
                            if (inside % (factor * factor) == 0) {
                                inside /= factor * factor;
                                outside *= factor;
                            } else {
                                factor++;
                            }
                        }
                        if (outside != 1) { //It would be redundant to multiply it by 1
                            root = new Node<Token>(OperatorFactory.makeMultiply());
                            Node<Token> squareRoot = new Node<Token>(FunctionFactory.makeSqrt());
                            Node<Token> outsideNode = new Node<Token>(new Number(outside));
                            Node<Token> insideNode = new Node<Token>(new Number(inside));
                            squareRoot.addChild(insideNode);
                            root.addChild(outsideNode);
                            root.addChild(squareRoot);
                        }
                        return root;
                    } else {
                        //Decimal inside the square roots; just calculates it
                        return new Node<Token>(new Number(((Function) token).perform(value)));
                    }
                }
            } else { //Root of a variable / expression
                //TODO: Add support to this
                return root;
            }
        } else {
            //Condenses square roots multiplying each other
            if (token instanceof Operator && token.getType() == Operator.MULTIPLY) {
                Node<Token> child1 = root.getChildren().get(0);
                Node<Token> child2 = root.getChildren().get(1);
                if (child1.getContent() instanceof Function && child1.getContent().getType() == Function.SQRT && child2.getContent() instanceof Function
                        && child2.getContent().getType() == Function.SQRT) { //Two square roots under a multiplication; Rule can be applied
                    Node<Token> node1 = child1.getChildren().get(0);
                    Node<Token> node2 = child2.getChildren().get(0);
                    root = new Node<Token>(FunctionFactory.makeSqrt());
                    Node<Token> multiply = new Node<Token>(OperatorFactory.makeMultiply());
                    multiply.addChild(node1);
                    multiply.addChild(node2);
                    multiply = evaluateConstants(multiply, true, true);
                    root.addChild(multiply);
                    root = applySquareRootRules(root); //In case it can be furthur simplified
                    return root;
                } else {
                    return root;
                }
            } else { //No changed made
                return root;
            }
        }
    }

    /**
     * Converts the given expression in postfix notation to a binary expression
     * tree.
     *
     * @param expression The expression in postfix
     * @return The root node of the tree
     * @throws IllegalArgumentException The given expression is invalid
     */
    public static Node<Token> setupTree(ArrayList<Token> expression) {
        Stack<Node<Token>> stack = new Stack<>();
        for (Token token : expression) {
            Node<Token> node = new Node<>(token);
            if (token instanceof Variable || token instanceof Number) {
                stack.push(node);
            } else if (token instanceof Operator) {
                Node<Token> node1 = stack.pop();
                Node<Token> node2 = stack.pop();
                node.addChild(node2);
                node.addChild(node1);
                stack.push(node);
            } else if (token instanceof Function) {
                Node<Token> child = stack.pop();
                node.addChild(child);
                stack.push(node);
            } else {
                //This should never be reached
                throw new IllegalArgumentException();
            }
        }
        if (stack.size() == 1) {
            return stack.pop();
        } else {
            //Invalid expression
            throw new IllegalArgumentException();
        }
    }

    /**
     * Alternative to traverse tree which won't create unnecessary brackets
     * Traverses the expression tree to return a infix expression.
     *
     * @param root The root node of the tree
     * @return The infix expression
     * @throws IllegalArgumentException Invalid tree
     */
    public static ArrayList<Token> traverseTree(Node<Token> root) {
        ArrayList<Token> toReturn = new ArrayList<>();
        if (root.getNumOfChildren() == 2) { //Tree not empty
            if (root.getContent() instanceof Operator) { //Beginning of a sub-expression
                if (root.getChildren().get(0).getContent() instanceof Operator && ((Operator) root.getContent()).getPrecedence() > ((Operator) root.getChildren().get(0).getContent()).getPrecedence()) {
                    toReturn.add(BracketFactory.makeOpenBracket());
                    toReturn.addAll(traverseTree(root.getChildren().get(0)));
                    toReturn.add(BracketFactory.makeCloseBracket());
                } else if (root.getChildren().get(0).getContent() instanceof Operator && ((Operator) root.getContent()).getPrecedence() == ((Operator) root.getChildren().get(0).getContent()).getPrecedence()) {
                    if ((root.getContent().getType() == Operator.DIVIDE || root.getContent().getType() == Operator.FRACTION) && root.getChildren().get(0).getContent().getType() == Operator.MULTIPLY) {
                        toReturn.add(BracketFactory.makeOpenBracket());
                        toReturn.addAll(traverseTree(root.getChildren().get(0)));
                        toReturn.add(BracketFactory.makeCloseBracket());
                    } else {
                        toReturn.addAll(traverseTree(root.getChildren().get(0)));
                    }
                } else {
                    toReturn.addAll(traverseTree(root.getChildren().get(0)));
                }
                toReturn.add(root.getContent());
                if (root.getChildren().get(1).getContent() instanceof Operator && ((Operator) root.getContent()).getPrecedence() > ((Operator) root.getChildren().get(1).getContent()).getPrecedence()) {
                    if (root.getContent().getType() == Operator.EXPONENT) {
                        toReturn.add(BracketFactory.makeSuperscriptOpen());
                        toReturn.addAll(traverseTree(root.getChildren().get(1)));
                        toReturn.add(BracketFactory.makeSuperscriptClose());
                    } else {
                        toReturn.add(BracketFactory.makeOpenBracket());
                        toReturn.addAll(traverseTree(root.getChildren().get(1)));
                        toReturn.add(BracketFactory.makeCloseBracket());
                    }
                } else if (root.getChildren().get(1).getContent() instanceof Operator && ((Operator) root.getContent()).getPrecedence() == ((Operator) root.getChildren().get(1).getContent()).getPrecedence()) {
                    if ((root.getContent().getType() == Operator.DIVIDE || root.getContent().getType() == Operator.FRACTION) && root.getChildren().get(1).getContent().getType() == Operator.MULTIPLY) {
                        if (root.getContent().getType() == Operator.EXPONENT) {
                            toReturn.add(BracketFactory.makeSuperscriptOpen());
                            toReturn.addAll(traverseTree(root.getChildren().get(1)));
                            toReturn.add(BracketFactory.makeSuperscriptClose());
                        } else {
                            toReturn.add(BracketFactory.makeOpenBracket());
                            toReturn.addAll(traverseTree(root.getChildren().get(1)));
                            toReturn.add(BracketFactory.makeCloseBracket());
                        }
                    } else {
                        if (root.getContent().getType() == Operator.EXPONENT) {
                            toReturn.add(BracketFactory.makeSuperscriptOpen());
                            toReturn.addAll(traverseTree(root.getChildren().get(1)));
                            toReturn.add(BracketFactory.makeSuperscriptClose());
                        } else {
                            toReturn.addAll(traverseTree(root.getChildren().get(1)));
                        }
                    }
                } else {
                    if (root.getContent().getType() == Operator.EXPONENT) {
                        toReturn.add(BracketFactory.makeSuperscriptOpen());
                        toReturn.addAll(traverseTree(root.getChildren().get(1)));
                        toReturn.add(BracketFactory.makeSuperscriptClose());
                    } else {
                        toReturn.addAll(traverseTree(root.getChildren().get(1)));
                    }
                }
            }
            return toReturn;
        } else if (root.getNumOfChildren() == 1) { //Function
            toReturn.add(root.getContent());
            toReturn.add(BracketFactory.makeOpenBracket());
            toReturn.addAll(traverseTree(root.getChildren().get(0)));
            toReturn.add(BracketFactory.makeCloseBracket());
            return toReturn;
        } else if (root.getNumOfChildren() == 0) {
            toReturn.add(root.getContent());
            return toReturn;
        } else { //This should not happen
            throw new IllegalArgumentException();
        }
    }

    /**
     * Converts an expression tree in binary tree format into a multi-branch
     * tree
     *
     * @param root The original binary tree
     * @return An equivalent multi-branch tree
     */
    public static Node<Token> convToMultiBranch(Node<Token> root) {
        Node<Token> newRoot = new Node<Token>(root.getContent());
        if (root.getContent() instanceof Operator && ((Operator) root.getContent()).isCommutative()) {
            for (int i = 0; i < root.getNumOfChildren(); i++) {
                if (root.getChildren().get(i).getContent() instanceof Operator && root.getChildren().get(i).getContent().getType() == root.getContent().getType()) {
                    newRoot.addChildren(convToMultiBranch(root.getChildren().get(i)).getChildren());
                } else if (root.getChildren().get(i).getContent() instanceof Operator && root.getChildren().get(i).getContent().getType() == Operator.SUBTRACT) {
                    newRoot.addChild(convToMultiBranch(root.getChildren().get(i)));
                } else {
                    newRoot.addChild(root.getChildren().get(i));
                }
            }
        } else if (root.getContent() instanceof Operator && ((Operator) root.getContent()).isAntiCommutative()) {
            if (root.getContent().getType() == Operator.SUBTRACT) {
                newRoot = new Node<Token>(OperatorFactory.makeAdd());
                newRoot.addChild(convToMultiBranch(root.getChildren().get(0)));
                Node<Token> temp = new Node<Token>(OperatorFactory.makeMultiply());
                temp.addChild(new Node<Token>(new Number(-1)));
                temp.addChild(convToMultiBranch(root.getChildren().get(1)));
                if (temp.getChildren().get(1).getContent() instanceof Number) {
                    temp = new Node<Token>(new Number(-1 * ((Number) temp.getChildren().get(1).getContent()).getValue()));
                } else if (temp.getChildren().get(1).getContent() instanceof Operator
                        && temp.getChildren().get(1).getContent().getType() == Operator.MULTIPLY) {
                    Node<Token> temp1 = temp.getChildren().get(1);
                    Node<Token> temp1_0 = temp1.getChildren().get(0);
                    if (temp1_0.getContent() instanceof Number) {
                        Node<Token> temp2 = new Node<Token>(new Number(-1 * ((Number) temp1_0.getContent()).getValue()));
                        temp = new Node<Token>(OperatorFactory.makeMultiply());
                        temp.addChild(temp2);
                        temp.addChild(temp1.getChildren().get(1));
                        if (temp.getChildren().get(0).getContent() instanceof Number
                                && ((Number) temp.getChildren().get(0).getContent()).getValue() == 1) {
                            temp = temp.getChildren().get(1);
                        }
                    }
                }

                newRoot.addChild(temp);
                return convToMultiBranch(newRoot);
            }
        } else if (root.getChildren().size() > 0) {
            for (int i = 0; i < root.getNumOfChildren(); i++) {
                newRoot.addChild(convToMultiBranch(root.getChildren().get(i)));
            }
        } else {
            newRoot = root;
        }
        return newRoot;
    }

    public static Node<Token> simplifyMultiBranch(Node<Token> root) {
        return addLikeTerms(groupLikeTerms(convToMultiBranch(root)));
    }

    /**
     * Converts an expression tree in multi-branch/general tree format into a
     * binary tree
     *
     * @param root The original multi-branch/general tree
     * @return An equivalent binary tree
     */
    public static Node<Token> convToBinary(Node<Token> root) {
        Node<Token> newRoot = new Node<>(root.getContent());
        if (root.getNumOfChildren() == 2) {
            newRoot.addChild(convToBinary(root.getChildren().get(0)));
            newRoot.addChild(convToBinary(root.getChildren().get(1)));
        } else if (root.getNumOfChildren() == 1) {
            if (root.getContent() instanceof Operator) {
                newRoot = root.getChildren().get(0);
            } else {
                newRoot.addChild(convToBinary(root.getChildren().get(0)));
            }
        } else if (root.getNumOfChildren() == 0) {
            if (root.getContent() instanceof Operator && root.getContent().getType() == Operator.ADD) {
                newRoot = new Node<Token>(new Number(0));
            } else if (root.getContent() instanceof Operator && root.getContent().getType() == Operator.MULTIPLY) {
                newRoot = new Node<Token>(new Number(1));
            } else if (root.getContent() instanceof Number) {
                return root;
            }
        } else {
            if (root.getContent() instanceof Operator && (root.getContent().getType() == Operator.ADD || root.getContent().getType() == Operator.MULTIPLY)) {
                Node<Token> temp;
                if (root.getNumOfChildren() % 2 == 0) {
                    temp = new Node<>(root.getContent());
                    for (int i = 0; i < root.getNumOfChildren() / 2; i++) {
                        temp.addChild(root.getChildren().get(i));
                    }
                    newRoot.addChild(convToBinary(temp));
                    temp = new Node<>(root.getContent());
                    for (int i = root.getNumOfChildren() / 2; i < root.getNumOfChildren(); i++) {
                        temp.addChild(root.getChildren().get(i));
                    }
                    newRoot.addChild(convToBinary(temp));
                } else if (root.getNumOfChildren() % 2 != 0) {
                    temp = new Node<>(root.getContent());
                    Node<Token> temp2 = new Node<>(root.getContent());
                    temp2.addChild(convToBinary(root.getChildren().get(0)));
                    temp2.addChild(convToBinary(root.getChildren().get(1)));
                    temp.addChild(temp2);
                    int midpoint = (int) Math.floor(root.getNumOfChildren() / 2);
                    for (int i = 2; i < midpoint; i++) {
                        temp.addChild(root.getChildren().get(i));
                    }
                    newRoot.addChild(convToBinary(temp));
                    temp = new Node<>(root.getContent());
                    for (int i = midpoint + 1; i < root.getNumOfChildren(); i++) {
                        temp.addChild(root.getChildren().get(i));
                    }
                    newRoot.addChild(convToBinary(temp));
                }
            }
        }
        return newRoot;
    }

    /**
     * Removes unnecessary negatives beside addition symbols and rewrites them
     * as subtractions. Examples: 1 + -2 * X -> 1 - 2*X ; -2 / -3 -> 2/3
     *
     * @param expression The expression to remove negatives
     * @return The expression with negatives removed
     */
    private static ArrayList<Token> removeNegatives(ArrayList<Token> expression) {
        for (int i = 0; i < expression.size(); i++) {
            Token t = expression.get(i);
            Token before = i > 0 ? expression.get(i - 1) : null;
            if (t instanceof Number && ((Number) t).getValue() < 0 && before != null && before instanceof Operator) { //Current token is a negative Number with Operator before it
                Token beforePrevious = i > 1 ? expression.get(i - 2) : null; //Before the previous
                Number absVal = new Number(Math.abs(((Number) expression.get(i)).getValue()));
                if (before.getType() == Operator.ADD) { //E + -N -> E - N
                    expression.set(i, absVal);
                    expression.set(i - 1, OperatorFactory.makeSubtract());
                } else if (before.getType() == Operator.SUBTRACT) { //E - -N -> E + N
                    expression.set(i, absVal);
                    expression.set(i - 1, OperatorFactory.makeAdd());
                } else if (before.getType() == Operator.MULTIPLY && beforePrevious != null && beforePrevious instanceof Number
                        && ((Number) beforePrevious).getValue() < 0) { // -N * -N -> N * N
                    Number absVal2 = new Number(Math.abs(((Number) beforePrevious).getValue()));
                    expression.set(i - 2, absVal2);
                    expression.set(i, absVal);
                } else if (before.getType() == Operator.MULTIPLY && beforePrevious != null && beforePrevious instanceof Variable
                        && ((Variable) beforePrevious).isNegative()) { // -N * -V -> N * V
                    ((Variable) beforePrevious).setNegative(false);
                    expression.set(i, absVal);
                }
            } else if (t instanceof Variable && ((Variable) t).isNegative() && before != null && before instanceof Operator) {     //Rules for variables
                Token beforePrevious = i > 1 ? expression.get(i - 2) : null; //Before the previous
                if (before.getType() == Operator.ADD) { //E + -N -> E - N
                    ((Variable) t).setNegative(false);
                    expression.set(i - 1, OperatorFactory.makeSubtract());
                } else if (before.getType() == Operator.SUBTRACT) { //E - -N -> E + N
                    ((Variable) t).setNegative(false);
                    expression.set(i - 1, OperatorFactory.makeAdd());
                } else if (before.getType() == Operator.MULTIPLY && beforePrevious != null && beforePrevious instanceof Number
                        && ((Number) beforePrevious).getValue() < 0) { // -V * -N -> V * N
                    Number absVal = new Number(Math.abs(((Number) beforePrevious).getValue()));
                    expression.set(i - 2, absVal);
                    ((Variable) t).setNegative(false);
                } else if (before.getType() == Operator.MULTIPLY && beforePrevious != null && beforePrevious instanceof Variable
                        && ((Variable) beforePrevious).isNegative()) { // -V * -V -> V * V
                    ((Variable) beforePrevious).setNegative(false);
                    ((Variable) t).setNegative(false);
                }
            }
        }
        return expression;
    }

    /**
     * Simplifies the given fraction such that there will be no common factors
     * between the numerator and the denominator.
     *
     * @param root The root of the tree of the fraction
     * @return The root of the simplified tree
     */
    private static Node<Token> simplifyFraction(Node<Token> root) {
        Node<Token> newRoot = root;
        if (root.getNumOfChildren() == 2) { //Tree not empty
            if (root.getContent() instanceof Operator && (root.getContent().getType() == Operator.DIVIDE || root.getContent().getType() == Operator.FRACTION)) { //Beginning of a sub-expression
                Node<Token> num = root.getChildren().get(0);
                Node<Token> denom = root.getChildren().get(1);
                Node<Token> temp;
                newRoot = new Node<Token>(OperatorFactory.makeDivide());
                if (num.getContent() instanceof Number && denom.getContent() instanceof Number) {
                    if (((Number) num.getContent()).getValue() % 1 == 0 && ((Number) denom.getContent()).getValue() % 1 == 0) {//checks to make sure num and denom are integers
                        double gcd = gcd(((Number) num.getContent()).getValue(), ((Number) denom.getContent()).getValue());
                        double numerator = ((Number) num.getContent()).getValue() / gcd;
                        double denominator = ((Number) denom.getContent()).getValue() / gcd;
                        num = new Node<Token>(new Number(numerator));
                        denom = new Node<Token>(new Number(denominator));
                    }
                } else if (num.getContent() instanceof Operator && num.getContent().getType() == Operator.MULTIPLY && denom.getContent() instanceof Number) {
                    double gcd;
                    double numerator = 1;
                    double denominator = ((Number) denom.getContent()).getValue();
                    int childNum;//which child branch was a number: -1 both, 0 the first, 1 the second, -2 neither
                    if (num.getChildren().get(0).getContent() instanceof Number && num.getChildren().get(1).getContent() instanceof Number) {
                        double product = ((Number) num.getChildren().get(0).getContent()).getValue() * ((Number) num.getChildren().get(1).getContent()).getValue();
                        if (product % 1 == 0) {
                            numerator = product;
                        }
                        childNum = -1;
                    } else if (num.getChildren().get(0).getContent() instanceof Number && (((Number) num.getChildren().get(0).getContent()).getValue() % 1 == 0)) {
                        numerator = ((Number) num.getChildren().get(0).getContent()).getValue();
                        childNum = 0;
                    } else if (num.getChildren().get(1).getContent() instanceof Number && (((Number) num.getChildren().get(1).getContent()).getValue() % 1 == 0)) {
                        numerator = ((Number) num.getChildren().get(1).getContent()).getValue();
                        childNum = 1;
                    } else {
                        childNum = -2;
                    }

                    gcd = gcd(numerator, denominator);
                    numerator /= gcd;
                    denominator /= gcd;
                    temp = num;
                    if (childNum == -1) {
                        num = new Node<Token>(new Number(numerator));
                        /*
                         for(int i=0; i<temp.getNumOfChildren();i++){
                         num.addChild(temp.getChildren().get(i));
                         }*/
                    } else if (childNum == 0) {
                        num = new Node<Token>(OperatorFactory.makeMultiply());
                        num.addChild(new Node<Token>(new Number(numerator)));
                        num.addChild(temp.getChildren().get(1));
                    } else if (childNum == 1) {
                        num = new Node<Token>(OperatorFactory.makeMultiply());
                        num.addChild(temp.getChildren().get(0));
                        num.addChild(new Node<Token>(new Number(numerator)));
                    }
                    temp = denom;
                    denom = new Node<Token>(new Number(denominator));
                    /*
                     for(int i=0; i<temp.getNumOfChildren();i++){
                     denom.addChild(temp.getChildren().get(i));
                     }*/
                } else if (denom.getContent() instanceof Operator && denom.getContent().getType() == Operator.MULTIPLY && num.getContent() instanceof Number) {
                    double gcd;
                    double denominator = 1;
                    double numerator = ((Number) num.getContent()).getValue();
                    double childNum;//which child branch was a number: -1 both, 0 the first, 1 the second, -2 neither
                    if (denom.getChildren().get(0).getContent() instanceof Number && denom.getChildren().get(1).getContent() instanceof Number) {
                        double product = ((Number) denom.getChildren().get(0).getContent()).getValue() * ((Number) denom.getChildren().get(1).getContent()).getValue();
                        if (product % 1 == 0) {
                            denominator = (int) product;
                        }
                        childNum = -1;
                    } else if (denom.getChildren().get(0).getContent() instanceof Number && (((Number) denom.getChildren().get(0).getContent()).getValue() % 1 == 0)) {
                        denominator = ((Number) denom.getChildren().get(0).getContent()).getValue();
                        childNum = 0;
                    } else if (denom.getChildren().get(1).getContent() instanceof Number && (((Number) denom.getChildren().get(1).getContent()).getValue() % 1 == 0)) {
                        denominator = ((Number) denom.getChildren().get(1).getContent()).getValue();
                        childNum = 1;
                    } else {
                        childNum = -2;
                    }
                    gcd = gcd(numerator, denominator);
                    numerator /= gcd;
                    denominator /= gcd;
                    temp = denom;
                    if (childNum == -1) {
                        denom = new Node<Token>(new Number(denominator));
                        /*
                         for(int i=0; i<temp.getNumOfChildren();i++){
                         num.addChild(temp.getChildren().get(i));
                         }*/
                    } else if (childNum == 0) {
                        denom = new Node<Token>(OperatorFactory.makeMultiply());
                        denom.addChild(new Node<Token>(new Number(denominator)));
                        denom.addChild(temp.getChildren().get(1));
                    } else if (childNum == 1) {
                        denom = new Node<Token>(OperatorFactory.makeMultiply());
                        denom.addChild(temp.getChildren().get(0));
                        denom.addChild(new Node<Token>(new Number(denominator)));
                    }
                    temp = num;
                    num = new Node<Token>(new Number(numerator));
                    /*
                     for(int i=0; i<temp.getNumOfChildren();i++){
                     num.addChild(temp.getChildren().get(i));
                     }*/
                } else if (denom.getContent() instanceof Operator && denom.getContent().getType() == Operator.MULTIPLY && num.getContent() instanceof Operator && num.getContent().getType() == Operator.MULTIPLY) {
                    for (int i = 0; i < denom.getNumOfChildren(); i++) {
                        for (int j = 0; j < num.getNumOfChildren(); j++) {
                            if (denom.getChildren().get(i) == num.getChildren().get(j)) {

                                temp = num;
                                for (int k = 0; k < temp.getNumOfChildren(); k++) {
                                    if (k != j) {
                                        num = temp.getChildren().get(k);
                                    }
                                }

                                temp = denom;
                                for (int k = 0; k < temp.getNumOfChildren(); k++) {
                                    if (k != i) {
                                        denom = temp.getChildren().get(k);
                                    }
                                }
                            }
                        }
                    }
                }
                newRoot.addChild(num);
                newRoot.addChild(denom);
            }
        } else if (root.getNumOfChildren() == 1) { //Function
            newRoot = new Node<>(root.getContent());
            newRoot.addChild(simplifyFraction(root.getChildren().get(0)));
        } else if (root.getNumOfChildren() == 0) {
            newRoot = root;
        } else { //This should not happen
            throw new IllegalArgumentException();
        }
        return newRoot;
    }

    /**
     * Determines the greatest common factor of two integers
     *
     * @param num   The numerator of a fraction
     * @param denom The denominator of a fraction
     * @return The greatest common factor of num and denom
     */
    private static double gcd(double num, double denom) {
        return denom == 0 ? num : gcd(denom, num % denom);
    }

    /**
     * Processes multiple fractions (such as 1/2/3) and re-writes it so that
     * there will be no more than one consecutive division. Example: 1 / 2 / 2
     * -> 1 / 4
     *
     * @param root      The root of the original expression
     * @param recursive The method to call if this rule is applied, (which method to recurse to)
     * @return The root of the simplified expression
     */
    private static Node<Token> processMultipleFractions(Node<Token> root, Command<Node<Token>, Node<Token>> recursive) {
        Node<Token> newRoot = root;
        if (root.getNumOfChildren() == 2) { //Tree not empty
            if (root.getContent() instanceof Operator && (root.getContent().getType() == Operator.DIVIDE || root.getContent().getType() == Operator.FRACTION)) { //main operation is division
                if (root.getChildren().get(1).getContent() instanceof Operator && (root.getChildren().get(1).getContent().getType() == Operator.DIVIDE || root.getChildren().get(1).getContent().getType() == Operator.FRACTION)) {//divisor(denominator) is a fraction
                    newRoot = new Node<Token>(OperatorFactory.makeMultiply());
                    newRoot.addChild(root.getChildren().get(0));
                    newRoot.addChild(reciprocal(root.getChildren().get(1)));
                    newRoot = recursive.execute(newRoot);
                } else if (root.getChildren().get(0).getContent() instanceof Operator && ((root.getChildren().get(0).getContent().getType() == Operator.DIVIDE || root.getChildren().get(0).getContent().getType() == Operator.FRACTION))) {//dividend(numerator) is a fraction
                    newRoot = new Node<Token>(OperatorFactory.makeDivide());
                    Node<Token> oldDividend = root.getChildren().get(0);
                    Node<Token> newDivisor = new Node<Token>(OperatorFactory.makeMultiply());
                    newDivisor.addChild(oldDividend.getChildren().get(1));
                    newDivisor.addChild(root.getChildren().get(1));
                    newRoot.addChild(oldDividend.getChildren().get(0));
                    newRoot.addChild(newDivisor);
                    newRoot = recursive.execute(newRoot);
                }
            }
        } else if (root.getNumOfChildren() == 1) { //Function
            newRoot = new Node<>(root.getContent());
            newRoot.addChild(processMultipleFractions(root.getChildren().get(0), recursive));
        } else if (root.getNumOfChildren() == 0) { //Just a number or a variable
            newRoot = root;
        } else { //This should not happen
            throw new IllegalArgumentException();
        }
        return newRoot;
    }

    /**
     * Converts a fraction into its reciprocal
     *
     * @param root The root of the original tree
     * @return The root of the new tree
     */
    private static Node<Token> reciprocal(Node<Token> root) {
        Node<Token> newRoot = new Node<Token>(OperatorFactory.makeDivide());
        if (root.getContent() instanceof Operator && (root.getContent().getType() == Operator.DIVIDE || root.getContent().getType() == Operator.FRACTION)) {
            newRoot.addChild(root.getChildren().get(1));
            newRoot.addChild(root.getChildren().get(0));
        } else {
            newRoot.addChild(new Node<Token>(new Number(1)));
            newRoot.addChild(root);
        }
        return newRoot;
    }

    /**
     * Multiplies two expressions if one expression is a fraction and the other
     * may or may not be one.
     *
     * @param root      The root of the original tree
     * @param recursive A Command implementation that contains the method this will recurse to if the rule has been applied
     * @return The new root of the multiplied fraction tree
     */
    public static Node<Token> multiplyFractions(Node<Token> root, Command<Node<Token>, Node<Token>> recursive) {
        Token content = root.getContent();
        if (content instanceof Operator && content.getType() == Operator.MULTIPLY) {
            Node<Token> child1 = root.getChildren().get(0);
            Node<Token> child2 = root.getChildren().get(1);
            Token childT1 = child1.getContent();
            Token childT2 = child2.getContent();
            //N1/D1 * N2/D2 -> (N1 * N2) / (D1 * D2)
            if (childT1 instanceof Operator && (childT1.getType() == Operator.FRACTION || childT1.getType() == Operator.DIVIDE)
                    && childT2 instanceof Operator && (childT2.getType() == Operator.FRACTION || childT2.getType() == Operator.DIVIDE)) {
                Node<Token> n1 = child1.getChildren().get(0);
                Node<Token> n2 = child2.getChildren().get(0);
                Node<Token> d1 = child1.getChildren().get(1);
                Node<Token> d2 = child2.getChildren().get(1);

                Node<Token> newRoot = new Node<Token>(OperatorFactory.makeFraction());
                Node<Token> newNum = new Node<Token>(OperatorFactory.makeMultiply());
                Node<Token> newDenom = new Node<Token>(OperatorFactory.makeMultiply());

                newNum.addChild(n1);
                newNum.addChild(n2);
                newNum = recursive.execute(newNum);
                newDenom.addChild(d1);
                newDenom.addChild(d2);
                newDenom = recursive.execute(newDenom);

                newRoot.addChild(newNum);
                newRoot.addChild(newDenom);
                newRoot = simplifyFraction(newRoot);
                return newRoot;
                //N/D * E -> (E * N) / D
            } else if (childT1 instanceof Operator && (childT1.getType() == Operator.FRACTION || childT1.getType() == Operator.DIVIDE)) {
                Node<Token> n = child1.getChildren().get(0);
                Node<Token> d = child1.getChildren().get(1);
                Node<Token> e = child2;

                Node<Token> newRoot = new Node<Token>(OperatorFactory.makeFraction());
                Node<Token> newNum = new Node<Token>(OperatorFactory.makeMultiply());

                newNum.addChild(n);
                newNum.addChild(e);
                newNum = recursive.execute(newNum);

                newRoot.addChild(newNum);
                newRoot.addChild(d);
                newRoot = simplifyFraction(newRoot);
                return newRoot;
                //E * N/D -> (E * N) / D
            } else if (childT2 instanceof Operator && (childT2.getType() == Operator.FRACTION || childT2.getType() == Operator.DIVIDE)) {
                Node<Token> n = child2.getChildren().get(0);
                Node<Token> d = child2.getChildren().get(1);
                Node<Token> e = child1;

                Node<Token> newRoot = new Node<Token>(OperatorFactory.makeFraction());
                Node<Token> newNum = new Node<Token>(OperatorFactory.makeMultiply());

                newNum.addChild(n);
                newNum.addChild(e);
                newNum = recursive.execute(newNum);

                newRoot.addChild(newNum);
                newRoot.addChild(d);
                newRoot = simplifyFraction(newRoot);
                return newRoot;
            }
        }
        return root;
    }

    /**
     * Applies multiplication rules for multiplying terms/constants with
     * polynomials. Examples of such include 2 * (x + 1) -> 2 * x + 2 * 1, as
     * well as x * (x + 1) -> x * x + 1 * x. It should also work with division
     * as well, but treats all division as multiplication of fractions. Example:
     * (x + 1) / 2 -> 1/2 * x + 1/2 * 1 -> 1/2 * x + 1/2.
     *
     * @param root The root of the original tree
     * @return The new root of the new tree
     */
    private static Node<Token> multiplyTerms(Node<Token> root) {//TODO:Add support for negatives and functions
        Node<Token> newRoot = new Node<Token>(OperatorFactory.makeAdd());
        if (root.getContent() instanceof Operator && (root.getContent().getType() == Operator.DIVIDE || root.getContent().getType() == Operator.FRACTION)) {
            if (root.getChildren().get(0).getNumOfChildren() == 2) {
                Token child = root.getChildren().get(1).getContent();
                if (child instanceof Number) {
                    //Rewrites E1 / E2 to E1 * 1/ E2
                    Node<Token> multiply = new Node<Token>(OperatorFactory.makeMultiply());
                    multiply.addChild(new Node<>(child));
                    Node<Token> divide = new Node<Token>(OperatorFactory.makeDivide());
                    divide.addChild(new Node<Token>(new Number(1)));
                    divide.addChild(root.getChildren().get(1));
                    multiply.addChild(divide);
                    return multiplyTerms(multiply);
                }
            }
            return root;
        } else if (root.getContent() instanceof Operator && root.getContent().getType() == Operator.MULTIPLY) {
            if ((root.getChildren().get(0).getNumOfChildren() == 0 || root.getChildren().get(0).getNumOfChildren() == 1) && root.getChildren().get(1).getNumOfChildren() == 2
                    && root.getChildren().get(1).getContent() instanceof Operator && (root.getChildren().get(1).getContent().getType() == Operator.ADD
                    || root.getChildren().get(1).getContent().getType() == Operator.SUBTRACT)) { //Ensures that one child is a single number and the other is a polynomial
                Node<Token> temp = root.getChildren().get(1);
                Node<Token> n1 = new Node<Token>(OperatorFactory.makeMultiply());
                n1.addChild(root.getChildren().get(0));
                n1.addChild(temp.getChildren().get(0));
                Node<Token> n2 = new Node<Token>(OperatorFactory.makeMultiply());
                n2.addChild(root.getChildren().get(0));
                n2.addChild(temp.getChildren().get(1));
                newRoot.addChild(n1);
                newRoot.addChild(n2);
                newRoot = expand(newRoot);
                return newRoot;
            } else if ((root.getChildren().get(1).getNumOfChildren() == 0 || root.getChildren().get(1).getNumOfChildren() == 1) && root.getChildren().get(0).getNumOfChildren() == 2
                    && root.getChildren().get(0).getContent() instanceof Operator && (root.getChildren().get(0).getContent().getType() == Operator.ADD
                    || root.getChildren().get(0).getContent().getType() == Operator.SUBTRACT)) { //Ensures that one child is a single number and the other is a polynomial) {
                Node<Token> temp = root.getChildren().get(0);
                Node<Token> n1 = new Node<Token>(OperatorFactory.makeMultiply());
                n1.addChild(temp.getChildren().get(0));
                n1.addChild(root.getChildren().get(1));
                Node<Token> n2 = new Node<Token>(OperatorFactory.makeMultiply());
                n2.addChild(temp.getChildren().get(1));
                n2.addChild(root.getChildren().get(1));
                newRoot.addChild(n1);
                newRoot.addChild(n2);
                newRoot = expand(newRoot);
                return newRoot;
            } else if (root.getChildren().get(0).getNumOfChildren() == 2 && root.getChildren().get(1).getNumOfChildren() == 2) {
                return multiplyPolynomials(root);
            }
        }
        return root;
    }

    /**
     * Applies multiplication rules for multiple polynomials. These include
     * expressions such as (x - 1) * (x - 2) -> x * (x - 1) - 2 * (x - 2). It
     * should also treat divisions as multiplications of fractions.
     *
     * @param root The root of the original subtree
     * @return The new root of the new subtree
     */
    private static Node<Token> multiplyPolynomials(Node<Token> root) {
        Token rootToken = root.getContent();
        if (rootToken instanceof Operator && rootToken.getType() == Operator.MULTIPLY) { //Multiplication at top
            Node<Token> n1 = root.getChildren().get(0);
            Node<Token> n2 = root.getChildren().get(1);
            if (n1.getContent() instanceof Operator && (n1.getContent().getType() == Operator.ADD || n1.getContent().getType() == Operator.SUBTRACT) //(T +- T) * (T +- T)
                    && n2.getContent() instanceof Operator && (n2.getContent().getType() == Operator.ADD || n2.getContent().getType() == Operator.SUBTRACT)) {
                Node<Token> head = n1; //Keeps track on where the expression is being read from
                Node<Token> expression = n2;
                Node<Token> newExpression = new Node<>(head.getContent()); //Tracks the start of the expression
                Node<Token> newHead = newExpression; //Tracks where the current head for building the expression is
                boolean done = false;
                do {
                    Node<Token> child1 = head.getChildren().get(0);
                    Node<Token> child2 = head.getChildren().get(1);
                    Token t1 = child1.getContent();
                    Token t2 = child2.getContent();
                    if (t1 instanceof Operator && (t1.getType() == Operator.ADD || t1.getType() == Operator.SUBTRACT)
                            && t2 instanceof Operator && (t2.getType() == Operator.ADD || t2.getType() == Operator.SUBTRACT)) { //((T1A +- T1B) + (T2A +- T2B))
                        Node<Token> n = new Node<>(head.getContent());
                        //Rewrites ((T1 +- T2) O (T3 +- T4)) * (E) -> (T1 +- T2) * (E) O (T3 +- T4) * (E) (Distributive property)
                        Node<Token> multiply1 = new Node<Token>(OperatorFactory.makeMultiply());
                        Node<Token> multiply2 = new Node<Token>(OperatorFactory.makeMultiply());
                        multiply1.addChild(expression);
                        multiply1.addChild(child1);
                        multiply2.addChild(expression);
                        multiply2.addChild(child2);
                        //Calls itself again to expand the polynomials
                        n.addChild(multiplyPolynomials(multiply2));
                        n.addChild(multiplyPolynomials(multiply1));
                        //Replaces newHead with n
                        if (newHead.getParent() != null) {
                            Node parent = newHead.getParent();
                            parent.getChildren().remove(newHead);
                            parent.addChild(n);
                        } else {
                            newExpression = n;
                        }
                    } else if (t1 instanceof Operator && (t1.getType() == Operator.ADD || t1.getType() == Operator.SUBTRACT)) { // (T1A +- T1B) +- T2
                        //Makes the subtree T2 * E
                        Node<Token> multiply = new Node<Token>(OperatorFactory.makeMultiply());
                        multiply.addChild(expression);
                        multiply.addChild(child2);
                        head = child1;
                        newHead.addChild(multiply);
                        Node<Token> futureHead = new Node<>(head.getContent());
                        newHead.addChild(futureHead);
                        newHead = futureHead;
                    } else if (t2 instanceof Operator && (t2.getType() == Operator.ADD || t2.getType() == Operator.SUBTRACT)) { // (T2A +- T2B) +- T1
                        //Makes the subtree T1 * E
                        Node<Token> multiply = new Node<Token>(OperatorFactory.makeMultiply());
                        multiply.addChild(expression);
                        multiply.addChild(child1);
                        head = child2;
                        newHead.addChild(multiply);
                        Node<Token> futureHead = new Node<>(head.getContent());
                        newHead.addChild(futureHead);
                        newHead = futureHead;
                    } else {
                        Node<Token> multiply1 = new Node<Token>(OperatorFactory.makeMultiply());
                        Node<Token> multiply2 = new Node<Token>(OperatorFactory.makeMultiply());
                        multiply1.addChild(child1);
                        multiply1.addChild(expression);
                        multiply2.addChild(child2);
                        multiply2.addChild(expression);
                        newHead.addChild(multiply2);
                        newHead.addChild(multiply1);
                        root = newExpression;
                        root = expand(root); //Re-evaluates the expression as the entirety has changed
                        done = true;
                    }
                } while (!done);
            }
        }
        return root;
    }

    /**
     * Removes redundant multiplications of one, such as 1 * x -> x.
     *
     * @param expression The expression to remove the multiplications of one
     * @return The expression with multiplications of one removed
     */
    private static ArrayList<Token> removeMultiplicationsOfOne(ArrayList<Token> expression) {
        ArrayList<Token> newExpression = new ArrayList<>();
        for (int i = 0; i < expression.size(); i++) {
            //Safely assigns the Token variables to do pattern searching
            Token before = i - 1 < 0 ? null : expression.get(i - 1);
            Token after = i + 1 > expression.size() - 1 ? null : expression.get(i + 1);
            Token current = expression.get(i);
            if (current instanceof Operator && current.getType() == Operator.MULTIPLY) { //Multiplication token found
                if (before instanceof Number) { //1 * E -> E
                    if (((Number) before).getValue() == 1) {
                        newExpression.remove(before);
                        //Removes the 1 Token and deos not add the * Token to the new expression
                    } else if (((Number) before).getValue() == -1) {
                        newExpression.remove(before); //Replaces -1 * with -
                        newExpression.add(DigitFactory.makeNegative());
                    } else {
                        newExpression.add(current);
                    }
                } else if (after instanceof Number) { //E * 1 -> E
                    if (((Number) after).getValue() == 1) {
                        i++; //Skips the current * and the next 1
                    } else if (((Number) after).getValue() == -1) {
                        int j = i - 1;
                        while (j >= 0 && (newExpression.get(j) instanceof Operator || newExpression.get(j) instanceof Function
                                || (j > 0 && newExpression.get(j - 1) instanceof Bracket && (newExpression.get(j - 1).getType() == Bracket.OPEN || newExpression.get(j - 1).getType() == Bracket.DENOM_OPEN
                                || newExpression.get(j - 1).getType() == Bracket.NUM_OPEN || newExpression.get(j - 1).getType() == Bracket.SUPERSCRIPT_OPEN)))) {
                            j--;
                        }
                        newExpression.add(j, DigitFactory.makeNegative()); //Adds a negative in front
                        i++; //Skips adding the * and the -1
                    } else {
                        newExpression.add(current);
                    }
                } else {
                    newExpression.add(current);
                }
            } else {
                newExpression.add(current);
            }
        }
        return newExpression;
    }

    /**
     * Removes redundant divisions of one, such as x / 1 -> x.
     *
     * @param expression The expression to remove the divisions of one
     * @return The expression with divisions of one removed
     */
    private static ArrayList<Token> removeDivisionsOfOne(ArrayList<Token> expression) {
        ArrayList<Token> newExpression = new ArrayList<>();
        for (int i = 0; i < expression.size(); i++) {
            //Safely assigns the Token variables to do pattern searching
            Token before = i - 1 < 0 ? null : expression.get(i - 1);
            Token after = i + 1 > expression.size() - 1 ? null : expression.get(i + 1);
            Token current = expression.get(i);
            if (current instanceof Operator && (current.getType() == Operator.DIVIDE || current.getType() == Operator.FRACTION)) { //Div / Frac found
                if (after instanceof Number) { //E / 1 -> E
                    if (((Number) after).getValue() == 1) {
                        i++; //Skips the next 1
                    } else if (((Number) after).getValue() == -1) {
                        int j = i - 1;
                        while (j >= 0 && (newExpression.get(j) instanceof Operator || newExpression.get(j) instanceof Function
                                || (j > 0 && newExpression.get(j - 1) instanceof Bracket && (newExpression.get(j - 1).getType() == Bracket.OPEN || newExpression.get(j - 1).getType() == Bracket.DENOM_OPEN
                                || newExpression.get(j - 1).getType() == Bracket.NUM_OPEN || newExpression.get(j - 1).getType() == Bracket.SUPERSCRIPT_OPEN)))) {
                            j--;
                        }
                        newExpression.add(j, DigitFactory.makeNegative()); //Adds a negative in front
                        i++; //Skips adding the * and the -1
                    } else {
                        newExpression.add(current);
                    }
                } else {
                    newExpression.add(current);
                }
            } else {
                newExpression.add(current);
            }
        }
        return newExpression;
    }

    /**
     * Groups like terms so that all like terms will be children of the same
     * parent
     *
     * @param root The root node of the expression tree (in general tree form)
     * @return Returns the root of a new expression tree with grouped like terms
     */
    public static Node<Token> groupLikeTerms(Node<Token> root) {
        Node<Token> newRoot = new Node<>(root.getContent());
        Node<Token> temp = new Node<>(root.getContent());
        Node<Token> first;
        for (int i = 0; i < root.getNumOfChildren(); i++) {
            first = root.getChildren().get(i).copy();
            for (int j = 0; j < root.getNumOfChildren(); j++) {
                if (areLikeTerms(first, root.getChildren().get(j))) {
                    temp.addChild(root.getChildren().get(j));
                    root.delChild(j);
                    j--;
                }
            }
            newRoot.addChild(temp);
            temp = new Node<Token>(OperatorFactory.makeAdd());
        }
        if (newRoot.getNumOfChildren() == 1) {
            return newRoot.getChildren().get(0);
        } else {
            return newRoot;
        }
    }

    /**
     * Checks if two nodes represent like terms
     *
     * @param term1 the first term
     * @param term2 the second term
     * @return Returns true if term1 and term2 are like terms
     */
    private static boolean areLikeTerms(Node<Token> term1, Node<Token> term2) {
        if (term1.getContent() instanceof Operator && term1.getContent().getType() == Operator.MULTIPLY
                && term2.getContent() instanceof Operator && term2.getContent().getType() == Operator.MULTIPLY) {
            for (int i = 0; i < term1.getNumOfChildren(); i++) {
                for (int j = 0; j < term2.getNumOfChildren(); j++) {
                    if (traverseTree(term1.getChildren().get(i)).equals(traverseTree(term2.getChildren().get(j)))) {
                        return true;
                    }
                }
            }
        } else if (term1.getContent() instanceof Operator && term1.getContent().getType() == Operator.MULTIPLY) {
            for (int i = 0; i < term1.getNumOfChildren(); i++) {
                if (traverseTree(term1.getChildren().get(i)).equals(traverseTree(term2))) {
                    return true;
                }
            }
        } else if (term2.getContent() instanceof Operator && term2.getContent().getType() == Operator.MULTIPLY) {
            for (int i = 0; i < term2.getNumOfChildren(); i++) {
                if (traverseTree(term2.getChildren().get(i)).equals(traverseTree(term1))) {
                    return true;
                }
            }
        } else if (term1.getContent() instanceof Number && term2.getContent() instanceof Number) {
            return true;
        }
        return term1.getContent() instanceof Variable && term2.getContent() instanceof Variable
                && term1.getContent().getType() == term2.getContent().getType();
    }

    /**
     * Adds the two given like terms together
     *
     * @param term1 The root of the first term
     * @param term2 The root of the second term
     * @return The root of the expression with the like terms added
     */
    private static Node<Token> add2LikeTerms(Node<Token> term1, Node<Token> term2) {
        Node<Token> newRoot = null;
        if (term1.getContent() instanceof Operator && term1.getContent().getType() == Operator.MULTIPLY) {
            newRoot = new Node<Token>(OperatorFactory.makeMultiply());
            int e1Pos = -1, e2Pos = -1;
            Node<Token> temp1 = null;
            Node<Token> temp2 = null;
            Node<Token> temp = new Node<Token>(OperatorFactory.makeAdd());
            Node<Token> exp = new Node<Token>(new Number(1));
            for (int i = 0; i < term1.getNumOfChildren(); i++) {
                for (int j = 0; j < term2.getNumOfChildren(); j++) {
                    if (traverseTree(term1.getChildren().get(i)).equals(traverseTree(term2.getChildren().get(j))) && i != j) {
                        exp = term1.getChildren().get(i);
                        e1Pos = i;
                        e2Pos = j;
                        i += term1.getNumOfChildren();
                        j += term2.getNumOfChildren();//stops loop
                    }
                }
            }
            if (e1Pos < 0 || e2Pos < 0) {
                throw new IllegalArgumentException("Not like terms");
            }
            for (int i = 0; i < term1.getNumOfChildren(); i++) {
                if (i != e1Pos) {
                    temp1 = new Node<Token>(OperatorFactory.makeMultiply());
                    temp1.addChild(term1.getChildren().get(i));
                }
            }
            for (int i = 0; i < term2.getNumOfChildren(); i++) {
                if (i != e2Pos) {
                    temp2 = new Node<Token>(OperatorFactory.makeMultiply());
                    temp2.addChild(term2.getChildren().get(i));
                }
            }
            temp.addChild(temp1);
            temp.addChild(temp2);
            newRoot.addChild(temp);
            newRoot.addChild(exp);
        } else if (term1.getContent() instanceof Number && term2.getContent() instanceof Number) {
            double sum = ((Number) term1.getContent()).getValue() + ((Number) term2.getContent()).getValue();
            newRoot = new Node<Token>(new Number(sum));
        } else if (term1.getNumOfChildren() == 1) {
            return add2LikeTerms(term1.getChildren().get(0), term2);
        } else if (term2.getNumOfChildren() == 1) {
            return add2LikeTerms(term1, term2.getChildren().get(0));
        }
        return newRoot;
    }

    /**
     * Adds together the like terms in an expression. Example: x + x -> 2 * x or
     * 2 * x ^ 3 + 3 * x ^ 3 -> 5 * x ^ 3.
     *
     * @param root The root of a multi-branch/general tree representing the
     *             expression
     * @return The root of the expression with the like terms added
     */
    public static Node<Token> addLikeTerms(Node<Token> root) {//TODO: rewrite -> just add adjacent terms recursively
        Node<Token> newRoot = new Node<>(root.getContent());
        Node<Token> first = root.getChildren().get(0);
        Node<Token> rest = new Node<>(root.getContent());
        rest.addChildren(new ArrayList<>(root.getChildren().subList(1, root.getNumOfChildren())));
        if (root.getContent() instanceof Operator && root.getContent().getType() == Operator.ADD) {
            if (rest.getNumOfChildren() == 1) {
                return rest.getChildren().get(0);
            } else if (rest.getNumOfChildren() == 2) {
                return add2LikeTerms(first, add2LikeTerms(rest.getChildren().get(0), rest.getChildren().get(1)));
            } else {
                return add2LikeTerms(first, addLikeTerms(rest));
            }
        } else {
            return root;
        }
    }

    /**
     * Adds all fractions in a given expression together.
     *
     * @param root The expression with the fractions to sum
     * @return The resulting expression
     */
    public static Node<Token> addLikeFractions(Node<Token> root) {
        Token t = root.getContent();
        if (t instanceof Operator && (t.getType() == Operator.ADD || t.getType() == Operator.SUBTRACT)) {
            Node<Token> o = root;
            Node<Token> child1 = o.getChildren().get(0);
            Node<Token> child2 = o.getChildren().get(1);
            Token content1 = child1.getContent();
            Token content2 = child2.getContent();
            //CASE 1: N1/D1 +- N2/D2 -> (N1*D2+N2*D1)/(D1*D2)
            if (content1 instanceof Operator && (content1.getType() == Operator.DIVIDE || content1.getType() == Operator.FRACTION)
                    && content2 instanceof Operator && (content2.getType() == Operator.DIVIDE || content2.getType() == Operator.FRACTION)) {
                Node<Token> num1 = child1.getChildren().get(0);
                Node<Token> num2 = child2.getChildren().get(0);
                Node<Token> denom1 = child1.getChildren().get(1);
                Node<Token> denom2 = child2.getChildren().get(1);

                Node<Token> newFrac = new Node<Token>(OperatorFactory.makeFraction());
                Node<Token> newNum = new Node<Token>(o.getContent());
                Node<Token> newDenom = new Node<Token>(OperatorFactory.makeMultiply());

                Node<Token> mult1 = new Node<Token>(OperatorFactory.makeMultiply());
                Node<Token> mult2 = new Node<Token>(OperatorFactory.makeMultiply());
                mult1.addChild(num1);
                mult1.addChild(denom2);
                mult1 = simplify(mult1);
                mult2.addChild(num2);
                mult2.addChild(denom1);
                mult2 = simplify(mult2);
                newNum.addChild(mult1);
                newNum.addChild(mult2);
                newNum = simplify(newNum);

                newDenom.addChild(denom1);
                newDenom.addChild(denom2);
                newDenom = simplify(newDenom);

                newFrac.addChild(newNum);
                newFrac.addChild(newDenom);
                newFrac = simplifyFraction(newFrac);
                return newFrac;
                //CASE 2: N/D+-E -> (N+-E*D)/D
            } else if (content1 instanceof Operator && (content1.getType() == Operator.DIVIDE || content1.getType() == Operator.FRACTION)) {
                Node<Token> frac = child1;
                Node<Token> expression = child2;
                Node<Token> num = frac.getChildren().get(0);
                Node<Token> denom = frac.getChildren().get(1);

                Node<Token> newFrac = new Node<Token>(OperatorFactory.makeFraction());
                Node<Token> newNum = new Node<>(o.getContent());
                Node<Token> mult = new Node<Token>(OperatorFactory.makeMultiply());

                mult.addChild(expression);
                mult.addChild(denom);
                mult = simplify(mult);
                newNum.addChild(num);
                newNum.addChild(mult);
                newNum = simplify(newNum);

                newFrac.addChild(newNum);
                newFrac.addChild(denom);
                newFrac = simplifyFraction(newFrac);
                return newFrac;
                //CASE 3: E+-N/D -> (E*D+-N)/D
            } else if (content2 instanceof Operator && (content2.getType() == Operator.DIVIDE || content2.getType() == Operator.FRACTION)) {
                Node<Token> frac = child2;
                Node<Token> expression = child1;
                Node<Token> num = frac.getChildren().get(0);
                Node<Token> denom = frac.getChildren().get(1);

                Node<Token> newFrac = new Node<Token>(OperatorFactory.makeFraction());
                Node<Token> newNum = new Node<>(o.getContent());
                Node<Token> mult = new Node<Token>(OperatorFactory.makeMultiply());

                mult.addChild(expression);
                mult.addChild(denom);
                mult = simplify(mult);
                newNum.addChild(num);
                newNum.addChild(mult);
                newNum = simplify(newNum);

                newFrac.addChild(newNum);
                newFrac.addChild(denom);
                newFrac = simplifyFraction(newFrac);
                return newFrac;
            } else {
                return root;
            }
        }
        return root;
    }

    /**
     * Multiplies and divides variables and transforms them into powers instead.
     * Example: x * x -> x ^ 2.
     *
     * @param root The root of the original subtree
     * @return The new subtree
     */
    private static Node<Token> multiplyVariables(Node<Token> root) {
        Token current = root.getContent();
        if (current instanceof Operator) {
            Operator o = (Operator) current;
            Node<Token> childNode1 = root.getChildren().get(0);
            Node<Token> childNode2 = root.getChildren().get(1);
            Token child1 = childNode1.getContent();
            Token child2 = childNode2.getContent();
            if (child1 instanceof Variable && child2 instanceof Variable) { //VARVAR
                Variable v1 = (Variable) child1;
                Variable v2 = (Variable) child2;
                if (v1.getType() == v2.getType()) { //Checks to make sure it's the same type
                    if (o.getType() == Operator.MULTIPLY) {
                        //Applies rule V * V -> V ^ 2
                        Node<Token> newRoot = new Node<Token>(OperatorFactory.makeExponent());
                        newRoot.addChild(new Node<Token>(v1));
                        newRoot.addChild(new Node<Token>(new Number(2)));
                        return newRoot;
                    } else if (o.getType() == Operator.DIVIDE || o.getType() == Operator.FRACTION) {
                        //Applies rule V/V -> 1
                        return new Node<Token>(new Number(1));
                    } else { //No rules apply
                        return root;
                    }
                } else { //Not the same variable; rule deos not apply
                    return root;
                }
            } else if (child1 instanceof Operator && child1.getType() == Operator.EXPONENT && child2 instanceof Operator
                    && child2.getType() == Operator.EXPONENT) { //EXPEXP
                Node<Token> node1 = childNode1.getChildren().get(0);
                Node<Token> node2 = childNode2.getChildren().get(1);
                Token exp1Child2 = childNode1.getChildren().get(0).getContent();
                Token exp2Child2 = childNode2.getChildren().get(1).getContent();
                Variable v1 = exp1Child2 instanceof Variable ? (Variable) exp1Child2 : null;
                Variable v2 = exp2Child2 instanceof Variable ? (Variable) exp2Child2 : null;
                if (v1 != null && v2 != null && v1.getType() == v2.getType()) { //Rule applies
                    if (o.getType() == Operator.DIVIDE || o.getType() == Operator.FRACTION) {
                        //Applies rule (V ^ E1) / (V ^ E2) -> V ^ (E1 - E2)
                        Node<Token> head = new Node<Token>(OperatorFactory.makeExponent());
                        Node<Token> subtract = new Node<Token>(OperatorFactory.makeSubtract());
                        subtract.addChild(node1);
                        subtract.addChild(node2);
                        head.addChild(new Node<Token>(v1));
                        head.addChild(subtract);
                        return head;
                    } else if (o.getType() == Operator.MULTIPLY) {
                        //Applies rule (V ^ E1) * (V ^ E2) -> V ^ (E1 + E2)
                        Node<Token> head = new Node<Token>(OperatorFactory.makeExponent());
                        Node<Token> add = new Node<Token>(OperatorFactory.makeAdd());
                        add.addChild(node1);
                        add.addChild(node2);
                        head.addChild(new Node<Token>(v1));
                        head.addChild(add);
                        return head;
                    } else { //No rules apply
                        return root;
                    }
                } else { //No rules apply
                    return root;
                }
            } else if (child1 instanceof Variable && child2 instanceof Operator && ((Operator) child2).getType() == Operator.EXPONENT) { //VAREXP
                Variable v = (Variable) child1;
                Node<Token> exponent = childNode2;
                Token t = exponent.getChildren().get(0).getContent();
                if (t instanceof Variable && v.getType() == t.getType()) { //Rule applies
                    Node<Token> node = exponent.getChildren().get(1);
                    if (o.getType() == Operator.DIVIDE || o.getType() == Operator.FRACTION) {
                        //Applies rule V / V ^ E -> V ^ (1 - E)
                        Node<Token> head = new Node<Token>(OperatorFactory.makeExponent());
                        head.addChild(new Node<Token>(v));
                        Node<Token> subtract = new Node<Token>(OperatorFactory.makeSubtract());
                        subtract.addChild(new Node<Token>(new Number(1)));
                        subtract.addChild(node);
                        head.addChild(subtract);
                        head = expand(head);
                        return head;
                    } else if (o.getType() == Operator.MULTIPLY) {
                        //Applies rule V * V ^ E -> V ^ (1 + E)
                        Node<Token> head = new Node<Token>(OperatorFactory.makeExponent());
                        head.addChild(new Node<Token>(v));
                        Node<Token> add = new Node<Token>(OperatorFactory.makeAdd());
                        add.addChild(new Node<Token>(new Number(1)));
                        add.addChild(node);
                        head.addChild(add);
                        head = expand(head);
                        return head;
                    } else { //No rules applies
                        return root;
                    }
                } else { //Rule deos not apply
                    return root;
                }
            } else if (child2 instanceof Variable && child1 instanceof Operator && ((Operator) child1).getType() == Operator.EXPONENT) { //EXPVAR
                Variable v = (Variable) child2;
                Node<Token> exponent = childNode1;
                Token t = (exponent.getChildren().get(0)).getContent();
                if (t instanceof Variable && v.getType() == t.getType()) { //Rule applies
                    Node<Token> node = exponent.getChildren().get(1);
                    if (o.getType() == Operator.DIVIDE || o.getType() == Operator.FRACTION) {
                        //Applies rule V / V ^ E -> V ^ (1 - E)
                        Node<Token> head = new Node<Token>(OperatorFactory.makeExponent());
                        head.addChild(new Node<Token>(v));
                        Node<Token> subtract = new Node<Token>(OperatorFactory.makeSubtract());
                        subtract.addChild(new Node<Token>(new Number(1)));
                        subtract.addChild(node);
                        head.addChild(subtract);
                        head = expand(head);
                        return head;
                    } else if (o.getType() == Operator.MULTIPLY) {
                        //Applies rule V * V ^ E -> V ^ (1 + E)
                        Node<Token> head = new Node<Token>(OperatorFactory.makeExponent());
                        head.addChild(new Node<Token>(v));
                        Node<Token> add = new Node<Token>(OperatorFactory.makeAdd());
                        add.addChild(new Node<Token>(new Number(1)));
                        add.addChild(node);
                        head.addChild(add);
                        head = expand(head);
                        return head;
                    } else { //No rules applies
                        return root;
                    }
                } else { //Rule deos not apply
                    return root;
                }
            } else { //No rules apply
                return root;
            }
        } else {
            return root;
        }
    }

    /**
     * Applies powers to polynomial expression and transforms them into
     * multiplications. Example: (x + 1) ^ 2 -> (x + 1) * (x + 1). This also
     * takes into consideration powers of zeros and negative exponents as well.
     *
     * @param root The root of the original expression
     * @return The new root of the expression
     */
    private static Node<Token> applyPowers(Node<Token> root) {
        Token current = root.getContent();
        Token child1 = root.getChildren().get(1).getContent();
        Token child2 = root.getChildren().get(0).getContent();
        if (current instanceof Operator && current.getType() == Operator.EXPONENT && child1 instanceof Number && child2 instanceof Operator) { //O ^ N rule applies
            double n = ((Number) child1).getValue();
            Node<Token> expression = root.getChildren().get(0);
            if (n % 1 != 0) {
                //TODO: Must be an integer; FIND A WAY TO HAVE THIS WORKING
                throw new IllegalArgumentException("Power must be an integer");
            }
            if (n < 0) {
                Node<Token> head = new Node<Token>(OperatorFactory.makeDivide());
                head.addChild(new Node<Token>(new Number(1)));
                n *= -1;
                if (n == 1) {
                    head.addChild(expression);
                } else { //n > 1
                    n -= 2;
                    Node<Token> head2 = new Node<Token>(OperatorFactory.makeMultiply());
                    head2.addChild(expression); //NOTE: No need to clone
                    head2.addChild(expression);
                    while (n > 0) {
                        Node<Token> newHead = new Node<Token>(OperatorFactory.makeMultiply());
                        newHead.addChild(expression);
                        newHead.addChild(head2);
                        head2 = newHead;
                        n--;
                    }
                    head.addChild(head2);
                }
                return head;
            } else if (n == 0) {
                return new Node<Token>(new Number(1)); //Anything ^ 0 = 1
            } else if (n == 1) {
                return expression; //Simply removes the ^ 1
            } else { //n > 2
                n -= 2;
                Node<Token> head = new Node<Token>(OperatorFactory.makeMultiply());
                head.addChild(expression); //NOTE: No need to clone
                head.addChild(expression);
                while (n > 0) {
                    Node<Token> newHead = new Node<Token>(OperatorFactory.makeMultiply());
                    newHead.addChild(expression);
                    newHead.addChild(head);
                    head = newHead;
                    n--;
                }
                return head;
            }
        } else {
            return root;
        }
    }

    /**
     * Uses the distributive property to group similar types of token that are
     * multiplied or divided together to set them up for multiplyTerms(),
     * evaluateConstants(), multiplyPolynomials() and multiplyVariables().
     *
     * @param root The root of the original function
     * @return The root of the new function
     */
    private static Node<Token> applyDistributiveProperty(Node<Token> root) {
        ArrayList<Node<Token>> multiplications = new ArrayList<>();
        ArrayList<Node> divisions = new ArrayList<>();
        Stack<Node<Token>> stack = new Stack<>(); //Things to read for multiplications / divisions
        stack.push(root);
        //Fills the lists of multiplications and divisions
        do {
            Node<Token> readHead = stack.pop();
            if (readHead.getContent() instanceof Operator) {
                Operator o = (Operator) readHead.getContent();
                if (o.getType() == Operator.MULTIPLY) {
                    Node<Token> child1 = readHead.getChildren().get(0);
                    Node<Token> child2 = readHead.getChildren().get(1);
                    Token t1 = child1.getContent();
                    Token t2 = child2.getContent();
                    if (t1 instanceof Operator && (t1.getType() == Operator.MULTIPLY || (t1.getType() == Operator.DIVIDE || t1.getType() == Operator.FRACTION))
                            && t2 instanceof Operator && (t2.getType() == Operator.MULTIPLY || (t2.getType() == Operator.DIVIDE || t2.getType() == Operator.FRACTION))) { //Both children have multiplication / divisions
                        stack.push(child1);
                        stack.push(child2);
                    } else if (t1 instanceof Operator && (t1.getType() == Operator.MULTIPLY || (t1.getType() == Operator.DIVIDE || t1.getType() == Operator.FRACTION))) { //More multiplications / divisions on T1
                        multiplications.add(child2);
                        stack.push(child1);
                    } else if (t2 instanceof Operator && (t2.getType() == Operator.MULTIPLY || (t2.getType() == Operator.DIVIDE || t2.getType() == Operator.FRACTION))) { //More multiplications / divisions on T2
                        multiplications.add(child1);
                        stack.push(child2);
                    } else { //No more multiplications or divisions
                        multiplications.add(child1);
                        multiplications.add(child2);
                    }
                } else if (o.getType() == Operator.DIVIDE || o.getType() == Operator.FRACTION) { //TODO: APPLY SPECIAL CASES FOR DIVISIONS
                    Node<Token> child1 = readHead.getChildren().get(0);
                    Node<Token> child2 = readHead.getChildren().get(1);
                    Token t1 = child1.getContent();
                    Token t2 = child2.getContent();
                    if (t1 instanceof Operator && (t1.getType() == Operator.MULTIPLY || (t1.getType() == Operator.DIVIDE || t1.getType() == Operator.FRACTION))
                            && t2 instanceof Operator && (t2.getType() == Operator.MULTIPLY || (t2.getType() == Operator.DIVIDE || t2.getType() == Operator.FRACTION))) { //Both children have multiplication / divisions
                    } else if (t1 instanceof Operator && (t1.getType() == Operator.MULTIPLY || (t1.getType() == Operator.DIVIDE || t1.getType() == Operator.FRACTION))) { //More multiplications / divisions on T1
                    } else if (t2 instanceof Operator && (t2.getType() == Operator.MULTIPLY || (t2.getType() == Operator.DIVIDE || t2.getType() == Operator.FRACTION))) { //More multiplications / divisions on T2
                    } else { //No more multiplications or divisions
                    }
                } else {
                    //Do nothing; NOT a multiplication or division
                }
            } else {
                ///Do nothing; NOT an operator
            }
        } while (!stack.isEmpty());
        if (!multiplications.isEmpty() && !divisions.isEmpty()) {
            //Now builds the sorts the multiplications according to type
            ArrayList<Node<Token>> variables = new ArrayList<>();
            ArrayList<Node<Token>> constants = new ArrayList<>();
            ArrayList<Node<Token>> polynomials = new ArrayList<>();
            ArrayList<Node<Token>> others = new ArrayList<>();
            for (Node<Token> node : multiplications) {
                if (node.getContent() instanceof Variable) {
                    variables.add(node);
                } else if (node.getContent() instanceof Number) {
                    constants.add(node);
                } else if (node.getContent() instanceof Operator) {
                    polynomials.add(node);
                } else { //Group the others together
                    others.add(node);
                }
            }
        } else {
            return root; //No multiplications of divisions found
        }
        return root;
    }
}
