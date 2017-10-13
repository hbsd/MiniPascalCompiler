package enshud.s3.checker.ast;

import java.util.Objects;

import enshud.s3.checker.Checker;
import enshud.s3.checker.Procedure;
import enshud.s3.checker.type.IType;
import enshud.s3.checker.type.RegularType;

public class TailedTerm extends Term
{
    final MultiplyOperator op;
    final Term             tail;

    public TailedTerm(IFactor factor, MultiplyOperator op, Term tail)
    {
        super(factor);
        this.op = Objects.requireNonNull(op);
        this.tail = Objects.requireNonNull(tail);
        switch( getOp() )
        {
        case MUL:
        case DIV:
        case MOD:
            type = RegularType.INTEGER;
            break;
        case AND:
            type = RegularType.BOOLEAN;
            break;
        default:
            assert false;
        }
    }

    public IFactor getHead()
    {
        return factor;
    }

    public MultiplyOperator getOp()
    {
        return op;
    }

    public Term getTail()
    {
        return tail;
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
        factor.retype(new_type);
        tail.retype(new_type);
    }

    @Override
    public IType check(Procedure proc, Checker checker)
    {
        IType head_type = getHead().check(proc, checker);
        IType tail_type = getTail().check(proc, checker);

        if( head_type.isUnknown() && !tail_type.isUnknown() )
        {
            getHead().retype(tail_type);
            head_type = tail_type;
        }
        else if( !head_type.isUnknown() && tail_type.isUnknown() )
        {
            getTail().retype(head_type);
            tail_type = head_type;
        }
        else if( head_type.isUnknown() && tail_type.isUnknown() )
        {
            return type;
        }

        if( type != head_type )
        {
            checker.addErrorMessage(
                proc, getHead(),
                "incompatible type: cannot use " + head_type + " type as right operand of " + getOp() + " operator. must be " + type
            );
        }
        if( !type.equals(tail_type) )
        {
            checker.addErrorMessage(
                proc, getTail(),
                "incompatible type: cannot use " + tail_type + " type as left operand of " + getOp() + " operator. must be " + type
            );
        }

        return type;
    }
    
    /*@Override
    public void compile(StringBuilder codebuilder, Procedure proc, LabelGenerator l_gen)
    {
        getTail().compile(codebuilder, proc, l_gen);
        codebuilder.append(" PUSH 0,GR2").append(System.lineSeparator());
        getHead().compile(codebuilder, proc, l_gen);

        switch( getOp() )
        {
        case MUL:
            codebuilder.append(" POP GR1").append(System.lineSeparator());
            codebuilder.append(" CALL MULT").append(System.lineSeparator());
            break;
        case DIV:
            codebuilder.append(" POP GR1").append(System.lineSeparator());
            codebuilder.append(" CALL DIV").append(System.lineSeparator());
            break;
        case MOD:
            codebuilder.append(" POP GR1").append(System.lineSeparator());
            codebuilder.append(" CALL DIV").append(System.lineSeparator());
            codebuilder.append(" LD GR2,GR1").append(System.lineSeparator());
            break;
        case AND:
            codebuilder.append(" POP GR1").append(System.lineSeparator());
            codebuilder.append(" AND GR2,GR1").append(System.lineSeparator());
            break;
        default:
            assert false;
        }
    }*/

    @Override
    public void printBodyln(String indent)
    {
        factor.println(indent + " |");
        op.println(indent + " |");
        tail.println(indent + "  ");
    }
}
