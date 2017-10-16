package enshud.s3.checker.ast;

import java.util.List;
import java.util.Objects;

import enshud.s3.checker.Checker;
import enshud.s3.checker.Procedure;
import enshud.s3.checker.type.IType;
import enshud.s3.checker.type.RegularType;
import enshud.s4.compiler.LabelGenerator;


public class ReadStatement implements IReadWriteStatement
{
    final VariableList vars;

    public ReadStatement(VariableList vars)
    {
        this.vars = Objects.requireNonNull(vars);
    }

    public List<IVariable> getVariables()
    {
        return vars.getList();
    }

    @Override
    public int getLine()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getColumn()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public IType check(Procedure proc, Checker checker)
    {
        int i = 1;
        for(final IVariable var: getVariables())
        {
            IType type = var.check(proc, checker);

            if( type.isUnknown() )
            {
                checker.addErrorMessage(
                    proc, var, "cannot identify the type of " + Checker.getOrderString(i) + " argument of readln."
                );
            }
            else if( type != RegularType.INTEGER && type != RegularType.CHAR && !type.isArrayOf(RegularType.CHAR) )
            {
                checker.addErrorMessage(
                    proc, var, "incompatible type: " + Checker.getOrderString(i)
                            + " argument of readln must be INTEGER, CHAR, or array of CHAR, but is " + type
                );
            }
            ++i;
        }
        return null;
    }
    
    @Override
    public void compile(StringBuilder codebuilder, Procedure proc, LabelGenerator l_gen)
    {
        if(getVariables().isEmpty())
        {
            codebuilder.append(" CALL RDLN").append(System.lineSeparator());
            return;
        }

        for(IVariable v: getVariables())
        {
            v.compileForAddr(codebuilder, proc, l_gen);
            if(v.getType() == RegularType.CHAR)
            {
                codebuilder.append(" CALL RDCH").append(System.lineSeparator());
            }
            else if(v.getType() == RegularType.INTEGER)
            {
                codebuilder.append(" CALL RDINT").append(System.lineSeparator());
            }
            else if(v.getType().isArrayOf(RegularType.CHAR))
            {
                codebuilder.append(" CALL RDSTR").append(System.lineSeparator());
            }
            else
            {
                assert false: "type error";
            }
        }
    }

    @Override
    public String toString()
    {
        return "";
    }

    @Override
    public void printBodyln(String indent)
    {
        vars.println(indent + "  ");
    }
}


