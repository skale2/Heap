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

    private static Bool equal(Bool first, Bool second) {
        return valueOf(first.value() == second.value());
    }

    private static Bool and(Bool first, Bool second) {
        return valueOf(first.value() && second.value());
    }

    private static Bool or(Bool first, Bool second) {
        return valueOf(first.value() || second.value());
    }

    private static Bool not(Bool value) {
        return valueOf(!value.value());
    }

    private static Bool xor(Bool first, Bool second) {
        return valueOf(first.value() != second.value());
    }

    /** Returns 1, 0, or -1 depending on whether left is greater, equal, or less
     * than right */
    public static int compare(Any left, Any right) {
        return 1; // TODO
    }


    private static final Scope _classScope = new Scope(null) {{
        set(Var.__eq__, new Func((f, s) -> equal((Bool) f, (Bool) s)));
        set(Var.__and__, new Func((f, s) -> and((Bool) f, (Bool) s)));
        set(Var.__or__, new Func((f, s) -> or((Bool) f, (Bool) s)));
        set(Var.__not__, new Func(f -> not((Bool) f)));
        set(Var.__xor__, new Func((f, s) -> xor((Bool) f, (Bool) s)));
    }};


    public static Bool valueOf(boolean bool) {
        return bool ? TRUE : FALSE;
    }

    public static Bool TRUE = new Bool(true);
    public static Bool FALSE = new Bool(false);

    private Scope _scope;

}
