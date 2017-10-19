package enshud.pascal.ast;

import java.util.List;
import java.util.Objects;

import enshud.pascal.type.RegularType;


public class Parameter implements IASTNode
{
    final NameList    names;
    final TypeLiteral type;

    public Parameter(NameList names, TypeLiteral type)
    {
        assert type.getType() instanceof RegularType;
        this.names = Objects.requireNonNull(names);
        this.type = Objects.requireNonNull(type);
    }

    public List<Identifier> getNames()
    {
        return names.getList();
    }

    public TypeLiteral getTypeLiteral()
    {
        return type;
    }

    public RegularType getType()
    {
        return (RegularType)type.getType();
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
    public void println(String indent)
    {
        type.println(indent + " |");
        names.println(indent + "  ");
    }
}


