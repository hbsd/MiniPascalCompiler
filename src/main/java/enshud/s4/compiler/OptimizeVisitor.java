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
    public IStatement visit(Procedure node, Procedure proc)
    {
        node.getBody().accept(this, node);
        node.getChildren().forEach(sub -> sub.accept(this, node));
        return null;
    }
    
    @Override
    public IStatement visit(AssignStatement node, Procedure proc)
    {
        final IExpression res = node.getRight().accept(exp_visitor, proc);
        if (res.isConstant())
        {
            node.setRight(res);
        }
        return node;
    }
    
    @Override
    public IStatement visit(CompoundStatement node, Procedure proc)
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
    public IStatement visit(IfElseStatement node, Procedure proc)
    {
        final IStatement sup = node.getIfPart().accept(this, proc);
        node.getElse().accept(this, proc);
        return (sup == null)? node.getElse(): (sup instanceof CompoundStatement)? sup: node;
    }
    
    @Override
    public IStatement visit(IfStatement node, Procedure proc)
    {
        final IExpression res = node.getCond().accept(exp_visitor, proc);
        node.getThen().accept(this, proc);
        
        if (res instanceof BooleanLiteral)
        {
            return ((BooleanLiteral)res).getValue().getBool()? node.getThen(): null;
        }
        
        return node;
    }
    
    @Override
    public IStatement visit(ProcCallStatement node, Procedure proc)
    {
        node.getArgs().forEach(e -> e.accept(exp_visitor, proc));
        return node;
    }
    
    @Override
    public IStatement visit(ReadStatement node, Procedure proc)
    {
        node.getVariables().forEach(v -> v.accept(exp_visitor, proc));
        return node;
    }
    
    @Override
    public IStatement visit(WhileStatement node, Procedure proc)
    {
        final IExpression res = node.getCond().accept(exp_visitor, proc);
        if (res instanceof BooleanLiteral)
        {
            if (((BooleanLiteral)res).getValue().getBool())
            {
                node.setIsInfiniteLoop(true);
            }
            else
            {
                return null;
            }
        }
        node.getStatement().accept(this, proc);
        return node;
    }
    
    @Override
    public IStatement visit(WriteStatement node, Procedure proc)
    {
        node.getExpressions().forEach(e -> e.accept(exp_visitor, proc));
        return node;
    }
    
    final IVisitor<IExpression, Procedure> exp_visitor = new TemplateVisitor<IExpression, Procedure>() {
        @Override
        public IConstant visit(BooleanLiteral node, Procedure proc)
        {
            return node;
        }
        
        @Override
        public IExpression visit(CharLiteral node, Procedure option)
        {
            return node;
        }
        
        @Override
        public IExpression visit(IndexedVariable node, Procedure proc)
        {
            return node;
        }
        
        @Override
        public IExpression visit(InfixOperation node, Procedure proc)
        {
            final IExpression l = node.getLeft().accept(this, proc);
            if (l.isConstant())
            {
                node.setLeft(l);
            }
            final IExpression r = node.getRight().accept(this, proc);
            if (r.isConstant())
            {
                node.setRight(r);
            }
            return node.getOp().eval(node);
        }
        
        @Override
        public IConstant visit(IntegerLiteral node, Procedure proc)
        {
            return node;
        }
        
        @Override
        public IExpression visit(PrefixOperation node, Procedure proc)
        {
            final IExpression res = node.getOperand().accept(this, proc);
            if (!res.isConstant())
            {
                return node;
            }
            
            node.setOperand(res);
            
            return node.getOp().eval(node);
        }
        
        @Override
        public IExpression visit(PureVariable node, Procedure proc)
        {
            return node;
        }
        
        @Override
        public IConstant visit(StringLiteral node, Procedure proc)
        {
            return node;
        }
    };
}
