package enshud.s3.checker;

import enshud.pascal.Procedure;
import enshud.pascal.ast.statement.IStatement;


public interface IPrecomputable
{
    IStatement precompute(Procedure proc);
}
