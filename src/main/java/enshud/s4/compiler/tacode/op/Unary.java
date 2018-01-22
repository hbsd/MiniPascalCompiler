package enshud.s4.compiler.tacode.op;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import enshud.pascal.PrefixOperator;
import enshud.s4.compiler.tacode.AbstractTAInst;
import enshud.s4.compiler.tacode.TAValue;


public class Unary extends AbstractTAInst
{
    String         to;
    PrefixOperator op;
    TAValue        val;
    
    public Unary(String to, PrefixOperator op, TAValue val, String label)
    {
        this.to = Objects.requireNonNull(to);
        this.op = Objects.requireNonNull(op);
        this.val = Objects.requireNonNull(val);
        setLabel(label);
    }
    
    public Unary(String to, PrefixOperator op, TAValue val)
    {
        this(to, op, val, null);
    }
    
    @Override
    public Optional<String> getAssigned()
    {
        return Optional.of(to);
    }
    
    @Override
    public Set<String> getRefered()
    {
        Set<String> s = new HashSet<>();
        val.whenVar(s::add);
        return s;
    }
    
    @Override
    public String toString()
    {
        return String.format("%s\t%s = %s %s", getLabel(), to, op, val);
    }
}
