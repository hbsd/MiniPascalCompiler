package enshud.pascal.value;

import java.util.HashMap;
import java.util.Map;

import enshud.pascal.type.StringType;


public class StringValue implements IValue
{
    private static Map<String, StringValue> memo = new HashMap<>();
    private final String                    str;
    private final StringType                type;
    
    public StringValue(String str)
    {
        this.str = str;
        type = StringType.create(str.length());
        memo.put(str, this);
    }
    
    public static StringValue create(String str)
    {
        return memo.containsKey(str)? memo.get(str): new StringValue(str);
    }
    
    @Override
    public int getInt()
    {
        if (getType() == StringType.CHAR)
        {
            return (int)toString().charAt(1);
        }
        else
        {
            throw new UnsupportedOperationException("not CHAR type");
        }
    }
    
    public String getString()
    {
        return str;
    }
    
    @Override
    public String toString()
    {
        return str;
    }
    
    public int length()
    {
        return toString().length() - 2;
    }
    
    @Override
    public StringType getType()
    {
        return type;
    }
    
    @Override
    public boolean equals(IValue rexp)
    {
        if (rexp instanceof StringValue)
        {
            return this == rexp || this.str == ((StringValue)rexp).str;
        }
        else
        {
            return false;
        }
    }
}
