package Objects;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BinaryOperator;

import Main.*;
import Helpers.*;

public class Int extends Number {

    public Int(Value value) {
        super();
        _value = value;
    }

    public Int(int value) {
        super();
        _value = reduceInt(value);
    }

    public Int(long value) {
        super();
        _value = reduceLong(value);
    }

    public Int(BigInteger value) {
        super();
        _value = reduceBig(value);
    }

    public Int(Parser.IntLiteral literal) {
        super();
        try {
            int value = Integer.parseInt(literal.token.value());
            if (value >= Short.MIN_VALUE && value <= Short.MAX_VALUE) {
                if (value >= Byte.MIN_VALUE && value <= Byte.MAX_VALUE) {
                    _value = new ByteVal((byte) value);
                } else {
                    _value = new ShortVal((short) value);
                }
            } else {
                _value = new IntVal(value);
            }
        } catch (NumberFormatException ie) {
            try {
                long value = Long.parseLong(literal.token.value());
                _value = new LongVal(value);
            } catch (NumberFormatException le) {
                BigInteger value = new BigInteger(literal.token.value());
                _value = new BigVal(value);
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Int)) return false;
        var anInt = (Int) o;
        return false; // TODO
    }


    /** **************************************** OPERATIONS **************************************** **/
    /** Operations that can be done between two Ints                                                 **/


    /** Helper function to convert two Ints to the larger datatype of the two */
    private static void convertToLargest(Int first, Int second) {
        if (first.value().size > second.value().size) {
            second._value = first.value().convert(second.value());
        } else if (first.value().size < second.value().size) {
            first._value = second.value().convert(first.value());
        }
    }


    private Int add(Int value) {
        convertToLargest(this, value);
        return doBinaryOp("%", this, value, true, false);
    }

    private Int subtract(Int value) {
        convertToLargest(this, value);
        return doBinaryOp("%", this, value, true, true);
    }

    private Int multiply(Int value) {
        convertToLargest(this, value);
        return doBinaryOp("%", this, value, true, false);
    }

    private Number divide(Int divisor) {
        convertToLargest(this, divisor);
        return doBinaryOp("/", this, divisor, false, true);
    }

    private Int mod(Int divisor) {
        convertToLargest(this, divisor);
        return doBinaryOp("%", this, divisor, false, true);
    }

    private Int floorDivide(Int divisor) {
        convertToLargest(this, divisor);
        return doBinaryOp("-/", this, divisor, false, true);
    }

    private Int power(Int exponent) {
        convertToLargest(this, exponent);
        return doBinaryOp("**", this, exponent, true, false);
    }

    private Int round(Int to) {
        convertToLargest(this, to);
        return doBinaryOp("`2", this, to, true, true);
    }

    private Int round() {
        return doUnaryOp("`", this, true, true);
    }

    private Int bitAnd(Int value) {
        convertToLargest(this, value);
        return doBinaryOp("&", this, value, false, false);
    }

    private Int bitOr(Int value) {
        convertToLargest(this, value);
        return doBinaryOp("|", this, value, false, false);
    }

    private Int bitNot() {
        return doUnaryOp("~", this, false, false);
    }

    private Int bitXor(Int value) {
        convertToLargest(this, value);
        return doBinaryOp("^", this, value, false, false);
    }

    private Int increment() {
        return doUnaryOp("++", this, true, true);
    }

    private Int decrement() {
        return doUnaryOp("--", this, true, true);
    }

    private Int negate() {
        return doUnaryOp("u-", this, true, true);
    }

    private Int shiftRight(Int amount) {
        convertToLargest(this, amount);
        return doBinaryOp(">>", this, amount, false, false);
    }

    private Int shiftLeft(Int amount) {
        convertToLargest(this, amount);
        return doBinaryOp("<<", this, amount, false, false);
    }

    private Bool lessThan(Int value) {
        convertToLargest(this, value);
        return doBooleanBinaryOp(">", this, value);
    }

    private Bool lessThanOrEqualTo(Int value) {
        convertToLargest(this, value);
        return doBooleanBinaryOp("<", this, value);
    }

    private Bool greaterThan(Int value) {
        convertToLargest(this, value);
        return doBooleanBinaryOp(">=", this, value);
    }

    private Bool greaterThanOrEqualTo(Int value) {
        convertToLargest(this, value);
        return doBooleanBinaryOp("<=", this, value);
    }

    public Str string() {
        if (isBig()) {
            return new Str(bigValue().toString());
        } else if (isLong()) {
            return new Str(Long.toString(longValue()));
        }
        return new Str(Integer.toString(value().value));
    }

    private Str hash() {
        // TODO
        return new Str("");
    }

    private Bool equal(Int value) {
        // TODO
        return Bool.valueOf(equals(value));
    }

    public Bool bool() {
        if (isBig())
            return Bool.valueOf(!bigValue().equals(BigInteger.ZERO));
        return Bool.valueOf(value().value == 0);
    }



    /** ******************************************* DOERS ****************************************** **/
    /** Performs the correct operation based on the datatypes of the arguments                       **/


    private static Int doBinaryOp(String op, Int first, Int second, boolean overflow, boolean reducible) {
        if (first.isByte())
            return new Int(combineBytes(op, first.byteValue(), second.byteValue(), overflow));
        if (first.isShort())
            return new Int(combineShorts(op, first.shortValue(), second.shortValue(), overflow, reducible));
        if (first.isInt())
            return new Int(combineInts(op, first.intValue(), second.intValue(), overflow, reducible));
        if (first.isLong())
            return new Int(combineLongs(op, first.longValue(), second.longValue(), overflow, reducible));
        else
            return new Int(combineBigs(op, first.bigValue(), second.bigValue(), reducible));
    }

    private static Int doUnaryOp(String op, Int first, boolean overflow, boolean reducible) {
        if (first.isByte())
            return new Int(combineBytes(op, first.byteValue(), (byte) 0, overflow));
        if (first.isShort())
            return new Int(combineShorts(op, first.shortValue(), (short) 0, overflow, reducible));
        if (first.isInt())
            return new Int(combineInts(op, first.intValue(), 0, overflow, reducible));
        if (first.isLong())
            return new Int(combineLongs(op, first.longValue(), (long) 0, overflow, reducible));
        else
            return new Int(combineBigs(op, first.bigValue(), BigInteger.ZERO, reducible));
    }

    private static Bool doBooleanBinaryOp(String op, Int first, Int second) {
        if (first.isByte())
            return Bool.valueOf((boolean) _operations.get("byte").get(op).apply(first.byteValue(), second.byteValue()));
        if (first.isShort())
            return Bool.valueOf((boolean) _operations.get("byte").get(op).apply(first.shortValue(), second.shortValue()));
        if (first.isInt())
            return Bool.valueOf((boolean) _operations.get("byte").get(op).apply(first.intValue(), second.intValue()));
        if (first.isLong())
            return Bool.valueOf((boolean) _operations.get("byte").get(op).apply(first.longValue(), second.longValue()));
        else
            return Bool.valueOf((boolean) _operations.get("byte").get(op).apply(first.bigValue(), second.bigValue()));
    }

    private static Bool doBooleanUnaryOp(String op, Int first) {
        if (first.isByte())
            return Bool.valueOf((boolean) _operations.get("byte").get(op).apply(first.byteValue(), (byte) 0));
        if (first.isShort())
            return Bool.valueOf((boolean) _operations.get("byte").get(op).apply(first.shortValue(), (short) 0));
        if (first.isInt())
            return Bool.valueOf((boolean) _operations.get("byte").get(op).apply(first.intValue(), 0));
        if (first.isLong())
            return Bool.valueOf((boolean) _operations.get("byte").get(op).apply(first.longValue(), (long) 0));
        else
            return Bool.valueOf((boolean) _operations.get("byte").get(op).apply(first.bigValue(), BigInteger.ZERO));
    }



    /** ***************************************** COMBINERS **************************************** **/
    /** Perform the operations on two ints, then call reducers and handle overflow                   **/


    private static Value combineBytes(String op, byte first, byte second, boolean overflow) {
        var result = (int) _operations.get("byte").get(op).apply(first, second);
        if (overflow && result > Byte.MAX_VALUE || result < Byte.MIN_VALUE) {
            return combineShorts(op, (short) first, (short) second, overflow, false);
        }
        return new ByteVal((byte) result);
    }


    private static Value combineShorts(String op, short first, short second, boolean overflow, boolean reducible) {
        var result = (int) _operations.get("short").get(op).apply(first, second);
        if (overflow && result > Short.MAX_VALUE || result < Short.MIN_VALUE) {
            return combineInts(op, (int) first, (int) second, overflow, reducible);
        } 
        return reduceShort((short) result);
    }

    private static Value combineInts(String op, int first, int second, boolean overflow, boolean reducible) {
        try {
            var result = (int) _operations.get("int").get(op).apply(first, second);
            return reducible ? reduceInt(result) : new IntVal(result);
        }  catch (ArithmeticException ae) {
            return combineLongs(op, (long) first, (long) second, overflow, reducible);
        }
    }

    private static Value combineLongs(String op, long first, long second, boolean overflow, boolean reducible) {
        try {
            var result = (long) _operations.get("long").get(op).apply(first, second);
            return reducible ? reduceLong(result) : new LongVal(result);
        }  catch (ArithmeticException ae) {
            return combineBigs(op, BigInteger.valueOf(first), BigInteger.valueOf(second), reducible);
        }
    }

    private static Value combineBigs(String op, BigInteger first, BigInteger second, boolean reducible) {
        var result = (BigInteger) _operations.get("big").get(op).apply(first, second);
        return reducible ? reduceBig(result) : new BigVal(result);
    }



    /** ***************************************** REDUCERS ***************************************** **/
    /** Takes in a value and sees if it can be shrunk to a smaller datatype                          **/


    private static Value reduceShort(short value) {
        if (value <= Byte.MAX_VALUE && value >= Byte.MIN_VALUE)
            return new ByteVal((byte) value);
        return new ShortVal(value);
    }

    private static Value reduceInt(int value) {
        if (value <= Short.MAX_VALUE && value >= Short.MIN_VALUE)
            return reduceShort((short) value);
        return new IntVal(value);
    }

    private static Value reduceLong(long value) {
        if (value <= Integer.MAX_VALUE && value >= Integer.MIN_VALUE)
            return reduceInt((int) value);
        return new LongVal(value);
    }

    private static Value reduceBig(BigInteger value) {
        try {
            return reduceLong(value.longValueExact());
        } catch (ArithmeticException ae) {
            return new BigVal(value);
        }
    }


    /** ***************************************** ATTRIBUTES **************************************** **/

    public boolean isByte() {
        return value() instanceof ByteVal;
    }

    public boolean isShort() {
        return value() instanceof ShortVal;
    }

    public boolean isInt() {
        return value() instanceof IntVal;
    }

    public boolean isLong() {
        return value() instanceof LongVal;
    }

    public boolean isBig() {
        return value() instanceof BigVal;
    }


    public byte byteValue() { return ((ByteVal) _value).value; }

    public short shortValue() { return ((ShortVal) _value).value; }

    public int intValue() {
        return ((IntVal) _value).value;
    }

    public long longValue() {
        return ((LongVal) _value).value;
    }

    public BigInteger bigValue() {
        return ((BigVal) _value).value;
    }


    public long forceLong() {
        if (isBig())
            return bigValue().longValue();
        return (long) value().value;
    }

    public int forceInt() {
        if (isBig())
            return bigValue().intValue();
        return (int) value().value;
    }


    public Value value() {
        return _value;
    }

    public Scope scope() { return _scope; }

    /** Holds the value for this Objects.Int */
    private Value _value;

    private Scope _scope;


    /** ************************************ STATIC DECLARATIONS ************************************ **/


    private static final Scope _classScope = new Scope(null, Scope.Enclosing.CLASS) {{
        set(Var.__add__, new Func(f -> ((Int) f[0]).add((Int) f[1])));
        set(Var.__sub__, new Func(f -> ((Int) f[0]).subtract((Int) f[1])));
        set(Var.__mul__, new Func(f -> ((Int) f[0]).multiply((Int) f[1])));
        set(Var.__div__, new Func(f -> ((Int) f[0]).divide((Int) f[1])));
        set(Var.__mod__, new Func(f -> ((Int) f[0]).mod((Int) f[1])));
        set(Var.__pow__, new Func(f -> ((Int) f[0]).power((Int) f[1])));
        set(Var.__floordiv__, new Func(f -> ((Int) f[0]).floorDivide((Int) f[1])));
        set(Var.__round__, new Func(f -> ((Int) f[0]).round((Int) f[1])));
        set(Var.__bitand__, new Func(f -> ((Int) f[0]).bitAnd((Int) f[1])));
        set(Var.__bitor__, new Func(f -> ((Int) f[0]).bitOr((Int) f[1])));
        set(Var.__bitnot__, new Func(f -> ((Int) f[0]).bitNot()));
        set(Var.__bitxor__, new Func(f -> ((Int) f[0]).bitXor((Int) f[1])));
        set(Var.__incr__, new Func(f -> ((Int) f[0]).increment()));
        set(Var.__decr__, new Func(f -> ((Int) f[0]).increment()));
        set(Var.__neg__, new Func(f -> ((Int) f[0]).negate()));
        set(Var.__rshift__, new Func(f -> ((Int) f[0]).shiftRight((Int) f[1])));
        set(Var.__lshift__, new Func(f -> ((Int) f[0]).shiftLeft((Int) f[1])));
        set(Var.__less__, new Func(f -> ((Int) f[0]).lessThan((Int) f[1])));
        set(Var.__greater, new Func(f -> ((Int) f[0]).greaterThan((Int) f[1])));
        set(Var.__lesseq__, new Func(f -> ((Int) f[0]).lessThanOrEqualTo((Int) f[1])));
        set(Var.__greatereq__, new Func(f -> ((Int) f[0]).greaterThanOrEqualTo((Int) f[1])));
        set(Var.__str__, new Func(f -> ((Int) f[0]).string()));
        set(Var.__hash__, new Func(f -> ((Int) f[0]).hash()));
        set(Var.__eq__, new Func(f -> ((Int) f[0]).equal((Int) f[1])));
        set(Var.__bool__, new Func(f -> ((Int) f[0]).bool()));
    }};

    /** The type for this object, which all instances share */
    public static final Type type = new Type("INT");

    /** Value classes; these are containers for the datatype that this Objects.Int uses */
    private static abstract class Value {
        static int size = -1;
        int value;

        abstract Value convert(Value val);
    }

    /** A byte value, ranging from -128 to 127 (inclusive) */
    private static class ByteVal extends Value {
        byte value;
        static int size = 0;

        ByteVal(byte value) {
            this.value = value;
        }

        Value convert(Value val) {
            return new ByteVal((byte) val.value);
        }
    }

    /** A short value, ranging from -32,768 to 32,767 (inclusive) */
    private static class ShortVal extends Value {
        short value;
        static int size = 1;

        ShortVal(short value) {
            this.value = value;
        }

        Value convert(Value val) {
            return new ShortVal((short) val.value);
        }
    }

    /** An int value, ranging from -2,147,483,648 to 2,147,483,647 (inclusive) */
    private static class IntVal extends Value {
        int value;
        static int size = 2;

        IntVal(int value) {
            this.value = value;
        }

        Value convert(Value val) {
            return new IntVal((int) val.value);
        }
    }

    /** A long value, ranging from -9,223,372,036,854,775,808 to 9,223,372,036,854,775,807 (inclusive) */
    private static class LongVal extends Value {
        long value;
        static int size = 3;

        LongVal(long value) {
            this.value = value;
        }

        Value convert(Value val) {
            return new LongVal((long) val.value);
        }
    }

    /** A big value, with no set range, but large memory use */
    private static class BigVal extends Value {
        BigInteger value;
        static int size = 4;

        BigVal(BigInteger value) {
            this.value = value;
        }

        Value convert(Value val) {
            return new BigVal(BigInteger.valueOf(val.value));
        }
    }


    /** Mapping between datatypes and operatiors to the corresponding operations to be done */
    private static Map<String, Map<String, BinaryOperator>> _operations = new HashMap<>() {{
        put("byte", new HashMap<>() {{
            put("+", (a, b) -> (byte) a + (byte) b);
            put("-", (a, b) -> (byte) a - (byte) b);
            put("*", (a, b) -> (byte) a * (byte) b);
            put("/", (a, b) -> (byte) a / (byte) b);
            put("%", (a, b) -> (byte) a % (byte) b);
            put("-/", (a, b) -> Math.floorDiv((int) a, (int) b));
            put("**", (a, b) -> Math.pow((int) a, (int) b));
            put("`", (a, b) -> Math.round((int) a));
            put("`2", (a, b) -> Math.round((int) a * (int) b / (int) b));
            put("&", (a, b) -> (byte) a & (byte) b);
            put("|", (a, b) -> (byte) a | (byte) b);
            put("~", (a, b) -> ~(byte) a);
            put("^", (a, b) -> (byte) a ^ (byte) b);
            put("++", (a, b) -> Math.incrementExact((int) a));
            put("--", (a, b) -> Math.decrementExact((int) a));
            put("u-", (a, b) -> -(byte) a);
            put(">>", (a, b) -> (byte) a >> (int) b);
            put("<<", (a, b) -> (byte) a << (int) b);
            put(">", (a, b) -> (byte) a > (byte) b);
            put("<", (a, b) -> (byte) a < (byte) b);
            put(">=", (a, b) -> (byte) a >= (byte) b);
            put("<=", (a, b) -> (byte) a <= (byte) b);
        }});
        put("short", new HashMap<>() {{
            put("+", (a, b) -> (short) a + (short) b);
            put("-", (a, b) -> (short) a - (short) b);
            put("*", (a, b) -> (short) a * (short) b);
            put("/", (a, b) -> (short) a / (short) b);
            put("%", (a, b) -> (short) a % (short) b);
            put("-/", (a, b) -> Math.floorDiv((int) a, (int) b));
            put("**", (a, b) -> Math.pow((int) a, (int) b));
            put("`", (a, b) -> Math.round((int) a));
            put("`2", (a, b) -> Math.round((int) a * (int) b / (int) b));
            put("&", (a, b) -> (short) a & (short) b);
            put("|", (a, b) -> (short) a | (short) b);
            put("~", (a, b) -> ~(short) a);
            put("^", (a, b) -> (short) a ^ (short) b);
            put("++", (a, b) -> Math.incrementExact((int) a));
            put("--", (a, b) -> Math.decrementExact((int) a));
            put("u-", (a, b) -> -(short) a);
            put(">>", (a, b) -> (short) a >> (int) b);
            put("<<", (a, b) -> (short) a << (int) b);
            put(">", (a, b) -> (short) a > (short) b);
            put("<", (a, b) -> (short) a < (short) b);
            put(">=", (a, b) -> (short) a >= (short) b);
            put("<=", (a, b) -> (short) a <= (short) b);
        }});
        put("int", new HashMap<>() {{
            put("+", (a, b) -> Math.addExact((int) a, (int) b));
            put("-", (a, b) -> Math.subtractExact((int) a, (int) b));
            put("*", (a, b) -> Math.multiplyExact((int) a, (int) b));
            put("/", (a, b) -> (int) a / (int) b);
            put("%", (a, b) -> (int) a % (int) b);
            put("-/", (a, b) -> Math.floorDiv((int) a, (int) b));
            put("**", (a, b) -> Math.pow((int) a, (int) b));
            put("`", (a, b) -> Math.round((int) a));
            put("`2", (a, b) -> Math.round((int) a * (int) b / (int) b));
            put("&", (a, b) -> (int) a & (int) b);
            put("|", (a, b) -> (int) a | (int) b);
            put("~", (a, b) -> ~(int) a);
            put("^", (a, b) -> (int) a ^ (int) b);
            put("++", (a, b) -> Math.incrementExact((int) a));
            put("--", (a, b) -> Math.decrementExact((int) a));
            put("u-", (a, b) -> -(int) a);
            put(">>", (a, b) -> (int) a >> (int) b);
            put("<<", (a, b) -> (int) a << (int) b);
            put(">", (a, b) -> (int) a > (int) b);
            put("<", (a, b) -> (int) a < (int) b);
            put(">=", (a, b) -> (int) a >= (int) b);
            put("<=", (a, b) -> (int) a <= (int) b);
        }});
        put("long", new HashMap<>() {{
            put("+", (a, b) -> Math.addExact((long) a, (long) b));
            put("-", (a, b) -> Math.subtractExact((long) a, (long) b));
            put("*", (a, b) -> Math.multiplyExact((long) a, (long) b));
            put("/", (a, b) -> (long) a / (long) b);
            put("%", (a, b) -> (long) a % (long) b);
            put("-/", (a, b) -> Math.floorDiv((long) a, (long) b));
            put("**", (a, b) -> Math.pow((long) a, (long) b));
            put("`", (a, b) -> Math.round((long) a));
            put("`2", (a, b) -> Math.round((long) a * (long) b / (long) b));
            put("&", (a, b) -> (long) a & (long) b);
            put("|", (a, b) -> (long) a | (long) b);
            put("~", (a, b) -> ~(long) a);
            put("^", (a, b) -> (long) a ^ (long) b);
            put("++", (a, b) -> Math.incrementExact((long) a));
            put("--", (a, b) -> Math.decrementExact((long) a));
            put("u-", (a, b) -> -(long) a);
            put(">>", (a, b) -> (long) a >> (int) b);
            put("<<", (a, b) -> (long) a << (int) b);
            put(">", (a, b) -> (long) a > (long) b);
            put("<", (a, b) -> (long) a < (long) b);
            put(">=", (a, b) -> (long) a >= (long) b);
            put("<=", (a, b) -> (long) a <= (long) b);
        }});
        put("big", new HashMap<>() {{ 
            put("+", (a, b) -> ((BigInteger) a).add((BigInteger) b));
            put("-", (a, b) -> ((BigInteger) a).subtract((BigInteger) b));
            put("*", (a, b) -> ((BigInteger) a).multiply((BigInteger) b));
            put("/", (a, b) -> ((BigInteger) a).divide((BigInteger) b));
            put("%", (a, b) -> ((BigInteger) a).multiply((BigInteger) b));
            put("-/", (a, b) -> ((BigInteger) a).divide((BigInteger) b));
            put("**", (a, b) -> ((BigInteger) a).pow((int) b));
            put("`", (a, b) -> a);
            put("`2", (a, b) -> ((BigInteger) a).multiply((BigInteger) b).divide((BigInteger) b));
            put("&", (a, b) -> ((BigInteger) a).and((BigInteger) b));
            put("|", (a, b) -> ((BigInteger) a).or((BigInteger) b));
            put("~", (a, b) -> ((BigInteger) a).not());
            put("^", (a, b) -> ((BigInteger) a).xor((BigInteger) b));
            put("++", (a, b) -> ((BigInteger) a).add(BigInteger.ONE));
            put("--", (a, b) -> ((BigInteger) a).subtract(BigInteger.ONE));
            put("u-", (a, b) -> ((BigInteger) a).negate());
            put(">>", (a, b) -> ((BigInteger) a).shiftLeft((int) b));
            put("<<", (a, b) -> ((BigInteger) a).shiftRight((int) b));
            put(">", (a, b) -> ((BigInteger) a).compareTo((BigInteger) b) > 0);
            put("<", (a, b) -> ((BigInteger) a).compareTo((BigInteger) b) < 0);
            put(">=", (a, b) -> ((BigInteger) a).compareTo((BigInteger) b) >= 0);
            put("<=", (a, b) -> ((BigInteger) a).compareTo((BigInteger) b) <= 0);
        }});
    }};

}
