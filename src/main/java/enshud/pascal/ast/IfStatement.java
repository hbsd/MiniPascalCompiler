package enshud.pascal.ast;

import java.util.List;
import java.util.Objects;

import enshud.pascal.type.IType;
import enshud.pascal.type.BasicType;
import enshud.s3.checker.Checker;
import enshud.s3.checker.Procedure;
import enshud.s4.compiler.Casl2Instruction;
import enshud.s4.compiler.LabelGenerator;


public class IfStatement implements IStatement
{
    private final ITyped    cond;
    private final CompoundStatement then_statements;
    
    public IfStatement(ITyped cond, CompoundStatement then_statements)
    {
        this.cond = Objects.requireNonNull(cond);
        this.then_statements = Objects.requireNonNull(then_statements);
    }
    
    public ITyped getCond()
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
    public void compile(List<Casl2Instruction> code, Procedure proc, LabelGenerator l_gen)
    {
        final int label = l_gen.next();
        
        getCond().compile(code, proc, l_gen);
        
        code.add(new Casl2Instruction("LD", "", "; set ZF", "GR2", "GR2"));
        code.add(new Casl2Instruction("JZE", "", "; branch of IF", "F" + label));
        
        getThen().compile(code, proc, l_gen);

        code.add(new Casl2Instruction("NOP", "F" + label, "; end of IF"));
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

