package enshud.pascal.ast.statement;

import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

import enshud.pascal.Procedure;
import enshud.pascal.ast.IVisitor;
import enshud.pascal.ast.Identifier;
import enshud.pascal.ast.NodeList;
import enshud.pascal.ast.expression.IConstant;
import enshud.pascal.ast.expression.IExpression;
import enshud.pascal.type.IType;
import enshud.s3.checker.Checker;
import enshud.s4.compiler.Casl2Code;
import enshud.s4.compiler.LabelGenerator;


public class ProcCallStatement implements IStatement
{
    private final Identifier            name;
    private final NodeList<IExpression> args;
    
    private Procedure                   called_proc;
    
    public ProcCallStatement(Identifier name, NodeList<IExpression> args)
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
        return args;
    }
    
    public Procedure getCalledProc()
    {
        return called_proc;
    }
    
    public void setCalledProc(Procedure called_proc)
    {
        this.called_proc = called_proc;
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
    public <T, U> T accept(IVisitor<T, U> visitor, U option)
    {
        return visitor.visitProcCallStatement(this, option);
    }
    
    @Override
    public IType check(Procedure proc, Checker checker)
    {
        this.called_proc = proc.getSubProc(getName().toString())
            .orElse(null);
        
        if (getCalledProc() == null)
        {
            checkWhenNotFound(proc, checker);
        }
        else if (getArgs().size() != getCalledProc().getParamLength())
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
        for (final IExpression exp: getArgs())
        {
            final IType ptype = getCalledProc().getParamType(i);
            final IType atype = exp.check(proc, checker);
            
            if (!ptype.equals(atype))
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
        final String msg2 = getCalledProc().getParamLength() == 0? "no": "" + getCalledProc().getParamLength();
        
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
    public void compile(Casl2Code code, Procedure proc, LabelGenerator l_gen)
    {
        compileArguments(code, proc, l_gen);
        compileStaticFramePointer(code, proc, l_gen);
        
        //f(code, proc.getName().charAt(0), '[');
        
        code.add("CALL", "", "; proc " + getCalledProc().getQualifiedName(), getCalledProc().getId());
        
        //f(code, proc.getName().charAt(0), ']');
        
        // remove rest child frame
        code.addAddlImm("GR8", args.size() + 1);
    }
    
    private void compileArguments(Casl2Code code, Procedure proc, LabelGenerator l_gen)
    {
        for (int i = getArgs().size() - 1; i >= 0; --i)
        {
            final IExpression e = getArgs().get(i);
            if (e instanceof IConstant)
            {
                code.add("PUSH", "", "", "" + ((IConstant)e).getInt());
            }
            else
            {
                getArgs().get(i).compile(code, proc, l_gen);
                code.add("PUSH", "", "", "0", "GR2");
            }
        }
    }
    
    private void compileStaticFramePointer(Casl2Code code, Procedure proc, LabelGenerator l_gen)
    {
        final int my_depth = proc.getDepth();
        final int your_depth = getCalledProc().getDepth();
        if (my_depth + 1 == your_depth) // child
        {
            code.add("PUSH", "", "", "0", "GR5");
        }
        else if (my_depth >= your_depth) // ancestor or recursive call
        {
            loadStaticLink(code, "GR2", my_depth - your_depth);
            
            code.add("PUSH", "", "", "0", "GR2");
        }
        else
        {
            new Exception(proc.getQualifiedName() + " cannot call " + called_proc.getQualifiedName());
        }
    }
    
    /// go back stack frame by static link
    /// diff == 0? my static
    public static void loadStaticLink(Casl2Code code, String gr, int diff)
    {
        code.add("LD", "", "", gr, "1", "GR5");
        IntStream.range(0, diff)
            .forEach(i -> code.add("LD", "", "", gr, "1", gr));
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

