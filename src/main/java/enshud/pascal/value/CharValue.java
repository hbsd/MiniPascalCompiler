package enshud.pascal.value;

import java.util.HashMap;
import java.util.Map;

import enshud.pascal.type.BasicType;

public class CharValue implements IValue
{
    private static final Map<Character, CharValue> memo = new HashMap<>();
    private final char val;
    
    private CharValue(char val)
    {
        this.val = val;
        memo.put(val, this);
    }
    
    public static CharValue create(char val)
    {
        return memo.containsKey(val)? memo.get(val): new CharValue(val);
    }
    
    @Override
    public int getInt()
    {
        return (int)val;
    }
    
    @Override
    public BasicType getType()
    {
        return BasicType.CHAR;
    }
    
    @Override
    public boolean equals(IValue rexp)
    {
        if (rexp instanceof CharValue)
        {
            return this == rexp || this.val == ((CharValue)rexp).val;
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
