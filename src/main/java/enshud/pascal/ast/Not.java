package enshud.pascal.ast;

import java.util.Objects;

import enshud.s3.checker.Checker;
import enshud.s3.checker.Context;
import enshud.pascal.type.IType;
import enshud.pascal.type.BasicType;
import enshud.s1.lexer.LexedToken;
import enshud.s3.checker.Procedure;
import enshud.s4.compiler.LabelGenerator;


public class Not implements IFactor
{
    IFactor          factor;
    final LexedToken not_token;
    
    public Not(IFactor factor, LexedToken not_token)
    {
        this.factor = Objects.requireNonNull(factor);
        this.not_token = Objects.requireNonNull(not_token);
    }
    
    public IFactor getFactor()
    {
        return factor;
    }
    
    @Override
    public String toString()
    {
        return "";
    }
    
    @Override
    public IType getType()
    {
        return BasicType.BOOLEAN;
    }
    
    @Override
    public int getLine()
    {
        return not_token.getLine();
    }
    
    @Override
    public int getColumn()
    {
        return not_token.getColumn();
    }
    
    @Override
    public void retype(IType new_type)
    {
        factor.retype(new_type);
    }
    
    @Override
    public IType check(Procedure proc, Checker checker)
    {
        final IType t = getFactor().check(proc, checker);
        if (t.isUnknown())
        {
            getFactor().retype(BasicType.BOOLEAN);
        }
        if (!getType().equals(t))
        {
            checker.addErrorMessage(
                proc, this, "incompatible type: cannot use " + t + " type as operand of NOT operator. must be BOOLEAN"
            );
        }
        return getType();
    }
    
    @Override
    public IConstant preeval(Procedure proc, Context context)
    {
        IConstant res = factor.preeval(proc, context);
        if (res == null)
        {
            return null;
        }
        
        factor = res;
        
        BooleanLiteral bl = (BooleanLiteral)factor;
        bl.val = bl.val.not();
        return bl;
    }
    
    @Override
    public void compile(StringBuilder codebuilder, Procedure proc, LabelGenerator l_gen)
    {
        factor.compile(codebuilder, proc, l_gen);
        codebuilder.append(" XOR GR2,=1").append(System.lineSeparator());
    }
    
    @Override
    public void printBodyln(String indent)
    {
        factor.println(indent + "  ");
    }
}

