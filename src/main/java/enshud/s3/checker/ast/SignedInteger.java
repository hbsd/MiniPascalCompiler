package enshud.s3.checker.ast;

import java.util.Objects;

public class SignedInteger implements ILiteral
{
    final SignLiteral     sign_lit;
    final UnsignedInteger uinteger;

    public SignedInteger(SignLiteral sign_lit, UnsignedInteger uinteger)
    {
        this.sign_lit = Objects.requireNonNull(sign_lit);
        this.uinteger = Objects.requireNonNull(uinteger);
    }

    public Sign getSign()
    {
        return sign_lit.getSign();
    }

    public int getInt()
    {
        switch( sign_lit.getSign() )
        {
        case PLUS:
        case NONE:
            return uinteger.getInt();
        case MINUS:
            return -uinteger.getInt();
        }
        assert false;
        return 0;
    }

    @Override
    public String toString()
    {
        switch( sign_lit.getSign() )
        {
        case PLUS:
            return "+" + uinteger;
        case MINUS:
            return "-" + uinteger;
        case NONE:
            return "" + uinteger;
        }
        assert false;
        return "";
    }

    @Override
    public int getLine()
    {
        return sign_lit.getLine();
    }

    @Override
    public int getColumn()
    {
        return sign_lit.getColumn();
    }
}


