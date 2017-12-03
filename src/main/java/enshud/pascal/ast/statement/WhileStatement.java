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


public class WhileStatement implements IStatement
{
    private final IExpression     cond;
    private final IStatement statement;
    private boolean          infinite_loop = false;
    
    public WhileStatement(IExpression cond, IStatement statement)
    {
        this.cond = Objects.requireNonNull(cond);
        this.statement = Objects.requireNonNull(statement);
    }
    
    public IExpression getCond()
    {
        return cond;
    }
    
    public IStatement getStatement()
    {
        return statement;
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
                "incompatible type: cannot use " + type + " type as condition of while-statement. must be BOOLEAN."
            );
        }
        getStatement().check(proc, checker);
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
        else if (((BooleanLiteral)res).getBool())
        {
            infinite_loop = true;
            return this;
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
        
        code.add("NOP", "C" + label, "; start of WHILE");
        
        if (!infinite_loop)
        {
            cond.compile(code, proc, l_gen);

            code.add("LD", "",                   "", "GR2", "GR2");
            code.add("JZE", "", "; branch of WHILE", "F" + label);
        }
        
        statement.compile(code, proc, l_gen);
        code.add("JUMP", "", "", "C" + label);
        
        if (!infinite_loop)
        {
            code.add("NOP", "F" + label, "; end of WHILE");
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
        cond.println(indent + " |", "Condition of While");
        statement.println(indent + "  ", "Do of While");
    }
}

