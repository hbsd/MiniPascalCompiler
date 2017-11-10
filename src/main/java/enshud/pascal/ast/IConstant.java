package enshud.pascal.ast;

import enshud.s3.checker.Procedure;


public interface IConstant extends ITyped, ILiteral
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

