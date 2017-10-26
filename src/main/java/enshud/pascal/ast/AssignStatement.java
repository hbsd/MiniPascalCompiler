package enshud.pascal.ast;

import java.util.Objects;

import enshud.pascal.type.IType;
import enshud.pascal.type.BasicType;
import enshud.pascal.type.StringType;
import enshud.s3.checker.Checker;
import enshud.s3.checker.Context;
import enshud.s3.checker.Procedure;
import enshud.s4.compiler.LabelGenerator;


public class AssignStatement implements IBasicStatement
{
    final IVariable left;
    IExpression     right;
    
    public AssignStatement(IVariable left, IExpression right)
    {
        this.left = Objects.requireNonNull(left);
        this.right = Objects.requireNonNull(right);
    }
    
    public IVariable getLeft()
    {
        return left;
    }
    
    public IExpression getRight()
    {
        return right;
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
    public IType check(Procedure proc, Checker checker)
    {
        IType left_type = getLeft().check(proc, checker);
        IType right_type = getRight().check(proc, checker);
        
        if (left_type.isUnknown() && !right_type.isUnknown())
        {
            left_type = right_type;
            getLeft().retype(right_type);
        }
        else if (!left_type.isUnknown() && right_type.isUnknown())
        {
            right_type = left_type;
            getRight().retype(left_type);
        }
        else
        {
            if (left_type instanceof StringType && right_type == BasicType.CHAR)
            {
                left_type = right_type;
                getLeft().retype(right_type);
            }
            if (right_type instanceof StringType && left_type == BasicType.CHAR)
            {
                right_type = left_type;
                getRight().retype(left_type);
            }
        }
        
        final String err_msgl = "cannot assign to array type variable '" + getLeft().getName()
                + "' used as pure variable.";
        final String err_msgr = "incompatible type: cannot assign array type: " + right_type + ".";
        
        if (left_type.isBasicType())
        {
            if (right_type.isBasicType())
            {
                if (!left_type.equals(right_type))
                {
                    checker.addErrorMessage(
                        proc, this,
                        "incompatible type: cannot assign " + right_type + " type to " + left_type + " type."
                    );
                }
            }
            else
            {
                checker.addErrorMessage(proc, getRight(), err_msgr);
            }
        }
        else
        {
            checker.addErrorMessage(proc, getLeft(), err_msgl);
            if (!right_type.isBasicType())
            {
                checker.addErrorMessage(proc, getRight(), err_msgr);
            }
        }
        return null;
    }
    
    @Override
    public IStatement precompute(Procedure proc, Context context)
    {
        IConstant res = right.preeval(proc, context);
        if (res != null)
        {
            right = new Expression(new SimpleExpression(new Term(res)));
        }
        return this;
    }
    
    @Override
    public void compile(StringBuilder codebuilder, Procedure proc, LabelGenerator l_gen)
    {
        getRight().compile(codebuilder, proc, l_gen);
        codebuilder.append(" PUSH 0,GR2").append(System.lineSeparator());
        
        getLeft().compileForAddr(codebuilder, proc, l_gen);
        codebuilder.append(" POP GR1").append(System.lineSeparator());
        codebuilder.append(" ST GR1,0,GR2").append(System.lineSeparator());
    }
    
    @Override
    public String toString()
    {
        return "";
    }
    
    @Override
    public void printBodyln(String indent)
    {
        left.println(indent + " |");
        right.println(indent + "  ");
    }
}

