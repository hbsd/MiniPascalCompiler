package enshud.pascal.ast.statement;

import java.util.Objects;

import enshud.pascal.ast.IVisitor;
import enshud.pascal.ast.NodeList;


@SuppressWarnings("serial")
public class CompoundStatement  extends NodeList<IStatement> implements IStatement
{
    public CompoundStatement()
    {
        super();
    }
    
    public CompoundStatement(IStatement stm)
    {
        super();
        add(Objects.requireNonNull(stm));
    }
    
    @Override
    public <T, U> T accept(IVisitor<T, U> visitor, U option)
    {
        return visitor.visit(this, option);
    }
}

