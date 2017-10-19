package enshud.pascal.ast;

import java.util.List;
import java.util.Objects;
import java.util.ArrayList;


public class NameList implements IList<Identifier>
{
    final List<Identifier> list;

    public NameList()
    {
        list = new ArrayList<>();
    }

    public NameList(Identifier id)
    {
        this();
        add(Objects.requireNonNull(id));
    }

    @Override
    public List<Identifier> getList()
    {
        return list;
    }

    public void add(Identifier id)
    {
        list.add(id);
    }
}


