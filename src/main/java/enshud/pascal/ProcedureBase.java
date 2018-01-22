package enshud.pascal;

import java.util.ArrayList;
import java.util.List;

import enshud.pascal.ast.statement.CompoundStatement;


abstract class ProcedureBase
{
    protected Procedure                  parent;
    protected String                     name;
    protected String                     proc_id;                                 // label
    protected final List<Procedure>      children    = new ArrayList<>();
    
    protected final VariableDeclarations param_decls = new VariableDeclarations();
    protected final VariableDeclarations local_decls = new VariableDeclarations();
    
    protected CompoundStatement          body;
    
    @Override
    public String toString()
    {
        return getName();
    }
    
    public void setParent(Procedure parent)
    {
        this.parent = parent;
    }
    
    public String getName()
    {
        return name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public String getId()
    {
        return proc_id;
    }
    
    public void setId(String proc_id)
    {
        this.proc_id = proc_id;
    }
    
    public List<Procedure> getChildren()
    {
        return children;
    }
    
    public CompoundStatement getBody()
    {
        return body;
    }
    
    public void setBody(CompoundStatement body)
    {
        this.body = body;
    }
    
    public VariableDeclarations getLocals()
    {
        return local_decls;
    }
    
    public VariableDeclarations getParams()
    {
        return param_decls;
    }
    
    protected Procedure getParent()
    {
        return parent;
    }
    
    public String getQualifiedName()
    {
        return isRoot()
                ? getName()
                : getParent().getQualifiedName() + "." + getName();
    }
    
    public int getDepth()
    {
        return isRoot()? 0: 1 + getParent().getDepth();
    }
    
    public boolean isRoot()
    {
        return getParent() == null;
    }
}
