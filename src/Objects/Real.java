package Objects;

import Main.*;
import Helpers.*;

public class Real extends Atom {
    public Real(Parser.RealLiteral literal) {
    }

    private static final Type type = new Type("REAL");
}
