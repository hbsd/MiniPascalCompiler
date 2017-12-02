package enshud.s3.checker;

import java.util.Objects;

import enshud.pascal.type.IType;

public class Variable
{
    private final String name;
    private final IType  type;
    private final int    alignment;

    public Variable(String name, IType type, int alignment)
    {
        this.name = Objects.requireNonNull(name);
        this.type = Objects.requireNonNull(type);
        this.alignment = alignment;
    }

    public String getName()
    {
        return name;
    }
    
    public IType getType()
    {
        return type;
    }
    
    public int getAlignment()
    {
        return alignment;
    }
    
    @Override
    public String toString()
    {
        return getName();
    }
}