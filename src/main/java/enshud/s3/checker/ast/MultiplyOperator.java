package enshud.s3.checker.ast;


public enum MultiplyOperator implements ILiteral
{
    MUL, DIV, MOD, AND;

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


