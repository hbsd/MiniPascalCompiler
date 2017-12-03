package enshud.pascal.ast.expression;

import enshud.pascal.Procedure;
import enshud.pascal.ast.ILiteral;


public interface IConstant extends IExpression, ILiteral
{
    @Override
    default IConstant preeval(Procedure proc)
    {
        return this;
    }
    
    @Override
    default void printHead(String indent, String msg)
    {
        ILiteral.super.printHead(indent, msg);
    }

    int getInt();
}

