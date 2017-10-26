package enshud.pascal.ast;

import java.util.Objects;

import enshud.s1.lexer.LexedToken;


public class SignLiteral implements ILiteral
{
    public static final SignLiteral NONE = new SignLiteral();
    final Sign                      sign;
    final LexedToken                sign_token;
    
    public SignLiteral(LexedToken sign_token)
    {
        this.sign_token = Objects.requireNonNull(sign_token);
        switch (sign_token.getType())
        {
        case SPLUS:
            sign = Sign.PLUS;
            break;
        case SMINUS:
            sign = Sign.MINUS;
            break;
        default:
            sign = null;
            assert false;
        }
    }
    
    private SignLiteral()
    {
        sign = Sign.NONE;
        sign_token = null;
    }
    
    public Sign getSign()
    {
        return sign;
    }
    
    @Override
    public int getLine()
    {
        if (sign_token != null)
        {
            return sign_token.getLine();
        }
        else
        {
            throw new UnsupportedOperationException();
        }
    }
    
    @Override
    public int getColumn()
    {
        if (sign_token != null)
        {
            return sign_token.getColumn();
        }
        else
        {
            throw new UnsupportedOperationException();
        }
    }
    
    @Override
    public String toString()
    {
        return sign.toString();
    }
}

