package enshud.s4.compiler;

public enum Casl2Mnem
{
    LDi, LDr, ST, LAD,
    
    ADDAi, ADDLi, SUBAi, SUBLi, ANDi, ORi, XORi, CPAi, CPLi,
    ADDAr, ADDLr, SUBAr, SUBLr, ANDr, ORr, XORr, CPAr, CPLr,
    
    SLA, SRA, SLL, SRL,
    JPL, JMI, JNZ, JZE, JOV, JUMP,
    PUSH, POP,
    CALL, RET, NOP,
    
    START, END, DS, DC, IN, OUT
}
