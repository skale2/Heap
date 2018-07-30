package Objects;

import Helpers.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public abstract class HList extends Container {
    public HList() {
        super();
        _list = new ArrayList<>();
    }

    public HList(List<Any> list) {
        super();
        _list = list;
    }

    private HList add(HList list) {
        List<Any> retlist = getList();
        retlist.addAll(list());
        retlist.addAll(list.list());
        return createHList(retlist);
    }

    private NULL add(Any value) {
        assert value.type().isType(containerTypes());
        list().add(value);
        return NULL.getInstance();
    }

    private HList multiply(Int amount) {
        List<Any> retList = getList();
        IntStream.range(0, amount.forceInt())
                .forEach(i -> retList.addAll(list()));
        return createHList(retList);
    }

    private HList divide(Int amount) {
        assert size() % amount.forceInt() == 0;
        return createHList(list().subList(0, size() / amount.forceInt()));
    }

    private HList shiftRight(Int amount) {
        List<Any> retList = getList();
        if (size() < amount.forceInt()) {
            IntStream.range(0, size())
                    .forEach(i -> retList.add(NULL.getInstance()));
            return createHList(retList);
        }
        IntStream.range(0, amount.forceInt())
                .forEach(j -> retList.add(NULL.getInstance()));
        for (int i = 0; i < size() - amount.forceInt(); i++) {
            retList.add(list().get(i));
        }
        return createHList(retList);
    }

    private HList shiftLeft(Int amount) {
        List<Any> retList = getList();
        if (size() < amount.forceInt()) {
            IntStream.range(0, size())
                    .forEach(i -> retList.add(NULL.getInstance()));
            return createHList(retList);
        }
        for (int i = amount.forceInt(); i < size(); i++) {
            retList.add(list().get(i));
        }
        IntStream.range(0, amount.forceInt())
                .forEach(j -> retList.add(NULL.getInstance()));
        return createHList(retList);
    }

    private Bool lessThan(HList list) {
        for (int i = 0; size() > i && list.size() > i; i++) {
            if (Bool.compare(get(i), list.get(i)) == -1)
                return Bool.TRUE;
        }
        return Bool.FALSE;
    }

    private Bool lessThanOrEqualTo(HList list) {
        for (int i = 0; size() > i && list.size() > i; i++) {
            if (Bool.compare(get(i), list.get(i)) < 1)
                return Bool.TRUE;
        }
        return Bool.FALSE;
    }

    private Bool greaterThan(HList list) {
        for (int i = 0; size() > i && list.size() > i; i++) {
            if (Bool.compare(get(i), list.get(i)) == 1)
                return Bool.TRUE;
        }
        return Bool.FALSE;
    }

    private Bool greaterThanOrEqualTo(HList list) {
        for (int i = 0; size() > i && list.size() > i; i++) {
            if (Bool.compare(get(i), list.get(i)) > -1)
                return Bool.TRUE;
        }
        return Bool.FALSE;
    }

    private Str hash() {
        // TODO
        return new Str("");
    }

    private Bool equal(HList list) {
        if (size() != list.size())
            return Bool.FALSE;
        
        return Bool.valueOf(IntStream.range(0, size())
                .allMatch(i -> Bool.compare(get(i), list.get(i)) == 0));
    }

    public Bool bool() {
        return Bool.valueOf(size() > 0);
    }

    private Any index(Int index) {
        return get(index.forceInt());
    }


    private static final Scope _classScope = new Scope(null) {{
        set(Var.__add__, new Func(f -> ((HList) f[0]).add((HList) f[1])));
        set(Var.__mul__, new Func(f -> ((HList) f[0]).multiply((Int) f[1])));
        set(Var.__div__, new Func(f -> ((HList) f[0]).divide((Int) f[1])));
        set(Var.__rshift__, new Func(f -> ((HList) f[0]).shiftRight((Int) f[1])));
        set(Var.__lshift__, new Func(f -> ((HList) f[0]).shiftLeft((Int) f[1])));
        set(Var.__less__, new Func(f -> ((HList) f[0]).lessThan((HList) f[1])));
        set(Var.__greater, new Func(f -> ((HList) f[0]).greaterThan((HList) f[1])));
        set(Var.__lesseq__, new Func(f -> ((HList) f[0]).lessThanOrEqualTo((HList) f[1])));
        set(Var.__greatereq__, new Func(f -> ((HList) f[0]).greaterThanOrEqualTo((HList) f[1])));
        set(Var.__hash__, new Func(f -> ((HList) f[0]).hash()));
        set(Var.__eq__, new Func(f -> ((HList) f[0]).equal((HList) f[1])));
        set(Var.__bool__, new Func(f -> ((HList) f[0]).bool()));
        set(Var.__index__, new Func(f -> ((HList) f[0]).index((Int) f[1])));
        set(Var.add, new Func(f -> ((HList) f[0]).add(f[1])));
    }};

    static List<Any> getList() { return new ArrayList<>(); }

    static HList createHList(List<Any> list) { return null; }


    public static final Type type = new Type("LIST");


    public Any get(int index) {
        return list().get(index);
    }

    public void set(int index, Any obj) { list().add(index, obj); }
    
    public int size() { return list().size(); }

    public List<Any> list() {
        return _list;
    }

    public Type containerTypes() { return _containerTypes; }

    private Scope _scope;
    private Type _containerTypes;
    private Type _type;
    List<Any> _list;
}








