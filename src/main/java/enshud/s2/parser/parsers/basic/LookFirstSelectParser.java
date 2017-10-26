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


public class LookFirstSelectParser implements IParser
{
    final IParser[] parsers;
    
    public LookFirstSelectParser(IParser[] parsers)
    {
        this.parsers = Objects.requireNonNull(parsers);
        if (parsers.length == 0)
        {
            throw new IllegalArgumentException();
        }
        
        // check this is LL(1)
        int len = 0;
        for (final IParser p: parsers)
        {
            len += p.getFirstSet().size();
        }
        if (getFirstSet().size() < len)
        {
            System.err.println("Not LL(1): ");
            for (final IParser p: parsers)
            {
                System.err.println("\t" + p);
            }
        }
    }
    
    @Override
    public Set<TokenType> getFirstSet()
    {
        final Set<TokenType> set = new HashSet<>();
        for (final IParser p: parsers)
        {
            set.addAll(p.getFirstSet());
        }
        return set;
    }
    
    @Override
    public INode parse(ParserInput input)
    {
        for (final IParser p: parsers)
        {
            final TokenType next = input.getFront().getType();
            if (p.getFirstSet().contains(next))
            {
                return p.parse(input);
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
