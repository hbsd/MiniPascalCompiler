package enshud.pascal.type;

import java.util.HashMap;
import java.util.Map;


public class StringType implements IType
{
    public static final StringType                CHAR = new StringType(1);
    private static final Map<Integer, StringType> memo = new HashMap<>();
    
    private final int                             size;
    
    private StringType(int size)
    {
        this.size = size;
    }
    
    public static StringType create(int size)
    {
        /*if (size == 1)
        {
            throw new IllegalArgumentException("String length must not be 1.");
        }
        else */if (memo.containsKey(size))
        {
            return memo.get(size);
        }
        else
        {
            final StringType s = new StringType(size);
            memo.put(size, s);
            return s;
        }
    }
    
    public static boolean isCharOrCharArray(IType bype)
    {
        return bype == BasicType.CHAR || bype.isArrayOf(BasicType.CHAR);
    }
    
    @Override
    public int getSize()
    {
        return size;
    }
    
    @Override
    public boolean equals(IType rval)
    {
        return this == rval || (rval == BasicType.CHAR && this == StringType.CHAR);
    }
    
    @Override
    public boolean isArrayOf(BasicType rtype)
    {
        return rtype == BasicType.CHAR;
    }
    
    @Override
    public boolean isArrayType()
    {
        return true;
    }
    
    @Override
    public boolean isBasicType()
    {
        return false;
    }
    
    @Override
    public boolean isUnknown()
    {
        return false;
    }
    
    @Override
    public String toString()
    {
        return "STRING";
    }
}
