package enshud.s2.parser.parsers.basic;

import java.util.Objects;
import enshud.s2.parser.parsers.IParser;


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
