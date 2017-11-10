package enshud.pascal.ast;


public enum AddOperator implements ILiteral
{
    ADD,
    SUB,
    OR;
    
    @Override
    public int getLine()
    {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public int getColumn()
    {
        throw new UnsupportedOperationException();
    }
}
