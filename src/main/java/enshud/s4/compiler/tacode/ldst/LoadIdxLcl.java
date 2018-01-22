package enshud.s4.compiler.tacode.ldst;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import enshud.pascal.QualifiedVariable;
import enshud.s4.compiler.tacode.AbstractTAInst;
import enshud.s4.compiler.tacode.TAValue;

public class LoadIdxLcl extends AbstractTAInst
{
    String to;
    QualifiedVariable var;
    TAValue idx;
    public LoadIdxLcl(String to, QualifiedVariable var, TAValue idx, String label)
    {
        this.to = Objects.requireNonNull(to);
        this.var = Objects.requireNonNull(var);
        this.idx = Objects.requireNonNull(idx);
        setLabel(label);
    }

    public LoadIdxLcl(String to, QualifiedVariable var, TAValue idx)
    {
        this(to, var, idx, null);
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
        idx.whenVar(s::add);
        return s;
    }
    
    @Override
    public String toString()
    {
        return String.format("%s\t%s = LclIdx(%s[%s])", getLabel(), to, var, idx);
    }
}
