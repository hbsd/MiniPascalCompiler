package enshud.pascal.ast;

import java.util.Objects;

public class Program implements IASTNode
{
    final Identifier                name;
    final NameList                  file_names;
    final VariableDeclarationList   vars;
    final SubProgramDeclarationList subprograms;
    final StatementList             body;

    public Program(
        Identifier name,
        NameList file_names,
        VariableDeclarationList vars,
        SubProgramDeclarationList subprograms,
        StatementList body
    )
    {
        this.name = Objects.requireNonNull(name);
        this.file_names = Objects.requireNonNull(file_names);
        this.vars = Objects.requireNonNull(vars);
        this.subprograms = Objects.requireNonNull(subprograms);
        this.body = Objects.requireNonNull(body);
    }

    public Identifier getName()
    {
        return name;
    }

    public VariableDeclarationList getVars()
    {
        return vars;
    }

    public SubProgramDeclarationList getSubProcs()
    {
        return subprograms;
    }

    public StatementList getBody()
    {
        return body;
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
    public String toString()
    {
        return "" + name;
    }

    @Override
    public void printBodyln(String indent)
    {
        file_names.println(indent + " |");
        vars.println(indent + " |");
        subprograms.println(indent + " |");
        body.println(indent + "  ");
    }
}


