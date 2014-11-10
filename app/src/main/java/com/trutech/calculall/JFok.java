package com.trutech.calculall;
import java.util.ArrayList;
import java.util.Stack;

/**
 * @version 0.6.0
 */
public class JFok {

    /**
     * Simplify and factor the given mathematical expression. There should be no
     * variables in the expression given.
     *
     * @param expression The expression to simplify
     * @return The fully simplified expression
     */
    public static ArrayList<Token> simplifyExpression(ArrayList<Token> expression) {
        //Sets up the tree
        expression = Utility.condenseDigits(expression);
        expression = Utility.setupExpression(expression);
        expression = Utility.convertToReversePolish(expression);
        Node<Token> root = setupTree(expression);
        //This is where the magic happens
        root = simplify(root);
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
        expression = Utility.condenseDigits(expression);
        expression = Utility.setupExpression(expression);
        expression = Utility.convertToReversePolish(expression);
        Node<Token> root = setupTree(expression);
        //This is where the magic happens
        root = expand(root);
        //Converts it back to human-readable form
        expression = traverseTree(root);
        expression = cleanupExpression(expression);
        return expression;
    }

    /**
     * Expands the given binary expression tree by applying rules.
     *
     * @param root The root of the tree
     * @return The root of the new expanded tree
     */
    private static Node<Token> expand(Node<Token> root) {
        if (root.getNumOfChildren() > 0) { //Not a leaf
            Node n1 = root.getNumOfChildren() > 0 ? root.getChildren().get(0) : null;
            Node n2 = root.getNumOfChildren() > 1 ? root.getChildren().get(1) : null;
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
        root = processMultipleFractions(root);
        root = applyDistributiveProperty(root);
        root = multiplyPolynomials(root);
        root = multiplyTerms(root);
        //Rules to be applied before constants are evaluated
        root = applyCommutativeProperty(root);
        root = evaluateConstants(root, false);
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
        root = processMultipleFractions(root);
        root = applySquareRootRules(root);
        root = applyCommutativeProperty(root);
        //Rules to be applied before constants are evaluated
        root = evaluateConstants(root, true);
        //Rules to be applied after constants are evaluated
        root = simplifyFraction(root);
        return root;
    }

    /**
     * Cleans up the expression and renders it more human-readable by removing
     * redundancies.
     *
     * @param expression The expression to clean up
     * @return The cleaned up expression
     */
    private static ArrayList<Token> cleanupExpression(ArrayList<Token> expression) {
        //expression = removeRedundantBrackets(expression);
        expression = removeNegatives(expression);
        expression = removeMultiplicationsOfOne(expression);
        return expression;
    }

    /**
     * Simplifies the given binary expression tree by applying rules.
     *
     * @param root The root of the tree
     * @return The root of the new expanded tree
     */
    private static Node<Token> simplify(Node<Token> root) {
        if (root.getNumOfChildren() > 0) { //Not a leaf
            Node n1 = root.getNumOfChildren() > 0 ? root.getChildren().get(0) : null;
            Node n2 = root.getNumOfChildren() > 1 ? root.getChildren().get(1) : null;
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
            Token child1 = (Token) childNode1.getContent();
            Token child2 = (Token) childNode2.getContent();
            Operator expression; //Operator of the interior subclass
            Number n1;
            Number n2;
            Token expressionChild1;
            Token expressionChild2;
            Node<Token> expressionNode1;
            Node<Token> expressionNode2;
            if (child1 instanceof Operator && (((Operator) child1).getType() == Operator.ADD || ((Operator) child1).getType() == Operator.SUBTRACT) && child2 instanceof Number) {
                expression = (Operator) child1;
                n1 = (Number) child2;
                expressionNode1 = (Node) childNode1.getChildren().get(0);
                expressionNode2 = (Node) childNode1.getChildren().get(1);
                expressionChild1 = expressionNode1.getContent();
                expressionChild2 = expressionNode2.getContent();
            } else if (child2 instanceof Operator && (((Operator) child2).getType() == Operator.ADD || ((Operator) child2).getType() == Operator.SUBTRACT) && child1 instanceof Number) {
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
                    root = new Node(OperatorFactory.makeAdd());
                    //Creates the new subtree (N1 + N2)
                    Node addition = new Node(OperatorFactory.makeAdd());
                    addition.addChild(new Node(n1));
                    addition.addChild(new Node(n2));
                    //Evaluates the expression
                    addition = evaluateConstants(addition, false);
                    //Adds both the the root and sends it off
                    root.addChild(interiorExpression);
                    root.addChild(addition);
                    return root;
                } else if (expression.getType() == Operator.SUBTRACT) { //(E - N1) + N2 -> E + (N2 - N1)
                    root = new Node(OperatorFactory.makeAdd());
                    //Creates the new subtree (N1 + N2)
                    Node subtract = new Node(OperatorFactory.makeSubtract());
                    subtract.addChild(new Node(n1));
                    subtract.addChild(new Node(n2));
                    //Evaluates the expression
                    subtract = evaluateConstants(subtract, false);
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
                    root = new Node(OperatorFactory.makeAdd());
                    //Creates the new subtree (N1 + N2)
                    Node subtract = new Node(OperatorFactory.makeSubtract());
                    subtract.addChild(new Node(n2));
                    subtract.addChild(new Node(n1));
                    //Evaluates the expression
                    subtract = evaluateConstants(subtract, false);
                    //Adds both the the root and sends it off
                    root.addChild(interiorExpression);
                    root.addChild(subtract);
                    return root;
                } else if (expression.getType() == Operator.SUBTRACT) { //(E - N1) - N2 -> E - (N2 + N1)
                    root = new Node(OperatorFactory.makeSubtract());
                    //Creates the new subtree (N1 + N2)
                    Node addition = new Node(OperatorFactory.makeAdd());
                    addition.addChild(new Node(n1));
                    addition.addChild(new Node(n2));
                    //Evaluates the expression
                    addition = evaluateConstants(addition, false);
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
     * @param root The root of the subtree
     * @param exactValue If true, this will not evaluate division and square
     * roots
     * @return The new root of the subtree
     * @throws IllegalArgumentException Invalid Expression
     */
    private static Node<Token> evaluateConstants(Node<Token> root, boolean exactValue) {
        Token token = root.getContent();
        //Evaluates this node
        if (root.getNumOfChildren() == 2) { //Operator
            //Now checks this node after the subtrees has been checked
            Token t1 = (Token) root.getChildren().get(0).getContent();
            Token t2 = (Token) root.getChildren().get(1).getContent();
            if (t1 instanceof Number && t2 instanceof Number && (((Operator) token).getType() != Operator.DIVIDE || !exactValue)) { //Rule applies (deos not do divisions)
                //Both numbers, rule can be applied
                double result = ((Operator) token).operate(((Number) t1).getValue(), ((Number) t2).getValue());
                Node node = new Node(new Number(result));
                return node;
            } else { //Rule deos not apply
                return root;
            }
        } else if (root.getNumOfChildren() == 1) { //Function
            Token child = (Token) root.getChildren().get(0).getContent();
            if (child instanceof Number && (((Function) token).getType() != Function.SQRT || !exactValue)) {
                double result = ((Function) token).perform(((Number) child).getValue());
                Node node = new Node(new Number(result));
                return node;
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
        Token token = root.getContent();
        if (token instanceof Function && ((Function) token).getType() == Function.SQRT) { //A square root; rules may apply
            Token child = (Token) root.getChildren().get(0).getContent();
            if (child instanceof Number) {
                double value = ((Number) child).getValue();
                //Rules specificly for Numbers
                double result = ((Function) token).perform(value);
                if (result % 1 == 0) { //The result is a integer
                    return new Node(new Number(result));
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
                            root = new Node(OperatorFactory.makeMultiply());
                            Node squareRoot = new Node(FunctionFactory.makeSqrt());
                            Node outsideNode = new Node(new Number(outside));
                            Node insideNode = new Node(new Number(inside));
                            squareRoot.addChild(insideNode);
                            root.addChild(squareRoot);
                            root.addChild(outsideNode);
                        }
                        return root;
                    } else {
                        //Decimal inside the square roots; just calculates it
                        return new Node(new Number(((Function) token).perform(value)));
                    }
                }
            } else { //Root of a variable / expression
                //TODO: Add support to this
                return root;
            }
        } else {
            //Condenses square roots multiplying each other
            if (token instanceof Operator && ((Operator) token).getType() == Operator.MULTIPLY) {
                Node child1 = root.getChildren().get(0);
                Node child2 = root.getChildren().get(1);
                if (child1.getContent() instanceof Function && ((Function) child1.getContent()).getType() == Function.SQRT && child2.getContent() instanceof Function
                        && ((Function) child2.getContent()).getType() == Function.SQRT) { //Two square roots under a multiplication; Rule can be applied
                    Node node1 = (Node) child1.getChildren().get(0);
                    Node node2 = (Node) child2.getChildren().get(0);
                    root = new Node(FunctionFactory.makeSqrt());
                    Node multiply = new Node(OperatorFactory.makeMultiply());
                    multiply.addChild(node1);
                    multiply.addChild(node2);
                    multiply = evaluateConstants(multiply, true);
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
        Stack<Node> stack = new Stack();
        for (Token token : expression) {
            Node node = new Node(token);
            if (token instanceof Variable || token instanceof Number) {
                stack.push(node);
            } else if (token instanceof Operator) {
                Node node1 = stack.pop();
                Node node2 = stack.pop();
                node.addChild(node2);
                node.addChild(node1);
                stack.push(node);
            } else if (token instanceof Function) {
                Node child = stack.pop();
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
        ArrayList<Token> toReturn = new ArrayList();
        if (root.getNumOfChildren() == 2) { //Tree not empty
            if (root.getContent() instanceof Operator) { //Beginning of a sub-expression
                if (root.getChildren().get(0).getContent() instanceof Operator && ((Operator) root.getContent()).getPrecedence() > ((Operator) root.getChildren().get(0).getContent()).getPrecedence()) {
                    toReturn.add(BracketFactory.createOpenBracket());
                    toReturn.addAll(traverseTree(root.getChildren().get(0)));
                    toReturn.add(BracketFactory.createCloseBracket());
                } else if (root.getChildren().get(0).getContent() instanceof Operator && ((Operator) root.getContent()).getPrecedence() == ((Operator) root.getChildren().get(0).getContent()).getPrecedence()) {
                    if (((Operator) root.getContent()).getType() == Operator.DIVIDE && ((Operator) root.getChildren().get(0).getContent()).getType() == Operator.MULTIPLY) {
                        toReturn.add(BracketFactory.createOpenBracket());
                        toReturn.addAll(traverseTree(root.getChildren().get(0)));
                        toReturn.add(BracketFactory.createCloseBracket());
                    } else {
                        toReturn.addAll(traverseTree(root.getChildren().get(0)));
                    }
                } else {
                    toReturn.addAll(traverseTree(root.getChildren().get(0)));
                }
                toReturn.add(root.getContent());
                if (root.getChildren().get(1).getContent() instanceof Operator && ((Operator) root.getContent()).getPrecedence() > ((Operator) root.getChildren().get(1).getContent()).getPrecedence()) {
                    toReturn.add(BracketFactory.createOpenBracket());
                    toReturn.addAll(traverseTree(root.getChildren().get(1)));
                    toReturn.add(BracketFactory.createCloseBracket());
                } else if (root.getChildren().get(1).getContent() instanceof Operator && ((Operator) root.getContent()).getPrecedence() == ((Operator) root.getChildren().get(1).getContent()).getPrecedence()) {
                    if (((Operator) root.getContent()).getType() == Operator.DIVIDE && ((Operator) root.getChildren().get(1).getContent()).getType() == Operator.MULTIPLY) {
                        toReturn.add(BracketFactory.createOpenBracket());
                        toReturn.addAll(traverseTree(root.getChildren().get(1)));
                        toReturn.add(BracketFactory.createCloseBracket());
                    } else {
                        toReturn.addAll(traverseTree(root.getChildren().get(1)));
                    }
                } else {
                    toReturn.addAll(traverseTree(root.getChildren().get(1)));
                }
            }
            return toReturn;
        } else if (root.getNumOfChildren() == 1) { //Function
            toReturn.add(root.getContent());
            toReturn.add(BracketFactory.createOpenBracket());
            toReturn.addAll(traverseTree(root.getChildren().get(0)));
            toReturn.add(BracketFactory.createCloseBracket());
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
                if (root.getChildren().get(i).getContent() instanceof Operator && ((Operator) root.getChildren().get(i).getContent()).getType() == ((Operator) root.getContent()).getType()) {
                    newRoot.addChildren(convToMultiBranch(root.getChildren().get(i)).getChildren());
                } else if (root.getChildren().get(i).getContent() instanceof Operator && ((Operator) root.getChildren().get(i).getContent()).getType() == Operator.SUBTRACT) {
                    newRoot.addChild(convToMultiBranch(root.getChildren().get(i)));
                } else {
                    newRoot.addChild(root.getChildren().get(i));
                }
            }
        } else if (root.getContent() instanceof Operator && ((Operator) root.getContent()).isAntiCommutative()) {
            if (((Operator) root.getContent()).getType() == Operator.SUBTRACT) {
                newRoot = new Node<Token>(OperatorFactory.makeAdd());
                newRoot.addChild(convToMultiBranch(root.getChildren().get(0)));
                Node<Token> temp = new Node<Token>(OperatorFactory.makeMultiply());
                temp.addChild(new Node(new Number(-1)));
                temp.addChild(convToMultiBranch(root.getChildren().get(1)));
                if (temp.getChildren().get(1).getContent() instanceof Number) {
                    temp = new Node(new Number(-1 * ((Number) temp.getChildren().get(1).getContent()).getValue()));
                } else if (temp.getChildren().get(1).getContent() instanceof Operator
                        && ((Operator) temp.getChildren().get(1).getContent()).getType() == Operator.MULTIPLY) {
                    Node<Token> temp1 = temp.getChildren().get(1);
                    Node<Token> temp1_0 = temp1.getChildren().get(0);
                    if (temp1_0.getContent() instanceof Number) {
                        Node<Token> temp2 = new Node(new Number(-1 * ((Number) temp1_0.getContent()).getValue()));
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
        Node<Token> newRoot = new Node(root.getContent());
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
            if (root.getContent() instanceof Operator && ((Operator) root.getContent()).getType() == Operator.ADD) {
                newRoot = new Node(new Number(0));
            } else if (root.getContent() instanceof Operator && ((Operator) root.getContent()).getType() == Operator.MULTIPLY) {
                newRoot = new Node(new Number(1));
            } else if (root.getContent() instanceof Number) {
                return root;
            }
        } else {
            if (root.getContent() instanceof Operator && (((Operator) root.getContent()).getType() == Operator.ADD || ((Operator) root.getContent()).getType() == Operator.MULTIPLY)) {
                Node<Token> temp;
                if (root.getNumOfChildren() % 2 == 0) {
                    temp = new Node(root.getContent());
                    for (int i = 0; i < root.getNumOfChildren() / 2; i++) {
                        temp.addChild(root.getChildren().get(i));
                    }
                    newRoot.addChild(convToBinary(temp));
                    temp = new Node(root.getContent());
                    for (int i = root.getNumOfChildren() / 2; i < root.getNumOfChildren(); i++) {
                        temp.addChild(root.getChildren().get(i));
                    }
                    newRoot.addChild(convToBinary(temp));
                } else if (root.getNumOfChildren() % 2 != 0) {
                    temp = new Node(root.getContent());
                    Node<Token> temp2 = new Node(root.getContent());
                    temp2.addChild(convToBinary(root.getChildren().get(0)));
                    temp2.addChild(convToBinary(root.getChildren().get(1)));
                    temp.addChild(temp2);
                    int midpoint = (int) Math.floor(root.getNumOfChildren() / 2);
                    for (int i = 2; i < midpoint; i++) {
                        temp.addChild(root.getChildren().get(i));
                    }
                    newRoot.addChild(convToBinary(temp));
                    temp = new Node(root.getContent());
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
     * as subtractions. Example: 1 + -2 * X -> 1 - 2*X.
     *
     * @param expression The expression to remove negatives
     * @return The expression with negatives removed
     */
    private static ArrayList<Token> removeNegatives(ArrayList<Token> expression) {
        for (int i = 0; i < expression.size(); i++) {
            if (expression.get(i) instanceof Number && ((Number) expression.get(i)).getValue() < 0) {
                if (i > 0 && expression.get(i - 1) != null && expression.get(i - 1) instanceof Operator
                        && (((Operator) expression.get(i - 1)).getType() == Operator.ADD || ((Operator) expression.get(i - 1)).getType() == Operator.SUBTRACT)) {
                    Number absVal = new Number(Math.abs(((Number) expression.get(i)).getValue()));
                    expression.set(i, absVal);
                    if (((Operator) expression.get(i - 1)).getType() == Operator.ADD) {
                        expression.set(i - 1, OperatorFactory.makeSubtract());
                    } else if (((Operator) expression.get(i - 1)).getType() == Operator.SUBTRACT) {
                        expression.set(i - 1, OperatorFactory.makeAdd());
                    }
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
            if (root.getContent() instanceof Operator && ((Operator) root.getContent()).getType() == Operator.DIVIDE) { //Beginning of a sub-expression
                Node<Token> num = root.getChildren().get(0);
                Node<Token> denom = root.getChildren().get(1);
                Node<Token> temp;
                newRoot = new Node<Token>(OperatorFactory.makeDivide());
                if (num.getContent() instanceof Number && denom.getContent() instanceof Number) {
                    if (((Number) num.getContent()).getValue() % 1 == 0 && ((Number) denom.getContent()).getValue() % 1 == 0) {//checks to make sure num and denom are integers
                        int gcd = gcd((int) ((Number) num.getContent()).getValue(), (int) ((Number) denom.getContent()).getValue());
                        int numerator = (int) ((Number) num.getContent()).getValue() / gcd;
                        int denominator = (int) ((Number) denom.getContent()).getValue() / gcd;
                        temp = num;
                        num = new Node<Token>(new Number(numerator));
                        /*
                         for(int i=0; i<temp.getNumOfChildren();i++){
                         num.addChild(temp.getChildren().get(i));
                         }*/
                        temp = denom;
                        denom = new Node<Token>(new Number(denominator));
                        /*
                         for(int i=0; i<temp.getNumOfChildren();i++){
                         denom.addChild(temp.getChildren().get(i));
                         }*/
                    }
                } else if (num.getContent() instanceof Operator && ((Operator) num.getContent()).getType() == Operator.MULTIPLY && denom.getContent() instanceof Number) {
                    int gcd;
                    int numerator = 1;
                    int denominator = (int) ((Number) denom.getContent()).getValue();
                    int childNum;//which child branch was a number: -1 both, 0 the first, 1 the second, -2 neither
                    if (num.getChildren().get(0).getContent() instanceof Number && num.getChildren().get(1).getContent() instanceof Number) {
                        double product = ((Number) num.getChildren().get(0).getContent()).getValue() * ((Number) num.getChildren().get(1).getContent()).getValue();
                        if (product % 1 == 0) {
                            numerator = (int) product;
                        }
                        childNum = -1;
                    } else if (num.getChildren().get(0).getContent() instanceof Number && (((Number) num.getChildren().get(0).getContent()).getValue() % 1 == 0)) {
                        numerator = (int) ((Number) num.getChildren().get(0).getContent()).getValue();
                        childNum = 0;
                    } else if (num.getChildren().get(1).getContent() instanceof Number && (((Number) num.getChildren().get(1).getContent()).getValue() % 1 == 0)) {
                        numerator = (int) ((Number) num.getChildren().get(1).getContent()).getValue();
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
                } else if (denom.getContent() instanceof Operator && ((Operator) denom.getContent()).getType() == Operator.MULTIPLY && num.getContent() instanceof Number) {
                    int gcd;
                    int denominator = 1;
                    int numerator = (int) ((Number) num.getContent()).getValue();
                    int childNum;//which child branch was a number: -1 both, 0 the first, 1 the second, -2 neither
                    if (denom.getChildren().get(0).getContent() instanceof Number && denom.getChildren().get(1).getContent() instanceof Number) {
                        double product = ((Number) denom.getChildren().get(0).getContent()).getValue() * ((Number) denom.getChildren().get(1).getContent()).getValue();
                        if (product % 1 == 0) {
                            denominator = (int) product;
                        }
                        childNum = -1;
                    } else if (denom.getChildren().get(0).getContent() instanceof Number && (((Number) denom.getChildren().get(0).getContent()).getValue() % 1 == 0)) {
                        denominator = (int) ((Number) denom.getChildren().get(0).getContent()).getValue();
                        childNum = 0;
                    } else if (denom.getChildren().get(1).getContent() instanceof Number && (((Number) denom.getChildren().get(1).getContent()).getValue() % 1 == 0)) {
                        denominator = (int) ((Number) denom.getChildren().get(1).getContent()).getValue();
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
                } else if (denom.getContent() instanceof Operator && ((Operator) denom.getContent()).getType() == Operator.MULTIPLY && num.getContent() instanceof Operator && ((Operator) num.getContent()).getType() == Operator.MULTIPLY) {
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
            newRoot = new Node<Token>(root.getContent());
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
     * @param num The numerator of a fraction
     * @param denom The denominator of a fraction
     * @return The greatest common factor of num and denom
     */
    private static int gcd(int num, int denom) {
        return denom == 0 ? num : gcd(denom, num % denom);
    }

    /**
     * Processes multiple fractions (such as 1/2/3) and re-writes it so that
     * there will be no more than one consecutive division. Example: 1 / 2 / 2
     * -> 1 / 4
     *
     * @param root The root of the original expression
     * @return The root of the simplified expression
     */
    private static Node<Token> processMultipleFractions(Node<Token> root) {
        Node<Token> newRoot = root;
        if (root.getNumOfChildren() == 2) { //Tree not empty
            if (root.getContent() instanceof Operator && ((Operator) root.getContent()).getType() == Operator.DIVIDE) { //main operation is division
                if (root.getChildren().get(1).getContent() instanceof Operator && ((Operator) root.getChildren().get(1).getContent()).getType() == Operator.DIVIDE) {//divisor(denominator) is a fraction
                    newRoot = new Node<Token>(OperatorFactory.makeMultiply());
                    newRoot.addChild(root.getChildren().get(0));
                    newRoot.addChild(reciprocal(root.getChildren().get(1)));
                    //newRoot = multiplyFractions(newRoot);
                } else if (root.getChildren().get(0).getContent() instanceof Operator && ((Operator) root.getChildren().get(0).getContent()).getType() == Operator.DIVIDE) {//dividend(numerator) is a fraction
                    newRoot = new Node<Token>(OperatorFactory.makeDivide());
                    Node<Token> oldDividend = root.getChildren().get(0);
                    Node<Token> newDivisor = new Node<Token>(OperatorFactory.makeMultiply());
                    newDivisor.addChild(oldDividend.getChildren().get(1));
                    newDivisor.addChild(root.getChildren().get(1));
                    newRoot.addChild(oldDividend.getChildren().get(0));
                    newRoot.addChild(newDivisor);
                }
            }
        } else if (root.getNumOfChildren() == 1) { //Function
            newRoot = new Node<Token>(root.getContent());
            newRoot.addChild(processMultipleFractions(root.getChildren().get(0)));
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
        if (root.getContent() instanceof Operator && ((Operator) root.getContent()).getType() == Operator.DIVIDE) {
            newRoot.addChild(root.getChildren().get(1));
            newRoot.addChild(root.getChildren().get(0));
        } else {
            newRoot.addChild(new Node<Token>(new Number(1)));
            newRoot.addChild(root);
        }
        return newRoot;
    }

    /**
     * Multiplies fractions
     *
     * @param root The root of the original tree
     * @return The root of the new tree
     */
    private static Node<Token> multiplyFractions(Node<Token> root) {
        Node<Token> newRoot = new Node<Token>(OperatorFactory.makeDivide());
        if (root.getContent() instanceof Operator && ((Operator) root.getContent()).getType() == Operator.MULTIPLY) {
            Node<Token> left;
            Node<Token> right;
            if (root.getChildren().get(0).getContent() instanceof Operator && ((Operator) root.getChildren().get(0).getContent()).getType() == Operator.MULTIPLY && (root.getChildren().get(1).getContent() instanceof Operator && ((Operator) root.getChildren().get(1).getContent()).getType() == Operator.MULTIPLY)) {
                left = multiplyFractions(root.getChildren().get(0));
                right = multiplyFractions(root.getChildren().get(1));
            } else if (root.getChildren().get(1).getContent() instanceof Operator && ((Operator) root.getChildren().get(1).getContent()).getType() == Operator.MULTIPLY) {
                left = root.getChildren().get(0);
                right = multiplyFractions(root.getChildren().get(1));
            } else if (root.getChildren().get(0).getContent() instanceof Operator && ((Operator) root.getChildren().get(0).getContent()).getType() == Operator.MULTIPLY) {
                right = root.getChildren().get(1);
                left = multiplyFractions(root.getChildren().get(0));
            } else {
                left = root.getChildren().get(0);
                right = root.getChildren().get(1);
            }
            Node<Token> numerator = new Node<Token>(null);
            Node<Token> denominator = new Node<Token>(null);
            if (isFraction(left) && isFraction(right)) {
                if (left.getChildren().get(0).getContent() instanceof Number && right.getChildren().get(0).getContent() instanceof Number) {
                    if (((Number) left.getChildren().get(0).getContent()).getValue() == 1 && ((Number) right.getChildren().get(0).getContent()).getValue() == 1) {
                        numerator = new Node<Token>(new Number(1));
                    } else if (((Number) right.getChildren().get(0).getContent()).getValue() == 1) {
                        numerator = left.getChildren().get(0);
                    } else if (((Number) left.getChildren().get(0).getContent()).getValue() == 1) {
                        numerator = right.getChildren().get(0);
                    }
                } else if (left.getChildren().get(0).getContent() instanceof Number) {
                    if (((Number) left.getChildren().get(0).getContent()).getValue() == 1) {
                        numerator = right.getChildren().get(0);
                    } else {
                        numerator = new Node<Token>(OperatorFactory.makeMultiply());
                        numerator.addChild(left.getChildren().get(0));
                        numerator.addChild(right.getChildren().get(0));
                    }
                } else if (right.getChildren().get(0).getContent() instanceof Number) {
                    if (((Number) right.getChildren().get(0).getContent()).getValue() == 1) {
                        numerator = left.getChildren().get(0);
                    } else {
                        numerator = new Node<Token>(OperatorFactory.makeMultiply());
                        numerator.addChild(left.getChildren().get(0));
                        numerator.addChild(right.getChildren().get(0));
                    }
                } else {
                    numerator = new Node<Token>(OperatorFactory.makeMultiply());
                    numerator.addChild(left.getChildren().get(0));
                    numerator.addChild(right.getChildren().get(0));
                }

                if (left.getChildren().get(1).getContent() instanceof Number && right.getChildren().get(1).getContent() instanceof Number) {
                    if (((Number) left.getChildren().get(1).getContent()).getValue() == 1 && ((Number) right.getChildren().get(1).getContent()).getValue() == 1) {
                        denominator = new Node<Token>(new Number(1));
                    } else if (((Number) right.getChildren().get(1).getContent()).getValue() == 1) {
                        denominator = left.getChildren().get(1);
                    } else if (((Number) left.getChildren().get(1).getContent()).getValue() == 1) {
                        denominator = right.getChildren().get(1);
                    }
                } else if (left.getChildren().get(1).getContent() instanceof Number) {
                    if (((Number) left.getChildren().get(1).getContent()).getValue() == 1) {
                        denominator = right.getChildren().get(1);
                    } else {
                        denominator = new Node<Token>(OperatorFactory.makeMultiply());
                        denominator.addChild(left.getChildren().get(1));
                        denominator.addChild(right.getChildren().get(1));
                    }
                } else if (right.getChildren().get(1).getContent() instanceof Number) {
                    if (((Number) right.getChildren().get(1).getContent()).getValue() == 1) {
                        denominator = left.getChildren().get(1);
                    } else {
                        denominator = new Node<Token>(OperatorFactory.makeMultiply());
                        denominator.addChild(left.getChildren().get(1));
                        denominator.addChild(right.getChildren().get(1));
                    }
                } else {
                    denominator = new Node<Token>(OperatorFactory.makeMultiply());
                    denominator.addChild(left.getChildren().get(1));
                    denominator.addChild(right.getChildren().get(1));
                }

            } else if (isFraction(left)) {
                if (right.getContent() instanceof Number && ((Number) right.getContent()).getValue() == 1) {
                    return left;
                } else {
                    numerator = new Node<Token>(OperatorFactory.makeMultiply());
                    numerator.addChild(left.getChildren().get(0));
                    numerator.addChild(right);
                    denominator = left.getChildren().get(1);
                }

            } else if (isFraction(right)) {
                if (left.getContent() instanceof Number && ((Number) left.getContent()).getValue() == 1) {
                    return right;
                } else {
                    numerator = new Node<Token>(OperatorFactory.makeMultiply());
                    numerator.addChild(left);
                    numerator.addChild(right.getChildren().get(0));
                    denominator = right.getChildren().get(1);
                }
            } else {
                numerator = new Node<Token>(OperatorFactory.makeMultiply());
                numerator.addChild(left);
                numerator.addChild(right);
                denominator = new Node<Token>(new Number(1));
            }

            if ((numerator.getContent() instanceof Number && ((Number) numerator.getContent()).getValue() == 1)) {
                newRoot.addChild(new Node<Token>(new Number(1)));
                newRoot.addChild(denominator);
            } else if ((denominator.getContent() instanceof Number && ((Number) denominator.getContent()).getValue() == 1)) {
                return numerator;
            } else {
                newRoot.addChild(numerator);
                newRoot.addChild(denominator);
            }
        }

        return newRoot;
    }

    /**
     * Determines whether or not the input is a fraction
     *
     * @param root The root of the original tree
     * @return Returns true if the input is a fraction
     */
    private static boolean isFraction(Node<Token> root) {
        if (root.getContent() instanceof Operator && ((Operator) root.getContent()).getType() == Operator.DIVIDE) {
            return true;
        } else if (root.getContent() instanceof Number) {
            return false;
        } else {
            if (root.getNumOfChildren() == 2) {
                return isFraction(root.getChildren().get(0)) || isFraction(root.getChildren().get(1));
            } else {
                return false;
            }
        }
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
        if (root.getContent() instanceof Operator && ((Operator) root.getContent()).getType() == Operator.DIVIDE) {
            if (root.getChildren().get(0).getNumOfChildren() == 2) {
                Token child = (Token) root.getChildren().get(1).getContent();
                if (child instanceof Number) {
                    //Rewrites E1 / E2 to E1 * 1/ E2
                    Node multiply = new Node(OperatorFactory.makeMultiply());
                    multiply.addChild(new Node(child));
                    Node divide = new Node(OperatorFactory.makeDivide());
                    divide.addChild(new Node(new Number(1)));
                    divide.addChild(root.getChildren().get(1));
                    multiply.addChild(divide);
                    return multiplyTerms(multiply);
                }
            }
            return root;
        } else if (root.getContent() instanceof Operator && ((Operator) root.getContent()).getType() == Operator.MULTIPLY) {
            if ((root.getChildren().get(0).getNumOfChildren() == 0 || root.getChildren().get(0).getNumOfChildren() == 1) && root.getChildren().get(1).getNumOfChildren() == 2
                    && root.getChildren().get(1).getContent() instanceof Operator && (((Operator) root.getChildren().get(1).getContent()).getType() == Operator.ADD
                    || ((Operator) root.getChildren().get(1).getContent()).getType() == Operator.SUBTRACT)) { //Ensures that one child is a single number and the other is a polynomial
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
                    && root.getChildren().get(0).getContent() instanceof Operator && (((Operator) root.getChildren().get(0).getContent()).getType() == Operator.ADD
                    || ((Operator) root.getChildren().get(0).getContent()).getType() == Operator.SUBTRACT)) { //Ensures that one child is a single number and the other is a polynomial) {
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
        if (rootToken instanceof Operator && ((Operator) rootToken).getType() == Operator.MULTIPLY) { //Multiplication at top
            Node n1 = root.getChildren().get(0);
            Node n2 = root.getChildren().get(1);
            if (n1.getContent() instanceof Operator && (((Operator) n1.getContent()).getType() == Operator.ADD || ((Operator) n1.getContent()).getType() == Operator.SUBTRACT) //(T +- T) * (T +- T)
                    && n2.getContent() instanceof Operator && (((Operator) n2.getContent()).getType() == Operator.ADD || ((Operator) n2.getContent()).getType() == Operator.SUBTRACT)) {
                Node head = n1; //Keeps track on where the expression is being read from
                Node expression = n2;
                Node newExpression = new Node(head.getContent()); //Tracks the start of the expression
                Node newHead = newExpression; //Tracks where the current head for building the expression is
                boolean done = false;
                do {
                    Node child1 = (Node) head.getChildren().get(0);
                    Node child2 = (Node) head.getChildren().get(1);
                    Token t1 = (Token) child1.getContent();
                    Token t2 = (Token) child2.getContent();
                    if (t1 instanceof Operator && (((Operator) t1).getType() == Operator.ADD || ((Operator) t1).getType() == Operator.SUBTRACT)
                            && t2 instanceof Operator && (((Operator) t2).getType() == Operator.ADD || ((Operator) t2).getType() == Operator.SUBTRACT)) { //((T1A +- T1B) + (T2A +- T2B))
                        Node n = new Node((Operator) head.getContent());
                        //Rewrites ((T1 +- T2) O (T3 +- T4)) * (E) -> (T1 +- T2) * (E) O (T3 +- T4) * (E) (Distributive property)
                        Node multiply1 = new Node(OperatorFactory.makeMultiply());
                        Node multiply2 = new Node(OperatorFactory.makeMultiply());
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
                    } else if (t1 instanceof Operator && (((Operator) t1).getType() == Operator.ADD || ((Operator) t1).getType() == Operator.SUBTRACT)) { // (T1A +- T1B) +- T2
                        //Makes the subtree T2 * E
                        Node multiply = new Node(OperatorFactory.makeMultiply());
                        multiply.addChild(expression);
                        multiply.addChild(child2);
                        head = child1;
                        newHead.addChild(multiply);
                        Node futureHead = new Node(head.getContent());
                        newHead.addChild(futureHead);
                        newHead = futureHead;
                    } else if (t2 instanceof Operator && (((Operator) t2).getType() == Operator.ADD || ((Operator) t2).getType() == Operator.SUBTRACT)) { // (T2A +- T2B) +- T1
                        //Makes the subtree T1 * E
                        Node multiply = new Node(OperatorFactory.makeMultiply());
                        multiply.addChild(expression);
                        multiply.addChild(child1);
                        head = child2;
                        newHead.addChild(multiply);
                        Node futureHead = new Node(head.getContent());
                        newHead.addChild(futureHead);
                        newHead = futureHead;
                    } else {
                        Node multiply1 = new Node(OperatorFactory.makeMultiply());
                        Node multiply2 = new Node(OperatorFactory.makeMultiply());
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
        ArrayList<Token> newExpression = new ArrayList();
        for (int i = 0; i < expression.size(); i++) {
            //Safely assigns the Token variables to do pattern searching
            Token before = i - 1 < 0 ? null : expression.get(i - 1);
            Token after = i + 1 > expression.size() - 1 ? null : expression.get(i + 1);
            Token current = expression.get(i);
            if (current instanceof Operator && ((Operator) current).getType() == Operator.MULTIPLY) { //Multiplication token found
                if (before instanceof Number && ((Number) before).getValue() == 1 && after instanceof Variable) { //The rule applies
                    newExpression.remove(before);
                    //Removes the 1 Token and deos not add the * Token to the new expression
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
        Node<Token> newRoot = new Node(root.getContent());
        Node<Token> temp = new Node(root.getContent());
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
            temp = new Node(OperatorFactory.makeAdd());
            i--;
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
        if (term1.getContent() instanceof Operator && ((Operator) term1.getContent()).getType() == Operator.MULTIPLY
                && term2.getContent() instanceof Operator && ((Operator) term2.getContent()).getType() == Operator.MULTIPLY) {
            for (int i = 0; i < term1.getNumOfChildren(); i++) {
                for (int j = 0; j < term2.getNumOfChildren(); j++) {
                    if (traverseTree(term1.getChildren().get(i)).equals(traverseTree(term2.getChildren().get(j)))) {
                        return true;
                    }
                }
            }
        } else if (term1.getContent() instanceof Operator && ((Operator) term1.getContent()).getType() == Operator.MULTIPLY) {
            for (int i = 0; i < term1.getNumOfChildren(); i++) {
                if (traverseTree(term1.getChildren().get(i)).equals(traverseTree(term2))) {
                    return true;
                }
            }
        } else if (term2.getContent() instanceof Operator && ((Operator) term2.getContent()).getType() == Operator.MULTIPLY) {
            for (int i = 0; i < term2.getNumOfChildren(); i++) {
                if (traverseTree(term2.getChildren().get(i)).equals(traverseTree(term1))) {
                    return true;
                }
            }
        } else if (term1.getContent() instanceof Number && term2.getContent() instanceof Number) {
            return true;
        }
        return term1.getContent() instanceof Variable && term2.getContent() instanceof Variable
                && ((Variable) term1.getContent()).getType() == ((Variable) term2.getContent()).getType();
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
        if (term1.getContent() instanceof Operator && ((Operator) term1.getContent()).getType() == Operator.MULTIPLY) {
            newRoot = new Node(OperatorFactory.makeMultiply());
            int e1Pos = -1, e2Pos = -1;
            Node<Token> temp1 = null;
            Node<Token> temp2 = null;
            Node<Token> temp = new Node(OperatorFactory.makeAdd());
            Node<Token> exp = new Node(new Number(1));
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
                    temp1 = new Node(OperatorFactory.makeMultiply());
                    temp1.addChild(term1.getChildren().get(i));
                }
            }
            for (int i = 0; i < term2.getNumOfChildren(); i++) {
                if (i != e2Pos) {
                    temp2 = new Node(OperatorFactory.makeMultiply());
                    temp2.addChild(term2.getChildren().get(i));
                }
            }
            temp.addChild(temp1);
            temp.addChild(temp2);
            newRoot.addChild(temp);
            newRoot.addChild(exp);
        } else if (term1.getContent() instanceof Number && term2.getContent() instanceof Number) {
            double sum = ((Number) term1.getContent()).getValue() + ((Number) term2.getContent()).getValue();
            newRoot = new Node(new Number(sum));
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
     * expression
     * @return The root of the expression with the like terms added
     */
    public static Node<Token> addLikeTerms(Node<Token> root) {//TODO: rewrite -> just add adjacent terms recursively
        Node<Token> newRoot = new Node(root.getContent());
        Node<Token> first = root.getChildren().get(0);
        Node<Token> rest = new Node(root.getContent());
        rest.addChildren(new ArrayList(root.getChildren().subList(1, root.getNumOfChildren())));
        if (root.getContent() instanceof Operator && ((Operator) root.getContent()).getType() == Operator.ADD) {
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
            Node childNode1 = root.getChildren().get(0);
            Node childNode2 = root.getChildren().get(1);
            Token child1 = (Token) childNode1.getContent();
            Token child2 = (Token) childNode2.getContent();
            if (child1 instanceof Variable && child2 instanceof Variable) { //VARVAR
                Variable v1 = (Variable) child1;
                Variable v2 = (Variable) child2;
                if (v1.getType() == v2.getType()) { //Checks to make sure it's the same type
                    if (o.getType() == Operator.MULTIPLY) {
                        //Applies rule V * V -> V ^ 2
                        Node newRoot = new Node(OperatorFactory.makeExponent());
                        newRoot.addChild(new Node(v1));
                        newRoot.addChild(new Node(new Number(2)));
                        return newRoot;
                    } else if (o.getType() == Operator.DIVIDE) {
                        //Applies rule V/V -> 1
                        return new Node(new Number(1));
                    } else { //No rules apply
                        return root;
                    }
                } else { //Not the same variable; rule deos not apply
                    return root;
                }
            } else if (child1 instanceof Operator && ((Operator) child1).getType() == Operator.EXPONENT && child2 instanceof Operator
                    && ((Operator) child2).getType() == Operator.EXPONENT) { //EXPEXP
                Node node1 = (Node) childNode1.getChildren().get(1);
                Node node2 = (Node) childNode2.getChildren().get(1);
                Token exp1Child2 = (Token) ((Node) childNode1.getChildren().get(0)).getContent();
                Token exp2Child2 = (Token) ((Node) childNode2.getChildren().get(0)).getContent();
                Variable v1 = exp1Child2 instanceof Variable ? (Variable) exp1Child2 : null;
                Variable v2 = exp2Child2 instanceof Variable ? (Variable) exp2Child2 : null;
                if (v1 != null && v2 != null && v1.getType() == v2.getType()) { //Rule applies
                    if (o.getType() == Operator.DIVIDE) {
                        //Applies rule (V ^ E1) / (V ^ E2) -> V ^ (E1 - E2)
                        Node head = new Node(OperatorFactory.makeExponent());
                        Node subtract = new Node(OperatorFactory.makeSubtract());
                        subtract.addChild(node1);
                        subtract.addChild(node2);
                        head.addChild(new Node(v1));
                        head.addChild(subtract);
                        return head;
                    } else if (o.getType() == Operator.MULTIPLY) {
                        //Applies rule (V ^ E1) * (V ^ E2) -> V ^ (E1 + E2)
                        Node head = new Node(OperatorFactory.makeExponent());
                        Node add = new Node(OperatorFactory.makeAdd());
                        add.addChild(node1);
                        add.addChild(node2);
                        head.addChild(new Node(v1));
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
                Node exponent = childNode2;
                Token t = (Token) ((Node) exponent.getChildren().get(0)).getContent();
                if (t instanceof Variable && v.getType() == ((Variable) t).getType()) { //Rule applies
                    Node node = (Node) exponent.getChildren().get(1);
                    if (o.getType() == Operator.DIVIDE) {
                        //Applies rule V / V ^ E -> V ^ (1 - E)
                        Node head = new Node(OperatorFactory.makeExponent());
                        head.addChild(new Node(v));
                        Node subtract = new Node(OperatorFactory.makeSubtract());
                        subtract.addChild(new Node(new Number(1)));
                        subtract.addChild(node);
                        head.addChild(subtract);
                        head = expand(head);
                        return head;
                    } else if (o.getType() == Operator.MULTIPLY) {
                        //Applies rule V * V ^ E -> V ^ (1 + E)
                        Node head = new Node(OperatorFactory.makeExponent());
                        head.addChild(new Node(v));
                        Node add = new Node(OperatorFactory.makeAdd());
                        add.addChild(new Node(new Number(1)));
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
                Node exponent = childNode1;
                Token t = (Token) ((Node) exponent.getChildren().get(0)).getContent();
                if (t instanceof Variable && v.getType() == ((Variable) t).getType()) { //Rule applies
                    Node node = (Node) exponent.getChildren().get(1);
                    if (o.getType() == Operator.DIVIDE) {
                        //Applies rule V / V ^ E -> V ^ (1 - E)
                        Node head = new Node(OperatorFactory.makeExponent());
                        head.addChild(new Node(v));
                        Node subtract = new Node(OperatorFactory.makeSubtract());
                        subtract.addChild(new Node(new Number(1)));
                        subtract.addChild(node);
                        head.addChild(subtract);
                        head = expand(head);
                        return head;
                    } else if (o.getType() == Operator.MULTIPLY) {
                        //Applies rule V * V ^ E -> V ^ (1 + E)
                        Node head = new Node(OperatorFactory.makeExponent());
                        head.addChild(new Node(v));
                        Node add = new Node(OperatorFactory.makeAdd());
                        add.addChild(new Node(new Number(1)));
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
        Token child1 = (Token) root.getChildren().get(1).getContent();
        Token child2 = (Token) root.getChildren().get(0).getContent();
        if (current instanceof Operator && ((Operator) current).getType() == Operator.EXPONENT && child1 instanceof Number && child2 instanceof Operator) { //O ^ N rule applies
            double n = ((Number) child1).getValue();
            Node expression = root.getChildren().get(0);
            if (n % 1 != 0) {
                //TODO: Must be an integer; FIND A WAY TO HAVE THIS WORKING
                throw new IllegalArgumentException("Power must be an integer");
            }
            if (n < 0) {
                Node head = new Node(OperatorFactory.makeDivide());
                head.addChild(new Node(new Number(1)));
                n *= -1;
                if (n == 1) {
                    head.addChild(expression);
                } else { //n > 1
                    n -= 2;
                    Node head2 = new Node(OperatorFactory.makeMultiply());
                    head2.addChild(expression); //NOTE: No need to clone
                    head2.addChild(expression);
                    while (n > 0) {
                        Node newHead = new Node(OperatorFactory.makeMultiply());
                        newHead.addChild(expression);
                        newHead.addChild(head2);
                        head2 = newHead;
                        n--;
                    }
                    head.addChild(head2);
                }
                return head;
            } else if (n == 0) {
                return new Node(new Number(1)); //Anything ^ 0 = 1
            } else if (n == 1) {
                return expression; //Simply removes the ^ 1
            } else { //n > 2
                n -= 2;
                Node head = new Node(OperatorFactory.makeMultiply());
                head.addChild(expression); //NOTE: No need to clone
                head.addChild(expression);
                while (n > 0) {
                    Node newHead = new Node(OperatorFactory.makeMultiply());
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
        ArrayList<Node> multiplications = new ArrayList<Node>();
        ArrayList<Node> divisions = new ArrayList<Node>();
        boolean stop = false;
        Stack<Node> stack = new Stack(); //Things to read for multiplications / divisions
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
                    if (t1 instanceof Operator && (((Operator) t1).getType() == Operator.MULTIPLY || ((Operator) t1).getType() == Operator.DIVIDE)
                            && t2 instanceof Operator && (((Operator) t2).getType() == Operator.MULTIPLY || ((Operator) t2).getType() == Operator.DIVIDE)) { //Both children have multiplication / divisions
                        stack.push(child1);
                        stack.push(child2);
                    } else if (t1 instanceof Operator && (((Operator) t1).getType() == Operator.MULTIPLY || ((Operator) t1).getType() == Operator.DIVIDE)) { //More multiplications / divisions on T1
                        multiplications.add(child2);
                        stack.push(child1);
                    } else if (t2 instanceof Operator && (((Operator) t2).getType() == Operator.MULTIPLY || ((Operator) t2).getType() == Operator.DIVIDE)) { //More multiplications / divisions on T2
                        multiplications.add(child1);
                        stack.push(child2);
                    } else { //No more multiplications or divisions
                        multiplications.add(child1);
                        multiplications.add(child2);
                    }
                } else if (o.getType() == Operator.DIVIDE) { //TODO: APPLY SPECIAL CASES FOR DIVISIONS
                    Node<Token> child1 = readHead.getChildren().get(0);
                    Node<Token> child2 = readHead.getChildren().get(1);
                    Token t1 = child1.getContent();
                    Token t2 = child2.getContent();
                    if (t1 instanceof Operator && (((Operator) t1).getType() == Operator.MULTIPLY || ((Operator) t1).getType() == Operator.DIVIDE)
                            && t2 instanceof Operator && (((Operator) t2).getType() == Operator.MULTIPLY || ((Operator) t2).getType() == Operator.DIVIDE)) { //Both children have multiplication / divisions
                    } else if (t1 instanceof Operator && (((Operator) t1).getType() == Operator.MULTIPLY || ((Operator) t1).getType() == Operator.DIVIDE)) { //More multiplications / divisions on T1
                    } else if (t2 instanceof Operator && (((Operator) t2).getType() == Operator.MULTIPLY || ((Operator) t2).getType() == Operator.DIVIDE)) { //More multiplications / divisions on T2
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
            ArrayList<Node> variables = new ArrayList<Node>();
            ArrayList<Node> constants = new ArrayList<Node>();
            ArrayList<Node> polynomials = new ArrayList<Node>();
            ArrayList<Node> others = new ArrayList<Node>();
            for (Node node : multiplications) {
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
