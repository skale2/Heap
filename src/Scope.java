import java.util.HashMap;
import java.util.Map;

public class Scope {
    public Scope(Scope parent) {
        _scope = new HashMap<>();
        _parent = parent;
    }

    public void set(String name, HObject object) {
        _scope.put(name, object);
    }

    public HObject lookup(String name, Boolean thisScope) {
        HObject object = _scope.get(name);
        if (object == null && thisScope) {
            return _parent.lookup(name, thisScope);
        }
        return object;
    }

    private Scope _parent;
    private Map<String, HObject> _scope;
}
