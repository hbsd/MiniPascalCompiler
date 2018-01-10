package enshud.s4.compiler;

import java.util.ListIterator;

import enshud.pascal.Procedure;
import enshud.pascal.ast.IVisitor;
import enshud.pascal.ast.TemplateVisitor;
import enshud.pascal.ast.expression.*;
import enshud.pascal.ast.statement.*;

public class OptimizeVisitor extends TemplateVisitor<IStatement, Procedure>
{
    @Override
    public IStatement visitAssignStatement(AssignStatement node, Procedure proc)
    {
        final IConstant res = node.getRight().accept(exp_visitor, proc);
        if (res != null)
        {
            node.setRight(res);
        }
        return node;
    }

    @Override
    public IStatement visitCompoundStatement(CompoundStatement node, Procedure proc)
    {
        final ListIterator<IStatement> it = node.listIterator();
        while (it.hasNext())
        {
            final IStatement res = it.next().accept(this, proc);
            if (res == null)
            {
                it.remove();
            }
            else
            {
                it.set(res);
            }
        }
        return node.isEmpty()? null: node;
    }

    @Override
    public IStatement visitIfElseStatement(IfElseStatement node, Procedure proc)
    {
        final IStatement sup = visitIfStatement(node, proc);
        node.getElse().accept(this, proc);
        return (sup != null)? sup: node.getElse();
    }

    @Override
    public IStatement visitIfStatement(IfStatement node, Procedure proc)
    {
        final IConstant res = node.getCond().accept(exp_visitor, proc);
        
        if (res == null)
        {
            node.getThen().accept(this, proc);
            return node;
        }
        if (((BooleanLiteral)res).getBool())
        {
            node.getThen().accept(this, proc);
            return node.getThen();
        }
        else
        {
            return null;
        }
    }

    @Override
    public IStatement visitProcCallStatement(ProcCallStatement node, Procedure proc)
    {
        node.getArgs().forEach(e -> e.accept(exp_visitor, proc));
        return node;
    }

    @Override
    public IStatement visitReadStatement(ReadStatement node, Procedure proc)
    {
        node.getVariables().forEach(v -> v.accept(exp_visitor, proc));
        return node;
    }

    @Override
    public IStatement visitWhileStatement(WhileStatement node, Procedure proc)
    {
        final IConstant res = node.getCond().accept(exp_visitor, proc);
        if (res == null)
        {
            node.getStatement().accept(this, proc);
            return node;
        }
        else if (((BooleanLiteral)res).getBool())
        {
            node.getStatement().accept(this, proc);
            node.setIsInfiniteLoop(true);
            return node;
        }
        else
        {
            return null;
        }
    }

    @Override
    public IStatement visitWriteStatement(WriteStatement node, Procedure proc)
    {
        node.getExpressions().forEach(e -> e.accept(exp_visitor, proc));
        return node;
    }
    
    final IVisitor<IConstant, Procedure> exp_visitor = new TemplateVisitor<IConstant, Procedure>() {
        @Override
        public IConstant visitBooleanLiteral(BooleanLiteral node, Procedure proc)
        {
            return node;
        }

        @Override
        public IConstant visitIndexedVariable(IndexedVariable node, Procedure proc)
        {
            return null;
        }

        @Override
        public IConstant visitInfixOperation(InfixOperation node, Procedure proc)
        {
            final IConstant l = node.getLeft().accept(this, proc);
            if (l != null)
            {
                node.setLeft(l);
            }
            
            final IConstant r = node.getRight().accept(this, proc);
            if (r != null)
            {
                node.setRight(r);
            }
            
            if (l == null || r == null)
            {
                return null;
            }
            else
            {
                return node.getOp().eval(l.getInt(), r.getInt());
            }
        }

        @Override
        public IConstant visitIntegerLiteral(IntegerLiteral node, Procedure proc)
        {
            return node;
        }

        @Override
        public IConstant visitPrefixOperation(PrefixOperation node, Procedure proc)
        {
            IConstant res = node.getOperand().accept(this, proc);
            if (res == null)
            {
                return null;
            }
            
            node.setOperand(res);
            
            return node.getOp().eval(res.getInt());
        }

        @Override
        public IConstant visitPureVariable(PureVariable node, Procedure proc)
        {
            return null;
        }

        @Override
        public IConstant visitStringLiteral(StringLiteral node, Procedure proc)
        {
            return node;
        }
    };
}
