package enshud.s3.checker.ast;

import enshud.s3.checker.Procedure;

public interface IVariable extends IFactor
{
    Identifier getName();

    /*void compileForAddr(StringBuilder codebuilder, Procedure proc, LabelGenerator l_gen);*/
}


