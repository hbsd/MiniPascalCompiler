package enshud.s3.checker.ast;

import java.util.Objects;

import enshud.s3.checker.Checker;
import enshud.s3.checker.Procedure;
import enshud.s3.checker.ParameterDeclaration.Param;
import enshud.s3.checker.VariableDeclaration.Variable;
import enshud.s3.checker.type.ArrayType;
import enshud.s3.checker.type.IType;
import enshud.s3.checker.type.UnknownType;
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
        	Param param = proc.getParam(nm);
        	if(param != null)
        	{
            	type = param.getType();
        	}
        	else
        	{
                checker.addErrorMessage(proc, getName(), "variable '" + nm + "' is not defined.");
        	}
        }
        
        return type;
    }

    @Override
    public void compile(StringBuilder codebuilder, Procedure proc, LabelGenerator l_gen)
    {
    	if(getType().isRegularType()){
            compileForData(codebuilder, proc, l_gen);
    	} else {
            compileForAddr(codebuilder, proc, l_gen);
    	}
    }
    
    private void _compile(String inst, StringBuilder codebuilder, Procedure proc, LabelGenerator l_gen)
    {
    	Variable var = proc.getLocalVar(getName().toString());
        if( var != null )
        {
            final int align = var.getAlignment();
            
            if( var.getType().isArrayType() )
            {
            	final int len = ((ArrayType)var.getType()).getSize();
            	codebuilder.append(" ").append(inst).append(" GR2,").append(-align - 1 - len).append(",GR5").append(System.lineSeparator());
            	// array length
                if( inst.equals("LAD"))
                {
                    codebuilder.append(" LAD GR1,").append(var.getType().getSize()).append(System.lineSeparator());
                }
            }
            else
            {
            	codebuilder.append(" ").append(inst).append(" GR2,").append(-align - 2).append(",GR5").append(System.lineSeparator());
            }
        }
        else
        {
        	final Param param = proc.getParam(getName().toString());
            if(param != null)
            {
                final int align = param.getAlignment();
                codebuilder.append(" ").append(inst).append(" GR2,").append(align + 1).append(",GR5").append(System.lineSeparator());
            }
            else
            {
                var = proc.getGlobalVar(getName().toString());
                final int align = var.getAlignment();

                if( var.getType().isArrayType() )
                {
                	final int len = ((ArrayType)var.getType()).getSize();
                	codebuilder.append(" ").append(inst).append(" GR2,").append(-align - 1 - len).append(",GR4").append(System.lineSeparator());
                	// array length
                    if( inst.equals("LAD"))
                    {
                        codebuilder.append(" LAD GR1,").append(var.getType().getSize()).append(System.lineSeparator());
                    }
                }
                else
                {
                	codebuilder.append(" ").append(inst).append(" GR2,").append(-align - 2).append(",GR4").append(System.lineSeparator());
                }
            }
        }
    }
    
    @Override
    public void compileForData(StringBuilder codebuilder, Procedure proc, LabelGenerator l_gen)
    {
        _compile("LD", codebuilder, proc, l_gen);
    }

    @Override
    public void compileForAddr(StringBuilder codebuilder, Procedure proc, LabelGenerator l_gen)
    {
        _compile("LAD", codebuilder, proc, l_gen);
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


