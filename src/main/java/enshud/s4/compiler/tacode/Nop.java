package enshud.s4.compiler.tacode;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class Nop extends AbstractTAInst
{
    public Nop(String label)
    {
        setLabel(label);
    }
    
    public Nop()
    {
        this(null);
    }
    
    @Override
    public Optional<String> getAssigned()
    {
        return Optional.empty();
    }
    
    @Override
    public Set<String> getRefered()
    {
        return new HashSet<>();
    }
    
    @Override
    public String toString()
    {
        return getLabel() + "\tNop";
    }
}
