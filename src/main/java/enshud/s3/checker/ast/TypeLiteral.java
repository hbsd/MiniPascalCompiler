package enshud.s3.checker.ast;

import java.util.Objects;

import enshud.s1.lexer.LexedToken;
import enshud.s3.checker.type.ArrayType;
import enshud.s3.checker.type.IType;
import enshud.s3.checker.type.RegularType;


public class TypeLiteral implements ILiteral
{
    final IType      type;
    final LexedToken token;


    public TypeLiteral(LexedToken token)
    {
        this.token = Objects.requireNonNull(token);
        switch( token.getType() )
        {
        case SINTEGER:
            this.type = RegularType.INTEGER;
            break;
        case SCHAR:
            this.type = RegularType.CHAR;
            break;
        case SBOOLEAN:
            this.type = RegularType.BOOLEAN;
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

    public TypeLiteral(RegularType rtype, SignedInteger min, SignedInteger max, LexedToken token)
    {
        this(new ArrayType(rtype, min, max), token);
    }

    public IType getType()
    {
        return type;
    }

    public RegularType getRegularType()
    {
        return type.getRegularType();
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

    public boolean isArrayOf(RegularType rtype)
    {
        return type.isArrayOf(rtype);
    }

    @Override
    public String toString()
    {
        return type.toString();
    }
}


