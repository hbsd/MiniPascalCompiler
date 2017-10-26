package enshud.s3.checker;

import java.util.HashMap;
import java.util.Map;

import enshud.pascal.ast.IConstant;


public class Context
{
    Map<String, IConstant>               pures;
    Map<String, Map<Integer, IConstant>> arrs;
    
    IConstant get(String name)
    {
        return pures.get(name);
    }
    
    void set(String name, IConstant val)
    {
        pures.put(name, val);
    }
    
    IConstant get(String name, int index)
    {
        Map<Integer, IConstant> a = arrs.get(name);
        return a == null? null: a.get(index);
    }
    
    void set(String name, int index, IConstant val)
    {
        Map<Integer, IConstant> a = arrs.get(name);
        if(a == null)
        {
            a = new HashMap<>();
            arrs.put(name, a);
        }
        a.put(index, val);
    }
}
