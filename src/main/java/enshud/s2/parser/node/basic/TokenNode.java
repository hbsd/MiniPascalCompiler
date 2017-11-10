package enshud.s2.parser.node.basic;

import java.util.Objects;

import enshud.s1.lexer.LexedToken;
import enshud.s1.lexer.TokenType;


public class TokenNode implements IParserNode
{
    private final LexedToken token;
    
    public TokenNode(LexedToken token)
    {
        this.token = Objects.requireNonNull(token);
    }
    
    @Override
    public LexedToken getToken()
    {
        return token;
    }
    
    public String getString()
    {
        return token.getString();
    }
    
    public TokenType getType()
    {
        return token.getType();
    }
    
    @Override
    public boolean isSuccess()
    {
        return true;
    }
    
    @Override
    public String toString()
    {
        return "" + token;
    }
}

