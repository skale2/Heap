package Objects;

import Helpers.*;

public class Any {
    private static Scope _classScope = new Scope(null);
    private Scope _scope;
    private Type _type;

    public Any() {
        _type = type;
        _scope = new Scope(_classScope);
    }

    public Scope scope() {
        return _scope;
    }

    public Type type() { return _type; }

    public Any get(Var var) {
        return _scope.get(var, true);
    }

    public void set(Var var, Any value) {
        _scope.set(var, value);
    }

    public static final Type type = new Type("ANY");
}
