package enshud.pascal.value;

import java.util.HashMap;
import java.util.Map;

import enshud.pascal.type.BasicType;

public class IntegerValue implements IValue
{
    private static final Map<Integer, IntegerValue> memo = new HashMap<>();
    private final int val;
    
    private IntegerValue(int val)
    {
        this.val = val;
        memo.put(val, this);
    }
    
    public static IntegerValue create(int val)
    {
        return memo.containsKey(val)? memo.get(val): new IntegerValue(val);
    }
    
    @Override
    public int getInt()
    {
        return val;
    }
    
    @Override
    public BasicType getType()
    {
        return BasicType.INTEGER;
    }
    
    @Override
    public boolean equals(IValue rexp)
    {
        if (rexp instanceof IntegerValue)
        {
            return this == rexp || this.val == ((IntegerValue)rexp).val;
        }
        else
        {
            return false;
        }
    }
    
    @Override
    public String toString()
    {
        return "" + val;
    }
}
