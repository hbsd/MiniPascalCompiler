package enshud.pascal.ast.statement;

import java.util.Objects;

import enshud.pascal.type.IType;
import enshud.pascal.Procedure;
import enshud.pascal.ast.expression.BooleanLiteral;
import enshud.pascal.ast.expression.IConstant;
import enshud.pascal.ast.expression.IExpression;
import enshud.pascal.type.BasicType;
import enshud.s3.checker.Checker;
import enshud.s4.compiler.Casl2Code;
import enshud.s4.compiler.LabelGenerator;


public class IfStatement implements IStatement
{
    private final IExpression    cond;
    private final CompoundStatement then_statements;
    
    public IfStatement(IExpression cond, CompoundStatement then_statements)
    {
        this.cond = Objects.requireNonNull(cond);
        this.then_statements = Objects.requireNonNull(then_statements);
    }
    
    public IExpression getCond()
    {
        return cond;
    }
    
    public CompoundStatement getThen()
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
    public IStatement precompute(Procedure proc)
    {
        IConstant res = cond.preeval(proc);
        
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
    public void compile(Casl2Code code, Procedure proc, LabelGenerator l_gen)
    {
        final int label = l_gen.next();
        
        getCond().compile(code, proc, l_gen);
        
        code.add("LD", "", "; set ZF", "GR2", "GR2");
        code.add("JZE", "", "; branch of IF", "F" + label);
        
        getThen().compile(code, proc, l_gen);

        code.add("NOP", "F" + label, "; end of IF");
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

