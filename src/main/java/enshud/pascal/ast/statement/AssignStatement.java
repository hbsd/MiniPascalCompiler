package enshud.pascal.ast.statement;

import java.util.Objects;

import enshud.pascal.type.IType;
import enshud.pascal.Procedure;
import enshud.pascal.ast.IVisitor;
import enshud.pascal.ast.expression.IConstant;
import enshud.pascal.ast.expression.IExpression;
import enshud.pascal.ast.expression.IVariable;
import enshud.pascal.type.BasicType;
import enshud.pascal.type.StringType;
import enshud.s3.checker.Checker;
import enshud.s4.compiler.Casl2Code;
import enshud.s4.compiler.LabelGenerator;


public class AssignStatement implements IStatement
{
    private final IVariable left;
    private IExpression          right;
    
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
    
    public void setRight(IExpression right)
    {
        this.right = right;
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
    public <T, U> T accept(IVisitor<T, U> visitor, U option)
    {
        return visitor.visitAssignStatement(this, option);
    }
    
    @Override
    public IType check(Procedure proc, Checker checker)
    {
        IType left_type = getLeft().check(proc, checker);
        IType right_type = getRight().check(proc, checker);
        
        if (left_type.isUnknown() && !right_type.isUnknown())
        {
            left_type = right_type;
        }
        else if (!left_type.isUnknown() && right_type.isUnknown())
        {
            right_type = left_type;
        }
        else
        {
            if (left_type == StringType.CHAR && right_type == BasicType.CHAR)
            {
                left_type = right_type;
            }
            if (right_type == StringType.CHAR && left_type == BasicType.CHAR)
            {
                right_type = left_type;
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
    public IStatement precompute(Procedure proc)
    {
        IConstant res = right.preeval(proc);
        if (res != null)
        {
            right = res;
        }
        return this;
    }
    
    @Override
    public void compile(Casl2Code code, Procedure proc, LabelGenerator l_gen)
    {
        getLeft().compileForAddr(code, proc, l_gen);
        if(getRight() instanceof IConstant)
        {
            code.add("LD", "", "", "GR1", "GR2");
            code.addLoadImm("GR2", ((IConstant)getRight()).getInt());
        }
        else
        {
            code.add("PUSH", "", "", "0", "GR2");
            getRight().compile(code, proc, l_gen);
            code.add("POP", "", "", "GR1");
        }
        code.add("ST", "", "", "GR2", "0", "GR1");
        
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

