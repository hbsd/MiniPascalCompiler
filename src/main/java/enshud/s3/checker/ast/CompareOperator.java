package enshud.s3.checker.ast;


public enum CompareOperator implements ILiteral
{
    EQUAL, NOTEQUAL, LESS, LESSEQUAL, GREAT, GREATEQUAL;

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


