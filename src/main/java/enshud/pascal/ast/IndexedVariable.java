package enshud.pascal.ast;

import java.util.List;
import java.util.Objects;

import enshud.pascal.type.ArrayType;
import enshud.pascal.type.IType;
import enshud.pascal.type.BasicType;
import enshud.pascal.type.UnknownType;
import enshud.s3.checker.Checker;
import enshud.s3.checker.Context;
import enshud.s3.checker.Procedure;
import enshud.s3.checker.VariableDeclaration.Variable;
import enshud.s4.compiler.LabelGenerator;


public class IndexedVariable implements IVariable, ILiteral
{
    final Identifier name;
    final Expression index;
    IType            type;
    
    public IndexedVariable(Identifier name, Expression index)
    {
        this.name = Objects.requireNonNull(name);
        this.index = Objects.requireNonNull(index);
        type = UnknownType.UNKNOWN;
    }
    
    @Override
    public Identifier getName()
    {
        return name;
    }
    
    public Expression getIndex()
    {
        return index;
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
    public void retype(IType new_type)
    {
        if (getType().isUnknown())
        {
            type = new_type;
        }
    }
    
    @Override
    public IType check(Procedure proc, Checker checker)
    {
        final String nm = getName().toString();
        type = proc.getVarType(nm);
        
        if (type == UnknownType.UNKNOWN)
        {
            List<Variable> vs = proc.getVarFuzzy(nm);
            checker.addErrorMessage(
                proc, getName(),
                "variable '" + nm + "' is not defined."
              + (vs.isEmpty()? "" : (" did you mean variable " + vs +  "?"))
            );
        }
        else if (type.isBasicType())
        {
            checker.addErrorMessage(
                proc, this, "incompatible type: regular type variable '" + nm + "' cannot have index."
            );
        }
        
        // check index
        final IType idx_type = getIndex().check(proc, checker);
        if (idx_type.isUnknown())
        {
            getIndex().retype(BasicType.INTEGER);
        }
        
        if (!idx_type.equals(BasicType.INTEGER))
        {
            checker.addErrorMessage(
                proc, getIndex(),
                "incompatible type: cannot use " + idx_type + " type as index of '" + nm + "'. must be INTEGER."
            );
        }
        return type.getBasicType();
    }
    
    @Override
    public IConstant preeval(Procedure proc, Context context)
    {
        return null;
    }
    
    @Override
    public void compile(StringBuilder codebuilder, Procedure proc, LabelGenerator l_gen)
    {
        compileForData(codebuilder, proc, l_gen);
    }
    
    private void _compile(StringBuilder codebuilder, Procedure proc, LabelGenerator l_gen)
    {
        codebuilder.append(";idx v").append(System.lineSeparator());
        getIndex().compile(codebuilder, proc, l_gen);
        
        final String nm = getName().toString();
        final Variable var = proc.getLocalVar(nm);
        if (var != null)
        {
            final int align = var.getAlignment();
            
            final int max = ((ArrayType)var.getType()).getMax();
            codebuilder.append(" LAD GR1,").append(-align - 2 - max).append(",GR5").append(System.lineSeparator());
        }
        else
        {
            final Variable var_g = proc.getGlobalVar(nm);
            final int align = var_g.getAlignment();
            
            final int max = ((ArrayType)var_g.getType()).getMax();
            codebuilder.append(" LAD GR1,").append(-align - 2 - max).append(",GR4").append(System.lineSeparator());
        }
        codebuilder.append(" ADDL GR1,GR2; add index").append(System.lineSeparator());
    }
    
    @Override
    public void compileForData(StringBuilder codebuilder, Procedure proc, LabelGenerator l_gen)
    {
        _compile(codebuilder, proc, l_gen);
        codebuilder.append(" LD GR2,0,GR1").append(System.lineSeparator());
        codebuilder.append(";idx ^").append(System.lineSeparator());
    }
    
    @Override
    public void compileForAddr(StringBuilder codebuilder, Procedure proc, LabelGenerator l_gen)
    {
        _compile(codebuilder, proc, l_gen);
        codebuilder.append(" LD GR2,GR1").append(System.lineSeparator());
        codebuilder.append(";idx ^").append(System.lineSeparator());
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
