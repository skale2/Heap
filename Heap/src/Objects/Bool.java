package Objects;

import Helpers.*;

public class Bool extends Atom {
    private boolean _value;

    public boolean value() {
        return _value;
    }

    public Bool(boolean _value) {
        super();
        this._value = _value;
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

    /** Returns 1, 0, or -1 depending on whether left is greater, equal, or less
     * than right */
    public static int compare(Any left, Any right) {
        return 1; // TODO
    }


    private static final Scope _classScope = new Scope(null) {{
        set(Var.__eq__, new Func(f -> ((Bool) f[0]).equal((Bool) f[1])));
        set(Var.__and__, new Func(f -> ((Bool) f[0]).and((Bool) f[1])));
        set(Var.__or__, new Func(f -> ((Bool) f[0]).or((Bool) f[1])));
        set(Var.__not__, new Func(f -> ((Bool) f[0]).not()));
        set(Var.__xor__, new Func(f -> ((Bool) f[0]).xor((Bool) f[1])));
    }};


    public static Bool valueOf(boolean bool) {
        return bool ? TRUE : FALSE;
    }

    public static Bool TRUE = new Bool(true);
    public static Bool FALSE = new Bool(false);

    private Scope _scope;

}
