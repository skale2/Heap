package Helpers;

import Objects.*;

import java.util.HashMap;
import java.util.Map;

public class Scope {
    public Scope(Scope parents, Enclosing enclosing) {
        _scope = new HashMap<>();
        _parent = parents;
        _enclosing = enclosing;
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

    public boolean isConstruct() {
        return _enclosing == Enclosing.FUNC ||
                _enclosing == Enclosing.CLASS ||
                _enclosing == Enclosing.INTERFACE ||
                _enclosing == Enclosing.ENUM ||
                _enclosing == Enclosing.MIXIN||
                _enclosing == Enclosing.STRUCT;
    }

    public boolean isDirect() {
        return _enclosing == Enclosing.IF ||
                _enclosing == Enclosing.ELSE ||
                _enclosing == Enclosing.LOOP ||
                _enclosing == Enclosing.TRY ||
                _enclosing == Enclosing.CATCH;
    }

    public Enclosing enclosing() { return _enclosing; }

    private Enclosing _enclosing;
    private Scope _parent;
    private Map<Var, Any> _scope;

    public enum Enclosing {
        MODULE, INNER,
        IF, ELSE, LOOP, TRY, CATCH,
        FUNC,
        CLASS, ENUM, INTERFACE, MIXIN, STRUCT;
    }
}
