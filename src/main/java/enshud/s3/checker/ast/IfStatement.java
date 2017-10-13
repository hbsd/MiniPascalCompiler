package enshud.s3.checker.ast;

import java.util.Objects;

import enshud.s3.checker.Checker;
import enshud.s3.checker.Procedure;
import enshud.s3.checker.type.IType;
import enshud.s3.checker.type.RegularType;


public class IfStatement implements IStatement
{
    final Expression    cond;
    final StatementList then_statements;

    public IfStatement(Expression cond, StatementList then_statements)
    {
        this.cond = Objects.requireNonNull(cond);
        this.then_statements = Objects.requireNonNull(then_statements);
    }

    public Expression getCond()
    {
        return cond;
    }

    public StatementList getThen()
    {
        return then_statements;
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
        final IType type = getCond().check(proc, checker);
        if( type.isUnknown() )
        {
            getCond().retype(RegularType.BOOLEAN);
        }
        if( !type.equals(RegularType.BOOLEAN) && !type.isUnknown() )
        {
            checker.addErrorMessage(
                proc, getCond(),
                "incompatible type: cannot use " + type + " type as condition of if-statement. must be BOOLEAN."
            );
        }

        getThen().check(proc, checker);
        return null;
    }

    /*@Override
    public void compile(StringBuilder codebuilder, Procedure proc, LabelGenerator l_gen)
    {
        final String label = l_gen.toString();
        l_gen.next();

        cond.compile(codebuilder, proc, l_gen);

        codebuilder.append(" LD GR2,GR2").append(System.lineSeparator());
        codebuilder.append(" JZE F").append(label).append("; branch of IF").append(System.lineSeparator());

        then_statements.compile(codebuilder, proc, l_gen);

        codebuilder.append("F").append(label).append(" NOP; end of IF").append(System.lineSeparator());
    }*/

    @Override
    public String toString()
    {
        return "Then only";
    }

    @Override
    public void printBodyln(String indent)
    {
        cond.println(indent + " |", "Condition of IfElse");
        then_statements.println(indent + "  ", "Then of IfElse");
    }
}


