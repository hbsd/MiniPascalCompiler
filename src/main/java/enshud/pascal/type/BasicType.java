package enshud.pascal.type;


public enum BasicType implements IType
{
    INTEGER,
    CHAR,
    BOOLEAN,
    
    UNKNOWN {
        @Override
        public boolean isUnknown()
        {
            return true;
        }
    };
    
    @Override
    public int getSize()
    {
        return 1;
    }
    
    @Override
    public final BasicType getBasicType()
    {
        return this;
    }
    
    @Override
    public final boolean isArrayOf(BasicType btype)
    {
        return false;
    }
    
    @Override
    public final boolean isBasicType()
    {
        return true;
    }
    
    @Override
    public boolean equals(IType rval)
    {
        return rval == this || rval == BasicType.UNKNOWN || rval == UnknownType.UNKNOWN;
    }
    
    @Override
    public boolean isArrayType()
    {
        return false;
    }
    
    @Override
    public boolean isUnknown()
    {
        return false;
    }
}

