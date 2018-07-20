package Objects;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import Helpers.*;
import Main.*;

public class Func extends Any implements Construct {

    protected Func() {}

    public Func(Parser.Func funcDef, Scope parentScope) {
        super();

        for (var assignment : funcDef.paramDefs.parameters) {
            if (assignment.var instanceof Parser.Declare) {
                _params.add(new Var(assignment.var.token.value(), null)); // TODO: parse type
                _defaults.add(Interpreter.doExpression(assignment.value, parentScope));
            } else {
                _params.add(new Var(assignment.var.token.value(), Any.type));
                _defaults.add(null);
            }
        }

        var operations = funcDef.operations;
        if (operations instanceof Parser.Block)
            _block = (Parser.Block) operations;
        else
            _block = new Parser.Block(
                        new ArrayList<>() {{
                            add(new Parser.Return((Parser.Expression) operations));
                        }}
                     );

        _scope = new Scope(null);
        _parentScope = parentScope;
    }

    public Func(Functional functional) {
        _functional = functional;
    }

    public Any call(Any... arguments) {
        if (functional() !=  null) {
            return functional().run(arguments);
        }

        var blockScope = new Scope(parentScope());

        for (var i = 0; i < params().size(); i++) {
            if (arguments.length > i)
                blockScope.set(params().get(i), arguments[i]);
            else if (defaults().get(i) != null)
                blockScope.set(params().get(i), defaults().get(i));
            else
                return null; // TODO: throw Exception for missing arguments
        }

        return Interpreter.doBlock(block(), blockScope);
    }


    private Parser.Block _block;
    private Scope _scope, _parentScope;
    private List<Var> _params;
    private List<Any> _defaults;
    private Functional _functional;

    public Parser.Block block() {
        return _block;
    }

    public Scope scope() {
        return _scope;
    }

    public void setScope(Scope scope) { _scope = scope; }

    public Scope parentScope() {
        return _parentScope;
    }

    public List<Var> params() {
        return _params;
    }

    public List<Any> defaults() {
        return _defaults;
    }

    public Functional functional() { return _functional; }

    public static final Type type = new Type("FUNC");


    @FunctionalInterface
    interface Functional {
        Any run(Any... args);
    }
}
