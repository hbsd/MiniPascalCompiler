package enshud.pascal.ast.expression;

import enshud.pascal.ast.ILiteral;
import enshud.pascal.value.IValue;


public interface IConstant extends IExpression, ILiteral
{
    @Override
    default boolean isConstant()
    {
        return true;
    }
    @Override
    default void printHead(String indent, String msg)
    {
        ILiteral.super.printHead(indent, msg);
    }

    IValue getValue();
}

