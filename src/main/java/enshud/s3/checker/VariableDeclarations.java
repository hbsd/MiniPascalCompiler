package enshud.s3.checker;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import enshud.pascal.type.IType;


@SuppressWarnings("serial")
public class VariableDeclarations extends ArrayList<Variable>
{
    void add(String name, IType type)
    {
        super.add(new Variable(name, type, getAllSize()));
    }
    
    Optional<Variable> get(String name)
    {
        return this
                .stream()
                .filter(v -> v.getName().equals(name))
                .findFirst();
    }
    
    List<Variable> searchForFuzzy(String name)
    {
        return this
                .stream()
                .filter(v -> Checker.isSimilar(v.getName(), name))
                .collect(Collectors.toList());
    }
    
    int getIndex(String name)
    {
        int i = 0;
        for (final Variable v: this)
        {
            if (v.getName().equals(name))
            {
                return i;
            }
            ++i;
        }
        return -1;
    }
    
    int getSize(int index)
    {
        return get(index).getType().getSize();
    }
    
    int length()
    {
        return this.size();
    }
    
    int getAllSize()
    {
        if (this.isEmpty())
        {
            return 0;
        }
        else
        {
            return this.get(length() - 1).getAlignment() + getSize(length() - 1);
        }
    }
    
    boolean exists(String name)
    {
        return get(name).isPresent();
    }
}
