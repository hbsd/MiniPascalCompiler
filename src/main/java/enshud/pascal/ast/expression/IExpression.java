package enshud.pascal.ast.expression;

import enshud.pascal.ast.IASTNode;
import enshud.pascal.ast.IAcceptable;
import enshud.pascal.type.IType;
import enshud.s2.parser.node.INode;


public interface IExpression extends IASTNode, IAcceptable
{
    IType getType();
    default boolean isConstant()
    {
        return false;
    }
    boolean equals(IExpression rexp);
    
    @Override
    default void printHead(String indent, String msg)
    {
        System.out.print("-");
        INode.printYellow(getClass().getSimpleName());
        System.out.print(": ");
        INode.printGreenln(msg != null? msg: toString());
    }
}
