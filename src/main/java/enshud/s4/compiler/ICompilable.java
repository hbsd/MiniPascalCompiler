package enshud.s4.compiler;

import java.util.List;

import enshud.s3.checker.Procedure;


public interface ICompilable
{
    void compile(List<Casl2Instruction> code, Procedure proc, LabelGenerator l_gen);
}
