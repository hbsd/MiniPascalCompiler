package enshud.pascal.ast.declaration;

import enshud.pascal.ast.IASTNode;
import enshud.s2.parser.node.INode;


public interface IDeclaration extends IASTNode
{
    @Override
    default void printHead(String indent, String msg)
    {
        System.out.print("-");
        INode.printRed(getClass().getSimpleName());
        System.out.print(": ");
        INode.printGreenln(msg != null? msg: toString());
    }
    
    @Override
    default String toOriginalCode(String indent)
    {
        throw new UnsupportedOperationException();
    }
}

