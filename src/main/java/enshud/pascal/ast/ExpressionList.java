package enshud.pascal.ast;

import java.util.List;
import java.util.Objects;
import java.util.ArrayList;


public class ExpressionList implements IList<ITyped>
{
    private final List<ITyped> list;
    
    public ExpressionList()
    {
        list = new ArrayList<>();
    }
    
    public ExpressionList(ITyped exp)
    {
        this();
        add(Objects.requireNonNull(exp));
    }
    
    @Override
    public List<ITyped> getList()
    {
        return list;
    }
    
    public void add(ITyped exp)
    {
        list.add(exp);
    }
}

