package enshud.s2.parser.parsers;


import java.util.Set;

import enshud.s1.lexer.TokenType;
import enshud.s2.parser.ParserInput;
import enshud.s2.parser.node.EmptyNode;
import enshud.s2.parser.node.INode;
import enshud.s2.parser.node.SequenceNode;


class OptionParser implements IParser
{
    private final IParser parser;
    
    OptionParser(IParser parser)
    {
        this.parser = Parsers.rep(0, 1, parser);
    }
    
    @Override
    public Set<TokenType> getFirstSet()
    {
        return parser.getFirstSet();
    }
    
    @Override
    public INode parse(ParserInput input)
    {
        final INode node = parser.parse(input);
        if (node.isFailure())
        {
            return node;
        }
        
        final SequenceNode n = (SequenceNode)node;
        
        return n.isEmpty()? new EmptyNode(): n.get(0);
    }
}
