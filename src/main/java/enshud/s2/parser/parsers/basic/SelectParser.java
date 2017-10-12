package enshud.s2.parser.parsers.basic;

import enshud.s2.parser.parsers.IParser;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import enshud.s1.lexer.TokenType;
import enshud.s2.parser.ParserInput;
import enshud.s2.parser.node.INode;


class SelectParser implements IParser
{
    final IParser[] parsers;

    SelectParser(IParser[] parsers)
    {
        this.parsers = Objects.requireNonNull(parsers);
    }
    
    @Override
    public Set<TokenType> getFirst() {
    	Set<TokenType> set = new HashSet<>();
    	for(IParser p: parsers)
    	{
    		set.addAll(p.getFirst());
    	}
    	return set;
    }

    @Override
    public INode parse(ParserInput input)
    {
        IParser.verbose("(|");
        INode n = null; // everytime rewritten, won't be null
        for(final IParser parser: parsers)
        {
            n = parser.parse(input);
            if( n.isSuccess() )
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
