package enshud.pascal.ast;

import java.util.List;
import java.util.Objects;

import enshud.pascal.type.IType;
import enshud.pascal.type.BasicType;
import enshud.s3.checker.Checker;
import enshud.s3.checker.Procedure;
import enshud.s4.compiler.Casl2Instruction;
import enshud.s4.compiler.LabelGenerator;


public class WhileStatement implements IStatement
{
    private final ITyped     cond;
    private final IStatement statement;
    private boolean          infinite_loop = false;
    
    public WhileStatement(ITyped cond, IStatement statement)
    {
        this.cond = Objects.requireNonNull(cond);
        this.statement = Objects.requireNonNull(statement);
    }
    
    public ITyped getCond()
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
    public void compile(List<Casl2Instruction> code, Procedure proc, LabelGenerator l_gen)
    {
        final int label = l_gen.next();
        
        code.add(new Casl2Instruction("NOP", "C" + label, "; start of WHILE"));
        
        if (!infinite_loop)
        {
            cond.compile(code, proc, l_gen);

            code.add(new Casl2Instruction("LD", "",                   "", "GR2", "GR2"));
            code.add(new Casl2Instruction("JZE", "", "; branch of WHILE", "F" + label));
        }
        
        statement.compile(code, proc, l_gen);
        code.add(new Casl2Instruction("JUMP", "", "", "C" + label));
        
        if (!infinite_loop)
        {
            code.add(new Casl2Instruction("NOP", "F" + label, "; end of WHILE"));
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

