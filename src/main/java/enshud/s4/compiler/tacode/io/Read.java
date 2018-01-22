package enshud.s4.compiler.tacode.io;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import enshud.s4.compiler.tacode.AbstractTAInst;

public class Read extends AbstractTAInst
{
    String to;
    public Read(String to, String label)
    {
        this.to = Objects.requireNonNull(to);
        setLabel(label);
    }
    public Read(String to)
    {
        this(to, null);
    }
    
    @Override
    public Optional<String> getAssigned()
    {
        return Optional.of(to);
    }
    
    @Override
    public Set<String> getRefered()
    {
        return new HashSet<>();
    }
    @Override
    public String toString()
    {
        return getLabel() + "\t " + to + " = Read";
    }
}
