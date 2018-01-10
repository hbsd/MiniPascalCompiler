package enshud.pascal.ast;

import enshud.pascal.ast.expression.*;
import enshud.pascal.ast.statement.*;

public abstract class TemplateVisitor<Ret, Opt> implements IVisitor<Ret, Opt>
{

    @Override
    public Ret visitBooleanLiteral(BooleanLiteral node, Opt option)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Ret visitIndexedVariable(IndexedVariable node, Opt option)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Ret visitInfixOperation(InfixOperation node, Opt option)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Ret visitIntegerLiteral(IntegerLiteral node, Opt option)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Ret visitPrefixOperation(PrefixOperation node, Opt option)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Ret visitPureVariable(PureVariable node, Opt option)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Ret visitStringLiteral(StringLiteral node, Opt option)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Ret visitAssignStatement(AssignStatement node, Opt option)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Ret visitCompoundStatement(CompoundStatement node, Opt option)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Ret visitIfElseStatement(IfElseStatement node, Opt option)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Ret visitIfStatement(IfStatement node, Opt option)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Ret visitProcCallStatement(ProcCallStatement node, Opt option)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Ret visitReadStatement(ReadStatement node, Opt option)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Ret visitWhileStatement(WhileStatement node, Opt option)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Ret visitWriteStatement(WriteStatement node, Opt option)
    {
        throw new UnsupportedOperationException();
    }
    
}
