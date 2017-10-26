package enshud.pascal.ast;

import java.util.Objects;

import enshud.pascal.type.ArrayType;
import enshud.pascal.type.IType;
import enshud.pascal.type.BasicType;
import enshud.s1.lexer.LexedToken;


public class TypeLiteral implements ILiteral
{
    final IType      type;
    final LexedToken token;
    
    
    public TypeLiteral(LexedToken token)
    {
        this.token = Objects.requireNonNull(token);
        switch (token.getType())
        {
        case SINTEGER:
            this.type = BasicType.INTEGER;
            break;
        case SCHAR:
            this.type = BasicType.CHAR;
            break;
        case SBOOLEAN:
            this.type = BasicType.BOOLEAN;
            break;
        default:
            this.type = null;
            assert false;
        }
    }
    
    private TypeLiteral(IType type, LexedToken token)
    {
        this.type = Objects.requireNonNull(type);
        this.token = Objects.requireNonNull(token);
    }
    
    public TypeLiteral(BasicType rtype, SignedInteger min, SignedInteger max, LexedToken token)
    {
        this(new ArrayType(rtype, min, max), token);
    }
    
    public IType getType()
    {
        return type;
    }
    
    public BasicType getRegularType()
    {
        return type.getBasicType();
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
    
    public boolean isArrayOf(BasicType rtype)
    {
        return type.isArrayOf(rtype);
    }
    
    @Override
    public String toString()
    {
        return type.toString();
    }
}

