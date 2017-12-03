package enshud.s3.checker;

import enshud.pascal.Procedure;
import enshud.pascal.type.IType;


public interface ICheckable
{
    IType check(Procedure proc, Checker checker);
    
    /*
     * default IValue eval(Context context, boolean enable_io) { throw new
     * UnsupportedOperationException(); }
     */
}
