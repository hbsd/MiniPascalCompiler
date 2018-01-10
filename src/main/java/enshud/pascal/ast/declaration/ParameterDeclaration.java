package enshud.pascal.ast.declaration;

import java.util.List;
import java.util.Objects;

import enshud.pascal.ast.Identifier;
import enshud.pascal.ast.NodeList;
import enshud.pascal.type.BasicType;


public class ParameterDeclaration implements IDeclaration
{
    private final NodeList<Identifier> names;
    private final TypeLiteral           type;
    
    public ParameterDeclaration(NodeList<Identifier> names, TypeLiteral type)
    {
        assert type.getType() instanceof BasicType;
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
    
    public BasicType getType()
    {
        return (BasicType)type.getType();
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

