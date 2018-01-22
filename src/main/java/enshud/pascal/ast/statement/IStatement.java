package enshud.pascal.ast.statement;

import enshud.pascal.ast.IASTNode;
import enshud.pascal.ast.IAcceptable;
import enshud.s2.parser.node.INode;


public interface IStatement extends IASTNode, IAcceptable
{
    @Override
    default void printHead(String indent, String msg)
    {
        System.out.print("-");
        INode.printPurple(getClass().getSimpleName());
        System.out.print(": ");
        INode.printGreenln(msg != null? msg: toString());
    }
}

