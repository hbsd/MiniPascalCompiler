package enshud.s2.parser.node;

import java.util.Objects;

import enshud.s1.lexer.LexedToken;


public class SingleNode implements IParserNode
{
    protected final INode child;
    
    public SingleNode(INode child)
    {
        this.child = Objects.requireNonNull(child);
    }
    
    public SingleNode()
    {
        child = null;
    }
    
    public INode getChild()
    {
        return child;
    }
    
    @Override
    public LexedToken getToken()
    {
        if (child instanceof IParserNode)
        {
            return ((IParserNode)child).getToken();
        }
        return LexedToken.DUMMY;
    }
    
    @Override
    public boolean isSuccess()
    {
        return true;
    }
    
    @Override
    public String toString()
    {
        return "";
    }
    
    @Override
    public void printBodyln(String indent)
    {
        if (getChild() != null)
        {
            getChild().println(indent + "  ");
        }
    }
}

