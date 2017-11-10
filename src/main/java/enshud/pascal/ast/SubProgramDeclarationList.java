package enshud.pascal.ast;

import java.util.List;
import java.util.Objects;
import java.util.ArrayList;


public class SubProgramDeclarationList implements IList<SubProgramDeclaration>
{
    private final List<SubProgramDeclaration> decls;
    
    public SubProgramDeclarationList()
    {
        decls = new ArrayList<>();
    }
    
    public SubProgramDeclarationList(SubProgramDeclaration decl)
    {
        this();
        add(Objects.requireNonNull(decl));
    }
    
    @Override
    public List<SubProgramDeclaration> getList()
    {
        return decls;
    }
    
    public void add(SubProgramDeclaration decl)
    {
        decls.add(decl);
    }
}

