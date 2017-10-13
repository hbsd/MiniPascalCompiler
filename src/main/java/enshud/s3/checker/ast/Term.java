package enshud.s3.checker.ast;

import java.util.Objects;

import enshud.s3.checker.Checker;
import enshud.s3.checker.Procedure;
import enshud.s3.checker.type.IType;
import enshud.s3.checker.type.UnknownType;


public class Term implements ITerm
{
    final IFactor    factor;
    IType            type;

    public Term(IFactor factor)
    {
        this.factor = Objects.requireNonNull(factor);
        type = UnknownType.UNKNOWN;
    }

    public IFactor getHead()
    {
        return factor;
    }

    @Override
    public IType getType()
    {
        return type;
    }

    @Override
    public int getLine()
    {
        return factor.getLine();
    }

    @Override
    public int getColumn()
    {
        return factor.getColumn();
    }

    @Override
    public void retype(IType new_type)
    {
        if( getType().isUnknown() )
        {
            type = new_type;
        }
        factor.retype(new_type);
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
        factor.println(indent + "  ");
    }
}


