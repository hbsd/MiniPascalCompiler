package enshud.s4.compiler.tacode.ldst;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import enshud.pascal.QualifiedVariable;
import enshud.s4.compiler.tacode.AbstractTAInst;
import enshud.s4.compiler.tacode.TAValue;

public class StoreLcl extends AbstractTAInst
{
    QualifiedVariable var;
    TAValue val;
    public StoreLcl(QualifiedVariable var, TAValue val, String label)
    {
        this.var = Objects.requireNonNull(var);
        this.val = Objects.requireNonNull(val);
        setLabel(label);
    }

    public StoreLcl(QualifiedVariable var, TAValue val)
    {
        this(var, val, null);
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
        return String.format("%s\tLcl(%s) = %s", getLabel(), var.getQualifiedName(), val);
    }
}
