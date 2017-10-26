package enshud.s2.parser.node.basic;

import java.util.List;
import java.util.Objects;
import java.util.ArrayList;

import enshud.s1.lexer.LexedToken;
import enshud.s2.parser.node.INode;


public class SequenceNode implements IParserNode
{
    protected final List<INode> children;
    
    public SequenceNode(List<INode> children)
    {
        this.children = Objects.requireNonNull(children);
    }
    
    public SequenceNode()
    {
        children = new ArrayList<>();
    }
    
    public List<INode> getChildren()
    {
        return children;
    }
    
    public INode get(int index)
    {
        return children.get(index);
    }
    
    public SequenceNode getAsSeq(int index)
    {
        return (SequenceNode)get(index);
    }
    
    public TokenNode getAsToken(int index)
    {
        return (TokenNode)get(index);
    }
    
    public int length()
    {
        return children.size();
    }
    
    public boolean isEmpty()
    {
        return children.isEmpty();
    }
    
    @Override
    public LexedToken getToken()
    {
        for (final INode n: children)
        {
            if (n instanceof IParserNode)
            {
                return ((IParserNode)n).getToken();
            }
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
        return length() + " elements.";
    }
    
    @Override
    public void printBodyln(String indent)
    {
        final List<INode> c = getChildren();
        if (!c.isEmpty())
        {
            for (final INode n: c.subList(0, c.size() - 1))
            {
                n.println(indent + " |");
            }
            get(c.size() - 1).println(indent + "  ");
        }
    }
}

