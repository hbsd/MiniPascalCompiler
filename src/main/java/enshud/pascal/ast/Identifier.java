package enshud.pascal.ast;

import java.util.Objects;

import enshud.s2.parser.node.TokenNode;


public class Identifier implements ILiteral
{
    private final TokenNode id;
    
    public Identifier(TokenNode id)
    {
        this.id = Objects.requireNonNull(id);
    }
    
    @Override
    public int getLine()
    {
        return id.getLine();
    }
    
    @Override
    public int getColumn()
    {
        return id.getColumn();
    }
    
    @Override
    public String toString()
    {
        return id.getString();
    }
    
    @Override
    public String toOriginalCode(String indent)
    {
        return toString();
    }
    
    public String getValidString()
    {
        final String s = id.getString();
        return s.length() > 8? s.substring(0, 8): s;
    }
}

