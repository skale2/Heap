
class TokenTypeTrie {
    TokenTypeTrie() {
        _root = new TrieNode(' ');
    }

    /**
     * Inserts WORD into the trie
     * @param token The set of characters to be inserted
     */
    void insert(Token.TokenType token) {
        TrieNode current = _root;
        TrieNode next;
        for (int i = 0; i < token.value().length(); i++) {
            next = current.children()[token.value().charAt(i) - 'A'];
            if (next == null) {
                next = current.setChild(token.value().charAt(i));
            }
            current = next;
        }
        current.setToken(token);
    }

    /**
     * Finds WORD in the trie and returns the token associated with it
     * @param word Set of characters to be found
     * @return The token associated with word, or null if not found
     */
    Token.TokenType find(String word) {
        TrieNode current = _root;
        for (int i = 0; i < word.length(); i++) {
            TrieNode node = current.getChild(word.charAt(i));
            if (node == null) { return null; }
            current = node;
        }
        return current.tokenType();
    }

    TrieNode root() { return _root; }

    private TrieNode _root;

    static class TrieNode {
        private TrieNode[] _children;
        private char _content;
        private Token.TokenType _token;

        TrieNode(char content) {
            _content = content;
            _children = new TrieNode[58];
            _token = null;
        }

        TrieNode[] children() {
            return _children;
        }

        TrieNode getChild(char c) { return _children[c - 'A']; }

        TrieNode setChild(char c) {
            TrieNode child = new TrieNode(c);
            _children[c - 'A'] = child;
            return child;
        }

        Token.TokenType tokenType() {
            return _token;
        }

        void setToken(Token.TokenType token) {
            this._token = token;
        }
    }
}
