package enshud.pascal.type;

public interface IType
{
    int getSize();
    RegularType getRegularType();

    default boolean equals(IType rval)
    {
        return this == rval;
    }

    boolean isArrayOf(RegularType rtype);
    boolean isArrayType();
    boolean isRegularType();
    boolean isUnknown();
}


