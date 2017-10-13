package enshud.s3.checker.ast;

import java.util.List;
import java.util.Objects;
import java.util.ArrayList;


public class VariableDeclarationList implements IList<VariableDeclaration>
{
    final List<VariableDeclaration> list;

    public VariableDeclarationList()
    {
        list = new ArrayList<>();
    }

    public VariableDeclarationList(VariableDeclaration decl)
    {
        this();
        add(Objects.requireNonNull(decl));
    }

    @Override
    public List<VariableDeclaration> getList()
    {
        return list;
    }

    public void add(VariableDeclaration decl)
    {
        list.add(decl);
    }
}


