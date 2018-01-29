package enshud.pascal.ast;

import java.util.ArrayList;
import java.util.Objects;

import enshud.s2.parser.node.INode;

@SuppressWarnings("serial")
public class NodeList<T extends INode> extends ArrayList<T> implements IASTNode
{
    public NodeList()
    {
        super();
    }
    
    public NodeList(T e)
    {
        super();
        add(Objects.requireNonNull(e));
    }
    
    @Override
    public void printHead(String indent, String msg)
    {
        System.out.print("-");
        INode.printBlue(getClass().getSimpleName());
        System.out.print(": ");
        INode.printGreenln(msg != null? msg: size() + " elements");
    }
    
    @Override
    public void printBodyln(String indent)
    {
        if (!isEmpty())
        {
            subList(0, size() - 1)
                .forEach(elem -> elem.println(indent + " |"));
            
            get(size() - 1).println(indent + "  ");
        }
    }
    
    @Override
    public int getLine()
    {
        return get(0).getLine();
    }
    
    @Override
    public int getColumn()
    {
        return get(0).getColumn();
    }
    
    @Override
    public String toOriginalCode(String indent)
    {
        throw new UnsupportedOperationException();
    }
}
