package enshud.pascal.ast;

import enshud.pascal.Procedure;
import enshud.pascal.ast.expression.*;
import enshud.pascal.ast.statement.*;

public interface IVisitor<Ret, Opt>
{
    Ret visit(Procedure node, Opt option);
    
    Ret visit(BooleanLiteral node, Opt option);
    Ret visit(CharLiteral node, Opt option);
    Ret visit(IndexedVariable node, Opt option);
    Ret visit(InfixOperation node, Opt option);
    Ret visit(IntegerLiteral node, Opt option);
    Ret visit(PrefixOperation node, Opt option);
    Ret visit(PureVariable node, Opt option);
    Ret visit(StringLiteral node, Opt option);

    Ret visit(AssignStatement node, Opt option);
    Ret visit(CompoundStatement node, Opt option);
    Ret visit(IfElseStatement node, Opt option);
    Ret visit(IfStatement node, Opt option);
    Ret visit(ProcCallStatement node, Opt option);
    Ret visit(ReadStatement node, Opt option);
    Ret visit(WhileStatement node, Opt option);
    Ret visit(WriteStatement node, Opt option);
}
