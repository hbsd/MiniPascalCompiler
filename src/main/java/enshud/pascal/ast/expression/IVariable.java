package enshud.pascal.ast.expression;

import enshud.pascal.ast.Identifier;


public interface IVariable extends IExpression
{
    Identifier getName();
    String getQualifiedName();
}

