package enshud.pascal.ast;


public enum Sign implements ILiteral
{
    PLUS,
    MINUS,
    NONE;
    
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

