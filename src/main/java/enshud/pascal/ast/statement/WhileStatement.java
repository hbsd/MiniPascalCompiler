package enshud.pascal.ast.statement;

import java.util.Objects;

import enshud.pascal.ast.IVisitor;
import enshud.pascal.ast.expression.IExpression;


public class WhileStatement implements IStatement
{
    private IExpression cond;
    private IStatement  statement;
    private boolean     infinite_loop = false;
    
    public WhileStatement(IExpression cond, IStatement statement)
    {
        this.cond = Objects.requireNonNull(cond);
        this.statement = Objects.requireNonNull(statement);
    }
    
    public IExpression getCond()
    {
        return cond;
    }
    
    public void setCond(IExpression cond)
    {
        this.cond = cond;
    }
    
    public IStatement getStatement()
    {
        return statement;
    }
    
    public boolean isInfiniteLoop()
    {
        return infinite_loop;
    }
    
    public void setIsInfiniteLoop(boolean infinite_loop)
    {
        this.infinite_loop = infinite_loop;
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
        return "";
    }
    
    @Override
    public String toOriginalCode(String indent)
    {
        return new StringBuilder()
                .append(indent).append("while ").append(getCond().toOriginalCode("")).append(" do").append(System.lineSeparator())
                .append(getStatement().toOriginalCode((getStatement() instanceof CompoundStatement)? indent: indent+"    "))
                .toString();
    }
    
    @Override
    public void printBodyln(String indent)
    {
        cond.println(indent + " |", "Condition of While");
        statement.println(indent + "  ", "Do of While");
    }
    
    public void setStatement(IStatement stm)
    {
        statement = stm;
    }
}

