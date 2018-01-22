package enshud.s4.compiler.tacode;

import io.vavr.control.Option;

public abstract class AbstractTAInst implements ITAInst
{
    private Option<String> label;

    @Override
    public final Option<String> getLabel()
    {
        return label;
    }
    
    protected void setLabel(String label)
    {
        this.label = Option.of(label);
    }
}
