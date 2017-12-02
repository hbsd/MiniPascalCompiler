package enshud.pascal.ast;

import enshud.s3.checker.ICheckable;
import enshud.s3.checker.IPreevaluable;
import enshud.pascal.type.IType;
import enshud.s2.parser.node.INode;
import enshud.s4.compiler.ICompilable;


public interface ITyped extends IASTNode, ICheckable, IPreevaluable, ICompilable
{
    IType getType();
    
    void retype(IType new_type);
    
    @Override
    default void printHead(String indent, String msg)
    {
        System.out.print("-");
        INode.printYellow(getClass().getSimpleName());
        System.out.print(": ");
        INode.printGreenln(msg != null? msg: toString());
    }
}
