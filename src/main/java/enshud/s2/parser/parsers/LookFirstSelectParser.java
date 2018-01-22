package enshud.s2.parser.parsers;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import enshud.s1.lexer.TokenType;
import enshud.s2.parser.ParserInput;
import enshud.s2.parser.node.FailureNode;
import enshud.s2.parser.node.INode;


public class LookFirstSelectParser implements IParser
{
    private final IParser[] parsers;
    
    public LookFirstSelectParser(IParser[] parsers)
    {
        this.parsers = Objects.requireNonNull(parsers);
        if (parsers.length == 0)
        {
            throw new IllegalArgumentException();
        }
        
        // check this is LL(1)
        int len = Stream.of(parsers)
                .mapToInt(p -> p.getFirstSet().size())
                .sum();
        if (getFirstSet().size() < len)
        {
            System.err.println("Not LL(1): ");
            Stream.of(parsers)
                .forEach(p -> System.err.println("\t" + p));
        }
    }
    
    @Override
    public Set<TokenType> getFirstSet()
    {
        return Stream.of(parsers)
                .flatMap(p -> p.getFirstSet().stream()) // merge two Sets
                .collect(Collectors.toSet());
    }
    
    @Override
    public INode parse(ParserInput input)
    {
        final TokenType next = input.getFront().getType();
        return Stream.of(parsers)
                .filter(p -> p.getFirstSet().contains(next))
                .findFirst()
                .map(p -> p.parse(input))
                .orElse(
                    (!input.isEmpty())
                        ? new FailureNode(input.getFront(), "Selection Not Found.")
                        : new FailureNode("Selection Not Found.")
                );
    }
    
}
