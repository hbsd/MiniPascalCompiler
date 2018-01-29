package enshud.pascal.ast.expression;

import java.util.Objects;

import enshud.pascal.QualifiedVariable;
import enshud.pascal.ast.ILiteral;
import enshud.pascal.ast.IVisitor;
import enshud.pascal.ast.Identifier;
import enshud.pascal.type.IType;


public class PureVariable implements IVariable, ILiteral
{
    private final Identifier  name;
    private IType             type;
    private QualifiedVariable var_referenced;
    private boolean           is_param;
    
    public PureVariable(Identifier name)
    {
        this.name = Objects.requireNonNull(name);
        type = null;
        var_referenced = null;
    }
    
    @Override
    public Identifier getName()
    {
        return name;
    }
    
    public String getQualifiedName()
    {
        if(var_referenced == null)
        {
            System.out.println(getName()+","+getLine());
        }
        return var_referenced.getQualifiedName();
    }
    
    @Override
    public IType getType()
    {
        return type;
    }
    
    public void setType(IType type)
    {
        this.type = type;
    }
    
    public QualifiedVariable getVar()
    {
        return var_referenced;
    }
    
    public void setVar(QualifiedVariable var_referenced)
    {
        this.var_referenced = var_referenced;
    }
    
    public boolean isParam()
    {
        return is_param;
    }
    
    public void setIsParam(boolean is_param)
    {
        this.is_param = is_param;
    }
    
    @Override
    public boolean equals(IExpression rexp)
    {
        if(rexp instanceof PureVariable)
        {
            return this == rexp || this.var_referenced == ((PureVariable)rexp).var_referenced;
        }
        else
        {
            return false;
        }
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
    public String toOriginalCode(String indent)
    {
        return "" + getName();
    }
    
    @Override
    public void printHead(String indent, String msg)
    {
        ILiteral.super.printHead(indent, msg);
    }
    
    @Override
    public String toString()
    {
        return name.toString();
    }
}

