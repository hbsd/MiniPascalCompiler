package enshud.s4.compiler.tacode.proc;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import enshud.s4.compiler.tacode.AbstractTAInst;
import enshud.s4.compiler.tacode.TAValue;

public class Arg extends AbstractTAInst
{
    TAValue val;

    public Arg(TAValue val, String label)
    {
        this.val = Objects.requireNonNull(val);
        setLabel(label);
    }
    
    public Arg(TAValue val)
    {
        this(val, null);
    }
    
    @Override
    public Optional<String> getAssigned()
    {
        return Optional.empty();
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
        return getLabel() + "\tArg " + val;
    }
}
