package enshud.s4.compiler.tacode;

import java.util.Optional;
import java.util.Set;

import io.vavr.control.Option;

public interface ITAInst
{
    Option<String> getLabel();
    Optional<String> getAssigned();
    Set<String> getRefered();
}
