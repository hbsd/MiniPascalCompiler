package enshud.pascal.ast.statement;

import java.util.Objects;

import enshud.pascal.ast.IVisitor;
import enshud.pascal.ast.expression.IExpression;
import enshud.pascal.ast.expression.IVariable;


public class AssignStatement implements IStatement
{
    private final IVariable left;
    private IExpression          right;
    
    public AssignStatement(IVariable left, IExpression right)
    {
        this.left = Objects.requireNonNull(left);
        this.right = Objects.requireNonNull(right);
    }
    
    public IVariable getLeft()
    {
        return left;
    }
    
    public IExpression getRight()
    {
        return right;
    }
    
    public void setRight(IExpression right)
    {
        this.right = right;
    }
    
    @Override
    public int getLine()
    {
        return left.getLine();
    }
    
    @Override
    public int getColumn()
    {
        return left.getColumn();
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
        left.println(indent + " |");
        right.println(indent + "  ");
    }
}

