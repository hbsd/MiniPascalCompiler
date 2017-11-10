package enshud.s3.checker;

import enshud.pascal.ast.IConstant;


public interface IPreevaluable
{
    IConstant preeval(Procedure proc);
}
