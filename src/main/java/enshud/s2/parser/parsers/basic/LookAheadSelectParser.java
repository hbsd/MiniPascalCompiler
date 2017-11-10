package enshud.s2.parser.parsers.basic;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import enshud.s1.lexer.LexedToken;
import enshud.s1.lexer.TokenType;
import enshud.s2.parser.ParserInput;
import enshud.s2.parser.node.INode;
import enshud.s2.parser.node.basic.FailureNode;
import enshud.s2.parser.parsers.IParser;


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
        final Set<TokenType> set = new HashSet<>();
        for (final ParserPair p: pairs)
        {
            set.addAll(p.look.getFirstSet());
        }
        return set;
    }
    
    @Override
    public INode parse(ParserInput input)
    {
        for (final ParserPair p: pairs)
        {
            if (p.look.parse(input).isSuccess())
            {
                return p.parser.parse(input);
            }
        }
        
        if (!input.isEmpty())
        {
            final LexedToken tk = input.getFront();
            return new FailureNode(tk, "Selection Not Found.");
        }
        else
        {
            return new FailureNode("Selection Not Found.");
        }
        
    }
    
}
