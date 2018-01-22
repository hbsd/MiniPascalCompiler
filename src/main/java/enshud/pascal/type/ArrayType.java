package enshud.pascal.type;

import java.util.Objects;


public class ArrayType implements IType
{
    private final BasicType       type;
    private final int             min;
    private final int             max;
    
    public ArrayType(BasicType type, int min, int max)
    {
        this.type = Objects.requireNonNull(type);
        this.min = Objects.requireNonNull(min);
        this.max = Objects.requireNonNull(max);
    }
    
    public int getMin()
    {
        return min;
    }
    
    public int getMax()
    {
        return max;
    }
    
    @Override
    public int getSize()
    {
        return max - min + 1;
    }
    
    public BasicType getBasicType()
    {
        return type;
    }
    
    @Override
    public String toString()
    {
        return "[" + min + ".." + max + "] of " + type;
    }
    
    @Override
    public boolean equals(IType rval)
    {
        if (rval == null)
        {
            return false;
        }
        if (this == rval || rval instanceof UnknownType)
        {
            return true;
        }
        if (rval instanceof ArrayType)
        {
            final ArrayType rv = (ArrayType)rval;
            return type == rv.type && getSize() == rv.getSize();
        }
        return false;
    }
    
    @Override
    public boolean isArrayOf(BasicType btype)
    {
        return type.equals(btype);
    }
    
    @Override
    public final boolean isArrayType()
    {
        return true;
    }
    
    @Override
    public final boolean isBasicType()
    {
        return false;
    }
    
    @Override
    public boolean isUnknown()
    {
        return false;
    }
}

