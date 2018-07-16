package Objects;

import Main.*;
import Helpers.*;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Objects;

public class Str extends Atom {
    private String _string;
    private Scope _scope;

    public String value() {
        return _string;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Str)) return false;
        Str str = (Str) o;
        return Objects.equals(value(), str.value());
    }

    public Str(String string) {
        super();
        _string = string;
    }

    public Str(Parser.StringLiteral string) {
        super();
        _string = string.token.value();
    }

    static Str add(Str first, Str second) {
        return new Str(first.value() + second.value());
    }

    static Str multiply(Str string, Int times) {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < times.forceLong(); i++) {
            s.append(string.value());
        }
        return new Str(s.toString());
    }

    static Str divide(Str string, Int times) {
        String value = string.value();
        assert value.length() % times.forceInt() == 0;
        return new Str(value.substring(0, value.length()/times.forceInt()));
    }

    static Str mod(Str string, Int times) {
        String value = string.value();
        int len = string.value().length() % times.forceInt();
        return new Str(value.substring(value.length() - len));
    }

    static Str floorDivide(Str string, Int times) {
        String value = string.value();
        return new Str(value.substring(0, Math.floorDiv(value.length(), times.forceInt())));
    }

    static Str shiftRight(Str value, Int amount) {
        String strValue = value.value();
        if (amount.forceInt() > strValue.length()) {
            return new Str(new String(new char[strValue.length()]).replace("\0", " "));
        }

        return new Str(
                new String(new char[amount.forceInt()]).replace("\0", " ") +
                        strValue.substring(0, strValue.length() - amount.forceInt())
        );
    }

    static Str shiftLeft(Str value, Int amount) {
        String strValue = value.value();
        if (amount.forceInt() > strValue.length()) {
            return new Str(new String(new char[strValue.length()]).replace("\0", " "));
        }

        return new Str(
                strValue.substring(strValue.length() - amount.forceInt(), strValue.length()) +
                        new String(new char[amount.forceInt()]).replace("\0", " ")
        );
    }

    static Bool lessThan(Str Str, Str right) {
        
    }

    static Bool lessThanOrEqualTo(Str left, Str right) {

    }

    static Bool greaterThan(Str left, Str right) {

    }

    static Bool greaterThanOrEqualTo(Str left, Any HString) {

    }

    static Int size(Str value) { return new Int(value.value().length()); }

    static Str string(Str value) {
        return value;
    }

    static Str hash(Str value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(value.value().getBytes(StandardCharsets.UTF_8));
            return new Str(Base64.getEncoder().encodeToString(hash));

        } catch (java.security.NoSuchAlgorithmException snme) {
            return new Str("");
        }
    }

    static Bool equal(Str first, Str second) {
        return Bool.valueOf(first.equals(second));
    }

    static Bool bool(Str value) {
        return Bool.valueOf(!value.value().isEmpty());
    }

    private static final Scope _classScope = new Scope(null) {{
        set(Var.__add__, new Func((f, s) -> add((Str) f, (Str) s)));
        set(Var.__mul__, new Func((f, s) -> multiply((Str) f, (Int) s)));
        set(Var.__div__, new Func((f, s) -> divide((Str) f, (Int) s)));
        set(Var.__mod__, new Func((f, s) -> mod((Str) f, (Int) s)));
        set(Var.__floordiv__, new Func((f, s) -> floorDivide((Str) f, (Int) s)));
        set(Var.__rshift__, new Func((f, s) -> shiftRight((Str) f, (Int) s)));
        set(Var.__lshift__, new Func((f, s) -> shiftLeft((Str) f, (Int) s)));
        set(Var.__less__, new Func((f, s) -> lessThan((Str) f, (Str) s)));
        set(Var.__greater, new Func((f, s) -> greaterThan((Str) f, (Int) s)));
        set(Var.__lesseq__, new Func((f, s) -> lessThanOrEqualTo((Str) f, (Int) s)));
        set(Var.__greatereq__, new Func((f, s) -> greaterThanOrEqualTo((Str) f, (Int) s)));
        set(Var.__str__, new Func(f -> string((Str) f)));
        set(Var.__hash__, new Func(f -> hash((Str) f)));
        set(Var.__eq__, new Func((f, s) -> equal((Str) f, (Str) s)));
        set(Var.__bool__, new Func(f -> bool((Str) f)));
        set(Var.__size__, new Func(f -> size((Str) f)));
    }};

    private static final Type type = new Type("STR");
}
