package enshud.s2.parser.parsers.basic;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.ArrayList;

import enshud.s2.parser.parsers.IParser;
import enshud.s1.lexer.TokenType;
import enshud.s2.parser.ParserInput;
import enshud.s2.parser.node.INode;
import enshud.s2.parser.node.basic.SequenceNode;


class RepeatParser implements IParser
{
    private final IParser parser;
    private final int     beg;
    private final int     end;
    
    RepeatParser(int beg, int end, IParser parser)
    {
        if (beg < 0 || (end >= 0 && beg > end))
        {
            throw new IllegalArgumentException("beg >= 0 && (end < 0 || beg <= end)");
        }
        this.parser = Objects.requireNonNull(parser);
        this.beg = beg;
        this.end = end;
    }
    
    RepeatParser(int beg, IParser parser)
    {
        this(beg, -1, parser);
    }
    
    @Override
    public Set<TokenType> getFirstSet()
    {
        final Set<TokenType> set = parser.getFirstSet();
        if (beg == 0)
        {
            set.add(TokenType.SUNKNOWN);
        }
        return set;
    }
    
    @Override
    public INode parse(ParserInput input)
    {
        IParser.verbose("{");
        int count = 0;
        final int save = input.getIndex();
        final List<INode> childs = new ArrayList<>();
        
        INode n = null;
        while (end < 0 || count <= end)
        {
            final TokenType next = input.getFront().getType();
            if (!parser.getFirstSet().contains(next))
            {
                break;
            }
            
            n = parser.parse(input);
            if (n.isSuccess())
            {
                ++count;
                childs.add(n);
            }
            else
            {
                IParser.verboseln("!}");
                input.setIndex(save);
                return n;
            }
        }
        
        if (count >= beg && (end < 0 || count <= end))
        {
            IParser.verboseln("}");
            return new SequenceNode(childs);
        }
        else
        {
            IParser.verboseln("!}");
            input.setIndex(save);
            return n;
        }
    }
}
