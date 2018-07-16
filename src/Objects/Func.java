package Objects;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import Helpers.*;
import Main.*;

public class Func extends Any implements Construct {

    public Func(Parser.Func funcDef, Scope parentScope) {
        super();

        for (Parser.Assignment assignment : funcDef.paramDefs.parameters) {
            if (assignment.var instanceof Parser.Declare) {
                _params.add(new Var(assignment.var.token.value(), null)); // TODO: parse type
                _defaults.add(Interpreter.doExpression(assignment.value, parentScope));
            } else {
                _params.add(new Var(assignment.var.token.value(), Any.type));
                _defaults.add(null);
            }
        }

        Parser.ASTNode operations = funcDef.operations;
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

    public Func(BiFunction<Any, Any, Any> function) {

    }

    public Func(Function<Any, Any> function) {

    }

    public static Any call(Func func, Any... arguments) {
        Scope blockScope = new Scope(func.parentScope());

        for (int i = 0; i < func.params().size(); i++) {
            if (arguments.length > i)
                blockScope.set(func.params().get(i), arguments[i]);
            else if (func.defaults().get(i) != null)
                blockScope.set(func.params().get(i), func.defaults().get(i));
            else
                return null; // TODO throw Exception for missing arguments
        }

        return Interpreter.doBlock(func.block(), blockScope);
    }


    private Parser.Block _block;
    private Scope _scope, _parentScope;
    private List<Var> _params;
    private List<Any> _defaults;

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

    public static final Type type = new Type("FUNC");
}
