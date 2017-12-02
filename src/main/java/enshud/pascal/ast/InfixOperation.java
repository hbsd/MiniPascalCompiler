package enshud.pascal.ast;

import java.util.List;

import enshud.pascal.type.IType;
import enshud.pascal.type.StringType;
import enshud.s1.lexer.LexedToken;
import enshud.s2.parser.node.INode;
import enshud.s2.parser.node.basic.TokenNode;
import enshud.s3.checker.Checker;
import enshud.s3.checker.Procedure;
import enshud.s4.compiler.Casl2Instruction;
import enshud.s4.compiler.LabelGenerator;


public class InfixOperation implements ITyped
{
    private ITyped        left;
    private ITyped        right;
    private InfixOperator op;
    private LexedToken op_token;
    
    public InfixOperation(ITyped left, ITyped right, LexedToken op_token)
    {
        this.left = left;
        this.right = right;
        this.op_token = op_token;
        this.op = InfixOperator.getFromToken(op_token);
    }
    
    public InfixOperation(ITyped left, ITyped right, TokenNode op_token)
    {
        this(left, right, op_token.getToken());
    }
    
    public ITyped getLeft()
    {
        return left;
    }
    
    public ITyped getRight()
    {
        return right;
    }
    
    public InfixOperator getOp()
    {
        return op;
    }
    
    @Override
    public int getLine()
    {
        return getLeft().getLine();
    }
    
    @Override
    public int getColumn()
    {
        return getLeft().getColumn();
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
        else if (left_type.isUnknown() && right_type.isUnknown())
        {
            return getOp().getReturnType();
        }
        else
        {
            // determine string or char
            if (left_type instanceof StringType && StringType.isCharOrCharArray(right_type))
            {
                left_type = right_type;
                getLeft().retype(right_type);
            }
            if (right_type instanceof StringType && StringType.isCharOrCharArray(left_type))
            {
                right_type = left_type;
                getRight().retype(left_type);
            }
        }
        
        checkTypeConsistency(proc, checker, left_type, right_type);
        
        return getOp().getReturnType();
    }
    
    private void checkTypeConsistency(Procedure proc, Checker checker, IType left_type, IType right_type)
    {
        if (!right_type.equals(getOp().getRightType()))
        {
            checker.addErrorMessage(
                proc, getRight(),
                "incompatible type: cannot use " + right_type + " type as right operand of " + getOp()
                        + " operator. must be " + getOp().getRightType()
            );
        }
        
        if (!left_type.equals(getOp().getLeftType()))
        {
            checker.addErrorMessage(
                proc, getLeft(),
                "incompatible type: cannot use " + left_type + " type as left operand of " + getOp()
                        + " operator. must be " + getOp().getLeftType()
            );
        }
        
        if (!left_type.equals(right_type))
        {
            checker.addErrorMessage(
                proc, op_token,
                "incompatible type: in compare operation, left is " + left_type + ". right is " + right_type + "."
            );
        }
    }
    
    @Override
    public IConstant preeval(Procedure proc)
    {
        IConstant l = left.preeval(proc);
        if (l != null)
        {
            left = l;
        }
        
        IConstant r = right.preeval(proc);
        if (r != null)
        {
            right = r;
        }
        
        if (l == null || r == null)
        {
            return null;
        }
        else
        {
            return getOp().eval(l.getInt(), r.getInt());
        }
    }
    
    @Override
    public void compile(List<Casl2Instruction> code, Procedure proc, LabelGenerator l_gen)
    {
        getLeft().compile(code, proc, l_gen);
        code.add(new Casl2Instruction("PUSH", "", "", "0", "GR2"));

        getRight().compile(code, proc, l_gen);
        code.add(new Casl2Instruction("POP", "", "", "GR1"));

        getOp().compile(code, l_gen);
    }
    
    @Override
    public IType getType()
    {
        return getOp().getReturnType();
    }
    
    @Override
    public void retype(IType new_type)
    {
        getLeft().retype(new_type);
        getRight().retype(new_type);
    }
    
    @Override
    public String toString()
    {
        return "";
    }
    
    @Override
    public void printBodyln(String indent)
    {
        getLeft().println(indent + " |");
        INode.printIndent(indent + " |");
        INode.printBlueln("-" + getOp().toString());
        getRight().println(indent + " |");
    }
}
