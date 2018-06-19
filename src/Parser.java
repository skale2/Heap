
import java.util.*;

/**
 * A class that takes in a stream of tokens from the Lexer and parses them
 * into an Abstract Syntax Tree (AST), which is easier to run through
 * when interpreting.
 */
public class Parser {

    public Parser(Lexer lexer) {
        this._lexer = lexer;
        this._current = null;
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
        }
    }

    private Assignment parseAssignment() {}

    private List<Type> parseType(Token endToken) {
        List<Type> types = new ArrayList<>();

        while(!current().equals(endToken)) {

            /* Check if is a pointer */
            boolean isPointer = false;
            if (current().type() == Token.TokenType.MULTIPLY) {
                eat(values.get("MULTIPLY"));
                isPointer = true;
            }

            /* Check if is a container */
            List<String> containerTypes = new ArrayList<String>() {{
                add("ARR_OPEN"); add("SCOPE_OPEN"); add("SET_OPEN");
                add("UNDIR_OPEN"); add("DIR_OPEN");
            }};

            if (containerTypes.contains(current().type().toString())) {
                types.add(parseContainerType(current().type().toString(), isPointer));
                continue;
            }

            /* Check if is a shorthand for graph */
            if (current().type() == Token.TokenType.DIR_TYPE) {
                eat(values.get("DIR_GRAPH_TYPE"));
                List<Type> containedType = new ArrayList<>();
                types.add(new ContainerType(isPointer, ContainerType.Container.DIR, containedType));
                continue;
            } else if (current().type() == Token.TokenType.UNDIR_TYPE) {
                eat(values.get("UNDIR_GRAPH_TYPE"));
                List<Type> containedType = new ArrayList<>();
                types.add(new ContainerType(isPointer, ContainerType.Container.UNDIR, containedType));
                continue;
            }

            /* Check current is either a built-in type, or a user made type
            * that isn't reserved */
            assert current().isType() || !current().isReserved();
            types.add(new Type(current(), isPointer));
            eat(current());

            eat(values.get("COMMA"));
        }

        return types;
    }

    private ContainerType parseContainerType(String containerType, boolean isPointer) {
        eat(values.get(containerType + "_OPEN"));
        List<Type> containerTypes =  parseType(values.get(containerType + "_CLOSE"));
        eat(values.get(containerType + "_CLOSE"));
        return new ContainerType(isPointer, ContainerType.Container.valueOf(containerType), containerTypes);
    }

    private Params parseParams(Token endBlock) {
        List<Assignment> params = new ArrayList<>();
        while (!current().equals(endBlock)) {
            params.add(parseAssignment());
            eat(values.get("COMMA"));
        }
        return new Params(params);
    }

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
        eat(values.get("LOOP"));
        eat(values.get("PAR_OPEN"));

        List<Assignment> initClauses = new ArrayList<>();
        while(!currentIs("COLON")) {
            initClauses.add(parseAssignment());
            if (currentIs("COMMA")) {
                eat(values.get("COMMA"));
            }
        }
        eat(values.get("COLON"));

        List<Expression> breakClauses = new ArrayList<>();
        while(!currentIs("COLON")) {
            breakClauses.add(parseExpression(values.get("COMMA"), values.get("COLON")));
            if (currentIs("COMMA")) {
                eat(values.get("COMMA"));
            }
        }
        eat(values.get("COLON"));

        List<Expression> loopClauses = new ArrayList<>();
        while(!currentIs("PAR_CLOSE")) {
            loopClauses.add(parseExpression(values.get("COMMA"), values.get("PAR_CLOSE")));
            if (currentIs("COMMA")) {
                eat(values.get("COMMA"));
            }
        }

        eat(values.get("PAR_CLOSE"));

        eat(values.get("DIRECT"));

        eat(values.get("SCOPE_OPEN"));
        Block loopBlock = parseBlock(values.get("SCOPE_CLOSE"));
        eat(values.get("SCOPE_CLOSE"));

        Block elseBlock = null;
        if (currentIs("ELSE")) {
            eat(values.get("SCOPE_OPEN"));
            elseBlock = parseBlock(values.get("SCOPE_CLOSE"));
            eat(values.get("SCOPE_CLOSE"));
        }

        return new Loop(initClauses, breakClauses, loopClauses, loopBlock, elseBlock);
    }

    private If parseIf() {
        eat(values.get("IF"));

        eat(values.get("PAR_OPEN"));
        Expression expression = parseExpression(values.get("PAR_CLOSE"));
        eat(values.get("PAR_CLOSE"));

        eat(values.get("DIRECT"));

        eat(values.get("SCOPE_OPEN"));
        Block block = parseBlock(values.get("SCOPE_CLOSE"));
        eat(values.get("SCOPE_CLOSE"));

        List<IfBlock> ifblocks = new ArrayList<IfBlock>() {{
            add(new IfBlock(expression, block));
        }};

        /* Create an else block to be populated */
        Block elseBlock = null;

        /* Check if (an) else if(s), or just an else, exist */
        if (currentIs("ELSE")) {
            eat(values.get("ELSE"));

            if (currentIs("SCOPE_OPEN")) {
                /* If just an else */
                eat(values.get("DIRECT"));

                eat(values.get("SCOPE_OPEN"));
                elseBlock = parseBlock(values.get("SCOPE_CLOSE"));
                eat(values.get("SCOPE_CLOSE"));
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

        eat(values.get("PAR_OPEN"));
        Expression switchExpression = parseExpression(values.get("PAR_CLOSE"));
        eat(values.get("PAR_CLOSE"));

        eat(values.get("DIRECT"));

        eat(values.get("SCOPE_OPEN"));

        List<Case> cases = new ArrayList<>();
        Block defaultBlock = null;
        Block elseBlock = null;

        while (!currentIs("SCOPE_CLOSE")) {
            if (currentIs("CASE")) {
                eat(values.get("CASE"));
                Expression caseExpression = parseExpression(values.get("COLON"));
                eat(values.get("COLON"));
                Block caseBlock = parseBlock(values.get("CASE"), values.get("DEFAULT"));

                Case newCase = new Case(caseExpression, caseBlock);
                cases.add(newCase);
            } else if (currentIs("DEFAULT")) {
                eat(values.get("DEFAULT"));
                eat(values.get("COLON"));

                defaultBlock = parseBlock(values.get("SCOPE_CLOSE"));
            }
        }

        eat(values.get("SCOPE_CLOSE"));

        if (currentIs("ELSE")) {
            eat(values.get("SCOPE_OPEN"));
            elseBlock = parseBlock(values.get("SCOPE_CLOSE"));
            eat(values.get("SCOPE_CLOSE"));
        }

        if (startToken.type() == Token.TokenType.SWITCH) {
            return new Switch(switchExpression, cases, defaultBlock, elseBlock);
        } else if (startToken.type() == Token.TokenType.SELECT) {
            return new Select(switchExpression, cases, defaultBlock, elseBlock);
        } return null;
    }

    private Try parseTry() {
        eat(values.get("TRY"));

        eat(values.get("SCOPE_OPEN"));
        Block tryBlock = parseBlock(values.get("SCOPE_CLOSE"));
        eat(values.get("SCOPE_CLOSE"));

        Declare exception;
        Block catchBlock, elseBlock = null;
        List<Catch> catches = new ArrayList<>();

        while (currentIs("CATCH")) {
            eat(values.get("CATCH"));

            eat(values.get("PAR_OPEN"));
            exception = parseDeclare();
            eat(values.get("PAR_CLOSE"));

            eat(values.get("SCOPE_OPEN"));
            catchBlock = parseBlock(values.get("SCOPE_CLOSE"));
            eat(values.get("SCOPE_CLOSE"));

            catches.add(new Catch(exception, catchBlock));
        }

        if (currentIs("ELSE")) {
            eat(values.get("SCOPE_OPEN"));
            elseBlock = parseBlock(values.get("SCOPE_CLOSE"));
            eat(values.get("SCOPE_CLOSE"));
        }

        return new Try(tryBlock, catches, elseBlock);
    }

    private Func parseFunc() {
        eat(values.get("FUNC"));

        eat(values.get("PAR_OPEN"));
        Params params = parseParams(values.get("PAR_CLOSE"));
        eat(values.get("PAR_CLOSE"));

        eat(values.get("DIRECT"));

        eat(values.get("SCOPE_OPEN"));
        Block block = parseBlock(values.get("SCOPE_CLOSE"));
        eat(values.get("SCOPE_CLOSE"));

        return new Func(params, block);
    }

    private Expression parseExpression(Token... endTokens) {}

    private Declare parseDeclare() {
        List<Modifier> modifiers = new ArrayList<>();
        while (current().isModifier()) {
            modifiers.add(new Modifier(current()));
            eat(current());
        }

        Var var = new Var(current());
        eat(current());

        eat(values.get("LESS_THAN"));
        List<Type> types = parseType(values.get("GREATER_THAN"));
        eat(values.get("GREATER_THAN"));

        return new Declare(modifiers, var, types);
    }

    private HArrayList parseArrayList() {
        eat(values.get("ARR_OPEN"));
        List<Expression> items = new ArrayList<>();
        while (!currentIs("ARR_CLOSE")) {
            items.add(parseExpression(values.get("COMMA")));
            eat(values.get("COMMA"));
        }
        eat(values.get("ARR_CLOSE"));
        return new HArrayList(items);
    }

    private HLinkedList parseLinkedList() {
        eat(values.get("L_ARR_OPEN"));
        List<Expression> items = new ArrayList<>();
        while (!currentIs("ARR_CLOSE")) {
            items.add(parseExpression(values.get("COMMA")));
            eat(values.get("COMMA"));
        }
        eat(values.get("ARR_CLOSE"));
        return new HLinkedList(items);
    }

    private HDoubleLinkedList parseDoubleLinkedList() {
        eat(values.get("DL_ARR_OPEN"));
        List<Expression> items = new ArrayList<>();
        while (!currentIs("ARR_CLOSE")) {
            items.add(parseExpression(values.get("COMMA")));
            eat(values.get("COMMA"));
        }
        eat(values.get("ARR_CLOSE"));
        return new HDoubleLinkedList(items);
    }

    private HMap parseMap() {
        eat(values.get("SCOPE_OPEN"));

        Map<Expression, Expression> items = new HashMap<>();
        while (!currentIs("SCOPE_CLOSE")) {
            Expression key = parseExpression(values.get("COLON"));
            Expression value = parseExpression(values.get("COMMA"));
            items.put(key, value);
        }

        eat(values.get("SCOPE_CLOSE"));
        return new HMap(items);
    }

    private HDirectedGraph parseDirectedGraph() {
        eat(values.get("DIR_OPEN"));

        List<Expression> nodes = new ArrayList<>();
        while (!currentIs("EOL")) {
            nodes.add(parseExpression(values.get("COMMA")));
            eat(values.get("COMMA"));
        }
        eat(values.get("EOL"));

        List<HDirectedGraph.HDirectedEdge> edges = new ArrayList<>();
        while (!currentIs("DIR_CLOSE")) {
            Expression first = parseExpression(values.get("DIR_EDGE"),
                    values.get("DIR_2_EDGE"));

            assert currentIs("DIR_EDGE") || currentIs("DIR_2_EDGE");
            boolean doubleEdge = currentIs("DIR_EDGE");
            eat(current());

            Expression second = parseExpression(values.get("COMMA"));

            edges.add(new HDirectedGraph.HDirectedEdge(first, second, doubleEdge));
            eat(values.get("COMMA"));
        }

        eat(values.get("DIR_CLOSE"));
        return new HDirectedGraph(nodes, edges);
    }

    private HUndirectedGraph parseUndirectedGraph() {
        eat(values.get("UNDIR_OPEN"));

        List<Expression> nodes = new ArrayList<>();
        while (!currentIs("EOL")) {
            nodes.add(parseExpression(values.get("COMMA")));
            eat(values.get("COMMA"));
        }
        eat(values.get("EOL"));

        List<HGraph.HEdge> edges = new ArrayList<>();
        while (!currentIs("DIR_CLOSE")) {
            Expression first = parseExpression(values.get("DIR_EDGE"),
                    values.get("DIR_2_EDGE"));

            eat(values.get("MINUS"));

            Expression second = parseExpression(values.get("COMMA"));

            edges.add(new HGraph.HEdge(first, second));
            eat(values.get("COMMA"));
        }

        eat(values.get("UNDIR_CLOSE"));
        return new HUndirectedGraph(nodes, edges);
    }




    private Token current() {
        return _current;
    }

    private void current(Token current) {
        this._current = current;
    }

    private boolean currentIs(String name) {
        return current().equals(values.get(name));
    }

    private boolean currentIs(Token token) {
        return current().type() == token.type();
    }

    private Lexer _lexer;
    private Token _current;

    /* A map of usable tokens that aren't dependent on user code, for comparisons. Avoids the
     * creation of unnecessary tokens. */
    private static final Map<String, Token> values = new HashMap<String, Token>() {{
        for (Token.TokenType type : Token.TokenType.values()) {
            if (type == Token.TokenType.VAR || type == Token.TokenType.INT_VAL ||
                    type == Token.TokenType.STR_VAL ||  type == Token.TokenType.REAL_VAL) {
                put(type.value(), new Token(type));
            }
        }
    }};




    /** The base AST node class */
    static abstract class ASTNode {
        Token token;
    }

    /** A sequential list of statements */
    static final class Block extends ASTNode {
        List<? extends ASTNode> statements;

        Block(List<? extends ASTNode> statements) {
            this.statements = statements;
        }
    }

    /** A complete line of instruction */
    static abstract class Statement extends ASTNode {}

    /** Assigns a variable a value */
    static final class Assignment extends Statement {
        ASTNode var, value;

        public Assignment(ASTNode var, ASTNode value) {
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
            this.modifier = modifier;
            this.var = var;
            this.type = type;
        }
    }

    /** A series of logical steps that returns a value */
    static abstract class Expression extends ASTNode {

    } // TODO


    /** A variable identifier */
    static final class Var extends ASTNode {
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
            assert token.isType() || token.type() == Token.TokenType.VAR;
            this.token = token;
            this.isPointer = isPointer;
        }

    } // TODO

    static final class ContainerType extends Type {
        Container container;
        List<Type> types;

        ContainerType(boolean isPointer, Container container, List<Type> types) {
            super(null, isPointer);
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

    /** A list of parameters, which are syntatically Declares */
    static final class Params extends ASTNode {
        List<Assignment> parameters;

        Params(List<Assignment> parameters) {
            this.parameters = parameters;
        }
    }


    /** Object-building blocks */
    static abstract class Construct extends Statement {}

    /** A defined operation taking a Param node */
    static final class Func extends Construct {
        Params params;
        Block block;

        Func(Params params, Block block) {
            this.token = new Token(Token.TokenType.FUNC);
            this.params = params;
            this.block = block;
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
            this.expression = expression;
            this.block = block;
        }
    }

    /** A block that may throw an Exception */
    static final class Try extends Direct {
        Block block;
        List<Catch> catchBlocks;

        Try(Block block, List<Catch> catchBlocks, Block elseBlock) {
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
            Exception = exception;
            this.block = block;
        }
    }


    /** Container nodes */
    static abstract class Container extends ASTNode {}

    /** A dynamic array of objects */
    static class HList extends Container {
        List<Expression> items;
    }

    /** A dynamic array of objects with a dynamic array implementation */
    static final class HArrayList extends HList {
        List<Expression> items;

        HArrayList(List<Expression> items) {
            this.items = items;
            this.token = values.get("ARR_OPEN");
        }
    }

    /** A dynamic array of objects with a linked list implementation */
    static final class HLinkedList extends HList {
        List<Expression> items;

        HLinkedList(List<Expression> items) {
            this.items = items;
            this.token = values.get("L_ARR_OPEN");
        }
    }

    /** A dynamic array of objects with a doubly linked list implementation */
    static final class HDoubleLinkedList extends HList {
        List<Expression> items;

        HDoubleLinkedList(List<Expression> items) {
            this.items = items;
            this.token = values.get("DL_ARR_OPEN");
        }
    }

    /** A bijective mapping between objects */
    static final class HMap extends Container {
        Map<Expression, Expression> items;

        HMap(Map<Expression, Expression> items) {
            this.items = items;
            this.token = Parser.values.get("SCOPE_OPEN");
        }
    }

    /** A unique set of objects */
    static final class HSet extends Container {
        List<Expression> items;

        HSet(List<Expression> items, Token token) {
            this.items = items;
            this.token = token;
        }
    }

    /** A base class for collection of objects and edges connecting them */
    static abstract class HGraph extends Container {
        List<Expression> nodes;
        List<? extends HEdge> edges;

        HGraph(List<Expression> nodes, List<? extends HEdge> edges, Token token) {
            this.nodes = nodes;
            this.edges = edges;
            this.token = token;
        }

        static class HEdge extends ASTNode {
            ASTNode first, second;

            HEdge(Expression first, Expression second) {
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




    /** Operations */
    static final class UnaryOp extends ASTNode {
        ASTNode child;

        public UnaryOp(ASTNode child, Token token) {
            this.child = child;
            this.token = token;
        }
    }

    static final class BinaryOp extends ASTNode {
        ASTNode left, right;

        public BinaryOp(ASTNode left, ASTNode right, Token token) {
            this.left = left;
            this.right = right;
            this.token = token;
        }
    }

    static final class TernaryOp extends ASTNode {
        ASTNode left, center, right;

        public TernaryOp(ASTNode left, ASTNode center, ASTNode right, Token token) {
            this.left = left;
            this.center = center;
            this.right = right;
            this.token = token;
        }
    }

}
