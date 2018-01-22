package enshud.s4.compiler.tacode.jmp;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import enshud.s4.compiler.tacode.AbstractTAInst;

public class Jump extends AbstractTAInst implements IJump
{
    final String to;
    
    @Override
    public String getTo()
    {
        return to;
    }
    
    public Jump(String to, String label)
    {
        this.to = Objects.requireNonNull(to);
        setLabel(label);
    }
    
    public Jump(String to)
    {
        this(to, null);
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
        return getLabel() + "\tJump :" + to;
    }
}
