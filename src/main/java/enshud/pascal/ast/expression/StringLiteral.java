package enshud.pascal.ast.expression;

import enshud.pascal.ast.IVisitor;
import enshud.pascal.type.StringType;
import enshud.pascal.value.StringValue;
import enshud.s2.parser.node.TokenNode;


public class StringLiteral implements IConstant
{
    private final StringValue val;
    private final int         line;
    private final int         col;
    
    public StringLiteral(TokenNode str_tok)
    {
        this(
            str_tok.getString().substring(1, str_tok.getString().length() - 1),
            str_tok.getLine(),
            str_tok.getColumn()
        );
    }
    
    public StringLiteral(String str, int line, int col)
    {
        this.val = StringValue.create(str);
        this.line = line;
        this.col = col;
    }
    
    public static StringLiteral create(String str)
    {
        return new StringLiteral(str, -1, -1);
    }
    
    @Override
    public StringValue getValue()
    {
        return val;
    }
    
    public int getInt()
    {
        if (getType() == StringType.CHAR)
        {
            return (int)toString().charAt(1);
        }
        else
        {
            throw new UnsupportedOperationException("not CHAR type");
        }
    }
    
    @Override
    public String toString()
    {
        return val.getString();
    }
    
    public int length()
    {
        return val.getType().getSize();
    }
    
    @Override
    public StringType getType()
    {
        return val.getType();
    }
    
    @Override
    public int getLine()
    {
        return line;
    }
    
    @Override
    public int getColumn()
    {
        return col;
    }
    
    @Override
    public String toOriginalCode(String indent)
    {
        return "'" + getValue().getString() + "'";
    }
    
    @Override
    public boolean equals(IExpression rexp)
    {
        if (rexp instanceof StringLiteral)
        {
            return this == rexp || this.toString() == ((StringLiteral)rexp).toString();
        }
        else
        {
            return false;
        }
    }
    
    @Override
    public <T, U> T accept(IVisitor<T, U> visitor, U option)
    {
        return visitor.visit(this, option);
    }
}

