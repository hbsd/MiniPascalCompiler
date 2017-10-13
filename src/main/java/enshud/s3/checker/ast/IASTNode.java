package enshud.s3.checker.ast;

import enshud.s2.parser.node.INode;


public interface IASTNode extends INode
{
    @Override
    default boolean isSuccess()
    {
        return true;
    }

    @Override
    default boolean isFailure()
    {
        return !isSuccess();
    }
}
