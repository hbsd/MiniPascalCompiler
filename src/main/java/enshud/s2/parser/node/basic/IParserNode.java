package enshud.s2.parser.node.basic;

import enshud.s1.lexer.LexedToken;
import enshud.s2.parser.node.INode;


public interface IParserNode extends INode
{
    @Override
    default void printHead(String indent, String msg)
    {
        System.out.print("-");
        INode.printPurple(getClass().getSimpleName());
        System.out.print(": ");
        INode.printGreenln(msg != null? msg: toString());
    }

    LexedToken getToken();

    @Override
    default int getLine()
    {
        return getToken().getLine();
    }

    @Override
    default int getColumn()
    {
        return getToken().getColumn();
    }
}
