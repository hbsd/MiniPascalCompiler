package enshud.pascal.ast.statement;

import java.util.List;
import java.util.Objects;

import enshud.pascal.type.IType;
import enshud.pascal.Procedure;
import enshud.pascal.ast.IVisitor;
import enshud.pascal.ast.NodeList;
import enshud.pascal.ast.expression.IVariable;
import enshud.pascal.type.BasicType;
import enshud.s3.checker.Checker;
import enshud.s4.compiler.Casl2Code;
import enshud.s4.compiler.LabelGenerator;


public class ReadStatement implements IStatement
{
    private final NodeList<IVariable> vars;
    
    public ReadStatement(NodeList<IVariable> vars)
    {
        this.vars = Objects.requireNonNull(vars);
    }
    
    public List<IVariable> getVariables()
    {
        return vars;
    }
    
    @Override
    public int getLine()
    {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public int getColumn()
    {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public <T, U> T accept(IVisitor<T, U> visitor, U option)
    {
        return visitor.visitReadStatement(this, option);
    }
    
    @Override
    public IType check(Procedure proc, Checker checker)
    {
        int i = 1;
        for (final IVariable var: getVariables())
        {
            final IType type = var.check(proc, checker);
            
            if (type.isUnknown())
            {
                checker.addErrorMessage(
                    proc, var, "cannot identify the type of " + Checker.getOrderString(i) + " argument of readln."
                );
            }
            else if (type != BasicType.INTEGER && type != BasicType.CHAR && !type.isArrayOf(BasicType.CHAR))
            {
                checker.addErrorMessage(
                    proc, var, "incompatible type: " + Checker.getOrderString(i)
                            + " argument of readln must be INTEGER, CHAR, or array of CHAR, but is " + type
                );
            }
            ++i;
        }
        return null;
    }
    
    @Override
    public IStatement precompute(Procedure proc)
    {
        vars.forEach(v -> v.preeval(proc));
        return this;
    }
    
    @Override
    public void compile(Casl2Code code, Procedure proc, LabelGenerator l_gen)
    {
        if (getVariables().isEmpty())
        {
            code.add("CALL", "", "", "RDLN");
            return;
        }
        
        for (final IVariable v: getVariables())
        {
            v.compileForAddr(code, proc, l_gen);
            if (v.getType() == BasicType.CHAR)
            {
                code.add("CALL", "", "", "RDCH");
            }
            else if (v.getType() == BasicType.INTEGER)
            {
                code.add("CALL", "", "", "RDINT");
            }
            else if (v.getType().isArrayOf(BasicType.CHAR))
            {
                code.add("CALL", "", "", "RDSTR");
            }
            else
            {
                assert false: "type error";
            }
        }
    }
    
    @Override
    public String toString()
    {
        return "";
    }
    
    @Override
    public void printBodyln(String indent)
    {
        vars.println(indent + "  ");
    }
}

