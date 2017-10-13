package enshud.s3.checker.ast;

import java.util.Objects;

import enshud.s3.checker.Checker;
import enshud.s3.checker.Procedure;
import enshud.s1.lexer.LexedToken;
import enshud.s3.checker.type.IType;
import enshud.s3.checker.type.RegularType;


public class BooleanLiteral implements IConstant
{
    final BooleanValue val;
    final LexedToken   bool_token;

    public BooleanLiteral(LexedToken bool_token)
    {
        this.bool_token = Objects.requireNonNull(bool_token);
        switch( bool_token.getType() )
        {
        case SFALSE:
            val = BooleanValue.FALSE;
            break;
        case STRUE:
            val = BooleanValue.TRUE;
            break;
        default:
            val = null;
            assert false;
        }
    }

    public BooleanValue getValue()
    {
        return val;
    }

    public boolean getBool()
    {
        return val.getBool();
    }

    @Override
    public IType getType()
    {
        return RegularType.BOOLEAN;
    }

    @Override
    public int getLine()
    {
        return bool_token.getLine();
    }

    @Override
    public int getColumn()
    {
        return bool_token.getColumn();
    }

    @Override
    public void retype(IType new_type)
    {}

    @Override
    public IType check(Procedure proc, Checker checker)
    {
        return getType();
    }

    /*@Override
    public void compile(StringBuilder codebuilder, Procedure proc, LabelGenerator l_gen)
    {
        codebuilder.append(" LAD GR2,").append(getBool()? "1": "0").append(System.lineSeparator());
    }*/
}


