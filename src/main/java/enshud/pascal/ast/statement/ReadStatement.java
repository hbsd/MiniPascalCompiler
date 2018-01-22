package enshud.pascal.ast.statement;

import java.util.List;
import java.util.Objects;

import enshud.pascal.ast.IVisitor;
import enshud.pascal.ast.NodeList;
import enshud.pascal.ast.expression.IVariable;


public class ReadStatement implements IStatement
{
    private final NodeList<IVariable> vars;
    
    public ReadStatement(NodeList<IVariable> vars)
    {
        this.vars = Objects.requireNonNull(vars);
    }
    
    public List<IVariable> getVariables()
    {
        return vars;
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
    public void printBodyln(String indent)
    {
        vars.println(indent + "  ");
    }
}

