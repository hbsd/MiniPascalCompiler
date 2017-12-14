package enshud.s4.compiler;

import enshud.pascal.Procedure;
import enshud.pascal.ast.statement.IStatement;


public interface IPrecomputable
{
    IStatement precompute(Procedure proc);
}
