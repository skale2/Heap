package Objects;

import java.util.List;
import Helpers.*;

public class Slice extends HList {
    public Slice() {
        super();
    }

    public Slice(List<Any> list) {
        super(list);
    }

    public Slice(HList list, Int start, Int stop, Int step) {
        this();

        for (int i = start.forceInt(); i < stop.forceInt(); i += step.forceInt()) {
            _list.add(new Pointer(list, i));
        }
    }

    private static final Scope _classScope = new Scope(null) {{
        set(Var.__less__, new Func(f -> ((HList) f[0]).lessThan((HList) f[1])));
        set(Var.__greater, new Func(f -> ((HList) f[0]).greaterThan((HList) f[1])));
        set(Var.__lesseq__, new Func(f -> ((HList) f[0]).lessThanOrEqualTo((HList) f[1])));
        set(Var.__greatereq__, new Func(f -> ((HList) f[0]).greaterThanOrEqualTo((HList) f[1])));
        set(Var.__hash__, new Func(f -> ((HList) f[0]).hash()));
        set(Var.__eq__, new Func(f -> ((HList) f[0]).equal((HList) f[1])));
        set(Var.__bool__, new Func(f -> f[0].bool()));
        set(Var.__index__, new Func(f -> ((HList) f[0]).index((Int) f[1])));
        set(Var.add, new Func(f -> ((HList) f[0]).add(f[1])));
    }};

    public static final Type type = new Type("SLICE");


    public Any get(int index) {
        return ((Pointer) list().get(index)).value();
    }

    public void set(int index, Any obj) { ((Pointer) list().get(index)).setValue(obj); }

    private Scope _scope;
    private Type _containerTypes;
    private Type _type;
    private List<Pointer> _list;
}
