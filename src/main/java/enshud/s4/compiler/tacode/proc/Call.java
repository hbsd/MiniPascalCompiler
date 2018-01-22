package enshud.s4.compiler.tacode.proc;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import enshud.pascal.Procedure;
import enshud.s4.compiler.tacode.AbstractTAInst;

public class Call extends AbstractTAInst
{
    Procedure proc;
    
    public Call(Procedure proc, String label)
    {
        this.proc = Objects.requireNonNull(proc);
        setLabel(label);
    }
    
    public Call(Procedure proc)
    {
        this(proc, null);
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
        return String.format("%s\tCall :%s", getLabel(), proc.getQualifiedName());
    }
}
