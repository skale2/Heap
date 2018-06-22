import java.io.BufferedReader;


/**
 * The main interpreter, which takes in a AST and runs through it, consuming
 * the nodes in a depth-first process.
 */
class Interpreter {
    Interpreter(BufferedReader text) {
        _parser = new Parser(new Lexer(text));
    }

    void run() {
        AST = _parser.parse();
    }

    private Parser.ASTNode AST;
    private Parser _parser;
}