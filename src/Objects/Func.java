package Objects;

import java.util.List;

import Helpers.*;
import Main.*;

public class Func extends Any {
    Parser.ASTNode block;
    Scope scope;

    void create(Parser.Func def) {
        for (Parser.Assignment assignment : def.paramDefs.parameters) {

        }
    }

    void run(List<Any> arguments) {

    }

    public static final Type type = new Type("FUNC");
}
