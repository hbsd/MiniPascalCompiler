package enshud.pascal.ast;

import java.util.List;
import java.util.Objects;

import enshud.pascal.type.IType;
import enshud.pascal.type.StringType;
import enshud.s3.checker.Checker;
import enshud.s3.checker.Procedure;
import enshud.s4.compiler.Casl2Instruction;
import enshud.s4.compiler.LabelGenerator;


public class ProcCallStatement implements IStatement
{
    private final Identifier     name;
    private final NodeList<ITyped> args;

    private Procedure proc;

    public ProcCallStatement(Identifier name, NodeList<ITyped> args)
    {
        this.name = Objects.requireNonNull(name);
        this.args = Objects.requireNonNull(args);
    }
    
    public Identifier getName()
    {
        return name;
    }
    
    public List<ITyped> getArgs()
    {
        return args;
    }
    
    public Procedure getProc()
    {
        return proc;
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
        this.proc = proc.getSubProc(getName().toString())
                .orElse(null);
        
        if (getProc() == null)
        {
            checkWhenNotFound(proc, checker);
        }
        else if (getArgs().size() != getProc().getParamLength())
        {
            checkWhenInvalidLength(proc, checker);
        }
        else
        {
            checkArgumentTypes(proc, checker);
        }
        return null;
    }
    
    private void checkWhenNotFound(Procedure proc, Checker checker)
    {
        List<Procedure> p = proc.getSubProcFuzzy(getName().toString());
        checker.addErrorMessage(
            proc, getName(),
            "procedure '" + name + "' is not defined."
                    + (p.isEmpty()? "": (" did you mean procedure " + p + "?"))
        );
        
        getArgs().forEach(exp -> exp.check(proc, checker));
    }
    
    private void checkArgumentTypes(Procedure proc, Checker checker)
    {
        int i = 0;
        for (final ITyped exp: getArgs())
        {
            final IType ptype = getProc().getParamType(i);
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
    
    private void checkWhenInvalidLength(Procedure proc, Checker checker)
    {
        final String msg1 = getArgs().size() == 0? "no": "" + getArgs().size();
        final String msg2 = getProc().getParamLength() == 0? "no": "" + getProc().getParamLength();
        
        checker.addErrorMessage(
            proc, this, "cannot call procedure '" + name + "' by " + msg1 + " arguments. must be " + msg2 + " args."
        );
        
        getArgs().forEach(exp -> exp.check(proc, checker));
    }
    
    @Override
    public IStatement precompute(Procedure proc)
    {
        getArgs().forEach(e -> e.preeval(proc));
        return this;
    }
    
    @Override
    public void compile(List<Casl2Instruction> code, Procedure proc, LabelGenerator l_gen)
    {
        // prepare argument
        for (int i = getArgs().size() - 1; i >= 0; --i)
        {
            final ITyped e = getArgs().get(i);
            e.compile(code, proc, l_gen);
            code.add(new Casl2Instruction("PUSH", "", "", "0", "GR2"));
        }
        
        code.add(new Casl2Instruction("CALL", "", "; proc " + getProc().getQualifiedName(), getProc().getId()));
        
        // remove arguments
        if (args.size() > 0)
        {
            code.add(new Casl2Instruction("LAD", "", "", "GR8", "" + args.size(), "GR8"));
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

