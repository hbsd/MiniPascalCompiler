package enshud.pascal.ast;

import java.util.Objects;

public class SubProgramDeclaration implements IDeclaration
{
    final Identifier              name;
    final ParameterList           params;
    final VariableDeclarationList vars;
    final StatementList           body;

    public SubProgramDeclaration(
        Identifier name,
        ParameterList params,
        VariableDeclarationList vars,
        StatementList body
    )
    {
        this.name = Objects.requireNonNull(name);
        this.params = Objects.requireNonNull(params);
        this.vars = Objects.requireNonNull(vars);
        this.body = Objects.requireNonNull(body);
    }

    public Identifier getName()
    {
        return name;
    }

    public ParameterList getParams()
    {
        return params;
    }

    public VariableDeclarationList getVars()
    {
        return vars;
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
        params.println(indent + " |");
        vars.println(indent + " |");
        body.println(indent + "  ");
    }
}


