package enshud.s2.parser.parsers.basic;

import enshud.s2.parser.parsers.IParser;

import java.util.Objects;
import java.util.Set;

import enshud.s1.lexer.TokenType;
import enshud.s2.parser.ParserInput;
import enshud.s2.parser.node.INode;


/**
 * Test parse without changing input
 */
class TestParser implements IParser
{
    private final IParser parser;
    
    TestParser(IParser parser)
    {
        this.parser = Objects.requireNonNull(parser);
    }
    
    @Override
    public Set<TokenType> getFirstSet()
    {
        return parser.getFirstSet();
    }
    
    @Override
    public INode parse(ParserInput input)
    {
        final int save = input.getIndex();
        
        final INode n = parser.parse(input);
        if (n.isSuccess())
        {
            input.setIndex(save);
        }
        return n;
    }
}
