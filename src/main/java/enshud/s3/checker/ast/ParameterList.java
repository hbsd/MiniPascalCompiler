package enshud.s3.checker.ast;

import java.util.List;
import java.util.Objects;
import java.util.ArrayList;


public class ParameterList implements IList<Parameter>
{
    final List<Parameter> list;

    public ParameterList()
    {
        list = new ArrayList<>();
    }

    public ParameterList(Parameter param)
    {
        this();
        add(Objects.requireNonNull(param));
    }

    @Override
    public List<Parameter> getList()
    {
        return list;
    }

    public void add(Parameter param)
    {
        list.add(param);
    }
}


