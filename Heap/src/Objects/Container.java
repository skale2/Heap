package Objects;

public abstract class Container extends Any {

    public abstract Any get(int index);

    public Any get(Int i) { return get(i.forceInt()); }

}
