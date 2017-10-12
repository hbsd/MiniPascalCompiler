package enshud.s2.parser.node.basic;

import java.util.Objects;

import enshud.s1.lexer.LexedToken;
import enshud.s2.parser.node.INode;


public class FailureNode extends SingleNode
{
    String msg;

    public FailureNode(INode child, String msg)
    {
        super(child);
        this.msg = Objects.requireNonNull(msg);
    }

    public FailureNode(LexedToken token, String msg)
    {
        this(new TokenNode(token), msg);
    }

    public FailureNode(INode child)
    {
        this(child, "Error Found.");
    }

    public FailureNode(LexedToken token)
    {
        this(new TokenNode(token), "Error Found.");
    }

    public FailureNode(String msg)
    {
        super();
        this.msg = Objects.requireNonNull(msg);
    }

    public FailureNode()
    {
        this("Error Found.");
    }

    @Override
    public boolean isSuccess()
    {
        return false;
    }

    @Override
    public String toString()
    {
        return msg;
    }
}


