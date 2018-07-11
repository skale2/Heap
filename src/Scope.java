import java.util.HashMap;
import java.util.Map;

public class Scope {
    Scope(Scope parent) {
        _scope = new HashMap<>();
        _parent = parent;
    }

    public void set(Var var, Any object) {
        _scope.put(var, object);
    }

    Any lookup(Var name, Boolean thisScope) {
        Any object = _scope.get(name);
        if (object == null && !thisScope && _parent != null) {
            return _parent.lookup(name, thisScope);
        }
        return object;
    }

    private Scope _parent;
    private Map<Var, Any> _scope;
}
