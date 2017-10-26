package enshud.pascal.ast;

import enshud.s3.checker.Context;
import enshud.s3.checker.Procedure;

public interface IConstant extends IFactor, ILiteral
{
    @Override
    default IConstant preeval(Procedure proc, Context context)
    {
        return this;
    }

    @Override
    default void printHead(String indent, String msg)
    {
        ILiteral.super.printHead(indent, msg);
    }
}

