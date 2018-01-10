package enshud.s4.compiler;


import enshud.pascal.Procedure;
import enshud.pascal.ast.expression.IConstant;


public interface IPreevaluable
{
    IConstant preeval(Procedure proc);
}
