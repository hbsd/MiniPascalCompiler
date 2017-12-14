package enshud.pascal.ast.expression;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import enshud.pascal.Procedure;
import enshud.pascal.QualifiedVariable;
import enshud.pascal.ast.ILiteral;
import enshud.pascal.ast.statement.ProcCallStatement;
import enshud.pascal.type.ArrayType;
import enshud.pascal.type.IType;
import enshud.pascal.type.UnknownType;
import enshud.s3.checker.Checker;
import enshud.s4.compiler.Casl2Code;
import enshud.s4.compiler.LabelGenerator;


public class PureVariable implements IVariable, ILiteral
{
    private final Identifier name;
    private IType            type;
    private QualifiedVariable         var_referenced;
    private boolean          is_param;
    
    public PureVariable(Identifier name)
    {
        this.name = Objects.requireNonNull(name);
        type = null;
    }
    
    @Override
    public Identifier getName()
    {
        return name;
    }
    
    @Override
    public IType getType()
    {
        return type;
    }
    
    @Override
    public int getLine()
    {
        return name.getLine();
    }
    
    @Override
    public int getColumn()
    {
        return name.getColumn();
    }
    
    @Override
    public IType check(Procedure proc, Checker checker)
    {
        final String nm = getName().toString();
        
        final Optional<QualifiedVariable> param = proc.getParam(nm);
        
        if (param.isPresent())
        {
            var_referenced = param.get();
            is_param = true;
            type = param.get().getType();
        }
        else
        {
            final Optional<QualifiedVariable> var = proc.getVar(nm);
            type = var.map(v -> v.getType())
                .orElse(UnknownType.UNKNOWN);
            if (var.isPresent())
            {
                var_referenced = var.get();
                is_param = false;
                type = var.get().getType();
            }
            else
            {
                type = UnknownType.UNKNOWN;
                checkFuzzy(proc, checker);
            }
        }
        
        return type;
    }
    
    private void checkFuzzy(Procedure proc, Checker checker)
    {
        final String nm = getName().toString();
        final List<QualifiedVariable> vs = proc.searchForParamFuzzy(nm);
        vs.addAll(proc.searchForVarFuzzy(nm));
        
        checker.addErrorMessage(
            proc, getName(),
            "variable '" + nm + "' is not defined."
                    + (vs.isEmpty()? "": (" did you mean variable " + vs + "?"))
        );
    }
    
    @Override
    public IConstant preeval(Procedure proc)
    {
        return null;
    }
    
    @Override
    public void compile(Casl2Code code, Procedure proc, LabelGenerator l_gen)
    {
        if (getType().isBasicType())
        {
            compileForData(code, proc, l_gen);
        }
        else
        {
            compileForAddr(code, proc, l_gen);
        }
    }
    
    @Override
    public void compileForData(Casl2Code code, Procedure proc, LabelGenerator l_gen)
    {
        compileImpl("LD", code, proc, l_gen);
    }
    
    @Override
    public void compileForAddr(Casl2Code code, Procedure proc, LabelGenerator l_gen)
    {
        compileImpl("LAD", code, proc, l_gen);
    }
    
    private void compileImpl(String inst, Casl2Code code, Procedure proc, LabelGenerator l_gen)
    {
        if (is_param)
        {
            code.add("", "", "; param " + var_referenced.getQualifiedName());
            final int align = var_referenced.getAlignment();
            if(var_referenced.getProc() == proc)
            {
                code.add(inst, "", "", "GR2", "" + (align + 2), "GR5");
            }
            else
            {
                final int depth_diff = proc.getDepth() - var_referenced.getProc().getDepth();
                ProcCallStatement.loadStaticPointer(code, "GR2", depth_diff - 1);
                code.add(inst, "", "", "GR2", "" + (align + 2), "GR2");
            }
        }
        else
        {
            code.add("", "", "; var " + var_referenced.getQualifiedName());
            if (var_referenced.getProc() == proc)
            {
                compileForVariable(code, null, inst, "GR5");
            }
            else
            {
                compileForVariable(code, proc, inst, "GR1");
            }
        }
    }
    
    /// proc == null? Local Var: Outer Var
    private void compileForVariable(Casl2Code code, Procedure proc, String inst, String gr)
    {
        final int align = var_referenced.getAlignment();
        
        if (proc != null) // go back stack frame by static link
        {
            final int depth_diff = proc.getDepth() - var_referenced.getProc().getDepth() - 1;
            ProcCallStatement.loadStaticPointer(code, gr, depth_diff);
        }
        
        if (var_referenced.getType().isArrayType())
        {
            final int len = ((ArrayType)var_referenced.getType()).getSize();
            code.add(inst, "", "", "GR2", "" + (-align - 1 - len), gr);
            // array length
            if (inst.equals("LAD"))
            {
                code.add("LAD", "", "", "GR1", "" + var_referenced.getType().getSize());
            }
        }
        else
        {
            code.add(inst, "", "", "GR2", "" + (-align - 2), gr);
        }
    }
    
    @Override
    public void printHead(String indent, String msg)
    {
        ILiteral.super.printHead(indent, msg);
    }
    
    @Override
    public String toString()
    {
        return name.toString();
    }
}

