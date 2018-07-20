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


    private Str add(Str str) {
        return new Str(value() + str.value());
    }

    private Str multiply(Int times) {
        var s = new StringBuilder();
        for (int i = 0; i < times.forceLong(); i++) {
            s.append(value());
        }
        return new Str(s.toString());
    }

    private Str divide(Int times) {
        var value = value();
        assert value.length() % times.forceInt() == 0;
        return new Str(value.substring(0, value.length()/times.forceInt()));
    }

    private Str mod(Int times) {
        var value = value();
        int len = value().length() % times.forceInt();
        return new Str(value.substring(value.length() - len));
    }

    private Str floorDivide(Int times) {
        var value = value();
        return new Str(value.substring(0, Math.floorDiv(value.length(), times.forceInt())));
    }

    private Str shiftRight(Int amount) {
        var strValue = value();
        if (amount.forceInt() > strValue.length()) {
            return new Str(new String(new char[strValue.length()]).replace("\0", " "));
        }

        return new Str(
                new String(new char[amount.forceInt()]).replace("\0", " ") +
                        strValue.substring(0, strValue.length() - amount.forceInt())
        );
    }

    private Str shiftLeft(Int amount) {
        var strValue = value();
        if (amount.forceInt() > strValue.length()) {
            return new Str(new String(new char[strValue.length()]).replace("\0", " "));
        }

        return new Str(
                strValue.substring(strValue.length() - amount.forceInt(), strValue.length()) +
                        new String(new char[amount.forceInt()]).replace("\0", " ")
        );
    }

    private Bool lessThan(Str str) {
        return Bool.FALSE; // TODO
    }

    private Bool lessThanOrEqualTo(Str str) {
        return Bool.FALSE; // TODO
    }

    private Bool greaterThan(Str str) {
        return Bool.FALSE; // TODO
    }

    private Bool greaterThanOrEqualTo(Str str) {
        return Bool.FALSE; // TODO
    }

    private Int size() { return new Int(value().length()); }

    private Str string() {
        return this;
    }

    private Str hash() {
        try {
            var digest = MessageDigest.getInstance("SHA-256");
            var hash = digest.digest(value().getBytes(StandardCharsets.UTF_8));
            return new Str(Base64.getEncoder().encodeToString(hash));

        } catch (java.security.NoSuchAlgorithmException snme) {
            return new Str("");
        }
    }

    private Bool equal(Str str) {
        return Bool.valueOf(equals(str));
    }

    private Bool bool() {
        return Bool.valueOf(!value().isEmpty());
    }


    class StrIter extends Iterator {
        public StrIter(Str string) {
            _index = -1;
            _string = string;
        }

        @Override
        public Str next() {
            _index++;
            return new Str_string.value().charAt(_index);
        }

        int _index;
        Str _string;
    }

    private static final Scope _classScope = new Scope(null) {{
        set(Var.__add__, new Func(f -> ((Str) f[0]).add((Str) f[1])));
        set(Var.__mul__, new Func(f -> ((Str) f[0]).multiply((Int) f[1])));
        set(Var.__div__, new Func(f -> ((Str) f[0]).divide((Int) f[1])));
        set(Var.__mod__, new Func(f -> ((Str) f[0]).mod((Int) f[1])));
        set(Var.__floordiv__, new Func(f -> ((Str) f[0]).floorDivide((Int) f[1])));
        set(Var.__rshift__, new Func(f -> ((Str) f[0]).shiftRight((Int) f[1])));
        set(Var.__lshift__, new Func(f -> ((Str) f[0]).shiftLeft((Int) f[1])));
        set(Var.__less__, new Func(f -> ((Str) f[0]).lessThan((Str) f[1])));
        set(Var.__greater, new Func(f -> ((Str) f[0]).greaterThan((Str) f[1])));
        set(Var.__lesseq__, new Func(f -> ((Str) f[0]).lessThanOrEqualTo((Str) f[1])));
        set(Var.__greatereq__, new Func(f -> ((Str) f[0]).greaterThanOrEqualTo((Str) f[1])));
        set(Var.__str__, new Func(f -> ((Str) f[0]).string()));
        set(Var.__hash__, new Func(f -> ((Str) f[0]).hash()));
        set(Var.__eq__, new Func(f -> ((Str) f[0]).equal((Str) f[1])));
        set(Var.__bool__, new Func(f -> ((Str) f[0]).bool()));
        set(Var.__size__, new Func(f -> ((Str) f[0]).size()));
    }};

    private static final Type type = new Type("STR");
}
