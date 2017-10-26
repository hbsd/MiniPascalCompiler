package enshud.pascal.ast;

import java.util.Objects;

import enshud.pascal.type.IType;
import enshud.pascal.type.UnknownType;
import enshud.s3.checker.Checker;
import enshud.s3.checker.Context;
import enshud.s3.checker.Procedure;
import enshud.s4.compiler.LabelGenerator;


public class Term implements ITerm
{
    IFactor factor;
    IType   type;
    
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
        type = new_type;
        factor.retype(new_type);
    }
    
    @Override
    public IType check(Procedure proc, Checker checker)
    {
        type = getHead().check(proc, checker);
        return type;
    }
    
    @Override
    public IConstant preeval(Procedure proc, Context context)
    {
        IConstant res = factor.preeval(proc, context);
        if (res != null)
        {
            factor = res;
        }
        return res;
    }
    
    @Override
    public void compile(StringBuilder codebuilder, Procedure proc, LabelGenerator l_gen)
    {
        getHead().compile(codebuilder, proc, l_gen);
    }
    
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

