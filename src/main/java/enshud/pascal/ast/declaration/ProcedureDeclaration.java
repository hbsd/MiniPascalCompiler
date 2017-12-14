package enshud.pascal.ast.declaration;

import java.util.Objects;

import enshud.pascal.ast.NodeList;
import enshud.pascal.ast.expression.Identifier;
import enshud.pascal.ast.statement.CompoundStatement;


public class ProcedureDeclaration implements IDeclaration
{
    private final Identifier                     name;
    private final NodeList<ParameterDeclaration>            params;
    private final NodeList<LocalDeclaration>  vars;
    private final NodeList<ProcedureDeclaration> subprocs;
    private final CompoundStatement                  body;
    
    public ProcedureDeclaration(
        Identifier name,
        NodeList<ParameterDeclaration> params,
        NodeList<LocalDeclaration> vars,
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
    
    public NodeList<ParameterDeclaration> getParams()
    {
        return params;
    }
    
    public NodeList<LocalDeclaration> getVars()
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

