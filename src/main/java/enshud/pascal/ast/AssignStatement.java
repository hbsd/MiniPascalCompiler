package enshud.pascal.ast;

import java.util.List;
import java.util.Objects;

import enshud.pascal.type.IType;
import enshud.pascal.type.BasicType;
import enshud.pascal.type.StringType;
import enshud.s3.checker.Checker;
import enshud.s3.checker.Procedure;
import enshud.s4.compiler.Casl2Instruction;
import enshud.s4.compiler.LabelGenerator;


public class AssignStatement implements IStatement
{
    private final IVariable left;
    private ITyped          right;
    
    public AssignStatement(IVariable left, ITyped right)
    {
        this.left = Objects.requireNonNull(left);
        this.right = Objects.requireNonNull(right);
    }
    
    public IVariable getLeft()
    {
        return left;
    }
    
    public ITyped getRight()
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
    public void compile(List<Casl2Instruction> code, Procedure proc, LabelGenerator l_gen)
    {
        getRight().compile(code, proc, l_gen);
        code.add(new Casl2Instruction("PUSH", "", "", "0", "GR2"));
        
        getLeft().compileForAddr(code, proc, l_gen);
        code.add(new Casl2Instruction("POP", "", "", "GR1"));
        code.add(new Casl2Instruction("ST", "", "", "GR1","0","GR2"));
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

