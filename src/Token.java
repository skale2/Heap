import java.util.*;

class Token {

    Token(String value, TokenType type) {
        _value = value;
        _type = type;
    }

    Token(TokenType type) {
        _value = type._value;
        _type = type;
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

    public Boolean isLiteral()      { return _type._groups.contains(LIT); }
    public Boolean isReserved()     { return _type._groups.contains(RSD); }
    public Boolean isOperator()     { return _type._groups.contains(OPR); }
    public Boolean isModifier()     { return _type._groups.contains(MDF); }
    public Boolean isType()         { return _type._groups.contains(TYP); }
    public Boolean isConstruct()    { return _type._groups.contains(CNS); }
    public Boolean isDirect()       { return _type._groups.contains(DIR); }

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
            CNT = "CONTAINER";

    public enum TokenType {

        EOL(";"), EOF(""), COMMA(","), COLON(":"), DIRECT("=>"),
        ANNOTATION("@"), TERNARY("?"),

        ASSIGN("="), EQUAL("==", OPR), CAST_EQUALS(":=="), NOT_EQUAL("!="), CAST_NOT_EQUAL(":!="),

        VAR("[a-zA-Z\\_]+[a-zA-Z0-9\\_]*"),

        REAL_VAL("\\d*\\.\\d+", LIT),
        INT_VAL("\\d+", LIT),
        STR_VAL("(\"|\')\\.*(\"|\')", LIT),

        ADD("+", OPR), SUBTRACT("-", OPR), MULTIPLY("*", OPR), DIVIDE("/", OPR),
        MOD("%", OPR), FLOOR("-/", OPR), EXP("**", OPR), ROUND("`", OPR),
        INCREMENT("++", OPR), DECREMENT("--", OPR),

        L_AND("&&", OPR), L_OR("||", OPR), L_NOT("!", OPR), L_XOR("^^", OPR),
        B_AND("&", OPR), B_OR("|", OPR), B_NOT("~", OPR), B_XOR("^", OPR),

        LESS_THAN("<", OPR), GREATER_THAN(">", OPR),

        TRUE("true", RSD), FALSE("false", RSD),

        PAR_OPEN("("), PAR_CLOSE(")"),

        ARR_OPEN("[", CNT), L_ARR_OPEN("-[", CNT),
        DL_ARR_OPEN("--[", CNT), ARR_CLOSE("]", CNT),
        SCOPE_OPEN("{", CNT), SCOPE_CLOSE("}", CNT),
        SET_OPEN("{<", CNT), SET_CLOSE("<}", CNT),
        UNDIR_OPEN("*-", CNT), UNDIR_CLOSE("-*", CNT),
        DIR_OPEN("*->", CNT), DIR_CLOSE("<-*", CNT),
        DIR_EDGE("->", CNT), DIR_2_EDGE("<->", CNT),

        ARR_TYPE("[]", CNT, TYP), L_ARR_TYPE("-[]", CNT, TYP),
        DL_ARR_TYPE("--[]", CNT, TYP),
        UNDIR_TYPE("*-*", CNT, TYP), DIR_TYPE("*->*", CNT, TYP),
        MAP_TYPE("{}", CNT, TYP), SET_TYPE("{><}", CNT, TYP),

        STR_BOUND("\"|\'"),

        INT("int", RSD, TYP), LONG("long", RSD, TYP), DOUBLE("double", RSD, TYP),
        FLOAT("float", RSD, TYP), CHAR("char", RSD, TYP), BOOL("bool", RSD, TYP),
        STR("str", RSD, TYP), NULL("null", RSD, TYP), ANY("any", RSD, TYP),

        IF("if", RSD, DIR), ELSE("else", RSD, DIR), SWITCH("switch", RSD, DIR),
        CASE("case", RSD, DIR), DEFAULT("default", RSD, DIR), SELECT("select", RSD, DIR),
        TRY("try", RSD, DIR), CATCH("catch", RSD, DIR),

        PRINT("print", RSD), SIZE("size", RSD), HASH("hash"), LOOP("loop", RSD),

        FUNC("func", RSD, CNS), CLASS("class", RSD, CNS), ENUM("enum", RSD, CNS),
        INTERFACE("interface", RSD, CNS), STRUCT("struct", RSD, CNS),
        EXTEND("extend", RSD, CNS),

        STATIC("static", RSD, MDF), PUBLIC("public", RSD, MDF),
        PRIVATE("private", RSD, MDF), PROPERTY("property", RSD, MDF),
        ABSTRACT("abstract", RSD, MDF), SUPER("super", RSD, MDF),
        FINAL("final", RSD, MDF), MODULE("module", RSD, MDF),

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
