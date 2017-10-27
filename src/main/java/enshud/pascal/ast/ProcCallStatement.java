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
    
    public List<IExpression> getExpressions()
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
        final String name = getName().toString();
        final Procedure sub = proc.getSubProc(name);
        final List<IExpression> exps = getExpressions();
        
        if (sub == null)
        {
            List<Procedure> p = proc.getSubProcFuzzy(name);
            checker.addErrorMessage(
                proc, getName(),
                "procedure '" + name + "' is not defined."
              + (p.isEmpty()? "" : (" did you mean procedure " + p +  "?"))
            );

            for (final IExpression exp: exps)
            {
                exp.check(proc, checker);
            }
        }
        else if (exps.size() != sub.getParamLength())
        {
            final String msg1 = exps.size() == 0? "no": "" + exps.size();
            final String msg2 = sub.getParamLength() == 0? "no": "" + sub.getParamLength();
            
            checker.addErrorMessage(
                proc, this, "cannot call procedure '" + name + "' by " + msg1 + " arguments. must be " + msg2 + " args."
            );
            for (final IExpression exp: exps)
            {
                exp.check(proc, checker);
            }
        }
        else
        {
            int i = 0;
            for (final IExpression exp: exps)
            {
                final BasicType ptype = sub.getParamType(i);
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
        return null;
    }
    
    @Override
    public IStatement precompute(Procedure proc, Context context)
    {
        for(IExpression e: getExpressions())
        {
            e.preeval(proc, context);
        }
        return this;
    }
    
    @Override
    public void compile(StringBuilder codebuilder, Procedure proc, LabelGenerator l_gen)
    {
        
        for (int i = getExpressions().size() - 1; i >= 0; --i)
        {
            final IExpression e = getExpressions().get(i);
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

