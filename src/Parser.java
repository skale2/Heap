
import java.util.*;
import java.lang.reflect.Method;

/**
 * A class that takes in a stream of tokens from the Lexer and parses them
 * into an Abstract Syntax Tree (AST), which is easier to run through
 * when interpreting.
 */
public class Parser {

    /** *************************************************************************************************
     *  FORMALITIES
     *  Basic formalities for the parser
     */

    Parser(Lexer lexer) {
        this._lexer = lexer;
        this._current = null;
    }

    /** Entry method into Parser - considers program as Block and parses it */
    ASTNode parse() {
        return parseBlock();
    }

    /**
     * Consumes a token from the lexer, if the passed in
     * token is equivalent to _current
     * @param token A token to compare to _current to
     *              determine whether to proceed
     */
    private void eat(Token token) {
        if (token.equals(current())) {
            current(_lexer.next());
        } else {
            System.out.println("Unknown token");
        }
    }

    private void eat(String name) {
        if (values.get(name).equals(current())) {
            current(_lexer.next());
        } else {
            System.out.println("Unknown token");
        }
    }

    private Token current() {
        return _current;
    }

    private void current(Token current) {
        this._current = current;
    }

    /**
     * Checks if _current is a token type by the token name
     * @param names List of token string names to check
     * @return If _current is any of the passed in token names
     */
    private boolean currentIs(String... names) {
        return Arrays.stream(names).anyMatch(name -> current().equals(values.get(name)));
    }

    /**
     * Check if _current is a token type by passing in the token itself
     * @param tokens List of tokens to check _current against
     * @return Whether _current is any of the passed in tokens
     */
    private boolean currentIs(Token... tokens) {
        return Arrays.stream(tokens).anyMatch(token -> current().equals(token));
    }

    /**
     * Check if _current falls into *all* of the passed in usage types
     * @param types List of usage type methods to call on _current
     * @return Whether *all* of types returns true
     */
    private boolean currentAll(String... types) {
        try {
            return Arrays.stream(types)
                    .allMatch(type -> current()
                            .getClass().getDeclaredMethod(type)
                            .invoke(current())
                    );
        } catch (NoSuchMethodException nsme) {
            return false;
        }
    }

    /**
     * Check if _current falls into *any* of the passed in usage types
     * @param types List of usage type methods to call on _current
     * @return Whether *any* of types returns true
     */
    private boolean currentAny(String... types) {
        try {
            return Arrays.stream(types)
                    .anyMatch(type -> current()
                            .getClass().getDeclaredMethod(type)
                            .invoke(current())
                    );
        } catch (NoSuchMethodException nsme) {
            return false;
        }
    }


    /** A map of usable tokens that aren't dependent on user code, for comparisons. Avoids the
     * creation of unnecessary tokens. */
    private static final Map<String, Token> values = new HashMap<String, Token>() {{
        for (Token.TokenType type : Token.TokenType.values()) {
            if (type != Token.TokenType.VAR && type != Token.TokenType.INT_VAL &&
                    type != Token.TokenType.STR_VAL &&  type != Token.TokenType.REAL_VAL) {
                put(type.value(), new Token(type));
            }
        }
    }};

    private Lexer _lexer;
    private Token _current;




    private Block parseBlock(Token... endTokens) {
        List<Statement> statements = new ArrayList<>();
        List tokens = Arrays.asList(endTokens);
        while (!tokens.contains(current())) {
            parseStatement();
        }
        return new Block(statements);
    }

    private Statement parseStatement() {
        if (current().isDirect()) {
            return parseDirect();
        } else if (currentIs("RETURN")) {
            return parseReturn();
        } else if (
                currentIs("ANNOTATION") || // Is a modifier
                currentIs("MULTIPLY") ||  // References a pointer variable
                current().isVar()
                ) {
            return parseAssignment(values.get("EOL"));
            eat("EOL");
        } else {
            parseExpression(values.get("EOL"));
        }
    }

    private Return parseReturn() {
        eat("RETURN");
        Expression expression = parseExpression(values.get("EOL"));
        eat("EOL");
        return new Return(expression);
    }

    private Assignment parseAssignment(Token... endTokens) {
        Declare declaration = parseDeclare();
        eat("ASSIGN");
        Expression expression = parseExpression(endTokens);
        return new Assignment(declaration, expression);
    }

    private List<Type> parseType(Token endToken) {
        List<Type> types = new ArrayList<>();

        while(!currentIs(endToken)) {

            /* Check if is a pointer */
            boolean isPointer = false;
            if (current().type() == Token.TokenType.MULTIPLY) {
                eat("MULTIPLY");
                isPointer = true;
            }

            /* The type of the token */
            String typeString = current().type().toString();

            /* Check if is a container */
            List<String> containerTypes = new ArrayList<String>() {{
                add("ARR_OPEN"); add("ARR_OPEN"); add("ARR_OPEN");
                add("SCOPE_OPEN"); add("SET_OPEN");
                add("UNDIR_OPEN"); add("DIR_OPEN");
            }};

            if (containerTypes.contains(current().type().toString())) {
                /* Take out the "_OPEN" part of type */
                typeString = typeString.substring(typeString.length() - 5);
                /* Convert between scope token and more definite map */
                if (typeString.equals("SCOPE")) {
                    typeString = "MAP";
                }

                types.add(parseContainerType(typeString, isPointer));
                continue;
            }

            /* Check if is a short hand container */
            List<String> shortHandContainerTypes = new ArrayList<String>() {{
                add("ARR_TYPE"); add("L_ARR_TYPE"); add("DL_ARR_TYPE");
                add("UNDIR_TYPE"); add("DIR_TYPE"); add("MAP_TYPE");
                add("SET_TYPE");
            }};

            if (shortHandContainerTypes.contains(current().type().toString())) {
                typeString = typeString.substring(typeString.length() - 5);
                /* A new container type with no internal types */
                types.add(new ContainerType(isPointer, ContainerType.Container.valueOf(typeString), new ArrayList<>()));
                continue;
            }

            /* Check if is a shorthand for graph */
            if (current().type() == Token.TokenType.DIR_TYPE) {
                eat("DIR_GRAPH_TYPE");
                List<Type> containedType = new ArrayList<>();
                types.add(new ContainerType(isPointer, ContainerType.Container.DIR, containedType));
                continue;
            } else if (current().type() == Token.TokenType.UNDIR_TYPE) {
                eat("UNDIR_GRAPH_TYPE");
                List<Type> containedType = new ArrayList<>();
                types.add(new ContainerType(isPointer, ContainerType.Container.UNDIR, containedType));
                continue;
            }

            /* Check current is either a built-in type, or a user made type
            * that isn't reserved */
            assert current().isType() || !current().isReserved();
            types.add(new Type(current(), isPointer));
            eat(current());

            if(!currentIs(endToken)) {
                eat("COMMA");
            }
        }

        return types;
    }

    private ContainerType parseContainerType(String containerType, boolean isPointer) {
        eat(values.get(containerType + "_OPEN"));
        List<Type> containerTypes =  parseType(values.get(containerType + "_CLOSE"));
        eat(values.get(containerType + "_CLOSE"));
        return new ContainerType(isPointer, ContainerType.Container.valueOf(containerType), containerTypes);
    }

    private ParamDefs parseParamDefs(Token endBlock) {
        List<Assignment> paramDefs = new ArrayList<>();
        while (!current().equals(endBlock)) {
            paramDefs.add(parseAssignment(values.get("COMMA")));
            eat("COMMA");
        }
        return new ParamDefs(paramDefs);
    }

    private Declare parseDeclare() {
        List<Modifier> modifiers = new ArrayList<>();
        while (current().isModifier()) {
            modifiers.add(new Modifier(current()));
            eat(current());
        }

        Var var = new Var(current());
        eat(current());

        List<Type> types = new ArrayList<>();
        if (currentIs("LESS_THAN")) {
            eat("LESS_THAN");
            types = parseType(values.get("GREATER_THAN"));
            eat("GREATER_THAN");
        }

        return new Declare(modifiers, var, types);
    }


    /** *************************************************************************************************
     *  EXPRESSIONS
     *  Strings of chained logic that includes operators, property calls, indexes, and references
     */

    private Token[] afterTokens = new Token[] {
            values.get("ARR_OPEN"),
            values.get("PERIOD"),
            values.get("PAR_OPEN")
    };


    /**
     * Takes in a expression tree in a weave (a series of operations branching
     * through the left side of the tree, such as one made initially by
     * parseExpression). Applies operator precedence to the tree by shuffling
     * nodes to make sure that higher precedence operators are lower in the
     * tree, and therefore are executed first by the interpreter.
     *
     * Terms are leaves (at least in this abstraction - terms can contain their
     * own expressions, but because they are bounded by parenthesis, they are
     * automatically a higher precedence than anything in this expression,so we
     * can ignore them as leaves. They can also contain unitary operators,
     * which also have higher precedence.
     *
     * Example -> g + f + e**d  * c**b + a
     *
     * Converts this (what I call a weave):
     *
     *                           +
     *                          / \
     * 						  **   a
     * 						 / \
     *                      *    b
     *                     / \
     * 					 **   c
     *                  / \
     * 				   +   d
     *                / \
     * 				 +   e
     *              / \
     * 			   g   f
     *
     *
     * Into this:
     *
     * 			        +
     *                /   \
     *               +    a
     *             /   \
     *           +      *
     *         /  \    /  \
     *        g    f  **   **
     * 			     /  \ /	\
     *              e  d  c   b
     *
     * *** MUTATIVE ***
     *
     * @param node Head of expression tree
     */
    private void parse(Expression node) {
        if (!(node instanceof Op)) {
            return;
        }

        /* Store left's parents to cancel any need of storing parent pointers
         * for each operation */
        BinaryOp op = (BinaryOp) node;
        Expression left = op.left;

        /* Run down left side of tree (which here in the tree is only non-trivial side)
         * until you hit a node with an equal to or lower precedence than the head node
         */
        while (!op.higherPrecedenceThan(left)) {
            left = op.left;
        }

        /* If left has the same precedence as the root op,
         *
         */
        BinaryOp opLeft = (BinaryOp) left;
        if (op.equalPrecedenceTo(left)) {
            ((BinaryOp) opLeft.parent).setLeft(opLeft.right);
            parse(op.left);
            opLeft.setRight(op.left);
            op.setLeft(opLeft);
        } else {
            if (((BinaryOp) op.parent).left.equals(op)) {
                ((BinaryOp) left.parent).setLeft(op.right);
            } else {
                ((BinaryOp) left.parent).setRight(op.right);
            }
            ((BinaryOp) op.parent).setLeft(left);
            parse(op);
            opLeft.setRight(op);
        }

        parse(left);
    }


    /** Entry point for all expression parsing
     * */
    private Expression parseExpression(Token... endTokens) {
        Expression expression, nextTerm = null;
        Token current;
        do {
            expression = parseTerm(endTokens);
            if (current().isOperator()) {
                current = current();
                eat(current);
                expression = new BinaryOp(expression, parseTerm(endTokens), current);
            }
        } while (!currentIs("EOL"));

        parse(expression);
        return expression;
    }

    /**
     * Parses terms, which are expressions that denote:
     *  1. variables or literals with property calls, unary operations, or indexes applied,
     *  2. nested expressions within parenthesis with same applications,
     *  3. container literals,
     *  4. construct definitions.
     * ** Except in special cases, use with postfix() to get full term **
     * @param endTokens A token to check for to see if the expression is finished
     * @return A term, which can later be combined with other terms
     */
    private Expression parseTerm(Token... endTokens) {
        /* Expression is over */
        if (currentIs(endTokens)) {
            return null;
        }

        Expression expression = null;

        if (current().isLiteral()) {
            expression = parseLiteral();
            if (!(expression instanceof StringLiteral)) {
                return expression;
            }
        } else if (current().isVar()) {
            eat(current());
            expression = new Var(current());
        }

        /* Is a nested expression */
        else if (currentIs(values.get("PAR_OPEN"))) {
            eat("PAR_OPEN");
            expression = parseExpression(values.get("PAR_CLOSE"));
            eat("PAR_CLOSE");
        }

        /* Prefix unary operators */
        else if (currentIs(values.get("MULTIPLY"))) {
            eat("MULTIPLY");
            expression = new UnaryOp(parseTerm(afterTokens), values.get("MULTIPLY"));
        } else if (current().isContainer()) {
            expression = parseContainer();
        } else if (currentIs("SUBTRACT")) {
            eat("SUBTRACT");
            expression = new UnaryOp(parseTerm(afterTokens), values.get("SUBTRACT"));
        } else if (currentIs("ADD")) {
            eat("ADD");
            return parseTerm(endTokens);
        } else if (currentIs("L_NOT")) {
            eat("L_NOT");
            expression = new UnaryOp(parseTerm(afterTokens), values.get("L_NOT"));
        } else if (currentIs("B_NOT")) {
            eat("B_NOT");
            expression = new UnaryOp(parseTerm(afterTokens), values.get("B_NOT"));
        } else if (currentIs("INCREMENT")) {
            eat("INCREMENT");
            expression = new UnaryOp(parseTerm(afterTokens), values.get("INCREMENT"), Meta.PREFIX);
        } else if (currentIs("DECREMENT")) {
            eat("DECREMENT");
            expression = new UnaryOp(parseTerm(afterTokens), values.get("DECREMENT"), Meta.PREFIX);
        }

        /* Creation expressions */
        else if (current().isConstruct()) {
            return parseConstruct();
        } else if (current().isContainer()) {
            return parseContainer();
        }

        /* Any term that has not already been returned can have after-effects (property
         * calls, after-unary operators, and indexes) */
        return postFix(expression);
    }

    /**
     * Handles postfix operations, such as property calls, method calls, and postfix operators
     * @param expression Expression to attach postfix operations on
     * @return A full term made up of expression and added postfix operations
     */
    private Expression postFix(Expression expression) {
        if (currentIs("ARR_OPEN")) {
            return postFix(parseIndex(expression));
        } else if (currentIs("PERIOD")) {
            return postFix(parseProperty(expression));
        } else if (currentIs("PAR_OPEN")) {
            return parseCall(expression);
        } else if (currentIs("INCREMENT")) {
            eat("INCREMENT");
            return postFix(new UnaryOp(expression, values.get("INCREMENT"), Meta.POSTFIX));
        } else if (currentIs("DECREMENT")) {
            eat("DECREMENT");
            return postFix(new UnaryOp(expression, values.get("DECREMENT"), Meta.POSTFIX));
        } else if (currentIs("ROUND")) {
            eat("ROUND");
            if (current().isLiteral()) {
                return new BinaryOp(expression, parseLiteral(), values.get("ROUND"));
            } else if (current().isVar()) {
                return new BinaryOp(expression, new Var(current()), values.get("ROUND"));
            }
            return new UnaryOp(expression, values.get("ROUND"));
        } else {
            return expression;
        }
    }

    private Index parseIndex(Expression var) {
        eat("ARR_OPEN");
        Expression index = parseExpression(values.get("ARR_CLOSE"));
        eat("ARR_CLOSE");
        return new Index(var, index);
    }

    private Get parseProperty(Expression var) {
        eat("PERIOD");
        if (current().isVar()) {
            Var getVar = new Var(current());
            eat(current());
            return new Get(var, getVar);
        }
        eat("PAR_OPEN");
        Get get = new Get(var, parseExpression(values.get("PAR_CLOSE")));
        eat("PAR_CLOSE");
        return get;
    }

    private Call parseCall(Expression var) {
        eat("PAR_OPEN");
        List<Expression> expressions = new ArrayList<>();
        while(!currentIs("PAR_CLOSE")) {
            expressions.add(parseExpression(values.get("COMMA")));
        }
        eat("PAR_CLOSE");
        return new Call(new Params(expressions), var);
    }

    private Literal parseLiteral() {
        Token current = current();
        eat(current);

        if (current.type().equals(Token.TokenType.INT_VAL) {
            return new IntLiteral(current);
        } else if (current.type() == Token.TokenType.REAL_VAL) {
            return new RealLiteral(current);
        } else if (current.type() == Token.TokenType.STR_VAL) {
            return new StringLiteral(current);
        } else if (current.type() == Token.TokenType.TRUE || current.type() == Token.TokenType.FALSE) {
            return new BooleanLiteral((current);
        } else if (current.type().equals(Token.TokenType.NULL)) {
            return new NullLiteral(current);
        }
        return null;
    }


    /** *************************************************************************************************
     *  DIRECTS
     *  Blocks that direct control flow. Only scheme that isn't set to a variable.
     */


    private Direct parseDirect() {
        if (current().type() == Token.TokenType.IF) {
            return parseIf();
        } else if (currentIs("LOOP")) {
            return parseLoop();
        } else if (current().type() == Token.TokenType.SWITCH) {
            return parseSwitch();
        } else if (current().type() == Token.TokenType.SELECT) {
            return parseSelect();
        } else if (current().type() == Token.TokenType.TRY) {
            return parseTry();
        }
    }

    private Loop parseLoop() {
        eat("LOOP");
        eat("PAR_OPEN");

        List<Assignment> initClauses = new ArrayList<>();
        while(!currentIs("COLON")) {
            initClauses.add(parseAssignment(values.get("COLON")));
            if (currentIs("COMMA")) {
                eat("COMMA");
            }
        }
        eat("COLON");

        List<Expression> breakClauses = new ArrayList<>();
        while(!currentIs("COLON")) {
            breakClauses.add(parseExpression(values.get("COMMA"), values.get("COLON")));
            if (currentIs("COMMA")) {
                eat("COMMA");
            }
        }
        eat("COLON");

        List<Expression> loopClauses = new ArrayList<>();
        while(!currentIs("PAR_CLOSE")) {
            loopClauses.add(parseExpression(values.get("COMMA"), values.get("PAR_CLOSE")));
            if (currentIs("COMMA")) {
                eat("COMMA");
            }
        }

        eat("PAR_CLOSE");

        eat("DIRECT");

        eat("SCOPE_OPEN");
        Block loopBlock = parseBlock(values.get("SCOPE_CLOSE"));
        eat("SCOPE_CLOSE");

        Block elseBlock = null;
        if (currentIs("ELSE")) {
            eat("SCOPE_OPEN");
            elseBlock = parseBlock(values.get("SCOPE_CLOSE"));
            eat("SCOPE_CLOSE");
        }

        return new Loop(initClauses, breakClauses, loopClauses, loopBlock, elseBlock);
    }

    private If parseIf() {
        eat("IF");

        eat("PAR_OPEN");
        Expression expression = parseExpression(values.get("PAR_CLOSE"));
        eat("PAR_CLOSE");

        eat("DIRECT");

        eat("SCOPE_OPEN");
        Block block = parseBlock(values.get("SCOPE_CLOSE"));
        eat("SCOPE_CLOSE");

        List<IfBlock> ifblocks = new ArrayList<IfBlock>() {{
            add(new IfBlock(expression, block));
        }};

        /* Create an else block to be populated */
        Block elseBlock = null;

        /* Check if (an) else if(s), or just an else, exist */
        if (currentIs("ELSE")) {
            eat("ELSE");

            if (currentIs("SCOPE_OPEN")) {
                /* If just an else */
                eat("DIRECT");

                eat("SCOPE_OPEN");
                elseBlock = parseBlock(values.get("SCOPE_CLOSE"));
                eat("SCOPE_CLOSE");
            } else {
                /* If an else if */
                If nextIf = parseIf();
                ifblocks.addAll(nextIf.ifblocks);
                elseBlock = nextIf.elseBlock;
            }
        }

        return new If(ifblocks, elseBlock);
    }

    private Direct parseSwitch() {
        return parseSwitchLike(values.get("SWITCH"));
    }

    private Direct parseSelect() {
        return parseSwitchLike(values.get("SELECT"));
    }

    private Direct parseSwitchLike(Token startToken) {
        eat(startToken);

        eat("PAR_OPEN");
        Expression switchExpression = parseExpression(values.get("PAR_CLOSE"));
        eat("PAR_CLOSE");

        eat("DIRECT");

        eat("SCOPE_OPEN");

        List<Case> cases = new ArrayList<>();
        Block defaultBlock = null;
        Block elseBlock = null;

        while (!currentIs("SCOPE_CLOSE")) {
            if (currentIs("CASE")) {
                eat("CASE");
                Expression caseExpression = parseExpression(values.get("COLON"));
                eat("COLON");
                Block caseBlock = parseBlock(values.get("CASE"), values.get("DEFAULT"));

                Case newCase = new Case(caseExpression, caseBlock);
                cases.add(newCase);
            } else if (currentIs("DEFAULT")) {
                eat("DEFAULT");
                eat("COLON");

                defaultBlock = parseBlock(values.get("SCOPE_CLOSE"));
            }
        }

        eat("SCOPE_CLOSE");

        if (currentIs("ELSE")) {
            eat("SCOPE_OPEN");
            elseBlock = parseBlock(values.get("SCOPE_CLOSE"));
            eat("SCOPE_CLOSE");
        }

        if (startToken.type() == Token.TokenType.SWITCH) {
            return new Switch(switchExpression, cases, defaultBlock, elseBlock);
        } else if (startToken.type() == Token.TokenType.SELECT) {
            return new Select(switchExpression, cases, defaultBlock, elseBlock);
        } return null;
    }

    private Try parseTry() {
        eat("TRY");

        eat("SCOPE_OPEN");
        Block tryBlock = parseBlock(values.get("SCOPE_CLOSE"));
        eat("SCOPE_CLOSE");

        Declare exception;
        Block catchBlock, elseBlock = null;
        List<Catch> catches = new ArrayList<>();

        while (currentIs("CATCH")) {
            eat("CATCH");

            eat("PAR_OPEN");
            exception = parseDeclare();
            eat("PAR_CLOSE");

            eat("SCOPE_OPEN");
            catchBlock = parseBlock(values.get("SCOPE_CLOSE"));
            eat("SCOPE_CLOSE");

            catches.add(new Catch(exception, catchBlock));
        }

        if (currentIs("ELSE")) {
            eat("SCOPE_OPEN");
            elseBlock = parseBlock(values.get("SCOPE_CLOSE"));
            eat("SCOPE_CLOSE");
        }

        return new Try(tryBlock, catches, elseBlock);
    }


    /** *************************************************************************************************
     *  CONSTRUCTS
     *  Reusable constructions that create objects
     */

    private Construct parseConstruct() {
        if (currentIs("FUNC")) {
            return parseFuncDeclare();
        }
    }

    private Func parseFuncDeclare() {
        eat("FUNC");

        eat("PAR_OPEN");
        ParamDefs paramDefs = parseParamDefs(values.get("PAR_CLOSE"));
        eat("PAR_CLOSE");

        eat("DIRECT");

        ASTNode operations;
        if (currentIs("SCOPE_OPEN")) {
            eat("SCOPE_OPEN");
            operations = parseBlock(values.get("SCOPE_CLOSE"));
            eat("SCOPE_CLOSE");
        } else {
            operations = parseExpression(values.get("EOL"));
        }

        return new Func(paramDefs, operations);
    }


    /** *************************************************************************************************
     *  CONTAINERS
     *  Containers hold objects and add relations between them
     */

    private Container parseContainer() {
        if (currentIs("ARR_OPEN")) {
            return parseArrayList();
        } else if (currentIs("L_ARR_OPEN")) {
            return parseLinkedList();
        } else if (currentIs("DL_ARR_OPEN")) {
            return parseDoubleLinkedList();
        } else if (currentIs("SCOPE_OPEN")) {
            return parseMap();
        } else if (currentIs("SET_OPEN")) {
            return parseSet();
        } else if (currentIs("UNDIR_OPEN")) {
            return parseUndirectedGraph();
        } else if (currentIs("DIR_OPEN")) {
            return parseDirectedGraph();
        }

        if (currentIs("ARR_TYPE")) {
            return new HArrayList(new ArrayList<>());
        } else if (currentIs("L_ARR_TYPE")) {
            return new HLinkedList(new ArrayList<>());
        } else if (currentIs("DL_ARR_TYPE")) {
            return new HDoubleLinkedList(new ArrayList<>());
        } else if (currentIs("MAP_TYPE")) {
            return new HMap(new HashMap<>());
        } else if (currentIs("SET_TYPE")) {
            return new HSet(new ArrayList<>());
        } else if (currentIs("UNDIR_TYPE")) {
            return new HUndirectedGraph(new ArrayList<>(), new ArrayList<>());
        } else if (currentIs("DIR_TYPE")) {
            return new HDirectedGraph(new ArrayList<>(), new ArrayList<>());
        }

        return null;
    }

    private HArrayList parseArrayList() {
        eat("ARR_OPEN");
        List<Expression> items = new ArrayList<>();
        while (!currentIs("ARR_CLOSE")) {
            items.add(parseExpression(values.get("COMMA")));
            eat("COMMA");
        }
        eat("ARR_CLOSE");
        return new HArrayList(items);
    }

    private HLinkedList parseLinkedList() {
        eat("L_ARR_OPEN");
        List<Expression> items = new ArrayList<>();
        while (!currentIs("ARR_CLOSE")) {
            items.add(parseExpression(values.get("COMMA")));
            eat("COMMA");
        }
        eat("ARR_CLOSE");
        return new HLinkedList(items);
    }

    private HDoubleLinkedList parseDoubleLinkedList() {
        eat("DL_ARR_OPEN");
        List<Expression> items = new ArrayList<>();
        while (!currentIs("ARR_CLOSE")) {
            items.add(parseExpression(values.get("COMMA")));
            eat("COMMA");
        }
        eat("ARR_CLOSE");
        return new HDoubleLinkedList(items);
    }

    private HSet parseSet() {
        eat("SET_OPEN");
        List<Expression> items = new ArrayList<>();
        while (!currentIs("SET_CLOSE")) {
            items.add(parseExpression(values.get("COMMA")));
            eat("COMMA");
        }
        eat("SET_CLOSE");
        return new HSet(items);
    }

    private HMap parseMap() {
        eat("SCOPE_OPEN");

        Map<Expression, Expression> items = new HashMap<>();
        while (!currentIs("SCOPE_CLOSE")) {
            Expression key = parseExpression(values.get("COLON"));
            Expression value = parseExpression(values.get("COMMA"));
            items.put(key, value);
        }

        eat("SCOPE_CLOSE");
        return new HMap(items);
    }

    private HDirectedGraph parseDirectedGraph() {
        eat("DIR_OPEN");

        List<Expression> nodes = new ArrayList<>();
        while (!currentIs("EOL")) {
            nodes.add(parseExpression(values.get("COMMA")));
            eat("COMMA"));
        }
        eat("EOL");

        List<HDirectedGraph.HDirectedEdge> edges = new ArrayList<>();
        while (!currentIs("DIR_CLOSE")) {
            Expression first = parseExpression(values.get("DIR_EDGE"),
                    values.get("DIR_2_EDGE"));

            assert currentIs("DIR_EDGE") || currentIs("DIR_2_EDGE");
            boolean doubleEdge = currentIs("DIR_EDGE");
            eat(current());

            Expression second = parseExpression(values.get("COMMA"));

            edges.add(new HDirectedGraph.HDirectedEdge(first, second, doubleEdge));
            eat("COMMA");
        }

        eat("DIR_CLOSE");
        return new HDirectedGraph(nodes, edges);
    }

    private HUndirectedGraph parseUndirectedGraph() {
        eat("UNDIR_OPEN");

        List<Expression> nodes = new ArrayList<>();
        while (!currentIs("EOL")) {
            nodes.add(parseExpression(values.get("COMMA")));
            eat("COMMA");
        }
        eat("EOL");

        List<HGraph.HEdge> edges = new ArrayList<>();
        while (!currentIs("DIR_CLOSE")) {
            Expression first = parseExpression(values.get("DIR_EDGE"),
                    values.get("DIR_2_EDGE"));

            eat("MINUS");

            Expression second = parseExpression(values.get("COMMA"));

            edges.add(new HGraph.HEdge(first, second));
            eat("COMMA");
        }

        eat("UNDIR_CLOSE");
        return new HUndirectedGraph(nodes, edges);
    }


    /** *************************************************************************************************
     *  NODES
     *  Abstract Syntax Tree node classes
     */

    static abstract class ASTNode {
        Token token;
        ASTNode parent;

        void setParent(ASTNode... nodes) {
            Arrays.asList(nodes).forEach(node -> node.parent = this);
        }

        void setParent(List<? extends ASTNode>... nodes) {
            Arrays.asList(nodes).forEach(list -> list.forEach(node -> node.parent = this));
        }

        void setParent(Map<? extends ASTNode, ? extends ASTNode>... nodes) {
            Arrays.asList(nodes).forEach(map -> map.forEach((name, node) -> node.parent = this));
        }
    }

    /** A sequential list of statements */
    static final class Block extends ASTNode {
        List<? extends ASTNode> statements;

        Block(List<? extends ASTNode> statements, ASTNode parent) {
            setParent(statements);
            setParent(parent);

            this.parent = parent;
            this.statements = statements;
        }
    }

    /** A complete line of instruction */
    static abstract class Statement extends ASTNode {}

    /** Assigns a variable a value */
    static final class Assignment extends Statement {
        ASTNode var, value;

        Assignment(ASTNode var, ASTNode value, ASTNode parent) {
            setParent(var, value, parent);

            assert var instanceof Declare || var instanceof Var;
            this.var = var;

            assert value instanceof Expression;
            this.value = value;

            this.token = new Token(Token.TokenType.ASSIGN);
        }
    }

    /** Declares a variable in the namespace */
    static final class Declare extends ASTNode {
        List<Modifier> modifier;
        Var var;
        List<Type> type;

        Declare(List<Modifier> modifier, Var var, List<Type> type) {
            setParent(modifier, type);
            setParent(var);

            this.modifier = modifier;
            this.var = var;
            this.type = type;
        }
    }

    /** A series of logical steps that returns a value */
    static abstract class Expression extends ASTNode {}

    /** A statement on what to return from a function */
    static final class Return extends Statement {
        Expression expression;

        Return(Expression expression) {
            setParent(expression);

            this.expression = expression;
            this.token = values.get("RETURN");
        }
    }


    /** A variable identifier */
    static final class Var extends Expression {
        String value;

        Var(Token token) {
            this.token = token;
            this.value = token.value();
        }
    }

    /** A (possibly nested) type */
    static class Type extends ASTNode {
        boolean isPointer;

        Type(Token token, boolean isPointer) {
            assert token.isType() || token.isVar();
            this.token = token;
            this.isPointer = isPointer;
        }
    }

    static final class ContainerType extends Type {
        Container container;
        List<Type> types;

        ContainerType(boolean isPointer, Container container, List<Type> types) {
            super(null, isPointer);
            setParent(types);
            this.container = container;
            this.types = types;
        }

        enum Container { NONE, SET, LIST, MAP, DIR, UNDIR, OTHER }
    }

    /** A @modifier that can be applied to a method, class, or variable */
    static final class Modifier extends ASTNode {
        Modifier(Token token) {
            assert token.isModifier();
            this.token = token;
        }
    }

    /** A list of definitions for parameters, which are syntactically Declares */
    static final class ParamDefs extends ASTNode {
        List<Assignment> parameters;

        ParamDefs(List<Assignment> parameters) {
            setParent(parameters);
            this.parameters = parameters;
        }
    }

    /** A list of passed in parameters, which are syntactically Expressions */
    static final class Params extends ASTNode {
        List<Expression> parameters;

        public Params(List<Expression> parameters) {
            setParent(parameters);
            this.parameters = parameters;
        }
    }


    /** Object-building blocks */
    static abstract class Construct extends Expression {}

    /** A defined operation taking a Param node */
    static final class Func extends Construct {
        ParamDefs paramDefs;
        ASTNode operations;

        Func(ParamDefs paramDefs, ASTNode operations) {
            assert operations instanceof Block || operations instanceof Expression;

            setParent(paramDefs, operations);
            this.token = new Token(Token.TokenType.FUNC);
            this.paramDefs = paramDefs;
            this.operations = operations;
        }
    }


    /** Control-flow blocks */
    static abstract class Direct extends Statement {
        Block elseBlock;
    }

    /** A repeated set of statements */
    static final class Loop extends Direct {
        List<Assignment> initClauses;
        List<Expression> breakClauses, loopClauses;
        Block block;

        Loop(List<Assignment> initClauses, List<Expression> breakClauses,
                    List<Expression> loopClauses, Block block, Block elseBlock) {

            setParent(initClauses, breakClauses, loopClauses);
            setParent(block, elseBlock);

            this.token = new Token(Token.TokenType.LOOP);
            this.initClauses = initClauses;
            this.breakClauses = breakClauses;
            this.loopClauses = loopClauses;
            this.block = block;
            this.elseBlock = elseBlock;
        }
    }

    /** A set of statements that execute conditionally */
    static final class If extends Direct {
        List<IfBlock> ifblocks;

        If(List<IfBlock> ifblocks, Block elseBlock) {
            setParent(ifblocks);
            setParent(elseBlock);

            this.token = new Token(Token.TokenType.IF);
            this.ifblocks = ifblocks;
            this.elseBlock = elseBlock;
        }
    }

    /** A single condition and resulting block */
    static final class IfBlock extends Direct {
        Expression condition;
        Block block;

        IfBlock(Expression condition, Block block) {
            setParent(condition, block);
            this.condition = condition;
            this.block = block;
        }
    }

    /** A set of cases done conditionally on a starting expression; each
     * statement is checked independently of each other (requires a break)*/
    static final class Switch extends Direct {
        Expression expression;
        List<Case> cases;
        Block defaultBlocks;

        Switch(Expression expression, List<Case> cases, Block defaultBlocks, Block elseBlock) {
            setParent(expression, defaultBlocks, elseBlock);
            setParent(cases);

            this.token = new Token(Token.TokenType.SWITCH);
            this.expression = expression;
            this.cases = cases;
            this.defaultBlocks = defaultBlocks;
            this.elseBlock = elseBlock;
        }
    }

    /** A set of cases done conditionally on a starting expression; after a
     * case is found, immediately breaks to end */
    static final class Select extends Direct {
        Expression expression;
        List<Case> cases;
        Block defaultBlocks;

        Select(Expression expression, List<Case> cases, Block defaultBlocks, Block elseBlock) {
            setParent(expression, defaultBlocks, elseBlock);
            setParent(cases);

            this.token = new Token(Token.TokenType.SWITCH);
            this.expression = expression;
            this.cases = cases;
            this.defaultBlocks = defaultBlocks;
            this.elseBlock = elseBlock;
        }
    }

    /** A single case and the resulting block */
    static final class Case extends Direct {
        Expression expression;
        Block block;

        Case(Expression expression, Block block) {
            setParent(expression, block);

            this.expression = expression;
            this.block = block;
        }
    }

    /** A block that may throw an Exception */
    static final class Try extends Direct {
        Block block;
        List<Catch> catchBlocks;

        Try(Block block, List<Catch> catchBlocks, Block elseBlock) {
            setParent(block, elseBlock);
            setParent(catchBlocks);

            this.block = block;
            this.catchBlocks = catchBlocks;
            this.elseBlock = elseBlock;
        }
    }

    /** A block that deals with caught exceptions in a try block */
    static final class Catch extends ASTNode {
        Declare Exception;
        Block block;

        Catch(Declare exception, Block block) {
            setParent(exception, block);
            Exception = exception;
            this.block = block;
        }
    }


    /** Calls to a construct (functions, classes, interfaces, structs, etc. */
    static final class Call extends Expression {
        Params params;
        Expression val;

        Call(Params params, Expression val) {
            setParent(params, val);
            this.token = values.get("PAR_OPEN");
            this.params = params;
            this.val = val;
        }
    }


    /** Container nodes */
    static abstract class Container extends Expression {}

    /** A dynamic array of objects */
    static class HList extends Container {
        List<Expression> items;
    }

    /** A dynamic array of objects with a dynamic array implementation */
    static final class HArrayList extends HList {
        List<Expression> items;

        HArrayList(List<Expression> items) {
            setParent(items);
            this.items = items;
            this.token = values.get("ARR_TYPE");
        }
    }

    /** A dynamic array of objects with a linked list implementation */
    static final class HLinkedList extends HList {
        List<Expression> items;

        HLinkedList(List<Expression> items) {
            setParent(items);
            this.items = items;
            this.token = values.get("L_ARR_TYPE");
        }
    }

    /** A dynamic array of objects with a doubly linked list implementation */
    static final class HDoubleLinkedList extends HList {
        List<Expression> items;

        HDoubleLinkedList(List<Expression> items) {
            setParent(items);
            this.items = items;
            this.token = values.get("DL_ARR_TYPE");
        }
    }

    /** A bijective mapping between objects */
    static final class HMap extends Container {
        Map<Expression, Expression> items;

        HMap(Map<Expression, Expression> items) {
            setParent(items);
            this.items = items;
            this.token = Parser.values.get("MAP_TYPE");
        }
    }

    /** A unique set of objects */
    static final class HSet extends Container {
        List<Expression> items;

        HSet(List<Expression> items) {
            setParent(items);
            this.items = items;
            this.token = values.get("SET_TYPE");
        }
    }

    /** A base class for collection of objects and edges connecting them */
    static abstract class HGraph extends Container {
        List<Expression> nodes;
        List<? extends HEdge> edges;

        HGraph(List<Expression> nodes, List<? extends HEdge> edges, Token token) {
            setParent(nodes, edges);
            this.nodes = nodes;
            this.edges = edges;
            this.token = token;
        }

        static class HEdge extends ASTNode {
            ASTNode first, second;

            HEdge(Expression first, Expression second) {
                setParent(first, second);
                this.first = first;
                this.second = second;
            }
        }
    }

    /** A graph where each edge is directed from one node to another */
    static final class HDirectedGraph extends HGraph {
        HDirectedGraph(List<Expression> nodes, List<HDirectedEdge> edges) {
            super(nodes, edges, values.get("DIR_TYPE"));
        }

        static final class HDirectedEdge extends HEdge {
            boolean doubleEdge;
            HDirectedEdge(Expression first, Expression second, boolean doubleEdge) {
                super(first, second);
                setParent(first, second);
                this.doubleEdge = doubleEdge;
            }
        }
    }

    /** A graph where nodes are parity-constant */
    static final class HUndirectedGraph extends HGraph {
        HUndirectedGraph(List<Expression> nodes, List<HEdge> edges) {
            super(nodes, edges, values.get("UNDIR_TYPE"));
        }
    }


    /** Built-in operations between language members */
    static abstract class Op extends Expression {
        /**
         * Whether this has a higher operator precedence than op. If op is actually
         * just a literal, return false.
         * */
        boolean higherPrecedenceThan(Expression op) {
            return op instanceof Op &&
                    Token.operatorPrecedence.get(this.token.type()) <
                    Token.operatorPrecedence.get(op.token.type());
        }

        /**
         * Whether this has equal operator precedence than op. If op is actually
         * just a literal, return false.
         * */
        boolean equalPrecedenceTo(Expression op) {
            return op instanceof Op &&
                    Token.operatorPrecedence.get(this.token.type()) ==
                            Token.operatorPrecedence.get(op.token.type());
        }
    }

    /** Meta information about operators that tokens cannot provide */
    enum Meta {
        POSTFIX, PREFIX
    }

    /** An operation that takes in one child */
    static final class UnaryOp extends Op {
        Expression child;
        Meta meta;

        UnaryOp(Expression child, Token token) {
            setParent(child);
            this.child = child;
            this.token = token;
        }

        UnaryOp(Expression child, Token token, Meta meta) {
            this(child, token);
            this.meta = meta;
        }

        void setChild(Expression expression) {
            this.child = expression;
            expression.parent = this;
        }
    }

    /** An operation that takes in two children */
    static class BinaryOp extends Op {
        Expression left, right;

        BinaryOp(Expression left, Expression right, Token token) {
            setParent(left, right);
            this.left = left;
            this.right = right;
            this.token = token;
        }

        void setLeft(Expression expression) {
            this.left = expression;
            expression.parent = this;
        }

        void setRight(Expression expression) {
            this.right = expression;
            expression.parent = this;
        }
    }

    /** Applies a binary op then sets left to resulting value, e.g. x += 2 */
    static final class SetOp extends BinaryOp {
        public SetOp(Expression left, Expression right, Token token) {
            super(left, right, token);
        }
    }

    /** Indexes a container object, e.g. x[2] */
    static final class Index extends BinaryOp {
        Expression left, right;

        Index(Expression var, Expression index) {
            super(var, index, values.get("ARR_TYPE"));
            this.left = var;
            this.right = index;
        }

        Expression var() { return left; }

        Expression index() { return right; }
    }

    /** Gets a property from an object, e.g. person.height */
    static final class Get extends BinaryOp {
        Expression left, right;

        Get(Expression var, Expression property) {
            super(var, property, values.get("PERIOD"));
        }

        Expression var() { return left; }

        Expression property() { return right; }
    }


    /** An operation that takes in three children */
    static final class TernaryOp extends Op {
        Expression left, center, right;

        TernaryOp(Expression left, Expression center, Expression right, Token token) {
            setParent(left, center, right);
            this.left = left;
            this.center = center;
            this.right = right;
            this.token = token;
        }

        void setLeft(Expression expression) {
            this.left = expression;
            expression.parent = this;
        }

        void setCenter(Expression expression) {
            this.center = expression;
            expression.parent = this;
        }

        void setRight(Expression expression) {
            this.right = expression;
            expression.parent = this;
        }
    }


    /** A proper value that serves no abstraction */
    static abstract class Literal extends Expression {}

    /** An integer literal */
    static final class IntLiteral extends Literal {
        IntLiteral(Token token) {
            this.token = token;
        }
    }

    /** A real number literal */
    static final class RealLiteral extends Literal {
        RealLiteral(Token token) {
            this.token = token;
        }
    }

    /** A String literal */
    static final class StringLiteral extends Literal {
        StringLiteral(Token token) {
            this.token = token;
        }
    }

    /** A boolean literal */
    static final class BooleanLiteral extends Literal {
        BooleanLiteral(Token token) {
            assert token.type() == Token.TokenType.TRUE || token.type() == Token.TokenType.FALSE;
            this.token = token;
        }
    }

    /** A null literal */
    static final class NullLiteral extends Literal {
        NullLiteral(Token token) {
            assert token.type() == Token.TokenType.NULL;
            this.token = token;
        }
    }
}
