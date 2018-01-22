package enshud.s2.parser.parsers;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import enshud.s1.lexer.TokenType;
import enshud.s2.parser.ParserInput;
import enshud.s2.parser.node.FailureNode;
import enshud.s2.parser.node.INode;


public class LookAheadSelectParser implements IParser
{
    private final ParserPair[] pairs;
    
    public LookAheadSelectParser(ParserPair[] pairs)
    {
        this.pairs = Objects.requireNonNull(pairs);
        if (pairs.length == 0)
        {
            throw new IllegalArgumentException();
        }
    }
    
    @Override
    public Set<TokenType> getFirstSet()
    {
        return Stream.of(pairs)
                .flatMap(p -> p.look.getFirstSet().stream()) // merge two Sets
                .collect(Collectors.toSet());
    }
    
    @Override
    public INode parse(ParserInput input)
    {
        return Stream.of(pairs)
                .filter(p -> p.look.parse(input).isSuccess())
                .findFirst()
                .map(p -> p.parser.parse(input))
                .orElse(
                    (!input.isEmpty())
                        ? new FailureNode(input.getFront(), "Selection Not Found.")
                        : new FailureNode("Selection Not Found.")
                );
    }
    
}
