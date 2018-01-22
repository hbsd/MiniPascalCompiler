package enshud.s4.compiler.tacode.ldst;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import enshud.pascal.QualifiedVariable;
import enshud.s4.compiler.tacode.AbstractTAInst;
import enshud.s4.compiler.tacode.TAValue;

public class StoreIdxLcl extends AbstractTAInst
{
    QualifiedVariable var;
    TAValue idx;
    TAValue val;
    public StoreIdxLcl(QualifiedVariable var, TAValue idx, TAValue val, String label)
    {
        this.var = Objects.requireNonNull(var);
        this.idx = Objects.requireNonNull(idx);
        this.val = Objects.requireNonNull(val);
        setLabel(label);
    }

    public StoreIdxLcl(QualifiedVariable var, TAValue idx, TAValue val)
    {
        this(var, idx, val, null);
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
        idx.whenVar(s::add);
        return s;
    }
    
    @Override
    public String toString()
    {
        return String.format("%s\tLclIdx(%s[%s]) = %s", getLabel(), var.getQualifiedName(), idx, val);
    }
}
