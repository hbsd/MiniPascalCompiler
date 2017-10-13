package enshud.s3.checker.ast;

import java.util.Objects;

import enshud.s3.checker.Checker;
import enshud.s3.checker.Procedure;
import enshud.s3.checker.VariableDeclaration.Variable;
import enshud.s3.checker.type.IType;
import enshud.s3.checker.type.UnknownType;


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
        if( getType().isUnknown() )
        {
            type = new_type;
        }
    }

    @Override
    public IType check(Procedure proc, Checker checker)
    {
        final String nm = getName().toString();
        type = proc.getVarType(nm);

        if( type == UnknownType.UNKNOWN )
        {
            checker.addErrorMessage(proc, getName(), "variable '" + nm + "' is not defined.");
        }
        return type;
    }

    /*@Override
    public void compile(StringBuilder codebuilder, Procedure proc, LabelGenerator l_gen)
    {
        compileForData(codebuilder, proc, l_gen);
    }
    
    public void compileForData(StringBuilder codebuilder, Procedure proc, LabelGenerator l_gen)
    {
        Variable var = proc.getLocalVar(getName().toString());
        if( var != null )
        {
            final int align = var.getAlignment();
            codebuilder.append(" LD GR2,").append(-align - 2).append(",GR5").append(System.lineSeparator());
        }
        else
        {
            var = proc.getGlobalVar(getName().toString());
            final int align = var.getAlignment();

            codebuilder.append(" LD GR2,").append(-align - 2).append(",GR4").append(System.lineSeparator());
        }
    }
    
    public void compileForAddr(StringBuilder codebuilder, Procedure proc, LabelGenerator l_gen)
    {
        Variable var = proc.getLocalVar(getName().toString());
        if( var != null )
        {
            final int align = var.getAlignment();
            codebuilder.append(" LAD GR2,").append(-align - 2).append(",GR5").append(System.lineSeparator());
        }
        else
        {
            var = proc.getGlobalVar(getName().toString());
            final int align = var.getAlignment();

            codebuilder.append(" LAD GR2,").append(-align - 2).append(",GR4").append(System.lineSeparator());
        }

        if( var.getType().isArrayType() )
        {
            codebuilder.append(" LAD GR1,").append(var.getType().getSize()).append(System.lineSeparator());
        }
    }*/

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
}


