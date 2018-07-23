package Objects;

import Helpers.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HMap extends Container {
    public HMap(HashMap<Any, Any> _map) {
        _map = _map;
    }

    public HMap() {
        _map = new HashMap<>();
    }

    private HMap add(HMap map) {
        HMap newMap = new HMap();
        for (Any key : keys())
            newMap.set(key, index(key));
        for (Any key : map.keys())
            newMap.set(key, map.index(key));
        return newMap;
    }



    public Any index(Any any) {
        return map().get(any);
    }

    @Override
    public Any get(int index) {
        return map().get(keys().get(index));
    }

    public NULL set(Any key, Any val) {
        if (map().containsKey(key))
            keys().add(key);
        map().put(key, val);
        return NULL.getInstance();
    }

    public NULL set(int index, Any key, Any val) {
        if (map().containsKey(key))
            keys().add(index, key);
        map().put(key, val);
        return NULL.getInstance();
    }

    public static final Type type = new Type("MAP");

    private static final Scope _classScope = new Scope(null) {{
        set(Var.__add__, new Func(f -> ((HMap) f[0]).add((HMap) f[1])));
        set(Var.__index__, new Func(f -> ((HMap) f[0]).index((Any) f[1])));
        set(Var.add, new Func(f -> ((HMap) f[0]).set(f[1], f[2])));
    }};

    public Map<Any, Any> map() {
        return _map;
    }

    public ArrayList<Any> keys() {
        return _keys;
    }

    private ArrayList<Any> _keys;
    private Map<Any, Any> _map;
}
