package enshud.s3.checker;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.commons.text.similarity.LevenshteinDistance;

import enshud.pascal.type.IType;


public class VariableDeclaration
{
    final List<Variable> vars = new ArrayList<>();
    
    public class Variable
    {
        final String name;
        final IType  type;
        final int    alignment;
        
        public String getName()
        {
            return name;
        }
        
        public IType getType()
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
        
        public Variable(String name, IType type, int alignment)
        {
            this.name = Objects.requireNonNull(name);
            this.type = Objects.requireNonNull(type);
            this.alignment = alignment;
        }
    }
    
    void add(String name, IType type)
    {
        vars.add(new Variable(name, type, getAllSize()));
    }
    
    Variable get(int num)
    {
        return vars.get(num);
    }
    
    Variable get(String name)
    {
        for (final Variable p: vars)
        {
            if (p.name.equals(name))
            {
                return p;
            }
        }
        return null;
    }
    
    List<Variable> getFuzzy(String name)
    {
        LevenshteinDistance ld = new LevenshteinDistance();
        List<Variable> l = new ArrayList<>();
        for (final Variable v: vars)
        {
            double th = Checker.FUZZY_THRESHOLD * (name.length() + v.name.length());
            if (ld.apply(v.name, name) <= th)
            {
                l.add(v);
            }
        }
        return l;
    }
    
    int getSize(int index)
    {
        return vars.get(index).type.getSize();
    }
    
    int length()
    {
        return vars.size();
    }
    
    int getAllSize()
    {
        if (vars.isEmpty())
        {
            return 0;
        }
        else
        {
            return vars.get(length() - 1).alignment + getSize(length() - 1);
        }
    }
    
    boolean exists(String name)
    {
        return get(name) != null;
    }
}
