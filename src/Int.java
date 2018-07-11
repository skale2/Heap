i
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BinaryOperator;

class Int extends Number {

    Int(Value value) {
        _value = value;
    }

    private static Int doBinaryOp(BinaryOperator method, Int first, Int second) {

    }

    static Value[] convertToLargest(Int first, Int second) {
        if (first.value().size > second.value().size) {

        }
    }

    static Int add(Int first, Int second) {

    }

    static Int subtract(Int first, Int second) {

    }

    static Int multiply(Int first, Int second) {

    }

    static Number divide(Int dividend, Int divisor) {

    }

    static Int mod(Int dividend, Int divisor) {

    }

    static Int floorDivide(Int dividend, Int divisor) {

    }

    static Int power(Int base, Int exponent) {

    }

    static Int roundTo(Int value, Int to) {

    }

    static Int round(Int value) {

    }

    static Int bitAnd(Int first, Int second) {

    }

    static Int bitOr(Int first, Int second) {

    }

    static Int bitNot(Int first, Int second) {

    }

    static Int bitXor(Int first, Int second) {

    }

    static Int increment(Int value) {

    }

    static Int decrement(Int value) {

    }

    static Int negate(Int value) {

    }



    private static Value combineBytes(BinaryOperator method, byte first, byte second, boolean overflow) {
        int result = (int) method.apply(first, second);
        if (result > Byte.MAX_VALUE || result < Byte.MIN_VALUE) {
            return combineShorts((short) first, (short) second, true, false);
        }
        return new ByteVal((byte) result);
    }


    private static Value combineShorts(short first, short second, boolean overflow, boolean reducible) {
        int result = first + second;
        if (result > Short.MAX_VALUE || result < Short.MIN_VALUE) {
            return combineInts((int) first, (int) second, true, false);
        } 
        return reduceShort((short) result);
    }

    private static Value combineInts(int first, int second, boolean overflow, boolean reducible) {
        try {
            int val = Math.addExact(first, second);
            return reducible ? reduceInt(val) : new IntVal(val);
        }  catch (ArithmeticException ae) {
            return combineLongs((long) first, (long) second, true, false);
        }
    }

    private static Value combineLongs(long first, long second, boolean overflow, boolean reducible) {
        try {
            long val = Math.addExact(first, second);
            return reducible ? reduceLong(val) : new LongVal(val);
        }  catch (ArithmeticException ae) {
            return combineBigs(BigInteger.valueOf(first), BigInteger.valueOf(second), false);
        }
    }

    private static Value combineBigs(BigInteger first, BigInteger second, boolean reducible) {
        BigInteger val = first.add(second);
        return reducible ? reduceBig(val) : new BigVal(val);
    }

    

    private static Value reduceShort(short value) {
        if (value <= Byte.MAX_VALUE && value >= Byte.MIN_VALUE)
            return new ByteVal((byte) value);
        return new ShortVal(value);
    }

        private static Value reduceInt(int value) {
        if (value <= Short.MAX_VALUE && value >= Short.MIN_VALUE)
            return reduceShort((short) value);
        return new IntVal((value);
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



    boolean isByte() {
        return value() instanceof ByteVal;
    }

    boolean isShort() {
        return value() instanceof ShortVal;
    }

    boolean isInt() {
        return value() instanceof IntVal;
    }

    boolean isLong() {
        return value() instanceof LongVal;
    }

    boolean isBig() {
        return value() instanceof BigVal;
    }

    byte byteValue() { return ((ByteVal) _value).value; }

    short shortValue() { return ((ShortVal) _value).value; }

    int intValue() {
        return ((IntVal) _value).value;
    }

    long longValue() {
        return ((LongVal) _value).value;
    }

    BigInteger bigValue() {
        return ((BigVal) _value).value;
    }


    Value value() {
        return _value;
    }

    private Value _value;


    private static abstract class Value {
        static int size = -1;
        int value;
    }

    private static class ByteVal extends Value {
        byte value;
        static int size = 0;

        ByteVal(byte value) {
            this.value = value;
        }
    }

    private static class ShortVal extends Value {
        short value;
        static int size = 1;

        ShortVal(short value) {
            this.value = value;
        }
    }

    private static class IntVal extends Value {
        int value;
        static int size = 2;

        IntVal(int value) {
            this.value = value;
        }
    }

    private static class LongVal extends Value {
        long value;
        static int size = 3;

        LongVal(long value) {
            this.value = value;
        }
    }

    private static class BigVal extends Value {
        BigInteger value;
        static int size = 4;

        BigVal(BigInteger value) {
            this.value = value;
        }
    }

    Map<String, Map<String, BinaryOperator>> _operations = new HashMap<>() {{
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
            put("u-", (a, b) -> -(byte) a) ;
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
            put("u-", (a, b) -> -(short) a) ;
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
            put("u-", (a, b) -> -(int) a) ;
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
            put("u-", (a, b) -> -(long) a) ;
        }});
        put("big", new HashMap<>() {{ 
            put("+", (a, b) -> ((BigInteger) a).add((BigInteger) b));
            put("-", (a, b) -> ((BigInteger) a).subtract((BigInteger) b));
            put("*", (a, b) -> ((BigInteger) a).multiply((BigInteger) b));
            put("/", (a, b) -> ((BigInteger) a).divide((BigInteger) b);
            put("%", (a, b) -> ((BigInteger) a).multiply((BigInteger) b));
            put("-/", (a, b) -> ((BigInteger) a).divide((BigInteger) b));
            put("**", (a, b) -> ((BigInteger) a).pow((int) b));
            put("`", (a, b) -> a);
            put("`2", (a, b) -> ((BigInteger) a).multiply((BigInteger) b).divide((BigInteger) b);
            put("&", (a, b) -> ((BigInteger) a).and((BigInteger) b));
            put("|", (a, b) -> ((BigInteger) a).or((BigInteger) b));
            put("~", (a, b) -> ((BigInteger) a).not());
            put("^", (a, b) -> ((BigInteger) a).xor((BigInteger) b));
            put("++", (a, b) -> ((BigInteger) a).add(BigInteger.ONE));
            put("--", (a, b) -> ((BigInteger) a).subtract(BigInteger.ONE));
            put("u-", (a, b) -> ((BigInteger) a).negate());
        }});
    }};

}
