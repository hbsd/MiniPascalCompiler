package enshud.pascal.ast;


public interface IConstant extends IFactor, ILiteral
{
    @Override
    default void printHead(String indent, String msg)
    {
        ILiteral.super.printHead(indent, msg);
    }
}


