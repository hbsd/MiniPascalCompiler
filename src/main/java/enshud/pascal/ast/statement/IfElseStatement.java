package enshud.pascal.ast.statement;

import java.util.Objects;

import enshud.pascal.Procedure;
import enshud.pascal.ast.IVisitor;
import enshud.pascal.ast.expression.IExpression;
import enshud.pascal.type.IType;
import enshud.s3.checker.Checker;
import enshud.s4.compiler.Casl2Code;
import enshud.s4.compiler.LabelGenerator;


public class IfElseStatement extends IfStatement
{
    private CompoundStatement else_statements;
    
    public IfElseStatement(IExpression cond, CompoundStatement then_statements, CompoundStatement else_statements)
    {
        super(cond, then_statements);
        this.else_statements = Objects.requireNonNull(else_statements);
    }
    
    public CompoundStatement getElse()
    {
        return else_statements;
    }
    
    @Override
    public <T, U> T accept(IVisitor<T, U> visitor, U option)
    {
        return visitor.visitIfElseStatement(this, option);
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
    public void compile(Casl2Code code, Procedure proc, LabelGenerator l_gen)
    {
        final int label = l_gen.next();
        
        getCond().compile(code, proc, l_gen);
        
        code.add("LD", "", "; set ZF", "GR2", "GR2");
        code.add("JZE", "", "; branch of IF", "E" + label);
        
        getThen().compile(code, proc, l_gen);
        code.add("JUMP", "", "", "F" + label);
        
        code.add("NOP", "E" + label, "; else of IF");
        getElse().compile(code, proc, l_gen);
        
        code.add("NOP", "F" + label, "; end of IF");
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
