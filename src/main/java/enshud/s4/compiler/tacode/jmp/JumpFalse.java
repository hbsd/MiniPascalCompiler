package enshud.s4.compiler.tacode.jmp;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import enshud.s4.compiler.tacode.AbstractTAInst;
import enshud.s4.compiler.tacode.TAValue;

public class JumpFalse extends AbstractTAInst implements IJump
{
    final TAValue cond;
    final String  to;
    
    @Override
    public String getTo()
    {
        return to;
    }
    
    public JumpFalse(TAValue cond, String to, String label)
    {
        this.cond = Objects.requireNonNull(cond);
        this.to = Objects.requireNonNull(to);
        setLabel(label);
    }
    
    public JumpFalse(TAValue cond, String to)
    {
        this(cond, to, null);
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
        cond.whenVar(s::add);
        return s;
    }
    
    @Override
    public String toString()
    {
        return String.format("%s\tif-not (%s) jump :%s", getLabel(), cond, to);
    }
}
