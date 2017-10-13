package enshud.s4.compiler;

import enshud.s3.checker.Procedure;


public interface ICompiler
{
    void compile(StringBuilder codebuilder, Procedure proc, LabelGenerator l_gen);
}
