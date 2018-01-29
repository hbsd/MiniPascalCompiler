package enshud.pascal.ast.expression;

import enshud.pascal.ast.IVisitor;
import enshud.pascal.type.BasicType;
import enshud.pascal.value.CharValue;
import enshud.s2.parser.node.TokenNode;


public class CharLiteral implements IConstant
{
    private final CharValue val;
    private final int       line;
    private final int       col;
    
    public CharLiteral(TokenNode ch_tok)
    {
        this(ch_tok.getString().charAt(1), ch_tok.getLine(), ch_tok.getColumn());
    }
    
    public CharLiteral(char ch, int line, int col)
    {
        this.val = CharValue.create(ch);
        this.line = line;
        this.col = col;
    }
    
    public static CharLiteral create(char ch)
    {
        return new CharLiteral(ch, -1, -1);
    }
    
    @Override
    public CharValue getValue()
    {
        return val;
    }
    
    @Override
    public BasicType getType()
    {
        return val.getType();
    }
    
    @Override
    public String toString()
    {
        return val.toString();
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
    public boolean equals(IExpression rexp)
    {
        if (rexp instanceof CharLiteral)
        {
            return this == rexp || this.toString() == ((CharLiteral)rexp).toString();
        }
        else
        {
            return false;
        }
    }
    
    @Override
    public String toOriginalCode(String indent)
    {
        return "'" + (char)getValue().getInt() + "'";
    }
    
    @Override
    public <T, U> T accept(IVisitor<T, U> visitor, U option)
    {
        return visitor.visit(this, option);
    }
}
