package enshud.s3.checker;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import enshud.pascal.type.BasicType;


public class ParameterDeclaration
{
    private final List<Param> params = new ArrayList<>();
    
    public class Param
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
    
    Param get(int num)
    {
        return params.get(num);
    }
    
    Param get(String name)
    {
        for (final Param p: params)
        {
            if (p.name.equals(name))
            {
                return p;
            }
        }
        return null;
    }
    
    List<Param> getFuzzy(String name)
    {
        List<Param> l = new ArrayList<>();
        for (final Param p: params)
        {
            if (Checker.isSimilar(p.name, name))
            {
                l.add(p);
            }
        }
        return l;
    }
    
    int getIndex(String name)
    {
        int i = 0;
        for (final Param p: params)
        {
            if (p.name.equals(name))
            {
                return i;
            }
            ++i;
        }
        return -1;
    }
    
    int length()
    {
        return params.size();
    }
    
    boolean exists(String name)
    {
        return params.contains(name);
    }
}
