package Objects;

import Helpers.*;

public abstract class Iterator extends Any {

    public Iterator() {
        super();
    }

    public abstract Any next();

    public abstract Bool hasNext();

    private static final Scope _classScope = new Scope(null) {{
        set(Var.__next__, new Func(f -> ((Iterator) f[0]).next()));
        set(Var.__hasnext__, new Func(f -> ((Iterator) f[0]).hasNext()));
    }};

    private Scope _scope;

    public static final Type type = new Type("ITERATOR");

}
