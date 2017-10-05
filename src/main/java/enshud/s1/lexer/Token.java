package enshud.s1.lexer;

/**
 * Simple token data
 */
public class Token
{
    final String str;
    final TokenType type;
    Token(String str, TokenType type)
    {
        this.str = str;
        this.type = type;
    }
}
