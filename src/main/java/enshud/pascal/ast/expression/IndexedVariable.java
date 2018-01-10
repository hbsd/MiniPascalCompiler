package enshud.pascal.ast.expression;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import enshud.pascal.Procedure;
import enshud.pascal.QualifiedVariable;
import enshud.pascal.ast.ILiteral;
import enshud.pascal.ast.IVisitor;
import enshud.pascal.ast.Identifier;
import enshud.pascal.ast.statement.ProcCallStatement;
import enshud.pascal.type.ArrayType;
import enshud.pascal.type.IType;
import enshud.pascal.type.BasicType;
import enshud.pascal.type.UnknownType;
import enshud.s3.checker.Checker;
import enshud.s4.compiler.Casl2Code;
import enshud.s4.compiler.LabelGenerator;


public class IndexedVariable implements IVariable, ILiteral
{
    private final Identifier  name;
    private final IExpression index;
    private IType             type;
    private QualifiedVariable var_referenced;
    
    public IndexedVariable(Identifier name, IExpression index)
    {
        this.name = Objects.requireNonNull(name);
        this.index = Objects.requireNonNull(index);
        type = null;
    }
    
    @Override
    public Identifier getName()
    {
        return name;
    }
    
    public IExpression getIndex()
    {
        return index;
    }
    
    @Override
    public IType getType()
    {
        return ((ArrayType)type).getBasicType();
    }
    
    public IType getArrayType()
    {
        return type;
    }
    
    public void setArrayType(IType type)
    {
        this.type = type;
    }
    
    public QualifiedVariable getVar()
    {
        return var_referenced;
    }
    
    public void setVar(QualifiedVariable v)
    {
        var_referenced = v;
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
    public <T, U> T accept(IVisitor<T, U> visitor, U option)
    {
        return visitor.visitIndexedVariable(this, option);
    }
    
    @Override
    public IType check(Procedure proc, Checker checker)
    {
        final String nm = getName().toString();
        final Optional<QualifiedVariable> var = proc.getVar(nm);
        type = var.map(v -> v.getType())
            .orElse(UnknownType.UNKNOWN);
        
        if (!var.isPresent())
        {
            List<QualifiedVariable> vs = proc.searchForVarFuzzy(nm);
            checker.addErrorMessage(
                proc, getName(),
                "variable '" + nm + "' is not defined."
                        + (vs.isEmpty()? "": (" did you mean variable " + vs + "?"))
            );
        }
        else if (type.isBasicType())
        {
            checker.addErrorMessage(
                proc, this, "incompatible type: non-array type variable '" + nm + "' cannot have index."
            );
        }
        else
        {
            var_referenced = var.get();
        }
        
        checkIndex(proc, checker);
        return getType();
    }
    
    private void checkIndex(Procedure proc, Checker checker)
    {
        final IType idx_type = getIndex().check(proc, checker);
        if (!idx_type.isUnknown() && !idx_type.equals(BasicType.INTEGER))
        {
            checker.addErrorMessage(
                proc, getIndex(),
                "incompatible type: cannot use " + idx_type + " type as index of '" + getName() + "'. must be INTEGER."
            );
        }
    }
    
    @Override
    public IConstant preeval(Procedure proc)
    {
        return null;
    }
    
    @Override
    public void compile(Casl2Code code, Procedure proc, LabelGenerator l_gen)
    {
        compileForData(code, proc, l_gen);
    }
    
    @Override
    public void compileForData(Casl2Code code, Procedure proc, LabelGenerator l_gen)
    {
        compileImpl(code, proc, l_gen);
        code.add("LD", "", "", "GR2", "0", "GR1");
    }
    
    @Override
    public void compileForAddr(Casl2Code code, Procedure proc, LabelGenerator l_gen)
    {
        compileImpl(code, proc, l_gen);
        code.add("LD", "", "", "GR2", "GR1");
    }
    
    private void compileImpl(Casl2Code code, Procedure proc, LabelGenerator l_gen)
    {
        code.add("", "", "; var " + var_referenced.getQualifiedName() + "[]");
        
        getIndex().compile(code, proc, l_gen);
        if (var_referenced.getProc() == proc)
        {
            compileLocalImpl(code, proc);
        }
        else
        {
            compileOuterImpl(code, proc);
        }
        code.add("ADDL", "", "; add index", "GR1", "GR2");
    }
    
    private void compileLocalImpl(Casl2Code code, Procedure proc)
    {
        final int align = var_referenced.getAlignment();
        final int max = ((ArrayType)var_referenced.getType()).getMax();
        code.add("LAD", "", "", "GR1", "" + (-align - 2 - max), "GR5");
    }
    
    /// proc == null? Local Var: Outer Var
    private void compileOuterImpl(Casl2Code code, Procedure proc)
    {
        final int depth_diff = proc.getDepth() - var_referenced.getProc().getDepth() - 1;
        ProcCallStatement.loadStaticLink(code, "GR1", depth_diff);
        
        final int align = var_referenced.getAlignment();
        final int max = ((ArrayType)var_referenced.getType()).getMax();
        code.addAddlImm("GR1", -align - 2 - max);
    }
    
    @Override
    public void printHead(String indent, String msg)
    {
        ILiteral.super.printHead(indent, msg);
    }
    
    @Override
    public String toString()
    {
        return "" + name;
    }
    
    @Override
    public void printBodyln(String indent)
    {
        index.println(indent + "  ");
    }
}

