import java.io.BufferedReader;


/**
 * The main interpreter, which takes in a AST and runs through it, consuming
 * the nodes in a depth-first process.
 */
class Interpreter {
    Interpreter(BufferedReader text) {
        _lexer = new Lexer(text);
        _parser = new Parser(_lexer);
    }

    void run() {
        AST = _parser.parse();
    }

    private Parser.ASTNode AST;
    private Lexer _lexer;
    private Parser _parser;
}