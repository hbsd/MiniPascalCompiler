package enshud.s2.parser.node.basic;

import enshud.s1.lexer.LexedToken;


public class EmptyNode implements IParserNode
{
    @Override
    public boolean isSuccess()
    {
        return true;
    }
    
    @Override
    public LexedToken getToken()
    {
        return LexedToken.DUMMY;
    }
    
    @Override
    public String toString()
    {
        return "";
    }
}

