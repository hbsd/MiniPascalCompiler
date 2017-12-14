package enshud.pascal.type;

public interface IType
{
    int getSize();
    
    default boolean equals(IType rval)
    {
        return this == rval;
    }
    
    boolean isArrayOf(BasicType btype);
    
    boolean isArrayType();
    
    boolean isUnknown();

    boolean isBasicType();
}

