package Main;

import java.io.BufferedReader;
import Helpers.*;


/**
 * The main interpreter, which takes in a AST and runs through it, consuming
 * the nodes in a depth-first process.
 */
public class Interpreter {
    Interpreter(BufferedReader text) {
        _parser = new Parser(new Lexer(text));
    }

    public void run() {
        _ast = _parser.parse();
        doBlock((Parser.Block) _ast, new Scope(null));
    }

    private void doBlock(Parser.Block block, Scope scope) {
        Scope newScope = new Scope(scope);
        block.statements.forEach(statement -> doStatement((Parser.Statement) statement, newScope));
    }

    private void doStatement(Parser.Statement statement, Scope scope) {
        if (statement instanceof Parser.Assignment) {
            doAssignment((Parser.Assignment) statement);
        }
    }

    private void doAssignment(Parser.Assignment assignment) {

    }

    private void doDeclare(Parser.Declare declare) {

    }

    private void doVar(Parser.Var var) {

    }

    /** **************************** Containers **************************** **/

    private void doContainer(Parser.Container container) {

    }

    private void doArrayList(Parser.HArrayList list) {

    }

    private void doLinkedList(Parser.HLinkedList list) {

    }

    private void doDoubleLinkedList(Parser.HDoubleLinkedList list) {

    }

    private void doArrayListRange(Parser.ArrayListRange range) {

    }

    private void doLinkedListRange(Parser.LinkedListRange range) {

    }

    private void doDoubleLinkedListRange(Parser.DoubleLinkedListRange range) {

    }

    private void doMap(Parser.HMap map) {

    }

    private void doObjectMap(Parser.HObjectMap map) {

    }

    private void doValueMap(Parser.HValueMap map) {

    }


    /** **************************** Objects.Construct **************************** **/

    private void doConstruct(Parser.Construct construct) {

    }

    private void doFunc(Parser.Func _func) {

    }

    private void doClass(Parser.Class _class) {

    }

    private void doStruct(Parser.Struct _struct) {

    }

    private void doInterface(Parser.Interface _interface) {

    }

    private void doEnum(Parser.Enum _enum) {

    }


    /** **************************** Operations **************************** **/

    private void doOperation(Parser.Op op) {
        if (op instanceof Parser.UnaryOp) {
            Parser.UnaryOp unaryOp = (Parser.UnaryOp) op;

        }
    }


    private Parser.ASTNode _ast;
    private Parser _parser;
}