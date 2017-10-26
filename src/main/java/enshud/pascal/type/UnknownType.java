package enshud.pascal.type;

public class UnknownType implements IType
{
    public static final UnknownType UNKNOWN = new UnknownType();
    
    private UnknownType()
    {}
    
    @Override
    public int getSize()
    {
        return -1;
    }
    
    @Override
    public BasicType getBasicType()
    {
        return BasicType.UNKNOWN;
    }
    
    @Override
    public boolean equals(IType rval)
    {
        return false;
    }
    
    @Override
    public boolean isArrayOf(BasicType btype)
    {
        return true;
    }
    
    @Override
    public boolean isArrayType()
    {
        return true;
    }
    
    @Override
    public boolean isBasicType()
    {
        return true;
    }
    
    @Override
    public boolean isUnknown()
    {
        return true;
    }
    
    @Override
    public String toString()
    {
        return "Unknown";
    }
}
