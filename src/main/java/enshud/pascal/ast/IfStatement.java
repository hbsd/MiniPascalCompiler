package enshud.pascal.ast;

import java.util.Objects;

import enshud.pascal.type.IType;
import enshud.pascal.type.BasicType;
import enshud.s3.checker.Checker;
import enshud.s3.checker.Context;
import enshud.s3.checker.Procedure;
import enshud.s4.compiler.LabelGenerator;


public class IfStatement implements IStatement
{
    final ITyped    cond;
    final StatementList then_statements;
    
    public IfStatement(ITyped cond, StatementList then_statements)
    {
        this.cond = Objects.requireNonNull(cond);
        this.then_statements = Objects.requireNonNull(then_statements);
    }
    
    public ITyped getCond()
    {
        return cond;
    }
    
    public StatementList getThen()
    {
        return then_statements;
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
    public IType check(Procedure proc, Checker checker)
    {
        final IType type = getCond().check(proc, checker);
        if (type.isUnknown())
        {
            getCond().retype(BasicType.BOOLEAN);
        }
        if (!type.equals(BasicType.BOOLEAN) && !type.isUnknown())
        {
            checker.addErrorMessage(
                proc, getCond(),
                "incompatible type: cannot use " + type + " type as condition of if-statement. must be BOOLEAN."
            );
        }
        
        getThen().check(proc, checker);
        return null;
    }
    
    @Override
    public IStatement precompute(Procedure proc, Context context)
    {
        IConstant res = cond.preeval(proc, context);
        
        if (res == null)
        {
            return this;
        }
        if (((BooleanLiteral)res).getBool())
        {
            return then_statements;
        }
        else
        {
            return null;
        }
    }
    
    @Override
    public void compile(StringBuilder codebuilder, Procedure proc, LabelGenerator l_gen)
    {
        final String label = l_gen.toString();
        l_gen.next();
        
        cond.compile(codebuilder, proc, l_gen);
        
        codebuilder.append(" LD GR2,GR2").append(System.lineSeparator());
        codebuilder.append(" JZE F").append(label).append("; branch of IF").append(System.lineSeparator());
        
        then_statements.compile(codebuilder, proc, l_gen);
        
        codebuilder.append("F").append(label).append(" NOP; end of IF").append(System.lineSeparator());
    }
    
    @Override
    public String toString()
    {
        return "Then only";
    }
    
    @Override
    public void printBodyln(String indent)
    {
        cond.println(indent + " |", "Condition of IfElse");
        then_statements.println(indent + "  ", "Then of IfElse");
    }
}

