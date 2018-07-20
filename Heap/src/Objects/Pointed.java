package Objects;

public class Pointed extends Any {
    
    private Pointed getPointed(int value) {
        return new Pointed(new Pointer(pointer().container(), value));
    }
    
    private Pointed add(Int amount) {
        return getPointed(pointer().index() + amount.forceInt());
    }

    private Pointed subtract(Int amount) {
        return getPointed(pointer().index() - amount.forceInt());
    }

    private Pointed multiply(Int multiplier) {
        return getPointed(pointer().index() * multiplier.forceInt());
    }

    private Pointed divide(Int divisor) {
        assert pointer().index() % divisor.forceInt() == 0;
        return getPointed(pointer().index() / divisor.forceInt());
    }

    private Pointed mod(Int divisor) {
        return getPointed(pointer().index() % divisor.forceInt());
    }

    private Pointed floorDivide(Int divisor) {
        return getPointed(Math.floorDiv(pointer().index(), divisor.forceInt()));
    }

    private Pointed power(Int exponent) {
        return getPointed((int) Math.pow(pointer().index(), exponent.forceInt()));
    }

    private Pointed round(Int to) {
        return getPointed(Math.round((pointer().index() * to.forceInt()) / to.forceInt()));
    }

    private Pointed round() {
        return getPointed(Math.round(pointer().index()));
    }

    private Pointed increment() {
        return getPointed(pointer().index() + 1);
    }

    private Pointed decrement() {
        return getPointed(pointer().index() - 1);
    }

    private Bool lessThan(Pointed value) {
        return new Bool(pointer().index() < value.pointer().index());
    }

    private Bool lessThanOrEqualTo(Pointed value) {
        return new Bool(pointer().index() < value.pointer().index());
    }

    private Bool greaterThan(Pointed value) {
        return new Bool(pointer().index() < value.pointer().index());
    }

    private Bool greaterThanOrEqualTo(Pointed value) {
        return new Bool(pointer().index() < value.pointer().index());
    }

    private Str string() {
        return new Str(String.format("<%s Pointer |> %s>", containerName(), pointer().value().toString()));
    }

    private Str hash() {
        // TODO
        return new Str("");
    }

    private Bool equal(Pointed value) {
        return Bool.valueOf(
                pointer().value().equals(value.pointer().value()) &&
                        pointer().container().equals(value.pointer().container())
        );
    }

    private Bool bool() {
        return Bool.TRUE;
    }

    private String containerName() {
        return "";
    }

    public Pointed(Pointer pointer) {
        _pointer = pointer;
    }

    public Pointer pointer() {
        return _pointer;
    }

    private Pointer _pointer;
}
