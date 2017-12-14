package enshud.pascal.ast.expression;

import enshud.s3.checker.ICheckable;
import enshud.pascal.ast.IASTNode;
import enshud.pascal.type.IType;
import enshud.s2.parser.node.INode;
import enshud.s4.compiler.ICompilable;
import enshud.s4.compiler.IPreevaluable;


public interface IExpression extends IASTNode, ICheckable, IPreevaluable, ICompilable
{
    IType getType();
    
    @Override
    default void printHead(String indent, String msg)
    {
        System.out.print("-");
        INode.printYellow(getClass().getSimpleName());
        System.out.print(": ");
        INode.printGreenln(msg != null? msg: toString());
    }
}
