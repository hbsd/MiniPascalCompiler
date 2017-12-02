package enshud.pascal.ast;

import java.util.List;

import enshud.s3.checker.Procedure;
import enshud.s4.compiler.Casl2Instruction;
import enshud.s4.compiler.LabelGenerator;


public interface IVariable extends ITyped
{
    Identifier getName();
    
    void compileForData(List<Casl2Instruction> code, Procedure proc, LabelGenerator l_gen);
    
    void compileForAddr(List<Casl2Instruction> code, Procedure proc, LabelGenerator l_gen);
}

