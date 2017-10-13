package enshud.s3.checker.ast;

import java.util.Objects;

import enshud.s3.checker.Checker;
import enshud.s3.checker.Procedure;
import enshud.s3.checker.type.IType;
import enshud.s3.checker.type.RegularType;
import enshud.s3.checker.type.UnknownType;


public class SignedSimpleExpression implements ITyped
{
    final SignLiteral       sign_lit;
    final ISimpleExpression exp;
    IType                   type;

    public SignedSimpleExpression(SignLiteral sign_lit, ISimpleExpression exp)
    {
        this.sign_lit = Objects.requireNonNull(sign_lit);
        this.exp = Objects.requireNonNull(exp);
        switch( getSign() )
        {
        case PLUS:
        case MINUS:
            type = RegularType.INTEGER;
            break;
        case NONE:
            type = UnknownType.UNKNOWN;
            break;
        default:
            assert false;
        }
    }

    public SignedSimpleExpression(ISimpleExpression exp)
    {
        this.sign_lit = SignLiteral.NONE;
        this.exp = Objects.requireNonNull(exp);
        type = UnknownType.UNKNOWN;
    }

    public Sign getSign()
    {
        return sign_lit.getSign();
    }
    
    public ISimpleExpression getExpression()
    {
        return exp;
    }

    @Override
    public int getLine()
    {
        return (getSign() != Sign.NONE)? sign_lit.getLine(): exp.getLine();
    }

    @Override
    public int getColumn()
    {
        return (getSign() != Sign.NONE)? sign_lit.getColumn(): exp.getLine();
    }

    @Override
    public IType getType()
    {
        return type;
    }

    @Override
    public void retype(IType new_type)
    {
        if( getType().isUnknown() )
        {
            type = new_type;
        }
        exp.retype(new_type);
    }

    @Override
    public IType check(Procedure proc, Checker checker)
    {
        final IType t = exp.check(proc, checker);
        switch( getSign() )
        {
        case PLUS:
        case MINUS:
            if( t.isUnknown() )
            {
                exp.retype(RegularType.INTEGER);
            }
            if( !t.equals(RegularType.INTEGER) )
            {
                checker.addErrorMessage(
                    proc, this, "incompatible type: " + t + " type cannot have sign. must be INTEGER."
                );
            }
            return type;
        case NONE:
            return t;
        default:
            assert false;
            return null;
        }
    }
    
    /*@Override
    public void compile(StringBuilder codebuilder, Procedure proc, LabelGenerator l_gen)
    {
        getExpression().compile(codebuilder, proc, l_gen);
        switch( getSign() )
        {
        case MINUS:
            codebuilder.append(" LD GR1,GR2").append(System.lineSeparator());
            codebuilder.append(" XOR GR2,GR2").append(System.lineSeparator());
            codebuilder.append(" SUBA GR2,GR1").append(System.lineSeparator());
            break;
        case PLUS:
        case NONE:
            // Empty
            break;
        default:
            assert false;
        }
    }*/

    @Override
    public String toString()
    {
        return "";
    }

    @Override
    public void printBodyln(String indent)
    {
        sign_lit.println(indent + " |");
        exp.printBodyln(indent);
    }
}


