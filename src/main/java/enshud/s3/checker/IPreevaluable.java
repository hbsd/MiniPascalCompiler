package enshud.s3.checker;

import enshud.pascal.Procedure;
import enshud.pascal.ast.expression.IConstant;


public interface IPreevaluable
{
    IConstant preeval(Procedure proc);
}
