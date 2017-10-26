package enshud.pascal.ast;

import java.util.List;

import enshud.s2.parser.node.INode;


public interface IList<T extends INode> extends IASTNode
{
    List<T> getList();
    
    default int size()
    {
        return getList().size();
    }
    
    @Override
    default void printHead(String indent, String msg)
    {
        System.out.print("-");
        INode.printBlue(getClass().getSimpleName());
        System.out.print(": ");
        INode.printGreenln(msg != null? msg: getList().size() + " elements");
    }
    
    @Override
    default void printBodyln(String indent)
    {
        final List<T> l = getList();
        if (!l.isEmpty())
        {
            for (final T elem: l.subList(0, l.size() - 1))
            {
                elem.println(indent + " |");
            }
            
            l.get(l.size() - 1).println(indent + "  ");
        }
    }
    
    @Override
    default int getLine()
    {
        return getList().get(0).getLine();
    }
    
    @Override
    default int getColumn()
    {
        return getList().get(0).getColumn();
    }
}

