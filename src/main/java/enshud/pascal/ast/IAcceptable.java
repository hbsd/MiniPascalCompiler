package enshud.pascal.ast;

public interface IAcceptable
{
    <T,U> T accept(IVisitor<T, U> visitor, U option);
}
