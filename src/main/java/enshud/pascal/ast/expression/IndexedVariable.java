package enshud.pascal.ast.expression;

import java.util.Objects;

import enshud.pascal.QualifiedVariable;
import enshud.pascal.ast.ILiteral;
import enshud.pascal.ast.IVisitor;
import enshud.pascal.ast.Identifier;
import enshud.pascal.type.ArrayType;
import enshud.pascal.type.IType;


public class IndexedVariable implements IVariable, ILiteral
{
    private final Identifier  name;
    private IExpression       index;
    private IType             type;
    private QualifiedVariable var_referenced;
    
    public IndexedVariable(Identifier name, IExpression index)
    {
        this.name = Objects.requireNonNull(name);
        this.index = Objects.requireNonNull(index);
        type = null;
    }
    
    @Override
    public Identifier getName()
    {
        return name;
    }
    
    public String getQualifiedName()
    {
        return var_referenced.getQualifiedName();
    }
    
    public IExpression getIndex()
    {
        return index;
    }
    
    public void setIndex(IExpression index)
    {
        this.index = index;
        
    }
    
    @Override
    public IType getType()
    {
        return ((ArrayType)type).getBasicType();
    }
    
    public ArrayType getArrayType()
    {
        return (ArrayType)type;
    }
    
    public void setArrayType(IType type)
    {
        this.type = type;
    }
    
    public QualifiedVariable getVar()
    {
        return var_referenced;
    }
    
    public void setVar(QualifiedVariable v)
    {
        var_referenced = v;
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
    public boolean equals(IExpression rexp)
    {
        if (rexp instanceof IndexedVariable)
        {
            final IndexedVariable v = (IndexedVariable)rexp;
            return this == rexp || (this.getVar() == v.getVar() && getIndex().equals(v.getIndex()));
        }
        else
        {
            return false;
        }
    }
    
    @Override
    public void printHead(String indent, String msg)
    {
        ILiteral.super.printHead(indent, msg);
    }
    
    @Override
    public String toString()
    {
        return "" + name;
    }
    
    @Override
    public void printBodyln(String indent)
    {
        index.println(indent + "  ");
    }
}

