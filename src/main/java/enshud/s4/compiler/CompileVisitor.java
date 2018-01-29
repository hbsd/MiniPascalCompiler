package enshud.s4.compiler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.stream.IntStream;

import enshud.pascal.Procedure;
import enshud.pascal.ast.IVisitor;
import enshud.pascal.ast.TemplateVisitor;
import enshud.pascal.ast.expression.*;
import enshud.pascal.ast.statement.*;
import enshud.pascal.type.ArrayType;
import enshud.pascal.type.BasicType;


public class CompileVisitor implements IVisitor<Object, Procedure>
{
    private final Casl2Code      code       = new Casl2Code();
    private final LabelGenerator l_gen      = new LabelGenerator();
    private boolean              use_mul    = false;
    private boolean              use_divmod = false;
    private boolean              use_rdint  = false;
    private boolean              use_rdch   = false;
    private boolean              use_rdstr  = false;
    private boolean              use_rdln   = false;
    private boolean              use_wrint  = false;
    private boolean              use_wrch   = false;
    private boolean              use_wrstr  = false;
    private boolean              use_wrln   = false;
    
    public void appendLibcas(final String file_name)
    {
        if (use_mul || use_rdint)
        {
            appendLibcas(file_name, "data/lib/mul.cas");
        }
        if (use_divmod || use_wrint)
        {
            appendLibcas(file_name, "data/lib/divmod.cas");
        }
        if (use_rdint)
        {
            appendLibcas(file_name, "data/lib/rdint.cas");
        }
        if (use_rdch)
        {
            appendLibcas(file_name, "data/lib/rdch.cas");
        }
        if (use_rdstr)
        {
            appendLibcas(file_name, "data/lib/rdstr.cas");
        }
        if (use_rdln)
        {
            appendLibcas(file_name, "data/lib/rdln.cas");
        }
        if (use_wrint)
        {
            appendLibcas(file_name, "data/lib/wrint.cas");
        }
        if (use_wrch)
        {
            appendLibcas(file_name, "data/lib/wrch.cas");
        }
        if (use_wrstr)
        {
            appendLibcas(file_name, "data/lib/wrstr.cas");
        }
        if (use_wrln)
        {
            appendLibcas(file_name, "data/lib/wrln.cas");
        }
    }
    
    private static void appendLibcas(final String fileName, final String libFileName)
    {
        try
        {
            final List<String> libcas = Files.readAllLines(Paths.get(libFileName));
            Files.write(Paths.get(fileName), libcas, StandardOpenOption.APPEND);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    
    public Casl2Code getCode()
    {
        return code;
    }
    
    @Override
    public Object visit(Procedure node, Procedure option)
    {
        if (node.isRoot())
        {
            code.add("START", "PROGRAM", "; proc " + node.getQualifiedName());
            code.add("XOR", "", "; buffer length", "GR6", "GR6");
            code.add("LAD", "", "; buffer address", "GR7", "BUF");
            code.add("PUSH", node.getId(), "; save parent's frame pointer", "0", "GR5");
        }
        else
        {
            code.add("START", node.getId(), "; proc " + node.getQualifiedName());
            code.add("PUSH", "", "; save parent's frame pointer", "0", "GR5");
        }
        
        code.add("LAD", "", "; set my frame pointer", "GR5", "1", "GR8");
        
        if (node.getLocalDecls().length() > 0)
        {
            code.add("LAD", "", "; reserve local variables", "GR8", "" + (-node.getLocalDecls().getAllSize()), "GR8");
        }
        
        node.getBody().accept(this, node);
        
        compileReturn(node);
        if (node.isRoot())
        {
            code.add("DS", "BUF", "; buffer for write", "256");
        }
        
        code.add("END", "", "; proc " + node.getQualifiedName());
        node.getChildren().forEach(sub -> sub.accept(this, node));
        return null;
    }
    
    private void compileReturn(Procedure node)
    {
        code.add("LD", "", "; point to return address", "GR8", "GR5");
        code.add("LD", "", "; restore parent's frame pointer", "GR5", "-1", "GR5");
        code.add("RET", "", "");
    }
    
    @Override
    public Object visit(BooleanLiteral node, Procedure option)
    {
        code.addLoadImm("GR2", node.getValue().getInt());
        return null;
    }
    
    @Override
    public Object visit(CharLiteral node, Procedure option)
    {
        code.addLoadImm("GR2", node.getValue().getInt());
        return null;
    }
    
    @Override
    public Object visit(IndexedVariable node, Procedure proc)
    {
        compileIndexedVariableForData(node, proc);
        return null;
    }
    
    public void compileIndexedVariableForData(IndexedVariable node, Procedure proc)
    {
        compileIndexedVariableImpl(node, proc);
        code.add("LD", "", "", "GR2", "0", "GR2");
    }
    
    public void compileIndexedVariableForAddr(IndexedVariable node, Procedure proc)
    {
        compileIndexedVariableImpl(node, proc);
    }
    
    private void compileIndexedVariableImpl(IndexedVariable node, Procedure proc)
    {
        code.add("", "", "; var " + node.getVar().getQualifiedName() + "[]");
        
        if (node.getIndex() instanceof IConstant)
        {
            final int idx = ((IConstant)node.getIndex()).getValue().getInt();
            if (node.getVar().getProc() == proc)
            {
                compileIndexedVariableLocalImpl(node, proc, "GR2", idx);
            }
            else
            {
                compileIndexedVariableOuterImpl(node, proc, "GR2", idx);
            }
        }
        else
        {
            node.getIndex().accept(this, proc);
            if (node.getVar().getProc() == proc)
            {
                compileIndexedVariableLocalImpl(node, proc, "GR1", 0);
            }
            else
            {
                compileIndexedVariableOuterImpl(node, proc, "GR1", 0);
            }
            code.add("ADDL", "", "; add index", "GR2", "GR1");
        }
    }
    
    private void compileIndexedVariableLocalImpl(IndexedVariable node, Procedure proc, String gr, int idx)
    {
        final int align = node.getVar().getAlignment();
        final int max = ((ArrayType)node.getVar().getType()).getMax();
        code.add("LAD", "", "", gr, "" + (-align - 2 - max + idx), "GR5");
    }
    
    /// proc == null? Local Var: Outer Var
    private void compileIndexedVariableOuterImpl(IndexedVariable node, Procedure proc, String gr, int idx)
    {
        final int depth_diff = proc.getDepth() - node.getVar().getProc().getDepth() - 1;
        loadStaticLink(gr, depth_diff);
        
        final int align = node.getVar().getAlignment();
        final int max = ((ArrayType)node.getVar().getType()).getMax();
        code.addAddlImm(gr, -align - 2 - max + idx);
    }
    
    @Override
    public Object visit(InfixOperation node, Procedure proc)
    {
        if (node.getLeft() instanceof IConstant)
        {
            if (node.getRight() instanceof IConstant)
            {
                code.addLoadImm("GR2", ((IConstant)node.getRight()).getValue().getInt());
            }
            else
            {
                node.getRight().accept(this, proc);
            }
            code.addLoadImm("GR1", ((IConstant)node.getLeft()).getValue().getInt());
        }
        else
        {
            node.getLeft().accept(this, proc);
            if (node.getRight() instanceof IConstant)
            {
                code.add("LD", "", "", "GR1", "GR2");
                code.addLoadImm("GR2", ((IConstant)node.getRight()).getValue().getInt());
            }
            else
            {
                code.add("PUSH", "", "", "0", "GR2");
                node.getRight().accept(this, proc);
                code.add("POP", "", "", "GR1");
            }
        }
        node.getOp().compile(code, l_gen);
        switch (node.getOp())
        {
        case MUL:
            use_mul = true;
            break;
        case DIV:
        case MOD:
            use_divmod = true;
            break;
        default:
            break;
        }
        return null;
    }
    
    @Override
    public Object visit(IntegerLiteral node, Procedure proc)
    {
        code.addLoadImm("GR2", node.getValue().getInt());
        return null;
    }
    
    @Override
    public Object visit(PrefixOperation node, Procedure proc)
    {
        node.getOperand().accept(this, proc);
        node.getOp().compile(code, l_gen);
        return null;
    }
    
    @Override
    public Object visit(PureVariable node, Procedure proc)
    {
        if (node.getType().isBasicType())
        {
            compilePureVariableForData(node, proc);
        }
        else
        {
            compilePureVariableForAddr(node, proc);
        }
        return null;
    }
    
    public void compilePureVariableForData(PureVariable node, Procedure proc)
    {
        compilePureVariableImpl(node, "LD", proc);
    }
    
    public void compilePureVariableForAddr(PureVariable node, Procedure proc)
    {
        compilePureVariableImpl(node, "LAD", proc);
    }
    
    private void compilePureVariableImpl(PureVariable node, String inst, Procedure proc)
    {
        if (node.isParam())
        {
            code.add("", "", "; param " + node.getVar().getQualifiedName());
            final int align = node.getVar().getAlignment();
            if (node.getVar().getProc() == proc)
            {
                code.add(inst, "", "", "GR2", "" + (align + 2), "GR5");
            }
            else
            {
                final int depth_diff = proc.getDepth() - node.getVar().getProc().getDepth();
                loadStaticLink("GR2", depth_diff - 1);
                code.add(inst, "", "", "GR2", "" + (align + 2), "GR2");
            }
        }
        else
        {
            code.add("", "", "; var " + node.getVar().getQualifiedName());
            if (node.getVar().getProc() == proc)
            {
                compilePureVariableForLocal(node, null, inst, "GR5");
            }
            else
            {
                compilePureVariableForLocal(node, proc, inst, "GR1");
            }
        }
    }
    
    /// proc == null? Local Var: Outer Var
    private void compilePureVariableForLocal(PureVariable node, Procedure proc, String inst, String gr)
    {
        final int align = node.getVar().getAlignment();
        
        if (proc != null) // go back stack frame by static link
        {
            final int depth_diff = proc.getDepth() - node.getVar().getProc().getDepth() - 1;
            loadStaticLink(gr, depth_diff);
        }
        
        if (node.getVar().getType().isArrayType())
        {
            final int len = ((ArrayType)node.getVar().getType()).getSize();
            code.add(inst, "", "", "GR2", "" + (-align - 1 - len), gr);
            code.addLoadImm("GR1", node.getVar().getType().getSize()); // array
                                                                       // length
        }
        else
        {
            code.add(inst, "", "", "GR2", "" + (-align - 2), gr);
        }
    }
    
    @Override
    public Object visit(StringLiteral node, Procedure proc)
    {
        code.add("LAD", "", "", "GR2", "='" + node.toString() + "'");
        code.addLoadImm("GR1", node.length());
        return null;
    }
    
    @Override
    public Object visit(AssignStatement node, Procedure proc)
    {
        node.getLeft().accept(address_visitor, proc);
        if (node.getRight() instanceof IConstant)
        {
            code.add("LD", "", "", "GR1", "GR2");
            code.addLoadImm("GR2", ((IConstant)node.getRight()).getValue().getInt());
        }
        else
        {
            code.add("PUSH", "", "", "0", "GR2");
            node.getRight().accept(this, proc);
            code.add("POP", "", "", "GR1");
        }
        code.add("ST", "", "", "GR2", "0", "GR1");
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
        
        code.add("", "", "; start of IF-ELSE");
        node.getCond().accept(this, proc);
        
        code.add("LD", "", "; set ZF", "GR2", "GR2");
        code.add("JZE", "", "; branch of IF-ELSE", "E" + label);
        
        node.getThen().accept(this, proc);
        code.add("JUMP", "", "", "F" + label);
        
        code.add("NOP", "E" + label, "; else of IF-ELSE");
        node.getElse().accept(this, proc);
        
        code.add("NOP", "F" + label, "; end of IF-ELSE");
        return null;
    }
    
    @Override
    public Object visit(IfStatement node, Procedure proc)
    {
        final int label = l_gen.next();
        
        code.add("", "", "; start of IF");
        node.getCond().accept(this, proc);
        
        code.add("LD", "", "; set ZF", "GR2", "GR2");
        code.add("JZE", "", "; branch of IF", "F" + label);
        
        node.getThen().accept(this, proc);
        
        code.add("NOP", "F" + label, "; end of IF");
        return null;
    }
    
    @Override
    public Object visit(ProcCallStatement node, Procedure proc)
    {
        compileArguments(node, proc);
        compileStaticFramePointer(node, proc);
        
        // f(code, proc.getName().charAt(0), '[');
        
        code.add("CALL", "", "; proc " + node.getCalledProc().getQualifiedName(), node.getCalledProc().getId());
        
        // f(code, proc.getName().charAt(0), ']');
        
        // remove rest child frame
        code.addAddlImm("GR8", node.getArgs().size() + 1);
        return null;
    }
    
    private void compileArguments(ProcCallStatement node, Procedure proc)
    {
        for (int i = node.getArgs().size() - 1; i >= 0; --i)
        {
            final IExpression e = node.getArgs().get(i);
            if (e instanceof IConstant)
            {
                code.add("PUSH", "", "", "" + ((IConstant)e).getValue().getInt());
            }
            else
            {
                node.getArgs().get(i).accept(this, proc);
                code.add("PUSH", "", "", "0", "GR2");
            }
        }
    }
    
    private void compileStaticFramePointer(ProcCallStatement node, Procedure proc)
    {
        final int my_depth = proc.getDepth();
        final int your_depth = node.getCalledProc().getDepth();
        if (my_depth + 1 == your_depth) // child
        {
            code.add("PUSH", "", "", "0", "GR5");
        }
        else if (my_depth >= your_depth) // ancestor or recursive call
        {
            loadStaticLink("GR2", my_depth - your_depth);
            
            code.add("PUSH", "", "", "0", "GR2");
        }
        else
        {
            new Exception(proc.getQualifiedName() + " cannot call " + node.getCalledProc().getQualifiedName());
        }
    }
    
    private void loadStaticLink(String gr, int diff)
    {
        code.add("LD", "", "", gr, "1", "GR5");
        IntStream.range(0, diff)
            .forEachOrdered(i -> code.add("LD", "", "", gr, "1", gr));
    }
    
    @Override
    public Object visit(ReadStatement node, Procedure proc)
    {
        if (node.getVariables().isEmpty())
        {
            use_rdln = true;
            code.add("CALL", "", "", "RDLN");
            return null;
        }
        
        node.getVariables().forEach(
            v -> {
                v.accept(address_visitor, proc);
                if (v.getType() == BasicType.CHAR)
                {
                    use_rdch = true;
                    code.add("CALL", "", "", "RDCH");
                }
                else if (v.getType() == BasicType.INTEGER)
                {
                    use_rdint = true;
                    code.add("CALL", "", "", "RDINT");
                }
                else if (v.getType().isArrayOf(BasicType.CHAR))
                {
                    use_rdstr = true;
                    code.add("CALL", "", "", "RDSTR");
                }
                else
                {
                    assert false: "type error";
                }
            }
        );
        return null;
    }
    
    @Override
    public Object visit(WhileStatement node, Procedure proc)
    {
        final int label = l_gen.next();
        
        code.add("NOP", "C" + label, "; start of WHILE");
        
        if (!node.isInfiniteLoop())
        {
            node.getCond().accept(this, proc);
            
            code.add("LD", "", "", "GR2", "GR2");
            code.add("JZE", "", "; branch of WHILE", "F" + label);
        }
        
        node.getStatement().accept(this, proc);
        code.add("JUMP", "", "", "C" + label);
        
        if (!node.isInfiniteLoop())
        {
            code.add("NOP", "F" + label, "; end of WHILE");
        }
        return null;
    }
    
    @Override
    public Object visit(WriteStatement node, Procedure proc)
    {
        for (final IExpression e: node.getExpressions())
        {
            e.accept(this, proc);
            
            if (e.getType() == BasicType.CHAR)
            {
                use_wrch = true;
                code.add("CALL", "", "", "WRTCH");
            }
            else if (e.getType() == BasicType.INTEGER)
            {
                use_wrint = true;
                code.add("CALL", "", "", "WRTINT");
            }
            else if (e.getType().isArrayOf(BasicType.CHAR))
            {
                use_wrstr = true;
                code.add("CALL", "", "", "WRTSTR");
            }
            else
            {
                assert false: "type error: (" + e.getLine() + "," + e.getColumn() + ")" + e.getType();
            }
        }
        use_wrln = true;
        code.add("CALL", "", "", "WRTLN");
        return null;
    }
    
    final IVisitor<Object, Procedure> address_visitor = new TemplateVisitor<Object, Procedure>() {
        @Override
        public Object visit(IndexedVariable node, Procedure proc)
        {
            compileIndexedVariableForAddr(node, proc);
            return null;
        }
        
        @Override
        public Object visit(PureVariable node, Procedure proc)
        {
            compilePureVariableForAddr(node, proc);
            return null;
        }
        
    };
}
