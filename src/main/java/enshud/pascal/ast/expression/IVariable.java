package enshud.pascal.ast.expression;

import enshud.pascal.Procedure;
import enshud.s4.compiler.Casl2Code;
import enshud.s4.compiler.LabelGenerator;


public interface IVariable extends IExpression
{
    Identifier getName();
    
    void compileForData(Casl2Code code, Procedure proc, LabelGenerator l_gen);
    
    void compileForAddr(Casl2Code code, Procedure proc, LabelGenerator l_gen);
}

