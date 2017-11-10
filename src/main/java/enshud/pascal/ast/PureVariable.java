package enshud.pascal.ast;

import java.util.List;
import java.util.Objects;

import enshud.pascal.type.ArrayType;
import enshud.pascal.type.IType;
import enshud.pascal.type.UnknownType;
import enshud.s3.checker.Checker;
import enshud.s3.checker.Context;
import enshud.s3.checker.Procedure;
import enshud.s3.checker.ParameterDeclaration.Param;
import enshud.s3.checker.VariableDeclaration.Variable;
import enshud.s4.compiler.LabelGenerator;


public class PureVariable implements IVariable, ILiteral
{
    final Identifier name;
    IType            type;
    
    public PureVariable(Identifier name)
    {
        this.name = Objects.requireNonNull(name);
        type = UnknownType.UNKNOWN;
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
            final Param param = proc.getParam(nm);
            if (param != null)
            {
                type = param.getType();
            }
            else
            {
                List<Variable> vs = proc.getVarFuzzy(nm);
                String n = null;
                if (!vs.isEmpty())
                {
                    n = vs.toString();
                }
                else
                {
                    List<Param> ps = proc.getParamFuzzy(nm);
                    if (!ps.isEmpty())
                    {
                        n = ps.toString();
                    }
                }
                
                checker.addErrorMessage(
                    proc, getName(),
                    "variable '" + nm + "' is not defined."
                            + ((n == null)? "": (" did you mean variable " + n + "?"))
                );
            }
        }
        
        return type;
    }
    
    @Override
    public IConstant preeval(Procedure proc, Context context)
    {
        return null;
    }
    
    @Override
    public void compile(StringBuilder codebuilder, Procedure proc, LabelGenerator l_gen)
    {
        if (getType().isBasicType())
        {
            compileForData(codebuilder, proc, l_gen);
        }
        else
        {
            compileForAddr(codebuilder, proc, l_gen);
        }
    }
    
    @Override
    public void compileForData(StringBuilder codebuilder, Procedure proc, LabelGenerator l_gen)
    {
        compileImpl("LD", codebuilder, proc, l_gen);
    }
    
    @Override
    public void compileForAddr(StringBuilder codebuilder, Procedure proc, LabelGenerator l_gen)
    {
        compileImpl("LAD", codebuilder, proc, l_gen);
    }
    
    private void compileImpl(String inst, StringBuilder codebuilder, Procedure proc, LabelGenerator l_gen)
    {
        Variable var = proc.getLocalVar(getName().toString());
        if (var != null)
        {
            compileForVariable(codebuilder, var, inst, "GR5");
        }
        else
        {
            final Param param = proc.getParam(getName().toString());
            if (param != null)
            {
                final int align = param.getAlignment();
                codebuilder.append(" ").append(inst).append(" GR2,").append(align + 1).append(",GR5")
                    .append(System.lineSeparator());
            }
            else
            {
                var = proc.getGlobalVar(getName().toString());
                compileForVariable(codebuilder, var, inst, "GR4");
            }
        }
    }
    
    private void compileForVariable(StringBuilder codebuilder, Variable var, String inst, String gr)
    {
        final int align = var.getAlignment();
        
        if (var.getType().isArrayType())
        {
            final int len = ((ArrayType)var.getType()).getSize();
            codebuilder.append(" ").append(inst).append(" GR2,").append(-align - 1 - len).append(',').append(gr)
                .append(System.lineSeparator());
            // array length
            if (inst.equals("LAD"))
            {
                codebuilder.append(" LAD GR1,").append(var.getType().getSize()).append(System.lineSeparator());
            }
        }
        else
        {
            codebuilder.append(" ").append(inst).append(" GR2,").append(-align - 2).append(',').append(gr)
                .append(System.lineSeparator());
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

