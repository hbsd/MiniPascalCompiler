package enshud.pascal.ast;

import enshud.pascal.ast.expression.*;
import enshud.pascal.ast.statement.*;

public interface IVisitor<Ret, Opt>
{
    Ret visitBooleanLiteral(BooleanLiteral node, Opt option);
    Ret visitIndexedVariable(IndexedVariable node, Opt option);
    Ret visitInfixOperation(InfixOperation node, Opt option);
    Ret visitIntegerLiteral(IntegerLiteral node, Opt option);
    Ret visitPrefixOperation(PrefixOperation node, Opt option);
    Ret visitPureVariable(PureVariable node, Opt option);
    Ret visitStringLiteral(StringLiteral node, Opt option);

    Ret visitAssignStatement(AssignStatement node, Opt option);
    Ret visitCompoundStatement(CompoundStatement node, Opt option);
    Ret visitIfElseStatement(IfElseStatement node, Opt option);
    Ret visitIfStatement(IfStatement node, Opt option);
    Ret visitProcCallStatement(ProcCallStatement node, Opt option);
    Ret visitReadStatement(ReadStatement node, Opt option);
    Ret visitWhileStatement(WhileStatement node, Opt option);
    Ret visitWriteStatement(WriteStatement node, Opt option);
}
