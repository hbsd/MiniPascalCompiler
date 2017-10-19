package enshud.pascal.ast;

import java.util.Objects;

import enshud.pascal.type.IType;
import enshud.pascal.type.RegularType;
import enshud.pascal.type.UnknownType;
import enshud.s3.checker.Checker;
import enshud.s3.checker.Procedure;
import enshud.s4.compiler.LabelGenerator;

public class TailedSimpleExpression extends SimpleExpression
{
    final AddOperator      op;
    final SimpleExpression tail;

    public TailedSimpleExpression(Term term, AddOperator op, SimpleExpression tail)
    {
        super(term);
        this.op = Objects.requireNonNull(op);
        this.tail = Objects.requireNonNull(tail);
        switch( getOp() )
        {
        case ADD:
        case SUB:
            type = RegularType.INTEGER;
            return;
        case OR:
            type = RegularType.BOOLEAN;
            return;
        default:
            assert false;
        }
    }

    public AddOperator getOp()
    {
        return op;
    }

    public SimpleExpression getTail()
    {
        return tail;
    }

    @Override
    public void retype(IType new_type)
    {
        super.retype(new_type);
        tail.retype(new_type);
    }

    @Override
    public IType check(Procedure proc, Checker checker)
    {
        if( type == UnknownType.UNKNOWN )
            throw new Error("aaa");

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

        if( !type.equals(head_type) )
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
    
    @Override
    public void compile(StringBuilder codebuilder, Procedure proc, LabelGenerator l_gen)
    {
        getTail().compile(codebuilder, proc, l_gen);
        codebuilder.append(" PUSH 0,GR2").append(System.lineSeparator());

        getHead().compile(codebuilder, proc, l_gen);
        codebuilder.append(" POP GR1").append(System.lineSeparator());

        switch( getOp() )
        {
        case ADD:
            codebuilder.append(" ADDA GR2,GR1").append(System.lineSeparator());
            break;
        case SUB:
            codebuilder.append(" SUBA GR1,GR2").append(System.lineSeparator());
            codebuilder.append(" LD GR2,GR1").append(System.lineSeparator());
            break;
        case OR:
            codebuilder.append(" OR GR2,GR1").append(System.lineSeparator());
            break;
        default:
            assert false;
        }
    }

    @Override
    public void printBodyln(String indent)
    {
        term.println(indent + " |");
        op.println(indent + " |");
        tail.println(indent + "  ");
    }
}
