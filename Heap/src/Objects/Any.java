package Objects;

import Helpers.*;

public abstract class Any {
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
        return _scope.get(var, false);
    }

    public void set(Var var, Any value) {
        _scope.set(var, value);
    }

    public Any callMethod(Var methodName, boolean thisScope, Scope scope, Any... args) {
        try {
            var func = ((Func) scope().get(methodName, thisScope));
            if (scope != null) {
                func.setScope(scope);
            }
            return func.call(args);
        } catch (ClassCastException cce) {
            // TODO: Throw property is not a method error or something
            return NULL.getInstance();
        }
    }

    public Str string() {
        return new Str("Any");
    }

    public Bool bool() {
        return Bool.TRUE;
    }

    public boolean isNull() { return equals(NULL.getInstance()); }

    public static final Type type = new Type("ANY");
}
