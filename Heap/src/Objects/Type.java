package Objects;

import java.util.Objects;

public class Type extends Any {
    private String _name;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Type type = (Type) o;
        return Objects.equals(_name, type._name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_name);
    }

    public Type(String name) {
        _name = name;
    }

    public String name() {
        return _name;
    }

    public boolean isType(Type type) {
        return false; // TODO
    }
}
