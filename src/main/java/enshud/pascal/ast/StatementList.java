package enshud.pascal.ast;

import java.util.List;
import java.util.ArrayList;
import java.util.Objects;

import enshud.pascal.type.IType;
import enshud.s3.checker.Checker;
import enshud.s3.checker.Procedure;
import enshud.s4.compiler.LabelGenerator;



public class StatementList implements IBasicStatement, IList<IStatement>
{
    final List<IStatement> list;

    public StatementList()
    {
        list = new ArrayList<>();
    }

    public StatementList(IStatement statement)
    {
        this();
        add(Objects.requireNonNull(statement));
    }

    @Override
    public List<IStatement> getList()
    {
        return list;
    }

    public void add(IStatement statement)
    {
        list.add(statement);
    }

    @Override
    public IType check(Procedure proc, Checker checker)
    {
        for(final IStatement stm: getList())
        {
            stm.check(proc, checker);
        }
        return null;
    }
    
    @Override
    public void compile(StringBuilder codebuilder, Procedure proc, LabelGenerator f_gen)
    {
        for(IStatement s: list)
        {
            s.compile(codebuilder, proc, f_gen);
        }
    }

    @Override
    public void printHead(String indent, String msg)
    {
        IList.super.printHead(indent, msg);
    }
}


