package Objects;

import Helpers.*;

public class Pointer extends Any {

    public Pointer(Container container, int index) {
        this._container = container;
        this._index = index;

        if (!value().isNull()) {
            _scope.set(value().scope());
        }
    }

    private static final Scope _classScope = new Scope(null) {{
        set(Var.__point__, new Func(f -> ((Pointer) f[0]).pointed()));
        set(Var.__total__, new Func(f -> ((Pointer) f[0]).container()));
        set(Var.__deref__, new Func(f -> ((Pointer) f[0]).value()));
    }};

    @Override
    public void callMethod(Var methodName, boolean thisScope, Any... args) {
        /* Because the first argument to the Func will be the instance itself, so dereference */
        args[0] = args[0] instanceof Pointer ? ((Pointer) args[0]).value() : args[0];
        super.callMethod(methodName, thisScope, args);
    }


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
            scope().set(value.scope());
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

    public Pointed pointed() {
        return _pointed;
    }


    private Container _container;
    private Scope _scope;
    private int _index;
    private Pointed _pointed;

}
