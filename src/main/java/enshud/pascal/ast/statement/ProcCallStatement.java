package enshud.pascal.ast.statement;

import java.util.List;
import java.util.Objects;

import enshud.pascal.Procedure;
import enshud.pascal.ast.IVisitor;
import enshud.pascal.ast.Identifier;
import enshud.pascal.ast.NodeList;
import enshud.pascal.ast.expression.IExpression;


public class ProcCallStatement implements IStatement
{
    private final Identifier            name;
    private final NodeList<IExpression> args;
    
    private Procedure                   called_proc;
    
    public ProcCallStatement(Identifier name, NodeList<IExpression> args)
    {
        this.name = Objects.requireNonNull(name);
        this.args = Objects.requireNonNull(args);
    }
    
    public Identifier getName()
    {
        return name;
    }
    
    public List<IExpression> getArgs()
    {
        return args;
    }
    
    public Procedure getCalledProc()
    {
        return called_proc;
    }
    
    public void setCalledProc(Procedure called_proc)
    {
        this.called_proc = called_proc;
    }
    
    @Override
    public int getLine()
    {
        return name.getLine();
    }
    
    @Override
    public int getColumn()
    {
        return name.getColumn();
    }
    
    @Override
    public <T, U> T accept(IVisitor<T, U> visitor, U option)
    {
        return visitor.visit(this, option);
    }
    
    @Override
    public String toString()
    {
        return "" + name;
    }
    
    @Override
    public void printBodyln(String indent)
    {
        args.println(indent + "  ");
    }
}

