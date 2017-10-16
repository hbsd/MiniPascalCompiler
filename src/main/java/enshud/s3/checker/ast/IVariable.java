package enshud.s3.checker.ast;

import enshud.s3.checker.Procedure;
import enshud.s4.compiler.LabelGenerator;

public interface IVariable extends IFactor
{
    Identifier getName();

    void compileForData(StringBuilder codebuilder, Procedure proc, LabelGenerator l_gen);
    void compileForAddr(StringBuilder codebuilder, Procedure proc, LabelGenerator l_gen);
}


