package enshud.pascal.ast.expression;

import java.util.Objects;

import enshud.pascal.value.BooleanValue;
import enshud.pascal.ast.IVisitor;
import enshud.pascal.type.BasicType;
import enshud.s1.lexer.LexedToken;


public class BooleanLiteral implements IConstant
{
    private BooleanValue     val;
    private final LexedToken token;
    
    private BooleanLiteral(boolean val)
    {
        this.val = BooleanValue.create(val);
        this.token = LexedToken.DUMMY;
    }
    
    public static BooleanLiteral create(boolean val)
    {
        return new BooleanLiteral(val);
    }
    
    public BooleanLiteral(LexedToken token)
    {
        this.token = Objects.requireNonNull(token);
        switch (token.getType())
        {
        case SFALSE:
            val = BooleanValue.FALSE;
            break;
        case STRUE:
            val = BooleanValue.TRUE;
            break;
        default:
            assert false;
        }
    }
    
    @Override
    public BooleanValue getValue()
    {
        return val;
    }
    
    @Override
    public BasicType getType()
    {
        return BasicType.BOOLEAN;
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
        if (rexp instanceof BooleanLiteral)
        {
            return this == rexp || this.val == ((BooleanLiteral)rexp).val;
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
    
    @Override
    public String toOriginalCode(String indent)
    {
        return "" + val.getBool();
    }
    
    @Override
    public String toString()
    {
        return val.toString();
    }
}

