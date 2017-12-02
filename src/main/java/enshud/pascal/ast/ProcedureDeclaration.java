package enshud.pascal.ast;

import java.util.Objects;


public class ProcedureDeclaration implements IDeclaration
{
    private final Identifier                     name;
    private final NodeList<Parameter>            params;
    private final NodeList<VariableDeclaration>  vars;
    private final NodeList<ProcedureDeclaration> subprocs;
    private final CompoundStatement                  body;
    
    public ProcedureDeclaration(
        Identifier name,
        NodeList<Parameter> params,
        NodeList<VariableDeclaration> vars,
        NodeList<ProcedureDeclaration> subprocs,
        CompoundStatement body
    )
    {
        this.name = Objects.requireNonNull(name);
        this.params = Objects.requireNonNull(params);
        this.vars = Objects.requireNonNull(vars);
        this.subprocs = Objects.requireNonNull(subprocs);
        this.body = Objects.requireNonNull(body);
    }
    
    public Identifier getName()
    {
        return name;
    }
    
    public NodeList<Parameter> getParams()
    {
        return params;
    }
    
    public NodeList<VariableDeclaration> getVars()
    {
        return vars;
    }
    
    public NodeList<ProcedureDeclaration> getSubProcs()
    {
        return subprocs;
    }
    
    public CompoundStatement getBody()
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
        return name.toString();
    }
    
    @Override
    public void printBodyln(String indent)
    {
        params.println(indent + " |");
        vars.println(indent + " |");
        body.println(indent + "  ");
    }
}
