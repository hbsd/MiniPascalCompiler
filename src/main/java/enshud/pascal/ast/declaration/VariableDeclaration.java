package enshud.pascal.ast.declaration;

import java.util.List;
import java.util.Objects;

import enshud.pascal.ast.NodeList;
import enshud.pascal.ast.expression.Identifier;
import enshud.pascal.type.IType;


public class VariableDeclaration implements IDeclaration
{
    private final NodeList<Identifier> names;
    private final TypeLiteral          type;
    
    public VariableDeclaration(NodeList<Identifier> names, TypeLiteral type)
    {
        this.names = Objects.requireNonNull(names);
        this.type = Objects.requireNonNull(type);
    }
    
    public List<Identifier> getNames()
    {
        return names;
    }
    
    public TypeLiteral getTypeLiteral()
    {
        return type;
    }
    
    public IType getType()
    {
        return type.getType();
    }
    
    @Override
    public int getLine()
    {
        return names.getLine();
    }
    
    @Override
    public int getColumn()
    {
        return names.getColumn();
    }
    
    @Override
    public String toString()
    {
        return "";
    }
    
    @Override
    public void printBodyln(String indent)
    {
        type.println(indent + " |");
        names.println(indent + "  ");
    }
}

