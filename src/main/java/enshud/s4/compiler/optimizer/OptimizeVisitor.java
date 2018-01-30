package enshud.s4.compiler.optimizer;

import java.util.ListIterator;
import java.util.stream.IntStream;

import enshud.pascal.PrefixOperator;
import enshud.pascal.Procedure;
import enshud.pascal.ast.IASTNode;
import enshud.pascal.ast.IVisitor;
import enshud.pascal.ast.TemplateVisitor;
import enshud.pascal.ast.expression.*;
import enshud.pascal.ast.statement.*;


public class OptimizeVisitor extends TemplateVisitor<IStatement, Procedure>
{
    public long        changed = 0;
    private ValueTable vtbl;       // value is IConstant or IVariable
    
    private void add_change(IASTNode node)
    {
        ++changed;
        // System.out.println(node.getClass().getSimpleName());
    }
    
    private OptimizeVisitor(ValueTable vtbl)
    {
        this.vtbl = new ValueTable(vtbl);
    }
    
    public OptimizeVisitor()
    {
        this.vtbl = new ValueTable();
    }
    
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
        node.getLeft().accept(exp_vtr, proc);
        final IExpression res = node.getRight().accept(exp_vtr, proc);
        if (res != null)
        {
            add_change(node);
            node.setRight(res);
        }
        if (node.getLeft().equals(node.getRight()))
        {
            return null;
        }
        vtbl.put(node.getLeft(), node.getRight());
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
                add_change(node);
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
        final IExpression res = node.getCond().accept(exp_vtr, proc);
        if (res != null)
        {
            add_change(node);
            node.getIfPart().setCond(res);
        }
        
        final OptimizeVisitor tv1 = new OptimizeVisitor(vtbl);
        node.getThen().accept(tv1, proc);
        changed += tv1.changed;
        
        final OptimizeVisitor tv2 = new OptimizeVisitor(vtbl);
        node.getElse().accept(tv2, proc);
        changed += tv2.changed;
        
        if (res instanceof BooleanLiteral)
        {
            add_change(node);
            if (((BooleanLiteral)res).getValue().getBool())
            {
                this.vtbl = tv1.vtbl;
                return node.getThen();
            }
            else
            {
                this.vtbl = tv2.vtbl;
                return node.getElse();
            }
        }
        
        this.vtbl = tv1.vtbl;
        this.vtbl.merge(tv2.vtbl);
        if (node.getThen().size() == 0)
        {
            return new IfStatement(new PrefixOperation(node.getCond(), PrefixOperator.NOT), node.getElse());
        }
        else if (node.getElse().size() == 0)
        {
            return new IfStatement(node.getCond(), node.getThen());
        }
        else
        {
            return node;
        }
    }
    
    @Override
    public IStatement visit(IfStatement node, Procedure proc)
    {
        final IExpression res = node.getCond().accept(exp_vtr, proc);
        if (res != null)
        {
            add_change(node);
            node.setCond(res);
        }
        
        final OptimizeVisitor tv = new OptimizeVisitor(vtbl);
        node.getThen().accept(tv, proc);
        changed += tv.changed;
        
        if (res instanceof BooleanLiteral)
        {
            add_change(node);
            if (((BooleanLiteral)res).getValue().getBool())
            {
                this.vtbl = tv.vtbl;
                return node.getThen();
            }
            else
            {
                return null;
            }
        }
        else
        {
            this.vtbl.merge(tv.vtbl);
            return node;
        }
    }
    
    @Override
    public IStatement visit(ProcCallStatement node, Procedure proc)
    {
        IntStream.range(0, node.getArgs().size()).forEach(
            i -> {
                final IExpression e = node.getArgs().get(i).accept(exp_vtr, proc);
                if (e != null)
                {
                    add_change(node);
                    node.getArgs().set(i, e);
                }
            }
        );
        node.getCalledProc().accept(new VarSurviveVisitor(vtbl), proc);
        return node;
    }
    
    @Override
    public IStatement visit(ReadStatement node, Procedure proc)
    {
        node.getVariables().forEach(
            v -> {
                v.accept(exp_vtr, proc);
                vtbl.put(v, null);
            }
        );
        return node;
    }
    
    @Override
    public IStatement visit(WhileStatement node, Procedure proc)
    {
        final ValueTable old = new ValueTable(vtbl);
        node.accept(new VarSurviveVisitor(vtbl), proc);
        
        final IExpression res = node.getCond().accept(exp_vtr, proc);
        if (res != null)
        {
            add_change(node);
            node.setCond(res);
        }
        
        final IStatement stm = node.getStatement().accept(this, proc);

        vtbl.merge(old);
        
        if (stm == null)
        {
            add_change(node);
            return null; // nothing to do in this While
            // node.setStatement(new CompoundStatement());
        }
        
        if (res instanceof BooleanLiteral)
        {
            add_change(node);
            if (((BooleanLiteral)res).getValue().getBool())
            {
                node.setIsInfiniteLoop(true);
            }
            else
            {
                return null;
            }
        }
        return node;
    }
    
    @Override
    public IStatement visit(WriteStatement node, Procedure proc)
    {
        IntStream.range(0, node.getExpressions().size())
            .forEach(
                i -> {
                    final IExpression e = node.getExpressions().get(i).accept(exp_vtr, proc);
                    if (e != null)
                    {
                        add_change(node);
                        node.getExpressions().set(
                            i,
                            (e instanceof IConstant)
                                    ? StringLiteral.create(e.toString())
                                    : e
                        );
                    }
                }
            );
        if (node.getExpressions().size() >= 2)
        {
            for (int i = 0; i < node.getExpressions().size() - 1; ++i)
            {
                final IExpression v1 = node.getExpressions().get(i);
                final IExpression v2 = node.getExpressions().get(i + 1);
                if ((v1 instanceof IConstant) && (v2 instanceof IConstant))
                {
                    node.getExpressions().set(i, StringLiteral.create(v1.toString() + v2));
                    node.getExpressions().remove(i + 1);
                    --i;
                }
            }
        }
        return node;
    }
    
    private final IVisitor<IExpression, Procedure> exp_vtr = new TemplateVisitor<IExpression, Procedure>() {
        @Override
        public IConstant visit(BooleanLiteral node, Procedure proc)
        {
            return null;
        }
        
        @Override
        public IExpression visit(CharLiteral node, Procedure option)
        {
            return null;
        }
        
        @Override
        public IExpression visit(IndexedVariable node, Procedure proc)
        {
            final IExpression ci = node.getIndex().accept(this, proc);
            if (ci != null)
            {
                add_change(node);
                node.setIndex(ci);
            }
            return vtbl.get(node);
        }
        
        @Override
        public IExpression visit(InfixOperation node, Procedure proc)
        {
            final IExpression l = node.getLeft().accept(this, proc);
            if (l != null)
            {
                add_change(node);
                node.setLeft(l);
            }
            final IExpression r = node.getRight().accept(this, proc);
            if (r != null)
            {
                add_change(node);
                node.setRight(r);
            }
            IExpression a = node.getOp().eval(node);
            // System.out.println(node.getLeft() + " " + node.getOp() + " " +
            // node.getRight() + " = " + a);
            return a;
        }
        
        @Override
        public IConstant visit(IntegerLiteral node, Procedure proc)
        {
            return null;
        }
        
        @Override
        public IExpression visit(PrefixOperation node, Procedure proc)
        {
            final IExpression res = node.getOperand().accept(this, proc);
            if (res != null)
            {
                add_change(node);
                node.setOperand(res);
            }
            return node.getOp().eval(node);
        }
        
        @Override
        public IExpression visit(PureVariable node, Procedure proc)
        {
            return vtbl.get(node);
        }
        
        @Override
        public IExpression visit(StringLiteral node, Procedure proc)
        {
            return null;
        }
    };
}
