package enshud.s3.checker;

import enshud.pascal.type.IType;
import enshud.s3.checker.Procedure;


public interface IChecker
{
    IType check(Procedure proc, Checker checker);

    /*default IValue eval(Context context, boolean enable_io)
    {
        throw new UnsupportedOperationException();
    }*/
}
