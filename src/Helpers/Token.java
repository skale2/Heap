package Helpers;

import java.util.*;

public class Token {

    public Token(String value, TokenType type) {
        _value = value;
        _type = type;
    }

    public Token(TokenType type) {
        _value = type._value;
        _type = type;
    }

    @Override
    public String toString() {
        if (_value.equals(_type._value)) {
            return _type.toString();
        } else {
            return String.format("%s(%s)", _type.toString(), _value);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Token)) return false;
        Token token = (Token) o;
        return _type == token._type &&
                Objects.equals(_value, token._value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_type, _value);
    }

    public String value() { return _value; }

    public TokenType type() { return _type; }

    /**
     * Helper functions to get all categories token is in
     * */
    public boolean isLiteral()      { return _type._groups.contains(LIT); }
    public boolean isReserved()     { return _type._groups.contains(RSD); }
    public boolean isOperator()     { return _type._groups.contains(OPR); }
    public boolean isModifier()     { return _type._groups.contains(MDF); }
    public boolean isType()         { return _type._groups.contains(TYP); }
    public boolean isConstruct()    { return _type._groups.contains(CNS); }
    public boolean isDirect()       { return _type._groups.contains(DIR); }
    public boolean isContainer()    { return _type._groups.contains(CNT); }
    public boolean isAssignment()   { return _type._groups.contains(ASN); }
    public boolean isVar()          { return _type == TokenType.VAR; }

    /**
     * Precedence of different operators by their token type (lower number means
     * higher precedence)
     * */
    public static Map<TokenType, Integer> operatorPrecedence = new HashMap<TokenType, Integer>() {{
        /* Rounding */
        put(TokenType.ROUND,            0);

        /* Exponentiative */
        put(TokenType.EXP,              1);

        /* Multiplicative */
        put(TokenType.MULTIPLY,         2);
        put(TokenType.DIVIDE,           2);
        put(TokenType.MOD,              2);
        put(TokenType.FLOOR,            2);

        /* Additive and concatenative */
        put(TokenType.ADD,              3);
        put(TokenType.SUBTRACT,         3);

        /* Shift */
        put(TokenType.SHIFT_LEFT,       4);
        put(TokenType.SHIFT_RIGHT,      4);

        /* Relational */
        put(TokenType.GREATER_THAN,     5);
        put(TokenType.LESS_THAN,        5);
        put(TokenType.GREATER_THAN_EQ,  5);
        put(TokenType.LESS_THAN_EQ,     5);

        /* Equality */
        put(TokenType.EQUAL,            6);
        put(TokenType.NOT_EQUAL,        6);
        put(TokenType.CAST_EQUAL,       6);
        put(TokenType.CAST_NOT_EQUAL,   6);

        /* Bitwise boolean operators */
        put(TokenType.B_AND,            7);
        put(TokenType.B_OR,             8);
        put(TokenType.B_XOR,            9);

        /* Logical boolean operators */
        put(TokenType.L_AND,            10);
        put(TokenType.L_OR,             11);
        put(TokenType.L_XOR,            12);

        /* Ternary operators */
        put(TokenType.TERNARY,          13);
        
        /* Assignment */
        put(TokenType.ADD_EQ,           14);
        put(TokenType.SUBTRACT_EQ,      14);
        put(TokenType.MULTIPLY_EQ,      14);
        put(TokenType.DIVIDE_EQ,        14);
        put(TokenType.FLOOR_EQ,         14);
        put(TokenType.ROUND_EQ,         14);
        put(TokenType.EXP_EQ,           14);
        put(TokenType.L_AND_EQ,         14);
        put(TokenType.L_OR_EQ,          14);
        put(TokenType.L_XOR_EQ,         14);
        put(TokenType.B_AND_EQ,         14);
        put(TokenType.B_OR_EQ,          14);
        put(TokenType.B_XOR_EQ,         14);
    }};

    private TokenType _type;
    private String _value;

    /** The usage type of a token for the AST */
    private static final String
            LIT = "LITERAL",
            RSD = "RESERVED",
            OPR = "OPERATOR",
            MDF = "MODIFIER",
            TYP = "TYPE",
            CNS = "CONSTRUCT",
            DIR = "DIRECT",
            CNT = "CONTAINER",
            ASN = "ASSIGNMENT";

    /**
     * The type of this token
     */
    public enum TokenType {

        EOL(";"), EOF(""), COMMA(","), COLON(":"), DIRECT("=>"),
        ANNOTATION("@"), TERNARY("?"), PERIOD("."),

        DEREF("&"), TOTAL_REF("#"),

        ASSIGN("="), CAST_ASSIGN(":="), EQUAL("==", OPR), CAST_EQUAL(":=="), NOT_EQUAL("!="), CAST_NOT_EQUAL(":!="),

        VAR("[a-zA-Z\\_]+[a-zA-Z0-9\\_]*"),

        REAL_VAL("\\d*\\.\\d+", LIT),
        INT_VAL("\\d+", LIT),
        STR_VAL("(\"|\')\\.*(\"|\')", LIT),

        TRUE("true", RSD, LIT), FALSE("false", RSD, LIT),

        ADD("+", OPR), SUBTRACT("-", OPR), MULTIPLY("*", OPR), DIVIDE("/", OPR),
        MOD("%", OPR), FLOOR("-/", OPR), EXP("**", OPR), ROUND("`", OPR),
        INCREMENT("++", OPR), DECREMENT("--", OPR),

        L_AND("&&", OPR), L_OR("||", OPR), L_NOT("!", OPR), L_XOR("^^", OPR),
        B_AND("&", OPR), B_OR("|", OPR), B_NOT("~", OPR), B_XOR("^", OPR),

        LESS_THAN("<", OPR), GREATER_THAN(">", OPR),
        LESS_THAN_EQ("<", OPR), GREATER_THAN_EQ(">=", OPR),
        SHIFT_RIGHT(">>", OPR), SHIFT_LEFT("<<", OPR),

        ADD_EQ("+=", OPR, ASN), SUBTRACT_EQ("-=", OPR, ASN), MULTIPLY_EQ("*=", OPR, ASN),
        DIVIDE_EQ("/=", OPR, ASN), MOD_EQ("%=", OPR, ASN), FLOOR_EQ("-/=", OPR, ASN),
        ROUND_EQ("`=", OPR, ASN), EXP_EQ("**=", OPR, ASN),

        L_AND_EQ("&&=", OPR, ASN), L_OR_EQ("||=", OPR, ASN), L_XOR_EQ("^^=", OPR, ASN),
        B_AND_EQ("&=", OPR, ASN), B_OR_EQ("|=", OPR, ASN), B_XOR_EQ("^=", OPR, ASN),

        SHIFT_RIGHT_EQ(">>=", OPR, ASN), SHIFT_LEFT_EQ("<<=", OPR, ASN),

        PAR_OPEN("("), PAR_CLOSE(")"),

        ARR_OPEN("[", CNT), L_ARR_OPEN("-[", CNT),
        DL_ARR_OPEN("--[", CNT), ARR_CLOSE("]", CNT),
        SCOPE_OPEN("{", CNT), SCOPE_CLOSE("}", CNT),
        SET_OPEN("{<", CNT), SET_CLOSE("<}", CNT),
        UNDIR_OPEN("*-", CNT, OPR), UNDIR_CLOSE("-*", CNT, OPR),
        DIR_OPEN("*->", CNT), DIR_CLOSE("<-*", CNT),

        DIR_EDGE("->", OPR), DIR_2_EDGE("<->", OPR),

        ARR_TYPE("[]", CNT, TYP), L_ARR_TYPE("-[]", CNT, TYP),
        DL_ARR_TYPE("--[]", CNT, TYP),
        UNDIR_TYPE("*-*", CNT, TYP), DIR_TYPE("*->*", CNT, TYP),
        MAP_TYPE("{}", CNT, TYP), SET_TYPE("{><}", CNT, TYP),

        STR_BOUND("\"|\'"),

        INT("int", RSD, TYP), REAL(RSD, TYP), CHAR("char", RSD, TYP), BOOL("bool", RSD, TYP),
        STR("str", RSD, TYP), NULL("null", RSD, TYP, LIT), ANY("any", RSD, TYP),

        IF("if", RSD, DIR), ELSE("else", RSD, DIR), SWITCH("switch", RSD, DIR),
        CASE("case", RSD, DIR), DEFAULT("default", RSD, DIR), SELECT("select", RSD, DIR),
        TRY("try", RSD, DIR), CATCH("catch", RSD, DIR),

        PRINT("print", RSD), SIZE("size", RSD), HASH("hash"), LOOP("loop", RSD),

        FUNC("func", RSD, CNS, TYP), CLASS("class", RSD, CNS, TYP), ENUM("enum", RSD, CNS, TYP),
        INTERFACE("interface", RSD, CNS, TYP), STRUCT("struct", RSD, CNS, TYP),
        EXTEND("extend", RSD, CNS),

        SUPER("super", RSD), THIS("this", RSD),

        STATIC("static", RSD, MDF), PUBLIC("public", RSD, MDF),
        PRIVATE("private", RSD, MDF), PROPERTY("property", RSD, MDF),
        ABSTRACT("abstract", RSD, MDF), MODULE("module", RSD, MDF),
        FINAL("final", RSD, MDF),

        BREAK("break", RSD), CONTINUE("continue", RSD), PASS("pass"),
        
        RETURN("return", RSD);


        TokenType(String value) {
            _value = value;
            _groups = new ArrayList<>();
        }

        TokenType(String value, String ...groups) {
            _value = value;
            _groups = Arrays.asList(groups);
        }

        private String _value;
        private List<String> _groups;

        public String value() {
            return _value;
        }

        public List<String> groups() {
            return _groups;
        }

        public static TokenTypeTrie reserved = new TokenTypeTrie() {{
            for (TokenType tokenType : TokenType.values()) {
                if (tokenType._groups.contains(RSD)) {
                    insert(tokenType);
                }
            }
        }};
    }

}
