package enshud.pascal.value;

import enshud.pascal.type.BasicType;

public enum BooleanValue implements IValue
{
    FALSE{
        @Override
        public int getInt()
        {
            return 0;
        }
        
        @Override
        public boolean getBool()
        {
            return false;
        }
        
        @Override
        public String toString()
        {
            return "Bool(false)";
        }
    },
    TRUE{
        @Override
        public int getInt()
        {
            return 1;
        }
        
        @Override
        public boolean getBool()
        {
            return true;
        }
        
        @Override
        public String toString()
        {
            return "Bool(true)";
        }
    };
    
    public static BooleanValue create(boolean val)
    {
        return val? TRUE: FALSE;
    }
    
    public abstract boolean getBool();
    
    @Override
    public final BasicType getType()
    {
        return BasicType.BOOLEAN;
    }
    
    @Override
    public final boolean equals(IValue rexp)
    {
        if (rexp instanceof BooleanValue)
        {
            return this == rexp;
        }
        else
        {
            return false;
        }
    }
}
