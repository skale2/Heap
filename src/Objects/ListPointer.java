package Objects;

import Helpers.*;

public class ListPointer extends Any {

    public ListPointer(HList list, int index) {
        super();
        this._list = list;
        this._index = index;

        Any object = list.get(index);

        /** Give pointer object the same functions as the underlying object */
        _scope.set(object.scope());
    }

    private static ListPointed getPointer(ListPointer pointer) {
        return pointer.pointed();
    }

    private static HList totalReference(ListPointer pointer) {
        return pointer.list();
    }

    private static Any dereference(ListPointer pointer) {
        return pointer.list().get(pointer.index());
    }

    private static final Scope _classScope = new Scope(null) {{
        set(Var.__point__, new Func(f -> getPointer((ListPointer) f)));
        set(Var.__total__, new Func(f -> totalReference((ListPointer) f)));
        set(Var.__deref__, new Func(f -> dereference((ListPointer) f)));
    }};


    public void setIndex(int value) { _index += value; }

    public Any value(int index) {
        try {
            return list().get(index);
        } catch (IndexOutOfBoundsException ibe) {
            return NULL.getInstance();
        }
    }

    public HList list() {
        return _list;
    }

    public int index() {
        return _index;
    }

    public ListPointed pointed() {
        return _pointed;
    }

    private HList _list;
    private Scope _scope;
    private int _index;
    private ListPointed _pointed;



    static class ListPointed extends Any {
        private static ListPointed add (ListPointed pointed, Int amount) {
            ListPointer pointer = pointed.pointer();
            return new ListPointed(new ListPointer(pointer.list(), pointer.index() + amount.forceInt());
        }

        public ListPointed(ListPointer pointer) {
            this._pointer = pointer;
        }

        public ListPointer pointer() {
            return _pointer;
        }

        private ListPointer _pointer;
    }

}
