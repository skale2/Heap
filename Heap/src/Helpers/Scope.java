package Helpers;

import Objects.*;

import java.util.HashMap;
import java.util.Map;

public class Scope {
    public Scope(Scope parents) {
        _scope = new HashMap<>();
        _parent = parents;
    }

    public boolean has(Var var) { return _scope.containsKey(var); }

    public boolean has(Var var, Type type) {
        return _scope.containsKey(var) && type.isType(var.type());
    }

    public void set(Var var, Any object) {
        _scope.put(var, object);
    }

    public void set(Scope scope) {
        scope._scope.entrySet().forEach(v -> set((Var) v, v.getValue()));
    }

    public Any get(Var name, boolean thisScope) {
        Any object = _scope.get(name);
        if (object == null && !thisScope && _parent != null) {
            return _parent.get(name, thisScope);
        }
        return object;
    }

    private Scope _parent;
    private Map<Var, Any> _scope;
}
