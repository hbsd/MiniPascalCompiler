package enshud.s3.checker;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import enshud.pascal.type.BasicType;


public class ParameterDeclaration 
{
    private final List<Param> params = new ArrayList<>();
    
    public static class Param
    {
        private final String    name;
        private final BasicType type;
        private final int       alignment;
        
        Param(String name, BasicType type, int alignment)
        {
            this.name = Objects.requireNonNull(name);
            this.type = Objects.requireNonNull(type);
            this.alignment = alignment;
        }
        
        public String getName()
        {
            return name;
        }
        
        public BasicType getType()
        {
            return type;
        }
        
        public int getAlignment()
        {
            return alignment;
        }
        
        @Override
        public String toString()
        {
            return getName();
        }
    }
    
    void add(String name, BasicType type)
    {
        params.add(new Param(name, type, params.size()));
    }
    
    Param get(int index)
    {
        return params.get(index);
    }
    
    Optional<Param> get(String name)
    {
        return params
                .stream()
                .filter(p -> p.getName().equals(name))
                .findFirst();
    }
    
    List<Param> searchForFuzzy(String name)
    {
        return params
                .stream()
                .filter(p -> Checker.isSimilar(p.getName(), name))
                .collect(Collectors.toList());
    }
    
    int getIndex(String name)
    {
        int i = 0;
        for (final Param p: params)
        {
            if (p.getName().equals(name))
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
        return params.size();
    }
    
    int getAllSize()
    {
        if (params.isEmpty())
        {
            return 0;
        }
        else
        {
            return params.get(length() - 1).getAlignment() + getSize(length() - 1);
        }
    }
    
    boolean exists(String name)
    {
        return get(name).isPresent();
    }
}
