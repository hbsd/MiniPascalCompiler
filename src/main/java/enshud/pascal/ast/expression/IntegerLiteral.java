package enshud.pascal.ast.expression;

import java.util.Objects;

import enshud.pascal.value.IValue;
import enshud.pascal.value.IntegerValue;
import enshud.s1.lexer.LexedToken;
import enshud.pascal.ast.IVisitor;
import enshud.pascal.type.BasicType;


public class IntegerLiteral implements IConstant
{
    private IntegerValue     val;
    private final LexedToken token;
    
    public IntegerLiteral(LexedToken token)
    {
        this.val = IntegerValue.create(Integer.parseInt(token.getString()));
        this.token = Objects.requireNonNull(token);
    }
    
    private IntegerLiteral(int val)
    {
        this.val = IntegerValue.create(val);
        this.token = LexedToken.DUMMY;
    }
    
    public static IntegerLiteral create(int val)
    {
        return new IntegerLiteral(val);
    }
    
    @Override
    public IValue getValue()
    {
        return val;
    }
    
    @Override
    public BasicType getType()
    {
        return val.getType();
    }
    
    @Override
    public int getLine()
    {
        return token.getLine();
    }
    
    @Override
    public int getColumn()
    {
        return token.getColumn();
    }
    
    @Override
    public boolean equals(IExpression rexp)
    {
        if (rexp instanceof IntegerLiteral)
        {
            return this == rexp || this.val == ((IntegerLiteral)rexp).val;
        }
        else
        {
            return false;
        }
    }
    
    @Override
    public String toString()
    {
        return val.toString();
    }
    
    @Override
    public <T, U> T accept(IVisitor<T, U> visitor, U option)
    {
        return visitor.visit(this, option);
    }
}

