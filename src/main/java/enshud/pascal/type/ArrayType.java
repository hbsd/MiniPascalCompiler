package enshud.pascal.type;

import java.util.Objects;

import enshud.pascal.ast.SignedInteger;


public class ArrayType implements IType
{
    public static final ArrayType CHAR = new ArrayType(BasicType.CHAR, 0, 0);
    
    private final BasicType       type;
    private final int             min;
    private final int             max;
    
    public ArrayType(BasicType type, int min, int max)
    {
        assert type != BasicType.UNKNOWN: "no Unknown";
        this.type = Objects.requireNonNull(type);
        this.min = Objects.requireNonNull(min);
        this.max = Objects.requireNonNull(max);
    }
    
    public ArrayType(BasicType type, SignedInteger min, SignedInteger max)
    {
        this(type, min.getInt(), max.getInt());
    }
    
    @Override
    public BasicType getBasicType()
    {
        return type;
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

