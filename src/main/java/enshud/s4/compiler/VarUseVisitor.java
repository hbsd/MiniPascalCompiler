package enshud.s4.compiler;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import enshud.pascal.Procedure;
import enshud.pascal.ast.IVisitor;
import enshud.pascal.ast.expression.*;
import enshud.pascal.ast.statement.*;


public class VarUseVisitor implements IVisitor<IStatement, Procedure>
{
    public long                  changed         = 0;
    private boolean              enable_optimize = true;
    private final Set<Procedure> memo            = new HashSet<>();
    private final Set<String>    vars_unused;
    
    public VarUseVisitor(VarUseVisitor vuv)
    {
        this.vars_unused = new HashSet<>(vuv.vars_unused);
        enable_optimize = vuv.enable_optimize;
    }
    
    public VarUseVisitor(Set<String> vars)
    {
        this.vars_unused = new HashSet<>(vars);
    }
    
    public VarUseVisitor()
    {
        this.vars_unused = new HashSet<>();
    }
    
    @Override
    public IStatement visit(Procedure node, Procedure proc)
    {
        if (!memo.contains(node))
        {
            final Set<String> vs = node.getVars().stream()
                .flatMap(v -> ValueTable.calcName(v).stream())
                .collect(Collectors.toSet());
            vars_unused.addAll(vs);
            memo.add(node);
            node.getBody().accept(this, node);
        }
        return null;
    }
    
    @Override
    public IStatement visit(BooleanLiteral node, Procedure proc)
    {
        return null;
    }
    
    @Override
    public IStatement visit(CharLiteral node, Procedure proc)
    {
        return null;
    }
    
    @Override
    public IStatement visit(IndexedVariable node, Procedure proc)
    {
        vars_unused.removeAll(ValueTable.calcName(node));
        return null;
    }
    
    @Override
    public IStatement visit(InfixOperation node, Procedure proc)
    {
        node.getRight().accept(this, proc);
        node.getLeft().accept(this, proc);
        return null;
    }
    
    @Override
    public IStatement visit(IntegerLiteral node, Procedure proc)
    {
        return null;
    }
    
    @Override
    public IStatement visit(PrefixOperation node, Procedure proc)
    {
        node.getOperand().accept(this, proc);
        return null;
    }
    
    @Override
    public IStatement visit(PureVariable node, Procedure proc)
    {
        vars_unused.removeAll(ValueTable.calcName(node));
        return null;
    }
    
    @Override
    public IStatement visit(StringLiteral node, Procedure proc)
    {
        return null;
    }
    
    @Override
    public IStatement visit(AssignStatement node, Procedure proc)
    {
        final List<String> nms = ValueTable.calcName(node.getLeft());
        final boolean f = nms.size() == 1 && vars_unused.contains(nms.get(0));
        //if (enable_optimize)
        //    System.out.println(node.getLeft().getLine() + ":" + node.getLeft() + ":" + vars_unused + "<" + nms);
        vars_unused.addAll(ValueTable.calcName(node.getLeft()));
        node.getRight().accept(this, proc);
        return f? null: node;
    }
    
    @Override
    public IStatement visit(CompoundStatement node, Procedure proc)
    {
        for (int i = node.size() - 1; i >= 0; --i)
        {
            final IStatement s = node.get(i).accept(this, proc);
            if (s == null && enable_optimize)
            {
                ++changed;
                //System.out.print(node.get(i).getLine() + " =>");
                //node.get(i).println();
                node.remove(i);
            }
        }
        return node;
    }
    
    @Override
    public IStatement visit(IfElseStatement node, Procedure proc)
    {
        final VarUseVisitor vuv = new VarUseVisitor(this);
        node.getIfPart().getThen().accept(vuv, proc);
        node.getElse().accept(this, proc);
        vars_unused.retainAll(vuv.vars_unused);
        node.getCond().accept(this, proc);
        return node;
    }
    
    @Override
    public IStatement visit(IfStatement node, Procedure proc)
    {
        final VarUseVisitor vuv = new VarUseVisitor(this);
        node.getThen().accept(vuv, proc);
        vars_unused.retainAll(vuv.vars_unused);
        node.getCond().accept(this, proc);
        return node;
    }
    
    @Override
    public IStatement visit(ProcCallStatement node, Procedure proc)
    {
        if (proc.getDepth() <= node.getCalledProc().getDepth() && proc != node.getCalledProc())
        {
            node.getCalledProc().accept(this, proc);
        }
        else
        {
            vars_unused.removeAll(
                proc.getAvailableVars().stream()
                    .flatMap(v -> ValueTable.calcName(v).stream())
                    .collect(Collectors.toSet())
            );
        }
        IntStream.rangeClosed(1, node.getArgs().size())
            .forEachOrdered(i -> node.getArgs().get(node.getArgs().size() - i).accept(this, proc));
        return node;
    }
    
    @Override
    public IStatement visit(ReadStatement node, Procedure proc)
    {
        final int s = node.getVariables().size();
        IntStream.rangeClosed(1, s)
            .forEachOrdered(i -> node.getVariables().get(s - i).accept(this, proc));
        return node;
    }
    
    @Override
    public IStatement visit(WhileStatement node, Procedure proc)
    {
        //System.out.println(">>>>");
        final VarUseVisitor vuv = new VarUseVisitor(this);
        vuv.enable_optimize = false;
        node.getCond().accept(vuv, proc);
        node.getStatement().accept(vuv, proc);
        vars_unused.retainAll(vuv.vars_unused);
        //System.out.println("<<<<");
        
        node.getCond().accept(this, proc);
        final IStatement s = node.getStatement().accept(this, proc);
        
        if (s == null && enable_optimize)
        {
            ++changed;
            //System.out.print(node.getStatement().getLine() + " >>");
            //node.getStatement().println();
            return null;
        }
        return node;
    }
    
    @Override
    public IStatement visit(WriteStatement node, Procedure proc)
    {
        IntStream.rangeClosed(1, node.getExpressions().size())
            .forEachOrdered(i -> node.getExpressions().get(node.getExpressions().size() - i).accept(this, proc));
        return node;
    }
}
