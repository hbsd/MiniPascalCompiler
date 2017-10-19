package enshud.pascal.ast;

import enshud.s2.parser.node.INode;


public interface ILiteral extends IASTNode
{
    @Override
    default void printHead(String indent, String msg)
    {
        System.out.print("-");
        INode.printCyan(getClass().getSimpleName());
        System.out.print(": ");
        INode.printGreenln(msg != null? msg: toString());
    }
}


