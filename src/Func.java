import java.util.List;

public class Func extends Any {
    Parser.ASTNode block;
    Scope scope;

    void create(Parser.Func def) {
        for (Parser.Assignment assignment : def.paramDefs.parameters) {

        }
    }

    void run(List<Any> arguments) {

    }
}
