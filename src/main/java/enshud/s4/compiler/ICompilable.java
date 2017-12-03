package enshud.s4.compiler;


import enshud.pascal.Procedure;


public interface ICompilable
{
    void compile(Casl2Code code, Procedure proc, LabelGenerator l_gen);
}
