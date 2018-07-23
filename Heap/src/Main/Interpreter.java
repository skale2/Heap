package Main;

import java.io.BufferedReader;
import java.lang.Boolean;
import java.lang.reflect.Array;
import java.util.*;

import Helpers.*;
import Objects.*;
import Objects.Class;

/**
 * The main interpreter, which takes in a AST and runs through it, consuming
 * the nodes in a depth-first process.
 */
public class Interpreter {
    public static void run(BufferedReader text) {
        var parser = new Parser(new Lexer(text));
        var ast = parser.parse();
        doBlock((Parser.Block) ast, new Scope(null));
    }

    public static Any doBlock(Parser.Block block, Scope scope) {
        var newScope = new Scope(scope);
        block.statements.forEach(statement -> doStatement((Parser.Statement) statement, newScope));
        return null;
    }

    private static void doStatement(Parser.Statement statement, Scope scope) {
        if (statement instanceof Parser.Assignment) {
            doAssignment((Parser.Assignment) statement, scope);
        }
    }

    private static void doAssignment(Parser.Assignment assignment, Scope scope) {

    }

    private static void doDeclare(Parser.Declare declare, Scope scope) {

    }

    private static Var doVar(Parser.Var var, Scope scope) {
        return null;
    }

    public static Any doExpression(Parser.Expression expression, Scope scope) {
        return null;
    }

    private static Any doCall(Parser.Call call, Scope scope) {
        return null;
    }

    /** **************************** Directs **************************** **/

    private static void doLoop(Parser.Loop loop) {

    }


    /** **************************** Containers **************************** **/

    private static Container doContainer(Parser.ContainerCreation container, Scope scope) {
        if (container instanceof Parser.HArrayList) {
            return doArrayList((Parser.HArrayList) container, scope);
        } else if (container instanceof Parser.HLinkedList) {
            return doLinkedList((Parser.HLinkedList) container, scope);
        } else if (container instanceof Parser.ArrayListRange) {
            return doArrayListRange((Parser.ArrayListRange) container, scope);
        } else if (container instanceof Parser.LinkedListRange) {
            return doLinkedListRange((Parser.LinkedListRange) container, scope);
        }
        return null;
    }

    private static HArrayList doArrayList(Parser.HArrayList list, Scope scope) {
        ArrayList<Any> arraylist = new ArrayList<>();
        for (Parser.Expression e : list.items)
            arraylist.add(doExpression(e, scope));
        return new HArrayList(arraylist);
    }

    private static HLinkedList doLinkedList(Parser.HLinkedList list, Scope scope) {
        LinkedList<Any> linkedlist = new LinkedList<>();
        for (Parser.Expression e : list.items)
            linkedlist.add(doExpression(e, scope));
        return new HLinkedList(linkedlist);
    }

    private static HArrayList doArrayListRange(Parser.ArrayListRange range, Scope scope) {
        int start = ((Int) doExpression(range.start(), scope)).forceInt();
        int stop = ((Int) doExpression(range.stop(), scope)).forceInt();
        int step = ((Int) doExpression(range.step(), scope)).forceInt();

        ArrayList<Any> arraylist = new ArrayList<>();
        for (int i = start; i < stop; i += step) {
            arraylist.add(new Int(i));
        }

        // TODO: catch errors for infinite loops, start/stop/step not being ints, etc
        return new HArrayList(arraylist);
    }

    private static HLinkedList doLinkedListRange(Parser.LinkedListRange range, Scope scope) {
        int start = ((Int) doExpression(range.start(), scope)).forceInt();
        int stop = ((Int) doExpression(range.stop(), scope)).forceInt();
        int step = ((Int) doExpression(range.step(), scope)).forceInt();

        LinkedList<Any> linkedlist = new LinkedList<>();
        for (int i = start; i < stop; i += step) {
            linkedlist.add(new Int(i));
        }

        // TODO: catch errors for infinite loops, start/stop/step not being ints, etc
        return new HLinkedList(linkedlist);
    }

    private static HMap doMap(Parser.HMap map, Scope scope) {
        if (map instanceof Parser.HValueMap) {
            return doValueMap((Parser.HValueMap) map, scope);
        } else if (map instanceof Parser.HObjectMap) {
            return doObjectMap((Parser.HObjectMap) map, scope);
        }
        return null;
    }

    private static HMap doObjectMap(Parser.HObjectMap map, Scope scope) {
        return null;
    }

    private static HValueMap doValueMap(Parser.HValueMap map, Scope scope) {
        return null;
    }


    /** **************************** Constructs **************************** **/

    private static Construct doConstruct(Parser.Construct construct, Scope scope) {
        if (construct instanceof Parser.Func) {
            return doFunc((Parser.Func) construct, scope);
        } else if (construct instanceof Parser.Class) {
            return doClass((Parser.Class) construct, scope);
        }
        return null;
    }

    private static Func doFunc(Parser.Func _func, Scope scope) {
        return new Func(_func, scope);
    }

    private static Class doClass(Parser.Class _class, Scope scope) {
        List<Interface> interfaces = new ArrayList<Interface>();
        _class.interfaces.forEach(v -> {
            var var = new Var(v.value, Interface.type);
            assert scope.has(var);
            interfaces.add((Interface) scope.get(var, false));
        });

        List<Class> superClasses = new ArrayList<>();
        _class.superClasses.forEach(v -> {
            var var = new Var(v.value, Class.type);
            assert scope.has(var);
            superClasses.add((Class) scope.get(var, false));
        });

        Scope classScope = new Scope(scope);
        doBlock(_class.block, scope);

        return new Class(null, classScope, superClasses, interfaces);
    }

    private static void doStruct(Parser.Struct _struct, Scope scope) {

    }

    private static void doInterface(Parser.Interface _interface, Scope scope) {

    }

    private static void doEnum(Parser.Enum _enum, Scope scope) {

    }


    /** **************************** Operations **************************** **/

    private static void doOperation(Parser.Op op, Scope scope) {
        if (op instanceof Parser.SetOp) {
            doSetOp((Parser.SetOp) op, scope);
        } else if (op instanceof Parser.Index) {
            doIndex((Parser.Index) op, scope);
        } else if (op instanceof Parser.Get) {
            doGet((Parser.Get) op, scope);
        } else if (op instanceof Parser.Range) {
            doRange((Parser.Range) op, scope);
        } else if (op instanceof Parser.Slice) {
            doSlice((Parser.Slice) op, scope);
        }

        if (op instanceof Parser.UnaryOp) {
            Parser.UnaryOp unaryOp = (Parser.UnaryOp) op;
            Any child = doExpression(unaryOp.child, scope);

            child.callMethod(_operations.get(unaryOp.token), false, scope, child);

        } else if (op instanceof Parser.BinaryOp) {
            Parser.BinaryOp binaryOp = (Parser.BinaryOp) op;
            Any left = doExpression(binaryOp.left, scope);
            Any right = doExpression(binaryOp.right, scope);

            left.callMethod(_operations.get(binaryOp.token), false, scope, left, right);
        }
    }

    private static void doSetOp(Parser.SetOp op, Scope scope) {

    }

    private static void doIndex(Parser.Index op, Scope scope) {

    }

    private static void doGet(Parser.Get op, Scope scope) {

    }

    private static void doRange(Parser.Range op, Scope scope) {

    }

    private static void doSlice(Parser.Slice op, Scope scope) {

    }


    private static final Map<Token, Var> _operations = new HashMap<>() {{
        put(Parser.values.get("ADD"), Var.__add__);
        put(Parser.values.get("SUBTRACT"), Var.__sub__);
        put(Parser.values.get("MULTIPLY"), Var.__mul__);
        put(Parser.values.get("DIVIDE"), Var.__div__);
        put(Parser.values.get("MOD"), Var.__mod__);
        put(Parser.values.get("FLOOR"), Var.__floordiv__);
        put(Parser.values.get("EXP"), Var.__add__);
        put(Parser.values.get("ROUND"), Var.__round__);
        put(Parser.values.get("INCREMENT"), Var.__incr__);
        put(Parser.values.get("DECREMENT"), Var.__decr__);
        put(Parser.values.get("L_AND"), Var.__and__);
        put(Parser.values.get("L_OR"), Var.__or__);
        put(Parser.values.get("L_NOT"), Var.__not__);
        put(Parser.values.get("L_XOR"), Var.__xor__);
        put(Parser.values.get("B_AND"), Var.__bitand__);
        put(Parser.values.get("B_OR"), Var.__bitor__);
        put(Parser.values.get("B_NOT"), Var.__bitnot__);
        put(Parser.values.get("B_XOR"), Var.__bitxor__);
        put(Parser.values.get("LESS_THAN"), Var.__less__);
        put(Parser.values.get("GREATER_THAN"), Var.__greater);
        put(Parser.values.get("LESS_THAN_EQ"), Var.__lesseq__);
        put(Parser.values.get("GREATER_THAN_EQ"), Var.__greatereq__);
        put(Parser.values.get("SHIFT_RIGHT"), Var.__rshift__);
        put(Parser.values.get("SHIFT_LEFT"), Var.__lshift__);
        put(Parser.values.get("EQUAL"), Var.__eq__);
        put(Parser.values.get("CAST_EQUAL"), Var.__eq__);
        put(Parser.values.get("NOT_EQUAL"), Var.__eq__);
        put(Parser.values.get("CAST_NOT_EQUAL"), Var.__eq__);
        put(Parser.values.get("DEREF"), Var.__deref__);
        put(Parser.values.get("TOTAL_REF"), Var.__total__);
    }};


    /** **************************** Literals **************************** **/

    private static Int doIntLiteral(Parser.IntLiteral intLiteral) {
        return new Int(intLiteral);
    }

    private static Str doStringLiteral(Parser.StringLiteral stringLiteral) {
        return new Str(stringLiteral);
    }

    private static Real doRealLiteral(Parser.RealLiteral realLiteral) {
        return new Real(realLiteral);
    }

    private static Bool doBoolLiteral(Parser.BooleanLiteral booleanLiteral) {
        return new Bool(Boolean.parseBoolean(booleanLiteral.token.value()));
    }

    private static NULL doNullLiteral(Parser.RealLiteral realLiteral) {
        return NULL.getInstance();
    }

}