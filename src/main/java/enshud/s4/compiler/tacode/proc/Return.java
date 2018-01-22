package enshud.s4.compiler.tacode.proc;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import enshud.s4.compiler.tacode.AbstractTAInst;

public class Return extends AbstractTAInst
{
    public Return(String label)
    {
        setLabel(label);
    }
    public Return()
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
        return getLabel() + "\tReturn";
    }
}
