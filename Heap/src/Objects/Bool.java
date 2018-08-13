package Objects;

import Helpers.*;

public class Bool extends Atom {
    private boolean _value;

    public boolean value() {
        return _value;
    }

    public Bool(boolean value) {
        super();
        _value = value;
    }

    private Bool equal(Bool bool) {
        return valueOf(value() == bool.value());
    }

    private Bool and(Bool bool) {
        return valueOf(value() && bool.value());
    }

    private Bool or(Bool bool) {
        return valueOf(value() || bool.value());
    }

    private Bool not() {
        return valueOf(!value());
    }

    private Bool xor(Bool bool) {
        return valueOf(value() != bool.value());
    }

    public Str string() { return new Str(value() ? "true" : "false"); }

    public Bool bool() { return this; }

    /** Returns 1, 0, or -1 depending on whether left is greater, equal, or less
     * than right */
    public static int compare(Any left, Any right) {
        return 1; // TODO
    }


    private static final Scope _classScope = new Scope(null, Scope.Enclosing.CLASS) {{
        set(Var.__eq__, new Func(f -> ((Bool) f[0]).equal((Bool) f[1])));
        set(Var.__and__, new Func(f -> ((Bool) f[0]).and((Bool) f[1])));
        set(Var.__or__, new Func(f -> ((Bool) f[0]).or((Bool) f[1])));
        set(Var.__not__, new Func(f -> ((Bool) f[0]).not()));
        set(Var.__xor__, new Func(f -> ((Bool) f[0]).xor((Bool) f[1])));
        set(Var.__str__, new Func(f -> f[0].string()));
        set(Var.__bool__, new Func(f -> f[0].bool()));
    }};


    public static Bool valueOf(boolean bool) {
        return bool ? TRUE : FALSE;
    }

    public static boolean isTrue(Any any) { return any.bool().equals(TRUE); }

    public static Bool TRUE = new Bool(true);
    public static Bool FALSE = new Bool(false);

    private Scope _scope;

    public static final Type type = new Type("BOOL");

}
