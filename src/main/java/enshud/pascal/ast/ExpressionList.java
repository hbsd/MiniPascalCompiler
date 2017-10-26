package enshud.pascal.ast;

import java.util.List;
import java.util.Objects;
import java.util.ArrayList;


public class ExpressionList implements IList<IExpression>
{
    final List<IExpression> list;
    
    public ExpressionList()
    {
        list = new ArrayList<>();
    }
    
    public ExpressionList(IExpression exp)
    {
        this();
        add(Objects.requireNonNull(exp));
    }
    
    @Override
    public List<IExpression> getList()
    {
        return list;
    }
    
    public void add(IExpression exp)
    {
        list.add(exp);
    }
}

