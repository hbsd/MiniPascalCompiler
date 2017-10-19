package enshud.pascal.type;


public enum RegularType implements IType
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
    public final RegularType getRegularType()
    {
        return this;
    }

    @Override
    public final boolean isArrayOf(RegularType rtype)
    {
        return false;
    }

    @Override
    public final boolean isRegularType()
    {
        return true;
    }

    @Override
    public boolean equals(IType rval)
    {
        return rval == this || rval == RegularType.UNKNOWN || rval == UnknownType.UNKNOWN;
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


