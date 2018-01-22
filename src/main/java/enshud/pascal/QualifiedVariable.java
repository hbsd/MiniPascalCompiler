package enshud.pascal;

import java.util.Objects;

import enshud.pascal.type.IType;


public class QualifiedVariable
{
    private final String    name;
    private final IType     type;
    private final int       alignment;
    private final Procedure proc;
    
    public QualifiedVariable(String name, IType type, int alignment, Procedure proc)
    {
        this.name = Objects.requireNonNull(name);
        this.type = type;
        this.alignment = alignment;
        this.proc = proc;
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
    
    public Procedure getProc()
    {
        return proc;
    }
    
    public String getQualifiedName()
    {
        return proc == null
                ? getName()
                : getProc().getQualifiedName() + "." + getName();
    }
    
    @Override
    public String toString()
    {
        return getName();
    }
}
