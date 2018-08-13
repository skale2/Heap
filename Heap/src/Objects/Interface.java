package Objects;

import Helpers.*;

import java.util.List;

public class Interface extends Type implements Construct {
    public Interface(String name, Scope parentScope) {
        super(name);
        _scope = new Scope(parentScope, Scope.Enclosing.INTERFACE);
    }

    public List<Interface> interfaces() {
        return _interfaces;
    }

    public Scope scope() {
        return _scope;
    }

    private List<Interface> _interfaces;
    private Scope _scope;

    public static final Type type = new Type("INTERFACE");
}
