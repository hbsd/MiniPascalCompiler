package enshud.s3.checker.type;

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
    public RegularType getRegularType()
    {
        return RegularType.UNKNOWN;
    }
    
    @Override
    public boolean equals(IType rval)
    {
        return false;
    }

    @Override
    public boolean isArrayOf(RegularType rtype)
    {
        return true;
    }
    
    @Override
    public boolean isArrayType()
    {
        return true;
    }

    @Override
    public boolean isRegularType()
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
