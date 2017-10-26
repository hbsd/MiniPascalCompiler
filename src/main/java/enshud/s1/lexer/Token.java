package enshud.s1.lexer;

import java.util.Objects;


/**
 * Simple token data
 */
public class Token
{
    final String    str;
    final TokenType type;
    
    public Token(String str, TokenType type)
    {
        this.str = Objects.requireNonNull(str);
        this.type = Objects.requireNonNull(type);
    }
}

