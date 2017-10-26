package enshud.pascal.type;

public interface IType
{
    int getSize();
    
    BasicType getBasicType();
    
    default boolean equals(IType rval)
    {
        return this == rval;
    }
    
    boolean isArrayOf(BasicType btype);
    
    boolean isArrayType();
    
    boolean isBasicType();
    
    boolean isUnknown();
}

