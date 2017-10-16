package enshud.s3.checker.ast;

import java.util.Objects;

import enshud.s3.checker.Checker;
import enshud.s3.checker.Procedure;
import enshud.s3.checker.type.IType;
import enshud.s3.checker.type.RegularType;
import enshud.s4.compiler.LabelGenerator;


public class WhileStatement implements IStatement
{
    final Expression cond;
    final IStatement statement;

    public WhileStatement(Expression cond, IStatement statement)
    {
        this.cond = Objects.requireNonNull(cond);
        this.statement = Objects.requireNonNull(statement);
    }

    public Expression getCond()
    {
        return cond;
    }

    public IStatement getStatement()
    {
        return statement;
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
                "incompatible type: cannot use " + type + " type as condition of while-statement. must be BOOLEAN."
            );
        }
        getStatement().check(proc, checker);
        return null;
    }

    @Override
    public void compile(StringBuilder codebuilder, Procedure proc, LabelGenerator l_gen)
    {
        final String label = l_gen.toString();
        l_gen.next();

        codebuilder.append("C").append(label).append(" NOP; start of WHILE").append(System.lineSeparator());
        cond.compile(codebuilder, proc, l_gen);

        codebuilder.append(" LD GR2,GR2").append(System.lineSeparator());
        codebuilder.append(" JZE F").append(label).append("; branch of WHILE").append(System.lineSeparator());

        statement.compile(codebuilder, proc, l_gen);
        codebuilder.append(" JUMP C").append(label).append(System.lineSeparator());

        codebuilder.append("F").append(label).append(" NOP; end of WHILE").append(System.lineSeparator());
    }

    @Override
    public String toString()
    {
        return "";
    }

    @Override
    public void printBodyln(String indent)
    {
        cond.println(indent + " |", "Condition of While");
        statement.println(indent + "  ", "Do of While");
    }
}

