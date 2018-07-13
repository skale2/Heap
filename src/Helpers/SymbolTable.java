package Helpers;

import Main.Parser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SymbolTable {
    Map<String, List<Type>> _symbols;

    public SymbolTable(Map<String, List<Type>> symbols) {
        this._symbols = symbols;
    }

    void add(String name, List<Parser.Type> types) {
        for (Parser.Type parsedType : types) {
            Type.Choice choice = Type.choices.get(parsedType.token.type());
            if (choice == null) {

            }
        }
    }

    boolean isType(String symbol, Type type) {
        if (_symbols.containsKey(symbol)) {
            return _symbols.get(symbol).stream().anyMatch(aType -> aType.equals(type));
        } else {
            return false;
        }
    }


    static class Type {
        Choice choice;
        List<Type> types;
        String name;

        Type(Choice choice) {
            this.choice = choice;
            assert choice.kind.equals(ATM);
        }

        Type(Choice choice, List<Type> types) {
            assert choice.kind.equals(CNT);
            this.choice = choice;
            this.types = types;
        }

        Type(Choice choice, String name) {
            assert choice == Choice.DEFINED;
            this.choice = choice;
            this.name = name;
        }

        Type(Choice choice, String name, List<Type> types) {
            assert choice == Choice.DEFINED_CONTAINER;
            this.choice = choice;
            this.name = name;
            this.types = types;
        }

        static String CNT = "CONTAINER", ATM = "ATOMIC";

        static Map<Token.TokenType, Choice> choices = new HashMap<>() {{
            put(Token.TokenType.ANY, Choice.ANY);
            put(Token.TokenType.NULL, Choice.NULL);
            put(Token.TokenType.INT, Choice.INT);
            put(Token.TokenType.REAL, Choice.REAL);
            put(Token.TokenType.STR, Choice.STR);
            put(Token.TokenType.CHAR, Choice.CHAR);
            put(Token.TokenType.BOOL, Choice.BOOL);
            put(Token.TokenType.ARR_TYPE, Choice.ARR);
            put(Token.TokenType.L_ARR_TYPE, Choice.L_ARR);
            put(Token.TokenType.DL_ARR_TYPE, Choice.DL_ARR);
            put(Token.TokenType.SET_TYPE, Choice.SET);
            put(Token.TokenType.MAP_TYPE, Choice.MAP);
            put(Token.TokenType.DIR_TYPE, Choice.DIR_GRAPH);
            put(Token.TokenType.UNDIR_TYPE, Choice.UNDIR_GRAPH);
        }};

        enum Choice {
            ANY(ATM), NULL(ATM),
            INT(ATM), REAL(ATM), STR(ATM), CHAR(ATM), BOOL(ATM),
            ARR(CNT), L_ARR(CNT), DL_ARR(CNT), SET(CNT), MAP(CNT),
            DIR_GRAPH(CNT), UNDIR_GRAPH(CNT),
            DEFINED(ATM), DEFINED_CONTAINER(CNT);

            Choice(String kind) {
                this.kind = kind;
            }

            String kind;
        }
    }
}
