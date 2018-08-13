package Objects;

import java.util.Objects;

public class Var extends Any {
    public enum Privilege {
        PRIVATE, PROTECTED, MODULE, PUBLIC
    }

    private String _name;
    private Type _type;
    private Privilege _privilege;
    private boolean _final, _abstract;

    public String name() {
        return _name;
    }

    public Type type() {
        return _type;
    }

    public Privilege privilege() { return _privilege; }

    public boolean isFinal() { return _final; }

    public boolean isAbstract() { return _abstract; }


    public Var(String name, Type type) {
        _name = name;
        _type = type;
        _privilege = Privilege.PUBLIC;
        _final = false;
        _abstract = false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Var var = (Var) o;
        return Objects.equals(_name, var._name) &&
                Objects.equals(_type, var._type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_name, _type);
    }

    public static final Var
            __add__ = new Var("__add__", Func.type),             // This object added to another
            __sub__ = new Var("__sub__", Func.type),             // This object subtracted from by another
            __mul__ = new Var("__mul__", Func.type),             // This object multiplied by another
            __div__ = new Var("__div__", Func.type),             // This object divided by another
            __mod__ = new Var("__mod__", Func.type),             // This object modded by an integer amount
            __floordiv__ = new Var("__floordiv__", Func.type),   // This object floor divided with another object
            __pow__ = new Var("__pow__", Func.type),             // This object powered to a number
            __round__ = new Var("__round__", Func.type),         // This object rounded to an integer amount
            __and__ =  new Var("__and__", Func.type),            // This object logically and'd with another
            __or__ =  new Var("__or__", Func.type),              // This object logically or'd with another
            __not__ = new Var("__not__", Func.type),             // The logical not of this object
            __xor__ = new Var("__xor__", Func.type),             // This object logically xor'd with another
            __bitand__ =  new Var("__bitand__", Func.type),      // This object bitwise and'd with another
            __bitor__ =  new Var("__bitor__", Func.type),        // This object bitwise or'd with another
            __bitnot__ = new Var("__bitnot__", Func.type),       // The bitwise not of this object
            __bitxor__ = new Var("__bitxor__", Func.type),       // This object bitwise xor'd with another
            __incr__ = new Var("__incr__", Func.type),           // Increments this object
            __decr__ = new Var("__decr__", Func.type),           // Decrements this object
            __neg__ = new Var("__neg__", Func.type),             // Negates this object
            __rshift__ = new Var("__rshift__", Func.type),       // This object shifted right by an integer amount
            __lshift__ = new Var("__lshift__", Func.type),       // This object shifted left by an integer amount
            __less__ = new Var("__less__", Func.type),           // Whether this object is less than another
            __greater = new Var("__greater", Func.type),         // Whether this object is greater than another
            __lesseq__ = new Var("__lesseq__", Func.type),       // Whether this object is less than or equal to another
            __greatereq__ = new Var("__greatereq__", Func.type), // Whether this object is greater than or equal to another
            __str__ = new Var("__str__", Func.type),             // The string representation of this object
            __hash__ = new Var("__hash__", Func.type),           // Hash code of this object
            __eq__ = new Var("__eq__", Func.type),               // Whether this object is equal to another
            __size__ = new Var("__size__", Func.type),           // The size of this object */
            __bool__ = new Var("__bool__", Func.type),           // The boolean value of this object */
            __create__ = new Var("__create__", Func.type),       // Constructor for the object
            __destroy__ = new Var("__destroy__", Func.type),     // Destructor to be called when garbage collector deletes this object
            __call__ = new Var("__call__", Func.type),           // Makes this callable
            __index__ = new Var("__index__", Func.type),         // Makes this index-able
            __throw__ = new Var("__throw__", Func.type),         // Makes this a Throwable
            __deref__ = new Var("__refer__", Func.type),         // Either references this object, or dereferences the resulting pointer
            __point__ = new Var("__point__", Func.type),         // Exposes pointer functionality for a pointer to this object
            __total__ = new Var("__total__", Func.type),         // Returns the underlying object of a pointer

            add = new Var("add", Func.type);
}
