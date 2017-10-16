package enshud.s3.checker.ast;

import java.util.Objects;

import enshud.s3.checker.Checker;
import enshud.s3.checker.Procedure;
import enshud.s3.checker.type.IType;
import enshud.s3.checker.type.RegularType;
import enshud.s3.checker.type.UnknownType;
import enshud.s4.compiler.LabelGenerator;


public class SimpleExpression implements ISimpleExpression
{
    final SignLiteral sign_lit;
    final ITerm       term;
    IType             type;

    public SimpleExpression(SignLiteral sign_lit, ITerm term)
    {
        this.term = Objects.requireNonNull(term);
        this.sign_lit = Objects.requireNonNull(sign_lit);
        switch( sign_lit.getSign() )
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

    public SimpleExpression(ITerm term)
    {
        this(SignLiteral.NONE, term);
    }

    public ITerm getHead()
    {
        return term;
    }

    @Override
    public IType getType()
    {
        return type;
    }

    @Override
    public int getLine()
    {
        return term.getLine();
    }

    @Override
    public int getColumn()
    {
        return term.getColumn();
    }

    @Override
    public void retype(IType new_type)
    {
    	type = new_type;
        term.retype(new_type);
    }

    @Override
    public IType check(Procedure proc, Checker checker)
    {
        type = getHead().check(proc, checker);
        switch( sign_lit.getSign() )
        {
        case PLUS:
        case MINUS:
            if( type.isUnknown() )
            {
            	getHead().retype(RegularType.INTEGER);
            }
            if( !type.equals(RegularType.INTEGER) )
            {
                checker.addErrorMessage(
                    proc, this, "incompatible type: " + type + " type cannot have sign. must be INTEGER."
                );
            }
            return type;
        case NONE:
            return type;
        default:
            assert false;
            return null;
        }
    }
    
    @Override
    public void compile(StringBuilder codebuilder, Procedure proc, LabelGenerator l_gen)
    {
        getHead().compile(codebuilder, proc, l_gen);

        switch( sign_lit.getSign() )
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
    }

    @Override
    public String toString()
    {
        return "";
    }

    @Override
    public void printBodyln(String indent)
    {
        sign_lit.println(indent + " |");
        term.println(indent + "  ");
    }
}


