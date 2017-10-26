package enshud.s2.parser.parsers;

import java.util.Set;

import enshud.s1.lexer.TokenType;
import enshud.s2.parser.ParserInput;
import enshud.s2.parser.node.INode;


public interface IParser
{
    INode parse(ParserInput input);
    
    Set<TokenType> getFirstSet();
    
    boolean enable_verbose = false;
    
    static void verboseln(String msg)
    {
        if (enable_verbose)
        {
            System.out.println(msg);
        }
    }
    
    static void verbose(String msg)
    {
        if (enable_verbose)
        {
            System.out.print(msg);
        }
    }
}

