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
        
        int len = 0;
        for(IParser p: parsers)
    	{
    		len += p.getFirst().size();
    	}
        if(getFirst().size() < len)
        {
        	System.err.println("Not LL(1): ");
        	for(IParser p: parsers)
        	{
        		System.err.println("\t" + p);
        	}
        }
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
        for(IParser p: parsers)
        {
        	TokenType next = input.getFront().getType();
            if( p.getFirst().contains(next) )
            {
                return p.parse(input);
            }
        }

        if( !input.isEmpty() )
        {
            LexedToken tk = input.getFront();
            return new FailureNode(tk, "Selection Not Found.");
        }
        else
        {
            return new FailureNode("Selection Not Found.");
        }

    }

}
