package Objects;

import Helpers.*;

public class Pointer extends Any {

    public Pointer(Container container, int index) {
        this._container = container;
        this._index = index;

        _scope.set(_container.get(_index).scope());
    }

    private static final Scope _classScope = new Scope(null) {{
        set(Var.__point__, new Func(f -> ((Pointer) f[0]).pointed()));
        set(Var.__total__, new Func(f -> ((Pointer) f[0]).container()));
        set(Var.__deref__, new Func(f -> ((Pointer) f[0]).value()));
    }};


    public void setIndex(int value) { _index += value; }

    public Any value() {
        try {
            return container().get(index());
        } catch (IndexOutOfBoundsException ibe) {
            return NULL.getInstance();
        }
    }

    public void setValue(Any value) {
        try {
            container().set(index(), value);
        } catch (IndexOutOfBoundsException ibe) {
            return;
        }
    }

    public Container container() {
        return _container;
    }

    public int index() {
        return _index;
    }

    public ListPointed pointed() {
        return _pointed;
    }


    private Container _container;
    private Scope _scope;
    private int _index;
    private ListPointed _pointed;



    static class ListPointed extends Any {
        private static ListPointed add (ListPointed pointed, Int amount) {
            Pointer pointer = pointed.pointer();
            return new ListPointed(new Pointer(pointer.container(), pointer.index() + amount.forceInt()));
        }

        public ListPointed(Pointer pointer) {
            this._pointer = pointer;
        }

        public Pointer pointer() {
            return _pointer;
        }

        private Pointer _pointer;
    }

}
