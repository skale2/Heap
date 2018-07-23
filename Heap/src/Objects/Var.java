package Objects;

import java.util.Objects;

public class Var {
    private String _name;
    private Type _type;
    
    private boolean isFinal;
    private boolean isSet;
    private boolean isPrivate;

    public String name() {
        return _name;
    }

    public Type type() {
        return _type;
    }

    public Var(String name, Type type) {
        _name = name;
        _type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Var var = (Var) o;
        return Objects.equals(_name, var._name) &&
                Objects.equals(_type, var._type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_name, _type);
    }

    public static final Var
            __add__ = new Var("__add__", Func.type),
            __sub__ = new Var("__sub__", Func.type),
            __mul__ = new Var("__mul__", Func.type),
            __div__ = new Var("__div__", Func.type),
            __mod__ = new Var("__mod__", Func.type),
            __floordiv__ = new Var("__floordiv__", Func.type),
            __pow__ = new Var("__pow__", Func.type),
            __round__ = new Var("__round__", Func.type),
            __and__ =  new Var("__and__", Func.type),
            __or__ =  new Var("__or__", Func.type),
            __not__ = new Var("__not__", Func.type),
            __xor__ = new Var("__xor__", Func.type),
            __bitand__ =  new Var("__bitand__", Func.type),
            __bitor__ =  new Var("__bitor__", Func.type),
            __bitnot__ = new Var("__bitnot__", Func.type),
            __bitxor__ = new Var("__bitxor__", Func.type),
            __incr__ = new Var("__incr__", Func.type),
            __decr__ = new Var("__decr__", Func.type),
            __neg__ = new Var("__neg__", Func.type),
            __rshift__ = new Var("__rshift__", Func.type),
            __lshift__ = new Var("__lshift__", Func.type),
            __less__ = new Var("__less__", Func.type),
            __greater = new Var("__greater", Func.type),
            __lesseq__ = new Var("__lesseq__", Func.type),
            __greatereq__ = new Var("__greatereq__", Func.type),
            __str__ = new Var("__str__", Func.type),
            __hash__ = new Var("__hash__", Func.type),
            __eq__ = new Var("__eq__", Func.type),
            __size__ = new Var("__size__", Func.type),
            __bool__ = new Var("__bool__", Func.type),
            __create__ = new Var("__create__", Func.type),
            __destroy__ = new Var("__destroy__", Func.type),
            __call__ = new Var("__call__", Func.type),
            __index__ = new Var("__index__", Func.type),
            __throw__ = new Var("__throw__", Func.type),
            __deref__ = new Var("__deref__", Func.type),
            __point__ = new Var("__point__", Func.type),
            __total__ = new Var("__total__", Func.type),
            __iter__ = new Var("__iter__", Func.type),
            __next__ = new Var("__next__", Func.type),
            __hasnext__ = new Var("__hasnext__", Func.type),

            add = new Var("add", Func.type);
}
