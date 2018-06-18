import sun.text.normalizer.Trie;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayDeque;


public class Lexer {
    public Lexer(BufferedReader text) {
        _text = text;
        _peekChars = new ArrayDeque();
        _EOF = false;
        advance();
    }

    /** Advances pointer in the filestream by one step
     * */
    private char advance() {
        try {
            if (!_peekChars.isEmpty()) {
                return (char) _peekChars.poll();
            }
            _current = (char) _text.read();
            return _current;
        } catch (IOException io) {
            _EOF = true;
        } return EMPTY;
    }

    /** Advances pointer in the filestream by NUM steps
     * */
    private char advance(int num) {
        try {
            while(num > 0) {
                while (!_peekChars.isEmpty()) {
                    _current = (char) _peekChars.poll();
                }
                _current = (char) _text.read();
                num -= 0;
            }
            return _current;
        } catch (IOException io) {
            _EOF = true;
        } return EMPTY;
    }

    /** Allows for peeking ahead one char in the filestram
     * */
    private char peek() {
        try {
            if (!_peekChars.isEmpty()) {
                return (char) _peekChars.peek();
            }
            char ch = (char) _text.read();
            _peekChars.offer(ch);
            return ch;
        } catch (IOException io) {
            return EMPTY;
        }
    }

    /** Allows for peeking ahead NUM chars in the filestream
     * */
    private char peek(int num) {
        try {
            char ch = EMPTY;
            while(num > 0) {
                if (!_peekChars.isEmpty()) {
                    ch = (char) _peekChars.peek();
                }
                ch = (char) _text.read();
                _peekChars.offer(ch);
            }
            return ch;
        } catch (IOException io) {
            return EMPTY;
        }
    }

    private Token getString(char type) {
        StringBuilder string = new StringBuilder();
        string.append(_current);
        while (peek() != type && _current != '\\') {
            string.append(advance());
        }
        return new Token(string.toString(), Token.TokenType.STR_VAL);
    }

    private Token getNumber() {
        StringBuilder number = new StringBuilder();
        number.append(_current);

        Token.TokenType type = Token.TokenType.INT_VAL;
        while (_current != EMPTY) {
            if (advance() == '.') {
                type = Token.TokenType.REAL_VAL;
            }
            number.append(_current);
        }
        return new Token(number.toString(), Token.TokenType.STR_VAL);
    }

    private Token getIdentifier() {
        StringBuilder id = new StringBuilder();
        TokenTypeTrie.TrieNode node = Token.TokenType.reserved.root();

        /* Keep advancing through the text, adding each character to the
        * final string, and checking to see if the string is still in the
        * reserved words trie. If it isn't, only add to the final string.
        */
        while(Character.isAlphabetic(_current) ||
                Character.isDigit(_current) ||
                _current == '_') {
            if (node != null) {
                node = node.getChild(_current);
            }
            id.append(_current);
            advance();
        }

        /* If the string of characters is still in the reserved words trie
        * and the final character has a token associated with it, return
        * that token
        * */
        if (node != null && node.tokenType() != null) {
            return new Token(node.tokenType());
        }
        /* Else, return the string as a variable token */
        return new Token(id.toString(), Token.TokenType.VAR);
    }

    private void skipWhitespace() {
        while (_current == EMPTY) { advance(); }
    }

    private void skipComment(char type) {
        if (type == '*') {
            while (advance() != type && peek() != '/') {}
        } else if (type == '/') {
            while (advance() != '\n') {}
        }
    }


    public Token next() {
        if (_current == ' ') {
            skipWhitespace();
            return next();
        } else if (_current == '"' || _current == '\'') {
            return getString(_current);
        } else if (Character.isDigit(_current)) {
            return getNumber();
        } else if (Character.isLetter(_current) || _current == '_') {
            return getIdentifier();
        } else if (_current == '-' && peek() == '[') {
            advance(2);
            return new Token(Token.TokenType.L_ARR_OPEN);
        } else if (_current == '{') {
            if (peek() == '>') {
                advance(2);
                return new Token(Token.TokenType.SET_OPEN);
            }
            advance();
            return new Token(Token.TokenType.SCOPE_OPEN);
        } else if (_current == '-' && peek() == '-' && peek(2) == '[') {
            advance(2);
            return new Token(Token.TokenType.DL_ARR_OPEN);
        } else if (_current == '/') {
            if (peek() == '*') {
                skipComment('*');
                return next();
            } else if (peek() == '/') {
                skipComment('/');
                return next();
            }
            return new Token(Token.TokenType.DIVIDE);
        } else if (_current == '=') {
            if (peek() == '=') {
                advance(2);
                return new Token(Token.TokenType.EQUAL);
            } else if (peek() == '>') {
                advance(2);
                return new Token(Token.TokenType.DIRECT);
            }
            advance();
            return new Token(Token.TokenType.ASSIGN);
        } else if (_current == ':') {
            if (peek() == '=' && peek(2) == '=') {
                advance(3);
                return new Token(Token.TokenType.CAST_EQUALS);
            } else if (peek() == '!' && peek(2) == '=') {
                advance(3);
                return new Token(Token.TokenType.CAST_NOT_EQUAL);
            }
            advance();
            return new Token(Token.TokenType.COLON);
        } else if (_current == '&') {
            if (peek() == '&') {
                advance(2);
                return new Token(Token.TokenType.L_AND);
            }
            advance();
            return new Token(Token.TokenType.B_AND);
        } else if (_current == '|') {
            if (peek() == '|') {
                advance(2);
                return new Token(Token.TokenType.L_OR);
            }
            advance();
            return new Token(Token.TokenType.B_OR);
        } else if (_current == '^') {
            if (peek() == '^') {
                advance(2);
                return new Token(Token.TokenType.L_XOR);
            }
            advance();
            return new Token(Token.TokenType.B_XOR);
        } else if (_current == '!') {
            if (peek() == '=') {
                advance(2);
                return new Token(Token.TokenType.NOT_EQUAL);
            }
            advance();
            return new Token(Token.TokenType.L_NOT);
        } else if (_current == '<') {
            if (peek() == '}') {
                advance(2);
                return new Token(Token.TokenType.SET_CLOSE);
            }
            advance();
            return new Token(Token.TokenType.LESS_THAN);
        }


        switch(_current) {
            case ',': return new Token(Token.TokenType.COMMA);
            case ';': return new Token(Token.TokenType.EOL);
            case '+': return new Token(Token.TokenType.ADD);
            case '-': return new Token(Token.TokenType.SUBTRACT);
            case '*': return new Token(Token.TokenType.MULTIPLY);
            case '%': return new Token(Token.TokenType.MOD);
            case '`': return new Token(Token.TokenType.ROUND);
            case '>': return new Token(Token.TokenType.GREATER_THAN);
            case '[': return new Token(Token.TokenType.ARR_OPEN);
            case ']': return new Token(Token.TokenType.ARR_CLOSE);
            case '}': return new Token(Token.TokenType.SCOPE_CLOSE);
            case '(': return new Token(Token.TokenType.PAR_OPEN);
            case ')': return new Token(Token.TokenType.PAR_CLOSE);
            case '~': return new Token(Token.TokenType.B_NOT);
            case '@': return new Token(Token.TokenType.ANNOTATION);
            case '?': return new Token(Token.TokenType.TERNARY);
            default:
                System.out.println("Error: Syntax");
                return null;
        }
    }

    private static char EMPTY = ' ';
    private boolean _EOF;
    private char _current;
    private ArrayDeque _peekChars;
    private BufferedReader _text;
}
