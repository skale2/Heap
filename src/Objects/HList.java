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

    private static HList add(HList first, HList second) {
        List<Any> list = getList();
        list.addAll(first.list());
        list.addAll(second.list());
        return createHList(list);
    }

    private static NULL add(HList list, Any value) {
        assert Type.isType(list.containerTypes(), value.type());
        list.list().add(value);
        return NULL.getInstance();
    }

    private static HList multiply(HList list, Int amount) {
        List<Any> retList = getList();
        IntStream.range(0, amount.forceInt())
                .forEach(i -> retList.addAll(list.list()));
        return createHList(retList);
    }

    private static HList divide(HList list, Int amount) {
        assert list.size() % amount.forceInt() == 0;
        return createHList(list.list().subList(0, list.size() / amount.forceInt()));
    }

    private static HList shiftRight(HList list, Int amount) {
        List<Any> retList = getList();
        if (list.size() < amount.forceInt()) {
            IntStream.range(0, list.size())
                    .forEach(i -> retList.add(NULL.getInstance()));
            return createHList(retList);
        }
        IntStream.range(0, amount.forceInt())
                .forEach(j -> retList.add(NULL.getInstance()));
        for (int i = 0; i < list.size() - amount.forceInt(); i++) {
            retList.add(list.list().get(i));
        }
        return createHList(retList);
    }

    private static HList shiftLeft(HList list, Int amount) {
        List<Any> retList = getList();
        if (list.size() < amount.forceInt()) {
            IntStream.range(0, list.size())
                    .forEach(i -> retList.add(NULL.getInstance()));
            return createHList(retList);
        }
        for (int i = amount.forceInt(); i < list.size(); i++) {
            retList.add(list.list().get(i));
        }
        IntStream.range(0, amount.forceInt())
                .forEach(j -> retList.add(NULL.getInstance()));
        return createHList(retList);
    }

    private static Bool lessThan(HList left, HList right) {
        for (int i = 0; left.size() > i && right.size() > i; i++) {
            if (Bool.compare(left.get(i), right.get(i)) == -1)
                return Bool.TRUE;
        }
        return Bool.FALSE;
    }

    private static Bool lessThanOrEqualTo(HList left, HList right) {
        for (int i = 0; left.size() > i && right.size() > i; i++) {
            if (Bool.compare(left.get(i), right.get(i)) < 1)
                return Bool.TRUE;
        }
        return Bool.FALSE;
    }

    private static Bool greaterThan(HList left, HList right) {
        for (int i = 0; left.size() > i && right.size() > i; i++) {
            if (Bool.compare(left.get(i), right.get(i)) == 1)
                return Bool.TRUE;
        }
        return Bool.FALSE;
    }

    private static Bool greaterThanOrEqualTo(HList left, HList right) {
        for (int i = 0; left.size() > i && right.size() > i; i++) {
            if (Bool.compare(left.get(i), right.get(i)) > -1)
                return Bool.TRUE;
        }
        return Bool.FALSE;
    }

    private static Str hash(HList list) {
        // TODO
        return new Str("");
    }

    private static Bool equal(HList first, HList second) {
        if (first.size() != second.size())
            return Bool.FALSE;
        
        return Bool.valueOf(IntStream.range(0, first.size())
                .allMatch(i -> Bool.compare(first.get(i), second.get(i)) == 0));
    }

    private static Bool bool(HList value) {
        return Bool.valueOf(value.size() > 0);
    }

    private static Any index(HList value, Int index) {
        return value.get(index.forceInt());
    }

    private static final Scope _classScope = new Scope(null) {{
        set(Var.__add__, new Func((f, s) -> add((HList) f, (HList) s)));
        set(Var.__mul__, new Func((f, s) -> multiply((HList) f, (Int) s)));
        set(Var.__div__, new Func((f, s) -> divide((HList) f, (Int) s)));
        set(Var.__rshift__, new Func((f, s) -> shiftRight((HList) f, (Int) s)));
        set(Var.__lshift__, new Func((f, s) -> shiftLeft((HList) f, (Int) s)));
        set(Var.__less__, new Func((f, s) -> lessThan((HList) f, (HList) s)));
        set(Var.__greater, new Func((f, s) -> greaterThan((HList) f, (HList) s)));
        set(Var.__lesseq__, new Func((f, s) -> lessThanOrEqualTo((HList) f, (HList) s)));
        set(Var.__greatereq__, new Func((f, s) -> greaterThanOrEqualTo((HList) f, (HList) s)));
        set(Var.__hash__, new Func(f -> hash((HList) f)));
        set(Var.__eq__, new Func((f, s) -> equal((HList) f, (HList) s)));
        set(Var.__bool__, new Func(f -> bool((HList) f)));
        set(Var.__index__, new Func((f, s) -> index((HList) f, (Int) s)));
        set(Var.add, new Func((f, s) -> add((HList) f, s)));
    }};

    static List<Any> getList() { return new ArrayList<>(); }

    static HList createHList(List<Any> list) { return null; }


    private static final Type type = new Type("LIST");


    public Any get(int index) {
        return list().get(index);
    }
    
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








