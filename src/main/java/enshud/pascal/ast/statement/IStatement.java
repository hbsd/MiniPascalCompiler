package enshud.pascal.ast.statement;

import enshud.s3.checker.ICheckable;
import enshud.s4.compiler.ICompilable;
import enshud.s4.compiler.IPrecomputable;
import enshud.pascal.ast.IASTNode;
import enshud.s2.parser.node.INode;


public interface IStatement extends IASTNode, ICheckable, IPrecomputable, ICompilable
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

