package enshud.pascal.ast.statement;

import java.util.Objects;

import enshud.pascal.ast.IVisitor;
import enshud.pascal.ast.expression.IExpression;


public class IfStatement implements IStatement
{
    private IExpression             cond;
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
    
    public void setCond(IExpression cond)
    {
        this.cond = cond;
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
    public <T, U> T accept(IVisitor<T, U> visitor, U option)
    {
        return visitor.visit(this, option);
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

