package enshud.s4.compiler;

import enshud.pascal.Procedure;
import enshud.pascal.ast.TemplateVisitor;
import enshud.pascal.ast.expression.*;
import enshud.pascal.ast.statement.*;
import enshud.s4.compiler.tacode.BasicBlockList;
import enshud.s4.compiler.tacode.Nop;
import enshud.s4.compiler.tacode.TACode;
import enshud.s4.compiler.tacode.TAValue;
import enshud.s4.compiler.tacode.io.*;
import enshud.s4.compiler.tacode.jmp.Jump;
import enshud.s4.compiler.tacode.jmp.JumpFalse;
import enshud.s4.compiler.tacode.ldst.*;
import enshud.s4.compiler.tacode.op.*;
import enshud.s4.compiler.tacode.proc.*;


public class IntmCodeVisitor extends TemplateVisitor<Object, Procedure>
{
    private final TACode         code  = new TACode();
    private final LabelGenerator l_gen = new LabelGenerator();
    private final LabelGenerator v_gen = new LabelGenerator();
    
    public TACode getCode()
    {
        return code;
    }
    
    @Override
    public Object visit(Procedure node, Procedure option)
    {
        node.getBody().accept(this, node);
        code.add(new Return());
        node.setBBList(BasicBlockList.fromInsts(code));
        
        node.getChildren().forEach(sub -> sub.accept(new IntmCodeVisitor(), node));
        return null;
    }
    
    @Override
    public Object visit(AssignStatement node, Procedure proc)
    {
        final String r = node.getRight().accept(exp_vtr, proc);
        exp_vtr.compileStoreVariable(node.getLeft(), proc, TAValue.variable(r));
        return null;
    }
    
    @Override
    public Object visit(CompoundStatement node, Procedure proc)
    {
        node.forEach(stm -> stm.accept(this, proc));
        return null;
    }
    
    @Override
    public Object visit(IfElseStatement node, Procedure proc)
    {
        final int label = l_gen.next();
        
        final String c = node.getCond().accept(exp_vtr, proc);
        code.add(new JumpFalse(TAValue.variable(c), "E" + label));
        
        node.getThen().accept(this, proc);
        code.add(new Jump("F" + label));
        
        code.add(new Nop("E" + label));
        node.getElse().accept(this, proc);
        
        code.add(new Nop("F" + label));
        return null;
    }
    
    @Override
    public Object visit(IfStatement node, Procedure proc)
    {
        final int label = l_gen.next();
        
        final String c = node.getCond().accept(exp_vtr, proc);
        code.add(new JumpFalse(TAValue.variable(c), "F" + label));
        
        node.getThen().accept(this, proc);
        
        code.add(new Nop("F" + label));
        return null;
    }
    
    @Override
    public Object visit(ProcCallStatement node, Procedure proc)
    {
        for (int i = node.getArgs().size() - 1; i >= 0; --i)
        {
            final String r = node.getArgs().get(i).accept(exp_vtr, proc);
            code.add(new Arg(TAValue.variable(r)));
        }
        
        code.add(new Call(node.getCalledProc()));
        return null;
    }
    
    @Override
    public Object visit(ReadStatement node, Procedure proc)
    {
        if (node.getVariables().isEmpty())
        {
            code.add(new ReadLn());
            return null;
        }
        
        node.getVariables().forEach(
            v -> {
                final String n = "t" + v_gen.next();
                code.add(new Read(n));
                exp_vtr.compileStoreVariable(v, proc, TAValue.variable(n));
            }
        );
        return null;
    }
    
    @Override
    public Object visit(WhileStatement node, Procedure proc)
    {
        final int label = l_gen.next();
        code.add(new Nop("C" + label));
        
        final String c = node.getCond().accept(exp_vtr, proc);
        code.add(new JumpFalse(TAValue.variable(c), "F" + label));
        
        node.getStatement().accept(this, proc);
        code.add(new Jump("C" + label));
        
        code.add(new Nop("F" + label));
        return null;
    }
    
    @Override
    public Object visit(WriteStatement node, Procedure proc)
    {
        for (final IExpression e: node.getExpressions())
        {
            final String v = e.accept(exp_vtr, proc);
            code.add(new Write(TAValue.variable(v)));
        }
        code.add(new WriteLn());
        return null;
    }
    
    private final ExpVtr exp_vtr = new ExpVtr();
    private final class ExpVtr extends TemplateVisitor<String, Procedure>
    {
        @Override
        public String visit(BooleanLiteral node, Procedure option)
        {
            final String n = "t" + v_gen.next();
            code.add(new Copy(n, TAValue.constant(node.getValue())));
            return n;
        }
        
        @Override
        public String visit(CharLiteral node, Procedure option)
        {
            final String n = "t" + v_gen.next();
            code.add(new Copy(n, TAValue.constant(node.getValue())));
            return n;
        }
        
        @Override
        public String visit(IndexedVariable node, Procedure proc)
        {
            final String idx = node.getIndex().accept(this, proc);
            final String n = "t" + v_gen.next();
            code.add(new LoadIdxLcl(n, node.getVar(), TAValue.variable(idx)));
            return n;
        }
        
        private void compileStoreIndexedVariable(IndexedVariable node, Procedure proc, TAValue val)
        {
            final String idx = node.getIndex().accept(this, proc);
            code.add(new StoreIdxLcl(node.getVar(), TAValue.variable(idx), val));
        }
        
        @Override
        public String visit(InfixOperation node, Procedure proc)
        {
            final TAValue l = TAValue.variable(node.getLeft().accept(this, proc));
            final TAValue r = TAValue.variable(node.getRight().accept(this, proc));
            final String n = "t" + v_gen.nextInt();
            code.add(new Binary(n, l, node.getOp(), r));
            return n;
        }
        
        @Override
        public String visit(IntegerLiteral node, Procedure proc)
        {
            final String n = "t" + v_gen.next();
            code.add(new Copy(n, TAValue.constant(node.getValue())));
            return n;
        }
        
        @Override
        public String visit(PrefixOperation node, Procedure proc)
        {
            final String opd = node.getOperand().accept(this, proc);
            final String n = "t" + v_gen.next();
            code.add(new Unary(n, node.getOp(), TAValue.variable(opd)));
            return n;
        }
        
        @Override
        public String visit(PureVariable node, Procedure proc)
        {
            final String n = "t" + v_gen.next();
            code.add(
                (node.isParam())
                        ? new LoadArg(n, node.getVar())
                        : new LoadLcl(n, node.getVar())
            );
            return n;
        }
        
        private void compileStorePureVariable(PureVariable node, Procedure proc, TAValue val)
        {
            code.add(
                (node.isParam())
                        ? new StoreArg(node.getVar(), val)
                        : new StoreLcl(node.getVar(), val)
            );
        }
        
        @Override
        public String visit(StringLiteral node, Procedure proc)
        {
            final String n = "t" + v_gen.next();
            code.add(new Copy(n, TAValue.constant(node.getValue())));
            return n;
        }
        
        public void compileStoreVariable(IVariable node, Procedure proc, TAValue val)
        {
            if(node instanceof PureVariable)
            {
                compileStorePureVariable((PureVariable)node, proc, val);
            }
            else if(node instanceof IndexedVariable)
            {
                compileStoreIndexedVariable((IndexedVariable)node, proc, val);
            }
            else
            {
                throw new IllegalArgumentException();
            }
        }
    };
}
