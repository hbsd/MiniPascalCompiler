package enshud.s4.compiler;

import java.util.HashSet;
import java.util.Set;

import enshud.pascal.Procedure;
import enshud.pascal.ast.IVisitor;
import enshud.pascal.ast.TemplateVisitor;
import enshud.pascal.ast.expression.*;
import enshud.pascal.ast.statement.*;


public class VarSurviveVisitor implements IVisitor<Object, Procedure>
{
    private final Set<Procedure> memo = new HashSet<>();
    private final ValueTable     vars;
    
    public VarSurviveVisitor(ValueTable vars)
    {
        this.vars = vars;
    }
    
    @Override
    public Object visit(Procedure node, Procedure proc)
    {
        if (!memo.contains(node))
        {
            memo.add(node);
            node.getBody().accept(this, node);
        }
        return null;
    }
    
    @Override
    public Object visit(BooleanLiteral node, Procedure proc)
    {
        return null;
    }
    
    @Override
    public Object visit(CharLiteral node, Procedure proc)
    {
        return null;
    }
    
    @Override
    public Object visit(IndexedVariable node, Procedure proc)
    {
        return null;
    }
    
    @Override
    public Object visit(InfixOperation node, Procedure proc)
    {
        node.getLeft().accept(this, proc);
        node.getRight().accept(this, proc);
        return null;
    }
    
    @Override
    public Object visit(IntegerLiteral node, Procedure proc)
    {
        return null;
    }
    
    @Override
    public Object visit(PrefixOperation node, Procedure proc)
    {
        node.getOperand().accept(this, proc);
        return null;
    }
    
    @Override
    public Object visit(PureVariable node, Procedure proc)
    {
        return null;
    }
    
    @Override
    public Object visit(StringLiteral node, Procedure proc)
    {
        return null;
    }
    
    @Override
    public Object visit(AssignStatement node, Procedure proc)
    {
        node.getLeft().accept(add_vtr, proc);
        node.getRight().accept(this, proc);
        return null;
    }
    
    @Override
    public Object visit(CompoundStatement node, Procedure proc)
    {
        node.forEach(s -> s.accept(this, proc));
        return null;
    }
    
    @Override
    public Object visit(IfElseStatement node, Procedure proc)
    {
        node.getIfPart().getThen().accept(this, proc);
        node.getElse().accept(this, proc);
        return null;
    }
    
    @Override
    public Object visit(IfStatement node, Procedure proc)
    {
        node.getThen().accept(this, proc);
        return null;
    }
    
    @Override
    public Object visit(ProcCallStatement node, Procedure proc)
    {
        node.getArgs().forEach(a -> a.accept(this, proc));
        if (proc.getDepth() <= node.getCalledProc().getDepth() && proc != node.getCalledProc())
        {
            node.getCalledProc().accept(this, proc);
        }
        else
        {
            vars.clear();
        }
        return null;
    }
    
    @Override
    public Object visit(ReadStatement node, Procedure proc)
    {
        node.getVariables().forEach(v -> v.accept(add_vtr, proc));
        return null;
    }
    
    @Override
    public Object visit(WhileStatement node, Procedure proc)
    {
        node.getStatement().accept(this, proc);
        return null;
    }
    
    @Override
    public Object visit(WriteStatement node, Procedure proc)
    {
        node.getExpressions().forEach(e -> e.accept(this, proc));
        return null;
    }
    
    private IVisitor<Object, Procedure> add_vtr = new TemplateVisitor<Object, Procedure>() {
        @Override
        public Object visit(IndexedVariable node, Procedure proc)
        {
            vars.put(node, null);
            return null;
        }
        
        @Override
        public Object visit(PureVariable node, Procedure proc)
        {
            vars.put(node, null);
            return null;
        }
    };
}
