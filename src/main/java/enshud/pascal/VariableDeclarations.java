package enshud.pascal;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import enshud.pascal.type.IType;
import enshud.s3.checker.Checker;


class VariableDeclarations
{
    private final List<Variable> list = new ArrayList<>();
    
    void add(String name, IType type, Procedure proc)
    {
        list.add(new Variable(name, type, getAllSize(), proc));
    }
    
    Variable get(int index)
    {
        return list.get(index);
    }
    
    Optional<Variable> get(String name)
    {
        return list
                .stream()
                .filter(v -> v.getName().equals(name))
                .findFirst();
    }
    
    List<Variable> searchForFuzzy(String name)
    {
        return list
                .stream()
                .filter(v -> Checker.isSimilar(v.getName(), name))
                .collect(Collectors.toList());
    }
    
    int getIndex(String name)
    {
        int i = 0;
        for (final Variable v: list)
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
        return list.get(index).getType().getSize();
    }
    
    int length()
    {
        return list.size();
    }
    
    int getAllSize()
    {
        if (list.isEmpty())
        {
            return 0;
        }
        else
        {
            return list.get(length() - 1).getAlignment() + getSize(length() - 1);
        }
    }
    
    boolean exists(String name)
    {
        return get(name).isPresent();
    }
}