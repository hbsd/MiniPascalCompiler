package enshud.pascal.ast;

import enshud.pascal.Procedure;
import enshud.pascal.ast.expression.*;
import enshud.pascal.ast.statement.*;

public abstract class TemplateVisitor<Ret, Opt> implements IVisitor<Ret, Opt>
{
    @Override
    public Ret visit(Procedure node, Opt option)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Ret visit(BooleanLiteral node, Opt option)
    {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public Ret visit(CharLiteral node, Opt option)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Ret visit(IndexedVariable node, Opt option)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Ret visit(InfixOperation node, Opt option)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Ret visit(IntegerLiteral node, Opt option)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Ret visit(PrefixOperation node, Opt option)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Ret visit(PureVariable node, Opt option)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Ret visit(StringLiteral node, Opt option)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Ret visit(AssignStatement node, Opt option)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Ret visit(CompoundStatement node, Opt option)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Ret visit(IfElseStatement node, Opt option)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Ret visit(IfStatement node, Opt option)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Ret visit(ProcCallStatement node, Opt option)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Ret visit(ReadStatement node, Opt option)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Ret visit(WhileStatement node, Opt option)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Ret visit(WriteStatement node, Opt option)
    {
        throw new UnsupportedOperationException();
    }
    
}
