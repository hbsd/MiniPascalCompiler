package enshud.s3.checker.ast;


public interface IConstant extends IFactor, ILiteral
{
    @Override
    default void printHead(String indent, String msg)
    {
        ILiteral.super.printHead(indent, msg);
    }
}


