package enshud.pascal.ast;

import java.util.List;
import java.util.Objects;

import enshud.pascal.type.IType;
import enshud.s3.checker.Checker;
import enshud.s3.checker.Procedure;
import enshud.s4.compiler.Casl2Instruction;
import enshud.s4.compiler.LabelGenerator;


public class IfElseStatement extends IfStatement
{
    private CompoundStatement else_statements;
    
    public IfElseStatement(ITyped cond, CompoundStatement then_statements, CompoundStatement else_statements)
    {
        super(cond, then_statements);
        this.else_statements = Objects.requireNonNull(else_statements);
    }
    
    public CompoundStatement getElse()
    {
        return else_statements;
    }
    
    @Override
    public IType check(Procedure proc, Checker checker)
    {
        super.check(proc, checker);
        getElse().check(proc, checker);
        return null;
    }
    
    @Override
    public IStatement precompute(Procedure proc)
    {
        IStatement sup = super.precompute(proc);
        return (sup != null)? sup: getElse();
    }
    
    @Override
    public void compile(List<Casl2Instruction> code, Procedure proc, LabelGenerator l_gen)
    {
        final int label = l_gen.next();
        
        getCond().compile(code, proc, l_gen);
        
        code.add(new Casl2Instruction("LD", "", "; set ZF", "GR2", "GR2"));
        code.add(new Casl2Instruction("JZE", "", "; branch of IF", "E" + label));
        
        getThen().compile(code, proc, l_gen);
        code.add(new Casl2Instruction("JUMP", "", "", "F" + label));
        
        code.add(new Casl2Instruction("NOP", "E" + label, "; else of IF"));
        getElse().compile(code, proc, l_gen);
        
        code.add(new Casl2Instruction("NOP", "F" + label, "; end of IF"));
    }
    
    @Override
    public String toString()
    {
        return "Then & Else";
    }
    
    @Override
    public void printBodyln(String indent)
    {
        super.printBodyln(indent);
        else_statements.println(indent + "  ", "Else of IfElse");
    }
}
