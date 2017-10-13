package enshud.s3.checker;

import enshud.s3.checker.Procedure;
import enshud.s3.checker.type.IType;
//import enshud.s3.checker.value.IValue;


public interface IChecker
{
    IType check(Procedure proc, Checker checker);

    /*default IValue eval(Context context, boolean enable_io)
    {
        throw new UnsupportedOperationException();
    }*/
}
