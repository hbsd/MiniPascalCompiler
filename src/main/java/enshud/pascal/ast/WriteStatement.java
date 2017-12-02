package enshud.pascal.ast;

import java.util.List;
import java.util.Objects;

import enshud.pascal.type.IType;
import enshud.pascal.type.BasicType;
import enshud.pascal.type.StringType;
import enshud.s3.checker.Checker;
import enshud.s3.checker.Procedure;
import enshud.s4.compiler.Casl2Instruction;
import enshud.s4.compiler.LabelGenerator;


public class WriteStatement implements IStatement
{
    private final NodeList<ITyped> exps;
    
    public WriteStatement(NodeList<ITyped> exps)
    {
        this.exps = Objects.requireNonNull(exps);
    }
    
    public List<ITyped> getExpressions()
    {
        return exps;
    }
    
    @Override
    public IType check(Procedure proc, Checker checker)
    {
        int i = 1;
        for (final ITyped exp: getExpressions())
        {
            IType type = exp.check(proc, checker);
            
            if (type instanceof StringType)
            {
                if (((StringType)type).getSize() == 1)
                {
                    type = BasicType.CHAR;
                }
                exp.retype(type);
            }
            else if (type != BasicType.INTEGER && type != BasicType.CHAR && !type.isArrayOf(BasicType.CHAR))
            {
                checker.addErrorMessage(
                    proc, exp, "incompatible type: " + Checker.getOrderString(i)
                            + " argument of writeln must be INTEGER, CHAR, or array of CHAR, but is " + type + "."
                );
            }
            else if (type.isUnknown())
            {
                checker.addErrorMessage(
                    proc, exp, "cannot identify the type of " + Checker.getOrderString(i) + " argument of writeln."
                );
            }
            ++i;
        }
        return null;
    }
    
    @Override
    public IStatement precompute(Procedure proc)
    {
        exps.forEach(e -> e.preeval(proc));
        return this;
    }
    
    @Override
    public void compile(List<Casl2Instruction> code, Procedure proc, LabelGenerator l_gen)
    {
        for (final ITyped e: getExpressions())
        {
            e.compile(code, proc, l_gen);
            
            if (e.getType() == BasicType.CHAR)
            {
                code.add(new Casl2Instruction("CALL", "", "", "WRTCH"));
            }
            else if (e.getType() == BasicType.INTEGER)
            {
                code.add(new Casl2Instruction("CALL", "", "", "WRTINT"));
            }
            else if (e.getType().isArrayOf(BasicType.CHAR))
            {
                code.add(new Casl2Instruction("CALL", "", "", "WRTSTR"));
            }
            else
            {
                assert false: "type error: (" + e.getLine() + "," + e.getColumn() + ")" + e.getType();
            }
        }
        code.add(new Casl2Instruction("CALL", "", "", "WRTLN"));
    }
    
    @Override
    public String toString()
    {
        return "";
    }
    
    @Override
    public int getLine()
    {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public int getColumn()
    {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void printBodyln(String indent)
    {
        exps.println(indent + "  ");
    }
}

