package enshud.pascal.ast.statement;

import java.util.Objects;

import enshud.pascal.ast.IVisitor;
import enshud.pascal.ast.expression.IExpression;


public class IfElseStatement implements IStatement
{
    private final IfStatement       if_part;
    private final CompoundStatement else_statements;
    
    public IfElseStatement(IExpression cond, CompoundStatement then_statements, CompoundStatement else_statements)
    {
        if_part = new IfStatement(cond, then_statements);
        this.else_statements = Objects.requireNonNull(else_statements);
    }
    
    public IfStatement getIfPart()
    {
        return if_part;
    }
    
    public IExpression getCond()
    {
        return if_part.getCond();
    }
    
    public CompoundStatement getThen()
    {
        return if_part.getThen();
    }
    
    public CompoundStatement getElse()
    {
        return else_statements;
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
        return "Then & Else";
    }
    
    @Override
    public void printBodyln(String indent)
    {
        if_part.printBodyln(indent);
        else_statements.println(indent + "  ", "Else of IfElse");
    }
}
