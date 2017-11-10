package enshud.pascal.ast;

import java.util.Objects;

import enshud.pascal.type.IType;
import enshud.pascal.type.BasicType;
import enshud.s3.checker.Checker;
import enshud.s3.checker.Context;
import enshud.s3.checker.Procedure;
import enshud.s4.compiler.LabelGenerator;


public class TailedTerm extends Term
{
    final MultiplyOperator op;
    ITerm                  tail;
    
    public TailedTerm(IFactor factor, MultiplyOperator op, ITerm tail)
    {
        super(factor);
        this.op = Objects.requireNonNull(op);
        this.tail = Objects.requireNonNull(tail);
        switch (getOp())
        {
        case MUL:
        case DIV:
        case MOD:
            type = BasicType.INTEGER;
            break;
        case AND:
            type = BasicType.BOOLEAN;
            break;
        default:
            assert false;
        }
    }
    
    @Override
    public IFactor getHead()
    {
        return factor;
    }
    
    public MultiplyOperator getOp()
    {
        return op;
    }
    
    public ITerm getTail()
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
        if (getType().isUnknown())
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
        
        if (head_type.isUnknown() && !tail_type.isUnknown())
        {
            head_type = tail_type;
            getHead().retype(tail_type);
        }
        else if (!head_type.isUnknown() && tail_type.isUnknown())
        {
            tail_type = head_type;
            getTail().retype(head_type);
        }
        else if (head_type.isUnknown() && tail_type.isUnknown())
        {
            return type;
        }
        
        if (!type.equals(head_type))
        {
            checker.addErrorMessage(
                proc, getHead(),
                "incompatible type: cannot use " + head_type + " type as right operand of " + getOp()
                        + " operator. must be " + type
            );
        }
        
        if (!type.equals(tail_type))
        {
            checker.addErrorMessage(
                proc, getTail(),
                "incompatible type: cannot use " + tail_type + " type as left operand of " + getOp()
                        + " operator. must be " + type
            );
        }
        
        return type;
    }
    
    @Override
    public IConstant preeval(Procedure proc, Context context)
    {
        IConstant l = factor.preeval(proc, context);
        IConstant r = tail.preeval(proc, context);
        
        if (l != null)
        {
            factor = l;
        }
        if (r != null)
        {
            tail = new Term(r);
        }
        
        if (l == null || r == null)
        {
            return null;
        }
        else
        {
            switch (op)
            {
            case MUL: {
                int lv = ((IntegerLiteral)l).getInt();
                int rv = ((IntegerLiteral)r).getInt();
                return new IntegerLiteral(lv * rv);
            }
            case DIV: {
                int lv = ((IntegerLiteral)l).getInt();
                int rv = ((IntegerLiteral)r).getInt();
                return new IntegerLiteral(lv / rv);
            }
            case MOD: {
                int lv = ((IntegerLiteral)l).getInt();
                int rv = ((IntegerLiteral)r).getInt();
                return new IntegerLiteral(lv % rv);
            }
            case AND: {
                int lv = ((BooleanLiteral)l).val.getInt();
                int rv = ((BooleanLiteral)r).val.getInt();
                return new BooleanLiteral(lv == 1 && rv == 1);
            }
            default:
                assert false;
                return null;
            }
        }
    }
    
    @Override
    public void compile(StringBuilder codebuilder, Procedure proc, LabelGenerator l_gen)
    {
        getTail().compile(codebuilder, proc, l_gen);
        codebuilder.append(" PUSH 0,GR2").append(System.lineSeparator());
        
        getHead().compile(codebuilder, proc, l_gen);
        codebuilder.append(" POP GR1").append(System.lineSeparator());
        
        switch (getOp())
        {
        case MUL:
            codebuilder.append(" CALL MULT").append(System.lineSeparator());
            break;
        case DIV:
            codebuilder.append(" CALL DIV").append(System.lineSeparator());
            break;
        case MOD:
            codebuilder.append(" CALL DIV").append(System.lineSeparator());
            codebuilder.append(" LD GR2,GR1").append(System.lineSeparator());
            break;
        case AND:
            codebuilder.append(" AND GR2,GR1").append(System.lineSeparator());
            break;
        default:
            assert false;
        }
    }
    
    @Override
    public void printBodyln(String indent)
    {
        factor.println(indent + " |");
        op.println(indent + " |");
        tail.println(indent + "  ");
    }
}
