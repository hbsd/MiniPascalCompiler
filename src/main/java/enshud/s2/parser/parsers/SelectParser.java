package enshud.s2.parser.parsers;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import enshud.s1.lexer.TokenType;
import enshud.s2.parser.ParserInput;
import enshud.s2.parser.node.INode;


class SelectParser implements IParser
{
    private final IParser[] parsers;
    
    SelectParser(IParser[] parsers)
    {
        this.parsers = Objects.requireNonNull(parsers);
        if (parsers.length == 0)
        {
            throw new IllegalArgumentException();
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
        IParser.verbose("(|");
        INode n = null; // everytime rewritten, won't be null
        for (final IParser parser: parsers)
        {
            n = parser.parse(input);
            if (n.isSuccess())
            {
                IParser.verbose(")");
                return n;
            }
        }
        assert n != null;
        
        IParser.verboseln("!)");
        return n;
    }
}
