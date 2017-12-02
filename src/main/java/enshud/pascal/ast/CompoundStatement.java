package enshud.pascal.ast;

import java.util.List;
import java.util.ListIterator;
import java.util.Objects;

import enshud.pascal.type.IType;
import enshud.s3.checker.Checker;
import enshud.s3.checker.Procedure;
import enshud.s4.compiler.Casl2Instruction;
import enshud.s4.compiler.LabelGenerator;


@SuppressWarnings("serial")
public class CompoundStatement extends NodeList<IStatement> implements IStatement
{
    public CompoundStatement(IStatement stm)
    {
        super();
        add(Objects.requireNonNull(stm));
    }
    @Override
    public IType check(Procedure proc, Checker checker)
    {
        forEach(stm -> stm.check(proc, checker));
        return null;
    }
    
    @Override
    public IStatement precompute(Procedure proc)
    {
        ListIterator<IStatement> it = listIterator();
        while (it.hasNext())
        {
            IStatement res = it.next().precompute(proc);
            if (res == null)
            {
                it.remove();
            }
            else
            {
                it.set(res);
            }
        }
        return isEmpty()? null: this;
    }
    
    @Override
    public void compile(List<Casl2Instruction> code, Procedure proc, LabelGenerator f_gen)
    {
        forEach(stm -> stm.compile(code, proc, f_gen));
    }
    
    @Override
    public void printHead(String indent, String msg)
    {
        super.printHead(indent, msg);
    }
}

