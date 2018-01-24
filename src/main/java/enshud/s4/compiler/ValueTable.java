package enshud.s4.compiler;

import java.util.HashMap;
import java.util.Map;

import enshud.pascal.ast.expression.IConstant;


class ValueTable
{
    private Map<String, IConstant> tbl = new HashMap<>();
    public void put(String name, IConstant val)
    {
        tbl.put(name, val);
    }
    public IConstant get(String name)
    {
        return tbl.get(name);
    }
}
