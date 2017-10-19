package enshud.pascal.ast;

import java.util.Objects;

import enshud.pascal.type.IType;
import enshud.pascal.type.UnknownType;
import enshud.s3.checker.Checker;
import enshud.s3.checker.Procedure;
import enshud.s4.compiler.LabelGenerator;


public class Expression implements IExpression
{
    final SimpleExpression left;
    IType                  type;

    public Expression(SimpleExpression left)
    {
        this.left = Objects.requireNonNull(left);
        this.type = UnknownType.UNKNOWN;
    }

    public SimpleExpression getLeft()
    {
        return left;
    }

    @Override
    public IType getType()
    {
        return type;
    }

    @Override
    public int getLine()
    {
        return left.getLine();
    }

    @Override
    public int getColumn()
    {
        return left.getColumn();
    }

    @Override
    public void retype(IType new_type)
    {
    	type = new_type;
        left.retype(new_type);
    }

    @Override
    public IType check(Procedure proc, Checker checker)
    {
        type = getLeft().check(proc, checker);
        return type;
    }
    
    @Override
    public void compile(StringBuilder codebuilder, Procedure proc, LabelGenerator l_gen)
    {
        getLeft().compile(codebuilder, proc, l_gen);
    }

    @Override
    public String toString()
    {
        return "";
    }

    @Override
    public void printBodyln(String indent)
    {
        left.println(indent + "  ");
    }
}


