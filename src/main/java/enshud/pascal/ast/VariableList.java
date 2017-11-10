package enshud.pascal.ast;

import java.util.List;
import java.util.Objects;
import java.util.ArrayList;


public class VariableList implements IList<IVariable>
{
    private final List<IVariable> list;
    
    public VariableList()
    {
        list = new ArrayList<>();
    }
    
    public VariableList(IVariable var)
    {
        this();
        add(Objects.requireNonNull(var));
    }
    
    @Override
    public List<IVariable> getList()
    {
        return list;
    }
    
    public void add(IVariable var)
    {
        list.add(var);
    }
}

