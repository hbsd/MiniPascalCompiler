package enshud.pascal.value;

import enshud.pascal.type.IType;

public interface IValue
{
    int getInt();
    IType getType();
    boolean equals(IValue rexp);
}
