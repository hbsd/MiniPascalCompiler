package enshud.s4.compiler.tacode.ldst;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import enshud.s4.compiler.tacode.AbstractTAInst;
import enshud.s4.compiler.tacode.TAValue;

public class Copy extends AbstractTAInst
{
    String  to;
    TAValue from;
    
    public Copy(String to, TAValue from, String label)
    {
        this.to = Objects.requireNonNull(to);
        this.from = Objects.requireNonNull(from);
        setLabel(label);
    }
    
    public Copy(String to, TAValue from)
    {
        this(to, from, null);
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
        from.whenVar(s::add);
        return s;
    }
    
    @Override
    public String toString()
    {
        return String.format("%s\t%s = %s", getLabel(), to, from);
    }
}
