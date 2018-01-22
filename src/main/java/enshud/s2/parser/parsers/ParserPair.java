package enshud.s2.parser.parsers;

import java.util.Objects;


public class ParserPair
{
    final IParser look;
    final IParser parser;
    
    public ParserPair(IParser look, IParser parser)
    {
        this.look = Parsers.test(look);
        this.parser = Objects.requireNonNull(parser);
    }
}
