package enshud.s4.compiler;

import java.util.HashMap;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;

import enshud.pascal.Procedure;
import enshud.pascal.ast.IASTNode;
import enshud.pascal.ast.IVisitor;
import enshud.pascal.ast.TemplateVisitor;
import enshud.pascal.ast.expression.*;
import enshud.pascal.ast.statement.*;
import enshud.pascal.type.ArrayType;
import enshud.pascal.type.BasicType;


public class OptimizeVisitor2 extends TemplateVisitor<IStatement, Procedure>
{
    public long                    changed = 0;
    private Map<String, IConstant> vtbl;
    
    private void add_change(IASTNode node)
    {
        ++changed;
        //System.out.println(node.getClass().getSimpleName());
    }
    
    private OptimizeVisitor2(Map<String, IConstant> vtbl)
    {
        this.vtbl = new HashMap<>(vtbl);
    }
    
    public OptimizeVisitor2()
    {
        this.vtbl = new HashMap<>();
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
        assign(node.getLeft(), (node.getRight() instanceof IConstant)? (IConstant)node.getRight(): null);
        return node;
    }
    
    private void assign(IVariable v, IConstant val)
    {
        String name = v.getQualifiedName();
        if (v instanceof PureVariable)
        {
            // System.out.println(name+" <- "+val);
            vtbl.put(name, val);
        }
        else
        {
            IndexedVariable iv = (IndexedVariable)v;
            IExpression idx = iv.getIndex();
            if (idx.isConstant())
            {
                // System.out.println(name + "[" +
                // ((IConstant)idx).getValue().getInt() + "] <- "+val);
                vtbl.put(name + "[" + ((IConstant)idx).getValue().getInt() + "]", val);
            }
            else if (iv.getArrayType() instanceof ArrayType)
            {
                IntStream.rangeClosed(
                    ((ArrayType)iv.getArrayType()).getMin(),
                    ((ArrayType)iv.getArrayType()).getMax()
                )
                    .forEach(
                        i -> {
                            // System.out.println(name + "[" + i + "] <-
                            // "+null);
                            vtbl.put(name + "[" + i + "]", null);
                        }
                    );
            }
            else
            {
                throw new UnsupportedOperationException();
            }
        }
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
        
        final OptimizeVisitor2 tv1 = new OptimizeVisitor2(vtbl);
        node.getThen().accept(tv1, proc);
        changed += tv1.changed;
        
        final OptimizeVisitor2 tv2 = new OptimizeVisitor2(vtbl);
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
        else
        {
            this.vtbl.keySet().forEach(
                k -> {
                    final IConstant v1 = tv1.vtbl.get(k);
                    final IConstant v2 = tv2.vtbl.get(k);
                    if (v1 == null || v2 == null)
                    {
                        this.vtbl.put(k, null);
                    }
                    else if (v1.equals(v2))
                    {
                        this.vtbl.put(k, v1);
                    }
                    else
                    {
                        this.vtbl.put(k, null);
                    }
                }
            );
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
        
        final OptimizeVisitor2 tv = new OptimizeVisitor2(vtbl);
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
            this.vtbl.keySet().forEach(
                k -> {
                    final IConstant v1 = this.vtbl.get(k);
                    final IConstant v2 = tv.vtbl.get(k);
                    if (v1 == null || v2 == null)
                    {
                        this.vtbl.put(k, null);
                    }
                    else if (v1.equals(v2))
                    {
                        this.vtbl.put(k, v1);
                    }
                    else
                    {
                        this.vtbl.put(k, null);
                    }
                }
            );
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
        node.getVariables().forEach(v -> {
            v.accept(exp_vtr, proc);
            assign(v, null);
        });
        return node;
    }
    
    @Override
    public IStatement visit(WhileStatement node, Procedure proc)
    {
        node.accept(new VarSurviveVisitor(vtbl), proc);
        
        final IExpression res = node.getCond().accept(exp_vtr, proc);
        if (res != null)
        {
            add_change(node);
            node.setCond(res);
        }
        
        final IStatement stm = node.getStatement().accept(this, proc);
        if (stm == null)
        {
            add_change(node);
            node.setStatement(new CompoundStatement());
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
                        node.getExpressions().set(i, e);
                    }
                }
            );
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
            if (ci instanceof IConstant)
            {
                final IConstant c = vtbl.get(node.getQualifiedName() + "[" + ((IConstant)ci).getValue().getInt() + "]");
                // System.out.println(node.getQualifiedName() + "[" +
                // ((IConstant)ci).getValue().getInt() + "] -> " + c);
                if (c != null)
                {
                    //add_change(node);
                    return c;
                }
            }
            return null;
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
            if (node.getType() instanceof BasicType)
            {
                final IConstant c = vtbl.get(node.getQualifiedName());
                // System.out.println(node.getQualifiedName() + " -> " + c);
                if (c != null)
                {
                    //add_change(node);
                    return c;
                }
            }
            return null;
        }
        
        @Override
        public IExpression visit(StringLiteral node, Procedure proc)
        {
            return null;
        }
    };
}
