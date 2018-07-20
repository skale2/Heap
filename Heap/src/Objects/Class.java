package Objects;

import Helpers.*;
import java.util.List;

public class Class extends Type implements Construct {

    public Class(String name, Scope parentScope, List<Class> superclasses, List<Interface> interfaces) {
        super(name);
        _superclasses = superclasses;
        _interfaces = interfaces;
        _scope = new Scope(parentScope);
    }

    public Any get(Var var) {
        Any object = scope().get(var, true);
        if (object != null)
            return object;

        for (Class superclass : superclasses()) {
            object = superclass.scope().get(var, true);
            if (object != null)
                return object;
        }

        for (Interface anInterface : interfaces()) {
            object = anInterface.scope().get(var, true);
            if (object != null)
                return object;
        }

        return scope().get(var, false);
    }

    private List<Interface> _interfaces;
    private List<Class> _superclasses;

    /** Holds methods and attributes on the class level */
    private Scope _scope;

    public List<Interface> interfaces() {
        return _interfaces;
    }

    public List<Class> superclasses() {
        return _superclasses;
    }

    public Scope scope() {
        return _scope;
    }
}
