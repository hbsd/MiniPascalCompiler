package enshud.s2.parser.parsers.basic;


import enshud.s1.lexer.TokenType;
import enshud.s2.parser.parsers.IParser;


/**
 * Factory Methods
 */
public class Parsers
{
    public static IParser tok(TokenType type)
    {
        return TokenParser.create(type);
    }
    
    public static IParser rep(int beg, int end, IParser parser)
    {
        return new RepeatParser(beg, end, parser);
    }
    
    public static IParser rep(int beg, IParser parser)
    {
        return new RepeatParser(beg, parser);
    }
    
    public static IParser rep0(IParser parser)
    {
        return rep(0, parser);
    }
    
    public static IParser rep1(IParser parser)
    {
        return rep(1, parser);
    }
    
    public static IParser opt(IParser parser)
    {
        return new OptionParser(parser);
    }
    
    public static IParser optseq(IParser... parsers)
    {
        return opt(seq(parsers));
    }
    
    public static IParser sel(IParser... parsers)
    {
        return new SelectParser(parsers);
    }
    
    public static IParser firstsel(IParser... parsers)
    {
        return new LookFirstSelectParser(parsers);
    }
    
    public static ParserPair pair(IParser look, IParser parser)
    {
        return new ParserPair(look, parser);
    }
    
    public static IParser looksel(ParserPair... pairs)
    {
        return new LookAheadSelectParser(pairs);
    }
    
    public static IParser seq(IParser... parsers)
    {
        return new SequenceParser(parsers);
    }
    
    public static IParser end(IParser parser)
    {
        return new EndParser(parser);
    }
    
    public static IParser rep0seq(IParser... parsers)
    {
        return rep0(seq(parsers));
    }
    
    public static IParser test(IParser parser)
    {
        return new TestParser(parser);
    }
}

