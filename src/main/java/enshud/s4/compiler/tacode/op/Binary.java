package enshud.s4.compiler.tacode.op;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import enshud.pascal.InfixOperator;
import enshud.s4.compiler.tacode.AbstractTAInst;
import enshud.s4.compiler.tacode.TAValue;


public class Binary extends AbstractTAInst
{
    String        to;
    TAValue       lval;
    InfixOperator op;
    TAValue       rval;
    
    public Binary(String to, TAValue lval, InfixOperator op, TAValue rval, String label)
    {
        this.to = Objects.requireNonNull(to);
        this.lval = Objects.requireNonNull(lval);
        this.op = Objects.requireNonNull(op);
        this.rval = Objects.requireNonNull(rval);
        setLabel(label);
    }
    
    public Binary(String to, TAValue lval, InfixOperator op, TAValue rval)
    {
        this(to, lval, op, rval, null);
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
        lval.whenVar(s::add);
        rval.whenVar(s::add);
        return s;
    }
    
    @Override
    public String toString()
    {
        return String.format("%s\t%s = %s %s %s", getLabel(), to, lval, op, rval);
    }
}
