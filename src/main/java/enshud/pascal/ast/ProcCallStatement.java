package enshud.pascal.ast;

import java.util.List;
import java.util.Objects;

import enshud.pascal.type.IType;
import enshud.pascal.type.BasicType;
import enshud.pascal.type.StringType;
import enshud.s3.checker.Checker;
import enshud.s3.checker.Context;
import enshud.s3.checker.Procedure;
import enshud.s4.compiler.LabelGenerator;


public class ProcCallStatement implements IBasicStatement
{
    final Identifier     name;
    final ExpressionList args;
    
    public ProcCallStatement(Identifier name, ExpressionList args)
    {
        this.name = Objects.requireNonNull(name);
        this.args = Objects.requireNonNull(args);
    }
    
    public Identifier getName()
    {
        return name;
    }
    
    public List<IExpression> getArgs()
    {
        return args.getList();
    }
    
    @Override
    public int getLine()
    {
        return name.getLine();
    }
    
    @Override
    public int getColumn()
    {
        return name.getColumn();
    }
    
    @Override
    public IType check(Procedure proc, Checker checker)
    {
        final Procedure sub = proc.getSubProc(getName().toString());
        
        if (sub == null)
        {
            checkWhenNotFound(proc, checker);
        }
        else if (getArgs().size() != sub.getParamLength())
        {
            checkWhenInvalidLength(proc, checker);
        }
        else
        {
            checkArgumentTypes(proc, checker);
        }
        return null;
    }
    
    private void checkArgumentTypes(Procedure proc, Checker checker)
    {
        int i = 0;
        for (final IExpression exp: getArgs())
        {
            final BasicType ptype = proc.getSubProc(getName().toString()).getParamType(i);
            final IType atype = exp.check(proc, checker);
            
            if (atype instanceof StringType)
            {
                exp.retype(ptype);
            }
            else if (!ptype.equals(atype))
            {
                checker.addErrorMessage(
                    proc, this,
                    "incompatible type: cannot pass " + atype + " type to " + Checker.getOrderString(i + 1)
                            + " argument of procedure '" + name + "'. must be " + ptype + "."
                );
            }
            ++i;
        }
    }
    
    private void checkWhenNotFound(Procedure proc, Checker checker)
    {
        List<Procedure> p = proc.getSubProcFuzzy(getName().toString());
        checker.addErrorMessage(
            proc, getName(),
            "procedure '" + name + "' is not defined."
                    + (p.isEmpty()? "": (" did you mean procedure " + p + "?"))
        );
        
        for (final ITyped exp: getArgs())
        {
            exp.check(proc, checker);
        }
    }
    
    private void checkWhenInvalidLength(Procedure proc, Checker checker)
    {
        final String msg1 = getArgs().size() == 0? "no": "" + getArgs().size();
        
        Procedure sub = proc.getSubProc(getName().toString());
        final String msg2 = sub.getParamLength() == 0? "no": "" + sub.getParamLength();
        
        checker.addErrorMessage(
            proc, this, "cannot call procedure '" + name + "' by " + msg1 + " arguments. must be " + msg2 + " args."
        );
        for (final ITyped exp: getArgs())
        {
            exp.check(proc, checker);
        }
    }
    
    @Override
    public IStatement precompute(Procedure proc, Context context)
    {
        for (ITyped e: getArgs())
        {
            e.preeval(proc, context);
        }
        return this;
    }
    
    @Override
    public void compile(StringBuilder codebuilder, Procedure proc, LabelGenerator l_gen)
    {
        
        for (int i = getArgs().size() - 1; i >= 0; --i)
        {
            final IExpression e = getArgs().get(i);
            e.compile(codebuilder, proc, l_gen);
            codebuilder.append(" PUSH 0,GR2").append(System.lineSeparator());
        }
        codebuilder.append(" CALL PSUB").append(proc.getSubProcIndex(name.toString())).append(System.lineSeparator());
        if (args.size() > 0)
        {
            codebuilder.append(" LAD GR8,").append(args.size()).append(",GR8").append(System.lineSeparator());
        }
    }
    
    
    @Override
    public String toString()
    {
        return "" + name;
    }
    
    @Override
    public void printBodyln(String indent)
    {
        args.println(indent + "  ");
    }
}

