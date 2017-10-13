package enshud.s3.checker.type;

import java.util.Objects;

import enshud.s3.checker.ast.SignedInteger;


public class ArrayType implements IType
{
    public static final ArrayType CHAR = new ArrayType(RegularType.CHAR, 0, 0);

    final RegularType type;
    final int         min;
    final int         max;

    public ArrayType(RegularType type, int min, int max)
    {
        assert type != RegularType.UNKNOWN: "no Unknown";
        this.type = Objects.requireNonNull(type);
        this.min = Objects.requireNonNull(min);
        this.max = Objects.requireNonNull(max);
    }

    public ArrayType(RegularType type, SignedInteger min, SignedInteger max)
    {
        this(type, min.getInt(), max.getInt());
    }

    @Override
    public RegularType getRegularType()
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
        if( rval == null )
        {
            return false;
        }
        if( this == rval || rval instanceof UnknownType )
        {
            return true;
        }
        if( rval instanceof ArrayType )
        {
            final ArrayType rv = (ArrayType)rval;
            return type == rv.type && getSize() == rv.getSize();
        }
        return false;
    }

    @Override
    public boolean isArrayOf(RegularType rtype)
    {
        return type.equals(rtype);
    }
    
    @Override
    public final boolean isArrayType()
    {
        return true;
    }

    @Override
    public final boolean isRegularType()
    {
        return false;
    }
    
    @Override
    public boolean isUnknown()
    {
        return false;
    }
}


