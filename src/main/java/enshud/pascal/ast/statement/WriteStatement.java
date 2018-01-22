package enshud.pascal.ast.statement;

import java.util.List;
import java.util.Objects;

import enshud.pascal.ast.IVisitor;
import enshud.pascal.ast.NodeList;
import enshud.pascal.ast.expression.IExpression;


public class WriteStatement implements IStatement
{
    private final NodeList<IExpression> exps;
    
    public WriteStatement(NodeList<IExpression> exps)
    {
        this.exps = Objects.requireNonNull(exps);
    }
    
    public List<IExpression> getExpressions()
    {
        return exps;
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
    public void printBodyln(String indent)
    {
        exps.println(indent + "  ");
    }
}

