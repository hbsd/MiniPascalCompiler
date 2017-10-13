package enshud.s3.checker.ast;

import java.util.Objects;

import enshud.s3.checker.Checker;
import enshud.s3.checker.Procedure;
import enshud.s3.checker.type.IType;
import enshud.s3.checker.type.UnknownType;


public class SimpleExpression implements ISimpleExpression
{
    final ITerm       term;
    IType            type;

    public SimpleExpression(ITerm term)
    {
        this.term = Objects.requireNonNull(term);
        type = UnknownType.UNKNOWN;
    }

    public SimpleExpression(SimpleExpression exp)
    {
        this(exp.term);
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
        if( getType().isUnknown() )
        {
            type = new_type;
        }
        term.retype(new_type);
    }

    @Override
    public IType check(Procedure proc, Checker checker)
    {
        type = getHead().check(proc, checker);
        return type;
    }
    
    /*@Override
    public void compile(StringBuilder codebuilder, Procedure proc, LabelGenerator l_gen)
    {
        getHead().compile(codebuilder, proc, l_gen);
    }*/

    @Override
    public String toString()
    {
        return "";
    }

    @Override
    public void printBodyln(String indent)
    {
        term.println(indent + "  ");
    }
}


