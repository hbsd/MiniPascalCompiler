package enshud.s3.checker;

import enshud.pascal.ast.IStatement;

public interface IPrecomputable
{
    IStatement precompute(Procedure proc, Context context);
}
